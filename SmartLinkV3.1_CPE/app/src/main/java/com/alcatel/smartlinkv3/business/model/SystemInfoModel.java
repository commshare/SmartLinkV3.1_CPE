package com.alcatel.smartlinkv3.business.model;

public class SystemInfoModel extends BaseModel {

	private String m_strSWVersion="";
	private String m_strHWVersion="";
	private String m_strDeviceName="";
	private String m_strIMEI="";
	private String m_strMacAddress="";
	private String m_strIP="";
	private String m_strSubnet="";
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		m_strSWVersion="";
		m_strHWVersion="";
		m_strDeviceName="";
		m_strIMEI="";
		m_strMacAddress="";
		m_strIP="";
		m_strSubnet="";
	}
	
	public String getMacAddress() {
		return m_strMacAddress;
	}
	public void setMacAddress(String strMacAddress) {
		m_strMacAddress = strMacAddress;
	}
	public String getDeviceName() {
		return m_strDeviceName;
	}
	public void setDeviceName(String deviceName) {
		m_strDeviceName = deviceName;
	}
	public String getIMEI() {
		return m_strIMEI;
	}
	public void setIMEI(String iMEI) {
		m_strIMEI = iMEI;
	}
	public String getHwVersion() {
		return m_strHWVersion;
	}
	public void setHwVersion(String hwVersion) {
		m_strHWVersion = hwVersion;
	}
	public String getSwVersion() {
		return m_strSWVersion;
	}
	public void setSwVersion(String swVersion) {
		m_strSWVersion = swVersion;
	}
	public String getIP() {
		return m_strIP;
	}
	public void setIP(String strIP) {
		m_strIP = strIP;
	}
	public String getSubnet() {
		return m_strSubnet;
	}
	public void setSubnet(String strSubnet) {
		m_strSubnet = strSubnet;
	}

}
