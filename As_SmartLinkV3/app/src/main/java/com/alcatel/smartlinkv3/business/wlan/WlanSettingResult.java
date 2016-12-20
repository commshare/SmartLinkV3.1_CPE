package com.alcatel.smartlinkv3.business.wlan;

import com.alcatel.smartlinkv3.business.BaseResult;

public class WlanSettingResult extends BaseResult{

	public int WlanAPMode = 0;  //0:2.4G; 1:5G; 2: 2.4G and 5G
	public String CountryCode = new String();
	public String Ssid = new String();//2.4GHz SSID
	public int SsidHidden = 0; //0: disable; 1: enable
	public int SecurityMode = 0; //0: disable; 	1: wep;	2: WPA;	3: WPA2; 4: WPA/WPA2	
	public int WpaType = 0; //0: TKIP	1: AES	2: AUTO
	public String WpaKey = new String();
	public int WepType = 0; //0: OPEN	1: share
	public String WepKey = new String();
	public int Channel = -1; //-1: auto-2.4G -2: auto-5G
	public int ApIsolation = 0; //0: disable	1: enable
	public int WMode = 0; //0: 802.11b; 1: 802.11b/g; 2: 802.11b/g/n; 3: Auto
	public int max_numsta = 0; //2.4 G WIFI max number client
	public String Ssid_5G = new String(); //5GHz SSID
	public int SsidHidden_5G = 0; //0: disable; 1: enable
	public int SecurityMode_5G = 0; //0: disable; 	1: wep;	2: WPA;	3: WPA2; 4: WPA/WPA2
	public int WpaType_5G = 0; //0: TKIP	1: AES	2: AUTO
	public String WpaKey_5G = new String();
	public int WepType_5G = 0; //0: OPEN	1: share
	public String WepKey_5G = new String();
	public int Channel_5G = -1; //The current used 5G WIFI Channel
	public int ApIsolation_5G = 0; //0: disable	1: enable
	public int WMode_5G = 0; //3: Auto;	4: 802.11a;	5: 802.11a/n; 6: 802.11a/c;
	public int max_numsta_5G = 0; //5G WIFI max number client
	public int curr_num = 0; //Current client count that connect to WIFI.
	
	
	public void clone(WlanSettingResult src) {	
		if(src == null)
			return ;
		WlanAPMode = src.WlanAPMode;
		Ssid = src.Ssid; 
		SsidHidden = src.SsidHidden;
	    SecurityMode = src.SecurityMode;
		CountryCode = src.CountryCode;
		WpaType = src.WpaType;
		WpaKey = src.WpaKey;
		WepType = src.WepType;
		WepKey = src.WepKey;
		Channel = src.Channel;
		ApIsolation = src.ApIsolation;
		WMode = src.WMode;
		max_numsta= src.max_numsta;
	    
	    
		Ssid_5G = src.Ssid_5G;
		SsidHidden_5G = src.SsidHidden_5G;
		SecurityMode_5G = src.SecurityMode_5G;
		WpaType_5G = src.WpaType_5G;
		WpaKey_5G = src.WpaKey_5G;
		WepType_5G = src.WepType_5G;
		WepKey_5G = src.WepKey_5G;
		Channel_5G = src.Channel_5G;
		ApIsolation_5G = src.ApIsolation_5G;
		WMode_5G = src.WMode_5G;
		max_numsta_5G= src.max_numsta_5G;
		curr_num = src.curr_num;
	}
	
	@Override
	public void clear() {
		WlanAPMode = 0;
		Ssid = ""; 
		SsidHidden = 0;
	    SecurityMode = 0;
		CountryCode = "";
		WpaType = 0;
		WpaKey = "";
		WepType = 0;
		WepKey = "";
		Channel = 0;
		ApIsolation = 0;
		WMode = 0;
		max_numsta= 0;
	    
	    
		Ssid_5G = "";
		SsidHidden_5G = 0;
		SecurityMode_5G = 0;
		WpaType_5G = 0;
		WpaKey_5G = "";
		WepType_5G = 0;
		WepKey_5G = "";
		Channel_5G = 0;
		ApIsolation_5G = 0;
		WMode_5G = 0;
		max_numsta_5G= 0;
		curr_num = 0;
	}

}
