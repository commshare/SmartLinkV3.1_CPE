package com.alcatel.smartlinkv3.rx.impl.login;

/**
 * Created by qianli.ma on 2017/9/27.
 */

public class LoginState {
    /**
     * LockedRemainingTime : 0
     * LoginRemainingTimes : 5
     * State : 0
     */

    private int LockedRemainingTime;
    private int LoginRemainingTimes;
    private int State;
    private int PwEncrypt;// 1:需要加密 0:不需要加密

    public int getPwEncrypt() {
        return PwEncrypt;
    }

    public void setPwEncrypt(int pwEncrypt) {
        PwEncrypt = pwEncrypt;
    }

    public int getLockedRemainingTime() {
        return LockedRemainingTime;
    }

    public void setLockedRemainingTime(int LockedRemainingTime) {
        this.LockedRemainingTime = LockedRemainingTime;
    }

    public int getLoginRemainingTimes() {
        return LoginRemainingTimes;
    }

    public void setLoginRemainingTimes(int LoginRemainingTimes) {
        this.LoginRemainingTimes = LoginRemainingTimes;
    }

    public int getState() {
        return State;
    }

    public void setState(int State) {
        this.State = State;
    }
}
