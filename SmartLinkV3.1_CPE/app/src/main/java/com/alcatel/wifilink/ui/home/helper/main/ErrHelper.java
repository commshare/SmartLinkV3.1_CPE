package com.alcatel.wifilink.ui.home.helper.main;

import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.utils.Logs;

/**
 * Created by qianli.ma on 2017/9/17.
 */

public class ErrHelper {

    private static String MA = "ma_main_new";// 可根据需求修改

    /**
     * 日志打印器
     *
     * @param pre   前缀
     * @param e     异常实体
     * @param error 错误码
     */
    public static void errlog(String pre, Throwable e, ResponseBody.Error error) {
        if (e != null) {
            Logs.v(MA, pre + " error: " + e.getMessage());
        }
        if (error != null) {
            Logs.v(MA, pre + " code: " + error.getCode() + "; " + pre + "error msg: " + error.getMessage());
        }
    }
}
