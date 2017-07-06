package com.alcatel.wifilink.business.wanguide;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.BooleanResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qianli.ma on 2017/5/24.
 */

public class HttpWan {

    /*Get Lan Settings*/
    public static class getWanSettingsRequest extends BaseRequest {

        public getWanSettingsRequest(LegacyHttpClient.IHttpFinishListener callback) {
            //super("WAN", "GetWanSettings", "23.1", callback);
            // TODO: 2017/5/26 当前版本没有把GetWanSetting烧录到硬件,暂且使用通配符略过权限判断 
            super("*", "GetWanSettings", "23.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
            /* *** 需要在MessageUti类中声明方法标记以及方法名*** */
            return new DataResponse<>(WanInfo.class, MessageUti.WAN_GET_WAN_SETTINGS, m_finsishCallback);
        }
    }

    /*Set Lan Settings*/
    public static class setWanSettinsRequest extends BaseRequest {
        private WanInfo m_wanInfo = new WanInfo();

        public setWanSettinsRequest(WanInfo waninfo, LegacyHttpClient.IHttpFinishListener callback) {
            //super("WAN", "SetWanSettings", "23.2", callback);
            super("*", "SetWanSettings", "23.2", callback);
            m_wanInfo = waninfo;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {

            JSONObject jWaninfo = new JSONObject();
            jWaninfo.put("SubNetMask", m_wanInfo.getSubNetMask());
            jWaninfo.put("Gateway", m_wanInfo.getGateway());
            jWaninfo.put("IpAddress", m_wanInfo.getIpAddress());
            jWaninfo.put("Mtu", m_wanInfo.getMtu());
            jWaninfo.put("ConnectType", m_wanInfo.getConnectType());
            jWaninfo.put("PrimaryDNS", m_wanInfo.getPrimaryDNS());
            jWaninfo.put("SecondaryDNS", m_wanInfo.getSecondaryDNS());
            jWaninfo.put("Account", m_wanInfo.getAccount());
            jWaninfo.put("Password", m_wanInfo.getPassword());
            jWaninfo.put("Status", m_wanInfo.getStatus());
            jWaninfo.put("StaticIpAddress", m_wanInfo.getStaticIpAddress());
            jWaninfo.put("pppoeMtu", m_wanInfo.getPppoeMtu());

            m_requestParamJson.put(ConstValue.JSON_PARAMS, jWaninfo);
        }

        @Override
        public BaseResponse createResponseObject() {
            return new BooleanResponse(null, m_finsishCallback);
        }
    }

}
