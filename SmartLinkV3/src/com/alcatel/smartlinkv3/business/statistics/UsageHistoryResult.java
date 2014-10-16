package com.alcatel.smartlinkv3.business.statistics;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;

public class UsageHistoryResult extends BaseResult{
	public ArrayList<UsageHistoryItem> UsageHistoryList = new ArrayList<UsageHistoryItem>();
	
	@Override
	public void clear(){
		UsageHistoryList.clear();
	}
}
