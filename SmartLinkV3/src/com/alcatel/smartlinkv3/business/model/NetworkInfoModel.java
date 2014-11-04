package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.network.NetworkInfoResult;
import com.alcatel.smartlinkv3.common.ENUM;

public class NetworkInfoModel extends BaseModel{
    
	public String m_strPLMN = new String();
	public ENUM.NetworkType m_NetworkType = ENUM.NetworkType.UNKNOWN;
	public String m_strNetworkName = new String();
	public String m_strSpnName = new String();
	public String m_strLAC = new String();
	public String m_strCellId = new String();
	public String m_strRncId = new String();
	public boolean m_bRoaming = false;
	public ENUM.SignalStrength m_signalStrength = ENUM.SignalStrength.Level_0;

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		m_strPLMN = "";
		m_NetworkType = ENUM.NetworkType.UNKNOWN;
		m_strNetworkName = "";
		m_strSpnName = "";
		m_strLAC = "";
		m_strCellId = "";
		m_strRncId = "";
		m_bRoaming = false;
		m_signalStrength = ENUM.SignalStrength.Level_0;
	}
	
	public void clone(NetworkInfoModel src) {
		m_strPLMN = src.m_strPLMN;
		m_NetworkType = src.m_NetworkType;
		m_strNetworkName = src.m_strNetworkName;
		m_strSpnName = src.m_strSpnName;
		m_strLAC = src.m_strLAC;
		m_strCellId = src.m_strCellId;
		m_strRncId = src.m_strRncId;
		m_bRoaming = src.m_bRoaming;
		m_signalStrength = src.m_signalStrength;
	}
	
	public void setValue(NetworkInfoResult result){
		m_strPLMN = result.PLMN;
		m_NetworkType = ENUM.NetworkType.build(result.NetworkType);
		m_strNetworkName = result.NetworkName;
		m_strSpnName = result.SpnName;
		m_strLAC = result.LAC;
		m_strCellId = result.CellId;
		m_strRncId = result.RncId;
		if(result.Roaming == 0) {
			m_bRoaming = true;
		}else{
			m_bRoaming = false;
		}
		m_signalStrength = ENUM.SignalStrength.build(result.SignalStrength);
	}
}
