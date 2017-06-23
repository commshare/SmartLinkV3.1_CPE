package com.alcatel.smartlinkv3.model.Usage;

/**
 * Created by qianli.ma on 2017/6/20.
 */

public class UsageSetting {

    public int BillingDay = 0;//Billing day (Min:1, Max:31),
    public long MonthlyPlan = 0;//The max data that on month could use..
    public long UsedData = 0;//The used data in month.
    public int Unit = 0;//  The unit function flage 0: MB 1: GB 2: KB
    public int TimeLimitFlag = 0;// The time limit function flage --> 0: disable 1:enable
    public int TimeLimitTimes = 0;// The time limit function open, must set the limit time.(单位为分钟)
    public int UsedTimes = 0;// The used time that after open time limit function.(单位为分钟)
    public int AutoDisconnFlag = 0;// This flage control the disconnection when the usage settings get conditions. 0: disable, 
    // not auto disconnect 1: enable, auto disconnect


    public int getBillingDay() {
        return BillingDay;
    }

    public void setBillingDay(int billingDay) {
        BillingDay = billingDay;
    }

    public long getMonthlyPlan() {
        return MonthlyPlan;
    }

    public void setMonthlyPlan(long monthlyPlan) {
        MonthlyPlan = monthlyPlan;
    }

    public long getUsedData() {
        return UsedData;
    }

    public void setUsedData(long usedData) {
        UsedData = usedData;
    }

    public int getUnit() {
        return Unit;
    }

    public void setUnit(int unit) {
        Unit = unit;
    }

    public int getTimeLimitFlag() {
        return TimeLimitFlag;
    }

    public void setTimeLimitFlag(int timeLimitFlag) {
        TimeLimitFlag = timeLimitFlag;
    }

    public int getTimeLimitTimes() {
        return TimeLimitTimes;
    }

    public void setTimeLimitTimes(int timeLimitTimes) {
        TimeLimitTimes = timeLimitTimes;
    }

    public int getUsedTimes() {
        return UsedTimes;
    }

    public void setUsedTimes(int usedTimes) {
        UsedTimes = usedTimes;
    }

    public int getAutoDisconnFlag() {
        return AutoDisconnFlag;
    }

    public void setAutoDisconnFlag(int autoDisconnFlag) {
        AutoDisconnFlag = autoDisconnFlag;
    }
}
