package com.alcatel.wifilink.model.wlan;

/**
 * Created by yaodong.zhang on 2017/8/1.
 */

public class LanSettings {

    /**
     * DNSMode : 0
     * DNSAddress1 :
     * DNSAddress2 :
     * IPv4IPAddress : 192.168.1.1
     * host_name : plus10.home
     * SubnetMask : 255.255.255.0
     * DHCPServerStatus : 1
     * StartIPAddress : 192.168.1.100
     * EndIPAddress : 192.168.1.200
     * DHCPLeaseTime : 12
     */

    private int DNSMode;
    private String DNSAddress1;
    private String DNSAddress2;
    private String IPv4IPAddress;
    private String host_name;
    private String SubnetMask;
    private int DHCPServerStatus;
    private String StartIPAddress;
    private String EndIPAddress;
    private int DHCPLeaseTime;

    public int getDNSMode() {
        return DNSMode;
    }

    public void setDNSMode(int DNSMode) {
        this.DNSMode = DNSMode;
    }

    public String getDNSAddress1() {
        return DNSAddress1;
    }

    public void setDNSAddress1(String DNSAddress1) {
        this.DNSAddress1 = DNSAddress1;
    }

    public String getDNSAddress2() {
        return DNSAddress2;
    }

    public void setDNSAddress2(String DNSAddress2) {
        this.DNSAddress2 = DNSAddress2;
    }

    public String getIPv4IPAddress() {
        return IPv4IPAddress;
    }

    public void setIPv4IPAddress(String IPv4IPAddress) {
        this.IPv4IPAddress = IPv4IPAddress;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getSubnetMask() {
        return SubnetMask;
    }

    public void setSubnetMask(String SubnetMask) {
        this.SubnetMask = SubnetMask;
    }

    public int getDHCPServerStatus() {
        return DHCPServerStatus;
    }

    public void setDHCPServerStatus(int DHCPServerStatus) {
        this.DHCPServerStatus = DHCPServerStatus;
    }

    public String getStartIPAddress() {
        return StartIPAddress;
    }

    public void setStartIPAddress(String StartIPAddress) {
        this.StartIPAddress = StartIPAddress;
    }

    public String getEndIPAddress() {
        return EndIPAddress;
    }

    public void setEndIPAddress(String EndIPAddress) {
        this.EndIPAddress = EndIPAddress;
    }

    public int getDHCPLeaseTime() {
        return DHCPLeaseTime;
    }

    public void setDHCPLeaseTime(int DHCPLeaseTime) {
        this.DHCPLeaseTime = DHCPLeaseTime;
    }
}
