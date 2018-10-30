package com.example.zhanghao.skylu.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhanghao.skylu.CommonTool;
import com.example.zhanghao.skylu.MainActivity;
import com.example.zhanghao.skylu.R;
import com.example.zhanghao.skylu.httpTool.APICommonDM;
import com.example.zhanghao.skylu.httpTool.APICommonJM;
import com.example.zhanghao.skylu.httpTool.InstanceYZ;
import com.example.zhanghao.skylu.httpTool.SimpleHttp;
import com.example.zhanghao.skylu.httpTool.activity.DaXiong;
import com.example.zhanghao.skylu.httpTool.activity.YunBay;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunListsFragment extends Fragment {
    private ListView mListView;
    private List<GetMobilenumResp> list = new ArrayList<>();
    private  RunListAdapter adapter;
    private Button btn_start;


    public RunListsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_run_lists, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = getActivity().findViewById(R.id.list_view_run);
        btn_start = getActivity().findViewById(R.id.qm_btn_clean_runlist);

        initListView();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yunBay();
            }
        });


    }

    private void yunBay(){
        Map<String, String> headers = new HashMap<>();

        Map<String, String> properties = OptionsFragment.param;
        int count = new Integer(properties.get("count"));

        Map<String, String> params = getJMProp("jm");
        Map<String, String> dm1 = getDmprop("dm");
        params.putAll(dm1);
        //headers.put("User-Agent", "okhttp/3.5.0");

        //项目配置
        params.put("typekey", "1040");
        params.put("pid", properties.get("projectId"));//项目id
        params.put("inviteId", properties.get("inviteId"));

        //请求参数配置
        params.put("password", "7bb0fcd4d5ae487e1a2d501ec62367a69f35308a");
        params.put("zfpwd","b884928d33c08949d0a79b7c1e6ae1b874628fda");

        APICommonJM jm = InstanceYZ.getInstance("jm");
        APICommonDM dm = InstanceYZ.getDMInstance("dm");

        YunBay.startRegist(jm, dm, params, new YunBay.YunBayCallback<String>() {
            @Override
            public void onRegistSuccess(String response) {
                addLog("注册成功："+response);
            }

            @Override
            public void onGetPhone(String phone) {
                addLog("获取手机号："+phone);
            }

            @Override
            public void onGetCaptureCode(String code) {
                addLog("获取验证码："+code);
            }

            @Override
            public void onError(String error) {
                addLog("出错了"+error);
            }

            @Override
            public void onDestroy() {
                addLog("线程结束");
            }

            @Override
            public void onSendSucess(String s) {
                addLog("请求验证码成功，开始获取验证码!-"+s);
            }
        });
    }

    private void DaXiong() {
        Map<String, String> headers = new HashMap<>();

        Map<String, String> properties = OptionsFragment.param;
        int count = new Integer(properties.get("count"));

        Map<String, String> params = getJMProp("jm");
        Map<String, String> dm1 = getDmprop("dm");
        params.putAll(dm1);
        headers.put("User-Agent", "okhttp/3.5.0");


        params.put("typekey", "1040");
        params.put("pid", properties.get("projectId"));//项目id
        params.put("inviteId", properties.get("inviteId"));

        params.put("password", properties.get("pwd"));
        params.put("machine_id", CommonTool.getUUID());
        params.put("cid", CommonTool.getUUID());
        params.put("vCod", "2254");

        APICommonJM jm = InstanceYZ.getInstance("jm");
        APICommonDM dm = InstanceYZ.getDMInstance("dm");
            DaXiong.getPhone(jm, dm, params, new DaXiong.DXCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    addLog("砍价成功：" + response);
                }

                @Override
                public void onGetPhone(String phone) {
                    addLog("获取手机号：" + phone);
                }

                @Override
                public void onGetCaptureCode(String code) {
                    addLog("获取验证码：" + code);
                }

                @Override
                public void onLogin(String token) {
                    addLog("注册成功：" + token);
                }

                @Override
                public void onGetToken(String url) {
                    addLog("获取accesstoken：" + url);
                }

                @Override
                public void onError(String error) {
                    addLog("出错：" + error);
                }

                @Override
                public void onDestroy() {
                    addLog("线程结束");
                }

                @Override
                public void sendSucess(String s) {
                    addLog("发送验证码成功："+s);
                }
            });

        }


    /**
     * 盛天云盘邀请
     */
    private void other(){
        Map<String, String> properties = OptionsFragment.param;
        int count = new Integer(properties.get("count"));

        Map<String,String> params = getJMProp("jm");
        params.put("pid",properties.get("projectId"));//项目id
        params.put("inviteId",properties.get("inviteId"));
        APICommonJM jm = InstanceYZ.getInstance("jm");

        for (int j=0;j<1;j++){
            final GetMobilenumResp phoneResp = new GetMobilenumResp(false, "手机号", "正在获取...");
            list.add(phoneResp);

            final int finalJ = list.size()-1;
            SimpleHttp.getPhone(jm, params, new SimpleHttp.HttpCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    //获取到手机号和验证码
                    System.out.println(response);
                    addLog(response);

                    phoneResp.setResult("获取验证码成功，开始注册！");

                }

                @Override
                public void onGetPhone(String phone) {

                    //TODO 获取到手机号打印日志
                    System.out.println("获取到手机号："+phone);
                    addLog("获取到手机号："+phone);
                    //TODO 设置item显示手机号

                    phoneResp.setMobile(phone);
                    phoneResp.setResult("获取手机号成功，开始获取验证码！");

                }

                @Override
                public void onLogin(String url) {
                    //获取到登录URL
                    System.out.println(url);
                    addLog("注册成功获取到登录url："+url);

                    phoneResp.setResult("获取手机号成功，开始获取验证码！");

                }

                @Override
                public void onError(String error) {
                    System.out.println("出错了:"+error);
                    addLog("出错了："+error);

                    phoneResp.setResult("出错了"+error);

                }

                @Override
                public void onDestroy() {
                    System.out.println("该线程结束");
                    addLog("该线程结束");
                    phoneResp.setResult("注册完成");
                }
            });
        }
    }

    private Map<String,String> getJMProp(String ps) {
        Map<String,String> map = new HashMap<>();
        SharedPreferences jm = getActivity().getSharedPreferences(ps, MODE_PRIVATE);
        String token = jm.getString(ps + ":token", "");
        String uname = jm.getString(ps + ":uname", "");
        String pwd = jm.getString(ps + ":pwd", "");
        Log.i("获取接码配置：","获取到信息："+token);
        map.put("token",token);
        map.put("uid",uname);
        //map.put("pwd",pwd);
        return map;
    }

    private Map<String,String> getDmprop(String ps){
        Map<String,String> map = new HashMap<>();
        SharedPreferences jm = getActivity().getSharedPreferences(ps, MODE_PRIVATE);
        String token = jm.getString(ps + ":token", "");
        String uname = jm.getString(ps + ":uname", "");
        String pwd = jm.getString(ps + ":pwd", "");
        Log.i("获取打码配置：","获取到信息："+uname+"-"+pwd);
        map.put("timeout","60");
        map.put("softid","115033");
        map.put("softkey","5e9ed7a70cab4baebc938cc2cc19eefa");
        map.put("uname",uname);
        map.put("pwd",pwd);
        return map;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getActivity(),"结束",Toast.LENGTH_SHORT).show();
    }

    private void initListView() {
        adapter = new RunListAdapter(getActivity(),list);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

   private void addLog(String log){
       Intent intent = new Intent("showLog");
       intent.putExtra("log", log);
       LocalBroadcastManager.getInstance(getActivity())
               .sendBroadcast(intent);
   }

}
