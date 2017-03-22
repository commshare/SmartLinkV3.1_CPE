package com.alcatel.smartlinkv3.httpservice;


/**
 * Created by zen on 17-3-21.
 */

public class DataResponse<T> extends BaseResponse {
    T data;

    public DataResponse(HttpRequestManager.IHttpFinishListener callback) {
        super(callback);
    }
}
