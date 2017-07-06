package com.alcatel.wifilink.model.wan;

/**
 * Created by tao.j on 2017/6/22.
 */

public class WanSettingsParams {


    private String SubNetMask;
    private String Gateway;
    private String IpAddress;
    private int Mtu;
    private int ConnectType;
    private String PrimaryDNS;
    private String SecondaryDNS;
    private String Account;
    private String Password;
    private int Status;
    private String StaticIpAddress;
    private int pppoeMtu;

    public void setSubNetMask(String subNetMask) {
        SubNetMask = subNetMask;
    }

    public void setGateway(String gateway) {
        Gateway = gateway;
    }

    public void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }

    public void setMtu(int mtu) {
        Mtu = mtu;
    }

    public void setConnectType(int connectType) {
        ConnectType = connectType;
    }

    public void setPrimaryDNS(String primaryDNS) {
        PrimaryDNS = primaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS) {
        SecondaryDNS = secondaryDNS;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setStaticIpAddress(String staticIpAddress) {
        StaticIpAddress = staticIpAddress;
    }

    public void setPppoeMtu(int pppoeMtu) {
        this.pppoeMtu = pppoeMtu;
    }
}
