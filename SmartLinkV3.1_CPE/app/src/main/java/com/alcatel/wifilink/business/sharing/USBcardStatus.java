package com.alcatel.wifilink.business.sharing;

import com.alcatel.wifilink.business.BaseResult;

public class USBcardStatus extends BaseResult{
	public int UsbcardStatus = 0;	
	@Override
	public void clear() {
		UsbcardStatus = 0;		
	}
}
