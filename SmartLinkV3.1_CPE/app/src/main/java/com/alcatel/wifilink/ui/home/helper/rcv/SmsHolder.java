package com.alcatel.wifilink.ui.home.helper.rcv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2017/7/11.
 */

public class SmsHolder extends RecyclerView.ViewHolder {


    public RelativeLayout rl_sms;// 总布局
    public ImageView iv_smsPoint;// 未读小点
    public RelativeLayout rl_smsInfo;// 内容区
    public TextView tv_smsPhone;// 电话号码
    public TextView tv_smsCount;// 信息数量
    public ImageView iv_smsSendFailed;// 发送失败的LOGO
    public TextView tv_smsContent;// 信息浏览
    public TextView tv_smsDate;// 信息日期

    public SmsHolder(View itemView) {
        super(itemView);
        rl_sms = (RelativeLayout) itemView.findViewById(R.id.rl_sms);
        iv_smsPoint = (ImageView) itemView.findViewById(R.id.iv_smsPoint);
        rl_smsInfo = (RelativeLayout) itemView.findViewById(R.id.rl_smsInfo);
        tv_smsPhone = (TextView) itemView.findViewById(R.id.tv_smsPhone);
        tv_smsCount = (TextView) itemView.findViewById(R.id.tv_smsCount);
        iv_smsSendFailed = (ImageView) itemView.findViewById(R.id.iv_smsSendFailed);
        tv_smsContent = (TextView) itemView.findViewById(R.id.tv_smsContent);
        tv_smsDate = (TextView) itemView.findViewById(R.id.tv_smsDate);
    }
}
