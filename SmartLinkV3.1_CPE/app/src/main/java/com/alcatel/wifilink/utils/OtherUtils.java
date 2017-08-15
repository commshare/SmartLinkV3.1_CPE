package com.alcatel.wifilink.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qianli.ma on 2017/7/10.
 */

public class OtherUtils {

    private OnSwVersionListener onSwVersionListener;
    private OnHwVersionListener onHwVersionListener;
    private OnCustomizedVersionListener onCustomizedVersionListener;

    /**
     * 线程自关
     */
    public static void kill() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 把光标置后
     *
     * @param et
     */
    public static void setLastSelection(EditText et) {
        String content = et.getText().toString();
        et.setSelection(content.length());//将光标移至文字末尾
    }


    /**
     * 拼接电话号码
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static String stitchPhone(Context context, List<String> phoneNumber) {
        if (phoneNumber.size() == 0) {
            return context.getString(R.string.error_info);
        } else if (phoneNumber.size() == 1) {
            return phoneNumber.get(0);
        } else {
            StringBuffer sb = new StringBuffer();
            for (String s : phoneNumber) {
                sb.append(s).append(";");
            }
            return sb.toString();
        }
    }

    /**
     * 转换日期
     *
     * @param oriDate
     * @return
     */
    public static String transferDate(String oriDate) {
        Date summaryDate = DataUti.formatDateFromString(oriDate);// sms date
        String strTimeText = new String();
        if (summaryDate != null) {
            Date now = new Date();// now date
            if (now.getYear() == summaryDate.getYear() && now.getMonth() == summaryDate.getMonth() && now.getDate() == summaryDate.getDate()) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                strTimeText = format.format(summaryDate);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                strTimeText = format.format(summaryDate);
            }
        }
        return strTimeText;
    }

    /**
     * 是否为定制的版本
     */
    public void isCustomVersion() {
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                String customId = result.getSwVersion().split("_")[1];
                if (onCustomizedVersionListener != null) {
                    onCustomizedVersionListener.getCustomizedStatus(customId.equalsIgnoreCase(Cons.CUSTOM_ID_1));
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                if (onCustomizedVersionListener != null) {
                    onCustomizedVersionListener.getCustomizedStatus(false);
                }
            }
        });
    }

    /**
     * 获取设备的软件版本号
     */
    public void getDeviceSwVersion() {

        // 1.需要加密的定制版本
        String needEncryptVersionCustomId = Cons.CUSTOM_ID_1;
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                // 字段值为1: 一定需要加密
                if (result.getPwEncrypt() == Cons.NEED_ENCRYPT) {
                    onSwVersionListener.getVersion(true);
                } else {
                    // 否则--> 获取系统信息:能使用systeminfo接口--> 判断是否为E1版本
                    // 否则--> 获取系统信息:不能使用systeminfo接口--> 一定需要加密
                    getSystemInfoImpl();
                }

            }

            /* 访问systeminfo接口 */
            private void getSystemInfoImpl() {
                API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
                    @Override
                    protected void onSuccess(SystemInfo result) {
                        // 2.获取当前版本
                        String currentVersion = result.getSwVersion();
                        String customId = currentVersion.split("_")[1];// customId:E1、IA、01....
                        if (onSwVersionListener != null) {
                            // 如能获取到版本,则判断是否为[E1]定制版本
                            if (customId.equalsIgnoreCase(needEncryptVersionCustomId) || customId.contains(needEncryptVersionCustomId)) {
                                onSwVersionListener.getVersion(true);
                            } else {
                                onSwVersionListener.getVersion(false);
                            }
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        // 如获取不到则一定是需要加密的
                        if (onSwVersionListener != null) {
                            onSwVersionListener.getVersion(true);
                        }
                    }
                });
            }
        });
    }


    /**
     * 获取硬件版本: HH70 OR HH40
     */
    public void getDeviceHWVersion() {
        // 1.需要加密的版本
        List<String> needEncryptVersions = new ArrayList<String>();
        needEncryptVersions.add("HH70");
        // 2.获取当前版本
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                if (onHwVersionListener != null) {
                    onHwVersionListener.getVersion(result.getSwVersion());
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }


    /**
     * 获取引导页的view
     *
     * @param context
     * @return
     */
    public static List<View> getGuidePages(Context context) {
        List<View> pages = new ArrayList<View>();
        View page1 = View.inflate(context, R.layout.what_new_one, null);
        View page2 = View.inflate(context, R.layout.what_new_two, null);
        View page3 = View.inflate(context, R.layout.what_new_three, null);
        pages.add(page1);
        pages.add(page2);
        pages.add(page3);
        return pages;
    }

    /**
     * 检查WIFI是否有链接
     *
     * @param context
     * @return
     */
    public static boolean checkConnect(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo.State wifiState = networkInfo.getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }

    public static boolean isWiFiActive(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (wifi.isWifiEnabled() && ipAddress != 0) {
            Log.d("ma_wifi", "Wifi Connect");
            return true;
        } else {
            Log.d("ma_wifi", "Wifi not Connect");
            return false;
        }
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

    /**
     * 停止全局定时器
     */
    public static void stopAutoTimer() {
        if (HomeActivity.autoTask != null) {
            HomeActivity.autoTask.cancel();
            HomeActivity.autoTask = null;
        }

        if (HomeActivity.autoTimer != null) {
            HomeActivity.autoTimer.cancel();
            HomeActivity.autoTimer.purge();
            HomeActivity.autoTimer = null;
        }
    }

    /**
     * 清除域
     */
    public static  void clearContexts() {
        for (Context context : SmartLinkV3App.getContextInstance()) {
            Activity ac = (Activity) context;
            if (ac != null & !ac.isFinishing()) {
                ac.finish();
            }
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
        Log.d("ma_servicename", "servicename: " + serviceName);
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


    /* -------------------------------------------- INTERFACE -------------------------------------------- */
    public interface OnSwVersionListener {
        void getVersion(boolean needToEncrypt);
    }

    public void setOnSwVersionListener(OnSwVersionListener onSwVersionListener) {
        this.onSwVersionListener = onSwVersionListener;
    }

    public interface OnHwVersionListener {
        void getVersion(String deviceVersion);
    }

    public void setOnHwVersionListener(OnHwVersionListener onHwVersionListener) {
        this.onHwVersionListener = onHwVersionListener;
    }

    public interface OnCustomizedVersionListener {
        void getCustomizedStatus(boolean isCustomized);
    }

    public void setOnCustomizedVersionListener(OnCustomizedVersionListener onCustomizedVersionListener) {
        this.onCustomizedVersionListener = onCustomizedVersionListener;
    }
}
