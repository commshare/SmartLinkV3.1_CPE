package com.alcatel.wifilink.network;

/**
 * Created by qianli.ma on 2017/11/24 0024.
 */

public class NetworkRegisterState {

    // 0: not regiseter
    // 1: registting
    // 2: register successful
    // 3: registration failed

    private int regist_state;

    public int getRegist_state() {
        return regist_state;
    }

    public void setRegist_state(int regist_state) {
        this.regist_state = regist_state;
    }
}
