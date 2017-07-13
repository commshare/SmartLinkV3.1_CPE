package com.alcatel.wifilink.model.sms;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSContentParam {

    public int Page;
    public long ContactId;

    public long getContactId() {
        return ContactId;
    }

    public void setContactId(long contactId) {
        ContactId = contactId;
    }

    public SMSContentParam(int page, long contactId) {
        Page = page;
        ContactId = contactId;
    }

    public int getPage() {
        return Page;
    }

    public void setPage(int Page) {
        this.Page = Page;
    }

}
