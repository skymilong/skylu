package com.example.zhanghao.skylu.httpTool.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.zhanghao.skylu.httpTool.APICommonDM;
import com.example.zhanghao.skylu.httpTool.APICommonJM;
import com.example.zhanghao.skylu.httpTool.MyProxy;
import com.example.zhanghao.skylu.httpTool.RuoKuai.RuoKuaiSuccess;
import com.example.zhanghao.skylu.httpTool.SimpleHttp;
import com.example.zhanghao.skylu.httpTool.dz.AddIgnoreListResp;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetVcodeAndReleaseMobileResp;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DaXiong {

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
    public interface DXCallback<T> {
        void onSuccess(T response);
        void onGetPhone(T phone);
        void onGetCaptureCode(T code);
        void onLogin(T token);
        void onGetToken(T url);
        void onError(T error);
        void onDestroy();

        void sendSucess(T s);
    }

    public static void getPhone(final APICommonJM jm, final APICommonDM dm, final Map<String,String> param, final DXCallback<String> callback){
        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                int count = 10;
                while (count-- > 0) {
                    boolean flag = true;
                    String accessToken = "";
                    GetVcodeAndReleaseMobileResp result = null;
                    GetMobilenumResp mobilenum = jm.getMobilenum(Integer.parseInt(param.get("pid")), param.get("uid"), param.get("token"));
                    Log.i("获取到手机号：", mobilenum.getResult());
                    callback.onGetPhone(mobilenum.getMobile());
                    String vCode = "";
                    String vId = "";
                    if (mobilenum.isState()) {
                        //获取图片验证码
                        Map<String, String> vCodeheaders = new HashMap<>();
                        vCodeheaders.put("User-Agent", "okhttp/3.5.0");
                        String vCodeUrl = "http://api.bbbearmall.com/api/member/createValidCode";
                        Map<String, String> vCodeParam = new HashMap<>();
                        vCodeParam.put("machine_id", param.get("machine_id"));
                        vCodeParam.put("cid", param.get("cid"));

                        String pic_param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s",
                                param.get("uname"), param.get("pwd"),
                                param.get("typekey"), "90", param.get("softid"), param.get("softkey"));


                        int count_t = 2;

                        try {
                            byte[] image = getImage(vCodeheaders, vCodeParam, vCodeUrl);
                            Map<String, String> ret = dm.httpPostImage("http://api.ruokuai.com/create.json", pic_param, image);

                            if (!ret.get("Id").equals("")) {
                                vCode = ret.get("Result");
                                vId = ret.get("Id");
                                callback.onGetCaptureCode(vCode);
                            } else {
                                System.out.println("获取验证码出错，重新获取。。");
                            }

                            //发送验证码
                            Map<String, String> sendCodeParam = new HashMap<>();
                            sendCodeParam.put("machine_id", param.get("machine_id"));
                            sendCodeParam.put("mobile", mobilenum.getMobile());
                            sendCodeParam.put("validType", "2");
                            sendCodeParam.put("vCod", vCode);
                            sendCodeParam.put("cid", param.get("cid"));
                            do {
                                //发送验证码请求
                                String s = doPost(vCodeheaders, sendCodeParam, "http://api.bbbearmall.com/api/member/createSmsVCod");
                                callback.sendSucess(s);
                                if (s.indexOf("1") > 0) {
                                    break;
                                } else {
                                    //换IP 线程暂停0.5秒
                                    try {
                                        MyProxy myProxy = MyProxy.getInstance();
                                        if (myProxy == null) {
                                            callback.onError("未设置代理");
                                            return;
                                        }
                                        proxy = myProxy.getProxy();
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        Log.e("线程暂停：", "线程暂停异常：" + e.getMessage());
                                    }
                                }
                            } while (count_t-- > 0);

                        } catch (IOException e) {
                            System.out.println("获取验证码出错，重新获取。。");
                            return;
                        }

                        int i = 15;
                        while (i-- > 0) {
                            //获取手机验证码
                            GetVcodeAndReleaseMobileResp vcodeAndReleaseMobile = jm.getVcodeAndReleaseMobile(mobilenum.getMobile(),
                                    param.get("uid"), param.get("token"), "skymilong66");
                            String resultCode = vcodeAndReleaseMobile.getResult();
                            callback.onGetCaptureCode(resultCode);
                            if (vcodeAndReleaseMobile.isState()) {
                                resultCode = resultCode.substring(resultCode.indexOf("码") + 1, resultCode.indexOf("，"));
                                System.out.println("验证码：" + resultCode);
                                //注册用户，获取token
                                String regist = "";

                                Map<String, Object> regResult = new HashMap<>();
                                try {
                                    regist = regist(param.get("pwd"), param.get("machine_id"), param.get("cid"), mobilenum.getMobile(), resultCode, vCode);


                                    accessToken = regist;
                                    callback.onGetToken(accessToken + ";" + param.get("cid"));
                                    System.out.println("注册成功");
                                    //TODO 请求砍价接口

                                        String cutp = cutPrice(vCodeheaders,param.get("inviteId"), accessToken, param.get("cid"));


                                        callback.onSuccess(cutp);
                                        System.out.println("砍价成功:" + cutp);
                                        Thread.sleep(500);


                                    break;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                callback.onError("正在获取验证码。。");
                                Thread.sleep(5000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private static  String cutPrice(Map<String,String> header,String bargainRecord_id,
                                    String access_token,String cid) throws IOException {

        String url = "http://api.bbbearmall.com/api/bargain/helpOthersBargain";
        Map<String,String> map = new HashMap<>();
        map.put("bargainRecord_id",bargainRecord_id);
        map.put("access_token",access_token);
        map.put("cid",cid);

        String s = doPost(header, map, url);

        return s;
    }

    private static byte[] getImage(Map<String,String> headers,Map<String,String> param,String url) throws IOException{
        //构造请求头

        HttpURLConnection urlConnection=null;
        byte[] img;
        byte[] data = getRequestData(param, "utf-8").toString().getBytes();//获得请求体

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
            //urlConnection.setRequestProperty("connection", "close");
            urlConnection.connect();

            //写入数据

            OutputStream out = urlConnection.getOutputStream();
            if(param.size()!=0)out.write(data);

            int code = urlConnection.getResponseCode();
            if (code >= 200 && code < 400) {

              Bitmap  bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
              img = Bitmap2Bytes(bitmap);
            } else {
                return null;
            }

            out.flush();
            out.close();
            return img;
        } catch (SocketTimeoutException e) {
            Log.e("代理：",e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("代理：",e.getMessage());
            return null;
        } finally {

        }
    }

    private static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    private static String regist(String pwd,String machineId,String cid,String moblie,String smsVCod,String vCode ) throws IOException {
        String token = "";
        String url = "http://api.bbbearmall.com/api/member/memberRegister";
        Map<String,String> headers = new HashMap<>();
        Map<String,String> params = new HashMap<>();

        headers.put("User-Agent","okhttp/3.5.0");

        params.put("password",pwd);
        params.put("machine_id",machineId);
        params.put("smsVCod",smsVCod);
        params.put("mobile",moblie);
        params.put("cid",cid);
        params.put("vCod",vCode);

        String result = doPost(headers, params, url);

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
        com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
        if(data==null){
            return token;
        }
        token = data.getString("access_token");
        Log.i("获取access_token:",token);
        return token;
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


}





