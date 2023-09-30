package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.TextInfo;
import com.wdb.demo.entity.TextInfoReq;
import com.wdb.demo.entity.TextInfoRsp;
import com.wdb.demo.entity.TextListInfo;
import com.wdb.demo.entity.TextListReq;
import com.wdb.demo.entity.TextListRsp;
import com.wdb.demo.entity.TextReq;
import com.wdb.demo.entity.TextRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;
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
public class Text extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/text/post")
    public TextRsp text_post(@RequestBody TextReq req) {
        String uid = this.auth(req.getO());
        TextRsp rsp = new TextRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ObjectMapper mapper = new ObjectMapper();
        long tm = this.time();
        if (req.getUuid().equals("")) {
            String cuuid = this.uuid();
            TextInfo info = new TextInfo();
            info.setUuid(cuuid);
            info.setTitle(req.getTitle());
            info.setContent(req.getContent());
            info.setCreateTime(tm);
            info.setUpdateTime(tm);

            try {
                String data = mapper.writeValueAsString(info);

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, new ArrayList<String>(Arrays.asList("my_text_" + uid)));
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
                    TextInfo info = mapper.readValue(apiRsp.getData(), TextInfo.class);
                    info.setTitle(req.getTitle());
                    info.setContent(req.getContent());
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

    @GetMapping("/text/info")
    public TextInfoRsp text_info(TextInfoReq req) {
        String uid = this.auth(req.getO());
        TextInfoRsp rsp = new TextInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TextInfo info = mapper.readValue(apiRsp.getData(), TextInfo.class);

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

    @GetMapping("/text/list")
    public TextListRsp text_list(TextListReq req) {
        String uid = this.auth(req.getO());
        TextListRsp rsp = new TextListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String category = String.format("my_text_%s", uid);
        ListRsp listRsp = this.wdb().ListObj(category, req.getOffset(), req.getLimit(), req.getOrder());
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<TextListInfo> lists = new ArrayList<>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    TextInfo info = mapper.readValue(item, TextInfo.class);

                    TextListInfo clist_info = new TextListInfo();
                    clist_info.setUuid(info.getUuid());
                    clist_info.setTitle(info.getTitle());
                    clist_info.setTime(info.getCreateTime());

                    lists.add(clist_info);

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

    @DeleteMapping("/text/del")
    public TextRsp text_del(TextInfoReq req) {
        String uid = this.auth(req.getO());
        TextRsp rsp = new TextRsp();
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
