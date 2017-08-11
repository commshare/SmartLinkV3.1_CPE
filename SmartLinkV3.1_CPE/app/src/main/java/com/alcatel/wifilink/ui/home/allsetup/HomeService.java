package com.alcatel.wifilink.ui.home.allsetup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.AppInfo;

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
                List<String> allRunningPackage = AppInfo.getAllRunningPackage(HomeService.this);
                String currentPackName = getPackageName();// 当前类名
                // 如果当前运行的包名没有包含当前服务的包名,则认为APP被杀死--> 退出
                if (!allRunningPackage.contains(currentPackName)) {
                    API.get().logout(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {

                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {

                        }
                    });
                }
            }
        };
        packageTimer.start(1000);
        return super.onStartCommand(intent, flags, startId);
    }
}
