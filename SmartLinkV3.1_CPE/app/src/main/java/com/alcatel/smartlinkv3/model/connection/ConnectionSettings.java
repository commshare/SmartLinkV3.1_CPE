package com.alcatel.smartlinkv3.model.connection;

/**
 * Created by tao.j on 2017/6/16.
 */

public class ConnectionSettings {
    int ConnectMode;
    int RoamingConnect;
    int PdpType;
    int ConnOffTime;

    public ConnectionSettings() {

    }

    public ConnectionSettings(int connectMode, int roamingConnect, int pdpType, int connOffTime) {
        ConnectMode = connectMode;
        RoamingConnect = roamingConnect;
        PdpType = pdpType;
        ConnOffTime = connOffTime;
    }

    public int getConnectMode() {
        return ConnectMode;
    }

    public void setConnectMode(int connectMode) {
        ConnectMode = connectMode;
    }

    public int getRoamingConnect() {
        return RoamingConnect;
    }

    public void setRoamingConnect(int roamingConnect) {
        RoamingConnect = roamingConnect;
    }

    public int getPdpType() {
        return PdpType;
    }

    public void setPdpType(int pdpType) {
        PdpType = pdpType;
    }

    public int getConnOffTime() {
        return ConnOffTime;
    }

    public void setConnOffTime(int connOffTime) {
        ConnOffTime = connOffTime;
    }
}
