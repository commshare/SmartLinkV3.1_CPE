package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.wan.ConnectStatusResult;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;

public class ConnectStatusModel extends BaseModel{
    
	public ConnectionStatus m_connectionStatus = ConnectionStatus.Disconnected;
	public String m_strConnectProfile = new String();
	public long m_lUlRate = 0;//Current upload rate 
	public long m_lDlRate = 0;//Current download rate 
	public long m_lUlBytes = 0;//Upload bytes
	public long m_lDlBytes = 0;//Download bytes
	public String m_strIPv4Adrress = new String();
	public String m_strIPv6Adrress = new String();
	public long m_lConnectionTime = 0;
	
	public void clone(ConnectStatusModel src) {
		m_connectionStatus = src.m_connectionStatus;
		m_strConnectProfile = src.m_strConnectProfile;
		m_lUlRate = src.m_lUlRate;
		m_lDlRate = src.m_lDlRate;
		m_lUlBytes = src.m_lUlBytes;
		m_lDlBytes = src.m_lDlBytes;
		m_strIPv4Adrress = src.m_strIPv4Adrress;
		m_strIPv6Adrress = src.m_strIPv6Adrress;
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
		m_strIPv4Adrress = "";
		m_strIPv6Adrress = "";
		m_lConnectionTime = 0;
	}
	
	public void setValue(ConnectStatusResult result){
		m_connectionStatus = ConnectionStatus.build(result.ConnectionStatus);
		m_strConnectProfile = result.ConnectProfile;
		m_lUlRate = result.UlRate;
		m_lDlRate = result.DlRate;
		m_lUlBytes = result.UlBytes;
		m_lDlBytes = result.DlBytes;
		m_strIPv4Adrress = result.IPv4Adrress;
		m_strIPv6Adrress = result.IPv6Adrress;
		m_lConnectionTime = result.ConnectionTime;
	}

}
