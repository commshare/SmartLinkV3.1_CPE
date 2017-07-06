package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alcatel.wifilink.business.model.NetworkInfoModel;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.business.network.HttpGetNetworkInfo;
import com.alcatel.wifilink.business.network.HttpGetNetworkRegisterState;
import com.alcatel.wifilink.business.network.HttpGetNetworkRegisterState.NetworkRegisterStateResult;
import com.alcatel.wifilink.business.network.HttpGetNetworkSettings;
import com.alcatel.wifilink.business.network.HttpGetNetworkSettings.GetNetworkSettingResult;
import com.alcatel.wifilink.business.network.HttpRegisterNetwork;
import com.alcatel.wifilink.business.network.HttpSearchNetwork;
import com.alcatel.wifilink.business.network.HttpSearchNetworkResult;
import com.alcatel.wifilink.business.network.HttpSearchNetworkResult.NetworkItem;
import com.alcatel.wifilink.business.network.HttpSearchNetworkResult.NetworkItemList;
import com.alcatel.wifilink.business.network.HttpSetNetworkSettings;
import com.alcatel.wifilink.business.network.NetworkInfoResult;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkManager extends BaseManager {
    public final int SEARCH_NOT_NETWORK = 0;
    public final int SEARCHING = 1;
    public final int SEARCH_SUCCESSFUL = 2;
    public final int SEARCH_FAILED = 3;
    private NetworkInfoModel m_networkInfo = new NetworkInfoModel();
    private Timer m_getNetworkInfoRollTimer = new Timer();
    private GetNetworkInfoTask m_getNetworkInfoTask = null;
    private GetNetworkRegisterStateTask m_getNetworkRegisterStateTask = null;
    private SearchNetworkResultTask m_searchNetworkResultTask = null;
    private List<NetworkItem> m_NetworkList = null;
    private GetNetworkSettingResult m_network_setting_result = null;
    private int m_network_mode = -1;
    private int m_network_selection = -1;

    public NetworkManager(Context context) {
        super(context);
        //CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
        m_context.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }

    public int getNetworkMode() {
        return m_network_mode;
    }

    public int getNetworkSelection() {
        return m_network_selection;
    }

    @Override
    protected void clearData() {
        // TODO Auto-generated method stub
        m_networkInfo.clear();
    }

    @Override
    protected void stopRollTimer() {
        /*m_getNetworkInfoRollTimer.cancel();
		m_getNetworkInfoRollTimer.purge();
		m_getNetworkInfoRollTimer = new Timer();*/
        if (m_getNetworkInfoTask != null) {
            m_getNetworkInfoTask.cancel();
            m_getNetworkInfoTask = null;
        }

        if (m_searchNetworkResultTask != null) {
            m_searchNetworkResultTask.cancel();
            m_searchNetworkResultTask = null;
        }
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();

        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {

            }
        }

        if (MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
            if (ok) {
                SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
                if (simStatus.m_SIMState == ENUM.SIMState.Accessable) {
                    startGetNetworkInfoTask();
                } else {
                    stopRollTimer();
                    m_networkInfo.clear();
                }
            }
        }
    }

    public NetworkInfoModel getNetworkInfo() {
        return m_networkInfo;
    }

    //GetNetworkInfo //////////////////////////////////////////////////////////////////////////////////////////
    private void startGetNetworkInfoTask() {
        if (m_getNetworkInfoTask == null) {
            m_getNetworkInfoTask = new GetNetworkInfoTask();
            m_getNetworkInfoRollTimer.scheduleAtFixedRate(m_getNetworkInfoTask, 0, 10 * 1000);
        }
    }

    public void startSearchNetworkResult(DataValue data) {
        getNetworkSearchState();
    }

    public void stopSearchNetworkResult() {
        if (m_searchNetworkResultTask != null) {
            m_searchNetworkResultTask.cancel();
            m_searchNetworkResultTask = null;
        }
    }

    public void stopGetNetworkRegisterState() {
        if (m_getNetworkRegisterStateTask != null) {
            m_getNetworkRegisterStateTask.cancel();
            m_getNetworkRegisterStateTask = null;
        }
    }

    private void startGetRegisterStateTask() {
        if (m_getNetworkRegisterStateTask == null) {
            m_getNetworkRegisterStateTask = new GetNetworkRegisterStateTask();
            m_getNetworkInfoRollTimer.scheduleAtFixedRate(m_getNetworkRegisterStateTask, 0, 3 * 1000);
        }
    }

    public void startRegisterNetwork(final int networId) {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpRegisterNetwork.RegisterNetwork(networId, response -> {
            if (response.isOk()) {
                startGetRegisterStateTask();
//					sendBroadcast(response, MessageUti.NETWORK_REGESTER_NETWORK_REQUEST);
            }
        }));
    }

    public void getNetworkSearchState() {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpSearchNetwork.SearchNetwork(response -> {
            if (response.isOk()) {
                Log.v("NetworkSearchResult", "Success");
                if (m_searchNetworkResultTask == null) {
                    m_searchNetworkResultTask = new SearchNetworkResultTask();
                    m_getNetworkInfoRollTimer.scheduleAtFixedRate(m_searchNetworkResultTask, 0, 5 * 1000);
                }
            } else {
                Log.v("NetworkSearchResult", "Failed");
            }

//    			sendBroadcast(response, MessageUti.NETWORK_SEARCH_NETWORK_REQUSET);
        }));
    }

    public List<NetworkItem> getNetworkList() {
        return m_NetworkList;
    }

    public void GetNetworkSettings(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpGetNetworkSettings.GetNetworkSettings(response -> {
            if (response.isOk()) {
                m_network_setting_result = response.getModelResult();
//                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetselectionMode);
//                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetworkMode);
//                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetworkBand);
                m_network_mode = m_network_setting_result.NetworkMode;
                m_network_selection = m_network_setting_result.NetselectionMode;
            }

//    			sendBroadcast(response, MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
        }));
    }

    public void SetNetworkSettings(DataValue data) {
        final int networkMode = (Integer) data.getParamByKey("network_mode");
        final int netselectionMode = (Integer) data.getParamByKey("netselection_mode");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSetNetworkSettings.SetNetworkSettings(networkMode,
                netselectionMode,
                response -> {
                    if (response.isOk()) {
//                		Log.v("GetNetworkSettingsTest", "Yes");
                        m_network_mode = networkMode;
                        m_network_selection = netselectionMode;
                    }

//    			sendBroadcast(response, MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
                }));
    }

    class GetNetworkInfoTask extends TimerTask {
        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpGetNetworkInfo.GetNetworkInfo(response -> {
                if (response.isOk()) {
                    NetworkInfoResult networkInfoResult = response.getModelResult();
                    m_networkInfo.setValue(networkInfoResult);
                }

//        			sendBroadcast(response, MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET);
            }));
        }
    }

    class SearchNetworkResultTask extends TimerTask {

        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSearchNetworkResult.SearchNetworkResult(response -> {
                boolean stop_waiting = false;
                boolean is_error = false;

                if (response.isOk()) {
                    try {
                        NetworkItemList networkItemList = response.getModelResult();
                        if (networkItemList.SearchState == SEARCH_SUCCESSFUL) {
                            m_NetworkList = networkItemList.ListNetworkItem;
                            stopSearchNetworkResult();
                            stop_waiting = true;
                        } else if (networkItemList.SearchState == SEARCH_FAILED || networkItemList.SearchState == SEARCH_NOT_NETWORK) {
                            stop_waiting = true;
                            is_error = true;
                            stopSearchNetworkResult();
                        } else {
                        }
                    } catch (Exception e) {
                        stopSearchNetworkResult();
                        stop_waiting = true;
                        is_error = true;
                    }
                } else {
                    //Log
                    stop_waiting = true;
                    is_error = true;
                }
                Intent megIntent = new Intent(MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra("needStopWaiting", stop_waiting);
                megIntent.putExtra("isError", is_error);
                m_context.sendBroadcast(megIntent);
            }));
        }

    }

    class GetNetworkRegisterStateTask extends TimerTask {

        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpGetNetworkRegisterState.GetNetworkRegisterState(response -> {
                if (response.isOk()) {
                    NetworkRegisterStateResult registerResult = response.getModelResult();
//		                    		Log.v("NetworkRegisterTest", "" + registerResult.State);
                    if (registerResult.State == 0 || registerResult.State == 2 || registerResult.State == 3) {
                        stopGetNetworkRegisterState();
                    }
                }
            }));
        }
    }
}
