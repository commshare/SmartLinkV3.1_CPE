package com.alcatel.wifilink.utils;

import android.text.TextUtils;
import android.widget.EditText;

public class EditUtils {

    /* **** 检测编辑域是否符合规范 **** */
    public static boolean checkEdit(EditText et) {
        String content = et.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        return true;
    }

    /* **** 获取编辑域内容 **** */
    public static String getContent(EditText et) {
        if (checkEdit(et)) {
            return et.getText().toString().replace(" ", "");
        }
        return "";
    }
}
