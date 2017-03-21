package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.wan.ConnectStatusResult;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;

public class WanConnectStatusModel extends BaseModel{
    public ConnectionStatus m_connectionStatus = ConnectionStatus.Disconnected;
	public String m_strConnectProfile = new String();
	public long m_lUlRate = 0;//Current upload rate 
	public long m_lDlRate = 0;//Current download rate 
	public long m_lUlBytes = 0;//Upload bytes
	public long m_lDlBytes = 0;//Download bytes
	public String m_strIPv4Address = new String();
	public String m_strIPv6Address = new String();
	public long m_lConnectionTime = 0;
	
	public void clone(WanConnectStatusModel src) {
		m_connectionStatus = src.m_connectionStatus;
		m_strConnectProfile = src.m_strConnectProfile;
		m_lUlRate = src.m_lUlRate;
		m_lDlRate = src.m_lDlRate;
		m_lUlBytes = src.m_lUlBytes;
		m_lDlBytes = src.m_lDlBytes;
		m_strIPv4Address = src.m_strIPv4Address;
		m_strIPv6Address = src.m_strIPv6Address;
		m_lConnectionTime = src.m_lConnectionTime;
	}
	
	public void clear() {
		// TODO Auto-generated method stub
		m_connectionStatus = ConnectionStatus.Disconnected;
		m_strConnectProfile = "";
		m_lUlRate = 0; 
		m_lDlRate = 0;
		m_lUlBytes = 0;
		m_lDlBytes = 0;
		m_strIPv4Address = "";
		m_strIPv6Address = "";
		m_lConnectionTime = 0;
	}
	
	public void setValue(ConnectStatusResult result){
		m_connectionStatus = ConnectionStatus.build(result.ConnectionStatus);
		m_strConnectProfile = result.ConnectProfile;
		m_lUlRate = result.UlRate;
		m_lDlRate = result.DlRate;
		m_lUlBytes = result.UlBytes;
		m_lDlBytes = result.DlBytes;
		m_strIPv4Address = result.IPv4Adrress;
		m_strIPv6Address = result.IPv6Adrress;
		m_lConnectionTime = result.ConnectionTime;
	}


    public String getStrConnectProfile() {
        return m_strConnectProfile;
    }

    public void setStrConnectProfile(String strConnectProfile) {
        m_strConnectProfile = strConnectProfile;
    }

    public long getlUlRate() {
        return m_lUlRate;
    }

    public void setlUlRate(long lUlRate) {
        m_lUlRate = lUlRate;
    }

    public long getlDlRate() {
        return m_lDlRate;
    }

    public void setlDlRate(long lDlRate) {
        m_lDlRate = lDlRate;
    }

    public long getlUlBytes() {
        return m_lUlBytes;
    }

    public void setlUlBytes(long lUlBytes) {
        m_lUlBytes = lUlBytes;
    }

    public long getlDlBytes() {
        return m_lDlBytes;
    }

    public void setlDlBytes(long lDlBytes) {
        m_lDlBytes = lDlBytes;
    }

    public String getIPv4Address() {
        return m_strIPv4Address;
    }

    public void setIPv4Address(String strIPv4Address) {
        m_strIPv4Address = strIPv4Address;
    }

    public String getIPv6Address() {
        return m_strIPv6Address;
    }

    public void setIPv6Address(String strIPv6Address) {
        m_strIPv6Address = strIPv6Address;
    }

    public long getlConnectionTime() {
        return m_lConnectionTime;
    }

    public void setlConnectionTime(long lConnectionTime) {
        m_lConnectionTime = lConnectionTime;
    }

    public ConnectionStatus getConnectionStatus() {
        return m_connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        m_connectionStatus = connectionStatus;
    }

    public boolean isConnected() {
        return getConnectionStatus() == ConnectionStatus.Connected;
    }

}
