package com.example.zhanghao.skylu.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhanghao.skylu.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsFragment extends Fragment {

    private EditText edt_runnum,edt_count,edt_invite,edt_proj,edt_uname,edt_pwd;
    private Button btn_save;
    public static Map<String,String> param = new HashMap<>();

    public OptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = "s"+new Date().getTime();
                String pwd = "qq1314520";
                if(edt_uname.getText()==null||edt_uname.getText().toString().equals("")){
                    uname = edt_uname.getText().toString();
                }
                if(edt_pwd.getText()==null||edt_pwd.getText().toString().equals("")){
                    pwd = edt_pwd.getText().toString();
                }

                param.put("proNum",edt_runnum.getText().toString().trim());
                param.put("inviteId",edt_invite.getText().toString().trim());
                param.put("count",edt_count.getText().toString().trim());
                param.put("projectId",edt_proj.getText().toString().trim());
                param.put("uname",uname);
                param.put("pwd",pwd);

                Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        edt_runnum = getActivity().findViewById(R.id.pro_num);
        edt_count = getActivity().findViewById(R.id.reg_num);
        edt_invite = getActivity().findViewById(R.id.invite_id);
        edt_proj = getActivity().findViewById(R.id.proj_id);
        edt_pwd = getActivity().findViewById(R.id.reg_pwd);
        edt_uname = getActivity().findViewById(R.id.reg_uname);
        btn_save = getActivity().findViewById(R.id.btn_save_option);
    }

    private void saveProperties(){
        if(param.size()>0){
            SharedPreferences.Editor properties = getActivity().getSharedPreferences("properties", Context.MODE_PRIVATE).edit();

        }
    }
}
