package com.alcatel.smartlinkv3.httpservice;

/**
 * Created by zen on 17-3-21.
 */

public class BooleanResponse extends BaseResponse {

    private Boolean m_blRes = false;

    public BooleanResponse(HttpRequestManager.IHttpFinishListener callback) {
        super(callback);
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
