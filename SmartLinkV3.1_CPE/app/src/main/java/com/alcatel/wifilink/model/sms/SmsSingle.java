package com.alcatel.wifilink.model.sms;

/**
 * Created by qianli.ma on 2017/7/12.
 */

public class SmsSingle {

    private String PhoneNumber;
    private int SMSType;
    private String SMSContent;
    private String SMSId;
    private String SMSTime;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public int getSMSType() {
        return SMSType;
    }

    public void setSMSType(int SMSType) {
        this.SMSType = SMSType;
    }

    public String getSMSContent() {
        return SMSContent;
    }

    public void setSMSContent(String SMSContent) {
        this.SMSContent = SMSContent;
    }

    public String getSMSId() {
        return SMSId;
    }

    public void setSMSId(String SMSId) {
        this.SMSId = SMSId;
    }

    public String getSMSTime() {
        return SMSTime;
    }

    public void setSMSTime(String SMSTime) {
        this.SMSTime = SMSTime;
    }
}
