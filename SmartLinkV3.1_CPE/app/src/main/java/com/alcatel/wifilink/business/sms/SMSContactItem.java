package com.alcatel.wifilink.business.sms;

import java.util.ArrayList;


public class SMSContactItem{
    
	public int ContactId = 0;
	public ArrayList<String> PhoneNumber;
	public int SMSType = 0;//0 : read 1 : unread 2 : sent 3 : sent failed 4 : report 5 : flash 6: draft
	public String SMSContent = new String();//The first 40 characters of the latest SMS.
	public int SMSId = 0;//The latest SMS id.
	public String SMSTime = new String();//the latest SMS time.format: YYYY-MM-DDhh:mm:ss
    public int UnreadCount = 0;
	public int TSMSCount = 0;//The total count of the SMS that the phone number has. 
}
