package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSetNetworkSettings {
    public static class SetNetworkSettings extends BaseRequest {

        private int NetworkMode = 0;
        private int NetselectionMode = 0;
        private int NetworkBand = 0;

        public SetNetworkSettings(int networkMode, int netSelectionMode, IHttpFinishListener callback) {
            super("Network", "SetNetworkSettings", "4.7", callback);
            setBroadcastAction(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
            NetworkMode = networkMode;
            NetselectionMode = netSelectionMode;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {
            JSONObject settingInfo = new JSONObject();
            settingInfo.put("NetworkMode", NetworkMode);
            settingInfo.put("NetselectionMode", NetselectionMode);
            settingInfo.put("NetworkBand", NetworkBand);

            m_requestParamJson.put(ConstValue.JSON_PARAMS, settingInfo);
        }
    }
}
