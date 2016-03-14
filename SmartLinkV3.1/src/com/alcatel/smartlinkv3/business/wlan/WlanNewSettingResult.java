package com.alcatel.smartlinkv3.business.wlan;
import java.util.ArrayList;
import java.util.List;

import android.renderscript.Type;

import com.alcatel.smartlinkv3.business.BaseResult;



public class WlanNewSettingResult extends BaseResult
{
	public int WlanAPMode = 0;  //0:2.4G; 1:5G; 2: 2.4G and 5G
	public int curr_num = 0; //Current client count that connect to WIFI.
	public List<ApList> APList= new ArrayList<ApList>();
	
	public void clone(WlanNewSettingResult src) 
	{	
		if(src == null)
			return ;
		WlanAPMode = src.WlanAPMode;
		curr_num = src.curr_num;
	}
	
	@Override
	public void clear()
	{
		WlanAPMode = 0;
		curr_num = 0;
		this.APList.clear();
	}


}


class ApList 
{
	public int WlanAPID = 0;
	public int  ApStatus = 1;
	public int WMode = 0; //0: 802.11b; 1: 802.11b/g; 2: 802.11b/g/n; 3: Auto
	public String Ssid = new String();//2.4GHz SSID
	public int SsidHidden = 0; //0: disable; 1: enable
	public int Channel = -1; //-1: auto-2.4G -2: auto-5G
	public int SecurityMode = 0; //0: disable; 	1: wep;	2: WPA;	3: WPA2; 4: WPA/WPA2	
	public int WepType = 0; //0: OPEN	1: share
	public String WepKey = new String();
	public int WpaType = 0; //0: TKIP	1: AES	2: AUTO
	public String WpaKey = new String();
	public String CountryCode = new String();
	public int ApIsolation = 0; //0: disable	1: enable
	public int max_numsta = 0; //2.4 G WIFI max number client
	public int curr_num = 0; //Current client count that connect to WIFI.
	
	

}



