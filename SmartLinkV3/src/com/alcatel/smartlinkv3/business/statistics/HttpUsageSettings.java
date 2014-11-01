package com.alcatel.smartlinkv3.business.statistics;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.business.wlan.HttpWlanSetting.SetWlanSettingResponse;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpUsageSettings {
	
/******************** GetUsageSettings  **************************************************************************************/	
	public static class GetUsageSettings extends BaseRequest
    {			
        public GetUsageSettings(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetUsageSettings");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetUsageSettingsResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetUsageSettingsResponse extends BaseResponse
    {
		private UsageSettingsResult m_UsageSettings;
        
        public GetUsageSettingsResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_UsageSettings = gson.fromJson(strJsonResult, UsageSettingsResult.class);
        }

        @Override
        public UsageSettingsResult getModelResult() 
        {
             return m_UsageSettings;
        }
    }
	
	/******************** set Usage setting **************************************************************************************/
	public static class SetUsageSettings extends BaseRequest {

		public UsageSettingsResult m_result = new UsageSettingsResult();

		public SetUsageSettings(String strId,	UsageSettingsResult result, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_result.setValue(result);
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetUsageSettings");
				
				JSONObject settings = new JSONObject();
				settings.put("BillingDay", m_result.BillingDay);
				settings.put("MonthlyPlan", m_result.MonthlyPlan);
				settings.put("UsedData", m_result.UsedData);
				settings.put("TimeLimitFlag", m_result.TimeLimitFlag);
				settings.put("TimeLimitTimes", m_result.TimeLimitTimes);
				settings.put("UsedTimes", m_result.UsedTimes);
				settings.put("AutoDisconnFlag", m_result.AutoDisconnFlag);
				
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetUsageSettingsResponse(m_finsishCallback);
		}

	}
	

	public static class SetUsageSettingsResponse extends BaseResponse {

		
		public SetUsageSettingsResponse(IHttpFinishListener callback) {
			super(callback);
		}
		
		@Override
		protected void parseContent(String strJsonResult) {
		
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}	
	}

}
