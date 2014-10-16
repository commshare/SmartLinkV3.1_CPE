package com.alcatel.smartlinkv3.business.system;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.BaseResult;

public class StorageList extends BaseResult{
	public ArrayList<StorageInfo> DeviceList = new ArrayList<StorageInfo>();
	
	@Override
	public void clear(){
		DeviceList.clear();	
	}
}
