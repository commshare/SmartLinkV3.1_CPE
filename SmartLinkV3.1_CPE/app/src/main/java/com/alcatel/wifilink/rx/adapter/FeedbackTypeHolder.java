package com.alcatel.wifilink.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2018/2/7 0007.
 */

public class FeedbackTypeHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rl_feedback_type;
    public TextView tv_feedback_type;
    public ImageView iv_feedback_type;

    public FeedbackTypeHolder(View itemView) {
        super(itemView);
        rl_feedback_type = (RelativeLayout) itemView.findViewById(R.id.rl_item_feedback_type);
        tv_feedback_type = (TextView) itemView.findViewById(R.id.tv_item_feedback_type_title);
        iv_feedback_type = (ImageView) itemView.findViewById(R.id.iv_item_feedback_type_check);
    }
}
