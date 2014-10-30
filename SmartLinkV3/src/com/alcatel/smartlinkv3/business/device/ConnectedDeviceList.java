package com.alcatel.smartlinkv3.business.device;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;


public class ConnectedDeviceList extends BaseResult{
	public ArrayList<DeviceInfo> DeviceList = new ArrayList<DeviceInfo>();
	
	@Override
	public void clear(){
		DeviceList.clear();	
	}
}
