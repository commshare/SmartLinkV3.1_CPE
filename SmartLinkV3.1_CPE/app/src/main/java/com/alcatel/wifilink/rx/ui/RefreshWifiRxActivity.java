package com.alcatel.wifilink.rx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.CheckBoard;
import com.alcatel.wifilink.utils.OtherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefreshWifiRxActivity extends AppCompatActivity {

    @BindView(R.id.btn_refresh)
    Button btnRefresh;
    private CheckBoard checkBoard;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refreshrx_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopTimer();// 停止定时器
        checkBorads();// 重新进入界面时检测硬件连接状态
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
            API.get().getLoginState(new MySubscriber<LoginState>() {
                @Override
                protected void onSuccess(LoginState result) {
                    to(LoginRxActivity.class, true);
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    showDialog();
                }

                @Override
                public void onError(Throwable e) {
                    showDialog();
                }
            });
        } else {
            showDialog();
        }
    }

    /**
     * 显示连接失败对话框
     */
    public void showDialog() {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.refresh_get_connected);
            builder.setMessage(R.string.refresh_manage_device_tips);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                // 前往wifi选择界面
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            });
            dialog = builder.create();
            dialog.setOnDismissListener(dialog -> dialog = null);
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
