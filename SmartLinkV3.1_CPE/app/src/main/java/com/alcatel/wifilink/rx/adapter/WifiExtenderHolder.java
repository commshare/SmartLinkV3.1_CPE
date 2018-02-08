package com.alcatel.wifilink.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2018/2/6 0006.
 */

public class WifiExtenderHolder extends RecyclerView.ViewHolder {

    private TextView tv_wifiEx_title;
    private ImageView iv_wifiEx_strong;
    private ImageView iv_wifiEx_lock;

    public WifiExtenderHolder(View itemView) {
        super(itemView);
        tv_wifiEx_title = (TextView) itemView.findViewById(R.id.tv_wifiExtender_had_connected_hotDot_name);
        iv_wifiEx_strong = (ImageView) itemView.findViewById(R.id.iv_wifiExtender_had_connected_wifi);
        iv_wifiEx_lock = (ImageView) itemView.findViewById(R.id.iv_wifiExtender_had_connected_lock);
    }
}
