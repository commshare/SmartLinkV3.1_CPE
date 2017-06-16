package com.alcatel.smartlinkv3.model.connection;

/**
 * Created by tao.j on 2017/6/16.
 */

public class ConnectionState {
    String ConnectionStatus;
    String Conprofileerror;
    String IPv4AdrressString;
    String IPv6AdrressString;
    String Speed_Dl;
    String Speed_Ul;
    String DlRate;
    String UlRate;
    String ConnectionTime;
    String UlBytes;
    String DlBytes;

    public String getConnectionStatus() {
        return ConnectionStatus;
    }

    public String getConprofileerror() {
        return Conprofileerror;
    }

    public String getIPv4AdrressString() {
        return IPv4AdrressString;
    }

    public String getIPv6AdrressString() {
        return IPv6AdrressString;
    }

    public String getSpeed_Dl() {
        return Speed_Dl;
    }

    public String getSpeed_Ul() {
        return Speed_Ul;
    }

    public String getDlRate() {
        return DlRate;
    }

    public String getUlRate() {
        return UlRate;
    }

    public String getConnectionTime() {
        return ConnectionTime;
    }

    public String getUlBytes() {
        return UlBytes;
    }

    public String getDlBytes() {
        return DlBytes;
    }
}
