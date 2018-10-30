package com.example.zhanghao.skylu.httpTool.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zhanghao.skylu.CommonTool;
import com.example.zhanghao.skylu.httpTool.APICommonDM;
import com.example.zhanghao.skylu.httpTool.APICommonJM;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetVcodeAndReleaseMobileResp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class YunBay {
    public static int POOL_SIZE = 8;

    private static ExecutorService sExecutorService;

    private static int READ_TIME_OUT = 20* 1000;

    private static int CONNECTE_TIME_OUT = 20 * 1000;

    private static final String ENCODE = "UTF-8";

    private static Proxy proxy = null;

    static {
        sExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public interface YunBayCallback<T> {
        void onRegistSuccess(T response);

        void onGetPhone(T phone);

        void onGetCaptureCode(T code);

        void onError(T error);

        void onDestroy();

        void onSendSucess(T s);
    }

    public static void startRegist(final APICommonJM jm, final APICommonDM dm, final Map<String, String> param, final YunBayCallback<String> callback) {
        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                int count = 20;
                while (count-- > 0) {
                    //获取手机号
                    GetVcodeAndReleaseMobileResp result = null;
                    GetMobilenumResp mobilenum = jm.getMobilenum(Integer.parseInt(param.get("pid")), param.get("uid"), param.get("token"));
                    Log.i("获取到手机号：", mobilenum.getResult());
                    callback.onGetPhone(mobilenum.getMobile());
                    String rid = "" + new Date().getTime();
                    if (mobilenum.isState()) {
                        //获取图片验证码
                        String pic = getPic(param.get("softid"), param.get("softkey"), param.get("uname"), param.get("pwd"), param.get("inviteId"), dm, rid);
                        String[] codeAndId = pic.split(";");


                        callback.onGetCaptureCode("图片-" + codeAndId[0]);

                        //发送验证码
                        String isSuccess = send(mobilenum.getMobile(), codeAndId[0], param.get("inviteId"), rid);
                        callback.onSendSucess(isSuccess);

                        //获取手机验证码
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

                                //注册
                                String regist = "";
                                try {
                                    regist = regist(mobilenum.getMobile(), resultCode, param.get("password"), param.get("zfpwd"), param.get("inviteId"), rid);
                                    callback.onRegistSuccess(regist);
                                    break;
                                } catch (Exception e) {
                                    callback.onError(e.getMessage());
                                }
                            }
                            try {
                                callback.onError("正在获取验证码。。");
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }

                    }
                    callback.onDestroy();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public static String regist(String tel, String code, String pwd, String zpwd, String inviteid, String rid) {
        Map<String, String> param = new HashMap<>();
        param.put("tel", tel);
        param.put("cc", "+86");
        param.put("type", "0");
        param.put("code", code);
        param.put("password", pwd);
        param.put("zjpassword", zpwd);
        param.put("from_inviteid", inviteid);

        try {
            String s = doPost(getPicHeaders(inviteid, rid), param, "https://m.yunbay.com/v1/account/reg");
            JSONObject jsonObject = JSON.parseObject(s);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                return data.getString("token");
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String send(String tel, String imgCode, String inviteId, String rid) {
        Map<String, String> param = new HashMap<>();
        param.put("tel", tel);
        param.put("cc", "+86");
        param.put("type", "0");
        param.put("imgkey", "web" + rid + "");
        param.put("imgcode", imgCode);
        try {
            String s = doPost(getPicHeaders(inviteId, rid), param, "https://m.yunbay.com/v1/account/sms/send");
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //发送图片验证码并且打码返回
    private static String getPic(String solftId, String solftKey, String username, String password, String inviteId, APICommonDM dm, String rid) {
        Map<String, String> ret = dm.creatByUrl(solftId, solftKey, "1040", username, password, "https://m.yunbay.com/v1/imgcode/get", getRequestData(getPicBody(rid), "UTF-8").toString(), getPicHeaders(inviteId, rid));
        String vCode = "";
        String vId = "";
        if (!ret.get("Id").equals("")) {
            vCode = ret.get("Result");
            vId = ret.get("Id");
            return vCode + ";" + vId;
        } else {
            System.out.println(ret.toString());
            return "error";
        }
    }

    private static Map<String, String> getPicHeaders(String invitedId, String rid) {
        Map<String, String> map = new HashMap<>();
        map.put("Host", "m.yunbay.com");
        map.put("content-type","application/json");
        map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36");
        map.put("Referer", "https://m.yunbay.com/register/" + invitedId + "er");
        map.put("Cookie", "captcha=\"web" + rid + "\"");
        map.put("Accept", "*/*");
        map.put("Connection","keep-alive");
        map.put("x-yf-appid", "account");
        map.put("x-yf-platform", "web");
        map.put("x-yf-rid", rid);
        map.put("sign", CommonTool.getUUID());
        map.put("x-yf-version", "1.0");
        return map;
    }

    private static Map<String, String> getPicBody(String rid) {
        Map<String, String> map = new HashMap<>();
        map.put("platform", "web");
        map.put("key", "web" + rid + "");
        return map;
    }

    private static byte[] getImage(Map<String, String> headers, Map<String, String> param, String url) throws IOException {
        //构造请求头

        HttpURLConnection urlConnection = null;
        byte[] img;
        byte[] data = getRequestData(param, "utf-8").toString().getBytes();//获得请求体

        try {
            URL uri = new URL(url);
            if (proxy != null) {
                urlConnection = (HttpURLConnection) uri.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) uri.openConnection();
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
            if (param.size() != 0) out.write(data);

            int code = urlConnection.getResponseCode();
            if (code >= 200 && code < 400) {

                Bitmap bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                img = Bitmap2Bytes(bitmap);
            } else {
                return null;
            }

            out.flush();
            out.close();
            return img;
        } catch (SocketTimeoutException e) {
            Log.e("代理：", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("代理：", e.getMessage());
            return null;
        } finally {

        }
    }

    private static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 封装请求体信息 *
     *
     * @param params 请求体 参数
     * @param encode 编码格式
     * @return 返回封装好的StringBuffer
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            if (stringBuffer.length() > 2) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }


    private static String doPost(Map<String, String> headers, Map<String, String> param, String url) throws IOException {
        //构造请求头

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        byte[] data = getRequestData(param, "utf-8").toString().getBytes();//获得请求体
        StringBuffer response = new StringBuffer();
        try {
            URL uri = new URL(url);
            if (uri.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();

                if (proxy != null) {
                    HttpsURLConnection https = (HttpsURLConnection) uri
                            .openConnection(proxy);
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    urlConnection = https;
                } else {
                    HttpsURLConnection https = (HttpsURLConnection) uri
                            .openConnection();
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    urlConnection = https;
                }
            } else {
                if (proxy != null) {
                    urlConnection = (HttpURLConnection) uri.openConnection(proxy);
                } else {
                    urlConnection = (HttpURLConnection) uri.openConnection();
                }
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

            urlConnection.connect();

            //写入数据
            OutputStream out = urlConnection.getOutputStream();
            if (param.size() != 0) out.write(data);

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
            Log.e("代理：", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("代理：", e.getMessage());
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("代理：", e.getMessage());
                    return null;
                }
            }
        }


        return response.toString();
    }

    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}

