package com.alcatel.smartlinkv3.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSDeleteParam {


    private int DelFlag;
    private List<Integer> SMSArray;

    public SMSDeleteParam(int delFlag, List<Integer> SMSArray) {
        DelFlag = delFlag;
        this.SMSArray = SMSArray;
    }

    public int getDelFlag() {
        return DelFlag;
    }

    public void setDelFlag(int DelFlag) {
        this.DelFlag = DelFlag;
    }

    public List<Integer> getSMSArray() {
        return SMSArray;
    }

    public void setSMSArray(List<Integer> SMSArray) {
        this.SMSArray = SMSArray;
    }
}
