package com.alcatel.wifilink.model.sms;

/**
 * Created by qianli.ma on 2017/6/27.
 */

public class SMSContactListParam {
    public int Page;

    public SMSContactListParam(int page) {
        Page = page;
    }

    public int getPage() {
        return Page;
    }

    public void setPage(int page) {
        Page = page;
    }
}
