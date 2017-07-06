package com.alcatel.wifilink.business.model;

import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ENUM.OVER_ROAMING_STATE;
import com.alcatel.wifilink.common.ENUM.PdpType;
import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.business.wan.ConnectionSettingsResult;

public class ConnectionSettingsModel extends BaseResult{
	
	public int HIdleTime = 0;
	public ENUM.OVER_ROAMING_STATE HRoamingConnect = OVER_ROAMING_STATE.Disable;
	public int HConnectMode = 0;
	public ENUM.PdpType HPdpType = PdpType.PDP_TYPE_IPv4;

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		HIdleTime = 0;
		HRoamingConnect = OVER_ROAMING_STATE.Disable;
		HConnectMode = 0;
		ENUM.PdpType HPdpType = PdpType.PDP_TYPE_IPv4;
	}
	
	public void clone(ConnectionSettingsModel src) {	
		if(src == null)
			return ;
		HIdleTime = src.HIdleTime;
		HRoamingConnect = src.HRoamingConnect;
		HConnectMode = src.HConnectMode;
		HPdpType = PdpType.PDP_TYPE_IPv4;
	}
	
	public void setValue(ConnectionSettingsResult result) {	
		HIdleTime = result.IdleTime;
		HRoamingConnect = ENUM.OVER_ROAMING_STATE.build(result.RoamingConnect);
		HConnectMode = result.ConnectMode;
		HPdpType = ENUM.PdpType.build(result.PdpType);

	}
}
