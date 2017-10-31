package com.alcatel.smartlinkv3.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.NotificationService;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.ui.activity.HandlerUtils;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianli.ma on 2017/9/26.
 */

public class OtherUtils {

    public static List<Object> timerList = new ArrayList<>();
    public static List<Activity> contexts = new ArrayList<>();

    /**
     * 传入字符查找集合中对应的角标
     *
     * @param contents
     * @param value
     * @return
     */
    public static int getIndexByString(List<String> contents, String value) {
        int position = -1;
        for (int i = 0; i < contents.size(); i++) {
            if (value.equalsIgnoreCase(contents.get(i))) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 初始化接口
     */
    public static void initBusiness() {
        // 将在登陆后启动
        BusinessMannager.getInstance();
        NotificationService.startService();
        HandlerUtils.replaceHandler();
        AllShareProxy.getInstance(SmartLinkV3App.getInstance());
    }

    /**
     * 清除所有的activity
     */
    public static void clearAllContext() {
        for (Activity context : contexts) {
            if (context != null & !context.isFinishing()) {
                context.finish();
            }
        }
        contexts.clear();
    }

    /**
     * 清理所有的定时器
     */
    public static void clearAllTimer() {
        BusinessMannager.getInstance().stopAllRoll();
    }

    /**
     * 显示等待进度条
     *
     * @param context
     */
    public static ProgressDialog showProgressPop(Context context) {
        ProgressDialog pgd = new ProgressDialog(context);
        pgd.setMessage(context.getString(R.string.home_connecting_to));
        pgd.setCanceledOnTouchOutside(false);
        pgd.show();
        return pgd;
    }

    /**
     * 隐藏等待进度条
     *
     * @param pd
     */
    public static void hideProgressPop(ProgressDialog pd) {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    /**
     * 获取编辑域内容(去除所有空格)
     *
     * @param editText
     * @return
     */
    public static String getEdittext(EditText editText) {
        return editText.getText().toString().trim().replace(" ", "");
    }

    /**
     * WIFI是否有连接
     *
     * @param context
     * @return
     */
    public static boolean isWiFiActive(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (wifi.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查WIFI是否有链接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo.State wifiState = networkInfo.getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }

    /**
     * 打开或关闭WIFI
     *
     * @param context
     * @return
     */
    public static boolean setWifiActive(Context context, boolean open) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.setWifiEnabled(open);
    }

    /**
     * 申請權限
     */
    public static void verifyPermisson(Activity activity) {
        String[] PERMISSIONS_WIFI = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, Manifest.permission.CHANGE_WIFI_STATE};
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CHANGE_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_WIFI, 1);
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param sClass   服务的类名
     * @return true:代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, Class sClass) {
        String serviceName = sClass.getName();// 获取服务的全类名
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);// 获取系统服务对象
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(1000);// 获取正在运行中的服务集合 -->1000为可能获取到的个数
        if (myList.size() <= 0) {// 判断集合是否有对象
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {// 遍历每一个服务对象
            String mName = myList.get(i).service.getClassName().toString();// 获取服务的类名
            if (mName.equalsIgnoreCase(serviceName) || mName.contains(serviceName)) {
                return true;
            }
        }
        return false;
    }

}
