package com.alcatel.smartlinkv3.business.device;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;


public class BlockDeviceList extends BaseResult{
	public ArrayList<DeviceInfo> BlockList = new ArrayList<DeviceInfo>();
	
	@Override
	public void clear(){
		BlockList.clear();	
	}
}
