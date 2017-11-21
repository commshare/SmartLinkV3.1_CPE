package com.alcatel.wifilink.model.wlan;

/**
 * Created by qianli.ma on 2017/11/20 0020.
 */

public class WlanStatus {


    /**
     * Ap2GStatus : 0
     * Ap5GStatus : 0
     */

    private int Ap2GStatus;
    private int Ap5GStatus;

    public int getAp2GStatus() {
        return Ap2GStatus;
    }

    public void setAp2GStatus(int Ap2GStatus) {
        this.Ap2GStatus = Ap2GStatus;
    }

    public int getAp5GStatus() {
        return Ap5GStatus;
    }

    public void setAp5GStatus(int Ap5GStatus) {
        this.Ap5GStatus = Ap5GStatus;
    }
}
