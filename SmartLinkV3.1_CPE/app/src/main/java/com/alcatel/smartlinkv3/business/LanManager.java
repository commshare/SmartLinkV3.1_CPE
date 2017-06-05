package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alcatel.smartlinkv3.business.lan.HttpLan;
import com.alcatel.smartlinkv3.business.lan.LanInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;

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
                Log.d(TAG, "lanManager successful");
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

    //get Lan information
    public void getLaninfo(DataValue dataValue) {
        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (bCPEWifiConnected) {
            // 启动请求
            LegacyHttpClient.getInstance().sendPostRequest(new HttpLan.getLanSettingsRequest(response -> {
                if (response.isOk()) {
                    m_lanInfo = response.getModelResult();
                    Log.d(TAG, "getLaninfo: " + m_lanInfo.toString());
                    BusinessManager.getInstance().getSystemInfoModel().setIP(m_lanInfo.getIPv4IPAddress());
                    BusinessManager.getInstance().getSystemInfoModel().setSubnet(m_lanInfo.getSubnetMask());
                    BusinessManager.getInstance().getSystemInfoModel().setMacAddress(m_lanInfo.getMacAddress());
                }
                // sendBroadcast(response, MessageUti.LAN_GET_LAN_SETTINGS);
            }));
        }
    }
}
