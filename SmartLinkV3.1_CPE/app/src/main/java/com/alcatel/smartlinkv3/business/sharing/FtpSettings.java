package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.business.BaseResult;

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
