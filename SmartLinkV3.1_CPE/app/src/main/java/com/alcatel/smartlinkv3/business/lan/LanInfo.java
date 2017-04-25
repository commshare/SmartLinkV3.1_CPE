package com.alcatel.smartlinkv3.business.lan;

import com.alcatel.smartlinkv3.business.BaseResult;

//WLAN connect information
public class LanInfo extends BaseResult{

	private String IPv4IPAddress="";//IPv4 IP address, Gateway
	private String SubnetMask="";//Subnet Mask
	private int DHCPServerStatus = 0;/*DHCP server status 0:disable 1:enable*/
	private String StartIPAddress="";//Start IP Address
	private String EndIPAddress="";//End IP Address
	private int DHCPLeaseTime=1;//DHCP Lease Time (hours):1,6,12,24
	private String MacAddress= "";//The LAN MAC Address
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		IPv4IPAddress = "";
		SubnetMask="";
		DHCPServerStatus=0;
		StartIPAddress="";
		EndIPAddress="";
		DHCPLeaseTime=0;
		MacAddress="";
	}
	//IPv4IPAddress
	public String getIPv4IPAddress(){
		return IPv4IPAddress;
	}

	public void setIPv4IPAddress(String strIPAddress){
		IPv4IPAddress = strIPAddress;
	}
	//Subnet mask
	public String getSubnetMask(){
		return SubnetMask;
	}

	public void setSubnetMask(String strSubnet){
		SubnetMask = strSubnet;
	}

	//DHCPServerStatus
	public int getDHCPServerStatus(){
		return DHCPServerStatus;
	}

	public void setDHCPServerStatus(int nDHCPServerStatus){
		DHCPServerStatus = nDHCPServerStatus;
	}

	//StartIPAddress
	public String getStartIPAddress(){
		return StartIPAddress;
	}

	public void setStartIPAddress(String strStartIPAddress){
		StartIPAddress = strStartIPAddress;
	}
	//EndIPAddress
	public String getEndIPAddress(){
		return EndIPAddress;
	}

	public void setEndIPAddress(String strEndIPAddress){
		EndIPAddress = strEndIPAddress;
	}

	//DHCPLeaseTime
	public int getDHCPLeaseTime(){
		return DHCPLeaseTime;
	}

	public void setDHCPLeaseTime(int nDHCPLeaseTime){
		DHCPLeaseTime = nDHCPLeaseTime;
	}

	//MacAddress
	public String getMacAddress(){
		return MacAddress;
	}

	public void setMacAddress(String strMacAddress){
		MacAddress = strMacAddress;
	}
}
