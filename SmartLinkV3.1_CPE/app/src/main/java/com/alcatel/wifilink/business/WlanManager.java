package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alcatel.wifilink.business.wlan.HttpWlanSetting;
import com.alcatel.wifilink.business.wlan.WlanNewSettingResult;
import com.alcatel.wifilink.business.wlan.WlanSettingResult;
import com.alcatel.wifilink.business.wlan.WlanSupportModeType;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.SecurityMode;
import com.alcatel.wifilink.common.ENUM.SsidHiddenEnum;
import com.alcatel.wifilink.common.ENUM.WEPEncryption;
import com.alcatel.wifilink.common.ENUM.WModeEnum;
import com.alcatel.wifilink.common.ENUM.WPAEncryption;
import com.alcatel.wifilink.common.ENUM.WlanFrequency;
import com.alcatel.wifilink.common.ENUM.WlanSupportMode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class WlanManager extends BaseManager {
    private final static String TAG = "WlanManager";
//	private String m_strSsid = "";
//	private String m_strSsid_5G = "";
//	private String m_strWifiPwd = "";
//	private String m_strWifiPwd_5G = "";

    private WlanSettingResult m_settings = new WlanSettingResult();
    private WlanNewSettingResult mWlanSettings = new WlanNewSettingResult();

    private Timer m_rollTimer = new Timer();
    GetHostNumTask m_getHostNumTask = null;

    private String m_strWpsPin = "";

    private WlanSupportMode m_wlanSupportMode = WlanSupportMode.Mode2Point4G;

    public WlanNewSettingResult getWlanSettings() {
        return mWlanSettings;
    }

    @Override
    protected void clearData() {
        m_settings.clear();
        m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
    }

    @Override
    protected void stopRollTimer() {
        /*m_getHostNumRollTimer.cancel();
        m_getHostNumRollTimer.purge();
		m_getHostNumRollTimer = new Timer();*/
        if (m_getHostNumTask != null) {
            m_getHostNumTask.cancel();
            m_getHostNumTask = null;
        }
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance()
                    .getCPEWifiConnected();
            if (bCPEWifiConnected) {
                startGetHostNumTask();
                getWlanSupportMode(null);
                getWlanSetting();
            } else {
                m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
            }
        }
    }

    public WlanManager(Context context) {
        super(context);
        // CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already
        // register in basemanager
    }


    public String getSsid() {
        return mWlanSettings.AP2G.getSsid();
    }

    public String getSsid_5G() {
        return mWlanSettings.AP5G.getSsid();
    }

    public String getWifiPwd() {
        switch (getSecurityMode()) {
            case WEP:
            case Disable:
                return mWlanSettings.AP2G.getWepKey();
            case WPA:
            case WPA2:
            case WPA_WPA2:
                return mWlanSettings.AP2G.getWpaKey();

            default:
                return "";
        }
    }

    public String getWifiPwd_5G() {
        switch (getSecurityMode_5G()) {
            case WEP:
            case Disable:
                return mWlanSettings.AP5G.getWepKey();
            case WPA:
            case WPA2:
            case WPA_WPA2:
                return mWlanSettings.AP5G.getWpaKey();
            default:
                return "";
        }
    }

    public WlanSupportMode getCurSupportWlanMode() {
        return m_wlanSupportMode;
    }

    public SecurityMode getSecurityMode() {
        return SecurityMode.build(mWlanSettings.AP2G.getSecurityMode());
    }

    public SecurityMode getSecurityMode_5G() {
        return SecurityMode.build(mWlanSettings.AP5G.getSecurityMode());
    }

    public WPAEncryption getWPAEncryption() {
        return WPAEncryption.build(mWlanSettings.AP2G.getWpaType());
    }

    public WPAEncryption getWPAEncryption_5G() {
        return WPAEncryption.build(mWlanSettings.AP5G.getWpaType());
    }

    public WEPEncryption getWEPEncryption() {
        return WEPEncryption.build(mWlanSettings.AP2G.getWepType());
    }

    public WEPEncryption getWEPEncryption_5G() {
        return WEPEncryption.build(mWlanSettings.AP5G.getWepType());
    }

    public WModeEnum getWMode() {
        return WModeEnum.build(mWlanSettings.AP2G.getWMode());
    }

    public WModeEnum getWMode_5G() {
        return WModeEnum.build(mWlanSettings.AP5G.getWMode());
    }

    public WlanFrequency getWlanFrequency() {
        return WlanFrequency.build(m_settings.WlanAPMode);
    }

    public WlanSettingResult getWlanSettingResult() {
        return m_settings;
    }

    public SsidHiddenEnum getSsidHidden() {
        return SsidHiddenEnum.build(mWlanSettings.AP2G.getSsidHidden());
    }

    public SsidHiddenEnum getSsidHidden_5G() {
        return SsidHiddenEnum.build(mWlanSettings.AP5G.getSsidHidden());
    }

    public String getCountryCode() {
        return mWlanSettings.AP2G.getCountryCode();
    }

    public String getCountryCode_5G() {
        return mWlanSettings.AP5G.getCountryCode();
    }

//	private void getInfoByWansetting() {
//		m_strSsid = m_settings.Ssid;
//		m_strSsid_5G = m_settings.Ssid_5G;
//
//		SecurityMode secMode = SecurityMode.build(m_settings.SecurityMode);
//		switch (secMode) {
//		case WEP:
//		case Disable:
//			m_strWifiPwd = m_settings.WepKey;
//			break;
//		case WPA:
//		case WPA2:
//		case WPA_WPA2:
//			m_strWifiPwd = m_settings.WpaKey;
//			break;
//
//		default:
//			m_strWifiPwd = "";
//			break;
//		}
//
//
//		secMode = SecurityMode.build(m_settings.SecurityMode_5G);
//		switch (secMode) {
//		case WEP:
//		case Disable:
//			m_strWifiPwd_5G = m_settings.WepKey_5G;
//			break;
//		case WPA:
//		case WPA2:
//		case WPA_WPA2:
//			m_strWifiPwd_5G = m_settings.WpaKey_5G;
//			break;
//
//		default:
//			m_strWifiPwd_5G = "";
//			break;
//		}
//
//	}

    // GetNumOfHosts
    // //////////////////////////////////////////////////////////////////////////////////////////
    private void startGetHostNumTask() {
        if (m_getHostNumTask == null) {
            m_getHostNumTask = new GetHostNumTask();
            m_rollTimer.scheduleAtFixedRate(m_getHostNumTask, 0, 10 * 1000);
        }
    }

    class GetHostNumTask extends TimerTask {
        @Override
        public void run() {
        }
    }

    // get wlan setting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getWlanSetting() {
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpWlanSetting.GetWlanSetting(new IHttpFinishListener() {
                    @Override
                    public void onHttpRequestFinish(BaseResponse response) {
                        if (response.isOk()) {
                            mWlanSettings = response.getModelResult();
//								getInfoByWansetting();
                        }
                    }
                }));
    }

    // set wlan setting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setWlanSetting(WlanNewSettingResult settings) {

        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpWlanSetting.SetWlanSetting(settings,
                        new IHttpFinishListener() {
                            @Override
                            public void onHttpRequestFinish(BaseResponse response) {
                                if (response.isOk()) {
//                                    m_settings.clone(settings);
//								getInfoByWansetting();
                                }
//						sendBroadcast(response, MessageUti.WLAN_SET_WLAN_SETTING_REQUSET);
                            }
                        }));
    }


    // Set WPS Pin
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void SetWPSPin(DataValue data) {
        String m_strPin = (String) data.getParamByKey("WpsPin");

        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpWlanSetting.SetWPSPin(m_strPin,
                        response -> {
                            if (response.isOk()) {
                                m_strWpsPin = response.getModelResult();
                            }

//						sendBroadcast(response, MessageUti.WLAN_SET_WPS_PIN_REQUSET);
                        }));
    }

    // Set WPS Pbc
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void SetWPSPbc(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpWlanSetting.SetWPSPbc(response -> {
//						sendBroadcast(response, MessageUti.WLAN_SET_WPS_PBC_REQUSET);
                }));
    }

    //get wlan support mode
    public void getWlanSupportMode(DataValue data) {
        String strDeviceName = BusinessManager.getInstance().getFeatures().getDeviceName();
        if (!FeatureVersionManager.getInstance().isSupportApi("Wlan", "GetWlanSupportMode")) {
            if (0 != strDeviceName.compareToIgnoreCase("Y900")) {
                m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
                return;
            }
        }

        boolean blCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blCPEWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(
                    new HttpWlanSetting.getWlanSupportModeRequest(response -> {
                        if (response.isOk()) {
                            WlanSupportModeType modeType = response.getModelResult();
                            m_wlanSupportMode = WlanSupportMode.build(modeType.getWlanSupportMode());
                        } else {
                            getWlanSupportMode(null);
                        }

//							sendBroadcast(response, MessageUti.WLAN_SET_WPS_PBC_REQUSET);
                    }));
        }
    }
}
