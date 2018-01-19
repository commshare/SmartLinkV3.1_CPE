package com.alcatel.wifilink.rx.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.alcatel.wifilink.utils.Logs;

/**
 * Created by qianli.ma on 2018/1/11 0011.
 */

public class CheckServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v("service","CheckServiceConnection onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v("service","CheckServiceConnection onServiceDisconnected");
    }
}
