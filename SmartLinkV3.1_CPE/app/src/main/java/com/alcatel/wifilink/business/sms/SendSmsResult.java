package com.alcatel.wifilink.business.sms;

import com.alcatel.wifilink.business.BaseResult;

public class SendSmsResult extends BaseResult{
	public int SmsSendId = 0;//The Sms send id, it will be used when you getInstant the sending result
	
	@Override
	public void clear(){
		SmsSendId = 0;
	}
}
