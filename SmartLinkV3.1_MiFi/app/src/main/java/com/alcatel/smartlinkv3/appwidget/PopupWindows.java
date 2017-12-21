package com.alcatel.smartlinkv3.appwidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.alcatel.smartlinkv3.R;

/**
 * @作者 qianli
 * @开发时间 下午5:25:35
 * @功能描述 气泡
 * @SVN更新者 $Author$
 * @SVN更新时间 $Date$
 * @SVN当前版本 $Rev$
 */
public class PopupWindows extends PopupWindow {

    private Context context;

    public PopupWindow getPopupWindow() {
        return this;
    }
    
    /**
     * 普通弹窗(全部自定义)
     *
     * @param context
     * @param contentView 要显示的VIEW
     * @param width       弹窗宽度
     * @param height      弹窗高度
     * @param isDismiss   点击空白是否消失
     */
    public PopupWindows(Context context, View contentView, int width, int height, boolean isDismiss,Drawable drawable) {
        super(contentView, width, height, true);
        this.context = context;
        setFocusable(isDismiss);
        setBackgroundDrawable(drawable);
        setAnimationStyle(R.style.popwin_anim_style);
        showAtLocation(contentView, Gravity.CENTER, 0, 0);
        contentView.setOnClickListener(v -> {
            if (isDismiss) {
                dismiss();
            }
        });
        showGray(context);// 背景变暗
    }
    

    /**
     * 不可设置底版颜色的弹窗(点击空白不消失)
     *
     * @param context
     * @param contentView 要显示的VIEW
     * @param width       弹窗宽度
     * @param height      弹窗高度
     * @param isDismiss   点击空白是否消失
     */
    public PopupWindows(Context context, View contentView, int width, int height, boolean isDismiss) {
        super(contentView, width, height, true);
        this.context = context;
        setFocusable(isDismiss);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setAnimationStyle(R.style.popwin_anim_style);
        showAtLocation(contentView, Gravity.CENTER, 0, 0);
        contentView.setOnClickListener(v -> {
            if (isDismiss) {
                dismiss();
            }
        });
        showGray(context);// 背景变暗
    }

    /**
     * 不可设置底版颜色的弹窗
     *
     * @param context
     * @param contentView 要显示的VIEW
     * @param width       弹窗宽度
     * @param height      弹窗高度
     */
    public PopupWindows(Context context, View contentView, int width, int height) {
        super(contentView, width, height, true);
        this.context = context;
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setAnimationStyle(R.style.popwin_anim_style);
        showAtLocation(contentView, Gravity.CENTER, 0, 0);
        contentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        showGray(context);// 背景变暗
    }

    /**
     * 可以设置底版颜色的弹窗(drawable)
     *
     * @param context
     * @param contentView 要显示的VIEW
     * @param width       弹窗宽度
     * @param height      弹窗高度
     * @param drawable    需要指定的底版背景
     */
    public PopupWindows(Context context, View contentView, int width, int height, Drawable drawable) {
        super(contentView, width, height, true);
        this.context = context;
        // 设置点击空白出隐藏
        setFocusable(true);
        // 指定底版
        setBackgroundDrawable(drawable);
        // 指定动画(需要自己写动画清单)
        setAnimationStyle(R.style.popwin_anim_style);
        // 指定显示位置(本案例: 以屏幕重点为基点, 偏移X:0, 偏移Y:0)
        showAtLocation(contentView, Gravity.CENTER, 0, 0);
        // 设置布局点击监听(本案例: 消隐)
        contentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        showGray(context);// 背景变暗
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 消失时背景恢复亮度
        showWhite(context);
    }

    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * 背景变暗
     *
     * @param context
     */
    public void showGray(Context context) {
        // 从窗体属性中获取当前在屏幕中正在显示的窗体--> 此时为popupwindow
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        // 设置透明度为 50%--> 达到变暗效果
        lp.alpha = 0.5f;
        // 重新配置属性
        ((Activity) context).getWindow().setAttributes(lp);
    }

    /**
     * 背景恢复亮度
     *
     * @param context
     */
    public void showWhite(Context context) {
        // 从窗体属性中获取当前在屏幕中正在显示的窗体--> 此时为popupwindow
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        // 设置透明度为 100%--> 达到变亮效果
        lp.alpha = 1f;
        // 重新配置属性
        ((Activity) context).getWindow().setAttributes(lp);
    }

}
