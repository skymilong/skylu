package com.example.zhanghao.skylu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.zhanghao.skylu.Entity.SpinnerJMItem;
import com.example.zhanghao.skylu.httpTool.APICommonDM;
import com.example.zhanghao.skylu.httpTool.APICommonJM;
import com.example.zhanghao.skylu.httpTool.InstanceYZ;
import com.example.zhanghao.skylu.httpTool.dz.LogonResp;

import java.util.ArrayList;
import java.util.List;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String DM_ID="86656";
    private final static String DM_KEY="1affadc5daf346b08226e625e2d775bd";

    private Button btnJMLogin,btnDMLogin;
    private TextInputEditText edit_uname_JM,edit_pwd_JM,edit_uname_DM,edit_pwd_DM;
    private Spinner spinnerJM,spinnerDM;
    private ProgressDialog dialog ;

    private static Context context;

    private String JMId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        context = this;

        initView();
        initData();
        bindEvent();

    }

    private void initData() {
        List<SpinnerJMItem> listJM = new ArrayList<SpinnerJMItem>();
        List<SpinnerJMItem> listDM = new ArrayList<SpinnerJMItem>();
        listJM.add(new SpinnerJMItem("短租","dz"));
        listDM.add(new SpinnerJMItem("若快","rk"));

        ArrayAdapter<SpinnerJMItem> adapterJM= new ArrayAdapter<SpinnerJMItem>(context,
                android.R.layout.simple_expandable_list_item_1, listJM);

        ArrayAdapter<SpinnerJMItem> adapterDM= new ArrayAdapter<SpinnerJMItem>(context,
                android.R.layout.simple_expandable_list_item_1, listDM);

        spinnerJM.setAdapter(adapterJM);
        spinnerDM.setAdapter(adapterDM);
        //添加弹出的对话框
        dialog = new ProgressDialog(this) ;
        dialog.setTitle("提示");
        dialog.setMessage("正在登录，请稍后···") ;
    }


    private void initView(){
        btnJMLogin = (Button)findViewById(R.id.btnJMLogin);
        btnDMLogin = (Button)findViewById(R.id.btnDMLogin);

        edit_uname_JM = (TextInputEditText)findViewById(R.id.JM_username);
        edit_pwd_JM = (TextInputEditText)findViewById(R.id.JM_pwd);

        edit_uname_DM = (TextInputEditText)findViewById(R.id.DM_username);
        edit_pwd_DM = (TextInputEditText)findViewById(R.id.DM_pwd);
        spinnerJM = (Spinner)findViewById(R.id.spinnerJM);
        spinnerDM = (Spinner)findViewById(R.id.spinnerDM);

    }

    private void bindEvent(){
        btnJMLogin.setOnClickListener(this);
        btnDMLogin.setOnClickListener(this);

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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnJMLogin:
                String username_jm =edit_uname_JM.getText().toString();
                String pwd_jm = edit_pwd_JM.getText().toString();
                Toast.makeText(context,"获取到账号密码为："+username_jm+"-"+pwd_jm,Toast.LENGTH_SHORT).show();
                new JMLoginTask().execute(username_jm,pwd_jm);
                break;
            case R.id.btnDMLogin:
                String username_dm =edit_uname_DM.getText().toString();
                final String pwd_dm = edit_pwd_DM.getText().toString();
                Toast.makeText(context,"获取到账号密码为："+username_dm+"-"+pwd_dm,Toast.LENGTH_LONG).show();
                new DMLoginTask().execute(username_dm,pwd_dm);
                break;
        }
    }

    /**
     * 保存登录信息
     * @param token
     * @param uname
     * @param pwd
     * @param ps
     */
    private void saveToken(String token,String uname,String pwd,String ps){
        Toast.makeText(context,token,Toast.LENGTH_SHORT);
        SharedPreferences.Editor editor = context.getSharedPreferences(ps,MODE_PRIVATE).edit();
        editor.putString(ps+":token",token);
        editor.putString(ps+":uname",uname);
        editor.putString(ps+":pwd",pwd);
        editor.commit();
        //hasToken(ps);
    }

    /**
     * 接码
     */
    public class JMLoginTask extends AsyncTask<String, Void, LogonResp> {
        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LogonResp logonResp) {
            dialog.dismiss();

            if(logonResp.isState()){
                btnJMLogin.setText("登录成功");
                btnJMLogin.setEnabled(false);
            }else{
                Toast.makeText(context,"打码平台登陆失败:"+logonResp.getResult(),Toast.LENGTH_SHORT);
            }
            saveToken(logonResp.getToken(),logonResp.getUid(),edit_pwd_JM.getText().toString(),"jm");

            super.onPostExecute(logonResp);
        }

        @Override
        protected LogonResp doInBackground(String... strings) {
            APICommonJM apiCommon = InstanceYZ.getInstance(JMId);
            LogonResp logonResp = apiCommon.loginIn( strings[0],  strings[1]);

            return logonResp;
        }
    }

    /**
     * 打码
     */
    public class DMLoginTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            dialog.dismiss();
            if(!s.containsKey("Error")){
                btnDMLogin.setText("登录成功");
                saveToken("ruokuai",edit_uname_DM.getText().toString(),edit_pwd_DM.getText().toString(),"dm");
            }else{
                btnDMLogin.setText("重试");
                String error = s.getString("Error");
                if(error.indexOf("请")>0){
                    String substring = error.substring(error.indexOf("请") + 1, error.indexOf("秒"));
                    int sec = Integer.parseInt(substring);
                    CountDownTimer timer = new CountDownTimer(1000*sec, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            btnDMLogin.setEnabled(false);
                            btnDMLogin.setText("重试" + millisUntilFinished / 1000 );

                        }

                        @Override
                        public void onFinish() {
                            btnDMLogin.setEnabled(true);
                            btnDMLogin.setText("登录");

                        }
                    }.start();

                }
            }

            System.out.println(s.toJSONString());
            super.onPostExecute(s);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            APICommonDM apiCommonDM = InstanceYZ.getDMInstance("rk");
            JSONObject inFo = apiCommonDM.getInFo(strings[0], strings[1]);

            return inFo;
        }
    }

}


