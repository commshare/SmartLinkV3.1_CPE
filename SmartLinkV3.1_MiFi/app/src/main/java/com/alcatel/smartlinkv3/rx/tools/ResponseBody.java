package com.alcatel.smartlinkv3.rx.tools;

/**
 * Created by tao.j on 2017/6/14.
 */

public class ResponseBody<T> {

    String jsonrpc;
    String id;
    Error error;
    T result;

    public Error getError() {
        return error;
    }

    public T getResult() {
        return result;
    }

    public class Error{
        String code;
        String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
