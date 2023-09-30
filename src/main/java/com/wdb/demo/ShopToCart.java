package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ShopCartAddReq;
import com.wdb.demo.entity.ShopCartInfo;
import com.wdb.demo.entity.ShopCartInfoRsp;
import com.wdb.demo.entity.ShopCartListInfo;
import com.wdb.demo.entity.ShopCartListRsp;
import com.wdb.demo.entity.ShopCartReq;
import com.wdb.demo.entity.ShopInfoReq;
import com.wdb.demo.entity.ShopProInfo;
import com.wdb.demo.entity.ShopRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.Wdb;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ShopToCart extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/shop/cart/add")
    public ShopCartInfoRsp shop_cart_add(@RequestBody ShopCartAddReq req) {
        String uid = this.auth(req.getO());
        ShopCartInfoRsp rsp = new ShopCartInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        Wdb wdb = this.wdb();
        ObjectMapper mapper = new ObjectMapper();

        String key = "shop_cart_" + uid;
        ApiRsp apiRsp = wdb.GetObj(key);

        try {
            
            if(apiRsp.getCode() == 200){
                ShopCartInfo shopCartInfo = mapper.readValue(apiRsp.getData(), ShopCartInfo.class);
                ArrayList<String> ids = shopCartInfo.getIds();
                ArrayList<Long> nums = shopCartInfo.getNums();
                if(ids.contains(req.getUuid())){
                    int index = ids.indexOf(req.getUuid());
                    nums.set(index, req.getNum());
                }else{
                    ids.add(req.getUuid());
                    nums.add(req.getNum());
                }

                shopCartInfo.setIds(ids);
                shopCartInfo.setNums(nums);
                shopCartInfo.setUpdateTime(this.time());

                String data = mapper.writeValueAsString(shopCartInfo);
                ApiRsp updateRsp = wdb.UpdateObj(key, data);

                if(updateRsp.getCode() == 200){
                    rsp.setCode(200);
                    rsp.setInfo(shopCartInfo);
                } else {
                    rsp.setCode(500);
                    rsp.setMsg(updateRsp.getMsg());
                }

            } else {
                if (apiRsp.getMsg().equals("not found key")) {
                    ShopCartInfoRsp init_rsp = init_cart_info(key, uid, req.getUuid(), req.getNum());
                    if (init_rsp.getCode() == 200) {
                        rsp.setCode(200);
                        rsp.setInfo(init_rsp.getInfo());
                    } else {
                        rsp.setCode(400);
                        rsp.setMsg(init_rsp.getMsg());
                    }
                } else {
                    rsp.setCode(500);
                    rsp.setMsg(apiRsp.getMsg());
                }
            }
        } catch (JsonProcessingException e) {
            rsp.setCode(400);
            rsp.setMsg("err json");
        }

        return rsp;
    }

    

    private ShopCartInfoRsp init_cart_info(String key, String uid, String uuid, Long num) {
        String cuuid = this.uuid();
        long tm = this.time();

        ShopCartInfo info = new ShopCartInfo();
        info.setUuid(cuuid);
        info.setUid(uid);
        info.setIds(new ArrayList<>(Arrays.asList(uuid)));
        info.setNums(new ArrayList<>(Arrays.asList(num)));
        info.setCreateTime(tm);
        info.setUpdateTime(tm);

        ShopCartInfoRsp rsp = new ShopCartInfoRsp();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(info);

            ApiRsp apiRsp = this.wdb().CreateObj(key, data, null);
            if (apiRsp.getCode() == 200) {
                rsp.setCode(200);
                rsp.setInfo(info);
            } else {
                rsp.setCode(400);
                rsp.setMsg(apiRsp.getMsg());
            }
        } catch (JsonProcessingException e) {
            logger.error("json encode user info fail");
            rsp.setCode(400);
            rsp.setMsg("err reg");
        }

        return rsp;
    }

    @GetMapping("/shop/cart/list")
    public ShopCartListRsp shop_cart_info(ShopCartReq req) {
        String uid = this.auth(req.getO());
        ShopCartListRsp rsp = new ShopCartListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String key = "shop_cart_" + uid;
        Wdb wdb = this.wdb();
        ApiRsp apiRsp = wdb.GetObj(key);
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopCartInfo info = mapper.readValue(apiRsp.getData(), ShopCartInfo.class);
                ArrayList<Long> nums = info.getNums();
                ArrayList<String> ids = info.getIds();

                ArrayList<ShopCartListInfo> listInfo = new ArrayList<>();
                double total = 0.0;
                for(int i = 0; i < ids.size(); i++){
                    String ckey = ids.get(i);
                    Long cnum = nums.get(i);

                    ApiRsp gRsp = wdb.GetObj(ckey);
                    
                    if(gRsp.getCode() == 200){
                        ShopProInfo proInfo = mapper.readValue(gRsp.getData(), ShopProInfo.class);

                        ShopCartListInfo cinfo = new ShopCartListInfo();
                        cinfo.setProid(proInfo.getUuid());
                        cinfo.setTitle(proInfo.getTitle());
                        cinfo.setPrice(proInfo.getPrice());
                        cinfo.setInventory(proInfo.getInventory());
                        cinfo.setImgid(proInfo.getImgid());
                        cinfo.setNum(cnum);

                        total += cinfo.getPrice() * cnum;

                        listInfo.add(cinfo);
                    }
                }

                total = Math.round(total*100.0)/100.0;

                rsp.setCode(200);
                rsp.setTotal(total);
                rsp.setListinfo(listInfo);
            } catch (JsonProcessingException e) {
                logger.error("json decode user info fail");
                rsp.setCode(400);
                rsp.setMsg("err json decode");
            }
        } else {
            rsp.setCode(400);
            rsp.setMsg(apiRsp.getMsg());
        }

        return rsp;
    }

    @DeleteMapping("/shop/cart/del")
    public ShopRsp shop_cart_del(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String key = "shop_cart_" + uid;
        ApiRsp getRsp = this.wdb().GetObj(key);
        if(getRsp.getCode() == 200){
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopCartInfo info = mapper.readValue(getRsp.getData(), ShopCartInfo.class);
                
                if(req.getUuid().equals("all")){
                    ArrayList<String> empty_ids = new ArrayList<>();
                    ArrayList<Long> empty_nums = new ArrayList<>();
                    info.setIds(empty_ids);
                    info.setNums(empty_nums);
                } else {
                    ArrayList<String> ids = info.getIds();
                    ArrayList<Long> nums = info.getNums();
                    if(ids.contains(req.getUuid())){
                        int index = ids.indexOf(req.getUuid());
                        ids.remove(index);
                        nums.remove(index);
                        info.setIds(ids);
                        info.setNums(nums);
                    }
                }
                
                String data = mapper.writeValueAsString(info);
                ApiRsp apiRsp = this.wdb().UpdateObj(key, data);
                if (apiRsp.getCode() == 200) {
                    rsp.setCode(200);
                    rsp.setUuid("");
                } else {
                    rsp.setCode(400);
                    rsp.setMsg(apiRsp.getMsg());
                }
            } catch (JsonProcessingException e) {
                logger.error("json decode user info fail");
                rsp.setCode(400);
                rsp.setMsg("err json decode");
            }
        } else {
            rsp.setCode(400);
            rsp.setMsg(getRsp.getMsg());
        }

        return rsp;
    }

}

