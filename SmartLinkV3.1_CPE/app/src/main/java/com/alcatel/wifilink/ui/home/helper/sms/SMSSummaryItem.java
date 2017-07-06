package com.alcatel.wifilink.ui.home.helper.sms;

import com.alcatel.wifilink.common.ENUM.EnumSMSType;

public class SMSSummaryItem {
    public String strNumber = new String();
    public int nContactid = 0;
    public int nUnreadNumber = 0;
    public EnumSMSType enumSmsType = EnumSMSType.Read;
    public int nCount = 0;
    public String strSummaryContent = new String();
    public String strSummaryTime = new String();
    public boolean bSelected = false;
}
