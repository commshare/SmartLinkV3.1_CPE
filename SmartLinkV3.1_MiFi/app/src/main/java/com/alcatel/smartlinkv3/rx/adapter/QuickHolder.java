package com.alcatel.smartlinkv3.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

/**
 * Created by qianli.ma on 2017/10/27 0027.
 */

public class QuickHolder extends RecyclerView.ViewHolder {

    public TextView tvItemQuickSecurityMode;

    public QuickHolder(View itemView) {
        super(itemView);
        tvItemQuickSecurityMode = (TextView) itemView.findViewById(R.id.tv_item_quick);
    }
}
