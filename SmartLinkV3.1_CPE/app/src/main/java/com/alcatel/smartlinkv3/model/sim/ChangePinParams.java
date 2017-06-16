package com.alcatel.smartlinkv3.model.sim;

/**
 * Created by tao.j on 2017/6/16.
 */

public class ChangePinParams {
        String NewPin;
        String CurrentPin;
    public ChangePinParams(String newPin, String currentPin) {
        NewPin = newPin;
        CurrentPin = currentPin;
    }
}
