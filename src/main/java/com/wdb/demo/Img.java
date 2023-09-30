package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ImgInfo;
import com.wdb.demo.entity.ImgInfoReq;
import com.wdb.demo.entity.ImgInfoRsp;
import com.wdb.demo.entity.ImgListInfo;
import com.wdb.demo.entity.ImgListReq;
import com.wdb.demo.entity.ImgListRsp;
import com.wdb.demo.entity.ImgRaw;
import com.wdb.demo.entity.ImgReq;
import com.wdb.demo.entity.ImgRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Img extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/img/post")
    public ImgRsp img_post(@RequestParam("img") MultipartFile file, ImgReq req) {
        logger.info("text post # {} {} {}", req.getUuid(), req.getO());
        String uid = this.auth(req.getO());
        ImgRsp rsp = new ImgRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ObjectMapper mapper = new ObjectMapper();
        String name = "img";
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        String fileUuid = this.uuid();

        try {
            byte[] raw = file.getBytes();
            String data = Base64.encodeBase64String(raw);
            ImgRaw img_raw = new ImgRaw();
            img_raw.setRaw(data);
            String content = mapper.writeValueAsString(img_raw);
            ApiRsp apiRsp = this.wdb().CreateObj(fileUuid, content, null);
            if (apiRsp.getCode() != 200) {
                rsp.setCode(400);
                rsp.setMsg(apiRsp.getMsg());
                return rsp;
            }
        } catch (Exception e) {
            rsp.setCode(400);
            rsp.setMsg("upload fail");
            return rsp;
        }

        long tm = this.time();
        if (req.getUuid().equals("")) {
            String cuuid = this.uuid();
            ImgInfo info = new ImgInfo();
            info.setUuid(cuuid);
            info.setName(name);
            info.setFileName(fileName);
            info.setContentType(contentType);
            info.setSize(size);
            info.setFileUuid(fileUuid);
            info.setCreateTime(tm);
            info.setUpdateTime(tm);

            try {
                String data = mapper.writeValueAsString(info);

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, new ArrayList<String>(Arrays.asList("my_img_" + uid)));
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
                    ImgInfo info = mapper.readValue(apiRsp.getData(), ImgInfo.class);
                    info.setFileName(fileName);
                    info.setContentType(contentType);
                    info.setSize(size);
                    info.setFileUuid(fileUuid);
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

    @GetMapping("/img/info")
    public ImgInfoRsp img_info(ImgInfoReq req) {
        String uid = this.auth(req.getO());
        ImgInfoRsp rsp = new ImgInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ImgInfo info = mapper.readValue(apiRsp.getData(), ImgInfo.class);

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

    @GetMapping("/img/data")
    public ResponseEntity<byte[]> img_data(ImgInfoReq req) {
        String uid = this.auth(req.getO());
        HttpHeaders headers = new HttpHeaders();

        if (uid.equals("")) {
            return ResponseEntity.status(403).body("".getBytes());
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ImgInfo info = mapper.readValue(apiRsp.getData(), ImgInfo.class);

                ApiRsp api_rrsp = this.wdb().GetObj(info.getFileUuid());
                if (api_rrsp.getCode() == 200) {
                    ImgRaw img_raw = mapper.readValue(api_rrsp.getData(), ImgRaw.class);
                    byte[] data = Base64.decodeBase64(img_raw.getRaw());
                    headers.set("Content-Type", info.getContentType());
                    return ResponseEntity
                            .status(200)
                            .headers(headers)
                            .body(data);
                }
            } catch (JsonProcessingException e) {
                logger.error("json decode user info fail");
            }
        }

        return ResponseEntity.status(400).body("".getBytes());
    }

    @GetMapping("/img/list")
    public ImgListRsp img_list(ImgListReq req) {
        logger.info("list req # {} {}", req.getO(), req.getOrder());
        String uid = this.auth(req.getO());
        ImgListRsp rsp = new ImgListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String category = String.format("my_img_%s", uid);
        ListRsp listRsp = this.wdb().ListObj(category, req.getOffset(), req.getLimit(), req.getOrder());
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<ImgListInfo> lists = new ArrayList<ImgListInfo>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ImgInfo info = mapper.readValue(item, ImgInfo.class);

                    ImgListInfo clist_info = new ImgListInfo();
                    clist_info.setUuid(info.getUuid());
                    clist_info.setName(info.getName());
                    clist_info.setFileName(info.getFileName());
                    clist_info.setContentType(info.getContentType());
                    clist_info.setSize(info.getSize());
                    clist_info.setFileUuid(info.getFileUuid());
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

    @DeleteMapping("/img/del")
    public ImgRsp img_del(ImgInfoReq req) {
        String uid = this.auth(req.getO());
        ImgRsp rsp = new ImgRsp();
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
