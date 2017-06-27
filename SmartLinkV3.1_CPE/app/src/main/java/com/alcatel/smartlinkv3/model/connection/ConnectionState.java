package com.alcatel.smartlinkv3.model.connection;

/**
 * Created by tao.j on 2017/6/16.
 */

public class ConnectionState {
    int ConnectionStatus;
    int Conprofileerror;
    String IPv4AdrressString;
    String IPv6AdrressString;
    int Speed_Dl;
    int Speed_Ul;
    int DlRate;
    int UlRate;
    int ConnectionTime;
    int UlBytes;
    int DlBytes;

    public int getConnectionStatus() {
        return ConnectionStatus;
    }

    public int getConprofileerror() {
        return Conprofileerror;
    }

    public String getIPv4AdrressString() {
        return IPv4AdrressString;
    }

    public String getIPv6AdrressString() {
        return IPv6AdrressString;
    }

    public int getSpeed_Dl() {
        return Speed_Dl;
    }

    public int getSpeed_Ul() {
        return Speed_Ul;
    }

    public int getDlRate() {
        return DlRate;
    }

    public int getUlRate() {
        return UlRate;
    }

    public int getConnectionTime() {
        return ConnectionTime;
    }

    public int getUlBytes() {
        return UlBytes;
    }

    public int getDlBytes() {
        return DlBytes;
    }
}
