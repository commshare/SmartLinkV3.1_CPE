package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SambaSettings extends BaseResult{

	public int SambaStatus = 0;
	
	@Override
	public void clear() {
		SambaStatus = 0;
	}

}
