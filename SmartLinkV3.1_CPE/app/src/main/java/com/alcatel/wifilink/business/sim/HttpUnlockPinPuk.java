package com.alcatel.wifilink.business.sim;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUnlockPinPuk {

    /******************** UnlockPin  **************************************************************************************/
    public static class UnlockPin extends BaseRequest {
        private String m_strPin = new String();

        public UnlockPin(String strPin, IHttpFinishListener callback) {
            super("SIM", "UnlockPin", "2.2", callback);
            setBroadcastAction(MessageUti.SIM_UNLOCK_PIN_REQUEST);
            m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {
            JSONObject pin = new JSONObject();
            pin.put("Pin", m_strPin);

            m_requestParamJson.put(ConstValue.JSON_PARAMS, pin);
        }
    }

    /******************** UnlockPuk  **************************************************************************************/
    public static class UnlockPuk extends BaseRequest {
        private String m_strPuk = new String();
        private String m_strPin = new String();

        public UnlockPuk(String strPuk, String strPin, IHttpFinishListener callback) {
            super("SIM", "UnlockPuk", "2.3", callback);
            setBroadcastAction(MessageUti.SIM_UNLOCK_PUK_REQUEST);
            m_strPuk = strPuk;
            m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {
            JSONObject pinPuk = new JSONObject();
            pinPuk.put("Puk", m_strPuk);
            pinPuk.put("Pin", m_strPin);

            m_requestParamJson.put(ConstValue.JSON_PARAMS, pinPuk);
        }
    }
}
