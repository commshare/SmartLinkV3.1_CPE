package com.alcatel.smartlinkv3.ui.home.helper.cons;

/**
 * Created by qianli.ma on 2017/6/19.
 */

public class Cons {

    public static String ISCONNECT = "isConnect";

    /* login */
    public static int LOGOUT = 0;
    public static int LOGIN = 1;
    public static int LOGINTIMESOUT = 2;

    /* sim */
    public static int NOWN = 0;
    public static int DETECTED = 1;
    public static int PIN_REQUIRED = 2;
    public static int PUK_REQUIRED = 3;
    public static int SIMLOCK = 4;
    public static int PUK_TIMESOUT = 5;
    public static int ILLEGAL = 6;
    public static int READY = 7;
    public static int INITING = 8;

    /* connect */
    public static int DISCONNECTED = 0;
    public static int CONNECTING = 1;
    public static int CONNECTED = 2;
    public static int DISCONNECTING = 3;

    /* networkinfo */
    public static int UNKNOW = -1;
    public static int NOSERVER = 0;
    public static int GPRS = 1;
    public static int EDGE = 2;
    public static int HSPA_PLUS = 3;
    public static int HSUPA = 4;
    public static int UMTS = 5;
    public static int HSPA = 6;
    public static int DC_HSPA_PLUS = 7;
    public static int LTE = 8;
    public static int LTE_PLUS = 9;
    public static int CDMA = 10;
    public static int GSM = 11;
    public static int EVDO = 12;
    public static int LTE_FDD = 13;
    public static int LTE_TDD = 14;
    public static int CDMA_Ehrpd = 15;

    public static int ROAMING = 0;
    public static int NOROAMING = 1;

    public static int NOSERVICE = -1;
    public static int LEVEL_0 = 0;
    public static int LEVEL_1 = 1;
    public static int LEVEL_2 = 2;
    public static int LEVEL_3 = 3;
    public static int LEVEL_4 = 4;
    public static int LEVEL_5 = 5;

    /* UsageSetting */
    public static int MB = 0;
    public static int GB = 1;
    public static int KB = 2;

    public static int DISABLE = 0;
    public static int ENABLE = 1;

    public static int DISABLE_NOTAUTODISCONNECT = 0;
    public static int ENABLE_AUTODISCONNECT = 1;
    
    /* Device */
    public static  int WEBUI_LOGIN = 0;
    public static  int CONNECT_NOTLOGIN = 1;

    public static  int USB_CONNECT =0;
    public static  int WIFI_CONNECT =1;


}
