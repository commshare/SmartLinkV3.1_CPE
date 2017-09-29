package com.alcatel.smartlinkv3.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
     * 打开或关闭WIFI
     *
     * @param context
     * @return
     */
    public static boolean setWifiActive(Context context, boolean open) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.setWifiEnabled(open);
    }
}
