package com.alcatel.smartlinkv3.business.user;

import com.alcatel.smartlinkv3.business.BaseResult;

public class LoginStateResult extends BaseResult{
	private int State = 0;//0: logout	1: some body login	2:logined

	private String Tokens = new String();

	@Override
	protected void clear() {		
		State = 0;
		Tokens = "";
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public String getTokens() {
		return Tokens;
	}

	public void setTokens(String tokens) {
		Tokens = tokens;
	}
}
