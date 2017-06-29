package com.alcatel.smartlinkv3.model.sms;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSContentNew {
    
    public SMSContentList.SMSContentBean scsb;
    public boolean isDateItem;
    public int contactId;

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public SMSContentList.SMSContentBean getScsb() {
        return scsb;
    }

    public void setScsb(SMSContentList.SMSContentBean scsb) {
        this.scsb = scsb;
    }

    public boolean isDateItem() {
        return isDateItem;
    }

    public void setDateItem(boolean dateItem) {
        isDateItem = dateItem;
    }
}
