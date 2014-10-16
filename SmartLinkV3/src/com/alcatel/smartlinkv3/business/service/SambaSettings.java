package com.alcatel.smartlinkv3.business.service;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SambaSettings extends BaseResult{
	
	public String HostName = new String();
	public String Description = new String();
	public String WorkGroup = new String();
	public String AccessPath = new String();
	public int DevType = 0;
	public int Anonymous = 0;
	public String UserName = new String();
	public String Password = new String();
	public int AuthType = 0;
	
	@Override
	public void clear() {
		HostName = "";
		Description = "";
		WorkGroup = "";
		AccessPath = "";
		DevType = 0;
		Anonymous = 0;
		UserName = "";
		Password = "";
		AuthType = 0;
	}

}
