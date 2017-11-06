package com.alcatel.smartlinkv3.rx.bean;

/**
 * Created by qianli.ma on 2017/11/3 0003.
 *
 * @function 存储WEP|WPA的初始化值
 * @call SettingwifiRxActivity
 */

public class PsdBean {
    private String wepKey;
    private int wepType;
    private String wpaKey;
    private int wpaType;

    public String getWepKey() {
        return wepKey;
    }

    public void setWepKey(String wepKey) {
        this.wepKey = wepKey;
    }

    public int getWepType() {
        return wepType;
    }

    public void setWepType(int wepType) {
        this.wepType = wepType;
    }

    public String getWpaKey() {
        return wpaKey;
    }

    public void setWpaKey(String wpaKey) {
        this.wpaKey = wpaKey;
    }

    public int getWpaType() {
        return wpaType;
    }

    public void setWpaType(int wpaType) {
        this.wpaType = wpaType;
    }

    @Override
    public String toString() {
        return "PsdBean{" + "wepKey='" + wepKey + '\'' + ", wepType=" + wepType + ", wpaKey='" + wpaKey + '\'' + ", wpaType=" + wpaType + '}';
    }
}
