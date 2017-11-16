package com.alcatel.wifilink.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences存储数据方式工具�?
 *
 * @author Tank
 */
public class SP {
    public static String SP_NAME = "As_SmartLink";
    private static int MODE = Context.MODE_PRIVATE;
    private static Context context;
    private static SP sharedPrefsUtil;

    public static SP getInstance(Context context) {
        if (sharedPrefsUtil == null) {
            sharedPrefsUtil = new SP(context);
        }
        return sharedPrefsUtil;
    }

    public SP(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    /**
     * @return sharedPrefsName
     */

    public static String getSP_NAME() {
        return SP_NAME;
    }

    /**
     * @param sharedPrefsName 要设置的 sharedPrefsName
     */

    public SP setSP_NAME(String sharedPrefsName) {
        this.SP_NAME = sharedPrefsName;
        return this;
    }

    public void putInt(String key, int value) {
        Editor sp = context.getSharedPreferences(SP_NAME, MODE).edit();
        sp.putInt(key, value);
        sp.apply();
    }

    public void putFloat(String key, float value) {
        Editor sp = context.getSharedPreferences(SP_NAME, MODE).edit();
        sp.putFloat(key, value);
        sp.apply();
    }

    public void putLong(String key, long value) {
        Editor sp = context.getSharedPreferences(SP_NAME, MODE).edit();
        sp.putLong(key, value);
        sp.apply();
    }

    public void putBoolean(String key, boolean value) {
        Editor sp = context.getSharedPreferences(SP_NAME, MODE).edit();
        sp.putBoolean(key, value);
        sp.apply();
    }

    public void putString(String key, String value) {
        Editor sp = context.getSharedPreferences(SP_NAME, MODE).edit();
        sp.putString(key, value);
        sp.apply();
    }

    public int getInt(String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.getFloat(key, defValue);
    }

    public long getLong(String key, long defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.getLong(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.getString(key, defValue);
    }

    public boolean isExist(String key) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE);
        return sp.contains(key);
    }
}

