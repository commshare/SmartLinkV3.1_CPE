package com.alcatel.wifilink.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by qianli.ma on 2017/6/24.
 */

public class DataUtils {

    public static String getCurrent() {
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return sDate.format(now);
    }

    public static String format1(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date);
    }

}
