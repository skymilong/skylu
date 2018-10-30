package com.example.zhanghao.skylu.httpTool;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.zhanghao.skylu.EdtLog;
import com.example.zhanghao.skylu.httpTool.dz.AddIgnoreListResp;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetVcodeAndReleaseMobileResp;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttp {

    private static final String TAG = SimpleHttp.class.getSimpleName();

    public static int POOL_SIZE = 8;

    private static ExecutorService sExecutorService;

    private static int READ_TIME_OUT = 10 * 1000;

    private static int CONNECTE_TIME_OUT = 10 * 1000;

    private static final String ENCODE = "UTF-8";

    private static Proxy proxy = null;

    static {
        sExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public interface HttpCallback<T> {
         void onSuccess(T response);
         void onGetPhone(T phone);
         void onLogin(T url);
         void onError(T error);

        void onDestroy();
    }

    public static void doPost(String url, Map params, final Map headers, String encode, final HttpCallback<String> callback) {
        final String _url = url;
        final byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {

                    url = new URL(_url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                    return;
                }
                BufferedReader bufferedReader = null;
                StringBuffer response = new StringBuffer();
                HttpURLConnection urlConnection = null;

                try {
                    if(headers.containsKey("proxyIp")){
                        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress((String)headers.get("porxyIp"),(Integer) headers.get("porxyPort")));
                        //TODO 打印日志
                        System.out.println("获取到代理ip:"+headers.get("proxyIp"));
                        urlConnection = (HttpURLConnection) url.openConnection(proxy);
                    }else{
                        urlConnection = (HttpURLConnection) url.openConnection();
                    }


                    //设置请求头
                    Set<Map.Entry> entries = headers.entrySet();
                    for (Iterator<Map.Entry> iterator = entries.iterator(); iterator.hasNext(); ) {
                        Map.Entry next = iterator.next();
                        urlConnection.setRequestProperty(next.getKey().toString(), next.getValue().toString());
                    }

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setReadTimeout(READ_TIME_OUT);
                    urlConnection.setConnectTimeout(CONNECTE_TIME_OUT);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("connection", "close");
                    urlConnection.connect();

                    //写入数据
                    OutputStream out = urlConnection.getOutputStream();
                    out.write(data);

                    int code = urlConnection.getResponseCode();
                    if (code >= 200 && code < 400) {
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), ENCODE));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }

                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), ENCODE));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    out.flush();
                    out.close();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });
    }

    /**
     * 获取手机号
     * @param jm
     * @param param
     * @param callback
     */
    public static void getPhone(final APICommonJM jm, final Map<String,String> param, final HttpCallback<String> callback){
        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {

                while(true){
                    GetVcodeAndReleaseMobileResp result=null;
                    String channel = "acquisition|invitee|default";
                    GetMobilenumResp mobilenum = jm.getMobilenum(Integer.parseInt(param.get("pid")), param.get("uid"), param.get("token"));
                    Log.i("获取到手机号：",mobilenum.getResult());
                    if(mobilenum.isState()){
                        callback.onGetPhone(mobilenum.getMobile());
                        Map<String,String> headers = new HashMap<>();
                        Map<String,String> params = new HashMap<>();

                        String token = "";

                        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                        headers.put("Host","pan.bitqiu.com");
                        headers.put("Referer","https://pan.bitqiu.com/invite-friend?mafrom=acquisition&mipos=invitee&uid=105011922");
                        headers.put("Origin","https://pan.bitqiu.com");

                        do{
                            //发送验证码请求
                            token = sendCode(mobilenum.getMobile(),channel,headers);
                            if(token.equals("error")){
                                //换IP 线程暂停0.5秒
                                try {
                                    MyProxy myProxy = MyProxy.getInstance();
                                    if(myProxy==null){
                                        callback.onError("未设置代理");
                                        return;
                                    }
                                    proxy = myProxy.getProxy();
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    Log.e("线程暂停：","线程暂停异常："+e.getMessage());
                                }
                            }else{
                                break;
                            }
                        }while (true);

                        boolean flag=false;
                        // 继续获取验证码5秒一次，并且打印日志
                        for(int i=1;i<16;i++) {
                            if(i==15){
                                flag=true;
                            }
                            Log.i(mobilenum.getMobile()+"获取验证码：","第"+i+"次获取验证码");
                            result = jm.getVcodeAndReleaseMobile(mobilenum.getMobile(), param.get("uid"), param.get("token"), "skymilong66");
                            String resultCode = result.getResult();
                            if(result.isState()){
                                Log.i("获取验证码：", resultCode);
                                resultCode = resultCode.substring(resultCode.indexOf("码")+1,resultCode.indexOf("，"));
                                if(resultCode.length()>4){
                                    result.setVerifyCode(resultCode);
                                }
                                callback.onSuccess(result.getVerifyCode()+";"+result.getMobile());
                                break;
                            }else{
                                if(resultCode.equals("not_receive")){
                                    try {
                                        Log.i("获取验证码：", resultCode +"--五秒后再次获取");
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        callback.onError(e.getMessage());
                                        return;
                                    }
                                }else{
                                    Log.i("获取验证码：", resultCode);
                                    callback.onError(mobilenum.getResult());
                                    return;
                                }
                            }
                        }

                        //如果多次未获得，重新获取手机号
                        if(flag){
                            //TODO 加黑手机号
                            AddIgnoreListResp addIgnoreListResp = jm.addIgnoreList(Integer.parseInt(param.get("pid")), mobilenum.getMobile(), param.get("uid"), param.get("token"));
                            if (addIgnoreListResp.isState()){
                                System.out.println(addIgnoreListResp.getResult());
                            }
                            continue;
                        }

                        do {
                            params.put("phone",mobilenum.getMobile());
                            params.put("acode",result.getVerifyCode());
                            params.put("reference_id",param.get("inviteId"));
                            params.put("agree","on");
                            params.put("org_channel",channel);
                            params.put("token",token);
                            // 获取到验证码提交注册
                            Log.i("提交注册：","开始发送注册请求");
                            String loginUrl = "";
                            /** 发送注册请求**/
                            loginUrl = toRegist(headers,params);
                            //TODO 日志记录
                            System.out.println("提交注册：获取登录url-> "+loginUrl);
                            if (!loginUrl.equals("error")) {
                                callback.onLogin(loginUrl);
                                //TODO 访问登录地址，获取cookie，取到所有物品，发送给主号
                                break;
                            }else{
                                break;
                            }
                        }
                        while (true);
                    }else {
                        callback.onError(mobilenum.getResult());
                    }
                    callback.onDestroy();
                }

            }
        });

    }

    /**
     *  发送验证码
     * @param phone
     * @param org_channel
     * @param headers
     * @return token 失败返回error
     */
    private static String sendCode(String phone,String org_channel,Map<String,String> headers){
        String url = "https://pan.bitqiu.com/activity/getCodeByReference";
        try {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("phone",phone);
            stringStringHashMap.put("org_channel",org_channel);
            String result = doPost(headers,stringStringHashMap,url);
            Map parse =(Map)JSON.parse(result);
            Map data = (Map) parse.get("data");
            if(data==null){
                return "error";
            }
            String token = (String) data.get("token");
            Log.i("发送验证码请求:","发送验证码请求成功返回token:"+token);
            return token;
        } catch (IOException e) {
            return "error";
        }

    }

    /**
     * 发送注册请求 获取rul用于登录
     * @param headers
     * @param param
     * @return url
     */
    private static String toRegist(Map<String,String> headers,Map<String,String> param){
        String url = "https://pan.bitqiu.com/activity/loginAndRegByReference";
        try {
            String s = doPost(headers, param,url);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(s);
            if(jsonObject.getString("code").equals("10200")){
                String token = jsonObject.getJSONObject("data").getString("url");
                return token;
            }
        } catch (IOException e) {
           Log.e("发送注册请求:",e.getMessage());
           return "error";
        }
        return "error";
    }

    private static String doPost(Map<String,String> headers,Map<String,String> param,String url) throws IOException {
        //构造请求头

        HttpURLConnection urlConnection=null;
        BufferedReader bufferedReader = null;
        byte[] data = getRequestData(param, "utf-8").toString().getBytes();//获得请求体
        StringBuffer response = new StringBuffer();
        try {
            URL uri = new URL(url);
            if(proxy!=null){
                urlConnection = (HttpURLConnection)uri.openConnection(proxy);
            }else{
                urlConnection = (HttpURLConnection)uri.openConnection();
            }


        //设置请求头
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
            Map.Entry next = iterator.next();
            urlConnection.setRequestProperty(next.getKey().toString(), next.getValue().toString());
        }

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setReadTimeout(READ_TIME_OUT);
        urlConnection.setConnectTimeout(CONNECTE_TIME_OUT);
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("connection", "close");
        urlConnection.connect();

        //写入数据
        OutputStream out = urlConnection.getOutputStream();
        if(param.size()!=0)out.write(data);

        int code = urlConnection.getResponseCode();
        if (code >= 200 && code < 400) {
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), ENCODE));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), ENCODE));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
        }

        out.flush();
        out.close();
    } catch (SocketTimeoutException e) {
            Log.e("代理：",e.getMessage());
            return null;
    } catch (IOException e) {
            Log.e("代理：",e.getMessage());
            return null;
    } finally {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                Log.e("代理：",e.getMessage());
                return null;
            }
        }
    }


        return response.toString();
    }


    /** * 封装请求体信息 *
     * @param params 请求体 参数
     * @param encode 编码格式
     * @return 返回封装好的StringBuffer
     * */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            if(stringBuffer.length()>2){
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }


}
