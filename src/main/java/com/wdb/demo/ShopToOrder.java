package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ShopInfo;
import com.wdb.demo.entity.ShopInfoReq;
import com.wdb.demo.entity.ShopOrderInfo;
import com.wdb.demo.entity.ShopOrderItem;
import com.wdb.demo.entity.ShopOrderItemRsp;
import com.wdb.demo.entity.ShopOrderListReq;
import com.wdb.demo.entity.ShopOrderListRsp;
import com.wdb.demo.entity.ShopOrderReq;
import com.wdb.demo.entity.ShopProInfo;
import com.wdb.demo.entity.ShopRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;
import io.github.wdatabase.wdbdrive.Wdb;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ShopToOrder extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/shop/order/create")
    public ShopRsp shop_order_create(@RequestBody ShopOrderReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ArrayList<String> rids = req.getIds();
        ArrayList<Long> rnums = req.getNums();
        ArrayList<Double> rprice = req.getPrices();

        Wdb wdb = this.wdb();
        ObjectMapper mapper = new ObjectMapper();

        try {
            long tm = this.time();

            ArrayList<String> lock_ids = new ArrayList<>(rids);
            String balancekey = "shop_info_" + uid;
            lock_ids.add(balancekey);

            //开始事务
            ApiRsp TransBeginRsp = wdb.TransBegin(lock_ids);
            String tsid = "";
            if(TransBeginRsp.getCode() == 200){
                tsid = TransBeginRsp.getData();
            } else {
                rsp.setCode(500);
                rsp.setMsg(TransBeginRsp.getMsg());
                return rsp;
            }

            //校验余额
            ApiRsp balanceRsp = wdb.TransGet(tsid, balancekey);
            ShopInfo balanceInfo = new ShopInfo();
            if(balanceRsp.getCode() == 200){
                balanceInfo = mapper.readValue(balanceRsp.getData(), ShopInfo.class);

                if(this.doublecmp(balanceInfo.getBalance(), req.getTotal()) == -1){
                    wdb.TransRollBack(tsid);
                    rsp.setCode(500);
                    rsp.setMsg("余额不足");
                    return rsp;
                }
                
            } else {
                wdb.TransRollBack(tsid);
                rsp.setCode(500);
                rsp.setMsg(balanceRsp.getMsg());
                return rsp;
            }

            String orderid = this.uuid();
            ArrayList<String> tlist = new ArrayList<>();
            String imgid = "";
            
            //遍历购物车相关商品
            for(int i = 0; i < rids.size(); i++){
                String cpid = rids.get(i);
                long cnum = rnums.get(i);
                double cprice = rprice.get(i);

                ApiRsp proRsp = wdb.TransGet(tsid, cpid);
                if(proRsp.getCode() == 200){
                    ShopProInfo proInfo = mapper.readValue(proRsp.getData(), ShopProInfo.class);

                    //校验价格
                    if(this.doublecmp(cprice, proInfo.getPrice()) != 0){
                        wdb.TransRollBack(tsid);
                        rsp.setCode(500);
                        rsp.setMsg("商品价格变动，请重新确认。");
                        return rsp;
                    }

                    //校验库存
                    if(cnum > proInfo.getInventory()){
                        wdb.TransRollBack(tsid);
                        rsp.setCode(500);
                        rsp.setMsg("库存不足。");
                        return rsp;
                    }

                    tlist.add(proInfo.getTitle());
                    imgid = proInfo.getImgid();

                    //保存订单产品详情
                    ShopOrderItem item = new ShopOrderItem();
                    String itemid = this.uuid();
                    item.setUuid(itemid);
                    item.setTitle(proInfo.getTitle());
                    item.setImgid(proInfo.getImgid());
                    item.setPrice(cprice);
                    item.setNum(cnum);
                    item.setCreateTime(tm);
                    item.setUpdateTime(tm);
                    String itemdata = mapper.writeValueAsString(item);
                    ApiRsp orderRsp = wdb.TransCreateObj(tsid, itemid, itemdata, new ArrayList<>(Arrays.asList("shop_order_item_" + orderid)));
                    if(orderRsp.getCode() != 200){
                        wdb.TransRollBack(tsid);
                        rsp.setCode(500);
                        rsp.setMsg(orderRsp.getMsg());
                        return rsp;
                    }

                    //减库存
                    proInfo.setInventory(proInfo.getInventory() - cnum);
                    String prodata = mapper.writeValueAsString(proInfo);
                    ApiRsp bRsp = wdb.TransUpdateObj(tsid, proInfo.getUuid(), prodata);
                    if(bRsp.getCode() != 200){
                        wdb.TransRollBack(tsid);
                        rsp.setCode(500);
                        rsp.setMsg(orderRsp.getMsg());
                        return rsp;
                    }

                } else {
                    wdb.TransRollBack(tsid);
                    rsp.setCode(500);
                    rsp.setMsg(proRsp.getMsg());
                    return rsp;
                }
            }

            //保存订单信息
            ShopOrderInfo order = new ShopOrderInfo();
            order.setUuid(orderid);
            order.setTitle(String.join("/", tlist));
            order.setImgid(imgid);
            order.setTotal(req.getTotal());
            order.setIds(req.getIds());
            order.setNums(req.getNums());
            order.setPrices(req.getPrices());
            order.setTotal(req.getTotal());
            order.setCreateTime(tm);
            order.setUpdateTime(tm);

            String orderdata = mapper.writeValueAsString(order);
            ApiRsp orderRsp = wdb.TransCreateObj(tsid, orderid, orderdata, null);
            if(orderRsp.getCode() == 200){
                
                //创建索引
                ArrayList<String> index_keys = new ArrayList<>();
                index_keys.add("shop_order_index_" + uid);

                ArrayList<String> indexraw = new ArrayList<>();
                indexraw.add("title:str:=" + order.getTitle());
                indexraw.add("total:num:=" + order.getTotal());
                indexraw.add("updateTime:num:=" + order.getUpdateTime());

                ApiRsp indexRsp = wdb.CreateIndex(index_keys, orderid, indexraw);
                if(indexRsp.getCode() == 200){

                    //更新余额积分
                    balanceInfo.setPoint(balanceInfo.getPoint() + order.getTotal());
                    balanceInfo.setBalance(balanceInfo.getBalance() - order.getTotal());
                    String balancedata = mapper.writeValueAsString(balanceInfo);
                    ApiRsp bRsp = wdb.TransUpdateObj(tsid, balancekey, balancedata);
                    if(bRsp.getCode() == 200){
                        
                        //提交事务
                        ApiRsp commitRsp = wdb.transCommit(tsid);
                        if(commitRsp.getCode() == 200){
                            rsp.setCode(200);
                            rsp.setUuid(orderid);
                        } else {
                            wdb.TransRollBack(tsid);
                            rsp.setCode(500);
                            rsp.setMsg(commitRsp.getMsg());
                        }
                    }else{
                        wdb.TransRollBack(tsid);
                        rsp.setCode(500);
                        rsp.setMsg(bRsp.getMsg());
                    }
                }
                
            } else {
                wdb.TransRollBack(tsid);
                rsp.setCode(500);
                rsp.setMsg(orderRsp.getMsg());
                return rsp;
            }
        } catch (JsonProcessingException e) {
            logger.error("json encode user info fail");
            rsp.setCode(400);
            rsp.setMsg("err reg");
        }

        return rsp;
    }

    @GetMapping("/shop/order/info")
    public ShopOrderItemRsp shop_order_info(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopOrderItemRsp rsp = new ShopOrderItemRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            ListRsp listRsp = this.wdb().ListObj("shop_order_item_" + req.getUuid(), 0, 100, "ASC");
            if (listRsp.getCode() == 200) {
                rsp.setCode(200);
                rsp.setTotal(listRsp.getTotal());

                ArrayList<ShopOrderItem> lists = new ArrayList<>();
                for (String item: listRsp.getList()) {
                    ShopOrderItem iteminfo = mapper.readValue(item, ShopOrderItem.class);
                    lists.add(iteminfo);
                }
                rsp.setList(lists);
            } else {
                rsp.setCode(400);
                rsp.setMsg(listRsp.getMsg());
            }

        } catch (JsonProcessingException e) {
            rsp.setCode(400);
            rsp.setMsg("err json");
        }
        

        return rsp;
    }

    @PostMapping("/shop/order/list")
    public ShopOrderListRsp shop_order_list(@RequestBody ShopOrderListReq req) {
        String uid = this.auth(req.getO());
        ShopOrderListRsp rsp = new ShopOrderListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ArrayList<String> arr = new ArrayList<String>();
        if (req.getTitlekey().isEmpty() == false) {
            arr.add(String.format("[\"title\",\"reg\",\"^.*%s.*$\"]", req.getTitlekey()));
        }

        String condition = "";
        if (arr.size() == 1) {
            condition = arr.get(0);
        } else if (arr.size() == 2) {
            condition = String.format("{\"and\": [%s]}", String.join(",", arr));
        }

        String order = "updateTime DESC";
        if (req.getOrder().equals("tasc")) {
            order = "updateTime ASC";
        } else if (req.getOrder().equals("tdesc")) {
            order = "updateTime DESC";
        } else if (req.getOrder().equals("pasc")) {
            order = "total ASC";
        } else if (req.getOrder().equals("pdesc")) {
            order = "total DESC";
        }

        String indexkey = "shop_order_index_" + uid;
        ListRsp listRsp = this.wdb().ListIndex(indexkey, condition, req.getOffset(), req.getLimit(), order);
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<ShopOrderInfo> lists = new ArrayList<>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ShopOrderInfo info = mapper.readValue(item, ShopOrderInfo.class);
                    lists.add(info);
                } catch (JsonProcessingException e) {
                    logger.error("json decode user info fail");
                    rsp.setCode(400);
                    rsp.setMsg("err json decode");
                    return rsp;
                }
            }
            rsp.setList(lists);
        } else {
            rsp.setCode(400);
            rsp.setMsg(listRsp.getMsg());
        }

        return rsp;
    }

}

