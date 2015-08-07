package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

public class DlnaSettings extends BaseResult{

	
	public int DlnaStatus = 0; // 0: disable; 1: enable
	public String DlnaName = new String();
	
	@Override
	public void clear() {
		DlnaStatus = 0;
		DlnaName = "";		
	}
	
	public int getDlnaStatus(){
		return DlnaStatus;
	}
}
