package com.example.zhanghao.skylu.httpTool;

import com.example.zhanghao.skylu.httpTool.dz.API;

public class InstanceJM {
    public  static APICommon getInstance(String jmId){
        switch (jmId){
            case "dz":
                return API.getInstance();
                default:
                    return API.getInstance();
        }
    }
}
