package com.alcatel.wifilink.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.EditText;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qianli.ma on 2017/7/10.
 */

public class OtherUtils {

    private OnVersionListener onVersionListener;
    private OnDeviceVersionListener onDeviceVersionListener;

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
     * 获取设备的驱动版本号
     */
    public void getDeviceSwVersion() {
        // 1.需要加密的版本
        List<String> needEncryptVersions = new ArrayList<String>();
        needEncryptVersions.add("HH70_E1_02.00_13");
        // 2.获取当前版本
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                String currentVersion = result.getSwVersion();
                if (onVersionListener != null) {
                    // 如果加密集合中的版本元素,包含当前获取的版本--> 则传递加密信号为true
                    if (needEncryptVersions.contains(currentVersion)) {
                        onVersionListener.getVersion(true);
                    } else {
                        onVersionListener.getVersion(false);
                    }
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                // 如果出错--> 则是需要加密的版本
                if (onVersionListener != null) {
                    onVersionListener.getVersion(true);
                }
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
                if (onDeviceVersionListener != null) {
                    onDeviceVersionListener.getVersion(result.getSwVersion());
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
    public static boolean checkWifiConnect(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }

    /* -------------------------------------------- INTERFACE -------------------------------------------- */
    public interface OnVersionListener {
        void getVersion(boolean needToEncrypt);
    }

    public void setOnVersionListener(OnVersionListener onVersionListener) {
        this.onVersionListener = onVersionListener;
    }

    public interface OnDeviceVersionListener {
        void getVersion(String deviceVersion);
    }

    public void setOnDeviceVersionListener(OnDeviceVersionListener onDeviceVersionListener) {
        this.onDeviceVersionListener = onDeviceVersionListener;
    }


}
