package com.alcatel.smartlinkv3.business.statistics;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.business.BaseResult;

public class UsageSettingsResult extends BaseResult{
	
	public int HBillingDay = 0;//Billing day (Min:1, Max:31),
	public int HMonthlyPlan = 0;//The max data that on month could use..
	public int HUsedData = 0;//The used data in month.
	public int HTimeLimitFlag = 0;//The time limit function flage,0: disable 1:enable
	public int HTimeLimitTimes = 0;//The time limit function open, must set the limit time.
	public int HUsedTimes = 0;//The used time that after open time limit function.
	public int HAutoDisconnFlag = 0;//This flage control the disconnection when the usage settings get conditions.0:disable, not auto disconnect 1:enable, auto disconnect

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		HBillingDay = 0;
		HMonthlyPlan = 0;
		HUsedData = 0;
		HTimeLimitFlag = 0;
		HTimeLimitTimes = 0;
		HUsedTimes = 0;
		HAutoDisconnFlag = 0;
	}
	
	public void clone(UsageSettingsResult src) {	
		if(src == null)
			return ;
		HBillingDay = src.HBillingDay;
		HMonthlyPlan = src.HMonthlyPlan;
		HUsedData = src.HUsedData;
		HTimeLimitFlag = src.HTimeLimitFlag;
		HTimeLimitTimes = src.HTimeLimitTimes;
		HUsedTimes = src.HUsedTimes;
		HAutoDisconnFlag = src.HAutoDisconnFlag;
	}
	
	public void setValue(UsageSettingsResult result) {	
		HBillingDay = result.HBillingDay;
		HMonthlyPlan = result.HMonthlyPlan;
		HUsedData = result.HUsedData;
		HTimeLimitFlag = result.HTimeLimitFlag;
		HTimeLimitTimes = result.HTimeLimitTimes;
		HUsedTimes = result.HUsedTimes;
		HAutoDisconnFlag = result.HAutoDisconnFlag;
	}
}
