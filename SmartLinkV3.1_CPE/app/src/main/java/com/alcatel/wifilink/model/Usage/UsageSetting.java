package com.alcatel.wifilink.model.Usage;

/**
 * Created by qianli.ma on 2017/6/20.
 */

public class UsageSetting {

    /**
     * BillingDay : 1
     * MonthlyPlan : 0
     * UsedData : 0
     * Unit : 1
     * TimeLimitFlag : 0
     * TimeLimitTimes : 5
     * UsedTimes : 0
     * AutoDisconnFlag : 0
     */

    private int BillingDay;
    private long MonthlyPlan;
    private int UsedData;
    private int Unit;
    private int TimeLimitFlag;
    private int TimeLimitTimes;
    private int UsedTimes;
    private int AutoDisconnFlag;

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

    public int getUsedData() {
        return UsedData;
    }

    public void setUsedData(int UsedData) {
        this.UsedData = UsedData;
    }

    public int getUnit() {
        return Unit;
    }

    public void setUnit(int Unit) {
        this.Unit = Unit;
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

    public int getUsedTimes() {
        return UsedTimes;
    }

    public void setUsedTimes(int UsedTimes) {
        this.UsedTimes = UsedTimes;
    }

    public int getAutoDisconnFlag() {
        return AutoDisconnFlag;
    }

    public void setAutoDisconnFlag(int AutoDisconnFlag) {
        this.AutoDisconnFlag = AutoDisconnFlag;
    }

    @Override
    public String toString() {
        return "UsageSetting{" +
                "BillingDay=" + BillingDay +
                ", MonthlyPlan=" + MonthlyPlan +
                ", UsedData=" + UsedData +
                ", Unit=" + Unit +
                ", TimeLimitFlag=" + TimeLimitFlag +
                ", TimeLimitTimes=" + TimeLimitTimes +
                ", UsedTimes=" + UsedTimes +
                ", AutoDisconnFlag=" + AutoDisconnFlag +
                '}';
    }
}
