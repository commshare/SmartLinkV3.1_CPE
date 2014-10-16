package com.alcatel.smartlinkv3.business.statistics;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.GetUsageSettings");

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
	
	/******************** SetBillingDay  **************************************************************************************/	
	public static class SetBillingDay extends BaseRequest
    {	
		private int m_nBillingDay = 1;
        public SetBillingDay(String strId,int nBillingDay,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nBillingDay = nBillingDay;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetBillingDay");

	        	JSONObject billingDay = new JSONObject();
	        	billingDay.put("BillingDay", m_nBillingDay);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, billingDay);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetBillingDayResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetBillingDayResponse extends BaseResponse
    {   
        public SetBillingDayResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** SetLimitValue  **************************************************************************************/	
	public static class SetLimitValue extends BaseRequest
    {	
		private long m_lLimitValue = 1;//Limit value (Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set)
        public SetLimitValue(String strId,long lLimitValue,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_lLimitValue = lLimitValue;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetLimitValue");

	        	JSONObject limitValue = new JSONObject();
	        	limitValue.put("LimitValue", m_lLimitValue);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, limitValue);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetLimitValueResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetLimitValueResponse extends BaseResponse
    {   
        public SetLimitValueResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** SetTotalValue  **************************************************************************************/	
	public static class SetTotalValue extends BaseRequest
    {	
		private long m_lTotalValue = 1;//Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set
        public SetTotalValue(String strId,long lTotalValue,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_lTotalValue = lTotalValue;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetTotalValue");

	        	JSONObject totalValue = new JSONObject();
	        	totalValue.put("TotalValue", m_lTotalValue);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, totalValue);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetTotalValueResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetTotalValueResponse extends BaseResponse
    {   
        public SetTotalValueResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** SetDisconnectOvertime  **************************************************************************************/	
	public static class SetDisconnectOvertime extends BaseRequest
    {	
		private int m_nOverTime = 5;//(Unit is minute, Min:1, Max:43200, default:5)
        public SetDisconnectOvertime(String strId,int nOverTime,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nOverTime = nOverTime;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetDisconnectOvertime");

	        	JSONObject overTime = new JSONObject();
	        	overTime.put("Overtime", m_nOverTime);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, overTime);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetDisconnectOvertimeResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetDisconnectOvertimeResponse extends BaseResponse
    {   
        public SetDisconnectOvertimeResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	/******************** SetDisconnectOvertimeState  **************************************************************************************/	
	public static class SetDisconnectOvertimeState extends BaseRequest
    {	
		private boolean m_bOverTimeState = false;//Enable auto disconnect by overtime:1, Disable auto disconnect by overtime:0, Default:0
        public SetDisconnectOvertimeState(String strId,boolean bOverTimeState,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_bOverTimeState = bOverTimeState;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetDisconnectOvertimeState");

	        	JSONObject overTimeState = new JSONObject();
	        	overTimeState.put("OvertimeState", m_bOverTimeState == true?1:0);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, overTimeState);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetDisconnectOvertimeStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetDisconnectOvertimeStateResponse extends BaseResponse
    {   
        public SetDisconnectOvertimeStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** SetDisconnectOverflowState  **************************************************************************************/	
	public static class SetDisconnectOverflowState extends BaseRequest
    {	
		private boolean m_bOverFlowState = false;//Enable auto disconnect by total flow:1, Disable auto disconnect by total flow:0, Default:0
        public SetDisconnectOverflowState(String strId,boolean bOverFlowState,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_bOverFlowState = bOverFlowState;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.SetDisconnectOverflowState");

	        	JSONObject overFlowState = new JSONObject();
	        	overFlowState.put("OverflowState", m_bOverFlowState == true?1:0);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, overFlowState);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetDisconnectOverflowStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetDisconnectOverflowStateResponse extends BaseResponse
    {   
        public SetDisconnectOverflowStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
}
