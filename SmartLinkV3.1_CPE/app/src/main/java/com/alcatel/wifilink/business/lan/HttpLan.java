package com.alcatel.wifilink.business.lan;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.BooleanResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpLan {

    /*Get Lan Settings*/
    public static class getLanSettingsRequest extends BaseRequest {

        public getLanSettingsRequest(IHttpFinishListener callback) {
            super("LAN", "GetLanSettings", "11.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
            return new DataResponse<>(LanInfo.class, MessageUti.LAN_GET_LAN_SETTINGS, m_finsishCallback);
        }
    }

    /*Set Lan Settings*/
    public static class setLanSettinsRequest extends BaseRequest {

        private LanInfo m_lanInfo = new LanInfo();

        public setLanSettinsRequest(LanInfo laninfo, IHttpFinishListener callback) {
            super("LAN", "SetLanSettings", "11.2", callback);
            m_lanInfo = laninfo;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {
            JSONObject jLaninfo = new JSONObject();

            jLaninfo.put("DNSMode", m_lanInfo.getDNSMode());
            jLaninfo.put("DNSAddress1", m_lanInfo.getDNSAddress1());
            jLaninfo.put("DNSAddress2", m_lanInfo.getDNSAddress2());
            jLaninfo.put("host_name", m_lanInfo.getHost_name());

            jLaninfo.put("IPv4IPAddress", m_lanInfo.getIPv4IPAddress());
            jLaninfo.put("SubnetMask", m_lanInfo.getSubnetMask());
            jLaninfo.put("DHCPServerStatus", m_lanInfo.getDHCPServerStatus());
            jLaninfo.put("StartIPAddress", m_lanInfo.getStartIPAddress());
            jLaninfo.put("EndIPAddress", m_lanInfo.getEndIPAddress());

            jLaninfo.put("DHCPLeaseTime", m_lanInfo.getDHCPLeaseTime());
            jLaninfo.put("MacAddress", m_lanInfo.getMacAddress());
            m_requestParamJson.put(ConstValue.JSON_PARAMS, jLaninfo);
        }

        @Override
        public BaseResponse createResponseObject() {
            return new BooleanResponse(null, m_finsishCallback);
        }
    }
}
