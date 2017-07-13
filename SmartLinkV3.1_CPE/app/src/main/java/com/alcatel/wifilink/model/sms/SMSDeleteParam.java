package com.alcatel.wifilink.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSDeleteParam {


    public int DelFlag;
    public List<Long> SMSArray;

    public SMSDeleteParam(int delFlag, List<Long> SMSArray) {
        DelFlag = delFlag;
        this.SMSArray = SMSArray;
    }

    public int getDelFlag() {
        return DelFlag;
    }

    public void setDelFlag(int DelFlag) {
        this.DelFlag = DelFlag;
    }

    public List<Long> getSMSArray() {
        return SMSArray;
    }

    public void setSMSArray(List<Long> SMSArray) {
        this.SMSArray = SMSArray;
    }
}
