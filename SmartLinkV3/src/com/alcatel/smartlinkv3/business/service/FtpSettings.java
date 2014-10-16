package com.alcatel.smartlinkv3.business.service;

import com.alcatel.smartlinkv3.business.BaseResult;

public class FtpSettings extends BaseResult{

	public String AccessPath = new String();
	public int DevType = 0;
	public int Anonymous = 0;
	public String UserName = new String();
	public String Password = new String();
	public int AuthType = 0;
	
	@Override
	public void clear() {
		
		AccessPath = "";
		DevType = 0;
		Anonymous = 0;
		UserName = "";
		Password = "";
		AuthType = 0;
	}

}
