package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;

import com.alcatel.smartlinkv3.business.system.Features;
import com.alcatel.smartlinkv3.business.system.HttpSystem;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.business.system.SystemInfo;
import com.alcatel.smartlinkv3.business.system.SystemStatus;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;
import com.alcatel.smartlinkv3.httpservice.RestHttpClient;

import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;

public class SystemManager extends BaseManager {
    private Features m_features = new Features();
    private SystemInfo m_systemInfo = new SystemInfo();
    private SystemStatus m_systemStatus = new SystemStatus();
    private static final String TAG = "SystemManager";

    private Timer m_getFeaturesTimer = new Timer();// this time not to cancel
    // when cpe wifi
    // disconnected,beacause
    // used to test the wifi
    // connect state
    // private Timer m_getStorageTimer = new Timer();
    private Timer m_getSystemStatusTimer = new Timer();

    private String m_strAppVersion = "";

    private GetFeaturesTask m_getFeaturesTask = null;
    private GetSystemStatusTask m_getSystemStatusTask = null;

    private RestoreError m_errorInfo = new RestoreError();
    private int featureTryTime = 0;
    private final static int MAX_TRY = 10;

    public SystemManager(Context context) {
        super(context);
        // CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already
        // register in basemanager
        // m_context.registerReceiver(m_msgReceiver, new
        // IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));

        //app version
        FetchAppVersion();
    }

    public Features getFeatures() {
        return m_features;
    }

    public SystemInfo getSystemInfoModel() {
        return m_systemInfo;
    }

    public SystemStatus getSystemStatus() {
        return m_systemStatus;
    }

    public String getAppVersion() {
        return m_strAppVersion;
    }

    public RestoreError getRestoreError() {
        return m_errorInfo;
    }

    @Override
    protected void clearData() {
        m_features.clear();
        m_systemInfo.clear();
        m_systemInfo.clear();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.CPE_BUSINESS_STATUS_CHANGE)) {
            m_bStopBusiness = intent.getBooleanExtra("stop", false);
            switchBusiness();
        }

        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            if (isWifiConnect()) {
                getSystemInfo();
                startSystemStatusTask();
            } else {
                stopRollTimer();
                m_systemInfo.clear();
                m_systemStatus.clear();
                BusinessManager.getInstance().getSystemInfoModel().clear();
            }
        }
    }

    private void FetchAppVersion() {
        PackageManager manager = m_context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(m_context.getPackageName(), 0);
            m_strAppVersion = info.versionName;
        } catch (NameNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected void stopRollTimer() {
        if (null != m_getSystemStatusTask) {
            m_getSystemStatusTask.cancel();
            m_getSystemStatusTask = null;
        }
    }

    public void switchBusiness() {
        if (m_bStopBusiness) {
            if (m_getFeaturesTask != null) {
                m_getFeaturesTask.cancel();
                m_getFeaturesTask = null;
            }
            // m_getFeaturesTimer.cancel();
            // m_getFeaturesTimer.purge();
            // m_getFeaturesTimer = new Timer();
        } else {
            requestFeatures();
            //if (m_getFeaturesTask == null) {
            //m_getFeaturesTask = new GetFeaturesTask();
            //m_getFeaturesTimer.scheduleAtFixedRate(m_getFeaturesTask, 0, 10*1000);
            //}
        }
    }

    public Observable<Features> requestFeatures() {
        BaseRequest request = new HttpSystem.GetFeature();
        Observable<Features> observable = createObservable(request);
        Subscription subscription = observable.subscribe(data -> {
                    // 获取到所有的api方法
                    m_features = data;

                    /* ******************************* 所有API方法 START ******************************* */
                    // TOAT: 测试方法 
                    Set<String> strings = m_features.getFeatures().keySet();
                    Iterator<String> iterator = strings.iterator();
                    while (iterator.hasNext()) {
                        Log.d(TAG, "getFeatures: " + iterator.next());
                    }
                    /* ******************************* 所有API方法 END ********************************* */

                    // no feature api
                    if (m_features != null && m_features.getFeatures().size() > 0) {
                        DataConnectManager.getInstance().setCPEWifiConnected(true);
                        featureTryTime = 0;
                    } else if (featureTryTime++ < MAX_TRY && !m_bStopBusiness) {
                        requestFeatures();
                    }
                },

                error -> {
                    if (featureTryTime++ < MAX_TRY && !m_bStopBusiness) {
                        requestFeatures();
                    }
                });//.unsubscribe();

        return observable;
    }

    // Get System info
    public void getSystemInfo() {
        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (bCPEWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.GetSystemInfo(new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    if (response.isOk()) {
                        m_systemInfo = response.getModelResult();
                        BusinessManager.getInstance().getSystemInfoModel().updateSystemInfo(m_systemInfo);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getSystemInfo();
                            }
                        }, 1000);
                    }

                    //sendBroadcast(response, MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST);
                }
            }));
        }
    }

    private void startSystemStatusTask() {
        if (m_getSystemStatusTask == null) {
            m_getSystemStatusTask = new GetSystemStatusTask();
            m_getSystemStatusTimer.scheduleAtFixedRate(m_getSystemStatusTask, 0, 5 * 1000);
        }
    }

    // startSystemStatusTask
    // //////////////////////////////////////////////////////////////////////////////////////////

    //device reboot
    public void rebootDevice(DataValue data) {
        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.SetDeviceReboot(response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_REBOOT);
            }));
        }
    }

    //reset device
    public void resetDevice(DataValue data) {
        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.SetDeviceReset(response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_RESET);
            }));
        }
    }

    //device backup
    public void backupDevice(DataValue data) {
        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.SetDeviceBackup(response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_BACKUP);
            }));
        }
    }

    //device restore
    public void restoreDevice(DataValue data) {
        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            String strFile = data.getParamByKey("FileName").toString();
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.SetDeviceRestore(strFile, response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_RESTORE);
            }));
        }
    }

    public void setDevicePowerOff(DataValue data) {
        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.setDevicePowerOffRequest(response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_POWER_OFF);
            }));
        }
    }

    public void setAppBackup(DataValue data) {
        //if (!FeatureVersionManager.getInstance().
        //isSupportApi("System", "SetAppBackup")) {
        //return;
        //}

        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.setAppBackupRequest(response -> {
                //sendBroadcast(response, MessageUti.SYSTEM_SET_APP_BACKUP);
            }));
        }
    }

    public void setAppRestoreBackup(DataValue data) {
        //if (!FeatureVersionManager.getInstance().
        //isSupportApi("System", "SetAppRestoreBackup")) {
        //return;
        //}

        boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if (blWifiConnected) {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.setAppRestoreBackupRequest(response -> {
                if (response.isOk()) {
                    m_errorInfo = response.getModelResult();
                }
                // sendBroadcast(response, MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP);
            }));
        }
    }

    // Get feature list
    class GetFeaturesTask extends TimerTask {
        @Override
        public void run() {
            RestHttpClient.getInstance().sendPostRequest(new HttpSystem.GetFeature((response -> {
                if (response.isOk()) {
                    m_features = response.getModelResult();
                }

                //sendBroadcast(response, MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET);
            })));

            //
            //LegacyHttpClient.getInstance().sendPostRequest(
            //new HttpSystem.GetFeature(new IHttpFinishListener() {
            //@Override
            //public void onHttpRequestFinish(BaseResponse response) {
            //if (response.isOk()) {
            //m_features = response.getModelResult();
            //}
            //
            //m_bAlreadyRecogniseCPEDevice = true;
            //
            //// change billing month,because to next billing
            //// month
            ////SimpleDateFormat startTemp = new SimpleDateFormat(
            ////Const.DATE_FORMATE);
            ////Calendar caNow = Calendar.getInstance();
            ////String strNow = startTemp.format(caNow.getTime());
            ////if (!(BusinessManager.getInstance()
            ////.getUsageSettings().m_strStartBillDate
            ////.compareTo(strNow) <= 0 && BusinessManager
            ////.getInstance().getUsageSettings().m_strEndBillDate
            ////.compareTo(strNow) >= 0)) {
            ////BusinessManager.getInstance()
            ////.getUsageSettings()
            ////.calStartAndEndCalendar();
            ////Intent megIntent = new Intent(
            ////MessageUti.CPE_CHANGED_BILLING_MONTH);
            ////m_context.sendBroadcast(megIntent);
            ////}
            //
            //sendBroadcast(response, MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET);
            //}
            //}));
        }
    }

    class GetSystemStatusTask extends TimerTask {
        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSystem.GetSystemStatus(response -> {
                if (response.isOk()) {
                    m_systemStatus = response.getModelResult();
                } else {
                    //new Handler().postDelayed(
                    //new Runnable() {
                    //@Override
                    //public void run() {
                    //getSystemStatus(null);
                    //}
                    //}, 1000);
                }
            }));
        }
    }
}
