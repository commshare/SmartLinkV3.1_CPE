package com.alcatel.smartlinkv3.business.calllog;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetCallLogList {
	
/******************** GetCallLogList  **************************************************************************************/	
	public static class GetCallLogList extends BaseRequest
    {		
		private int m_nType = 0;//0:All call log,  1:in call log, 2:out call log 3:missed call log


        public GetCallLogList(String strId,int nType,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;        	
        	m_nType = nType;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "CallLog.GetCallLogList");
	        	
	        	JSONObject callLogInfo = new JSONObject();	 
	        	callLogInfo.put("Type", m_nType);	        
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, callLogInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetCallLogListResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetCallLogListResponse extends BaseResponse
    {
		private CallLogListResult m_callLogList;
        
        public GetCallLogListResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_callLogList = gson.fromJson(strJsonResult, CallLogListResult.class);
        }

        @Override
        public CallLogListResult getModelResult() 
        {
             return m_callLogList;
        }
    }
}
