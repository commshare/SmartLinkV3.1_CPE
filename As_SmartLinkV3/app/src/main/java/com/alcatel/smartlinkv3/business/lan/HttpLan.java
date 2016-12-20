package com.alcatel.smartlinkv3.business.lan;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpLan {

	/*Get Lan Settings*/
	public static class getLanSettingsRequest extends BaseRequest{

		public getLanSettingsRequest(String strId, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetLanSettings");

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
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
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
			m_lanInfo = laninfo;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetLanSettings");

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
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
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
