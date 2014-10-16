package com.alcatel.smartlinkv3.business.service;

import com.alcatel.smartlinkv3.business.BaseResult;

public class DlnaSettings extends BaseResult{

	public String FriendlyName = new String();
	public String MediaDirectories = new String();	
	public int DevType = 0;
	
	@Override
	public void clear() {
		FriendlyName = "";
		MediaDirectories = "";		
		DevType = 0;		
	}
}
