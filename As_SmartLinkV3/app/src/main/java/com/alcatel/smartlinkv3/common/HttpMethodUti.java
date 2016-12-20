package com.alcatel.smartlinkv3.common;

public class HttpMethodUti {
	private String m_managerClassName;
	private String m_methodName;
	
	public HttpMethodUti(Class managerClass,String methodName) {
		m_managerClassName = managerClass.getSimpleName();
		m_methodName = methodName;
	}
	
	public String getManagerClassName() {
		return m_managerClassName;
	}
	
	public String getMethodString() {
		return m_methodName;
	}
}
