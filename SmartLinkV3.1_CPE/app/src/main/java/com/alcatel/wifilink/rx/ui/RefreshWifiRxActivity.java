package com.alcatel.wifilink.rx.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.CheckBoard;
import com.alcatel.wifilink.utils.OtherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefreshWifiRxActivity extends AppCompatActivity {

    @BindView(R.id.btn_refresh)
    Button btnRefresh;
    private CheckBoard checkBoard;
    private AlertDialog dialog;
    private Handler handler;
    private ProgressDialog pgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refreshrx_activity);
        ButterKnife.bind(this);
        handler = new Handler();
        Logs.t("ma_permission").vv("refreshrx onCreate");
        Logs.t("ma_unknown").vv("refresh start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopTimer();// 停止定时器
        pgd = OtherUtils.showProgressPop(this);
        handler.postDelayed(this::checkBorads, 3000);
        // checkBorads();// 重新进入界面时检测硬件连接状态
    }


    @OnClick(R.id.btn_refresh)
    public void onViewClicked() {
        checkBorads();
    }

    /**
     * 重新进入界面时检测硬件连接状态
     */
    private void checkBorads() {
        boolean iswifi = OtherUtils.isWifiConnect(this);
        if (iswifi) {
            RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
                @Override
                protected void onSuccess(LoginState result) {
                    OtherUtils.hideProgressPop(pgd);
                    // to(LoginRxActivity.class, true);
                    to(LoadingRxActivity.class, true);
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    OtherUtils.hideProgressPop(pgd);
                    gotConnectDialog();
                }

                @Override
                public void onError(Throwable e) {
                    OtherUtils.hideProgressPop(pgd);
                    gotConnectDialog();
                }
            });
        } else {
            OtherUtils.hideProgressPop(pgd);
            gotConnectDialog();
        }
    }

    /**
     * 显示连接失败对话框
     */
    public void gotConnectDialog() {
        dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.refresh_get_connected);
        builder.setMessage(R.string.refresh_manage_device_tips);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            // 前往wifi选择界面
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });
        dialog = builder.create();
        if (dialog != null & !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        OtherUtils.clearContexts(getClass().getSimpleName());
        OtherUtils.setWifiActive(this, true);
        OtherUtils.clearAllTimer();
        OtherUtils.stopHomeTimer();
        stopHomeHeart();
    }

    public void stopHomeHeart() {
        if (HomeRxActivity.heartTimer != null) {
            HomeRxActivity.heartTimer.stop();
        }
    }

    public void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(this, ac, false, isFinish, false, 0);
    }
}
