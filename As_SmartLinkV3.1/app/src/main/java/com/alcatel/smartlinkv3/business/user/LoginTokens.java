package com.alcatel.smartlinkv3.business.user;

import com.alcatel.smartlinkv3.business.BaseResult;

public class LoginTokens extends BaseResult{
	
	private String Login = new String();
	
	@Override
	public void clear() {
		Login = "";
	}

	
	public String getLogin() {
		return Login;
	}

}
