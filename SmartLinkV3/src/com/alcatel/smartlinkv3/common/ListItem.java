package com.alcatel.smartlinkv3.common;

import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.WIFIConnectStatus;

public class ListItem {
	public static class MM100WifiLstItem {
		public int id = 0;
		public String  ssid = new String(); 
		public SecurityMode securityMode = SecurityMode.Disable;
		public int signalStrength = 1;
		public WIFIConnectStatus wifiConnectStatus = WIFIConnectStatus.Disconnected;
		public String channel = new String();
		public String encrypt = new String();
		public String tkip_aes = new String();
		
		public MM100WifiLstItem() {	
			id = 0;	
			ssid = "";
			securityMode = SecurityMode.Disable;
			signalStrength = 1;
			wifiConnectStatus = WIFIConnectStatus.Disconnected;
			channel = "";
			encrypt = "";
			tkip_aes = "";
		}
		
		public void clone(MM100WifiLstItem src) {
			id = src.id;	
			ssid = src.ssid;
			securityMode = src.securityMode;
			signalStrength = src.signalStrength;
			wifiConnectStatus = src.wifiConnectStatus;
			channel = src.channel;
			encrypt = src.encrypt;
			tkip_aes = src.tkip_aes;
		}
	}
}
