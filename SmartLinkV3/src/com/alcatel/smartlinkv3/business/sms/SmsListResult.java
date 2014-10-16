package com.alcatel.smartlinkv3.business.sms;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SmsListResult extends BaseResult{
	public ArrayList<SMSMessage> SmsList = new ArrayList<SMSMessage>();
	public String Page = new String();
	public String Type = new String();
	
	@Override
	public void clear(){
		SmsList.clear();
		Page = "";
		Type = "";
	}
}
