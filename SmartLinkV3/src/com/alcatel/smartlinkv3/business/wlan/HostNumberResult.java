package com.alcatel.smartlinkv3.business.wlan;

import com.alcatel.smartlinkv3.business.BaseResult;

public class HostNumberResult extends BaseResult{
	public int NumOfHosts = 0; 
	public int MaxNumOfHosts = 0;

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		NumOfHosts = 0; 
		MaxNumOfHosts = 0;
	}

}
