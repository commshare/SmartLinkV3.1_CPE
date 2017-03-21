package com.alcatel.smartlinkv3.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by zen on 17-3-20.
 */

public class LinkAppSettings {
    // just for test
    public static final String USER_NAME = "admin";

    public static boolean isLoginSwitchOff() {
        String strLoginFile = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/CPE/LoginDisable";
        File f = new File(strLoginFile);
        return f.exists();
    }
}
