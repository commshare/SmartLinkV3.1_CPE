package com.alcatel.smartlinkv3.utils;

import android.util.Log;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

/**
 * Created by qianli.ma on 2017/9/25.
 */

public class TokenUtils {

    public static String token = "0";
    public static String TOKEN_KEY = "TOKEN_KEY";

    /* 原始的token通过loginResult.getToken获取后进行加密后保存 */
    public static void setToken(String token) {
        Log.v("ma_token", "token-encrypt-before: " + token);
        token = EncryptionUtil.encrypt(token);// 加密
        Log.v("ma_token", "token-encrypt-after: " + token);
        SPUtils.getInstance(SmartLinkV3App.getInstance()).putString(TOKEN_KEY, token);// 保存
    }

    /* 获取的token实际为加密后的token而非token原文 */
    public static String getToken() {
        return SPUtils.getInstance(SmartLinkV3App.getInstance()).getString(TOKEN_KEY, "0");
    }
}
