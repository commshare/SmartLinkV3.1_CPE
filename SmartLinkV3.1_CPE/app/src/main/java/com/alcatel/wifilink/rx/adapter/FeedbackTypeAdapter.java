package com.alcatel.wifilink.rx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.rx.bean.FeedbackTypeBean;

import java.util.List;

/**
 * Created by qianli.ma on 2018/2/7 0007.
 */

public class FeedbackTypeAdapter extends RecyclerView.Adapter<FeedbackTypeHolder> {
    private Context context;
    private List<FeedbackTypeBean> ftbs;

    public FeedbackTypeAdapter(Context context, List<FeedbackTypeBean> ftbs) {
        this.context = context;
        this.ftbs = ftbs;
    }

    public void notifys(List<FeedbackTypeBean> ftbs) {
        this.ftbs = ftbs;
        notifyDataSetChanged();
    }

    @Override
    public FeedbackTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedbackTypeHolder(LayoutInflater.from(context).inflate(R.layout.item_feedback_type_rx, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedbackTypeHolder holder, int position) {
        holder.rl_feedback_type.setOnClickListener(v -> {
            selectTypeNext(selectCurrentChoice(ftbs, position));
        });
        holder.tv_feedback_type.setText(ftbs.get(position).getTypeName());
        holder.iv_feedback_type.setVisibility(ftbs.get(position).isSelected() ? View.VISIBLE : View.GONE);
    }

    /**
     * 切换当前选中的项
     *
     * @param ftbs
     * @param position
     * @return
     */
    private List<FeedbackTypeBean> selectCurrentChoice(List<FeedbackTypeBean> ftbs, int position) {
        for (int i = 0; i < ftbs.size(); i++) {
            ftbs.get(i).setSelected(i == position ? true : false);
        }
        return ftbs;
    }

    @Override
    public int getItemCount() {
        return ftbs != null & ftbs.size() > 0 ? ftbs.size() : 0;
    }

    private OnSelectTypeListener onSelectTypeListener;

    // 接口OnSelectTypeListener
    public interface OnSelectTypeListener {
        void selectType(List<FeedbackTypeBean> attr);
    }

    // 对外方式setOnSelectTypeListener
    public void setOnSelectTypeListener(OnSelectTypeListener onSelectTypeListener) {
        this.onSelectTypeListener = onSelectTypeListener;
    }

    // 封装方法selectTypeNext
    private void selectTypeNext(List<FeedbackTypeBean> attr) {
        if (onSelectTypeListener != null) {
            onSelectTypeListener.selectType(attr);
        }
    }
}
