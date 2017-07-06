package com.alcatel.wifilink.model.wlan;

/**
 * Created by tao.j on 2017/6/17.
 */

public class WlanSupportAPMode {

    /**
     * 0:Only 2.4G, 1: Only 5G, 2:Both 2.4G And 5G
     */
    private int WlanAPMode;

    public WlanSupportAPMode(int wlanAPMode) {
        WlanAPMode = wlanAPMode;
    }

    public int getWlanSupportAPMode() {
        return WlanAPMode;
    }
}
