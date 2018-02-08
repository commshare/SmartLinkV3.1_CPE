package com.alcatel.wifilink.rx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

import java.util.List;

/**
 * Created by qianli.ma on 2018/2/7 0007.
 */

public class FeedbackPhotoAdapter extends RecyclerView.Adapter<FeedbackPhotoHolder> {

    private Context context;
    private List<Bitmap> bitmaps;

    public FeedbackPhotoAdapter(Context context, List<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public FeedbackPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedbackPhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_feedback_photo_rx, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedbackPhotoHolder holder, int position) {
        holder.iv_photo.setImageBitmap(bitmaps.get(position));
        holder.rl_photo.setOnClickListener(v -> {
            // TODO: 2018/2/7 0007 打开图片
            toast("open pic");
        });
        holder.iv_photo.setOnClickListener(v -> {
            // TODO: 2018/2/7 0007 打开图片 
            toast("open pic");
        });
        holder.iv_photo_del.setOnClickListener(v -> {
            // TODO: 2018/2/7 0007  删除图片
            toast("del pic");
        });
    }

    @Override
    public int getItemCount() {
        return bitmaps != null | bitmaps.size() > 0 ? bitmaps.size() : 0;
    }

    public void toast(int resId) {
        ToastUtil_m.show(context, resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(context, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(context, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(context, ac, false, isFinish, false, 0);
    }
}
