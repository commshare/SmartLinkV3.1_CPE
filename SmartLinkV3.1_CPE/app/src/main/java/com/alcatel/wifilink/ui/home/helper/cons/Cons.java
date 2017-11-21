package com.alcatel.wifilink.ui.home.helper.cons;

import java.util.Set;

public class Cons {

    public static String SP_FILE = "SP_FILE";// 临时文件
    public static String GUIDE_RX = "GUIDE_RX";// 向导标记
    public static String LOGIN_REMEM = "LOGIN_REMEM";// 登陆记住密码
    public static String LOGIN_RXPSD = "LOGIN_RXPSD";// 登陆密码
    public static String WIZARD_RX = "WIZARD_RX";// 连接设置向导(SIM|WAN)
    public static String DATAPLAN_RX = "DATAPLAN_RX";// 设置SIM流量向导(SIM)
    public static String WANMODE_RX = "WANMODE_RX";// 设置WAN口向导(WAN)
    public static String WIFIINIT_RX = "WIFIINIT_RX";// 设置wifi初始化
    public static String PIN_REMEM_STR_RX = "PIN_REMEM_STR_RX";// 缓存的PIN码
    public static String PIN_REMEM_FLAG_RX = "PIN_REMEM_FLAG_RX";// 记住PIN码标记
    public static int PIN_FLAG = 0;// pin标记
    public static int PUK_FLAG = 1;// puk标记
    public static int MODE_2P4G_ONLY = 0;// 只有3G
    public static int MODE_5G_ONLY = 1;// 只有5G
    public static int MODE_2P4G_2P4G$GUEST = 2;// 3G以及3G访客模式
    public static int MODE_5G_5G$GUEST = 3;// 5G以及5G访客模式
    public static int SECURITY_DISABLE = 0;
    public static int SECURITY_WEP = 1;
    public static int SECURITY_WPA = 2;
    public static int SECURITY_WPA2 = 3;
    public static int SECURITY_WPA_WPA2 = 4;


    public static final int NEED_ENCRYPT = 1;// 1:表示要加密 0:不需要加密
    public static String IP_PRE = "http://";
    public static String IP_SUFFIX = "/";
    public static int AUTO_LOGOUT_PERIOD = 7 * 60 * 1000;
    public static int PERIOD = 5000;// 5秒获取
    public static String PAGE = "PAGE";// 主页的fragment切换标记
    public static String GATEWAY = "http://192.168.1.1";// 默认网关
    public static String WIFI_GUIDE_FLAG = "WIFI_GUIDE_FLAG";// WIFI向导标记
    public static String DATA_PLAN_FLAG = "DATA_PLAN_FLAG";// 月流量向导标记
    public static String WAN_MODE_FLAG = "WAN_MODE_FLAG";// 连接向导标记
    public static String FIRST_RUN = "FIRST_RUN";// 连接向导标记

    public static String CUSTOM_ID_1 = "E1";// 需要定制的版本customId

    /* 标记 */
    public static int FLAG_PPPOE = 0;
    public static int FLAG_DHCP = 1;
    public static int FLAG_STATIC_IP = 2;

    public static final int MAIN = 0;
    public static final int WIFI = 1;
    public static final int SMS = 2;
    public static final int SETTING = 3;

    public static String TYPE_SIM = "SIM";// 以SIM卡模式连接
    public static String TYPE_WAN = "WAN";// 以WAN模式连接

    public static String ISCONNECT = "isConnect";
    public static String LOGIN_CHECK = "LOGIN_CHECK";
    public static String LOGIN_PSD = "LOGIN_PSD";

    /* login */
    public static int LOGOUT = 0;
    public static int LOGIN = 1;
    public static int LOGINTIMESOUT = 2;

    /* sim */
    public static final int NOWN = 0;
    public static final int DETECTED = 1;
    public static final int PIN_REQUIRED = 2;
    public static final int PUK_REQUIRED = 3;
    public static final int SIMLOCK = 4;
    public static final int PUK_TIMESOUT = 5;
    public static final int ILLEGAL = 6;
    public static final int READY = 7;
    public static final int INITING = 8;

    /* connect */
    public static final int DISCONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
    public static final int DISCONNECTING = 3;

    /* networkinfo */
    public static final int UNKNOW = -1;
    public static final int NOSERVER = 0;
    public static final int GPRS = 1;
    public static final int EDGE = 2;
    public static final int HSPA = 3;
    public static final int HSUPA = 4;
    public static final int UMTS = 5;
    public static final int HSPA_PLUS = 6;
    public static final int DC_HSPA_PLUS = 7;
    public static final int LTE = 8;
    public static final int LTE_PLUS = 9;
    public static final int CDMA = 10;
    public static final int GSM = 11;
    public static final int EVDO = 12;
    public static final int LTE_FDD = 13;
    public static final int LTE_TDD = 14;
    public static final int CDMA_Ehrpd = 15;

    public static int ROAMING = 0;
    public static int NOROAMING = 1;

    public static final int NOSERVICE = -1;
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    public static final int LEVEL_5 = 5;

    /* UsageSetting */
    public static int MB = 0;
    public static int GB = 1;
    public static int KB = 2;

    public static int DISABLE = 0;
    public static int ENABLE = 1;

    public static int DISABLE_NOTAUTODISCONNECT = 0;
    public static int ENABLE_AUTODISCONNECT = 1;

    /* Device */
    public static int WEBUI_LOGIN = 0;
    public static int CONNECT_NOTLOGIN = 1;

    public static int USB_CONNECT = 0;
    public static int WIFI_CONNECT = 1;

    /* sms */
    public static int SMS_INITING = 1;
    public static int SMS_COMPLETE = 0;

    public static int DELETE_ALL_SMS = 0;
    public static int DELETE_ONE_SMS = 1;
    public static int DELETE_SESSION_SMS = 2;
    public static int DELETE_MORE_SMS = 3;

    public static final int READ = 0;
    public static final int UNREAD = 1;
    public static final int SENT = 2;
    public static final int SENT_FAILED = 3;
    public static final int REPORT = 4;
    public static final int FLASH = 5;
    public static final int DRAFT = 6;

    public static final String SEND_SMS_FAILED = "060601";
    public static final String FAIL_STILL_SENDING_LAST_MESSAGE = "060602";
    public static final String FAIL_WITH_STORE_SPACE_FULL = "060603";

    public static final String SAVE_SMS_FAILED = "060801";
    public static final String SAVE_FAIL_WITH_STORE_SPACE_FULL = "060802";
    public static final String DELETE_SMS_FAILED = "060501";
    public static final String GET_SMS_CONTACT_LIST_FAILED = "060201";
    public static final String GET_SMS_CONTENT_LIST_FAILED = "060301";
    public static final String GET_SEND_SMS_STATUS_FAILED = "060701";

    public static final String PASSWORD_IS_NOT_CORRECT = "010101";
    public static final String OTHER_USER_IS_LOGIN = "010102";
    public static final String DEVICE_REBOOT = "010103";
    public static final String GUEST_AP_OR_WEBUI = "010104";

    public static final int NONE = 0;
    public static final int SENDING = 1;
    public static final int SUCCESS = 2;
    public static final int FAIL_STILL_SENDING_LAST_MSG = 3;
    public static final int FAIL_WITH_MEMORY_FULL = 4;
    public static final int FAIL = 5;

    public static final String HH70 = "HH70";
    public static final String HH40 = "HH40";

    public static final int PPPOE = 0;
    public static final int DHCP = 1;
    public static final int STATIC = 2;

    public static final String GET_LOGIN_STATE_FAILED = "010301";

    public static final String SET_WLAN_SETTINGS_FAILED = "050501";
    public static final String WIFI_IS_OFF = "050502";
    public static final String WPS_IS_WORKING = "050503";
    public static final String GET_SYSTEM_STATUS_FAIL = "050503";

    public static final int PIN_ENABLE_VERIFIED = 2;

    public static final int AUTO = 0;
    public static final int MANUAL = 1;

    public static final String INVALID_PIN = "020201";


}
