package com.alcatel.wifilink.ui.sms.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2017/7/12.
 */

public class SmsDetailHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rl_smsdetail;// 总布局
    public ImageView iv_smsdetail_selected;// 选中LOGO

    public RelativeLayout rl_smsdetail_receiver;// 接收布局
    public TextView tv_smsdetail_text_receiver;// 接收文本
    public TextView tv_smsdetail_date_receiver;// 接收时间

    public RelativeLayout rl_smsdetail_send;// 发送布局
    public ImageView iv_smsdetail_failed_send;// 发送失败图标
    public TextView tv_smsdetail_text_send;// 发送文本
    public TextView tv_smsdetail_date_send;// 发送日期

    public SmsDetailHolder(View itemView) {
        super(itemView);
        // 总布局
        rl_smsdetail = (RelativeLayout) itemView.findViewById(R.id.rl_smsdetail);
        // selected logo
        iv_smsdetail_selected = (ImageView) itemView.findViewById(R.id.iv_smsdetail_selected);
        // receiver
        rl_smsdetail_receiver = (RelativeLayout) itemView.findViewById(R.id.rl_smsdetail_receiver);
        tv_smsdetail_text_receiver = (TextView) itemView.findViewById(R.id.tv_smsdetail_text_receiver);
        tv_smsdetail_date_receiver = (TextView) itemView.findViewById(R.id.tv_smsdetail_date_receiver);
        // send
        rl_smsdetail_send = (RelativeLayout) itemView.findViewById(R.id.rl_smsdetail_send);
        iv_smsdetail_failed_send = (ImageView) itemView.findViewById(R.id.iv_smsdetail_failed_send);
        tv_smsdetail_text_send = (TextView) itemView.findViewById(R.id.tv_smsdetail_text_send);
        tv_smsdetail_date_send = (TextView) itemView.findViewById(R.id.tv_smsdetail_date_send);
    }
}
