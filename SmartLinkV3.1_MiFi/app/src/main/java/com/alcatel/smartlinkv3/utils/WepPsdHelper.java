package com.alcatel.smartlinkv3.utils;

import android.text.TextUtils;

/**
 * Created by qianli.ma on 2017/8/30.
 */

public class WepPsdHelper {

    public static boolean psdMatch(String key) {
        int len = key.length();
        switch (len) {
            case 5:
            case 13:
                if (!isASCIIData(key)) {
                    return false;
                }
                break;
            case 10:
            case 26:
                if (!isHexaData(key)) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private static boolean isASCIIData(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        int i = 0;
        char char_i;
        int num_char_i;
        for (i = 0; i < str.length(); i++) {
            char_i = str.charAt(i);
            num_char_i = (int) char_i;
            if (num_char_i > 255) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHexaData(String data) {
        int len = data.length();
        int i = 0;
        for (i = 0; i < len; i++) {
            if (!isHexaDigit(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHexaDigit(char c) {
        char[] hexVals = new char[]{//
                '0', '1', '2', '3', '4',// 
                '5', '6', '7', '8', '9',//
                'A', 'B', 'C', 'D', 'E',//
                'F', 'a', 'b', 'c', 'd', 'e', 'f'};//
        int len = hexVals.length;
        int i = 0;
        boolean ret = false;
        for (i = 0; i < len; i++) {
            if (c == hexVals[i]) {
                break;
            }
        }
        if (i < len) {
            ret = true;
        }
        return ret;
    }
}
