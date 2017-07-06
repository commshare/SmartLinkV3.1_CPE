package com.alcatel.wifilink.business.sms;

import java.util.ArrayList;

import com.alcatel.wifilink.business.BaseResult;

public class SmsContentListResult extends BaseResult {
	public ArrayList<SMSContentItem> SMSContentList = new ArrayList<SMSContentItem>();
	public int Page = 1;
	public int ContactId = 0;
	public ArrayList<String> PhoneNumber = new ArrayList<String>();
	public int TotalPageCount = 1;
	
	@Override
	public void clear(){
		SMSContentList.clear();
		Page = 1;
		ContactId = 0;
		PhoneNumber.clear();
		TotalPageCount = 1;
	}
}
