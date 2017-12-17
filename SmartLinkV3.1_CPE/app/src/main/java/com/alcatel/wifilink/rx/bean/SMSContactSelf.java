package com.alcatel.wifilink.rx.bean;

import com.alcatel.wifilink.model.sms.SMSContactList;

/**
 * Created by qianli.ma on 2017/12/17 0017.
 */

public class SMSContactSelf {
    
    private SMSContactList.SMSContact smscontact;
    private boolean isLongClick;

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
