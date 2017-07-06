package com.alcatel.wifilink.business.statistics;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

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
            return new DataResponse<>(UsageRecordResult.class,
                    MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET,
                    m_finsishCallback);
        }
    }
	
	/******************** SetUsageRecordClear  **************************************************************************************/	
	public static class SetUsageRecordClear extends BaseRequest
    {	
		String m_strUsageCleartime = new String();
		
        public SetUsageRecordClear(String Cleartime, IHttpFinishListener callback)
        {
        	super("Statistics", "SetUsageRecordClear", "7.2", callback);
            setBroadcastAction(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET);
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
