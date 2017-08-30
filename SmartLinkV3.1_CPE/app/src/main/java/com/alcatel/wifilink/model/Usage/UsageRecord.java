package com.alcatel.wifilink.model.Usage;

/**
 * Created by qianli.ma on 2017/6/21.
 */

public class UsageRecord {

    private long TConnTimes;
    private long CurrConnTimes;
    private long HCurrUseUL;
    private long HCurrUseDL;
    private long HUseData;
    private long RCurrUseUL;
    private long RCurrUseDL;
    private long RoamUseData;
    private long MonthlyPlan;

    public long getTConnTimes() {
        return TConnTimes;
    }

    public void setTConnTimes(long TConnTimes) {
        this.TConnTimes = TConnTimes;
    }

    public long getCurrConnTimes() {
        return CurrConnTimes;
    }

    public void setCurrConnTimes(long currConnTimes) {
        CurrConnTimes = currConnTimes;
    }

    public long getHCurrUseUL() {
        return HCurrUseUL;
    }

    public void setHCurrUseUL(long HCurrUseUL) {
        this.HCurrUseUL = HCurrUseUL;
    }

    public long getHCurrUseDL() {
        return HCurrUseDL;
    }

    public void setHCurrUseDL(long HCurrUseDL) {
        this.HCurrUseDL = HCurrUseDL;
    }

    public long getHUseData() {
        return HUseData;
    }

    public void setHUseData(long HUseData) {
        this.HUseData = HUseData;
    }

    public long getRCurrUseUL() {
        return RCurrUseUL;
    }

    public void setRCurrUseUL(long RCurrUseUL) {
        this.RCurrUseUL = RCurrUseUL;
    }

    public long getRCurrUseDL() {
        return RCurrUseDL;
    }

    public void setRCurrUseDL(long RCurrUseDL) {
        this.RCurrUseDL = RCurrUseDL;
    }

    public long getRoamUseData() {
        return RoamUseData;
    }

    public void setRoamUseData(long roamUseData) {
        RoamUseData = roamUseData;
    }

    public long getMonthlyPlan() {
        return MonthlyPlan;
    }

    public void setMonthlyPlan(long monthlyPlan) {
        MonthlyPlan = monthlyPlan;
    }
}
