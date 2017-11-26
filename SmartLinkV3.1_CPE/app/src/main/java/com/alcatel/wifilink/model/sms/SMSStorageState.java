package com.alcatel.wifilink.model.sms;

/**
 * Created by qianli.ma on 2017/11/22 0022.
 */

public class SMSStorageState {

    private int UnreadReport;
    private int LeftCount;
    private int MaxCount;
    private int TUseCount;
    private int UnreadSMSCount;

    public int getUnreadReport() {
        return UnreadReport;
    }

    public void setUnreadReport(int UnreadReport) {
        this.UnreadReport = UnreadReport;
    }

    public int getLeftCount() {
        return LeftCount;
    }

    public void setLeftCount(int LeftCount) {
        this.LeftCount = LeftCount;
    }

    public int getMaxCount() {
        return MaxCount;
    }

    public void setMaxCount(int MaxCount) {
        this.MaxCount = MaxCount;
    }

    public int getTUseCount() {
        return TUseCount;
    }

    public void setTUseCount(int TUseCount) {
        this.TUseCount = TUseCount;
    }

    public int getUnreadSMSCount() {
        return UnreadSMSCount;
    }

    public void setUnreadSMSCount(int UnreadSMSCount) {
        this.UnreadSMSCount = UnreadSMSCount;
    }
}
