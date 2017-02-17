package com.alcatel.smartlinkv3.httpservice;

public class ConstValue {
	public static String HTTP_SERVER_ADDRESS = "http://%1$s/jrd/webapi";
	public static String HTTP_GET_CONFIG_ADDRESS = "http://192.168.1.1/cfgbak/configure.bin";
    public static String HTTP_UPLOAD_BACKUP_SETTINGS_ADDRESS = "http://192.168.1.1/goform/uploadBackupSettings";
	public static String SMB_SERVER_ADDRESS = "smb://%1$s/";
	public static String LOG_FILE_NAME = "/SmartLinkV3Log.txt";
	
	public static String JSON_RPC_VERSION = "2.0";
	
	public static String JSON_RPC = "jsonrpc";
	public static String JSON_METHOD = "method";
	public static String JSON_PARAMS = "params";
	public static String JSON_RESULT = "result";
	public static String JSON_ERROR = "error";
	public static String JSON_ERROR_CODE = "code";
	public static String JSON_ERROR_MESSAGE = "message";
	public static String JSON_ID = "id";
	public static int CHARGE_STATE_CHARGING = 0;
	public static int CHARGE_STATE_COMPLETED = 1;
	public static int CHARGE_STATE_REMOVED = 2;
	public static int CHARGE_STATE_ABORT = 3;
	public static int DISABLE = 0;
	public static int ENABLE = 1;
	
}
