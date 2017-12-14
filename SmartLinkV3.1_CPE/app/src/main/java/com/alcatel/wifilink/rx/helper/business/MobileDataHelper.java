package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectSettingHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectStatusHelper;
import com.alcatel.wifilink.rx.helper.base.UsageHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2017/12/11 0011.
 */

public class MobileDataHelper {

    private Activity context;

    public MobileDataHelper(Activity context) {
        this.context = context;
    }

    /**
     * 连接或者断连
     */
    public void toConnOrNot() {
        // 1.检查sim卡状态
        BoardSimHelper boardSimHelper = new BoardSimHelper(context);
        boardSimHelper.setOnSimReadyListener(result -> {
            // 2.获取连接状态(断连--> 连接,连接--> 断连)
            ConnectStatusHelper connectStatusHelper = new ConnectStatusHelper();
            connectStatusHelper.setOnError(e -> toast(R.string.connect_failed));
            connectStatusHelper.setOnResultError(e -> toast(R.string.connect_failed));
            connectStatusHelper.setOnDisConnected(result1 -> toConn());// 连接
            connectStatusHelper.setOnConnected(e -> toDisConn());// 断连
            connectStatusHelper.setOnConnecting(result1 -> toast(R.string.connecting));
            connectStatusHelper.setOnDisConnecting(result1 -> toast(R.string.setting_network_try_again));
            connectStatusHelper.getStatus();
        });
        boardSimHelper.setOnInitingListener(simStatus -> toast(R.string.home_initializing));
        boardSimHelper.setOnDetectedListener(simStatus -> toast(R.string.Home_SimCard_Detected));
        boardSimHelper.setOnpukTimeoutListener(result -> toast(R.string.Home_PukTimes_UsedOut));
        boardSimHelper.setOnPinRequireListener(result -> toast(R.string.home_pin_locked_notice));
        boardSimHelper.setOnpukRequireListener(result -> toast(R.string.home_pin_locked_notice));
        boardSimHelper.setOnSimLockListener(simStatus -> toast(R.string.home_pin_locked_notice));
        boardSimHelper.boardNormal();
    }

    /**
     * 断连
     */
    private void toDisConn() {
        ConnectSettingHelper csDisconnhelper = new ConnectSettingHelper();
        // 注册产生的错误
        csDisconnhelper.setOnRegisterErrorListener(attr -> toast(R.string.connect_failed));
        csDisconnhelper.setOnRegisterResultErrorListener(attr -> toast(R.string.connect_failed));
        csDisconnhelper.setOnRegisteFailedListener(attr -> toast(R.string.connect_failed));
        csDisconnhelper.setOnNotRegisterListener(attr -> toast(R.string.connect_failed));
        csDisconnhelper.setOnRegistingListener(attr -> toast(R.string.connecting));
        // 断连成功
        csDisconnhelper.setOnDisConnSuccessListener(this::disConnSuccessNext);
        // 连接产生的错误
        csDisconnhelper.setOnErrorListener(attr -> checkMonthly());
        csDisconnhelper.setOnResultErrorListener(attr -> checkMonthly());
        csDisconnhelper.toDisConnect();
    }

    /**
     * 连接
     */
    private void toConn() {
        ConnectSettingHelper csConnhelper = new ConnectSettingHelper();
        // 注册产生的错误
        csConnhelper.setOnRegisterErrorListener(attr -> toast(R.string.connect_failed));
        csConnhelper.setOnRegisterResultErrorListener(attr -> toast(R.string.connect_failed));
        csConnhelper.setOnRegisteFailedListener(attr -> toast(R.string.connect_failed));
        csConnhelper.setOnNotRegisterListener(attr -> toast(R.string.connect_failed));
        csConnhelper.setOnRegistingListener(attr -> toast(R.string.connecting));
        // 连接成功
        csConnhelper.setOnConnSuccessListener(this::connSuccessNext);
        // 连接产生的错误--> 检测是否超出了流量
        csConnhelper.setOnErrorListener(attr -> checkMonthly());
        csConnhelper.setOnResultErrorListener(attr -> checkMonthly());
        csConnhelper.toConnect();
    }

    /**
     * 检查是否超出流量
     */
    private void checkMonthly() {
        UsageHelper usageHelper = new UsageHelper(context);
        usageHelper.setOnGetUsageErrorListener(attr -> toast(R.string.connect_failed));
        usageHelper.setOnGetUsageResultErrorListener(attr -> toast(R.string.connect_failed));
        usageHelper.setOnOverMonthlyListener(attr -> toast(R.string.usage_limit_over_notification_content));// 超出流量
        usageHelper.getOverUsage();
    }

    private OnDisConnSuccessListener onDisConnSuccessListener;

    // 接口OnDisConnSuccessListener
    public interface OnDisConnSuccessListener {
        void disConnSuccess(Object attr);
    }

    // 对外方式setOnDisConnSuccessListener
    public void setOnDisConnSuccessListener(OnDisConnSuccessListener onDisConnSuccessListener) {
        this.onDisConnSuccessListener = onDisConnSuccessListener;
    }

    // 封装方法disConnSuccessNext
    private void disConnSuccessNext(Object attr) {
        if (onDisConnSuccessListener != null) {
            onDisConnSuccessListener.disConnSuccess(attr);
        }
    }

    private OnConnSuccessListener onConnSuccessListener;

    // 接口OnConnSuccessListener
    public interface OnConnSuccessListener {
        void connSuccess(Object attr);
    }

    // 对外方式setOnConnSuccessListener
    public void setOnConnSuccessListener(OnConnSuccessListener onConnSuccessListener) {
        this.onConnSuccessListener = onConnSuccessListener;
    }

    // 封装方法connSuccessNext
    private void connSuccessNext(Object attr) {
        if (onConnSuccessListener != null) {
            onConnSuccessListener.connSuccess(attr);
        }
    }

    public void toast(int resId) {
        ToastUtil_m.show(context, resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(context, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(context, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(context, ac, false, isFinish, false, 0);
    }
}
