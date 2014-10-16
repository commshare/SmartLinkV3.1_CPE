package com.alcatel.smartlinkv3.business.statistics;

import com.alcatel.smartlinkv3.business.BaseResult;

public class UsageSettingsResult extends BaseResult{
    
	public int BillingDay = 1;//(Min:1, Max:31)
	public long CalibrationValue = 0;//Unit is byte�� Min: -100*1024*1024*1024, Max: 100��1024��1024��1024
	public long LimitValue = 0;//Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set
	public long TotalValue = 0;//Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set
	public long Overtime = 5;//Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set
	public int OvertimeState = 0;//Enable auto disconnect by overtime: 1, Disable auto disconnect by overtime:0, Default:0
	public int OverflowState = 0;//Enable auto disconnect by total flow: 1, Disable auto disconnect by total flow:0, Default:0
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		BillingDay = 1;
		CalibrationValue = 0;
		LimitValue = 0;
		TotalValue = 0;
		Overtime = 5;
		OvertimeState = 0;
		OverflowState = 0;
	}

}
