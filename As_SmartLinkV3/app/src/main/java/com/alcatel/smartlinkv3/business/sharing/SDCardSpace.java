package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SDCardSpace extends BaseResult{
	
	public String TotalSpace = new String();
	public String UsedSpace = new String();
	
	@Override
	public void clear() {
		TotalSpace = "";
		UsedSpace = "";		
	}
}
