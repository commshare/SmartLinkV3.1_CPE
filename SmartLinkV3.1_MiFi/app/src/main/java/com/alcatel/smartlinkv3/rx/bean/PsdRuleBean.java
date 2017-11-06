package com.alcatel.smartlinkv3.rx.bean;

/**
 * Created by qianli.ma on 2017/11/4 0004.
 */

public class PsdRuleBean {
    int wlanID;// AP类型(2.4G | 5G)
    int securityMode;// 加密模式(WEP | WPA)
    boolean isMatchWep;// 是否符合WEP密码规则
    boolean isMatchWpa;// 是否符合WPA密码规则

    public int getWlanID() {
        return wlanID;
    }

    public void setWlanID(int wlanID) {
        this.wlanID = wlanID;
    }

    public int getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(int securityMode) {
        this.securityMode = securityMode;
    }

    public boolean isMatchWep() {
        return isMatchWep;
    }

    public void setMatchWep(boolean matchWep) {
        isMatchWep = matchWep;
    }

    public boolean isMatchWpa() {
        return isMatchWpa;
    }

    public void setMatchWpa(boolean matchWpa) {
        isMatchWpa = matchWpa;
    }
}
