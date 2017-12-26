package com.alcatel.smartlinkv3.rx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.ui.service.LoginService;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingRxActivity extends BaseRxActivity {

    @BindView(R.id.iv_loading)
    ImageView ivLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading_rx);
        ButterKnife.bind(this);
        checkBoardConnect();// 检测能否连接上硬件
    }

    /**
     * 登陆后启动服务--> 用于检测APP是否被杀死
     */
    private void loginService() {
        // 查看正在运行的服务
        boolean homeServiceWorked = OtherUtils.isServiceWork(this, LoginService.class);
        if (!homeServiceWorked) {// 指定的服务没有运行--> 创建(用于检测APP是否被杀死)
            Intent intent = new Intent(this, LoginService.class);
            startService(intent);
        }
    }

    /**
     * 检测是否连接上了硬件
     */
    private void checkBoardConnect() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                Logger.t("ma_loading").v("loading success");
                OtherUtils.verifyPermisson(LoadingRxActivity.this);// 申請權限
                loginService();// 啟動服務
                goActivity();
            }

            @Override
            public void onError(Throwable e) {
                toActivity(RefreshWifiActivity.class);
                Logger.t("ma_loading").v("loading error: " + e.getMessage());
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toActivity(RefreshWifiActivity.class);
                Logger.t("ma_loading").v("loading error: " + error.getMessage());
            }
        });
    }

    /**
     * 按情况跳转
     */
    private void goActivity() {
        if (SPUtils.getInstance(this).getBoolean(Conn.GUIDE_FLAG, false)) {
            toActivity(LoginRxActivity.class);// 进入过向导--> 前往登陆页
        } else {
            toActivity(GuideNActivity.class);// 没有进入过--> 前往向导页
        }
    }

    /**
     * 跳转
     *
     * @param clazz 需要跳转的Activity类
     */
    public void toActivity(Class clazz) {
        ChangeActivity.toActivity(this, clazz, false, true, false, 2000);
    }

}
