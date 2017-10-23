package com.alcatel.smartlinkv3.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.utils.AppInfo;
import com.alcatel.smartlinkv3.utils.TimerHelper;

import java.util.List;

/**
 * Created by qianli.ma on 2017/10/19 0019.
 */

public class LoginService extends Service {
    private TimerHelper timerHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                List<String> allRunningPackage = AppInfo.getAllRunningPackage(LoginService.this);
                String currentPackName = getPackageName();// 当前类名
                // 如果当前运行的包名没有包含当前服务的包名,则认为APP被杀死--> 退出
                if (!allRunningPackage.contains(currentPackName)) {
                    API.get().logout(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {
                            // 彈出提示
                            // ToastUtil_m.show(LoginService.this, getString(R.string.login_logout_successful));
                            // 結束服務
                            stopService(intent);
                            // 停止定時器
                            timerHelper.stop();
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {

                        }
                    });
                }
            }
        };
        timerHelper.start(500);
        return super.onStartCommand(intent, flags, startId);
    }
}
