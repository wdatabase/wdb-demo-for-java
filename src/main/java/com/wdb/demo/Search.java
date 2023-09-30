package com.wdb.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdb.demo.entity.SearchInfo;
import com.wdb.demo.entity.SearchInfoReq;
import com.wdb.demo.entity.SearchInfoRsp;
import com.wdb.demo.entity.SearchListInfo;
import com.wdb.demo.entity.SearchListReq;
import com.wdb.demo.entity.SearchListRsp;
import com.wdb.demo.entity.SearchReq;
import com.wdb.demo.entity.SearchRsp;
import io.github.wdatabase.wdbdrive.ApiRsp;
import io.github.wdatabase.wdbdrive.ListRsp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public class Search extends Comm {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/search/post")
    public SearchRsp search_post(@RequestBody SearchReq req) {
        String uid = this.auth(req.getO());
        SearchRsp rsp = new SearchRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }
        ArrayList<String> index_key = new ArrayList<>(Arrays.asList("my_search_index_" + uid));

        ObjectMapper mapper = new ObjectMapper();
        long tm = this.time();
        if (req.getUuid().equals("")) {
            String cuuid = this.uuid();
            SearchInfo info = new SearchInfo();
            info.setUuid(cuuid);
            info.setScore(req.getScore());
            info.setTitle(req.getTitle());
            info.setContent(req.getContent());
            info.setCreateTime(tm);
            info.setUpdateTime(tm);

            try {
                String data = mapper.writeValueAsString(info);

                ApiRsp apiRsp = this.wdb().CreateObj(cuuid, data, new ArrayList<String>());
                if (apiRsp.getCode() == 200) {
                    
                    ApiRsp idx_rsp = this.wdb().CreateIndex(index_key, cuuid, new ArrayList<String>(
                        Arrays.asList(
                            "title:str:=" + info.getTitle(),
                            "score:num:=" + info.getScore(),
                            "updateTime:num:=" + info.getUpdateTime()
                        )
                    ));
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
                    SearchInfo info = mapper.readValue(apiRsp.getData(), SearchInfo.class);
                    info.setTitle(req.getTitle());
                    info.setContent(req.getContent());
                    info.setUpdateTime(tm);

                    String data = mapper.writeValueAsString(info);
                    ApiRsp api_prsp = this.wdb().UpdateObj(req.getUuid(), data);
                    if (api_prsp.getCode() == 200) {
                        ApiRsp idx_rsp = this.wdb().UpdateIndex(index_key, index_key, info.getUuid(), new ArrayList<String>(
                            Arrays.asList(
                                "title:str:=" + req.getTitle(),
                                "score:num:=" + req.getScore(),
                                "updateTime:num:=" + info.getUpdateTime()
                            )
                        ));
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

    @GetMapping("/search/info")
    public SearchInfoRsp search_info(SearchInfoReq req) {
        String uid = this.auth(req.getO());
        SearchInfoRsp rsp = new SearchInfoRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().GetObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                SearchInfo info = mapper.readValue(apiRsp.getData(), SearchInfo.class);

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

    @PostMapping("/search/list")
    public SearchListRsp search_list(@RequestBody SearchListReq req) {
        String uid = this.auth(req.getO());
        SearchListRsp rsp = new SearchListRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ArrayList<String> arr = new ArrayList<String>();
        if (req.getTitle().isEmpty() == false) {
            arr.add(String.format("[\"title\",\"reg\",\"^.*%s.*$\"]", req.getTitle()));
        }
        if (req.getScore().isEmpty() == false) {
            arr.add(String.format("[\"score\",\">=\",%s]", req.getScore()));
        }
        if (req.getBegin().isEmpty() == false && req.getEnd().isEmpty() == false) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                long cbegin = df.parse(req.getBegin()).getTime() / 1000;
                long cend = df.parse(req.getEnd()).getTime() / 1000;
                arr.add(String.format("[\"updateTime\",\">=\",%d,\"<=\",%d]", cbegin, cend));
            } catch (Exception e) {
                rsp.setCode(400);
                rsp.setMsg("parse err");
                return rsp;
            }
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
        } else if (req.getOrder().equals("sasc")) {
            order = "score ASC";
        } else if (req.getOrder().equals("sdesc")) {
            order = "score DESC";
        }

        String index_key = "my_search_index_" + uid;
        ListRsp listRsp = this.wdb().ListIndex(index_key, condition, req.getOffset(), req.getLimit(), order);
        if (listRsp.getCode() == 200) {
            rsp.setCode(200);
            rsp.setTotal(listRsp.getTotal());
            ArrayList<SearchListInfo> lists = new ArrayList<>();
            for (String item: listRsp.getList()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    SearchInfo info = mapper.readValue(item, SearchInfo.class);

                    SearchListInfo clist_info = new SearchListInfo();
                    clist_info.setUuid(info.getUuid());
                    clist_info.setTitle(info.getTitle());
                    clist_info.setScore(info.getScore());
                    clist_info.setTime(info.getUpdateTime());

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

    @DeleteMapping("/search/del")
    public SearchRsp search_del(SearchInfoReq req) {
        String uid = this.auth(req.getO());
        SearchRsp rsp = new SearchRsp();
        if (uid.equals("")) {
            rsp.setCode(403);
            rsp.setMsg("no login");
            return rsp;
        }

        ApiRsp apiRsp = this.wdb().DelObj(req.getUuid());
        if (apiRsp.getCode() == 200) {
            ArrayList<String> index_key = new ArrayList<>(Arrays.asList("my_search_index_" + uid));
            ApiRsp idx_rsp = this.wdb().DelIndex(index_key, req.getUuid());
            if (idx_rsp.getCode() == 200) {
                rsp.setCode(200);
                rsp.setUuid(req.getUuid());
            } else {
                rsp.setCode(400);
                rsp.setMsg(idx_rsp.getMsg());
            }
        } else {
            rsp.setCode(400);
            rsp.setMsg(apiRsp.getMsg());
        }

        return rsp;
    }
}
