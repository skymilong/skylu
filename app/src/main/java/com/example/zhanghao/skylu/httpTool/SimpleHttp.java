package com.example.zhanghao.skylu.httpTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
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

    static {
        sExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public interface HttpCallback<T> {
        public void onSuccess(T response);

        public void onError(T error);
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
                    urlConnection = (HttpURLConnection) url.openConnection();

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
                        callback.onSuccess(response.toString());
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), ENCODE));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        callback.onError(response.toString());
                    }

                    out.flush();
                    out.close();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
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



    /** * 封装请求体信息 * @param params 请求体 参数 * @param encode 编码格式 * @return 返回封装好的StringBuffer */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
