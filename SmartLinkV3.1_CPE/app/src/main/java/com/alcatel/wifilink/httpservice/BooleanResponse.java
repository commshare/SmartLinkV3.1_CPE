package com.alcatel.wifilink.httpservice;

/**
 * Created by zen on 17-3-21.
 */

public class BooleanResponse extends BaseResponse {

    private Boolean m_blRes = false;

    public BooleanResponse(String action, LegacyHttpClient.IHttpFinishListener callback) {
        super(action, callback);
    }

    @Override
    protected void parseContent(String strJsonResult) {
        if (!strJsonResult.isEmpty()) {
            m_blRes = true;
        }
    }

    @Override
    public Boolean getModelResult() {
        return m_blRes;
    }
}
