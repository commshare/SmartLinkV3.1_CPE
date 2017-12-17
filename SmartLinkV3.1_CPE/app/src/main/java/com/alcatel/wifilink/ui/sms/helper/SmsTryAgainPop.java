package com.alcatel.wifilink.ui.sms.helper;

import android.content.Context;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.utils.ScreenSize;

/**
 * Created by qianli.ma on 2017/7/12.
 */
@Deprecated
public abstract class SmsTryAgainPop {

    private Context context;
    private PopupWindows pop;

    public SmsTryAgainPop(Context context) {
        this.context = context;
    }

    public abstract void getView(View inflate);

    public PopupWindows show() {
        View inflate = View.inflate(context, R.layout.pop_smsdetail_tryagain, null);
        int width = (int) (ScreenSize.getSize(context).width * 0.75f);
        int height = (int) (ScreenSize.getSize(context).height * 0.20f);
        getView(inflate);
        pop = new PopupWindows(context, inflate, width, height, true);
        return pop;
    }

    public void dismiss(PopupWindows pop) {
        if (pop != null) {
            pop.dismiss();
        }
    }

    public PopupWindows getPop() {
        return pop;
    }
}
