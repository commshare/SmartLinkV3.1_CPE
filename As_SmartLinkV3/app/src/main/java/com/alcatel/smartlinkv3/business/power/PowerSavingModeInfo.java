package com.alcatel.smartlinkv3.business.power;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.ConstValue;

public class PowerSavingModeInfo extends BaseResult {

	private int SmartMode = ConstValue.DISABLE;//0:disable; 1:enable
	private int WiFiMode= ConstValue.DISABLE;//0:disable; 1:enable
	@Override
	public void clear() {
		// TODO Auto-generated method stub

		SmartMode = ConstValue.DISABLE;
		WiFiMode= ConstValue.DISABLE;
	}

	//SmartMode
	public int getSmartMode(){
		return SmartMode;
	}

	public void setSmartMode(int nSmartMode){
		SmartMode = nSmartMode;
	}

	//WiFiMode
	public int getWiFiMode(){
		return WiFiMode;
	}

	public void setWiFiMode(int nWiFiMode){
		WiFiMode = nWiFiMode;
	}
}
