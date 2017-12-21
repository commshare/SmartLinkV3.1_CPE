package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.power.HttpPower;
import com.alcatel.smartlinkv3.business.power.PowerSavingModeInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class PowerManager extends BaseManager {

    private PowerSavingModeInfo m_powerSavingMode = null;
    private BatteryInfo m_battery = null;
    private getBatteryStateTask m_getBatteryStateTask = null;
    private Timer m_getBatteryRollTimer = new Timer();

    public PowerManager(Context context) {
        super(context);
        m_powerSavingMode = new PowerSavingModeInfo();
        m_battery = new BatteryInfo();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected) {
                startGetBatteryStatusTask();
                getPowerSavingModeInfo(null);
            } else {
                clearData();
                stopBatteryStatusTask();
            }
        }
    }

    @Override
    protected void clearData() {
        m_powerSavingMode.clear();
        m_battery.clear();
    }

    @Override
    protected void stopRollTimer() {

    }

    public BatteryInfo getBatteryInfo() {
        return m_battery;
    }

    public PowerSavingModeInfo getPowerSavingModeInfo() {
        return m_powerSavingMode;
    }

    private void startGetBatteryStatusTask() {
        if (m_getBatteryStateTask == null) {
            m_getBatteryStateTask = new getBatteryStateTask();
            m_getBatteryRollTimer.scheduleAtFixedRate(m_getBatteryStateTask, 0, 10 * 1000);
        }
    }

    private void stopBatteryStatusTask() {
        if (m_getBatteryStateTask != null) {
            m_getBatteryStateTask.cancel();
            m_getBatteryStateTask = null;
        }
    }

    class getBatteryStateTask extends TimerTask {

        @Override
        public void run() {
            getBatteryState(null);
        }

    }

    //get battery state
    public void getBatteryState(DataValue data) {
        if (!FeatureVersionManager.getInstance().isSupportApi("PowerManagement", "GetBatteryState")) {
            return;
        }

        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            HttpRequestManager.GetInstance().sendPostRequest(new HttpPower.getBatteryStateRequest("16.1", new IHttpFinishListener() {

                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    int nRes = response.getResultCode();
                    String strErr = response.getErrorMessage();
                    if (BaseResponse.RESPONSE_OK == nRes && 0 == strErr.length()) {
                        m_battery = response.getModelResult();
                    }

                    Intent megIntent = new Intent(MessageUti.POWER_GET_BATTERY_STATE);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, nRes);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErr);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }
    }

    //get power saving mode
    public void getPowerSavingModeInfo(DataValue data) {
        if (!FeatureVersionManager.getInstance().
                                                        isSupportApi("PowerManagement", "GetPowerSavingMode")) {
            return;
        }

        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            HttpRequestManager.GetInstance().sendPostRequest(new HttpPower.getPowerSavingModeRequest("16.2", new IHttpFinishListener() {

                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    int nRes = response.getResultCode();
                    String strErr = response.getErrorMessage();
                    if (BaseResponse.RESPONSE_OK == nRes && 0 == strErr.length()) {
                        m_powerSavingMode = response.getModelResult();
                    } else {
                        getPowerSavingModeInfo(null);
                    }

                    Intent megIntent = new Intent(MessageUti.POWER_GET_POWER_SAVING_MODE);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, nRes);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErr);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }
    }

    //set power saving mode
    public void setPowerSavingModeInfo(DataValue data) {
        if (!FeatureVersionManager.getInstance().
                                                        isSupportApi("PowerManagement", "SetPowerSavingMode")) {
            return;
        }

        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            PowerSavingModeInfo info = (PowerSavingModeInfo) data.getParamByKey("PowerSavingMode");
            HttpRequestManager.GetInstance().sendPostRequest(new HttpPower.setPowerSavingModeRequest("16.3", info, new IHttpFinishListener() {

                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    int nRes = response.getResultCode();
                    String strErr = response.getErrorCode();

                    Intent megIntent = new Intent(MessageUti.POWER_SET_POWER_SAVING_MODE);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, nRes);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErr);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }
    }
}
