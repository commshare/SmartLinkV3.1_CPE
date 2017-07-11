package com.alcatel.wifilink.ui.home.helper.pop;

import android.content.Context;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.utils.ScreenSize;

public abstract class SimPopHelper {

    public abstract void getView(View popupwindow);

    public PopupWindows showPop(Context context) {
        ScreenSize.SizeBean size = ScreenSize.getSize(context);
        int width = (int) (size.width * 0.75f);
        int height = (int) (size.height * 0.16f);
        View pop = View.inflate(context, R.layout.pop_sim_unlock, null);
        getView(pop);
        return new PopupWindows(context, pop, width, height, false);
    }

}
