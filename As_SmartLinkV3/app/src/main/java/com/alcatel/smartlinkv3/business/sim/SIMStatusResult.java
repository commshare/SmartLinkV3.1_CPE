package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.business.BaseResult;

public class SIMStatusResult extends BaseResult{
	public String Imsi = new String();
	public String Iccid = new String();//Reserved this filed, not implemented yet. Return value is empty string.
	public String MSISDN = new String();//Reserved this filed, not implemented yet. Return value is empty string.
	public int SIMState = 5;//Possible values:0 - NoSim;1 - PinRequired;2 - PukRequired;3 - Accessable (Network unlocked);4 - InvalidSim 5 - Unknown
	public int PinOperationalState = 0;//Indicates whether enter PIN is required or not. Possible values:0 - False;1 - True
	public int PinRemainingTimes = 0;//Pin remaining times
	public int PukRemainingTimes = 0;//Puk remaining times
	public int PinState = 0;//0: Not available1: PIN disable2: PIN enable3:Require PUK


	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		Imsi = "";
		Iccid = "";
		MSISDN = "";
		SIMState = 5;
		PinOperationalState = 0;
		PinRemainingTimes = 0;
		PukRemainingTimes = 0;
		PinState = 0;
	}

}
