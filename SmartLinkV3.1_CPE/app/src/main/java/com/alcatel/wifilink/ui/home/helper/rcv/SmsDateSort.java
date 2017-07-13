package com.alcatel.wifilink.ui.home.helper.rcv;

import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.model.sms.SMSContactList;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by qianli.ma on 2017/7/11.
 */

public class SmsDateSort implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        SMSContactList.SMSContact s1 = (SMSContactList.SMSContact) o1;
        SMSContactList.SMSContact s2 = (SMSContactList.SMSContact) o2;
        Date d1 = DataUti.formatDateFromString(s1.getSMSTime());
        Date d2 = DataUti.formatDateFromString(s2.getSMSTime());
        if (d1.after(d2))
            return 1;
        if (d1.equals(d2))
            return 0;
        return -1;
    }
}
