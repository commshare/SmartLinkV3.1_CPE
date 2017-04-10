package com.alcatel.smartlinkv3.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by zen on 17-4-6.
 */

public class ConnectivityUtils {
    public static final int TYPE_NONE        = -1;

    public static String getServerAddress(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
//		java.net.InetAddress inetAddress;
//		inetAddress.getHostAddress();
        return Formatter.formatIpAddress(dhcpInfo.gateway);
    }

    public static String getGatewayMac(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        return wifiinfo.getBSSID();
    }

    public static boolean checkHaveWifi(Context context) {
//        boolean isHave = false;
//
//        State wifiState = null;
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if (wifiState != null && State.CONNECTED == wifiState) {
//            isHave = true;
//        }
//
//        return isHave;

        int nType = getActiveConnectedType(context);
        if (nType == ConnectivityManager.TYPE_WIFI)
            return true;
        return false;
    }

    public static int getActiveConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo == null)
                return TYPE_NONE;
            NetworkInfo.DetailedState state = mNetworkInfo.getDetailedState();
            if (mNetworkInfo.isConnected() && state == NetworkInfo.DetailedState.CONNECTED) {
                return mNetworkInfo.getType();
            }
        }
        return TYPE_NONE;
    }
}
