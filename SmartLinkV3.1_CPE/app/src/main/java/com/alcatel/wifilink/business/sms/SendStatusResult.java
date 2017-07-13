package com.alcatel.wifilink.business.sms;

import com.alcatel.wifilink.business.BaseResult;

public class SendStatusResult extends BaseResult{
	public int SendStatus = 0;//0 : none 1 : sending 2 : sendAgainSuccess 3 : fail still sending last message 4 : fail with Memory full  5 : fail

	
	@Override
	public void clear(){
		SendStatus = 0;
	}
}
