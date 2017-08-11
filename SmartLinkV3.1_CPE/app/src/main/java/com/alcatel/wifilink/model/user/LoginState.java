package com.alcatel.wifilink.model.user;

/**
 * Created by tao.j on 2017/6/15.
 */

public class LoginState {

    private int State;
    private int LoginRemainingTimes;
    private int LockedRemainingTime;
    private int PwEncrypt;// 1:需要加密 0:不需要加密

    public int getPwEncrypt() {
        return PwEncrypt;
    }

    public void setPwEncrypt(int pwEncrypt) {
        PwEncrypt = pwEncrypt;
    }

    /**
     * get login state
     *
     * @return 0: logout 1: login 2: the login times used out.
     */
    public int getState() {
        return State;
    }

    public int getLoginRemainingTimes() {
        return LoginRemainingTimes;
    }

    public int getLockedRemainingTime() {
        return LockedRemainingTime;
    }

    @Override
    public String toString() {
        return "LoginState{" + "State=" + State + ", LoginRemainingTimes=" + LoginRemainingTimes + ", LockedRemainingTime=" + LockedRemainingTime + '}';
    }
}
