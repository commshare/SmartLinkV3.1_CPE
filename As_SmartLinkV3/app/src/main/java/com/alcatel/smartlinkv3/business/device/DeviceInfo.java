package com.alcatel.smartlinkv3.business.device;

import com.alcatel.smartlinkv3.business.BaseResult;

public class DeviceInfo extends BaseResult{
	
	
	public int id = 0;
	public String DeviceName = new String();
	public String MacAddress = new String();
	public String IPAddress = new String();
	public int AssociationTime = 0; //optional
	public int DeviceType = 0;
	public int ConnectMode = 0;
	

	@Override
	public void clear() {
		id = 0;
		DeviceName = "";
		MacAddress = "";
		IPAddress = "";
		AssociationTime = 0;
		DeviceType = 0;
		ConnectMode = 0;
	}
}
