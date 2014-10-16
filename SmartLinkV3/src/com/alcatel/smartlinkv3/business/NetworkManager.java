package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.network.HttpGetNetworkInfo;
import com.alcatel.smartlinkv3.business.network.NetworkInfoResult;
import com.alcatel.smartlinkv3.business.sim.AutoEnterPinStateResult;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpChangePinAndState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.HttpUnlockPinPuk;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class NetworkManager extends BaseManager {
	private NetworkInfoModel m_networkInfo = new NetworkInfoModel();
	private Timer m_getNetworkInfoRollTimer = new Timer();
	private GetNetworkInfoTask m_getNetworkInfoTask = null;
	
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
		if(m_getNetworkInfoTask != null) {
			m_getNetworkInfoTask.cancel();
			m_getNetworkInfoTask = null;
		}
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {

			}
    	}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetNetworkInfoTask();
				}else{
					stopRollTimer();					
					m_networkInfo.clear();
				}
			}
    	}
	}  
	
	public NetworkManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }
	
	public NetworkInfoModel getNetworkInfo() {
		return m_networkInfo;
	}
	
	//GetNetworkInfo ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetNetworkInfoTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("Network", "GetNetworkInfo") != true)
			return;
		if(m_getNetworkInfoTask == null) {
			m_getNetworkInfoTask = new GetNetworkInfoTask();
			m_getNetworkInfoRollTimer.scheduleAtFixedRate(m_getNetworkInfoTask, 0, 10 * 1000);
		}
	}
    
	class GetNetworkInfoTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpGetNetworkInfo.GetNetworkInfo("4.1", new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		NetworkInfoResult networkInfoResult = response.getModelResult();
                    		m_networkInfo.setValue(networkInfoResult);
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    Intent megIntent= new Intent(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
                }
            }));
        } 
	}
}
