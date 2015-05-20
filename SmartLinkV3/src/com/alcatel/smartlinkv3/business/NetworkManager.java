package com.alcatel.smartlinkv3.business;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.network.HttpGetNetworkInfo;
import com.alcatel.smartlinkv3.business.network.HttpGetNetworkSettings;
import com.alcatel.smartlinkv3.business.network.HttpGetNetworkSettings.GetNetworkSettingResult;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetwork;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItem;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItemList;
import com.alcatel.smartlinkv3.business.network.HttpSetNetworkSettings;
import com.alcatel.smartlinkv3.business.network.NetworkInfoResult;
import com.alcatel.smartlinkv3.business.sim.AutoEnterPinStateResult;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpChangePinAndState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.HttpUnlockPinPuk;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.business.user.HttpUser;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class NetworkManager extends BaseManager {
	private NetworkInfoModel m_networkInfo = new NetworkInfoModel();
	private Timer m_getNetworkInfoRollTimer = new Timer();
	private GetNetworkInfoTask m_getNetworkInfoTask = null;
	private SearchNetworkResultTask m_searchNetworkResultTask = null;
	private List<NetworkItem> m_NetworkList = null;
	private GetNetworkSettingResult m_network_setting_result = null;
	
	private int m_network_mode = -1;
	private int m_network_selection = -1;
	
	public final int SEARCH_NOT_NETWORK = 0;
	public final int SEARCHING = 1;
	public final int SEARCH_SUCCESSFUL = 2;
	public final int SEARCH_FAILED = 3;
	
	public int getNetworkMode(){
		return m_network_mode;
	}
	
	public int getNetworkSelection(){
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
		if(m_getNetworkInfoTask != null) {
			m_getNetworkInfoTask.cancel();
			m_getNetworkInfoTask = null;
		}
		
		if(m_searchNetworkResultTask != null) {
			m_searchNetworkResultTask.cancel();
			m_searchNetworkResultTask = null;
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
	
	public void startSearchNetworkResult(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Network", "SearchNetworkResult") != true)
			return;
		getNetworkSearchState();
	}
	
	public void stopSearchNetworkResult() {
		if(m_searchNetworkResultTask != null) {
			m_searchNetworkResultTask.cancel();
			m_searchNetworkResultTask = null;
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
	
	class SearchNetworkResultTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpRequestManager.GetInstance().sendPostRequest(new HttpSearchNetworkResult.SearchNetworkResult("4.3", new IHttpFinishListener(){

				@Override
				public void onHttpRequestFinish(BaseResponse response) {
					// TODO Auto-generated method stub
					String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		
                    		try{
                    			NetworkItemList networkItemList = response.getModelResult();
                    			if(networkItemList.SearchState == SEARCH_SUCCESSFUL){
                    				m_NetworkList = networkItemList.ListNetworkItem;
//                        			Log.v("NetworkSearchResult", m_NetworkList.get(0).ShortName);
//                    				for(NetworkItem tmp : m_NetworkList){
//                    					Log.v("NetworkSearchResult1", Integer.toString(tmp.Rat));
//                    					Log.v("NetworkSearchResult1", Integer.toString(tmp.State));
//                    					Log.v("NetworkSearchResult1", Integer.toString(tmp.NetworkID));
//                    					Log.v("NetworkSearchResult1", tmp.Numberic);
//                    					Log.v("NetworkSearchResult1", tmp.ShortName);
//                    					
//                    				}
                    				stopSearchNetworkResult();
                					
                					Intent megIntent= new Intent(MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET);
                                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                        			m_context.sendBroadcast(megIntent);
                        		}
                    			else if(networkItemList.SearchState == SEARCH_FAILED || networkItemList.SearchState == SEARCH_NOT_NETWORK){
                    				stopSearchNetworkResult();
                    			}
                    			else{
                    				Log.v("NetworkSearchResult", Integer.toString(networkItemList.SearchState));
                    			}
                    		}
                    		catch(Exception e){
                    			Log.v("NetworkSearchResult2", "Exception");
                    			stopSearchNetworkResult();
                    		}
                    	}else{
                    		//Log
                    	}
                    }else{
                    	//Log
                    }
                    
				}
				
			}));
		}
		
	}
	
	private void getNetworkSearchState(){
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSearchNetwork.SearchNetwork("4.2", new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) { 
                		Log.v("NetworkSearchResult", "Success");
                		if(m_searchNetworkResultTask == null) {
                			m_searchNetworkResultTask = new SearchNetworkResultTask();
                			m_getNetworkInfoRollTimer.scheduleAtFixedRate(m_searchNetworkResultTask, 0, 5 * 1000);
                		}
                	}else{
                		Log.v("NetworkSearchResult", "Failed");
                	}
                }
                else{
                	Log.v("NetworkSearchResult", "Failed");
                }
			}
    	}));
	}
	
	public List<NetworkItem> getNetworkList(){
		return m_NetworkList;
	}
	
	public void GetNetworkSettings(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Network", "GetNetworkSettings") != true){
			return;
		}
			
		HttpRequestManager.GetInstance().sendPostRequest(new HttpGetNetworkSettings.GetNetworkSettings("4.6", new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) { 
                		try{
                			m_network_setting_result = response.getModelResult();
//                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetselectionMode);
                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetworkMode);
//                			Log.v("GetNetworkSettingsTest", "" + m_network_setting_result.NetworkBand);
                			m_network_mode = m_network_setting_result.NetworkMode;
                			m_network_selection = m_network_setting_result.NetselectionMode;
                		}
                		catch(Exception e){
                			
                		}
                		
                	}else{
                		//Log
                	}
                }else{
                	//Log
                }
                
                Intent megIntent= new Intent(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
 
			}
			
		}));
	}
	
	public void SetNetworkSettings(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Network", "SetNetworkSettings") != true){
			return;
		}
    	
    	int networkMode = (Integer) data.getParamByKey("network_mode");
    	int netselectionMode = (Integer) data.getParamByKey("netselection_mode");
    	
    	HttpRequestManager.GetInstance().sendPostRequest(new HttpSetNetworkSettings.SetNetworkSettings("4.7", networkMode, netselectionMode, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) { 
//                		Log.v("GetNetworkSettingsTest", "Yes");
                	}else{
                		//Log
                	}
                }else{
                	//Log
                }
                
                Intent megIntent= new Intent(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
			}
    		
    	}));
	}
}
