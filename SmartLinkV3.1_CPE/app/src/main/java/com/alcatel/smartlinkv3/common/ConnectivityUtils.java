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
    public static final int TYPE_NONE = -1;

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


    /*
     * Wep key
        涓嶈兘涓虹┖
        濡傛灉鏄痥ey闀垮害鏄�鎴栨槸13锛屽垯闇�婊¤冻浠ヤ笅鏉′欢锛�
        1.	ASCII鐮佹暟瀛楀ぇ浜�2骞朵笖灏忎簬127浣嗕笉鍖呭惈34銆�8銆�8銆�9銆�2瀵瑰簲鐨勫瓧绗�

        濡傛灉wep key闀垮害涓�0鎴栨槸26锛宬ey鍙兘鍖呭惈0鍒�鍜屽ぇ灏忓啓26涓嫳鏂囧瓧姣�

        WPA
        WPA2
        WPA/WPA2
        闇�鍚屾椂婊¤冻3涓潯浠讹細
        1.	涓嶈兘涓虹┖
        2.	Key.length>7 && Key.length<64
        3.	Key鍙寘鍚獳SCII鐮佹暟瀛楀ぇ浜�2骞朵笖灏忎簬127浣嗕笉鍖呭惈34銆�8銆�8銆�9銆�2瀵瑰簲鐨勫瓧绗�
     */
    public static boolean checkPassword(String strPsw, ENUM.SecurityMode mode) {
        int nLength = strPsw.length();
        boolean bCorrect = true;
        if (mode == ENUM.SecurityMode.WEP) {
            if (nLength == 5 || nLength == 13) {
                for (int i = 0; i < nLength; i++) {
                    char c = strPsw.charAt(i);
                    if (!(c > 32 && c < 127 && c != 34 && c != 38 && c != 58 && c != 59 && c != 92)) {
                        bCorrect = false;
                        break;
                    }
                }
            } else if (nLength == 10 || nLength == 26) {
                for (int i = 0; i < nLength; i++) {
                    char c = strPsw.charAt(i);
                    if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')) {
                        bCorrect = false;
                        break;
                    }
                }
            } else {
                bCorrect = false;
            }
        } else {
            if (!(nLength > 7 && nLength < 64)) {
                bCorrect = false;
            } else {
                for (int i = 0; i < nLength; i++) {
                    char c = strPsw.charAt(i);
                    if (!(c > 32 && c < 127 && c != 34 && c != 38 && c != 58 && c != 59 && c != 92)) {
                        bCorrect = false;
                        break;
                    }
                }
            }
        }

        return bCorrect;
    }

    public static boolean checkSsid(String ssid) {
        if (ssid.length() > 32)
            return false;
        return ssid.matches("[a-zA-Z0-9-_. ]+");
    }
}
