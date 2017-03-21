package com.alcatel.smartlinkv3.business.lan;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpLan {

	/*Get Lan Settings*/
	public static class getLanSettingsRequest extends BaseRequest{

		public getLanSettingsRequest(String strId, IHttpFinishListener callback) {
			super("GetLanSettings", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new getLanSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class getLanSettingsResponse extends BaseResponse{

		private LanInfo m_lanInfo=null;
		public getLanSettingsResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_lanInfo = gson.fromJson(strJsonResult, LanInfo.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public LanInfo getModelResult() {
			// TODO Auto-generated method stub
			return m_lanInfo;
		}
		
	}
	
	/*Set Lan Settings*/
	public static class setLanSettinsRequest extends BaseRequest{

		private LanInfo m_lanInfo = new LanInfo();
		public setLanSettinsRequest(String strId, LanInfo laninfo, IHttpFinishListener callback) {
			super("SetLanSettings", strId, callback);
			m_lanInfo = laninfo;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject jLaninfo = new JSONObject();
				jLaninfo.put("IPv4IPAddress", m_lanInfo.getIPv4IPAddress());
				jLaninfo.put("SubnetMask", m_lanInfo.getSubnetMask());
				jLaninfo.put("DHCPServerStatus", m_lanInfo.getDHCPServerStatus());
				jLaninfo.put("StartIPAddress", m_lanInfo.getStartIPAddress());
				jLaninfo.put("EndIPAddress", m_lanInfo.getEndIPAddress());
				jLaninfo.put("DHCPLeaseTime", m_lanInfo.getDHCPLeaseTime());
				jLaninfo.put("MacAddress", m_lanInfo.getMacAddress());
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, jLaninfo);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new setLanSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class setLanSettingsResponse extends BaseResponse{

		private Boolean m_blRes = false;
		public setLanSettingsResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			if (!strJsonResult.isEmpty()) {
				m_blRes = true;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blRes;
		}
		
	}
}
