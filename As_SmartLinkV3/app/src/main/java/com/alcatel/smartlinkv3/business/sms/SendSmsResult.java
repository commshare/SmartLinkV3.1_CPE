package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SendSmsResult extends BaseResult{
	public int SmsSendId = 0;//The Sms send id, it will be used when you get the sending result
	
	@Override
	public void clear(){
		SmsSendId = 0;
	}
}
