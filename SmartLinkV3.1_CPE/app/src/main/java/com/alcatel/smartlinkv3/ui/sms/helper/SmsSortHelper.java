package com.alcatel.smartlinkv3.ui.sms.helper;

import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.model.sms.SMSContentList;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by qianli.ma on 2017/6/29.
 */

public class SmsSortHelper implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        SMSContentList.SMSContentBean c1 = (SMSContentList.SMSContentBean) o1;
        SMSContentList.SMSContentBean c2 = (SMSContentList.SMSContentBean) o2;
        Date d1 = DataUti.formatDateFromString(c1.getSMSTime());
        Date d2 = DataUti.formatDateFromString(c2.getSMSTime());
        if (d1.after(d2))
            return 1;
        if (d1.equals(d2))
            return 0;
        return -1;
    }
}
