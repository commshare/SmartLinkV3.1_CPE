package com.alcatel.smartlinkv3.business.statistics;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;

public class UsageSettingsResult extends BaseResult{
	
	public int BillingDay = 0;//Billing day (Min:1, Max:31),
	public long MonthlyPlan = 0;//The max data that on month could use..
	public int Unit = 0; //The unit function flage,0: MB 1: GB
	public long UsedData = 0;//The used data in month.
	public int TimeLimitFlag = 0;//The time limit function flage,0: disable 1:enable
	public int TimeLimitTimes = 0;//The time limit function open, must set the limit time.
	public int UsedTimes = 0;//The used time that after open time limit function.
	public int AutoDisconnFlag = 0;//This flage control the disconnection when the usage settings get conditions.0:disable, not auto disconnect 1:enable, auto disconnect

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		BillingDay = 0;
		MonthlyPlan = 0;
		Unit = 0;
		UsedData = 0;
		TimeLimitFlag = 0;
		TimeLimitTimes = 0;
		UsedTimes = 0;
		AutoDisconnFlag = 0;
	}
	
	public void clone(UsageSettingModel src) {	
		if(src == null)
			return ;
		BillingDay = src.HBillingDay;
		MonthlyPlan = src.HMonthlyPlan;
		Unit = src.HUnit;
		UsedData = src.HUsedData;
		TimeLimitFlag = ENUM.OVER_TIME_STATE.antiBuild(src.HTimeLimitFlag);
		TimeLimitTimes = src.HTimeLimitTimes;
		UsedTimes = src.HUsedTimes;
		AutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.antiBuild(src.HAutoDisconnFlag);
	}
	
	public void setValue(UsageSettingsResult result) {	
		BillingDay = result.BillingDay;
		MonthlyPlan = result.MonthlyPlan;
		Unit = result.Unit;
		UsedData = result.UsedData;
		TimeLimitFlag = result.TimeLimitFlag;
		TimeLimitTimes = result.TimeLimitTimes;
		UsedTimes = result.UsedTimes;
		AutoDisconnFlag = result.AutoDisconnFlag;
	}
}
