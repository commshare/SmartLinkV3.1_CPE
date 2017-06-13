package com.alcatel.smartlinkv3.business.wlan;

import com.alcatel.smartlinkv3.business.BaseResult;

import java.util.ArrayList;
import java.util.List;


public class WlanNewSettingResult extends BaseResult {
    public int WlanAPMode = 0;  //0:2.4G; 1:5G; 2: 2.4G and 5G
    public int curr_num = 0; //Current client count that connect to WIFI.
//    public List<AP> APList = new ArrayList<AP>();

    public AP AP2G = new AP();
    public AP AP5G = new AP();

    public void clone(WlanNewSettingResult src) {
        if (src == null)
            return;
        WlanAPMode = src.WlanAPMode;
        curr_num = src.curr_num;
    }

    @Override
    public void clear() {
        WlanAPMode = 0;
        curr_num = 0;
//        this.APList.clear();
    }
}


