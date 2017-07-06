package com.alcatel.wifilink.business.user;

import com.alcatel.wifilink.business.BaseResult;

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
