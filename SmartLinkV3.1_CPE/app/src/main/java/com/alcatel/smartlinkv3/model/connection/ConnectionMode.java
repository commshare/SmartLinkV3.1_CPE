package com.alcatel.smartlinkv3.model.connection;

/**
 * Created by jianqiang.li on 2017/6/23.
 */

public class ConnectionMode {

    /**
     * ConnectMode : 1
     */

    private int ConnectMode;

    public ConnectionMode(int connectMode) {
        ConnectMode = connectMode;
    }

    public int getConnectMode() {
        return ConnectMode;
    }

    public void setConnectMode(int ConnectMode) {
        this.ConnectMode = ConnectMode;
    }
}
