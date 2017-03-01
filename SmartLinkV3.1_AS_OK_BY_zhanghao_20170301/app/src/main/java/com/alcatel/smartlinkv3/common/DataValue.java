package com.alcatel.smartlinkv3.common;

import java.util.HashMap;
import java.util.Map;

public class DataValue {
	private Map<String, Object> m_dataMap;
	
	public DataValue() {
		m_dataMap = new HashMap<String, Object>(); 
	} 
	
	public void addParam(String paramKey,Object param) {
		m_dataMap.put(paramKey, param);
	}
	
	public Object getParamByKey(String paramKey) {
		if(!m_dataMap.containsKey(paramKey)) {
			return null;
		}
		return m_dataMap.get(paramKey);
	}
}
