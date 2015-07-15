package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

public class USBcardStatus extends BaseResult{
	public int UsbcardStatus = 0;	
	@Override
	public void clear() {
		UsbcardStatus = 0;		
	}
}
