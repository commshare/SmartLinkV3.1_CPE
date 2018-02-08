package com.alcatel.wifilink.rx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2018/2/7 0007.
 */

public class FeedbackPhotoHolder extends RecyclerView.ViewHolder {


    public RelativeLayout rl_photo;// 布局
    public ImageView iv_photo;// 显示图片
    public ImageView iv_photo_del;// 删除图标

    public FeedbackPhotoHolder(View itemView) {
        super(itemView);
        rl_photo = (RelativeLayout) itemView.findViewById(R.id.rl_item_feedback_photo);
        iv_photo = (ImageView) itemView.findViewById(R.id.iv_item_feedback_photo);
        iv_photo_del = (ImageView) itemView.findViewById(R.id.iv_item_feedback_photo_del);
    }
}
