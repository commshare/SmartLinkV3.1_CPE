package com.alcatel.wifilink.rx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.home.helper.rcv.SmsHolder;

import java.util.List;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public abstract class BillingAdaper extends RecyclerView.Adapter<BillingHolder> {

    private Context context;
    private List<String> days;

    public BillingAdaper(Context context, List<String> days) {
        this.context = context;
        this.days = days;
    }

    @Override
    public BillingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BillingHolder(LayoutInflater.from(context).inflate(R.layout.item_setplan_rx_pop_billing, parent, false));
    }

    @Override
    public void onBindViewHolder(BillingHolder holder, int position) {
        holder.tv_billing.setText(days.get(position));
        holder.tv_billing.setOnClickListener(v -> {
            clickDay(position + 1);
        });
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    public abstract void clickDay(int day);
}
