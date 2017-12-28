package com.alcatel.smartlinkv3.rx.bean;

/**
 * Created by qianli.ma on 2017/12/28 0028.
 */

public class SimState {
    /**
     * SIMState : 0
     * PinState : 0
     * PinRemainingTimes : 0
     * PukRemainingTimes : 0
     * SIMLockState : -1
     * SIMLockRemainingTimes : 0
     */

    private int SIMState;
    private int PinState;
    private int PinRemainingTimes;
    private int PukRemainingTimes;
    private int SIMLockState;
    private int SIMLockRemainingTimes;

    public int getSIMState() {
        return SIMState;
    }

    public void setSIMState(int SIMState) {
        this.SIMState = SIMState;
    }

    public int getPinState() {
        return PinState;
    }

    public void setPinState(int PinState) {
        this.PinState = PinState;
    }

    public int getPinRemainingTimes() {
        return PinRemainingTimes;
    }

    public void setPinRemainingTimes(int PinRemainingTimes) {
        this.PinRemainingTimes = PinRemainingTimes;
    }

    public int getPukRemainingTimes() {
        return PukRemainingTimes;
    }

    public void setPukRemainingTimes(int PukRemainingTimes) {
        this.PukRemainingTimes = PukRemainingTimes;
    }

    public int getSIMLockState() {
        return SIMLockState;
    }

    public void setSIMLockState(int SIMLockState) {
        this.SIMLockState = SIMLockState;
    }

    public int getSIMLockRemainingTimes() {
        return SIMLockRemainingTimes;
    }

    public void setSIMLockRemainingTimes(int SIMLockRemainingTimes) {
        this.SIMLockRemainingTimes = SIMLockRemainingTimes;
    }
}
