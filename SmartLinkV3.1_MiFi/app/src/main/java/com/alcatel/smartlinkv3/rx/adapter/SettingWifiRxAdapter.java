package com.alcatel.smartlinkv3.rx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.smartlinkv3.R;

import java.util.List;

/**
 * Created by qianli.ma on 2017/11/2 0002.
 */

public abstract class SettingWifiRxAdapter extends RecyclerView.Adapter<SettingWifiRxHolder> {

    private Context context;
    private List<String> contents;
    private int checkPosition;
    private int checkColor;// 选中色
    private int uncheckColor;// 未选中色

    public abstract void getCheckPositon(int hadCheckPosition);

    /**
     * @param context
     * @param contents      数组内容
     * @param checkPosition 选中的元素角标
     */
    public SettingWifiRxAdapter(Context context, List<String> contents, int checkPosition) {
        this.context = context;
        this.contents = contents;
        this.checkPosition = checkPosition;
        checkColor = context.getResources().getColor(R.color.main_title_background);
        uncheckColor = context.getResources().getColor(R.color.gray11);
    }

    /**
     * 手動更新
     *
     * @param contents
     * @param checkPosition
     */
    public void notifys(List<String> contents, int checkPosition) {
        this.contents = contents;
        this.checkPosition = checkPosition;
        notifyDataSetChanged();
    }

    @Override
    public SettingWifiRxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingWifiRxHolder(LayoutInflater.from(context).inflate(R.layout.item_pop_settingwifirx, parent, false));
    }

    @Override
    public void onBindViewHolder(SettingWifiRxHolder holder, int position) {
        holder.tvSettingwifiRxItem.setText(contents.get(position));
        holder.tvSettingwifiRxItem.setTextColor(checkPosition == position ? checkColor : uncheckColor);
        holder.tvSettingwifiRxItem.setOnClickListener(v -> getCheckPositon(position));
    }

    @Override
    public int getItemCount() {
        return contents != null & contents.size() > 0 ? contents.size() : 0;
    }
}
