package com.alcatel.wifilink.business.sim.helper;

import android.content.Context;

import com.alcatel.wifilink.business.SimManager;
import com.alcatel.wifilink.common.DataValue;

/**
 * Created by qianli.ma on 2017/6/5.
 */

public abstract class SimPinHelper {

    private Context context;

    public SimPinHelper(Context context) {
        this.context = context;
    }

    public void connect(String pinCode) {
        SimManager simManager = new SimManager(context);
        simManager.setOnLockPinListener(correct -> {
            isPinCorrect(correct);
        });
        DataValue data = new DataValue();
        data.addParam("pin", pinCode);
        simManager.unlockPin(data);
    }

    public abstract void isPinCorrect(boolean correct);
}
