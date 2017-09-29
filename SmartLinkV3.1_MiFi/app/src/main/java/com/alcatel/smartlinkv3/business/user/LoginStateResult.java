package com.alcatel.smartlinkv3.business.user;

import com.alcatel.smartlinkv3.business.BaseResult;

public class LoginStateResult extends BaseResult {
    private int State = 0;//0: logout	1: login	2:the login times used out. 

    @Override
    protected void clear() {
        State = 0;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }
}
