package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SmsStorageStateResult extends BaseResult{
	public int MaxCount = 0;//The current max space (SMS Count) thatstore the SMS.
    public int TUseCount = 0;//Current store SMS total count.
    public int UnreadSMSCount = 0;//Current store unread SMS count. 
	
	@Override
	public void clear(){
		MaxCount = 0;
		TUseCount = 0;
		UnreadSMSCount = 0; 		
	}
}
