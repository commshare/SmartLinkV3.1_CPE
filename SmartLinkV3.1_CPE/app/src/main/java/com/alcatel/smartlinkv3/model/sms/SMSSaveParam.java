package com.alcatel.smartlinkv3.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSSaveParam {

    private int SMSId;
    private String SMSContent;
    private String SMSTime;
    private List<String> PhoneNumber;

    public int getSMSId() {
        return SMSId;
    }

    public void setSMSId(int SMSId) {
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
