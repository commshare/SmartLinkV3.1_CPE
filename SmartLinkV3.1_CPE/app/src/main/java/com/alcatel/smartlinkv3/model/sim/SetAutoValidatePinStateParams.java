package com.alcatel.smartlinkv3.model.sim;

/**
 * Created by tao.j on 2017/6/16.
 */

public class SetAutoValidatePinStateParams {

    String Pin;
    /**
     0: Disable
     1: Enable
     */
    int State;

    public SetAutoValidatePinStateParams(String pin, int state) {
        Pin = pin;
        State = state;
    }
}
