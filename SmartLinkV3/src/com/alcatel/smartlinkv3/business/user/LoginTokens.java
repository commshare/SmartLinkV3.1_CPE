package com.alcatel.smartlinkv3.business.user;

import com.alcatel.smartlinkv3.business.BaseResult;

public class LoginTokens extends BaseResult{
	
	private String Login = new String();
	private String Tokens = new String();
	
	@Override
	public void clear() {
		Tokens = "";
		Login = "";
	}

	public String getTokens() {
		return Tokens;
	}
	
	public String getLogin() {
		return Login;
	}

}
