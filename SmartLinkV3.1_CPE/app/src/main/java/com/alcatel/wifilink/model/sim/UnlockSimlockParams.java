package com.alcatel.wifilink.model.sim;

/**
 * Created by tao.j on 2017/6/16.
 */

public class UnlockSimlockParams {
    /**
     * SIMLockState:
     0: nck
     1: nsck
     2: spck
     3: cck
     4: pck
     15,16,17,18,19: rck
     */
    int SIMLockState;
    String SIMLockCode;

    public UnlockSimlockParams(int SIMLockState, String SIMLockCode) {
        this.SIMLockState = SIMLockState;
        this.SIMLockCode = SIMLockCode;
    }
}
