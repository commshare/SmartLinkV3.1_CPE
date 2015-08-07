package com.alcatel.smartlinkv3.business.wlan;

import com.alcatel.smartlinkv3.business.BaseResult;

public class WlanSupportModeType extends BaseResult {

	private int WlanAPMode = 0;
	@Override
	protected void clear() {
		// TODO Auto-generated method stub

		WlanAPMode = 0;
	}
	
	public int getWlanSupportMode(){
		return WlanAPMode;
	}
	
	public void setWlanSupportMode(int nSupportMode){
		WlanAPMode = nSupportMode;
	}

}
