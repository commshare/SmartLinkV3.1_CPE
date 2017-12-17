package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alcatel.wifilink.business.lan.HttpLan;
import com.alcatel.wifilink.business.lan.LanInfo;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;

public class LanManager extends BaseManager {

    private LanInfo m_lanInfo = new LanInfo();
    private static final String TAG = "LanManager";

    public LanManager(Context context) {
        super(context);
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {
                getLaninfo(null);
            } else {
                clearData();
                BusinessManager.getInstance().getSystemInfoModel().clear();
            }
        }
    }

    @Override
    protected void clearData() {
        // TODO Auto-generated method stub

        m_lanInfo.clear();
    }

    @Override
    protected void stopRollTimer() {
        // TODO Auto-generated method stub

    }

    //getInstant Lan information
    public void getLaninfo(DataValue dataValue) {
        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (bCPEWifiConnected) {
            // 启动请求
            LegacyHttpClient.getInstance().sendPostRequest(new HttpLan.getLanSettingsRequest(response -> {
                if (response.isOk()) {
                    m_lanInfo = response.getModelResult();
                    BusinessManager.getInstance().getSystemInfoModel().setIP(m_lanInfo.getIPv4IPAddress());
                    BusinessManager.getInstance().getSystemInfoModel().setSubnet(m_lanInfo.getSubnetMask());
                    BusinessManager.getInstance().getSystemInfoModel().setMacAddress(m_lanInfo.getMacAddress());
                }
                // sendBroadcast(response, MessageUti.LAN_GET_LAN_SETTINGS);
            }));
        }
    }
}
