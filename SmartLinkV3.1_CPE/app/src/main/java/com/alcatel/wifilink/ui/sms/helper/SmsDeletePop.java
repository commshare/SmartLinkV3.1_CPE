package com.alcatel.wifilink.ui.sms.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.utils.ScreenSize;

/**
 * Created by qianli.ma on 2017/7/12.
 */

public abstract class SmsDeletePop {

    private Context context;
    private PopupWindows pop;

    public abstract void getView(View inflate);

    public SmsDeletePop(Context context) {
        this.context = context;
        show();
    }

    public PopupWindows show() {
        Drawable pop_bg = context.getResources().getDrawable(R.drawable.bg_pop_conner);
        View inflate = View.inflate(context, R.layout.pop_smsdetail_deleted, null);
        int width = (int) (ScreenSize.getSize(context).width * 0.75f);
        int height = (int) (ScreenSize.getSize(context).height * 0.22f);
        getView(inflate);
        pop = new PopupWindows(context, inflate, width, height, true, pop_bg);
        return pop;
    }

    public PopupWindows getPop() {
        return pop;

    }

}
