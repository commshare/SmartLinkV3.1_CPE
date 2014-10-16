package com.alcatel.smartlinkv3.business.user;

import com.alcatel.smartlinkv3.business.BaseResult;

public class LogoutResult extends BaseResult{
	private String UserName = new String();
	private String Tokens = new String();

	public String getTokens() {
		return Tokens;
	}

	public void setTokens(String tokens) {
		Tokens = tokens;
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		UserName = "";
		Tokens = "";
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}
}
