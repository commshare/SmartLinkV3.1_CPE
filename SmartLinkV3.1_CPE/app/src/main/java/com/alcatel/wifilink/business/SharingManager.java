package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.sharing.DlnaSettings;
import com.alcatel.wifilink.business.sharing.FtpSettings;
import com.alcatel.wifilink.business.sharing.HttpSharing;
import com.alcatel.wifilink.business.sharing.SDCardSpace;
import com.alcatel.wifilink.business.sharing.SDcardStatus;
import com.alcatel.wifilink.business.sharing.SambaSettings;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;

import java.util.Timer;
import java.util.TimerTask;

public class SharingManager extends BaseManager {

    private DlnaSettings m_dlnaSettings = new DlnaSettings();
    private SambaSettings m_sambaSettings = new SambaSettings();
    private SDCardSpace m_sdcardSpace = new SDCardSpace();
    private SDcardStatus m_sdcardStatus = new SDcardStatus();
    private FtpSettings m_ftpSettings = new FtpSettings();

    private Timer m_getSDCardTimer = new Timer();
    private GetSDcardStatusTask m_getSDCardTask = null;

    private GetFtpStatusTask m_ftp_task = null;
    private GetDlnaStatusTask m_dlna_task = null;

    private Timer m_getShareStatusTimer = new Timer();


    public SharingManager(Context context) {
        super(context);
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance()
                    .getCPEWifiConnected();
            if (bCPEWifiConnected == true) {
                startGetSDCardStatusTask();
            } else {
                stopRollTimer();
            }
        }
    }

    @Override
    protected void clearData() {

    }

    @Override
    public void stopRollTimer() {
        if (null != m_getSDCardTask) {
            m_getSDCardTask.cancel();
            m_getSDCardTask = null;
        }
        if (null != m_ftp_task) {
            m_ftp_task.cancel();
            m_ftp_task = null;
        }
        if (null != m_dlna_task) {
            m_dlna_task.cancel();
            m_dlna_task = null;
        }
    }

    public DlnaSettings getDlnaSettings() {
        return m_dlnaSettings;
    }

    public SambaSettings getSambaSettings() {
        return m_sambaSettings;
    }

    public SDCardSpace getSDCardSpace() {
        return m_sdcardSpace;
    }

    public SDcardStatus getSDCardStatus() {
        return m_sdcardStatus;
    }

    public FtpSettings getFtpSettings() {
        return m_ftpSettings;
    }

    //SetUSBcardSetting//////////////////////////////////

    public void switchOnDlna() {
        if (!FeatureVersionManager.getInstance().isSupportDLNA() && !FeatureVersionManager.getInstance().isY900Project()) {
            DataValue data = new DataValue();
            data.addParam("DlnaStatus", 1);
            data.addParam("DlnaName", m_dlnaSettings.DlnaName);

            BusinessManager.getInstance().sendRequestMessage(
                    MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
        } else {
            LegacyHttpClient.getInstance().sendPostRequest(
                    new HttpSharing.SetUSBcardSetting(0, response -> {
                        if (response.isOk()) {
                            final int nPreStatus = m_dlnaSettings.DlnaStatus;
                            m_dlnaSettings.DlnaStatus = 1;
                            LegacyHttpClient.getInstance().sendPostRequest(
                                    new HttpSharing.SetDlnaSetting(1, m_dlnaSettings.DlnaName,
                                            dlnaResponse -> {
                                                if (dlnaResponse.isOk()) {

                                                } else {
                                                    m_dlnaSettings.DlnaStatus = nPreStatus;
                                                }

//                                                    sendBroadcast(dlnaResponse, MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
                                            }));
                        }

//                        sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                    }));
        }
    }

    public void switchOffDlna() {
        if (!FeatureVersionManager.getInstance().isSupportDLNA() && !FeatureVersionManager.getInstance().isY900Project()) {
            DataValue data = new DataValue();
            data.addParam("DlnaStatus", 0);
            data.addParam("DlnaName", m_dlnaSettings.DlnaName);

            BusinessManager.getInstance().sendRequestMessage(
                    MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
        } else {

            final int nPreStatus = m_dlnaSettings.DlnaStatus;
            m_dlnaSettings.DlnaStatus = 0;
            LegacyHttpClient.getInstance().sendPostRequest(
                    new HttpSharing.SetDlnaSetting(0,
                            m_dlnaSettings.DlnaName,
                            response -> {
                                if (response.isOk()) {
                                    LegacyHttpClient.getInstance().sendPostRequest(
                                            new HttpSharing.SetUSBcardSetting(1,
                                                    response1 -> {
//                                                            sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                                                    }));

                                } else {
                                    m_dlnaSettings.DlnaStatus = nPreStatus;
                                }

//                                sendBroadcast(response, MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
                            }));
        }
    }

    public void setUSBcardSetting(DataValue data) {
        int status = (Integer) data.getParamByKey("USBcardStatus");
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.SetUSBcardSetting(status, response -> {
//                                sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                }));
    }

    // SetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setFtpSetting(DataValue data) {
        int status = (Integer) data.getParamByKey("FtpStatus");
        final int nPreStatus = m_ftpSettings.FtpStatus;
        m_ftpSettings.FtpStatus = status;
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.SetFtpSetting(status, response -> {
                    if (response.isOk()) {

                    } else {
                        m_ftpSettings.FtpStatus = nPreStatus;
                    }

//                                sendBroadcast(response, MessageUti.SHARING_SET_FTP_SETTING_REQUSET);
                }));
    }

    // GetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getFtpSetting(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.GetFtpSetting(response -> {
                    if (response.isOk()) {
                        m_ftpSettings = response.getModelResult();
                    } else {
                        m_ftpSettings.clear();
                    }
//                                sendBroadcast(response, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
                }));
    }


    // SetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setSambaSetting(DataValue data) {
        int status = (Integer) data.getParamByKey("SambaStatus");
        final int nPreStatus = m_sambaSettings.SambaStatus;
        m_sambaSettings.SambaStatus = status;
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.SetSambaSetting(status, response -> {
                    if (response.isOk()) {

                    } else {
                        m_sambaSettings.SambaStatus = nPreStatus;
                    }

//                                sendBroadcast(response, MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET);
                }));
    }

    // GetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getSambaSetting(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.GetSambaSetting(response -> {
                    if (response.isOk()) {
                        m_sambaSettings = response.getModelResult();
                    } else {
                        m_sambaSettings.clear();
                    }
//                                sendBroadcast(response, MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET);
                }));
    }

    // SetDlnaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setDlnaSetting(DataValue data) {
        if (!FeatureVersionManager.getInstance().isSupportDLNA() && !FeatureVersionManager.getInstance().isY900Project()) {
            return;
        }

        int status = (Integer) data.getParamByKey("DlnaStatus");
        String name = (String) data.getParamByKey("DlnaName");
        final int nPreStatus = m_dlnaSettings.DlnaStatus;
        m_dlnaSettings.DlnaStatus = status;
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.SetDlnaSetting(status, name, response -> {
                    if (response.isOk()) {

                    } else {
                        m_dlnaSettings.DlnaStatus = nPreStatus;
                    }

//                                sendBroadcast(response, MessageUti.SHARING_SET_DLNA_SETTING_REQUSET);
                }));
    }

    // GetDlnaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getDlnaSetting(DataValue data) {
        if (!FeatureVersionManager.getInstance().isSupportDLNA() && !FeatureVersionManager.getInstance().isY900Project()) {
//                sendBroadcast(BaseResponse.EMPTY, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
            return;
        }

        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.GetDlnaSetting(response -> {
                    if (response.isOk()) {
                        m_dlnaSettings = response.getModelResult();
                    } else {
                        m_dlnaSettings.clear();
                    }
//                                sendBroadcast(response, MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
                }));
    }

    // GetSDCardSpace
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getSDCardSpace(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.GetSDCardSpace(response -> {
                    if (response.isOk()) {
                        m_sdcardSpace = response.getModelResult();
                    } else {
                        m_sdcardSpace.clear();
                    }
//                                sendBroadcast(response, MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET);
                }));
    }

    // GetSDcardStatus
    // //////////////////////////////////////////////////////////////////////////////////////////

    private void startGetSDCardStatusTask() {
        if (m_getSDCardTask == null) {
            m_getSDCardTask = new GetSDcardStatusTask();
            m_getSDCardTimer.scheduleAtFixedRate(
                    m_getSDCardTask, 0, 10000);
        }
    }

    public void getSDcardStatus(DataValue data) {
//		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
//				"GetSDcardStatus") != true)
//			return;

        LegacyHttpClient.getInstance().sendPostRequest(
                new HttpSharing.GetSDcardStatus(response -> {
                    if (response.isOk()) {
                        m_sdcardStatus = response.getModelResult();
                    } else {
                        m_sdcardStatus.clear();
                    }
//                                sendBroadcast(response, MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET);
                }));
    }

    public void startGetFtpStatus() {
        if (m_ftp_task == null) {
            m_ftp_task = new GetFtpStatusTask();
            m_getShareStatusTimer.scheduleAtFixedRate(m_ftp_task, 0, 5 * 1000);
        }
    }

    public void startGetDlnaStatus() {
        if (!FeatureVersionManager.getInstance().isSupportDLNA() && !FeatureVersionManager.getInstance().isY900Project()) {
            return;
        }

        if (m_dlna_task == null) {
            m_dlna_task = new GetDlnaStatusTask();
            m_getShareStatusTimer.scheduleAtFixedRate(m_dlna_task, 0, 5 * 1000);
        }
    }

    class GetSDcardStatusTask extends TimerTask {
        @Override
        public void run() {
            getSDcardStatus(null);
        }
    }

    class GetFtpStatusTask extends TimerTask {

        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(
                    new HttpSharing.GetFtpSetting(response -> {
                        if (response.isOk()) {
                            m_ftpSettings = response.getModelResult();
                        } else {
                            m_ftpSettings.clear();
                        }
//                                    sendBroadcast(response, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
                    }));
        }

    }

    class GetDlnaStatusTask extends TimerTask {

        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(
                    new HttpSharing.GetDlnaSetting(response -> {
                        if (response.isOk()) {
                            m_dlnaSettings = response.getModelResult();
                        } else {
                            m_dlnaSettings.clear();
                        }
//                                    sendBroadcast(response, MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
                    }));
        }

    }

}
