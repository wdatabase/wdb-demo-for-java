package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.ImgInfo;
import com.wdb.demo.entity.ImgInfoReq;
import com.wdb.demo.entity.ImgRaw;
import com.wdb.demo.entity.ImgReq;
import com.wdb.demo.entity.ImgRsp;
import com.wdb.demo.entity.ShopInfoReq;
import com.wdb.demo.entity.ShopListReq;
import com.wdb.demo.entity.ShopProInfo;
import com.wdb.demo.entity.ShopProInfoRsp;
import com.wdb.demo.entity.ShopProListRsp;
import com.wdb.demo.entity.ShopProReq;
import com.wdb.demo.entity.ShopRsp;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
public class ShopToPro extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/shop/pro/post")
    public ShopRsp shop_pro_post(@RequestBody ShopProReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }
        long tm = this.time();

        ArrayList<String> index_keys = new ArrayList<>();
        for(String ctp: req.getTps()) {
            index_keys.add("shop_pro_tp_" + ctp);
        }
        index_keys.add("all_shop_pro_tp_" + uid);

        ArrayList<String> indexraw = new ArrayList<String>(
                                        Arrays.asList(
                                            "title:str:=" + req.getTitle(),
                                            "price:num:=" + req.getPrice(),
                                            "weight:num:=" + req.getWeight(),
                                            "updateTime:num:=" + tm
                                        )
                                    );

        ObjectMapper mapper = new ObjectMapper();
        
        if (req.getUuid().equals("")) {
            String cuuid = this.uuid();
            ShopProInfo info = new ShopProInfo();
            info.setUuid(cuuid);
            info.setTitle(req.getTitle());
            info.setPrice(req.getPrice());
            info.setWeight(req.getWeight());
            info.setInventory(req.getInventory());
            info.setTps(req.getTps());
            info.setImgid(req.getImgid());
            info.setCreateTime(tm);
            info.setUpdateTime(tm);

            try {
                String data = mapper.writeValueAsString(info);

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, null);
                if (apiRsp.getCode() == 200) {
                    
                    ApiRsp idx_rsp = this.wdb().CreateIndex(index_keys, cuuid, indexraw);
                    if (idx_rsp.getCode() == 200) {
                        rsp.setCode(200);
                        rsp.setUuid(cuuid);
                    } else {
                        rsp.setCode(400);
                        rsp.setMsg(idx_rsp.getMsg());
                    }
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
                    ShopProInfo info = mapper.readValue(apiRsp.getData(), ShopProInfo.class);

                    ArrayList<String> old_index_keys = new ArrayList<>();
                    for(String ctp: info.getTps()) {
                        old_index_keys.add("shop_pro_tp_" + ctp);
                    }
                    old_index_keys.add("all_shop_pro_tp_" + uid);
                    
                    info.setTitle(req.getTitle());
                    info.setPrice(req.getPrice());
                    info.setWeight(req.getWeight());
                    info.setInventory(req.getInventory());
                    info.setTps(req.getTps());
                    info.setImgid(req.getImgid());
                    info.setUpdateTime(tm);

                    String data = mapper.writeValueAsString(info);
                    ApiRsp api_prsp = this.wdb().UpdateObj(req.getUuid(), data);
                    if (api_prsp.getCode() == 200) {
                        ApiRsp idx_rsp = this.wdb().UpdateIndex(old_index_keys, index_keys, info.getUuid(), indexraw);
                        if (idx_rsp.getCode() == 200) {
                            rsp.setCode(200);
                            rsp.setUuid(req.getUuid());
                        } else {
                            rsp.setCode(400);
                            rsp.setMsg(idx_rsp.getMsg());
                        }
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

    @GetMapping("/shop/pro/info")
    public ShopProInfoRsp shop_pro_info(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopProInfoRsp rsp = new ShopProInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopProInfo info = mapper.readValue(apiRsp.getData(), ShopProInfo.class);

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

    @PostMapping("/shop/pro/list")
    public ShopProListRsp shop_pro_list(@RequestBody ShopListReq req) {
        String uid = this.auth(req.getO());
        ShopProListRsp rsp = new ShopProListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String indexkey = req.getIndexkey();
        if(indexkey.equals("all")){
            indexkey = "all_shop_pro_tp_" + uid;
        } else {
            indexkey = "shop_pro_tp_" + indexkey;
        }

        ArrayList<String> arr = new ArrayList<String>();
        if (req.getTitlekey().isEmpty() == false) {
            arr.add(String.format("[\"title\",\"reg\",\"^.*%s.*$\"]", req.getTitlekey()));
        }
        if (req.getBegin().isEmpty() == false && req.getEnd().isEmpty() == true) {
            arr.add(String.format("[\"price\",\">=\",%s]", req.getBegin()));
        }
        if (req.getBegin().isEmpty() == true && req.getEnd().isEmpty() == false) {
            arr.add(String.format("[\"price\",\"<=\",%s]", req.getEnd()));
        }
        if (req.getBegin().isEmpty() == false && req.getEnd().isEmpty() == false) {
            arr.add(String.format("[\"price\",\">=\",%s,\"<=\",%s]", req.getBegin(), req.getEnd()));
        }

        String condition = "";
        if (arr.size() == 1) {
            condition = arr.get(0);
        } else if (arr.size() == 2) {
            condition = String.format("{\"and\": [%s]}", String.join(",", arr));
        }

        String order = "weight DESC";
        if (req.getOrder().equals("tasc")) {
            order = "updateTime ASC";
        } else if (req.getOrder().equals("tdesc")) {
            order = "updateTime DESC";
        } else if (req.getOrder().equals("pasc")) {
            order = "price ASC";
        } else if (req.getOrder().equals("pdesc")) {
            order = "price DESC";
        } else if (req.getOrder().equals("wasc")) {
            order = "weight ASC";
        } else if (req.getOrder().equals("wdesc")) {
            order = "weight DESC";
        }

        ListRsp listRsp = this.wdb().ListIndex(indexkey, condition, req.getOffset(), req.getLimit(), order);
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<ShopProInfo> lists = new ArrayList<>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ShopProInfo info = mapper.readValue(item, ShopProInfo.class);
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

    @DeleteMapping("/shop/pro/del")
    public ShopRsp shop_pro_del(ShopInfoReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp getRsp = this.wdb().GetObj(req.getUuid());
        if(getRsp.getCode() == 200){
            try {
                ObjectMapper mapper = new ObjectMapper();
                ShopProInfo info = mapper.readValue(getRsp.getData(), ShopProInfo.class);
                
                ArrayList<String> index_key = new ArrayList<>();
                index_key.add("all_shop_pro_tp_" + uid);
                for(String tp: info.getTps()){
                    index_key.add("shop_pro_tp_" + tp);
                }

                ApiRsp apiRsp = this.wdb().DelObj(req.getUuid());
                if (apiRsp.getCode() == 200) {
                    ApiRsp idx_rsp = this.wdb().DelIndex(index_key, req.getUuid());
                    if (idx_rsp.getCode() == 200) {
                        rsp.setCode(200);
                        rsp.setUuid("");
                    } else {
                        rsp.setCode(400);
                        rsp.setMsg(apiRsp.getMsg());
                    }
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

    @PostMapping("/shop/pro/img/post")
    public ImgRsp shop_pro_img_post(@RequestParam("img") MultipartFile file, ImgReq req) {
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
            ApiRsp apiRsp = this.wdb().CreateObj(fileUuid, content, new ArrayList<String>());
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

            ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, new ArrayList<String>());
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
        

        return rsp;
    }

    @GetMapping("/shop/pro/img/data")
    public ResponseEntity<byte[]> shop_pro_img_data(ImgInfoReq req) {
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

    
}

