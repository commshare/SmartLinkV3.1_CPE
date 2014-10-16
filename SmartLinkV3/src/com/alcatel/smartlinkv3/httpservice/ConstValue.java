package com.alcatel.smartlinkv3.httpservice;

public class ConstValue {
	//public static String HTTP_SERVER_ADDRESS = "http://%1$s/jrd/webapi";	//only h850
	public static String HTTP_SERVER_ADDRESS = "http://%1$s/cgi-bin/rpc";//mm100 or h850
	public static String LOG_FILE_NAME = "/CPELog.txt";
	
	public static String JSON_RPC_VERSION = "2.0";
	
	public static String JSON_RPC = "jsonrpc";
	public static String JSON_METHOD = "method";
	public static String JSON_PARAMS = "params";
	public static String JSON_RESULT = "result";
	public static String JSON_ERROR = "error";
	public static String JSON_ERROR_CODE = "code";
	public static String JSON_ERROR_MESSAGE = "message";
	public static String JSON_ID = "id";
}
