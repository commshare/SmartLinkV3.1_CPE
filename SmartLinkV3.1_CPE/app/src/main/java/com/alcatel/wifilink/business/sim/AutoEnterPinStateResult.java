package com.alcatel.wifilink.business.sim;

import com.alcatel.wifilink.business.BaseResult;

public class AutoEnterPinStateResult extends BaseResult{
	public int State = 0;

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		State = 0;
	}

}
