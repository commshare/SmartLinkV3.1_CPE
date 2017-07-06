package com.alcatel.wifilink.model.Usage;

/**
 * Created by qianli.ma on 2017/6/21.
 */

public class UsageRecord {

    public int TConnTimes;
    public int CurrConnTimes;
    public int HCurrUseUL;
    public int HCurrUseDL;
    public int HUseData;
    public int RCurrUseUL;
    public int RCurrUseDL;
    public int RoamUseData;
    public int MonthlyPlan;

    public int getTConnTimes() {
        return TConnTimes;
    }

    public void setTConnTimes(int TConnTimes) {
        this.TConnTimes = TConnTimes;
    }

    public int getCurrConnTimes() {
        return CurrConnTimes;
    }

    public void setCurrConnTimes(int currConnTimes) {
        CurrConnTimes = currConnTimes;
    }

    public int getHCurrUseUL() {
        return HCurrUseUL;
    }

    public void setHCurrUseUL(int HCurrUseUL) {
        this.HCurrUseUL = HCurrUseUL;
    }

    public int getHCurrUseDL() {
        return HCurrUseDL;
    }

    public void setHCurrUseDL(int HCurrUseDL) {
        this.HCurrUseDL = HCurrUseDL;
    }

    public int getHUseData() {
        return HUseData;
    }

    public void setHUseData(int HUseData) {
        this.HUseData = HUseData;
    }

    public int getRCurrUseUL() {
        return RCurrUseUL;
    }

    public void setRCurrUseUL(int RCurrUseUL) {
        this.RCurrUseUL = RCurrUseUL;
    }

    public int getRCurrUseDL() {
        return RCurrUseDL;
    }

    public void setRCurrUseDL(int RCurrUseDL) {
        this.RCurrUseDL = RCurrUseDL;
    }

    public int getRoamUseData() {
        return RoamUseData;
    }

    public void setRoamUseData(int roamUseData) {
        RoamUseData = roamUseData;
    }

    public int getMonthlyPlan() {
        return MonthlyPlan;
    }

    public void setMonthlyPlan(int monthlyPlan) {
        MonthlyPlan = monthlyPlan;
    }

    @Override
    public String toString() {
        return "UsageRecord{" + "TConnTimes=" + TConnTimes + ", CurrConnTimes=" + CurrConnTimes + ", HCurrUseUL=" + HCurrUseUL + ", HCurrUseDL=" + HCurrUseDL + ", HUseData=" + HUseData + ", RCurrUseUL=" + RCurrUseUL + ", RCurrUseDL=" + RCurrUseDL + ", RoamUseData=" + RoamUseData + ", MonthlyPlan=" + MonthlyPlan + '}';
    }
}
