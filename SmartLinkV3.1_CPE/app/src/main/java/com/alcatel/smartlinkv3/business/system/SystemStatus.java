package com.alcatel.smartlinkv3.business.system;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SystemStatus extends BaseResult{
	private int NetworkType = 0;  //Network Type	0: No service	1: 2G	2: 3G	3: 4G
	private int SignalStrength = 0; //RSSI value:	0: level 0	1: level 1	2: level 2	3: level 3	4: level 4
	private int WlanState = 0; //Wlan status	0: off	1: on	2:WPS
	private int Ap2GStatus = 0;//2G Wlan status 0: off 1: on
	private int Ap5GStatus = 0;//5G Wlan status 0: off 1: on
	private int ConnectionStatus = 0; //0: disconnected	1: connecting	2:connected	3:disconnecting
	private int barrery = 0;  //0: disconnected	1: connected
	private int Roaming = 0;  //0: no 1:Roaming
	private int curr_num = 0;	//Current WIFI client

	/**
	 * usb status
	 0: Not Insert
	 1: USB storage
	 2: USB print
	 */
	private int UsbStatus;	//usb status
	/**
	 * USB disk name
	 */
	private String UsbName;

	@Override
	public void clear(){
		NetworkType = 0;
		SignalStrength = 0;
		WlanState = 0;
		Ap2GStatus = 0;
		Ap5GStatus = 0;
		ConnectionStatus = 0;
		barrery = 0;
		Roaming = 0;
		curr_num = 0;
	}
	public int getCurrNum() {
		return curr_num;
	}

	public int getRoamingStatus() {
		return Roaming;
	}
	
	public int getBarreryStatus(String deviceName) {
		return barrery;
	}
	public int getConnectionStatus() {
		return ConnectionStatus;
	}

	public void setWlanState(int wlanState) {
		WlanState = wlanState;
	}

	public int getWlanState() {
		return WlanState;
	}

	public int getAp2GStatus() {
		return Ap2GStatus;
	}

	public void setAp2GStatus(int ap2GStatus) {
		Ap2GStatus = ap2GStatus;
	}

	public int getAp5GStatus() {
		return Ap5GStatus;
	}

	public void setAp5GStatus(int ap5GStatus) {
		Ap5GStatus = ap5GStatus;
	}

	public int getSignalStrength() {
		return SignalStrength;
	}
	public int getNetworkType() {
		return NetworkType;
	}
	public int getUsbStatus() {
		return UsbStatus;
	}

	public void setUsbStatus(int usbStatus) {
		UsbStatus = usbStatus;
	}

	public String getUsbName() {
		return UsbName;
	}

	public void setUsbName(String usbName) {
		UsbName = usbName;
	}
}
