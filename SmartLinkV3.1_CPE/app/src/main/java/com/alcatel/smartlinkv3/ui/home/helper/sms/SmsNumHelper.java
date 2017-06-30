package com.alcatel.smartlinkv3.ui.home.helper.sms;

import java.util.List;

/**
 * 处理号码集合
 */
public class SmsNumHelper {

    public static String getNew(List<String> phones) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < phones.size(); i++) {
            if (i == phones.size() - 1) {
                sb.append(phones.get(i));
            } else {
                sb.append(phones.get(i)).append(";");
            }
        }
        return sb.toString();
    }

}
