package com.alcatel.smartlinkv3.business.update;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;

public class DeviceNewVersionInfo extends BaseResult {

	private int State=EnumDeviceCheckingStatus.antiBuild(EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION);//0:checking;	1: New version;	2: no new version
	private String Version = "";
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		State = EnumDeviceCheckingStatus.antiBuild(EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION);
		Version = "";
	}

	//state
	public int getState(){
		return State;
	}
	public void setState(int nState){
		State = nState;
	}

	//version
	public String getVersion(){
		return Version;
	}
	public void setVersion(String strVersion){
		Version = strVersion;
	}
}
