package com.alcatel.smartlinkv3.rx.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.GuideActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.ui.service.LoginService;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;

public class LoadingRxActivity extends BaseRxActivity {

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading_rx);
        checkBoardConnect();
        OtherUtils.verifyPermisson(this);// 申請權限
        loginService();// 啟動服務
    }

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
                goActivity();
            }

            @Override
            public void onError(Throwable e) {
                toActivity(RefreshWifiActivity.class);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                // toActivity(RefreshWifiActivity.class);
            }
        });
    }

    /**
     * 按情况跳转
     */
    private void goActivity() {
        if (!CPEConfig.getInstance().getInitialLaunchedFlag()) {
            toActivity(GuideActivity.class);// 前往向导页
        } else {
            toActivity(LoginRxActivity.class);// 前往登陆页
        }
    }

    /**
     * 跳转
     *
     * @param clazz 需要跳转的Activity类
     */
    public void toActivity(Class clazz) {
        ChangeActivity.toActivity(context, clazz, false, true, false, 2000);
    }

}
