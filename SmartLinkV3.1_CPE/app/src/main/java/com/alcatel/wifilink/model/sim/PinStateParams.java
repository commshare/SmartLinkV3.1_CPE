package com.alcatel.wifilink.model.sim;

/**
 * Created by tao.j on 2017/6/16.
 */

public class PinStateParams {

    String Pin;

    /**0: Disabled PIN, 1: Enabled PIN*/
    int State;

    public PinStateParams(String pin, int state) {
        Pin = pin;
        State = state;
    }
}
