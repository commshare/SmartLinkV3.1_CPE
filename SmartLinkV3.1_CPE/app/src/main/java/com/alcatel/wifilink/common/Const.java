package com.alcatel.wifilink.common;

public class Const {
	public static final String REG_STR = "[^a-zA-Z0-9-+!@$#\\^&*]";
	//// TODO: 17-3-20 this hint text says 8-16 characters, but the default password 'admin' is only 5 characters. 
	public static final int LOGIN_PASSWD_MIN = 5;//8; 
	public static final int LOGIN_PASSWD_MAX = 16;
	public static String DATE_FORMATE = "yyyy-MM-dd";
	public static String DATE_CALL_LOG_FORMATE = "MM/dd/yyyy";
	public static String DATE_CALL_LOG_TIME_FORMATE = "HH:mm";
	public static String SMS_SNED_ID = "com.alcatel.cpe.business.sms.sendsms.sendid";
	public static String SMS_SNED_STATUS = "com.alcatel.cpe.business.sms.sendsms.sendstatus";
	public final static String ACTION_NETWORK_DISCONNECT = "com.alcatel.cpe.NETWORK_DISCONNECTED";
}
