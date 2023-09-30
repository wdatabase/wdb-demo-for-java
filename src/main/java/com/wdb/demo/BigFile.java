package com.wdb.demo;

import com.wdb.demo.entity.BigFileReq;
import com.wdb.demo.entity.ShopRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class BigFile extends Comm {

    @PostMapping("/big/file/upload")
    public ShopRsp big_file_upload(@RequestBody BigFileReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().UploadByPath(req.getPath(), req.getKey(), null);
        if(apiRsp.getCode() == 200){
            rsp.setCode(200);
            rsp.setUuid(req.getKey());
        } else {
            rsp.setCode(500);
            rsp.setMsg(apiRsp.getMsg());
        }
        
        return rsp;
    }

    @PostMapping("/big/file/down")
    public ShopRsp big_file_down(@RequestBody BigFileReq req) {
        String uid = this.auth(req.getO());
        ShopRsp rsp = new ShopRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().DownToPath(req.getPath(), req.getKey());
        if(apiRsp.getCode() == 200){
            rsp.setCode(200);
            rsp.setUuid(req.getKey());
        } else {
            rsp.setCode(500);
            rsp.setMsg(apiRsp.getMsg());
        }
        
        return rsp;
    }

}

