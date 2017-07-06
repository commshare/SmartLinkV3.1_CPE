package com.alcatel.wifilink.business.model;

import java.util.ArrayList;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.business.sms.SmsContactListResult;

public class SmsContactMessagesModel extends BaseResult {
    public ArrayList<SMSContactItemModel> SMSContactList = new ArrayList<SMSContactItemModel>();
    public int Page = 1;
    public int TotalPageCount = 1;

    @Override
    public void clear() {
        SMSContactList.clear();
        Page = 1;
        TotalPageCount = 1;
    }

    public SmsContactMessagesModel clone() {
        SmsContactMessagesModel cloneObj = new SmsContactMessagesModel();
        cloneObj.SMSContactList = SMSContactList;
        cloneObj.Page = this.Page;
        cloneObj.TotalPageCount = this.TotalPageCount;
        return cloneObj;
    }

    public void bulidFromResult(SmsContactListResult result) {
        SMSContactList.clear();
        if (result == null)
            return;
        for (int i = 0; i < result.SMSContactList.size(); i++) {
            SMSContactItemModel model = new SMSContactItemModel();
            model.buildFromResult(result.SMSContactList.get(i));
            SMSContactList.add(model);
        }
        Page = result.Page;
        TotalPageCount = result.TotalPageCount;
    }
}
