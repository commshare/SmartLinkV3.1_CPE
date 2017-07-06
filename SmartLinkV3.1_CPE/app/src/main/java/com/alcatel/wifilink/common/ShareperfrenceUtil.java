package com.alcatel.wifilink.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

public class ShareperfrenceUtil {

    /**
     * 提交共享
     *
     * @param context
     * @param fileName
     * @param tagName
     * @param content
     * @return
     */
    public static boolean setSp(Context context, String fileName, String tagName, Object content) {
        SharedPreferences sp = context.getSharedPreferences(fileName, 0);// 私有共享对象
        String type = content.getClass().getName();// 获取传入对象类型
        Editor edit = sp.edit();
        // 判断类型
        if (type.equals("java.lang.Boolean")) {
            boolean value = Boolean.valueOf(String.valueOf(content));
            sp.edit().putBoolean(tagName, value).commit();
            return true;
        } else if (type.equals("java.lang.Float")) {
            Float value = Float.valueOf(String.valueOf(content));
            sp.edit().putFloat(tagName, value).commit();
            return true;
        } else if (type.equals("java.lang.Integer")) {
            int value = Integer.valueOf(String.valueOf(content));
            sp.edit().putInt(tagName, value).commit();
            return true;
        } else if (type.equals("java.lang.Long")) {
            long value = Long.valueOf(String.valueOf(content));
            sp.edit().putLong(tagName, value).commit();
            return true;
        } else if (type.equals("java.lang.String")) {
            String value = String.valueOf(content);
            sp.edit().putString(tagName, value).commit();
            return true;
        } else {
            throw new RuntimeException("转换异常");
        }
    }

    /**
     * 获取共享
     *
     * @param context  上下文
     * @param fileName 配置清单名称
     * @param tagName  标签名称
     * @return 对应的值
     */
    public static String getSp(Context context, String fileName, String tagName) {

        // 非空判断
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(tagName) || context == null) {
            return null;
        }

        // =====================  获取	START  ====================== //
        SharedPreferences sp = context.getSharedPreferences(fileName, 0);
        String value = "";
        Map<String, ?> all = sp.getAll();
        Set<String> keySet = all.keySet();
        // 遍历所有键值对
        for (String key : keySet) {
            if (key.equals(tagName)) {// 匹配外界参数
                value = String.valueOf(all.get(key));
                break;
            }
        }

        // =====================  获取	END  ====================== //

        if (TextUtils.isEmpty(value)) {// 是否为空
            return null;
        }

        return value;

    }

}
