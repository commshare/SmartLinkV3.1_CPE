package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SDCardSpace extends BaseResult{
	
	public long TotalSpace = 0;
	public long UsedSpace = 0;
	
	@Override
	public void clear() {
		TotalSpace = 0;
		UsedSpace = 0;		
	}
}
