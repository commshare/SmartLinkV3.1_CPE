package com.alcatel.wifilink.common;

import android.os.Environment;

import java.io.File;

public class LinkAppSettings {
    // TOIN 2017/6/7 测试帐号
    public static final String USER_NAME = "admin";
    // 登陆是否关闭
    public static boolean isLoginSwitchOff() {
        String strLoginFile = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/CPE/LoginDisable";
        File f = new File(strLoginFile);
        return f.exists();
    }
}
