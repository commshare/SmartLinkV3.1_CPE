package com.alcatel.smartlinkv3.business.lan;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.BooleanResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpLan {

	/*Get Lan Settings*/
	public static class getLanSettingsRequest extends BaseRequest{

		public getLanSettingsRequest(IHttpFinishListener callback) {
			super("GetLanSettings", "11.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new LanSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class LanSettingsResponse extends BaseResponse{

		private LanInfo m_lanInfo=null;
		public LanSettingsResponse(IHttpFinishListener callback) {
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
	public static class LanSettinsRequest extends BaseRequest{

		private LanInfo m_lanInfo = new LanInfo();
		public LanSettinsRequest(LanInfo laninfo, IHttpFinishListener callback) {
			super("SetLanSettings", "11.2", callback);
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
			return new BooleanResponse(m_finsishCallback);
		}
	}
}
