package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SendStatusResult extends BaseResult{
	public int SendStatus = 0;//0 : none 1 : sending 2 : success 3 : fail still sending last message 4 : fail with Memory full  5 : fail

	
	@Override
	public void clear(){
		SendStatus = 0;
	}
}
