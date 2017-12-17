package com.alcatel.wifilink.business.model;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.business.statistics.UsageSettingsResult;
import com.alcatel.wifilink.common.ENUM;

public class UsageSettingModel extends BaseResult {

    public int HBillingDay = 0;//Billing day (Min:1, Max:31),
    public long HMonthlyPlan = 0;//The max data that on month could use..
    public int HUnit = 0;
    public long HUsedData = 0;//The used data in month.
    public int HUsageAlertValue = 0;//Alert Value (Min:0, Max:100)
    public int HTimeLimitTimes = 0;//The time limit function open, must set the limit time.
    public int HUsedTimes = 0;//The used time that after open time limit function.
    public ENUM.OVER_TIME_STATE HTimeLimitFlag = ENUM.OVER_TIME_STATE.Disable;  //The time limit function flage,0: disable 1:enable
    public ENUM.OVER_DISCONNECT_STATE HAutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.Disable;//This flage control the disconnection when the usage settings getInstant conditions.0:disable, not auto disconnect 1:enable, auto disconnect


    @Override
    public void clear() {
        // TODO Auto-generated method stub
        HBillingDay = 0;
        HMonthlyPlan = 0;
        HUnit = 0;
        HUsedData = 0;
        HUsageAlertValue = 0;
        HTimeLimitFlag = ENUM.OVER_TIME_STATE.Disable;
        HTimeLimitTimes = 0;
        HUsedTimes = 0;
        HAutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.Disable;
    }

    public void clone(UsageSettingModel src) {
        if (src == null)
            return;
        HBillingDay = src.HBillingDay;
        HMonthlyPlan = src.HMonthlyPlan;
        HUnit = src.HUnit;
        HUsedData = src.HUsedData;
        HUsageAlertValue = src.HUsageAlertValue;
        HTimeLimitFlag = src.HTimeLimitFlag;
        HTimeLimitTimes = src.HTimeLimitTimes;
        HUsedTimes = src.HUsedTimes;
        HAutoDisconnFlag = src.HAutoDisconnFlag;
    }

    public void setValue(UsageSettingsResult result) {
        HBillingDay = result.BillingDay;
        HMonthlyPlan = result.MonthlyPlan;
        HUnit = result.Unit;
        HUsedData = result.UsedData;
        HUsageAlertValue = result.UsageAlertValue;
        HTimeLimitFlag = ENUM.OVER_TIME_STATE.build(result.TimeLimitFlag);
        HTimeLimitTimes = result.TimeLimitTimes;
        HUsedTimes = result.UsedTimes;
        HAutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.build(result.AutoDisconnFlag);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        final UsageSettingModel o = (UsageSettingModel) obj;

        if (o.HBillingDay != this.HBillingDay ||
            o.HMonthlyPlan != this.HMonthlyPlan ||
            o.HUsedData != this.HUsedData ||
            o.HUsageAlertValue != this.HUsageAlertValue ||
            o.HTimeLimitFlag != this.HTimeLimitFlag ||
            o.HTimeLimitTimes != this.HTimeLimitTimes ||
            o.HUsedTimes != this.HUsedTimes ||
            o.HAutoDisconnFlag != this.HAutoDisconnFlag)
        {
            return false;
        }

        return true;
    }
}
