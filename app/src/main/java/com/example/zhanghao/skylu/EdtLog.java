package com.example.zhanghao.skylu;

import android.widget.EditText;

import java.util.Date;

public class EdtLog {
    public static void Log(String content,EditText editLogText){
        editLogText.setText(new Date().toString()+":"+content+"/r/n");
    }
}
