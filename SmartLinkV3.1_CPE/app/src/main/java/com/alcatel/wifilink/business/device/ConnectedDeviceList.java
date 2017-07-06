package com.alcatel.wifilink.business.device;

import java.util.ArrayList;

import com.alcatel.wifilink.business.BaseResult;


public class ConnectedDeviceList extends BaseResult{
	public ArrayList<DeviceInfo> ConnectedList = new ArrayList<DeviceInfo>();
	
	@Override
	public void clear(){
		ConnectedList.clear();	
	}
}
