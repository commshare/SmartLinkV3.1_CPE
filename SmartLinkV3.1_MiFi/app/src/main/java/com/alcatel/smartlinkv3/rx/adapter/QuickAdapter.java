package com.alcatel.smartlinkv3.rx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.smartlinkv3.R;

import java.util.List;

/**
 * Created by qianli.ma on 2017/10/27 0027.
 */

public abstract class QuickAdapter extends RecyclerView.Adapter<QuickHolder> {

    private Context context;
    private List<String> modes;

    public QuickAdapter(Context context, List<String> modes) {
        this.context = context;
        this.modes = modes;
    }

    public abstract void onQuickItemClick(String mode, int position);

    @Override
    public QuickHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuickHolder(LayoutInflater.from(context).inflate(R.layout.item_quick_securitymode, parent, false));
    }

    @Override
    public void onBindViewHolder(QuickHolder holder, int position) {
        holder.tvItemQuickSecurityMode.setText(modes.get(position));
        holder.tvItemQuickSecurityMode.setOnClickListener(v -> {
            onQuickItemClick(modes.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return modes != null ? modes.size() : 0;
    }
}
