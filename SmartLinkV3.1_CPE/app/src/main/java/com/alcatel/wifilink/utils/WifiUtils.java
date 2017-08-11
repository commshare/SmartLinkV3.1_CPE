package com.alcatel.wifilink.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by qianli.ma on 2017/8/2.
 */

public class WifiUtils {

    /**
     * 获取设备的WIFI ip
     *
     * @param wifiMan
     * @return
     */
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

    /**
     * 检查WIFI是否有链接
     *
     * @param context
     * @return
     */
    public static boolean checkWifiConnect(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo.State wifiState = networkInfo.getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }

    /**
     * 获取wifi网关
     *
     * @param context
     * @return
     */
    public static String getWifiGateWay(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        String gateWay = transferNetMask(dhcp.gateway);
        Log.d("ma_gate", "gate: " + gateWay);
        return gateWay;
    }

    /**
     * 转换16进制的WIFI网关
     *
     * @param gateWayHex 16进制的网关
     * @return
     */
    private static String transferNetMask(long gateWayHex) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (gateWayHex & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((gateWayHex >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((gateWayHex >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((gateWayHex >> 24) & 0xff)));
        return sb.toString();
    }

}
