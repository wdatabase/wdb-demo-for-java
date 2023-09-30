package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.LoginReq;
import com.wdb.demo.entity.RegReq;
import com.wdb.demo.entity.UserInfo;
import com.wdb.demo.entity.UserRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class User extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/api/reg")
    public UserRsp reg(@RequestBody RegReq req){
        String cuuid = this.uuid();
        String pwd = this.hash(String.format("%s_%s", req.getUser(), req.getPwd()));

        UserInfo info = new UserInfo();
        info.setUuid(cuuid);
        info.setUser(req.getUser());
        info.setPwd(pwd);
        info.setMail(req.getMail());
        info.setCreateTime(this.time());
        info.setUpdateTime(this.time());

        UserRsp rsp = new UserRsp();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(info);
            logger.info("-------->reg#" + data);
            
            String key = String.format("user_%s", req.getUser());
            ApiRsp apiRsp = this.wdb().CreateObj(key, data, new ArrayList<String>(Arrays.asList("user_list")));
            if(apiRsp.getCode() == 200){
                rsp.setCode(200);
                rsp.setUid(cuuid);
            } else {
                rsp.setCode(400);
                rsp.setMsg(apiRsp.getMsg());
            }
        } catch(JsonProcessingException e) {
            logger.error("json encode user info fail");
            rsp.setCode(400);
            rsp.setMsg("err reg");
        };

        return rsp;
    }

    @PostMapping("/api/login")
    public UserRsp login(@RequestBody LoginReq req){
        String pwd = this.hash(String.format("%s_%s", req.getUser(), req.getPwd()));
        String key = String.format("user_%s", req.getUser());
        ApiRsp apiRsp = this.wdb().GetObj(key);

        UserRsp rsp = new UserRsp();
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info(apiRsp.getData());
            UserInfo info = mapper.readValue(apiRsp.getData(), UserInfo.class);

            if(info.getPwd().equals(pwd)) {
                long tm = this.time();
                String sg = this.sign(info.getUuid(), tm);
                rsp.setCode(200);
                rsp.setUid(info.getUuid());
                rsp.setTime(tm);
                rsp.setSign(sg);
            } else {
                rsp.setCode(400);
                rsp.setMsg("pwd err");
            }
        } catch(JsonProcessingException e) {
            logger.error("json decode user info fail");
            rsp.setCode(400);
            rsp.setMsg("err json decode");
        };

        return rsp;
    }
}
