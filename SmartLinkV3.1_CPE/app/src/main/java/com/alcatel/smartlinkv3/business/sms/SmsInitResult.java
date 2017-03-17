package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SmsInitResult extends BaseResult{
	public int Status = 0;//The status of the SMS initing.0:Complete; 1:Initing

	
	@Override
	public void clear(){
		Status = 0;
	}
}
