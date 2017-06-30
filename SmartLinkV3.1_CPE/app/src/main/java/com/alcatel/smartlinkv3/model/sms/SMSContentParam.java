package com.alcatel.smartlinkv3.model.sms;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSContentParam {

    public  int Page;
    public int ContactId;

    public SMSContentParam(int page, int contactId) {
        Page = page;
        ContactId = contactId;
    }

    public int getPage() {
        return Page;
    }

    public void setPage(int Page) {
        this.Page = Page;
    }

    public int getContactId() {
        return ContactId;
    }

    public void setContactId(int ContactId) {
        this.ContactId = ContactId;
    }
}
