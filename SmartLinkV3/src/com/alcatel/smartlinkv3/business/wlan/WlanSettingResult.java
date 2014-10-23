package com.alcatel.smartlinkv3.business.wlan;

import com.alcatel.smartlinkv3.business.BaseResult;

public class WlanSettingResult extends BaseResult{

	public int WlanFrequency = 0;  //0:Disable; 1:2.4GHz; 2: 5GHz
	public String Ssid5G = new String(); //5GHz SSID
	public String Ssid = new String();//2.4GHz SSID
	public int SsidHidden5G = 0; //0: disable; 1: enable
	public int SsidHidden = 0; //0: disable; 1: enable
	public String CountryCode = new String();
	public int SecurityMode = 0; //0: disable; 	1: wep;	2: WPA;	3: WPA2; 4: WPA/WPA2	
	public int WpaType = 0; //0: TKIP	1: AES	2: AUTO
	public String WpaKey = new String();
	public int WepType = 0; //0: OPEN	1: share
	public String WepKey = new String();
	public int Channel = -1; //-1: auto-2.4G -2: auto-5G
	public int ApIsolation5G = 0; //0: disable	1: enable
	public int ApIsolation = 0; //0: disable	1: enable
	public int Bandwidth = 0; //0: disable	1: enable
	public int WMode = 0; //0: auto	1: 802.11a	2: 802.11b	3: 802.11g	4: 802.11a+n	5: 802.11g+n
	public String MacAddress = new String();
	public int NumOfHosts = 0; 
	public int MaxNumOfHosts = 0;
	
	
	public void clone(WlanSettingResult src) {	
		if(src == null)
			return ;
		WlanFrequency = src.WlanFrequency; 
		Ssid5G = src.Ssid5G;
		Ssid = src.Ssid;
		SsidHidden5G = src.SsidHidden5G;
		SsidHidden = src.SsidHidden;
		CountryCode = src.CountryCode;
	    SecurityMode = src.SecurityMode;
		WpaType = src.WpaType;
		WpaKey = src.WpaKey;
		WepType = src.WepType;
		WepKey = src.WepKey;
		Channel = src.Channel;
		ApIsolation5G = src.ApIsolation5G;
		ApIsolation = src.ApIsolation;
		Bandwidth = src.Bandwidth;
		WMode = src.WMode;
		MacAddress = src.MacAddress;
		NumOfHosts = src.NumOfHosts;
		MaxNumOfHosts = src.MaxNumOfHosts;
	}
	
	@Override
	public void clear() {		
		WlanFrequency = 0; 
		Ssid5G ="";
		Ssid = "";
		SsidHidden5G = 0; 
		SsidHidden = 0; 
		CountryCode = "";
	    SecurityMode = 0; 
		WpaType = 0;
		WpaKey = "";
		WepType = 0; 
		WepKey = "";
		Channel = -1;
		ApIsolation5G = 0; 
		ApIsolation = 0;
		Bandwidth = 0;
		WMode = 0;
		MacAddress = "";
		NumOfHosts = 0; 
		MaxNumOfHosts = 0;
	}

}
