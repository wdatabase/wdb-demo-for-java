package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ShopCategorizeInfo;
import com.wdb.demo.entity.ShopCategorizeInfoRsp;
import com.wdb.demo.entity.ShopCategorizeListInfo;
import com.wdb.demo.entity.ShopCategorizeListReq;
import com.wdb.demo.entity.ShopCategorizeListRsp;
import com.wdb.demo.entity.ShopCategorizeReq;
import com.wdb.demo.entity.ShopInfoReq;
import com.wdb.demo.entity.ShopRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ShopToCategorize extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/shop/categorize/post")
    public ShopRsp shop_categorize_post(@RequestBody ShopCategorizeReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ObjectMapper mapper = new ObjectMapper();
        long tm = this.time();
        if (req.getUuid().equals("")) {
            String cuuid = this.uuid();
            ShopCategorizeInfo info = new ShopCategorizeInfo();
            info.setUuid(cuuid);
            info.setName(req.getName());
            info.setSort(req.getSort());
            info.setCreateTime(tm);
            info.setUpdateTime(tm);

            try {
                String data = mapper.writeValueAsString(info);

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, new ArrayList<String>(Arrays.asList("shop_categorize_" + uid)));
                if (apiRsp.getCode() == 200) {
                    rsp.setCode(200);
                    rsp.setUuid(cuuid);
                } else {
                    rsp.setCode(400);
                    rsp.setMsg(apiRsp.getMsg());
                }
            } catch (JsonProcessingException e) {
                logger.error("json encode user info fail");
                rsp.setCode(400);
                rsp.setMsg("err reg");
            }
        } else {
            ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
            if (apiRsp.getCode() == 200) {
                try {
                    ShopCategorizeInfo info = mapper.readValue(apiRsp.getData(), ShopCategorizeInfo.class);
                    info.setName(req.getName());
                    info.setSort(req.getSort());
                    info.setUpdateTime(tm);

                    String data = mapper.writeValueAsString(info);
                    ApiRsp api_prsp = this.wdb().UpdateObj(req.getUuid(), data);
                    if (api_prsp.getCode() == 200) {
                        rsp.setCode(200);
                        rsp.setUuid(req.getUuid());
                    } else {
                        rsp.setCode(400);
                        rsp.setMsg(api_prsp.getMsg());
                    }
                } catch (Exception e) {
                    rsp.setCode(400);
                    rsp.setMsg(String.format("%s", e));
                }
            } else {
                rsp.setCode(400);
                rsp.setMsg(apiRsp.getMsg());
            }
        }

        return rsp;
    }

    @PostMapping("/shop/categorize/list")
    public ShopCategorizeListRsp shop_categorize_list(@RequestBody ShopCategorizeListReq req) {
        String uid = this.auth(req.getO());
        ShopCategorizeListRsp rsp = new ShopCategorizeListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String category = "shop_categorize_" + uid;
        ListRsp listRsp = this.wdb().ListObj(category, req.getOffset(), req.getLimit(), req.getOrder());
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<ShopCategorizeListInfo> lists = new ArrayList<>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ShopCategorizeInfo info = mapper.readValue(item, ShopCategorizeInfo.class);

                    ShopCategorizeListInfo clist_info = new ShopCategorizeListInfo();
                    clist_info.setUuid(info.getUuid());
                    clist_info.setName(info.getName());
                    clist_info.setSort(info.getSort());

                    lists.add(clist_info);

                } catch (JsonProcessingException e) {
                    logger.error("json decode user info fail");
                    rsp.setCode(400);
                    rsp.setMsg("err json decode");
                    return rsp;
                }
            }
            Collections.sort(lists);
            rsp.setList(lists);
        } else {
            rsp.setCode(400);
            rsp.setMsg(listRsp.getMsg());
        }

        return rsp;
    }

    @GetMapping("/shop/categorize/info")
    public ShopCategorizeInfoRsp categorize_info(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopCategorizeInfoRsp rsp = new ShopCategorizeInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopCategorizeInfo info = mapper.readValue(apiRsp.getData(), ShopCategorizeInfo.class);

                rsp.setCode(200);
                rsp.setInfo(info);
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

    @DeleteMapping("/shop/categorize/del")
    public ShopRsp shop_categorize_del(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().DelObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setUuid(req.getUuid());    
        } else {
            rsp.setCode(400);
            rsp.setMsg(apiRsp.getMsg());
        }

        return rsp;
    }
}
