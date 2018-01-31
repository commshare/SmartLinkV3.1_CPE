package com.alcatel.wifilink.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by qianli.ma on 2017/6/7.
 */

public class ScreenUtils {

    public static Sizez getSize(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new ScreenUtils().new Sizez(width, height);
    }

    public class Sizez {
        public int width;
        public int height;

        public Sizez(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }


}
