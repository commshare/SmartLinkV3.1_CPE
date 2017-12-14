package com.alcatel.wifilink.model.Usage;

/**
 * Created by qianli.ma on 2017/12/8 0008.
 */

public class UsageSettings {

    private int BillingDay;
    private long MonthlyPlan;
    private long UsedData;
    private int Unit;
    private int TimeLimitFlag;
    private int TimeLimitTimes;
    private int UsedTimes;
    private int AutoDisconnFlag;

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
