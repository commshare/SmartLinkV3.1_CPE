package com.alcatel.smartlinkv3.common;

public class ErrorCode {
	public static final String UNKNOWN_ERROR = "000000";
	/********************User ErrorCode start**********************/
	public static final String ERR_USER_OTHER_USER_LOGINED = "010102";
	public static final String ERR_LOGIN_TIMES_USED_OUT = "010103";
	public static final String ERR_USERNAME_OR_PASSWORD = "010101";
	/********************User ErrorCode end**********************/
	
	/********************SMS ErrorCode start**********************/
	public static final String ERR_SMS_SIM_IS_FULL = "060603";
	public static final String ERR_SAVE_SMS_SIM_IS_FULL = "060802";
	/********************SMS ErrorCode end**********************/
	
	/********************Change Password ErrorCode start**********************/
	public static final String CHANGE_PASSWORD_FAILED = "010401";
	public static final String CURRENT_PASSWORD_IS_WRONG = "010402";
	/********************Change Password ErrorCode end**********************/

}
