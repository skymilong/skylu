package com.example.zhanghao.skylu.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhanghao.skylu.EdtLog;
import com.example.zhanghao.skylu.MainActivity;
import com.example.zhanghao.skylu.R;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;

import java.util.List;
import java.util.logging.Handler;

public class RunListAdapter extends BaseAdapter {

    private Context context;
    private List<GetMobilenumResp> datas;


    public RunListAdapter(Context context, List<GetMobilenumResp> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if(datas!=null){
            return datas.size();
        }
        return 0;
    }

    @Override
    public GetMobilenumResp getItem(int position) {
        if(datas!=null){
           return  datas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null!=convertView){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            viewHolder = new ViewHolder ();
            convertView = LayoutInflater.from (context).inflate (R.layout.run_list_item, parent, false);

            viewHolder.phone = convertView.findViewById (R.id.tv_phone_num);
            viewHolder.msg = convertView.findViewById (R.id.run_msg);
            viewHolder.phone.setText (getItem (position).getMobile());
            viewHolder.msg.setText (getItem (position).getResult ());
            convertView.setTag (viewHolder);
        }

        return convertView;
    }

    public class ViewHolder{
        private TextView phone;
        private TextView msg;

        public TextView getPhone() {
            return phone;
        }

        public void setPhone(TextView phone) {
            this.phone = phone;
        }

        public TextView getMsg() {
            return msg;
        }

        public void setMsg(TextView msg) {
            this.msg = msg;
        }
    }


}
