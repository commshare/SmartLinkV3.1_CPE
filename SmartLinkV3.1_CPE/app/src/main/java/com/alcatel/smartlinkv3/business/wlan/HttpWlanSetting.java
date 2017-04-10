package com.alcatel.smartlinkv3.business.wlan;


import android.util.Log;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpWlanSetting {

	/******************** get wlan setting **************************************************************************************/
	public static class GetWlanSetting extends BaseRequest {
		
		public GetWlanSetting(IHttpFinishListener callback) {
			super("Wlan", "GetWlanSettings", "5.4", callback);
			setBroadcastAction(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetWlanSettingResponse(m_finsishCallback);
		}

	}

	public static class GetWlanSettingResponse extends BaseResponse {

		WlanSettingResult m_result = new WlanSettingResult();
		WlanNewSettingResult m_New_result = new WlanNewSettingResult();
		public GetWlanSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}
		
		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result=gson.fromJson(strJsonResult, WlanSettingResult.class);
			if(m_result.CountryCode.isEmpty())
			{
				m_New_result = gson.fromJson(strJsonResult, WlanNewSettingResult.class);
				set_Prase_new_Setting_Result(m_result,m_New_result);
				m_result.New_Interface=true;
			}
		}
		
		protected enum WlanAPMode 
		{	
			E_JRD_WIFI_MODE_2G(0),
			E_JRD_WIFI_MODE_5G(1),
			E_JRD_WIFI_MODE_2G_2G(2),
			E_JRD_WIFI_MODE_5G_5G(3);
			
			private WlanAPMode(int mode) 
			{
				this.mMode = mode;
			}
			
			private int mMode;

			public int getmMode() 
			{
				return mMode;
			}	
		}


		protected void set_Prase_new_Setting_Result(WlanSettingResult m_result,WlanNewSettingResult m_New_result)
		{
			m_result.curr_num=m_New_result.curr_num;
			m_result.WlanAPMode=m_New_result.WlanAPMode;
			int size=m_New_result.APList.size();
			for(int i=0;i<size;i++)
			{
				//WlanAPID_2G
				int type=m_New_result.APList.get(i).WlanAPID;
				if(type==WlanAPMode.E_JRD_WIFI_MODE_2G.getmMode())
				{
					m_result.WlanAPID_2G=m_New_result.APList.get(i).WlanAPID;
					m_result.curr_num_2G=m_New_result.APList.get(i).curr_num;
					m_result.ApStatus_2G=m_New_result.APList.get(i).ApStatus;
					
					m_result.Ssid=m_New_result.APList.get(i).Ssid;
			        m_result.SsidHidden=m_New_result.APList.get(i).SsidHidden;
			        m_result.CountryCode=m_New_result.APList.get(i).CountryCode;
			        m_result.SecurityMode=m_New_result.APList.get(i).SecurityMode;
			        m_result.WpaType=m_New_result.APList.get(i).WpaType;
			        m_result.WpaKey=m_New_result.APList.get(i).WpaKey;
			        m_result.WepType=m_New_result.APList.get(i).WepType;
			        m_result.WepKey=m_New_result.APList.get(i).WepKey;
			        m_result.Channel=m_New_result.APList.get(i).Channel;
			        m_result.ApIsolation=m_New_result.APList.get(i).ApIsolation;
			        m_result.WMode=m_New_result.APList.get(i).WMode;
			        m_result.max_numsta=m_New_result.APList.get(i).max_numsta;
				}
				if(type==WlanAPMode.E_JRD_WIFI_MODE_5G.getmMode())
				{
					m_result.WlanAPID_5G=m_New_result.APList.get(i).WlanAPID;
					m_result.curr_num_5G=m_New_result.APList.get(i).curr_num;
					m_result.ApStatus_5G=m_New_result.APList.get(i).ApStatus;
					m_result.Ssid_5G=m_New_result.APList.get(i).Ssid;
			        m_result.SsidHidden_5G=m_New_result.APList.get(i).SsidHidden;
			        m_result.SecurityMode_5G=m_New_result.APList.get(1).SecurityMode;
			        m_result.WpaType_5G=m_New_result.APList.get(i).WpaType;
			        m_result.WpaKey_5G=m_New_result.APList.get(i).WpaKey;
			        m_result.WepType_5G=m_New_result.APList.get(i).WepType;
			        m_result.WepKey_5G=m_New_result.APList.get(i).WepKey;
			        m_result.Channel_5G=m_New_result.APList.get(i).Channel;
			        m_result.ApIsolation_5G=m_New_result.APList.get(i).ApIsolation;
			        m_result.WMode_5G=m_New_result.APList.get(i).WMode;
			        m_result.max_numsta_5G=m_New_result.APList.get(i).max_numsta;
			    }
			}

			
		}

		@SuppressWarnings("unchecked")
		@Override
		public WlanSettingResult getModelResult() {
			return m_result;
		}
	}	
	
	/******************** set wlan setting **************************************************************************************/
	public static class SetWlanSetting extends BaseRequest {

		public WlanSettingResult m_result = new WlanSettingResult();

		public SetWlanSetting(WlanSettingResult result, IHttpFinishListener callback) {
			super("Wlan", "SetWlanSettings", "5.5", callback);
			setBroadcastAction(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET);
			m_result = result;
		}
		
		public WlanNewSettingResult m_Newresult = new WlanNewSettingResult();

		@Override
		protected void buildHttpParamJson() throws JSONException {

				JSONObject settings = new JSONObject();
				if(m_result.New_Interface)
				{
					settings.put("WlanAPMode", m_result.WlanAPMode); 
					settings.put("curr_num", m_result.curr_num); 
					
					JSONArray member=new JSONArray();
					JSONObject json_2G=new JSONObject();
					json_2G.put("WlanAPID", 0);
					json_2G.put("ApStatus", m_result.ApStatus_2G);
					json_2G.put("WMode", m_result.WMode);
					json_2G.put("Ssid", m_result.Ssid);                
					json_2G.put("SsidHidden", m_result.SsidHidden);
					json_2G.put("Channel", m_result.Channel);
					json_2G.put("SecurityMode", m_result.SecurityMode);
					json_2G.put("WepType", m_result.WepType);   
					json_2G.put("WepKey", m_result.WepKey);
					json_2G.put("WpaType", m_result.WpaType);	          			
					json_2G.put("WpaKey", m_result.WpaKey);
					
					json_2G.put("CountryCode", m_result.CountryCode);  
					json_2G.put("ApIsolation", m_result.ApIsolation);
					json_2G.put("max_numsta", m_result.max_numsta);
					json_2G.put("curr_num", m_result.curr_num_2G);	
					member.put(json_2G);
					
					JSONObject json_5G=new JSONObject();
					json_5G.put("WlanAPID", 1);
					json_5G.put("ApStatus", m_result.ApStatus_5G);
					json_5G.put("WMode", m_result.WMode_5G);
					json_5G.put("Ssid", m_result.Ssid_5G);
					json_5G.put("SsidHidden", m_result.SsidHidden_5G);
					json_5G.put("Channel", m_result.Channel_5G);
					json_5G.put("SecurityMode", m_result.SecurityMode_5G);
					json_5G.put("WepType", m_result.WepType_5G);
					json_5G.put("WepKey", m_result.WepKey_5G);
					json_5G.put("WpaType", m_result.WpaType_5G);				
					json_5G.put("WpaKey", m_result.WpaKey_5G);		
					json_5G.put("CountryCode", m_result.CountryCode);  
					json_5G.put("ApIsolation", m_result.ApIsolation_5G);
					json_5G.put("max_numsta", m_result.max_numsta_5G);
					json_5G.put("curr_num", m_result.curr_num_5G);
					
					member.put(json_5G);
					settings.put("APList",member);	
				}
				else
				{
					settings.put("WlanAPMode", m_result.WlanAPMode);
					settings.put("CountryCode", m_result.CountryCode);
				    settings.put("Ssid", m_result.Ssid);                
				    settings.put("SsidHidden", m_result.SsidHidden);    
				    settings.put("SecurityMode", m_result.SecurityMode);  
				    settings.put("WpaType", m_result.WpaType);	          			
				    settings.put("WpaKey", m_result.WpaKey);
				    settings.put("WepType", m_result.WepType);   
				    settings.put("WepKey", m_result.WepKey);
				    settings.put("Channel", m_result.Channel);
				    settings.put("ApIsolation", m_result.ApIsolation);
				    settings.put("WMode", m_result.WMode);
				    settings.put("max_numsta", m_result.max_numsta);
				
				    settings.put("Ssid_5G", m_result.Ssid_5G);
				    settings.put("SsidHidden_5G", m_result.SsidHidden_5G);
				    settings.put("SecurityMode_5G", m_result.SecurityMode_5G);
				    settings.put("WpaType_5G", m_result.WpaType_5G);				
				    settings.put("WpaKey_5G", m_result.WpaKey_5G);
				    settings.put("WepType_5G", m_result.WepType_5G);
				    settings.put("WepKey_5G", m_result.WepKey_5G);
				    settings.put("Channel_5G", m_result.Channel_5G);
				    settings.put("ApIsolation_5G", m_result.ApIsolation_5G);
				    settings.put("WMode_5G", m_result.WMode_5G);
				    settings.put("max_numsta_5G", m_result.max_numsta_5G);
				}
				String setString = settings.toString();
				Log.d("Json", setString);
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
		}
	}
	
	/*Set WPS Pin*/
	public static class SetWPSPin extends BaseRequest{
		public String m_strPin = "";

		public SetWPSPin(String strPin,IHttpFinishListener callback) {
			super("Wlan", "SetWPSPin", "5.6", callback);
			setBroadcastAction(MessageUti.WLAN_SET_WPS_PIN_REQUSET);
			m_strPin = strPin;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
			    JSONObject settings = new JSONObject();
				settings.put("WpsPin", m_strPin);
				m_requestParamJson.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	
	/*Setb WPS Pbc*/
	public static class SetWPSPbc extends BaseRequest{
		public SetWPSPbc(IHttpFinishListener callback) {
			super("Wlan", "SetWPSPbc", "5.7", callback);
			setBroadcastAction(MessageUti.WLAN_SET_WPS_PBC_REQUSET);
		}
	}

	
	/*get Wlan support mode start*/
	public static class getWlanSupportModeRequest extends BaseRequest{

		public getWlanSupportModeRequest(IHttpFinishListener callback) {
			super("Wlan", "GetWlanSupportMode", "5.8", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new getWlanSupportModeResponse(m_finsishCallback);
			return new DataResponse<>(WlanSupportModeType.class, MessageUti.WLAN_SET_WPS_PBC_REQUSET, m_finsishCallback);
		}
	}

	/*get Wlan support mode end***/
}
