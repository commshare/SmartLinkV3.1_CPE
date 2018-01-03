package com.alcatel.wifilink.model.wan;

/**
 * Created by tao.j on 2017/6/21.
 */

public class WanSettingsResult {

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
    private int DurationTime;
    private String WanType;


    public String getSubNetMask() {
        return SubNetMask;
    }

    public void setSubNetMask(String SubNetMask) {
        this.SubNetMask = SubNetMask;
    }

    public String getGateway() {
        return Gateway;
    }

    public void setGateway(String Gateway) {
        this.Gateway = Gateway;
    }

    public String getIpAddress() {
        return IpAddress;
    }

    public void setIpAddress(String IpAddress) {
        this.IpAddress = IpAddress;
    }

    public int getMtu() {
        return Mtu;
    }

    public void setMtu(int Mtu) {
        this.Mtu = Mtu;
    }

    public int getConnectType() {
        return ConnectType;
    }

    public void setConnectType(int ConnectType) {
        this.ConnectType = ConnectType;
    }

    public String getPrimaryDNS() {
        return PrimaryDNS;
    }

    public void setPrimaryDNS(String PrimaryDNS) {
        this.PrimaryDNS = PrimaryDNS;
    }

    public String getSecondaryDNS() {
        return SecondaryDNS;
    }

    public void setSecondaryDNS(String SecondaryDNS) {
        this.SecondaryDNS = SecondaryDNS;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String Account) {
        this.Account = Account;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getStaticIpAddress() {
        return StaticIpAddress;
    }

    public void setStaticIpAddress(String StaticIpAddress) {
        this.StaticIpAddress = StaticIpAddress;
    }

    public int getPppoeMtu() {
        return pppoeMtu;
    }

    public void setPppoeMtu(int pppoeMtu) {
        this.pppoeMtu = pppoeMtu;
    }

    public int getDurationTime() {
        return DurationTime;
    }

    public void setDurationTime(int DurationTime) {
        this.DurationTime = DurationTime;
    }

    public String getWanType() {
        return WanType;
    }

    public void setWanType(String WanType) {
        this.WanType = WanType;
    }

    @Override
    public String toString() {
        return "WanSettings{" + "\n" + "SubNetMask='" + SubNetMask + '\'' + "\n" + ", Gateway='" + Gateway + '\'' + "\n" + ", IpAddress='" + IpAddress + '\'' + "\n" + ", Mtu=" + Mtu + "\n" + ", ConnectType=" + ConnectType + "\n" + ", PrimaryDNS='" + PrimaryDNS + '\'' + "\n" + ", SecondaryDNS='" + SecondaryDNS + '\'' + "\n" + ", Account='" + Account + '\'' + "\n" + ", Password='" + Password + '\'' + "\n" + ", Status=" + Status + "\n" + ", StaticIpAddress='" + StaticIpAddress + '\'' + "\n" + ", pppoeMtu=" + pppoeMtu + "\n" + ", DurationTime=" + DurationTime + "\n" + ", WanType='" + WanType + '\'' + "\n" + '}';
    }
}
