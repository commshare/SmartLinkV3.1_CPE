package com.alcatel.wifilink.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSSaveParam {

    public long SMSId;
    public String SMSContent;
    public String SMSTime;
    public List<String> PhoneNumber;


    public SMSSaveParam(long SMSId, String SMSContent, String SMSTime, List<String> phoneNumber) {
        this.SMSId = SMSId;
        this.SMSContent = SMSContent;
        this.SMSTime = SMSTime;
        PhoneNumber = phoneNumber;
    }

    public SMSSaveParam() {
    }

    public long getSMSId() {
        return SMSId;
    }

    public void setSMSId(long SMSId) {
        this.SMSId = SMSId;
    }

    public String getSMSContent() {
        return SMSContent;
    }

    public void setSMSContent(String SMSContent) {
        this.SMSContent = SMSContent;
    }

    public String getSMSTime() {
        return SMSTime;
    }

    public void setSMSTime(String SMSTime) {
        this.SMSTime = SMSTime;
    }

    public List<String> getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(List<String> PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }
}
