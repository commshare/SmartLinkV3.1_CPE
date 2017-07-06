package com.alcatel.wifilink.business.wan;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.business.model.ConnectionSettingsModel;
import com.alcatel.wifilink.common.ENUM;

public class ConnectionSettingsResult extends BaseResult {

    public int IdleTime = 0;//Connection max time (option)
    public int RoamingConnect = 0; //Under Roaming status if can connect:0: when roaming can not connect 1: when roaming can connect
    public int ConnectMode = 0; //0: manual connect 1: auto connect
    public int PdpType = 0; //PDP connection type: PDP_TYPE_IPv4 = 0, PDP_TYPE_PPP = 1, PDP_TYPE_IPv6 = 2, PDP_TYPE_IPv4v6 =3


    @Override
    protected void clear() {
        // TODO Auto-generated method stub
        IdleTime = 0;
        RoamingConnect = 0;
        ConnectMode = 0;
        PdpType = 0;
    }

    public void clone(ConnectionSettingsModel src) {
        if (src == null)
            return;
        IdleTime = src.HIdleTime;
        RoamingConnect = ENUM.OVER_ROAMING_STATE.antiBuild(src.HRoamingConnect);
        ConnectMode = src.HConnectMode;
        PdpType = ENUM.PdpType.antiBuild(src.HPdpType);
    }

    public void setValue(ConnectionSettingsResult result) {
        IdleTime = result.IdleTime;
        RoamingConnect = result.RoamingConnect;
        ConnectMode = result.ConnectMode;
        PdpType = result.PdpType;

    }

}
