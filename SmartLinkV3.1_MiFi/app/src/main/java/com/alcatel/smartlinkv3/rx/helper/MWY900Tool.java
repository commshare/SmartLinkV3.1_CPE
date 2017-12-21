package com.alcatel.smartlinkv3.rx.helper;

import com.alcatel.smartlinkv3.rx.bean.WlanSettingForY900;
import com.alcatel.smartlinkv3.rx.impl.wlan.WlanResult;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianli.ma on 2017/12/21 0021.
 */

public class MWY900Tool {
    /**
     * 转换Y900的wlan对象为MW40的新格式
     *
     * @param wlY900 Y900制式
     * @return
     */
    public static WlanResult transferY900WlanToMW(WlanSettingForY900 wlY900, int wlanState) {

        Logger.t("ma_wifi").v("wlY900:" + wlY900.toString());
        WlanResult wlMW = new WlanResult();
        wlMW.setCurr_num(wlY900.getCurr_num());
        wlMW.setWlanAPMode(wlY900.getWlanAPMode());
        wlMW.setWiFiOffTime(0);// 旧版没有,任意设置

        /* create 2.4G */
        WlanResult.APListBean apMW_2P4G = new WlanResult.APListBean();
        apMW_2P4G.setWMode(wlY900.getWMode());
        apMW_2P4G.setSsid(wlY900.getSsid());
        apMW_2P4G.setSsidHidden(wlY900.getSsidHidden());
        apMW_2P4G.setChannel(wlY900.getChannel());
        apMW_2P4G.setMax_numsta(wlY900.getMax_numsta());
        apMW_2P4G.setSecurityMode(wlY900.getSecurityMode());
        apMW_2P4G.setWepType(wlY900.getWepType());
        apMW_2P4G.setWepKey(wlY900.getWepKey());
        apMW_2P4G.setWpaType(wlY900.getWpaType());
        apMW_2P4G.setWpaKey(wlY900.getWpaKey());
        apMW_2P4G.setCountryCode(wlY900.getCountryCode());
        apMW_2P4G.setApIsolation(wlY900.getApIsolation());
        apMW_2P4G.setCurr_num(wlY900.getCurr_num());
        apMW_2P4G.setBandwidth(2);// 旧版没有,任意设置
        apMW_2P4G.setCurChannel(0);// 旧版没有,任意设置
        apMW_2P4G.setWlanAPID(Cons._2P4G);// 旧版没有--> 但这个必须为2.4G
        // wlanstatus为打开状态并且当前模式为2.4G--> 则MW的2.4G的APSTATUS有效
        boolean is2P4Status = wlanState == Cons.ON & (wlY900.getWlanAPMode() == Cons._2P4G | wlY900.getWlanAPMode() == Cons._2P4G_5G);
        apMW_2P4G.setApStatus(is2P4Status ? Cons.ENABLE : Cons.DISABLE);

        /* create 5G */
        WlanResult.APListBean apMW_5G = new WlanResult.APListBean();
        apMW_5G.setWMode(wlY900.getWMode_5G());
        apMW_5G.setSsid(wlY900.getSsid_5G());
        apMW_5G.setSsidHidden(wlY900.getSsidHidden_5G());
        apMW_5G.setChannel(wlY900.getChannel_5G());
        apMW_5G.setMax_numsta(wlY900.getMax_numsta_5G());
        apMW_5G.setSecurityMode(wlY900.getSecurityMode_5G());
        apMW_5G.setWepType(wlY900.getWepType_5G());
        apMW_5G.setWepKey(wlY900.getWepKey_5G());
        apMW_5G.setWpaType(wlY900.getWpaType_5G());
        apMW_5G.setWpaKey(wlY900.getWpaKey_5G());
        apMW_5G.setCountryCode(wlY900.getCountryCode_5G());
        apMW_5G.setApIsolation(wlY900.getApIsolation_5G());
        apMW_5G.setCurr_num(wlY900.getCurr_num());
        apMW_5G.setBandwidth(2);// 旧版没有,任意设置
        apMW_5G.setCurChannel(0);// 旧版没有,任意设置
        apMW_5G.setWlanAPID(Cons._5G);// 旧版没有--> 但这个必须为2.4G
        // wlanstatus为打开状态并且当前模式为2.4G--> 则MW的2.4G的APSTATUS有效
        boolean is5GStatus = wlanState == Cons.ON & (wlY900.getWlanAPMode() == Cons._5G | wlY900.getWlanAPMode() == Cons._2P4G_5G);
        apMW_5G.setApStatus(is5GStatus ? Cons.ENABLE : Cons.DISABLE);

        List<WlanResult.APListBean> apList = new ArrayList<>();
        apList.add(apMW_2P4G);
        apList.add(apMW_5G);

        wlMW.setAPList(apList);
        return wlMW;
    }

    /**
     * 转换MW40的wlan对象为Y900的老格式
     *
     * @param wlMW
     * @return
     */
    public static WlanSettingForY900 transferMWToY900(WlanResult wlMW) {
        // 区分2.4G以及5G的AP
        WlanResult.APListBean ap2P4 = null;
        WlanResult.APListBean ap5G = null;
        for (WlanResult.APListBean ap : wlMW.getAPList()) {
            if (ap.getWlanAPID() == Cons._2P4G) {
                ap2P4 = ap;
            }
            if (ap.getWlanAPID() == Cons._5G) {
                ap5G = ap;
            }
        }
        // 新建一个wlY900
        WlanSettingForY900 wlY900 = new WlanSettingForY900();
        wlY900.setWlanAPMode(wlMW.getWlanAPMode());// WlanApMode(1)
        wlY900.setCurr_num(wlMW.getCurr_num());// curr_Num(1)
        // 针对2.4G的赋值(12)
        if (ap2P4 != null) {
            wlY900.setWMode(ap2P4.getWMode());
            wlY900.setSsid(ap2P4.getSsid());
            wlY900.setSsidHidden(ap2P4.getSsidHidden());
            wlY900.setChannel(ap2P4.getChannel());
            wlY900.setMax_numsta(ap2P4.getMax_numsta());
            wlY900.setSecurityMode(ap2P4.getSecurityMode());
            wlY900.setWepType(ap2P4.getWepType());
            wlY900.setWepKey(ap2P4.getWepKey());
            wlY900.setWpaType(ap2P4.getWpaType());
            wlY900.setWpaKey(ap2P4.getWpaKey());
            wlY900.setCountryCode(ap2P4.getCountryCode());
            wlY900.setApIsolation(ap2P4.getApIsolation());
        }
        // 针对5G的赋值(12)
        if (ap5G != null) {
            wlY900.setWMode_5G(ap5G.getWMode());
            wlY900.setSsid_5G(ap5G.getSsid());
            wlY900.setSsidHidden_5G(ap5G.getSsidHidden());
            wlY900.setChannel_5G(ap5G.getChannel());
            wlY900.setMax_numsta_5G(ap5G.getMax_numsta());
            wlY900.setSecurityMode_5G(ap5G.getSecurityMode());
            wlY900.setWepType_5G(ap5G.getWepType());
            wlY900.setWepKey_5G(ap5G.getWepKey());
            wlY900.setWpaType_5G(ap5G.getWpaType());
            wlY900.setWpaKey_5G(ap5G.getWpaKey());
            wlY900.setCountryCode_5G(ap5G.getCountryCode());
            wlY900.setApIsolation_5G(ap5G.getApIsolation());
        }

        return wlY900;
    }
}
