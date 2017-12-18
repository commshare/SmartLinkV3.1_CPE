package com.alcatel.wifilink.rx.bean;

import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.model.sms.SMSContactList;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by qianli.ma on 2017/12/17 0017.
 */

public class SMSContactSelf {

    private SMSContactList.SMSContact smscontact;
    private boolean isLongClick;
    private int state = 0;// CLICK,SELECT ALL,DESELECT ALL

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public SMSContactList.SMSContact getSmscontact() {
        return smscontact;
    }

    public void setSmscontact(SMSContactList.SMSContact smscontact) {
        this.smscontact = smscontact;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

}
