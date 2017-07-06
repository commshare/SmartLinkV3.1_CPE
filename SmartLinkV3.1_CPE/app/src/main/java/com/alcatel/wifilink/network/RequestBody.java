package com.alcatel.wifilink.network;

/**
 * Created by tao.j on 2017/6/14.
 */

public class RequestBody {
    String jsonrpc = "2.0";
    String method;
    Object params;
    String id = "1.8";

    public RequestBody(String method) {
        this.method = method;
        params = new Object();
    }

    public RequestBody(String method, Object params) {
        this.method = method;
        this.params = params;
    }
}
