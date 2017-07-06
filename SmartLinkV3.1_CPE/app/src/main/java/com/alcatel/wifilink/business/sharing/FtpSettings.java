package com.alcatel.wifilink.business.sharing;

import com.alcatel.wifilink.business.BaseResult;

public class FtpSettings extends BaseResult{

	public int FtpStatus = 0;
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		FtpStatus = 0;
	}

	public int getFtpStatus(){
		return FtpStatus;
	}
}
