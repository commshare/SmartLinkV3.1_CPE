package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.business.BaseResult;

public class NetworkInfoResult extends BaseResult{
	public String PLMN = new String();
	/*Network Type 
	0: No service
	1: GPRS
	2: EDGE
	3: UMTS
	4: HSDPA
	5: HSPA
	6: HSUPA
	7: DC-HSPA+
	8: LTE
	9: GSM
	10: HSPA+
	11: UNKNOWN*/
    
	public int NetworkType = 0;
	public String NetworkName = new String();
	public String SpnName = new String();
	public String LAC = new String();
	public String CellId = new String();
	public String RncId = new String();
	/*
	Possible Values: 
	0:no roaming
	1:roaming
	 */
	public int Roaming = 0;
	/*
	RSSI value:
	0: level 0
	1: level 1
	2: level 2
	3: level 3
	4: level 4
	5: level 5
	 */
	public int SignalStrength = 0;


	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		PLMN = "";
		NetworkType = 0;
		NetworkName = "";
		SpnName = "";
		LAC = "";
		CellId = "";
		RncId = "";
		Roaming = 0;
		SignalStrength = 0;
	}

}
