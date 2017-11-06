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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.common.NotificationService;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.rx.bean.PsdRuleBean;
import com.alcatel.smartlinkv3.rx.impl.wlan.WlanResult;
import com.alcatel.smartlinkv3.ui.activity.HandlerUtils;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by qianli.ma on 2017/9/26.
 */

public class OtherUtils {

    public static List<Object> timerList = new ArrayList<>();
    public static List<Activity> contexts = new ArrayList<>();

    /**
     * 隐藏软键盘
     */
    public static void hideKeyBoard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyBoard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInputFromInputMethod(context.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 判断密码规则并返回各AP的状态
     *
     * @param aps
     * @return AP状态集合
     */
    public static List<PsdRuleBean> checkPsdRule(WlanResult.APListBean... aps) {
        List<PsdRuleBean> psdRules = new ArrayList<>();
        for (WlanResult.APListBean ap : aps) {
            PsdRuleBean psdRuleBean = new PsdRuleBean();
            if (ap != null) {
                int wlanAPID = ap.getWlanAPID();// 2.4G|5G
                psdRuleBean.setWlanID(wlanAPID);
                int securityMode = ap.getSecurityMode();// WEP | WPA
                psdRuleBean.setSecurityMode(securityMode);
                if (securityMode == Conn.WEP) {
                    psdRuleBean.setMatchWep(WepPsdHelper.psdMatch(ap.getWepKey()));
                } else {
                    psdRuleBean.setMatchWpa(ap.getWpaKey().length() >= 8 & ap.getWpaKey().length() <= 63);
                }
            }
            psdRules.add(psdRuleBean);
        }
        return psdRules;
    }

    /**
     * 获取wlan模式类型(WEP WPA WPA2 WPA/WPA2)
     *
     * @param context
     * @return
     */
    public static List<String> getSecurityArrWithoutDisable(Context context) {
        List<String> securityList = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.wlan_settings_security);
        Collections.addAll(securityList, stringArray);
        return securityList;
    }

    /**
     * 获取wlan模式类型(WEP WPA WPA2 WPA/WPA2)
     *
     * @param context
     * @return
     */
    public static List<String> getSecurityArr(Context context) {
        List<String> securityList = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.wlan_settings_security);
        Collections.addAll(securityList, stringArray);
        securityList.add(0, "disable");// 由于配置数组中不包含disable,为方便代码操作,故添加一个disable元素,但不使用
        return securityList;
    }

    /**
     * 获取WEP模式下的可选数组(open share)
     *
     * @param context
     * @return
     */
    public static List<String> getWepArr(Context context) {
        List<String> wepList = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.setting_wep_mode_array);
        Collections.addAll(wepList, stringArray);
        return wepList;
    }

    /**
     * 获取WPA模式下的可选数组(TKIP AES AUTO)
     *
     * @param context
     * @return
     */
    public static List<String> getWpaArr(Context context) {
        String tkip = context.getString(R.string.setting_wifi_tkip);
        String aes = context.getString(R.string.setting_wifi_aes);
        String auto = context.getString(R.string.setting_network_mode_auto);
        List<String> wpaArr = new ArrayList<String>();
        wpaArr.add(tkip);
        wpaArr.add(aes);
        wpaArr.add(auto);
        return wpaArr;
    }

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
