package com.alcatel.smartlinkv3.model.sim;

/**
 * Created by tao.j on 2017/6/16.
 */

public class SimStatus {
//SIMState:
//0: noun
//1: SIM Card detected;
//2: PIN Required;
//3: PUK Required;
//4: SIM Lock required;
//5: PUK times used out;
//6: SIM card illegal;
//7: SIM card ready;
//11: SIM card is initing;

//PinState:
//0: unknown
//1: enable but not verified
//2: PIN enable verified
//3: PIN disable
//4: PUK required
//5: PUK times used out;

//SIMLockState:
//-1: no simlock/sim lock unlock
//0: nck
//1: nsck
//2: spck
//3: cck
//4: pck
//15,16,17,18,19:rck
//30: rck forbid
//
    int SIMState;
    int PinState;
    int PinRemainingTimes;
    int PukRemainingTimes;
    int SIMLockState;
    int SIMLockRemainingTimes;

    public int getSIMState() {
        return SIMState;
    }

    public void setSIMState(int SIMState) {
        this.SIMState = SIMState;
    }

    public int getPinState() {
        return PinState;
    }

    public void setPinState(int pinState) {
        PinState = pinState;
    }

    public int getPinRemainingTimes() {
        return PinRemainingTimes;
    }

    public void setPinRemainingTimes(int pinRemainingTimes) {
        PinRemainingTimes = pinRemainingTimes;
    }

    public int getPukRemainingTimes() {
        return PukRemainingTimes;
    }

    public void setPukRemainingTimes(int pukRemainingTimes) {
        PukRemainingTimes = pukRemainingTimes;
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

    @Override
    public String toString() {
        return "SimStatus{" +
                "SIMState=" + SIMState +
                ", PinState=" + PinState +
                ", PinRemainingTimes=" + PinRemainingTimes +
                ", PukRemainingTimes=" + PukRemainingTimes +
                ", SIMLockState=" + SIMLockState +
                ", SIMLockRemainingTimes=" + SIMLockRemainingTimes +
                '}';
    }
}
