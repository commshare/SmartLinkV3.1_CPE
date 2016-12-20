package com.alcatel.smartlinkv3.httpservice;

import java.util.HashMap;
import java.util.Map;

public class MethodProp {

	private static final Map<String, Boolean> methodPropMap = new HashMap<String, Boolean>();

	static {		
		add("SMS.DeleteSms", false);
		add("SMS.SendSms", false);
		add("SMS.GetSmsSendResult", false);
		add("SMS.ModifySmsReadStatus", false);
		add("SMS.SaveSms", false);	
		add("CallLog.DeleteCallLog", false);		
	}

	private static void add(String name, Boolean removealbe) {

		if (!methodPropMap.containsKey(name)) {
			methodPropMap.put(name, removealbe);
		}
	}

	public static Boolean isRemoveAble(String name) {
		if(methodPropMap.get(name) == null)	{
			return true;
		}
		else
			return methodPropMap.get(name);
	}
}
