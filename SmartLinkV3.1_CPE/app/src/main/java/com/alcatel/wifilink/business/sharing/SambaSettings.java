package com.alcatel.wifilink.business.sharing;

import com.alcatel.wifilink.business.BaseResult;

public class SambaSettings extends BaseResult{

	public int SambaStatus = 0;
	
	@Override
	public void clear() {
		SambaStatus = 0;
	}

}
