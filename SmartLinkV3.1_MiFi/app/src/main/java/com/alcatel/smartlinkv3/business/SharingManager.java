package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.FtpSettings;
import com.alcatel.smartlinkv3.business.sharing.HttpSharing;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.business.sharing.SDcardStatus;
import com.alcatel.smartlinkv3.business.sharing.SambaSettings;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

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


    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {
                startGetSDCardStatusTask();
            } else {
                stopRollTimer();
            }
        }
    }

    public SharingManager(Context context) {
        super(context);
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
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "SetDLNASettings") != true && BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") != true) {
            DataValue data = new DataValue();
            data.addParam("DlnaStatus", 1);
            data.addParam("DlnaName", m_dlnaSettings.DlnaName);

            BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
        } else {

            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetUSBcardSetting("14.11", 0, new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    String strErrcode = new String();
                    int ret = response.getResultCode();
                    if (ret == BaseResponse.RESPONSE_OK) {
                        strErrcode = response.getErrorCode();
                        if (strErrcode.length() == 0) {

                            final int nPreStatus = m_dlnaSettings.DlnaStatus;
                            m_dlnaSettings.DlnaStatus = 1;
                            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetDlnaSetting("14.2", 1, m_dlnaSettings.DlnaName, new IHttpFinishListener() {
                                @Override
                                public void onHttpRequestFinish(BaseResponse response) {
                                    String strErrcode = new String();
                                    int ret = response.getResultCode();
                                    if (ret == BaseResponse.RESPONSE_OK) {
                                        strErrcode = response.getErrorCode();
                                        if (strErrcode.length() == 0) {

                                        } else {
                                            m_dlnaSettings.DlnaStatus = nPreStatus;
                                        }
                                    } else {
                                        m_dlnaSettings.DlnaStatus = nPreStatus;
                                    }

                                    Intent megIntent = new Intent(MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
                                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                                    m_context.sendBroadcast(megIntent);
                                }
                            }));

                        } else {

                        }
                    } else {

                    }

                    Intent megIntent = new Intent(MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }
    }

    public void switchOffDlna() {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "SetDLNASettings") != true && BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") != true) {
            DataValue data = new DataValue();
            data.addParam("DlnaStatus", 0);
            data.addParam("DlnaName", m_dlnaSettings.DlnaName);

            BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
        } else {

            final int nPreStatus = m_dlnaSettings.DlnaStatus;
            m_dlnaSettings.DlnaStatus = 0;
            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetDlnaSetting("14.2", 0, m_dlnaSettings.DlnaName, new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    String strErrcode = new String();
                    int ret = response.getResultCode();
                    if (ret == BaseResponse.RESPONSE_OK) {
                        strErrcode = response.getErrorCode();
                        if (strErrcode.length() == 0) {

                            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetUSBcardSetting("14.11", 1, new IHttpFinishListener() {
                                @Override
                                public void onHttpRequestFinish(BaseResponse response) {
                                    String strErrcode = new String();
                                    int ret = response.getResultCode();
                                    if (ret == BaseResponse.RESPONSE_OK) {
                                        strErrcode = response.getErrorCode();
                                        if (strErrcode.length() == 0) {

                                        } else {

                                        }
                                    } else {

                                    }

                                    Intent megIntent = new Intent(MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                                    m_context.sendBroadcast(megIntent);
                                }
                            }));

                        } else {
                            m_dlnaSettings.DlnaStatus = nPreStatus;
                        }
                    } else {
                        m_dlnaSettings.DlnaStatus = nPreStatus;
                    }

                    Intent megIntent = new Intent(MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }
    }

    public void setUSBcardSetting(DataValue data) {
        int status = (Integer) data.getParamByKey("USBcardStatus");
        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetUSBcardSetting("14.11", status, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {

                    } else {

                    }
                } else {

                }

                Intent megIntent = new Intent(MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // SetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setFtpSetting(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "SetFtpStatus") != true)
            return;

        int status = (Integer) data.getParamByKey("FtpStatus");
        final int nPreStatus = m_ftpSettings.FtpStatus;
        m_ftpSettings.FtpStatus = status;
        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetFtpSetting("14.6", status, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {

                    } else {
                        m_ftpSettings.FtpStatus = nPreStatus;
                    }
                } else {
                    m_ftpSettings.FtpStatus = nPreStatus;
                }

                Intent megIntent = new Intent(MessageUti.SHARING_SET_FTP_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // GetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getFtpSetting(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetFtpStatus") != true) {
            Intent megIntent = new Intent(MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
            megIntent.putExtra(MessageUti.RESPONSE_RESULT, "Noresult");
            megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, "Error");
            m_context.sendBroadcast(megIntent);
            return;
        }


        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetFtpSetting("14.5", new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {
                        m_ftpSettings = response.getModelResult();
                    } else {
                        m_ftpSettings.clear();
                    }
                } else {
                    m_ftpSettings.clear();
                }
                Intent megIntent = new Intent(MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }


    // SetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setSambaSetting(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "SetSambaStatus") != true)
            return;

        int status = (Integer) data.getParamByKey("SambaStatus");
        final int nPreStatus = m_sambaSettings.SambaStatus;
        m_sambaSettings.SambaStatus = status;
        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetSambaSetting("14.4", status, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {

                    } else {
                        m_sambaSettings.SambaStatus = nPreStatus;
                    }
                } else {
                    m_sambaSettings.SambaStatus = nPreStatus;
                }

                Intent megIntent = new Intent(MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // GetSambaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getSambaSetting(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetSambaStatus") != true)
            return;

        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetSambaSetting("14.3", new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {
                        m_sambaSettings = response.getModelResult();
                    } else {
                        m_sambaSettings.clear();
                    }
                } else {
                    m_sambaSettings.clear();
                }
                Intent megIntent = new Intent(MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // SetDlnaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void setDlnaSetting(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "SetDLNASettings") != true && BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") != true) {
            return;
        }

        int status = (Integer) data.getParamByKey("DlnaStatus");
        String name = (String) data.getParamByKey("DlnaName");
        final int nPreStatus = m_dlnaSettings.DlnaStatus;
        m_dlnaSettings.DlnaStatus = status;
        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.SetDlnaSetting("14.2", status, name, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {

                    } else {
                        m_dlnaSettings.DlnaStatus = nPreStatus;
                    }
                } else {
                    m_dlnaSettings.DlnaStatus = nPreStatus;
                }

                Intent megIntent = new Intent(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // GetDlnaSetting
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getDlnaSetting(DataValue data) {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") != true) {
            if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetDLNASettings") != true) {
                Intent megIntent = new Intent(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, "Noresult");
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, "Error");
                m_context.sendBroadcast(megIntent);
                return;
            }
        }

        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetDlnaSetting("14.1", new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {
                        m_dlnaSettings = response.getModelResult();
                    } else {
                        m_dlnaSettings.clear();
                    }
                } else {
                    m_dlnaSettings.clear();
                }
                Intent megIntent = new Intent(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // GetSDCardSpace
    // //////////////////////////////////////////////////////////////////////////////////////////
    public void getSDCardSpace(DataValue data) {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetSDCardSpace") != true)
            return;

        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetSDCardSpace("14.7", new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {
                        m_sdcardSpace = response.getModelResult();
                    } else {
                        m_sdcardSpace.clear();
                    }
                } else {
                    m_sdcardSpace.clear();
                }
                Intent megIntent = new Intent(MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    // GetSDcardStatus
    // //////////////////////////////////////////////////////////////////////////////////////////	

    private void startGetSDCardStatusTask() {
        if (m_getSDCardTask == null) {
            m_getSDCardTask = new GetSDcardStatusTask();
            m_getSDCardTimer.scheduleAtFixedRate(m_getSDCardTask, 0, 10000);
        }
    }

    class GetSDcardStatusTask extends TimerTask {
        @Override
        public void run() {
            getSDcardStatus(null);
        }
    }

    public void getSDcardStatus(DataValue data) {
        //		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
        //				"GetSDcardStatus") != true)
        //			return;

        HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetSDcardStatus("14.9", new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                String strErrcode = new String();
                int ret = response.getResultCode();
                if (ret == BaseResponse.RESPONSE_OK) {
                    strErrcode = response.getErrorCode();
                    if (strErrcode.length() == 0) {
                        m_sdcardStatus = response.getModelResult();
                    } else {
                        m_sdcardStatus.clear();
                    }
                } else {
                    m_sdcardStatus.clear();
                }
                Intent megIntent = new Intent(MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    class GetFtpStatusTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetFtpSetting("14.5", new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    String strErrcode = new String();
                    int ret = response.getResultCode();
                    if (ret == BaseResponse.RESPONSE_OK) {
                        strErrcode = response.getErrorCode();
                        if (strErrcode.length() == 0) {
                            m_ftpSettings = response.getModelResult();
                        } else {
                            m_ftpSettings.clear();
                        }
                    } else {
                        m_ftpSettings.clear();
                    }
                    Intent megIntent = new Intent(MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }

    }

    class GetDlnaStatusTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            HttpRequestManager.GetInstance().sendPostRequest(new HttpSharing.GetDlnaSetting("14.1", new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    String strErrcode = new String();
                    int ret = response.getResultCode();
                    if (ret == BaseResponse.RESPONSE_OK) {
                        strErrcode = response.getErrorCode();
                        if (strErrcode.length() == 0) {
                            m_dlnaSettings = response.getModelResult();
                        } else {
                            m_dlnaSettings.clear();
                        }
                    } else {
                        m_dlnaSettings.clear();
                    }
                    Intent megIntent = new Intent(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                    m_context.sendBroadcast(megIntent);
                }
            }));
        }

    }

    public void startGetFtpStatus() {
        if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetFtpStatus") != true) {
            return;
        }

        if (m_ftp_task == null) {
            m_ftp_task = new GetFtpStatusTask();
            m_getShareStatusTimer.scheduleAtFixedRate(m_ftp_task, 0, 5 * 1000);
        }
    }

    public void startGetDlnaStatus() {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") != true) {
            if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetDLNASettings") != true) {
                return;
            }
        }

        if (m_dlna_task == null) {
            m_dlna_task = new GetDlnaStatusTask();
            m_getShareStatusTimer.scheduleAtFixedRate(m_dlna_task, 0, 5 * 1000);
        }
    }

}
