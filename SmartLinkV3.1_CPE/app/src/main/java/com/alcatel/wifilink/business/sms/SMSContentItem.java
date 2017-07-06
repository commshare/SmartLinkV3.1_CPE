package com.alcatel.wifilink.business.sms;


public class SMSContentItem{
	public int SMSId = 0;
	public int SMSType = 0;//0 : read 1 : unread 2 : sent 3 : sent failed 4 : report 5 : flash 6: draft
	public String SMSContent = new String();
	public String SMSTime = new String();//the latest SMS time.format: YYYY-MM-DDhh:mm:ss 
}
