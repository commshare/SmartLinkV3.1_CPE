package com.alcatel.smartlinkv3.rx.impl.usage;

/**
 * Created by qianli.ma on 2017/11/6 0006.
 */

public class UsageSetting {

    /**
     * AutoDisconnFlag : 0
     * BillingDay : 5
     * MonthlyPlan : 23622320128
     * TimeLimitFlag : 0
     * TimeLimitTimes : 5
     * Unit : 1
     * UsedData : 238606108
     * UsedTimes : 25
     */

    private int AutoDisconnFlag;
    private int BillingDay;
    private long MonthlyPlan;
    private int TimeLimitFlag;
    private int TimeLimitTimes;
    private int Unit;
    private long UsedData;
    private int UsedTimes;

    public int getAutoDisconnFlag() {
        return AutoDisconnFlag;
    }

    public void setAutoDisconnFlag(int AutoDisconnFlag) {
        this.AutoDisconnFlag = AutoDisconnFlag;
    }

    public int getBillingDay() {
        return BillingDay;
    }

    public void setBillingDay(int BillingDay) {
        this.BillingDay = BillingDay;
    }

    public long getMonthlyPlan() {
        return MonthlyPlan;
    }

    public void setMonthlyPlan(long MonthlyPlan) {
        this.MonthlyPlan = MonthlyPlan;
    }

    public int getTimeLimitFlag() {
        return TimeLimitFlag;
    }

    public void setTimeLimitFlag(int TimeLimitFlag) {
        this.TimeLimitFlag = TimeLimitFlag;
    }

    public int getTimeLimitTimes() {
        return TimeLimitTimes;
    }

    public void setTimeLimitTimes(int TimeLimitTimes) {
        this.TimeLimitTimes = TimeLimitTimes;
    }

    public int getUnit() {
        return Unit;
    }

    public void setUnit(int Unit) {
        this.Unit = Unit;
    }

    public long getUsedData() {
        return UsedData;
    }

    public void setUsedData(long UsedData) {
        this.UsedData = UsedData;
    }

    public int getUsedTimes() {
        return UsedTimes;
    }

    public void setUsedTimes(int UsedTimes) {
        this.UsedTimes = UsedTimes;
    }

    @Override
    public String toString() {
        return "UsageSetting{" + "AutoDisconnFlag=" + AutoDisconnFlag + ", BillingDay=" + BillingDay + ", MonthlyPlan=" + MonthlyPlan + ", TimeLimitFlag=" + TimeLimitFlag + ", TimeLimitTimes=" + TimeLimitTimes + ", Unit=" + Unit + ", UsedData=" + UsedData + ", UsedTimes=" + UsedTimes + '}';
    }
}
