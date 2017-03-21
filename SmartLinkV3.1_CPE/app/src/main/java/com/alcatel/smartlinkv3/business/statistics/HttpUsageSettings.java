package com.alcatel.smartlinkv3.business.statistics;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUsageSettings {
	
/******************** GetUsageSettings  **************************************************************************************/	
	public static class GetUsageSettings extends BaseRequest
    {			
        public GetUsageSettings(String strId,IHttpFinishListener callback) 
        {
        	super("GetUsageSettings", strId, callback);
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
			super("SetUsageSettings", strId, callback);
			m_result.setValue(result);
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
				
				JSONObject settings = new JSONObject();
				settings.put("BillingDay", m_result.BillingDay);
				settings.put("MonthlyPlan", m_result.MonthlyPlan);
				settings.put("Unit", m_result.Unit);
				settings.put("AlertValue", m_result.UsageAlertValue);
				settings.put("TimeLimitFlag", m_result.TimeLimitFlag);
				settings.put("TimeLimitTimes", m_result.TimeLimitTimes);
				settings.put("AutoDisconnFlag", m_result.AutoDisconnFlag);

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
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
