package com.example.zhanghao.skylu.httpTool;

import com.example.zhanghao.skylu.httpTool.RuoKuai.RuoKuai;
import com.example.zhanghao.skylu.httpTool.dz.API;

public class InstanceYZ {
    public  static APICommonJM getInstance(String jmId){
        switch (jmId){
            case "dz":
                return API.getInstance();
                default:
                    return API.getInstance();
        }
    }

    public static APICommonDM getDMInstance(String dmId){
        switch (dmId){
            case "rk":
                return RuoKuai.getInstance();
            default:
                return RuoKuai.getInstance();
        }
    }
}
