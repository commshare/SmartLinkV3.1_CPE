package com.alcatel.smartlinkv3.business.statistics;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUsageSettings {
	
/******************** GetUsageSettings  **************************************************************************************/	
	public static class GetUsageSettings extends BaseRequest
    {			
        public GetUsageSettings(IHttpFinishListener callback)
        {
        	super("Statistics", "GetUsageSettings", "7.3", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetUsageSettingsResponse(m_finsishCallback);
            return new DataResponse<>(UsageSettingsResult.class, MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET, m_finsishCallback);
        }
    }

	
	/******************** set Usage setting **************************************************************************************/
	public static class SetUsageSettings extends BaseRequest {

		public UsageSettingsResult m_result = new UsageSettingsResult();

		public SetUsageSettings(UsageSettingsResult result, IHttpFinishListener callback) {
			super("Statistics", "SetUsageSettings", "7.4", callback);
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
	}
}
