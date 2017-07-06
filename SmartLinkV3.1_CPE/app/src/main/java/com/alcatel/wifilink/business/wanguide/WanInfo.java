package com.alcatel.wifilink.business.wanguide;

import com.alcatel.wifilink.business.BaseResult;

/**
 * Created by qianli.ma on 2017/5/24.
 */

public class WanInfo extends BaseResult {

    private String SubNetMask = "";
    private String Gateway = "";
    private String IpAddress = "";
    private int Mtu = 1500;// Default : 1500 do not change if you not nesssary
    private int ConnectType = 0;// 0:DHCP ; 1:PPPoE ; 2:Static IP
    private String PrimaryDNS = "";
    private String SecondaryDNS = "";
    private String Account = "";
    private String Password = "";
    private String Status = "";
    private String StaticIpAddress = "";
    private int pppoeMtu = 1492;// State in PPPOE will be 1492


    @Override
    protected void clear() {
        SubNetMask = "";
        Gateway = "";
        IpAddress = "";
        Mtu = 1500;
        ConnectType = 0;
        PrimaryDNS = "";
        SecondaryDNS = "";
        Account = "";
        Password = "";
        Status = "";
        StaticIpAddress = "";
        pppoeMtu = 1492;
    }

    public String getSubNetMask() {
        return SubNetMask;
    }

    public void setSubNetMask(String subNetMask) {
        SubNetMask = subNetMask;
    }

    public String getGateway() {
        return Gateway;
    }

    public void setGateway(String gateway) {
        Gateway = gateway;
    }

    public String getIpAddress() {
        return IpAddress;
    }

    public void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }

    public int getMtu() {
        return Mtu;
    }

    public void setMtu(int mtu) {
        Mtu = mtu;
    }

    public int getConnectType() {
        return ConnectType;
    }

    public void setConnectType(int connectType) {
        ConnectType = connectType;
    }

    public String getPrimaryDNS() {
        return PrimaryDNS;
    }

    public void setPrimaryDNS(String primaryDNS) {
        PrimaryDNS = primaryDNS;
    }

    public String getSecondaryDNS() {
        return SecondaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS) {
        SecondaryDNS = secondaryDNS;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStaticIpAddress() {
        return StaticIpAddress;
    }

    public void setStaticIpAddress(String staticIpAddress) {
        StaticIpAddress = staticIpAddress;
    }

    public int getPppoeMtu() {
        return pppoeMtu;
    }

    public void setPppoeMtu(int pppoeMtu) {
        this.pppoeMtu = pppoeMtu;
    }

    @Override
    public String toString() {
        return "WanInfo{" + "SubNetMask='" + SubNetMask + '\'' + ", Gateway='" + Gateway + '\'' + ", IpAddress='" + IpAddress + '\'' + ", Mtu=" + Mtu + ", ConnectType=" + ConnectType + ", PrimaryDNS='" + PrimaryDNS + '\'' + ", SecondaryDNS='" + SecondaryDNS + '\'' + ", Account='" + Account + '\'' + ", Password='" + Password + '\'' + ", Status='" + Status + '\'' + ", StaticIpAddress='" + StaticIpAddress + '\'' + ", pppoeMtu=" + pppoeMtu + '}';
    }
}
