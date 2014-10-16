package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.common.ENUM;

public class SimStatusModel extends BaseModel{
    
	public String m_strImsi = new String();
	public String m_strIccid = new String();//Reserved this filed, not implemented yet. Return value is empty string.
	public String m_strMSISDN;//Reserved this filed, not implemented yet. Return value is empty string.
	public ENUM.SIMState m_SIMState = ENUM.SIMState.Unknown;//Possible values:0 - NoSim;1 - PinRequired;2 - PukRequired;3 - Accessable (Network unlocked);//ready4 - InvalidSim 5 - Unknown
	public boolean m_bPinOperationalState = false;//Indicates whether enter PIN is required or not. Possible values:0 - False;1 - True
	public int m_nPinRemainingTimes = 0;
	public int m_nPukRemainingTimes = 0;
	public ENUM.PinState m_PinState = ENUM.PinState.NotAvailable;

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		m_strImsi = "";
		m_strIccid = "";
		m_strMSISDN = "";
		m_SIMState = ENUM.SIMState.Unknown;
		m_bPinOperationalState = false;
		m_nPinRemainingTimes = 0;
		m_nPukRemainingTimes = 0;
		m_PinState = ENUM.PinState.NotAvailable;
	}
	
	public void clone(SimStatusModel src) {
		m_strImsi = src.m_strImsi;
		m_strIccid = src.m_strIccid;
		m_strMSISDN = src.m_strMSISDN;
		m_SIMState = src.m_SIMState;
		m_bPinOperationalState = src.m_bPinOperationalState;
		m_nPinRemainingTimes = src.m_nPinRemainingTimes;
		m_nPukRemainingTimes = src.m_nPukRemainingTimes;
		m_PinState = src.m_PinState;
	}
	
	public void setValue(SIMStatusResult result){
		m_strImsi = result.Imsi;
		m_strIccid = result.Iccid;
		m_strMSISDN = result.MSISDN;
		m_SIMState = ENUM.SIMState.build(result.SIMState);
		if(result.PinOperationalState == 0) {
			m_bPinOperationalState = false;
		}else{
			m_bPinOperationalState = true;
		}
		m_nPinRemainingTimes = result.PinRemainingTimes;
		m_nPukRemainingTimes = result.PukRemainingTimes;
		m_PinState = ENUM.PinState.build(result.PinState);
	}
	
	public boolean equalTo(SimStatusModel instanse) {
		boolean bEqual = true;
		if(!m_strImsi.equalsIgnoreCase(instanse.m_strImsi) || 
				!m_strIccid.equalsIgnoreCase(instanse.m_strIccid) ||
				!m_strMSISDN.equalsIgnoreCase(instanse.m_strMSISDN) ||
				m_SIMState != instanse.m_SIMState || 
				m_bPinOperationalState != instanse.m_bPinOperationalState ||
				m_nPinRemainingTimes != instanse.m_nPinRemainingTimes ||
				m_nPukRemainingTimes != instanse.m_nPukRemainingTimes ||
				m_PinState != instanse.m_PinState)
			bEqual = false;
		
		return bEqual;
	}

}
