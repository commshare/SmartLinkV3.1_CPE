package com.alcatel.wifilink.model.wlan;

/**
 * Created by tao.j on 2017/6/17.
 */

public class WlanState {
    /**
     * 0: off, 1: on
     */
    int Ap2GStatus;

    /**
     * 0: off, 1: on
     */
    int Ap5GStatus;

    public WlanState(boolean is2GEnabled, boolean is5GEnabled) {
        Ap2GStatus = is2GEnabled ? 1 : 0;
        Ap5GStatus = is5GEnabled ? 1 : 0;
    }

    public boolean isAP2GEnabled(){
        return Ap2GStatus == 1;
    }

    public boolean isAP5GEnabled(){
        return Ap5GStatus == 1;
    }

}
