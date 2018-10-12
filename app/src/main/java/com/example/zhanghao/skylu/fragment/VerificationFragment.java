package com.example.zhanghao.skylu.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zhanghao.skylu.Entity.SpinnerJMItem;
import com.example.zhanghao.skylu.MainActivity;
import com.example.zhanghao.skylu.R;
import com.example.zhanghao.skylu.httpTool.APICommon;
import com.example.zhanghao.skylu.httpTool.InstanceJM;
import com.example.zhanghao.skylu.httpTool.SimpleHttp;
import com.example.zhanghao.skylu.httpTool.dz.API;
import com.example.zhanghao.skylu.httpTool.dz.LogonResp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerificationFragment extends Fragment implements View.OnClickListener {
    //private TextView textView;
    private Button btnJMLogin;
    private TextInputEditText edit_uname_JM,edit_pwd_JM;
    private Spinner spinnerJM;
    private ProgressDialog dialog ;

    private static Context context;

    private String JMId="";

    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();

        initView();
        initData();
        bindEvent();


    }

    private void initData() {
        List<SpinnerJMItem> list = new ArrayList<SpinnerJMItem>();
        list.add(new SpinnerJMItem("短租","dz"));
        ArrayAdapter<SpinnerJMItem> adapter= new ArrayAdapter<SpinnerJMItem>(context,
                android.R.layout.simple_spinner_item, list);
        spinnerJM.setAdapter(adapter);

        //添加弹出的对话框
        dialog = new ProgressDialog(getActivity()) ;
        dialog.setTitle("提示");
        dialog.setMessage("正在登录，请稍后···") ;
    }

    private boolean hasToken() {
        SharedPreferences token =context.getSharedPreferences("DZ_TOKEN",MODE_PRIVATE);
        String tokenString = token.getString("token", "-1");
        System.out.println(tokenString);
        if(tokenString.equals("-1")){
            return false;
        }
        return true;
    }

    private void initView(){
        btnJMLogin = (Button)getActivity().findViewById(R.id.btnJMLogin);
        edit_uname_JM = (TextInputEditText)getActivity().findViewById(R.id.JM_username);
        edit_pwd_JM = (TextInputEditText)getActivity().findViewById(R.id.JM_pwd);
        spinnerJM = (Spinner)getActivity().findViewById(R.id.spinnerJM);

    }

    private void bindEvent(){
        btnJMLogin.setOnClickListener(this);
        spinnerJM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JMId = spinnerJM.getSelectedItem().toString();
                System.out.println("获取到ID:"+JMId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void saveToken(String token,String uname,String pwd){
        Toast.makeText(getActivity(),token,Toast.LENGTH_SHORT);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("DZ_TOKEN",MODE_PRIVATE).edit();
        editor.putString("token",token);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnJMLogin:
                final String username =edit_uname_JM.getText().toString();
                final String pwd = edit_pwd_JM.getText().toString();
                //Toast.makeText(getApplicationContext(),"获取到账号密码为："+username+"-"+pwd,Toast.LENGTH_SHORT).show();
                new LoginTask().execute(username,pwd);
        }
    }

    public class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            btnJMLogin.setText("登录成功");
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            APICommon apiCommon = InstanceJM.getInstance(JMId);
            LogonResp logonResp = apiCommon.loginIn( strings[0],  strings[1]);
            if(logonResp.isState()){
                System.out.println("登录成功："+logonResp.getUid());
                //saveToken(logonResp.getToken(),username,pwd);

            }else{
                System.out.println("登录失败："+logonResp.getResult());
            }
            return logonResp.getResult();
        }
    }


}
