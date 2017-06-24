package com.alcatel.smartlinkv3.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qianli.ma on 2017/6/24.
 */

public class DataUtils {

    public static String getCurrent() {
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sDate.format(now);
    }

}
