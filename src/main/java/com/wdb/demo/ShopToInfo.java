package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ShopBalanceLog;
import com.wdb.demo.entity.ShopBalanceReq;
import com.wdb.demo.entity.ShopInfo;
import com.wdb.demo.entity.ShopInfoReq;
import com.wdb.demo.entity.ShopInfoRsp;
import com.wdb.demo.entity.ShopRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.Wdb;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ShopToInfo extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/shop/balance")
    public ShopRsp shop_balance(@RequestBody ShopBalanceReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }   

        Wdb wdb = this.wdb();
        ObjectMapper mapper = new ObjectMapper();

        try {
            
            String key = "shop_info_" + uid;
            long tm = this.time();

            ApiRsp TransBeginRsp = wdb.TransBegin(new ArrayList<String>(Arrays.asList(key)));
            String tsid = "";
            if(TransBeginRsp.getCode() == 200){
                tsid = TransBeginRsp.getData();
            } else {
                rsp.setCode(500);
                rsp.setMsg(TransBeginRsp.getMsg());
                return rsp;
            }

            ApiRsp shopInfoRsp = wdb.TransGet(tsid, key);
            if(shopInfoRsp.getCode() == 200){

                String cuuid = this.uuid();
                ShopBalanceLog shopBalanceLog = new ShopBalanceLog();
                shopBalanceLog.setUuid(cuuid);
                shopBalanceLog.setUid(uid);
                shopBalanceLog.setBalance(req.getBalance());
                shopBalanceLog.setOp("in");
                shopBalanceLog.setUpdateTime(tm);
                shopBalanceLog.setCreateTime(tm);
                String balanceLogData = mapper.writeValueAsString(shopBalanceLog);
                ApiRsp balanceLogRsp = wdb.TransCreateObj(tsid, cuuid, balanceLogData, new ArrayList<String>(Arrays.asList("shop_balance_log_" + uid)));
                if(balanceLogRsp.getCode() != 200){
                    wdb.TransRollBack(tsid);
                    rsp.setCode(500);
                    rsp.setMsg(balanceLogRsp.getMsg());
                    return rsp;
                }

                ShopInfo shopInfo = mapper.readValue(shopInfoRsp.getData(), ShopInfo.class);
                shopInfo.setBalance(shopInfo.getBalance() + req.getBalance());
                shopInfo.setUpdateTime(tm);
                String shopInfoData = mapper.writeValueAsString(shopInfo);
                ApiRsp updateShopInfoRsp = wdb.TransUpdateObj(tsid, key, shopInfoData);
                if(updateShopInfoRsp.getCode() != 200){
                    wdb.TransRollBack(tsid);
                    rsp.setCode(500);
                    rsp.setMsg(updateShopInfoRsp.getMsg());
                    return rsp;
                }

                ApiRsp commitRsp = wdb.transCommit(tsid);
                if(commitRsp.getCode() == 200){
                    rsp.setCode(200);
                }

            } else {
                wdb.TransRollBack(tsid);
                rsp.setCode(500);
                rsp.setMsg(shopInfoRsp.getMsg());
                return rsp;
            }
        } catch (JsonProcessingException e) {
            logger.error("json encode user info fail");
            rsp.setCode(400);
            rsp.setMsg("err reg");
        }

        return rsp;
    }

    @GetMapping("/shop/info")
    public ShopInfoRsp shop_info(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopInfoRsp rsp = new ShopInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String key = "shop_info_" + uid;
        ApiRsp apiRsp = this.wdb().GetObj(key);
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopInfo info = mapper.readValue(apiRsp.getData(), ShopInfo.class);

                rsp.setCode(200);
                rsp.setInfo(info);
            } catch (JsonProcessingException e) {
                logger.error("json decode user info fail");
                rsp.setCode(400);
                rsp.setMsg("err json decode");
            }
        } else {
            if (apiRsp.getMsg().equals("not found key")) {
                ShopInfoRsp init_rsp = init_info(key, uid);
                if (init_rsp.getCode() == 200) {
                    rsp.setCode(200);
                    rsp.setInfo(init_rsp.getInfo());
                } else {
                    rsp.setCode(400);
                    rsp.setMsg(init_rsp.getMsg());
                }
            } else {
                rsp.setCode(400);
                rsp.setMsg(apiRsp.getMsg());
            }
        }

        return rsp;
    }

    private ShopInfoRsp init_info(String key, String uid) {
        String cuuid = this.uuid();
        long tm = this.time();

        ShopInfo info = new ShopInfo();
        info.setUuid(cuuid);
        info.setUid(uid);
        info.setBalance(0.0);
        info.setCreateTime(tm);
        info.setUpdateTime(tm);

        ShopInfoRsp rsp = new ShopInfoRsp();
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

}

