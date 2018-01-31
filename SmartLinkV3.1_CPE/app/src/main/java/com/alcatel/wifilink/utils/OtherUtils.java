package com.alcatel.wifilink.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.ArrayRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.bean.SMSContactSelf;
import com.alcatel.wifilink.rx.ui.HomeRxActivity;
import com.alcatel.wifilink.rx.ui.LoginRxActivity;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.wizard.allsetup.DataPlanActivity;
import com.alcatel.wifilink.ui.wizard.allsetup.WifiGuideActivity;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by qianli.ma on 2017/7/10.
 */

public class OtherUtils {

    public static boolean TEST = true;

    private OnSwVersionListener onSwVersionListener;
    private OnHwVersionListener onHwVersionListener;
    private OnCustomizedVersionListener onCustomizedVersionListener;
    public static List<Object> timerList = new ArrayList<>();
    public static List<Object> homeTimerList = new ArrayList<>();// 仅存放自动退出定时器
    public static List<PopupWindows> popList = new ArrayList<>();
    public static OnHeartBeatListener onHeartBeatListener;

    /**
     * 获取某个contactId下所有的smsid
     *
     * @param scList
     * @return
     */
    public static List<Long> getAllSmsIdByOneSession(SMSContentList scList) {
        List<Long> smsIds = new ArrayList<>();
        for (SMSContentList.SMSContentBean scb : scList.getSMSContentList()) {
            smsIds.add(scb.getSMSId());
        }
        return smsIds;
    }

    /**
     * 根据SP读取到的值获取数组中的字符
     *
     * @param arr     需要匹配的字符数组
     * @param include 数组中可能包含的字符
     * @return
     */
    public static String getAlert(String[] arr, int include) {
        String alert = "90%";
        for (String s : arr) {
            if (s.contains(String.valueOf(include))) {
                alert = s;
                break;
            }
        }
        return alert;
    }

    /**
     * 获取字符数组资源
     *
     * @param context
     * @return
     */
    public static String[] getResArr(Context context, @ArrayRes int resId) {
        return context.getResources().getStringArray(resId);
    }


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
     * 设置编辑域编辑状态
     *
     * @param et
     * @param isEditable
     */
    public static void setEdittextEditable(EditText et, boolean isEditable) {
        et.setFocusable(isEditable);
        et.setFocusableInTouchMode(isEditable);
    }


    /**
     * 判断编辑域是否为空
     *
     * @param strs
     * @return
     */
    public static boolean isEmptys(String... strs) {
        for (String str : strs) {
            if (TextUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断编辑域是否为空
     *
     * @param ets
     * @return
     */
    public static boolean isEmptys(EditText... ets) {
        for (EditText editText : ets) {
            if (TextUtils.isEmpty(getEdContent(editText))) {
                return true;
            }
        }
        return false;
    }

    /**
     * IP地址匹配
     *
     * @param content
     * @return
     */
    public static boolean ipMatch(String content) {
        String ipRule = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."// 1
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."// 2
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."// 3
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";   // 4
        return Pattern.matches(ipRule, content);
    }

    /**
     * IP、submask高级匹配
     *
     * @param ipAddress
     */
    public static boolean ipSuperMatch(String ipAddress) {
        if (!ipMatch(ipAddress)) {
            Logs.t("ma_ip").vv("ip address: " + "init check not match");
            return false;
        }
        Logs.t("ma_ip").vv("ip address: " + ipAddress);
        if (TextUtils.isEmpty(ipAddress)) {// 空值
            Logs.t("ma_ip").vv("ip address: " + "empty");
            return false;
        }
        if (!ipAddress.contains(".")) {// 不含点
            Logs.t("ma_ip").vv("ip address: " + "not dot");
            return false;
        }
        String[] address = ipAddress.split("\\.");
        for (String s : address) {// 全部是点没有数字
            if (TextUtils.isEmpty(s)) {
                return false;
            }
        }
        Logs.t("ma_ip").vv("ip address: " + "normal");
        List<Integer> ips = new ArrayList<>();
        for (String s : address) {
            ips.add(Integer.valueOf(s));
        }

        int num0 = 0;
        try {// 避免位数不够
            num0 = ips.get(0);
        } catch (Exception e) {
            num0 = 0;
        }

        int num1 = 0;
        try {// 避免位数不够
            num1 = ips.get(1);
        } catch (Exception e) {
            num1 = 0;
        }

        int num2 = 0;
        try {// 避免位数不够
            num2 = ips.get(2);
        } catch (Exception e) {
            num2 = 0;
        }

        int num3 = 0;
        try {// 避免位数不够
            num3 = ips.get(3);
        } catch (Exception e) {
            num3 = 0;
        }

        Logs.t("ma_ip").vv(num0 + ":" + num1 + ":" + num2 + ":" + num3);
        if ((num0 <= 0 || num0 == 127 || num0 > 223) || // num0
                    (num1 < 0 || num1 > 255) || // num1
                    (num2 < 0 || num2 > 255) || // num2
                    (num3 <= 0 || num3 >= 255)) {// num3
            return false;
        } else {
            return true;
        }
    }

    /**
     * 启动心跳定时器
     *
     * @param oriActivity 当前context
     * @param acTimeout   出错时的目标地址
     * @return 定时器辅助
     */
    public static TimerHelper startHeartBeat(Activity oriActivity, Class acTimeout, Class acLogout) {

        TimerHelper heartTimerAll = new TimerHelper(oriActivity) {
            @Override
            public void doSomething() {
                Logger.v("ma_check:" + "startHeartBeat");
                // is wifi effect?
                boolean isWifi = OtherUtils.isWifiConnect(oriActivity);
                if (isWifi) {
                    RX.getInstant().heartBeat(new ResponseObject() {
                        @Override
                        protected void onSuccess(Object result) {
                            if (onHeartBeatListener != null) {
                                onHeartBeatListener.onSucess();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logs.v("ma_couldn_connect", "startHeartBeat1: " + e.getMessage());
                            Logs.v("ma_unknown", "startHeartBeat1: onError");
                            clear(oriActivity, acTimeout);
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            Logs.v("ma_unknown", "startHeartBeat1: onResultError");
                            Logs.v("ma_couldn_connect", "startHeartBeat2: " + error.getMessage());
                            clear(oriActivity, acTimeout);
                        }
                    });
                } else {// wifi失效
                    Logs.v("ma_unknown", "startHeartBeat1: is not Wifi");
                    Logs.v("ma_couldn_connect", "no wifi");
                    clear(oriActivity, acTimeout);
                }
            }
        };
        heartTimerAll.start(2000);
        return heartTimerAll;
    }

    /**
     * 清理并跳转
     *
     * @param oriActivity
     * @param acTimeout
     */
    private static void clear(Activity oriActivity, Class acTimeout) {
        // 出错, 跳转到目标界面
        Log.v("ma_couldn_connect", "clear startHeartBeat error");

        // 获取当前顶层的activity
        String currentActivitySimpleName = AppInfo.getCurrentActivitySimpleName(oriActivity);
        String loginName = LoginRxActivity.class.getSimpleName();
        String saveName = SPUtils.getInstance(oriActivity).getString(Cons.CURRENT_ACTIVITY, "");
        // 如果当前是处于登陆页面则不跳转
        if (!currentActivitySimpleName.equalsIgnoreCase(loginName) & // 不相等
                    !currentActivitySimpleName.contains(loginName) & // 不包含
                    !loginName.contains(currentActivitySimpleName) & // 不包含
                    !loginName.equalsIgnoreCase(saveName) & // 不相等
                    !loginName.contains(saveName) & // 不包含
                    !saveName.contains(loginName) // 不包含
                ) {
            CA.toActivity(oriActivity, acTimeout, false, true, false, 0);
        } else {
            Logs.t("ma_unknown").vv("OtherUtils--> startHeartBeat--> clear--> current is login");
        }

    }

    /**
     * 获取短信联系人列表并添加长按标记
     *
     * @param smsContactList
     * @return
     */
    public static List<SMSContactSelf> getSMSSelfList(SMSContactList smsContactList) {
        List<SMSContactSelf> smscs = new ArrayList<>();
        for (SMSContactList.SMSContact smsContact : smsContactList.getSMSContactList()) {
            // 新建自定义SMS Contact对象
            SMSContactSelf scs = new SMSContactSelf();
            scs.setSmscontact(smsContact);
            scs.setLongClick(false);
            smscs.add(scs);
        }
        return smscs;
    }

    /**
     * 修改联系人列表是否进入可删除状态(islongClick==true则为可删除)
     *
     * @param isLongClick
     */
    public static List<SMSContactSelf> modifySMSContactSelf(List<SMSContactSelf> smsContactSelves, boolean isLongClick) {
        for (SMSContactSelf smsContactSelf : smsContactSelves) {
            smsContactSelf.setLongClick(isLongClick);
        }
        return smsContactSelves;
    }

    public interface OnHeartBeatListener {
        void onSucess();
    }

    public static void setOnHeartBeatListener(OnHeartBeatListener onHeartBeatListener) {
        OtherUtils.onHeartBeatListener = onHeartBeatListener;
    }

    /**
     * 停止心跳定时器
     *
     * @param timerHelper
     */
    public static void stopHeartBeat(TimerHelper timerHelper) {
        if (timerHelper != null) {
            timerHelper.stop();
        }
    }


    /**
     * 获取编辑域内容(去除空格)
     *
     * @param ed
     * @return
     */
    public static String getEdContent(EditText ed) {
        return ed.getText().toString().trim().replace(" ", "");
    }

    /**
     * 获取编辑域内容(不去除空格)
     *
     * @param ed
     * @return
     */
    public static String getEdContentIncludeSpace(EditText ed) {
        return ed.getText().toString().trim();
    }

    /**
     * 线程自关
     */
    public static void kill() {
        System.exit(0);
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
        RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
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
        RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
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
                RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
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
        RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
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
    public static boolean isWifiConnect(Context context) {
        SmartLinkV3App instance = SmartLinkV3App.getInstance();
        ConnectivityManager connMgr = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo.State wifiState = networkInfo.getState();
        return NetworkInfo.State.CONNECTED == wifiState;
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
     * 打开并连接WIFI
     *
     * @param context
     * @return
     */
    public static boolean setWifiActive(Context context, boolean open) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.setWifiEnabled(open);
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
     * 清除域
     */
    public static void clearContexts(String clazz) {
        List<Activity> contexts = SmartLinkV3App.getContextInstance();
        for (Activity activity : contexts) {
            if (activity.getClass().getSimpleName().equalsIgnoreCase(clazz)) {// 与指定的类相同--> 跳过
                continue;
            }
            if (activity != null & !activity.isFinishing()) {
                activity.finish();
            }
        }
        contexts.clear();
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

    /**
     * 某些界面的跳转
     *
     * @param context
     */
    public static void loginSkip(Context context) {
        boolean isWifiGuide = SP.getInstance(context).getBoolean(Cons.WIFI_GUIDE_FLAG, false);
        if (isWifiGuide) {/* 进入过了 */
            // 是否进入过流量设置界面
            boolean isDataPlan = SP.getInstance(context).getBoolean(Cons.DATA_PLAN_FLAG, false);
            if (isDataPlan) {
                CA.toActivity(context, HomeActivity.class, false, true, false, 0);
            } else {
                CA.toActivity(context, DataPlanActivity.class, false, true, false, 0);
            }
        } else {/* 没有进入过 */
            CA.toActivity(context, WifiGuideActivity.class, false, true, false, 0);
        }
    }

    /**
     * 清除全部的定时器
     */
    public static void clearAllTimer() {
        for (Object o : timerList) {
            // 辅助类的情况
            if (o instanceof TimerHelper) {
                TimerHelper th = (TimerHelper) o;
                th.stop();
            }

            // 任务
            if (o instanceof TimerTask) {
                TimerTask tk = (TimerTask) o;
                tk.cancel();
                tk = null;
            }

            // 定时器
            if (o instanceof Timer) {
                Timer t = (Timer) o;
                t.cancel();
                t.purge();
                t = null;
            }

            // 任务集
            if (o instanceof Map) {
                Map<Activity, Intent> map = (Map<Activity, Intent>) o;
                Set<Activity> acts = map.keySet();
                for (Activity act : acts) {
                    act.stopService(map.get(act));
                }
            }

        }
        timerList.clear();
    }

    /**
     * 停止全局定时器
     */
    public static void stopHomeTimer() {

        for (Object o : homeTimerList) {
            if (o instanceof TimerTask) {
                TimerTask tk = (TimerTask) o;
                tk.cancel();
                tk = null;
            }
            if (o instanceof Timer) {
                Timer t = (Timer) o;
                t.cancel();
                t.purge();
                t = null;
            }
        }
        homeTimerList.clear();

        if (HomeRxActivity.autoLogoutTask != null) {
            HomeRxActivity.autoLogoutTask.cancel();
            HomeRxActivity.autoLogoutTask = null;
        }

        if (HomeRxActivity.autoLogoutTimer != null) {
            HomeRxActivity.autoLogoutTimer.cancel();
            HomeRxActivity.autoLogoutTimer.purge();
            HomeRxActivity.autoLogoutTimer = null;
        }
    }

    /**
     * 显示等待进度条
     *
     * @param context
     */
    public static ProgressDialog showProgressPop(Context context) {
        ProgressDialog pgd = new ProgressDialog(context);
        pgd.setMessage(context.getString(R.string.connecting));
        pgd.setCanceledOnTouchOutside(false);
        if (!((Activity) context).isFinishing()) {
            pgd.show();
        }
        return pgd;
    }

    /**
     * 显示等待进度条
     *
     * @param context
     */
    public static ProgressDialog showProgressPop(Context context, String content) {
        ProgressDialog pgd = new ProgressDialog(context);
        pgd.setMessage(content);
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
     * 切换语言
     *
     * @param activity
     */
    public static void transferLanguage(Activity activity) {
        // 初始化PreferenceUtil
        PreferenceUtil.init(activity);
        // 根据上次的语言设置，重新设置语言
        if (!"".equals(PreferenceUtil.getString(Constants.Language.LANGUAGE, ""))) {
            String language = PreferenceUtil.getString(Constants.Language.LANGUAGE, "");
            // 设置应用语言类型
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (language.equals(Constants.Language.ENGLISH)) {
                config.locale = Locale.ENGLISH;
            } else if (language.equals(Constants.Language.ARABIC)) {
                // 阿拉伯语
                config.locale = new Locale(Constants.Language.ARABIC);
            } else if (language.equals(Constants.Language.GERMENIC)) {
                // 德语
                config.locale = Locale.GERMANY;
            } else if (language.equals(Constants.Language.ESPANYOL)) {
                // 西班牙语
                config.locale = new Locale(Constants.Language.ESPANYOL);
            } else if (language.equals(Constants.Language.ITALIAN)) {
                // 意大利语
                config.locale = Locale.ITALIAN;
            } else if (language.equals(Constants.Language.FRENCH)) {
                // 法语
                config.locale = Locale.FRENCH;
            } else if (language.equals(Constants.Language.SERBIAN)) {
                // 塞尔维亚
                config.locale = new Locale(Constants.Language.SERBIAN);
            } else if (language.equals(Constants.Language.CROATIAN)) {
                // 克罗地亚
                config.locale = new Locale(Constants.Language.CROATIAN);
            } else if (language.equals(Constants.Language.SLOVENIAN)) {
                // 斯洛文尼亚
                config.locale = new Locale(Constants.Language.SLOVENIAN);
            }
            resources.updateConfiguration(config, dm);

            // 保存设置语言的类型
            PreferenceUtil.commitString(Constants.Language.LANGUAGE, language);
        }
    }

    /**
     * @return 当前语言
     */
    public static String getCurrentLanguage() {
        return PreferenceUtil.getString(Constants.Language.LANGUAGE, "");
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
