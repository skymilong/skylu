package com.example.zhanghao.skylu.httpTool;

import com.alibaba.fastjson.JSONObject;
import com.example.zhanghao.skylu.httpTool.RuoKuai.RuoKuaiSuccess;

import java.io.IOException;
import java.util.Map;

public interface APICommonDM {
    /**
     * 查询用户信息
     * @param username
     * @param password
     * @return
     */
    JSONObject getInFo(String username, String password);
    /**
     * 答题
     * @param url            请求URL，不带参数 如：http://api.ruokuai.com/create.json
     * @param param            请求参数，如：username=test&password=1
     * @param data            图片二进制流
     * @return				平台返回结果XML样式
     * @throws IOException
     */
    Map<String, String> httpPostImage(String url, String param,
                                      byte[] data) throws IOException;

    /**
     * 上传题目图片返回结果
     * @param username        用户名
     * @param password        密码
     * @param typeid        题目类型
     * @param timeout        任务超时时间
     * @param softid        软件ID
     * @param softkey        软件KEY
     * @param filePath        题目截图或原始图二进制数据路径
     * @return
     * @throws IOException
     */
    Map<String, String> createByPost(String username, String password,
                                     String typeid, String timeout, String softid, String softkey,
                                     String filePath);


    Map<String, String> creatByUrl(String softId, String softKey, String typeid, String username, String password, String url, String body, Map<String, String> headers);

    RuoKuaiSuccess httpPostImageBase64(String softId, String softKey, String typeid, String timeout, String username, String password, String base64);
}
