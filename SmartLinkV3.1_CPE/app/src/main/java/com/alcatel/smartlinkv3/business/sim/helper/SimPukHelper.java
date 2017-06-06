package com.alcatel.smartlinkv3.business.sim.helper;

import android.content.Context;
import android.widget.EditText;

import com.alcatel.smartlinkv3.business.SimManager;
import com.alcatel.smartlinkv3.common.DataValue;

/**
 * Created by qianli.ma on 2017/6/6.
 */

public abstract class SimPukHelper {
    private Context context;

    public SimPukHelper(Context context) {
        this.context = context;
    }

    public abstract void isSuccesss(boolean isSuccess);

    public void commit(EditText et_pukCode, EditText et_pinCode) {
        SimManager simManager = new SimManager(context);
        simManager.setOnUnlockPukListener(success -> {
            isSuccesss(success);
        });
        DataValue data = new DataValue();
        data.addParam("puk", et_pukCode.getText().toString().trim().replace(" ", ""));
        data.addParam("pin", et_pinCode.getText().toString().trim().replace(" ", ""));
        simManager.unlockPuk(data);
    }
}
