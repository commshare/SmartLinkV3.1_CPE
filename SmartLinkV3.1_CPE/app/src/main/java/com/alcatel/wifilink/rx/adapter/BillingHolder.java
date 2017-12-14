package com.alcatel.wifilink.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public class BillingHolder extends RecyclerView.ViewHolder {

    public TextView tv_billing;

    public BillingHolder(View itemView) {
        super(itemView);
        tv_billing = (TextView) itemView.findViewById(R.id.tv_pop_item_billing);
    }
}
