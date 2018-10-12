package com.example.zhanghao.skylu.httpTool;

import com.example.zhanghao.skylu.httpTool.dz.AddIgnoreListResp;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetMultiMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetUserInfos;
import com.example.zhanghao.skylu.httpTool.dz.GetVcodeAndHoldMobilenumResp;
import com.example.zhanghao.skylu.httpTool.dz.GetVcodeAndReleaseMobileResp;
import com.example.zhanghao.skylu.httpTool.dz.LogonResp;

public interface APICommon {
    LogonResp loginIn(String uid, String pwd);
    GetMobilenumResp getMobilenum(int pid, String uid, String token);
    GetMultiMobilenumResp getMobilenum(int pid, String uid,String token, int size);
    GetMobilenumResp getMobilenum(int pid, String uid, String token,String province,String operator,String notProvince,String vno);
    GetMultiMobilenumResp getMobilenum(int pid, String uid,String token, int size, String province, String operator,String notProvince, String vno);
    GetVcodeAndHoldMobilenumResp getVcodeAndHoldMobilenum(String mobile,String uid, String token, String next_pid, String author_uid);
    GetVcodeAndReleaseMobileResp getVcodeAndReleaseMobile(String mobile, String uid, String token, String author_uid);
    AddIgnoreListResp addIgnoreList(int pid, String mobiles, String uid,String token);
    /**
     * 用户获取个人用户信息
     * @param uid
     * @param token
     * @return 用户信息
     */
    GetUserInfos getUserInfos(String uid, String token);
}
