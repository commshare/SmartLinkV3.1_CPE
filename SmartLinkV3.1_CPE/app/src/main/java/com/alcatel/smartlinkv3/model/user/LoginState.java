package com.alcatel.smartlinkv3.model.user;

/**
 * Created by tao.j on 2017/6/15.
 */

public class LoginState {
    int State;
    int LoginRemainingTimes;
    int LockedRemainingTime;

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
        return "LoginState{" +
                "State=" + State +
                ", LoginRemainingTimes=" + LoginRemainingTimes +
                ", LockedRemainingTime=" + LockedRemainingTime +
                '}';
    }
}
