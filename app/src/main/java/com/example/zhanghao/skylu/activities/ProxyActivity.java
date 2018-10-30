package com.example.zhanghao.skylu.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhanghao.skylu.R;
import com.example.zhanghao.skylu.httpTool.MyProxy;

import java.net.Proxy;

public class ProxyActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_save;
    private EditText editText_order;
    private EditText editText_spid;
    private Context context;

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String[] proxy = msg.obj.toString().split(";");

                    SharedPreferences proxyShare = getSharedPreferences("proxy", MODE_PRIVATE);
                    SharedPreferences.Editor edit = proxyShare.edit();
                    edit.putString("spiderId",proxy[0]);
                    edit.putString("oderId",proxy[1]);
                    edit.commit();
                    Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("讯代理");
        context = getApplicationContext();
        btn_save = findViewById(R.id.btn_save_xdl);
        editText_order = findViewById(R.id.edt_order_no);
        editText_spid = findViewById(R.id.edt_spider_id);

        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save_xdl:
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyProxy myProxy = MyProxy.getInstance(editText_spid.getText().toString().trim(),editText_order.getText().toString().trim());
                        Proxy proxy = myProxy.getProxy();
                        if(proxy==null){

                        }
                        Message message = new Message();
                        message.what=1;
                        message.obj=editText_spid.getText().toString().trim()+";"+editText_order.getText().toString().trim();
                        uiHandler.sendMessage(message);
                    }
                });
                thread.start();
                break;
        }

    }
}
