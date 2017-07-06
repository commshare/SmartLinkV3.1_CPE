package com.alcatel.wifilink.business.device;

import java.util.ArrayList;

import com.alcatel.wifilink.business.BaseResult;


public class BlockDeviceList extends BaseResult{
	public ArrayList<DeviceInfo> BlockList = new ArrayList<DeviceInfo>();
	
	@Override
	public void clear(){
		BlockList.clear();	
	}
}
