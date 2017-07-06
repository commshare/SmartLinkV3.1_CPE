package com.alcatel.wifilink.business.sms;

import java.util.ArrayList;

import com.alcatel.wifilink.business.BaseResult;

public class SmsContactListResult extends BaseResult{
	public ArrayList<SMSContactItem> SMSContactList = new ArrayList<SMSContactItem>();
	public int Page = 1;
	public int TotalPageCount = 1;
	
	@Override
	public void clear(){
		SMSContactList.clear();
		Page = 1;
		TotalPageCount = 1;
	}
}
