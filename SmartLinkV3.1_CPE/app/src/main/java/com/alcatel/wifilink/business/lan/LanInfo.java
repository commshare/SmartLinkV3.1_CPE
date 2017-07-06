package com.alcatel.wifilink.business.lan;

import com.alcatel.wifilink.business.BaseResult;

//WLAN connect information
public class LanInfo extends BaseResult {


    private int DNSMode = 0;// DNS mode --> 0:off; 1:on
    private String DNSAddress1 = "";// DNS Address1
    private String DNSAddress2 = "";// DNS Address2
    private String host_name = "";// HOST NAME

    private String IPv4IPAddress = "";//IPv4 IP address, Gateway
    private String SubnetMask = "";//Subnet Mask
    private int DHCPServerStatus = 0;/*DHCP server status 0:disable 1:enable*/
    private String StartIPAddress = "";//Start IP Address

    private String EndIPAddress = "";//End IP Address
    private int DHCPLeaseTime = 1;//DHCP Lease Time (hours):1,6,12,24
    private String MacAddress = "";//The LAN MAC Address

    @Override
    public void clear() {

        DNSMode = 0;
        DNSAddress1 = "";
        DNSAddress2 = "";
        host_name = "";

        IPv4IPAddress = "";
        SubnetMask = "";
        DHCPServerStatus = 0;
        StartIPAddress = "";

        EndIPAddress = "";
        DHCPLeaseTime = 0;
        MacAddress = "";
    }


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

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    //IPv4IPAddress
    public String getIPv4IPAddress() {
        return IPv4IPAddress;
    }

    public void setIPv4IPAddress(String strIPAddress) {
        IPv4IPAddress = strIPAddress;
    }

    //Subnet mask
    public String getSubnetMask() {
        return SubnetMask;
    }

    public void setSubnetMask(String strSubnet) {
        SubnetMask = strSubnet;
    }

    //DHCPServerStatus
    public int getDHCPServerStatus() {
        return DHCPServerStatus;
    }

    public void setDHCPServerStatus(int nDHCPServerStatus) {
        DHCPServerStatus = nDHCPServerStatus;
    }

    //StartIPAddress
    public String getStartIPAddress() {
        return StartIPAddress;
    }

    public void setStartIPAddress(String strStartIPAddress) {
        StartIPAddress = strStartIPAddress;
    }

    //EndIPAddress
    public String getEndIPAddress() {
        return EndIPAddress;
    }

    public void setEndIPAddress(String strEndIPAddress) {
        EndIPAddress = strEndIPAddress;
    }

    //DHCPLeaseTime
    public int getDHCPLeaseTime() {
        return DHCPLeaseTime;
    }

    public void setDHCPLeaseTime(int nDHCPLeaseTime) {
        DHCPLeaseTime = nDHCPLeaseTime;
    }

    //MacAddress
    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String strMacAddress) {
        MacAddress = strMacAddress;
    }

    @Override
    public String toString() {
        return "LanInfo{" + "DNSMode=" + DNSMode + ", DNSAddress1='" + DNSAddress1 + '\'' + ", DNSAddress2='" + DNSAddress2 + '\'' + ", host_name='" + host_name + '\'' + ", IPv4IPAddress='" + IPv4IPAddress + '\'' + ", SubnetMask='" + SubnetMask + '\'' + ", DHCPServerStatus=" + DHCPServerStatus + ", StartIPAddress='" + StartIPAddress + '\'' + ", EndIPAddress='" + EndIPAddress + '\'' + ", DHCPLeaseTime=" + DHCPLeaseTime + ", MacAddress='" + MacAddress + '\'' + '}';
    }
}
