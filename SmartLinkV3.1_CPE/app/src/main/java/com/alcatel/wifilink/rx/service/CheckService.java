package com.alcatel.wifilink.rx.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alcatel.wifilink.utils.Logs;

/**
 * Created by qianli.ma on 2018/1/11 0011.
 */

public class CheckService extends Service {

    private CheckBinder checkBinder = new CheckBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CheckService service = checkBinder.getService();
        Logs.t("ma_service").vv("CheckService onBind: " + service.getPackageName());
        return checkBinder;
    }

    public class CheckBinder extends Binder {
        CheckService getService() {
            return CheckService.this;
        }
    }
}
