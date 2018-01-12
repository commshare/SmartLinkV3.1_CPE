package com.alcatel.wifilink.ui.home.allsetup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.service.CheckService;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.AppInfo;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;

import java.util.List;

/**
 * Created by qianli.ma on 2017/8/10.
 */

public class HomeService extends Service {
    private TimerHelper packageTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        packageTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                // TOGO 2018/1/11 0011 获取当前运行的包集合是否包含当前APP的包名(该操作在android6.0已过时)
                // getCurrentPackage();

                /* 推荐使用该方式, 通过获取当前运行的服务是否包含了HomeRxActivity绑定的服务 */
                // 1.检测HomeRxActivity绑定的服务是否正在运行
                boolean checkServiceRunning = isCheckServiceRunning();
                Logs.t("ma_service").vv("checkServiceRunning:" + checkServiceRunning);
                // 2.如果绑定的服务不再运行
                if (!checkServiceRunning) {
                    // 3.停止服务--> 4.停止服务的onDestroy方法里登出
                    stopService(intent);
                }
            }
        };
        packageTimer.start(500);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (packageTimer != null) {
            packageTimer.stop();
        }
        Logs.t("ma_service").vv("homeservice onDestroy");
        logout();// 4.停止服务的onDestroy方法里登出
    }

    private boolean isCheckServiceRunning() {
        return OtherUtils.isServiceWork(SmartLinkV3App.getInstance(), CheckService.class);
    }

    /**
     * 获取当前运行的包集合是否包含当前APP的包名(该操作在android6.0已过时)
     */
    private void getCurrentPackage() {
        List<String> allRunningPackage = AppInfo.getAllRunningPackage(HomeService.this);
        Logs.t("ma_package").vv(allRunningPackage.toString());
        String currentPackName = getPackageName();// 当前包名
        // 如果当前运行的包名没有包含当前服务的包名,则认为APP被杀死--> 退出
        if (!allRunningPackage.contains(currentPackName)) {
            logout();
        }
    }

    private void logout() {
        RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                OtherUtils.clearAllTimer();
                if (result.getState() == Cons.LOGIN) {
                    Logs.t("ma_permission").vv("service logout");
                    RX.getInstant().logout(new ResponseObject() {
                        @Override
                        protected void onSuccess(Object result) {
                            stopSelf();
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {

                        }
                    });
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
