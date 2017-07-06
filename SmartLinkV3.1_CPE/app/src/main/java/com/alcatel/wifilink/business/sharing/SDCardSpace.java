package com.alcatel.wifilink.business.sharing;

import com.alcatel.wifilink.business.BaseResult;

public class SDCardSpace extends BaseResult{
	
	public String TotalSpace = new String();
	public String UsedSpace = new String();
	
	@Override
	public void clear() {
		TotalSpace = "";
		UsedSpace = "";		
	}
}
