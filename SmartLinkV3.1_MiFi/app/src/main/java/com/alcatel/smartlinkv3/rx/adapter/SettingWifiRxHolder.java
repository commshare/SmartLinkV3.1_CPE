package com.alcatel.smartlinkv3.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

/**
 * Created by qianli.ma on 2017/11/2 0002.
 */

    public class SettingWifiRxHolder extends RecyclerView.ViewHolder {
    public TextView tvSettingwifiRxItem;

    public SettingWifiRxHolder(View itemView) {
        super(itemView);
        tvSettingwifiRxItem = (TextView) itemView.findViewById(R.id.tv_pop_settingwifirx_item);
    }
}
