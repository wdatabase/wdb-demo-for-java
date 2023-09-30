package com.wdb.demo;

import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import io.github.wdatabase.wdbdrive.Wdb;


public class Comm {
    public long time(){
        return System.currentTimeMillis() / 1000;
    }

    public int doublecmp(double num1, double num2){
        if(num1 - num2 > 0.000001){
            return 1;
        }else if (num1 - num2 < -0.000001){
            return -1;
        }else {
            return 0;
        }
    }

    public String uuid(){
        return String.valueOf(UUID.randomUUID());
    }

    public String hash(String data){
        return DigestUtils.sha256Hex(data);
    }

    public String sign(String uid, long tm){
        return this.hash(String.format("wdb_dfgrDR43d_%s%d", uid, tm));
    }

    public String auth(String o){
        String[] arr = o.split("_");
        if(arr.length == 3) {
            String uid = arr[0];
            long tm = Long.parseLong(arr[1]);
            String sg = arr[2];
            String csg = this.sign(uid, tm);
            long ctm = this.time();
            if(tm > ctm || ctm - tm > 6000){
                return "";
            } else if(sg.equals(csg)) {
                return uid;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public Wdb wdb(){
        return new Wdb("http://127.0.0.1:8000", "key");
    }
}
