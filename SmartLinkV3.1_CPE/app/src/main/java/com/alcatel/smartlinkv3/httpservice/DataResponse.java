package com.alcatel.smartlinkv3.httpservice;


import com.google.gson.Gson;

/**
 * Created by zen on 17-3-21.
 */

public class DataResponse<T> extends BaseResponse {
    T data;
    Class<T> clazz;

//    public DataResponse(Class<T> classOfT, LegacyHttpClient.IHttpFinishListener callback) {
//        super(callback);
//        clazz = classOfT; //(Class<T>) data.getClass()
//    }

    public DataResponse(Class<T> classOfT, String action, LegacyHttpClient.IHttpFinishListener callback) {
        super(action, callback);
        clazz = classOfT; //(Class<T>) data.getClass()
    }

    @Override
    protected void parseContent(String strJsonResult) {
        Gson gson = new Gson();

        data = gson.fromJson(strJsonResult, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getModelResult() {
        return data;
    }
}
