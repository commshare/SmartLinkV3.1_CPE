package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.app.ProgressDialog;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.ui.LoginRxActivity;
import com.alcatel.wifilink.rx.ui.RefreshWifiRxActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;

/**
 * 检测是否连接上硬件并处于登陆状态
 */
public abstract class CheckBoardLogin {
    private Activity activity;
    private ProgressDialog pgd;

    public abstract void afterCheckSuccess(ProgressDialog pop);

    public CheckBoardLogin(Activity activity) {
        this.activity = activity;
        initCheck();
    }

    private void initCheck() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(activity);
        }
        // 1.连接硬件
        new CheckBoard() {
            @Override
            public void successful() {
                // 2.登陆状态
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        if (result.getState() == Cons.LOGOUT) {
                            toast(R.string.login_kickoff_logout_successful);
                            to(LoginRxActivity.class);
                            return;
                        }
                        // 3.连接成功后
                        afterCheckSuccess(pgd);
                        OtherUtils.hideProgressPop(pgd);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }
                });
            }
        }.checkBoard(activity, RefreshWifiRxActivity.class);
    }


    public void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private void to(Class clazz) {
        CA.toActivity(activity, clazz, false, true, false, 0);
    }

}
