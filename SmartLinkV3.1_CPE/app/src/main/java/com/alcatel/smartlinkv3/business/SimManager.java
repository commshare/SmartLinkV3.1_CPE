package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.sim.AutoEnterPinStateResult;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpChangePinAndState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.HttpUnlockPinPuk;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class SimManager extends BaseManager {
	private SimStatusModel m_simStatus = new SimStatusModel();
	private AutoPinState m_autoPinState  = AutoPinState.Disable;
	private Timer m_rollTimer = new Timer();
	
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_simStatus.clear();
	} 
	@Override
	protected void stopRollTimer() {
		m_rollTimer.cancel();
		m_rollTimer.purge();
		m_rollTimer = new Timer();
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				startGetSimStatusTask();
			}else{
				
			}
    	}
	}  
	
	public SimManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
    }
	
	public SimStatusModel getSimStatus() {
		return m_simStatus;
	}
	
	public AutoPinState getAutoPinState() {
		return m_autoPinState;
	}
	
	public void changePinState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "ChangePinState") != true)
			return;
		
		int nState = (Integer) data.getParamByKey("state");
		String strPin = (String) data.getParamByKey("pin");
		LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.ChangePinState(nState,strPin, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
//                	if(strErrcode.length() == 0) {
//                		
//                	}else{
//                		
//                	}
                }else{
                	//Log
                	
                }

    			sendBroadcast(response, MessageUti.SIM_CHANGE_PIN_STATE_REQUEST);
            }
        }));
    } 
	
	//SetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setAutoValidatePinState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "setAutoValidatePinState") != true)
			return;
		
		int nState = (Integer) data.getParamByKey("state");
		String strPin = (String) data.getParamByKey("pin");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.SetAutoValidatePinState(nState,strPin, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST);
            }
        }));
    } 
	
	//GetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void getAutoPinState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "GetAutoValidatePinState") != true)
			return;
		LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.GetAutoValidatePinState(new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		AutoEnterPinStateResult result = response.getModelResult();
//                		m_autoPinState.build(result.State);
//                		Log.v("PINCHECK", "RESULT " + result.State);
                		if(result.State == 1){
                			m_autoPinState = AutoPinState.Enable;
                		}
                		else{
                			m_autoPinState = AutoPinState.Disable;
                		}
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST);
            }
        }));
    } 
	
	//change pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void changePin(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "ChangePinCode") != true)
			return;
		
		String strNewPin = (String) data.getParamByKey("new_pin");
		String strCurrentPin = (String) data.getParamByKey("current_pin");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpChangePinAndState.ChangePinCode(strNewPin,strCurrentPin, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.SIM_CHANGE_PIN_REQUEST);
            }
        }));
    } 
	
	//unlock Puk  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void unlockPuk(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "UnlockPuk") != true)
			return;
		
		String strPuk = (String) data.getParamByKey("puk");
		String strPin = (String) data.getParamByKey("pin");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPuk(strPuk,strPin, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		changeSimStatusGetInterval(true);
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.SIM_UNLOCK_PUK_REQUEST);
            }
        }));
    } 
	
	//unlock Pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void unlockPin(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "UnlockPin") != true)
			return;
		
		String strPin = (String) data.getParamByKey("pin");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPin(strPin, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		changeSimStatusGetInterval(true);
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.SIM_UNLOCK_PIN_REQUEST);
            }
        }));
    } 
	
	//GetSimStatus ////////////////////////////////////////////////////////////////////////////////////////// 
	private GetSimStatusTask m_getSimStatusTask = null;
	private void startGetSimStatusTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "GetSimStatus") != true)
			return;
		
		m_getSimStatusTask = new GetSimStatusTask();
		changeSimStatusGetInterval(false);
	}
	
	private boolean m_bisFastSpeed = false;
	
	private void changeSimStatusGetInterval(boolean bFast) {
		if(m_getSimStatusTask == null)
			return;
		m_bisFastSpeed = bFast;
		int nInterval = 10 * 1000;
        if(bFast) {
        	nInterval = 3 * 1000;
        }
        
        stopRollTimer();
        
        m_getSimStatusTask = new GetSimStatusTask();
    	m_rollTimer.scheduleAtFixedRate(m_getSimStatusTask, 0, nInterval);
  
    }
    
	class GetSimStatusTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpGetSimStatus.GetSimStatus(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {                 	
                	boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        			if(bCPEWifiConnected == false) {
        				return;
        			}
        			            	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();                    	
                    	if(strErrcode.length() == 0) {
                    		SIMStatusResult simStatusResult = response.getModelResult();
                    		SimStatusModel pre = new SimStatusModel();
                    		pre.clone(m_simStatus);
                    		m_simStatus.setValue(simStatusResult);
//                    		Log.v("PINCHECK", "PINSTATUS " + simStatusResult.PinState);
                    		
                    		
                    		if(m_simStatus.m_SIMState == SIMState.Accessable) {
                    			if(m_bisFastSpeed == true)
                    				changeSimStatusGetInterval(false);
                    		}
                    		
                    		if(!m_simStatus.equalTo(pre)) {
        	        			sendBroadcast(response, MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET);
//        	        			Log.v("PINCHECK", "BROADCAST");
                    		}
                    	}else if(strErrcode.equalsIgnoreCase("1") 
                    			||strErrcode.equalsIgnoreCase("6") )
                    	{
                    		m_simStatus.clear();
                    		m_simStatus.m_SIMState = SIMState.NoSim;
    	        			sendBroadcast(response, MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET);
                    	}
                    }else{
                    	//Log
                    }
                }
            }));
        } 
	}
}
