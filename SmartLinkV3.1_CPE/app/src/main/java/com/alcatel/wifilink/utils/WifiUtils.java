package com.alcatel.wifilink.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Field;

/**
 * Created by qianli.ma on 2017/8/2.
 */

public class WifiUtils {

    public static String getWifiIp(WifiManager wifiMan) {
        WifiInfo info = wifiMan.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ipString = "";// 本机在WIFI状态下路由分配给的IP地址  

        // 获得IP地址的方法一：  
        if (ipAddress != 0) {
            ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        }
        /* 如果获取不到再用反射的方法获取一次 */
        if (TextUtils.isEmpty(ipString)) {
            // 获得IP地址的方法二（反射的方法）：  
            try {
                Field field = info.getClass().getDeclaredField("mIpAddress");
                field.setAccessible(true);
                ipString = (String) field.get(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ipString;
    }

}
