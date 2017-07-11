package com.alcatel.wifilink.ui.home.helper.rcv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2017/7/11.
 */

public class SmsRcvAdapter extends RecyclerView.Adapter<SmsHolder> {

    private Context context;

    public SmsRcvAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SmsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SmsHolder(LayoutInflater.from(context).inflate(R.layout.item_sms_update, parent, false));
    }

    @Override
    public void onBindViewHolder(SmsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
