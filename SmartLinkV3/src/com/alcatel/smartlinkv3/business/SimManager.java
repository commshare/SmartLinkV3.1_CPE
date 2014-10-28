package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.sim.AutoEnterPinStateResult;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpChangePinAndState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.HttpUnlockPinPuk;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	
	//SetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setAutoEnterPinState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "ChangePinState") != true)
			return;
		
		int nState = (Integer) data.getParamByKey("state");
		String strPin = (String) data.getParamByKey("pin");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpAutoEnterPinState.ChangePinState("2.5",nState,strPin, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SIM_CHANGE_PIN_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//GetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void getAutoPinState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "GetAutoValidatePinState") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpAutoEnterPinState.GetAutoValidatePinState("2.6",new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		AutoEnterPinStateResult result = response.getModelResult();
                		m_autoPinState.build(result.State);
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                Intent megIntent= new Intent(MessageUti.SIM_CHANGE_PIN_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//change pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void changePin(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "ChangePinCode") != true)
			return;
		
		String strNewPin = (String) data.getParamByKey("new_pin");
		String strCurrentPin = (String) data.getParamByKey("current_pin");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpChangePinAndState.ChangePinCode("2.4",strNewPin,strCurrentPin, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SIM_CHANGE_PIN_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//unlock Puk  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void unlockPuk(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "UnlockPuk") != true)
			return;
		
		String strPuk = (String) data.getParamByKey("puk");
		String strPin = (String) data.getParamByKey("pin");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPuk("2.3",strPuk,strPin, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SIM_UNLOCK_PUK_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//unlock Pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void unlockPin(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SIM", "UnlockPin") != true)
			return;
		
		String strPin = (String) data.getParamByKey("pin");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPin("2.2",strPin, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SIM_UNLOCK_PIN_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
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
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpGetSimStatus.GetSimStatus("2.1", new IHttpFinishListener() {           
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
                    		
                    		if(m_simStatus.m_SIMState == SIMState.Accessable) {
                    			if(m_bisFastSpeed == true)
                    				changeSimStatusGetInterval(false);
                    		}
                    		
                    		if(!m_simStatus.equalTo(pre)) {
                    			Intent megIntent= new Intent(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET);
        	                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
        	                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        	        			m_context.sendBroadcast(megIntent);
                    		}
                    	}else if(strErrcode.equalsIgnoreCase("1") 
                    			||strErrcode.equalsIgnoreCase("6") )
                    	{
                    		m_simStatus.clear();
                    		m_simStatus.m_SIMState = SIMState.NoSim;
                    		Intent megIntent= new Intent(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET);
    	                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
    	                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    	        			m_context.sendBroadcast(megIntent);
                    	}
                    }else{
                    	//Log
                    }
                }
            }));
        } 
	}
}
