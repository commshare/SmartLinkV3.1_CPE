package com.alcatel.smartlinkv3.business.wlan;

public class AP {
   public int WlanAPID = 0;
   public int ApStatus = 1;
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

   @Override
   public String toString() {
       return "AP{" +
               "WlanAPID=" + WlanAPID +
               ", ApStatus=" + ApStatus +
               ", WMode=" + WMode +
               ", Ssid='" + Ssid + '\'' +
               ", SsidHidden=" + SsidHidden +
               ", Channel=" + Channel +
               ", SecurityMode=" + SecurityMode +
               ", WepType=" + WepType +
               ", WepKey='" + WepKey + '\'' +
               ", WpaType=" + WpaType +
               ", WpaKey='" + WpaKey + '\'' +
               ", CountryCode='" + CountryCode + '\'' +
               ", ApIsolation=" + ApIsolation +
               ", max_numsta=" + max_numsta +
               ", curr_num=" + curr_num +
               '}';
   }


}
