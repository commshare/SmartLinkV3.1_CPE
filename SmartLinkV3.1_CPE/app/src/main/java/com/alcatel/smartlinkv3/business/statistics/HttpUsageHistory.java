package com.alcatel.smartlinkv3.business.statistics;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUsageHistory {
	
/******************** GetUsageHistory  **************************************************************************************/	
	public static class GetUsageRecord extends BaseRequest
    {			
        public GetUsageRecord(IHttpFinishListener callback)
        {
        	super("Statistics", "GetUsageRecord", "7.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetUsageRecordResponse(m_finsishCallback);
            return new DataResponse<>(UsageRecordResult.class, m_finsishCallback);
        }
    }
	
	/******************** SetUsageRecordClear  **************************************************************************************/	
	public static class SetUsageRecordClear extends BaseRequest
    {	
		String m_strUsageCleartime = new String();
		
        public SetUsageRecordClear(String Cleartime, IHttpFinishListener callback)
        {
        	super("Statistics", "SetUsageRecordClear", "7.2", callback);
        	m_strUsageCleartime = Cleartime;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
            JSONObject usageClearTime = new JSONObject();
            usageClearTime.put("clear_time", m_strUsageCleartime);

            m_requestParamJson.put(ConstValue.JSON_PARAMS, usageClearTime);
        }
    }
}
