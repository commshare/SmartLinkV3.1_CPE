package com.alcatel.smartlinkv3.rx.model;

/**
 * Created by qianli.ma on 2017/12/20 0020.
 */

public class WlanState {

    public WlanState(int wlanState) {
        WlanState = wlanState;
    }

    /**
     * WlanState : 1
     */
    private int WlanState;

    public int getWlanState() {
        return WlanState;
    }

    public void setWlanState(int WlanState) {
        this.WlanState = WlanState;
    }
}
