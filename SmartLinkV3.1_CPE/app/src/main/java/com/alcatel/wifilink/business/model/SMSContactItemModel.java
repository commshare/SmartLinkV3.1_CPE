package com.alcatel.wifilink.business.model;

import java.util.ArrayList;

import com.alcatel.wifilink.business.sms.SMSContactItem;
import com.alcatel.wifilink.common.ENUM.EnumSMSType;


public class SMSContactItemModel{
    
	public int ContactId = 0;
	public ArrayList<String> PhoneNumber;
	public EnumSMSType SMSType = EnumSMSType.Read;//0 : read 1 : unread 2 : sent 3 : sent failed 4 : report 5 : flash 6: draft
	public String SMSContent = new String();//The first 40 characters of the latest SMS.
	public int SMSId = 0;//The latest SMS id.
	public String SMSTime = new String();//the latest SMS time.format: YYYY-MM-DDhh:mm:ss
    public int UnreadCount = 0;
	public int TSMSCount = 0;//The total count of the SMS that the phone number has. 
	
	public void buildFromResult(SMSContactItem result) {
		ContactId = result.ContactId;
		PhoneNumber = (ArrayList<String>) result.PhoneNumber.clone();
		SMSType = EnumSMSType.build(result.SMSType);
		SMSContent = result.SMSContent;
		SMSId = result.SMSId;
		SMSTime = result.SMSTime;
		UnreadCount = result.UnreadCount;
		TSMSCount = result.TSMSCount;
	}
}
