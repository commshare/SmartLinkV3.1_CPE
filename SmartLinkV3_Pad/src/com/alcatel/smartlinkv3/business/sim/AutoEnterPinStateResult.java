package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.business.BaseResult;

public class AutoEnterPinStateResult extends BaseResult{
	public int State = 0;

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		State = 0;
	}

}
