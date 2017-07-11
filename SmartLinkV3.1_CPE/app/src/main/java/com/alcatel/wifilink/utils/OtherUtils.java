package com.alcatel.wifilink.utils;

import android.widget.EditText;

/**
 * Created by qianli.ma on 2017/7/10.
 */

public class OtherUtils {

    /**
     * 线程自关
     */
    public static void kill() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 把光标置后
     *
     * @param et
     */
    public static void setLastSelection(EditText et) {
        String content = et.getText().toString();
        et.setSelection(content.length());//将光标移至文字末尾
    }

}
