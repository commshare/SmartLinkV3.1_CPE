package com.alcatel.wifilink.business.wanguide;

/**
 * Created by qianli.ma on 2017/5/27.
 */

public class StatusBean {

    private int status = 0;// 连接状态
    private WanInfo wanInfo;// WAN对象

    public StatusBean() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public WanInfo getWanInfo() {
        return wanInfo;
    }

    public void setWanInfo(WanInfo wanInfo) {
        this.wanInfo = wanInfo;
    }
}
