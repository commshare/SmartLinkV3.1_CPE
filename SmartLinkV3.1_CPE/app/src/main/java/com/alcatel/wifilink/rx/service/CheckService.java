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

    /* 1.创建binder */
    private CheckBinder checkBinder = new CheckBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CheckService service = checkBinder.getService();
        /* 3.返回binder */
        return checkBinder;
    }

    /* 1.定义一个类继承binder进行绑定 */
    public class CheckBinder extends Binder {
        CheckService getService() {
            return CheckService.this;
        }
    }
}
