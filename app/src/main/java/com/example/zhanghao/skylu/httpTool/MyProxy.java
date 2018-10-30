package com.example.zhanghao.skylu.httpTool;

import android.util.Log;

import com.example.zhanghao.skylu.httpTool.RuoKuai.RuoKuai;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class MyProxy {

    private static final String POST_URL= "http://api.xdaili.cn/xdaili-api//greatRecharge/getGreatIp";
    private String param;
    private static MyProxy myProxy;

    public static final MyProxy getInstance(String spId ,String orderno){

        myProxy= new MyProxy(orderno,spId);
        return myProxy;
    }

    public static final MyProxy getInstance(){
        return myProxy;
    }

    private  MyProxy(String orderno,String spId){
        this.param = "spiderId="+spId+"&orderno="+orderno+"&returnType=1&count=1";
    }
    public Proxy getProxy(){
        String[] split=new String[3];
        try {
            String s = RuoKuai.httpRequestData(POST_URL, param);
            Log.i("获取代理：",s);
            s = s.replaceAll("\r|\n|\\s", "");
            split = s.split(":");
        } catch (IOException e) {
            Log.e("获取代理",e.getMessage());
            return null;
        }
        return new Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress(split[0],new Integer(split[1])));
    }
}
