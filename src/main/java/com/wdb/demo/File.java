package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.FileInfo;
import com.wdb.demo.entity.FileInfoReq;
import com.wdb.demo.entity.FileInfoRsp;
import com.wdb.demo.entity.FileListInfo;
import com.wdb.demo.entity.FileListReq;
import com.wdb.demo.entity.FileListRsp;
import com.wdb.demo.entity.FileReq;
import com.wdb.demo.entity.FileRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;
import io.github.wdatabase.wdbdrive.RawRsp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class File extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/file/post")
    public FileRsp file_post(@RequestParam("file") MultipartFile file, FileReq req) {
        //logger.info("text post # {} {} {}", req.getUuid(), req.getO());
        String uid = this.auth(req.getO());
        FileRsp rsp = new FileRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ObjectMapper mapper = new ObjectMapper();
        String name = "file";
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        String fileUuid = this.uuid();

        try {
            byte[] raw = file.getBytes();

            ApiRsp apiRsp = this.wdb().CreateRawData(fileUuid, raw, null);
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
            FileInfo info = new FileInfo();
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

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data,
                        new ArrayList<String>(Arrays.asList("my_file_" + uid)));
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
                    FileInfo info = mapper.readValue(apiRsp.getData(), FileInfo.class);
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

    @GetMapping("/file/info")
    public FileInfoRsp file_info(FileInfoReq req) {
        String uid = this.auth(req.getO());
        FileInfoRsp rsp = new FileInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                FileInfo info = mapper.readValue(apiRsp.getData(), FileInfo.class);

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

    @GetMapping("/file/data")
    public ResponseEntity<byte[]> file_data(@RequestHeader(value = "Range", defaultValue = "") String range,
            FileInfoReq req) {
        String uid = this.auth(req.getO());
        HttpHeaders headers = new HttpHeaders();

        if (uid.equals("")) {
            return ResponseEntity.status(403).body("".getBytes());
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                FileInfo info = mapper.readValue(apiRsp.getData(), FileInfo.class);

                byte[] data = "".getBytes();
                int stat = 200;
                if (range.equals("")) {
                    RawRsp rawRsp = this.wdb().GetRawData(info.getFileUuid());
                    data = rawRsp.getRaw();
                    headers.set("Accept-Range", "bytes");
                } else {
                    stat = 206;
                    String[] srg = range.substring(6).split("-");
                    long start = Long.parseLong(srg[0]);
                    long end = 0;
                    if (srg.length == 2 && !srg[1].equals("")) {
                        end = Long.parseLong(srg[1]);
                    }
                    if (end == 0) {
                        end = start + 1024 * 1024;
                    }

                    RawRsp raw_rsp = this.wdb().GetRangeData(info.getFileUuid(), start, end - start);
                    data = raw_rsp.getRaw();
                    long size = raw_rsp.getSize();
                    if (end > size - 1) {
                        end = size - 1;
                    }
                    headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, size));
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                String tm = format.format(info.getUpdateTime());

                headers.set("Last-Modified", tm);
                headers.set("Etag", info.getUuid());
                headers.set("Content-Type", "application/octet-stream");
                headers.set("Content-Disposition", "attachment;filename=" + info.getFileName());

                return ResponseEntity
                        .status(stat)
                        .headers(headers)
                        .body(data);
            } catch (JsonProcessingException e) {
                logger.error("json decode user info fail");
            }
        }

        return ResponseEntity.status(400).body("".getBytes());
    }

    @GetMapping("/file/list")
    public FileListRsp file_list(FileListReq req) {
        //logger.info("list req # {} {}", req.getO(), req.getOrder());
        String uid = this.auth(req.getO());
        FileListRsp rsp = new FileListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        String category = String.format("my_file_%s", uid);
        ListRsp listRsp = this.wdb().ListObj(category, req.getOffset(), req.getLimit(), req.getOrder());
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<FileListInfo> lists = new ArrayList<FileListInfo>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    FileInfo info = mapper.readValue(item, FileInfo.class);

                    FileListInfo clist_info = new FileListInfo();
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

    @DeleteMapping("/file/del")
    public FileRsp file_del(FileInfoReq req) {
        String uid = this.auth(req.getO());
        FileRsp rsp = new FileRsp();
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
