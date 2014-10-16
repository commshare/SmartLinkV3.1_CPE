package com.alcatel.smartlinkv3.business.calllog;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;

public class CallLogListResult extends BaseResult{
	public ArrayList<CallLog> CallLogList = new ArrayList<CallLog>();	
	@Override
	public void clear(){
		CallLogList.clear();	
	}
}
