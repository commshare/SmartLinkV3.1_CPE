package com.alcatel.wifilink.model.sms;

import com.alcatel.wifilink.ui.home.helper.cons.Cons;

public class SMSDetailItem {

    public boolean bIsDateItem = false;
    public String strContent = new String();
    public String strTime = new String();
    public int eSMSType = Cons.READ;
    public int nContactID = 0;
    public long nSMSID = 0;
    public boolean selectFlag;

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public boolean isbIsDateItem() {
        return bIsDateItem;
    }

    public void setbIsDateItem(boolean bIsDateItem) {
        this.bIsDateItem = bIsDateItem;
    }

    public String getStrContent() {
        return strContent;
    }

    public void setStrContent(String strContent) {
        this.strContent = strContent;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public int geteSMSType() {
        return eSMSType;
    }

    public void seteSMSType(int eSMSType) {
        this.eSMSType = eSMSType;
    }

    public int getnContactID() {
        return nContactID;
    }

    public void setnContactID(int nContactID) {
        this.nContactID = nContactID;
    }

    public long getnSMSID() {
        return nSMSID;
    }

    public void setnSMSID(long nSMSID) {
        this.nSMSID = nSMSID;
    }
}

  
