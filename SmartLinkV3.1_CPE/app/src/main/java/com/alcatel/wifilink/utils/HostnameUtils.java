package com.alcatel.wifilink.utils;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by qianli.ma on 2017/8/5.
 */

public class HostnameUtils {

    private static HostNameImpl hostName;// hostname接口

    /* 设置SSL安全性验证 */
    public static void setVerifyHostName() {
        HttpsURLConnection.setDefaultHostnameVerifier(getVerify());
    }

    /* 获取SSL验证签名 */
    public static HostnameVerifier getVerify() {
        if (hostName == null) {
            hostName = new HostNameImpl();
        }
        return hostName;
    }

    /* 验证SSL类 */
    private static class HostNameImpl implements HostnameVerifier {
        
        @Override
        public boolean verify(String hostname, SSLSession session) {
            String ip = "192.168.1.1";
            String http_ip = "http://" + ip;
            if (hostname.equalsIgnoreCase(http_ip) || hostname.equalsIgnoreCase(ip) || hostname.contains(ip)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
