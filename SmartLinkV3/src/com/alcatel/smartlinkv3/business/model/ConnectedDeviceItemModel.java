package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.device.DeviceInfo;
import com.alcatel.smartlinkv3.common.ENUM.EnumConnectMode;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceType;


public class ConnectedDeviceItemModel{
    
	public int id = 0;
	public String DeviceName = new String();
	public String MacAddress = new String();
	public String IPAddress = new String();
	public int AssociationTime = 0; 
	public EnumDeviceType DeviceType = EnumDeviceType.USE_WEB_LOGIN;
	public EnumConnectMode ConnectMode = EnumConnectMode.USB_CONNECT;
	
	public void buildFromResult(DeviceInfo result) {
		id = result.id;
		DeviceName = result.DeviceName;
		MacAddress = result.MacAddress;
		IPAddress = result.IPAddress;
		AssociationTime = result.AssociationTime;
		DeviceType = EnumDeviceType.build(result.DeviceType);
		ConnectMode = EnumConnectMode.build(result.ConnectMode);
	}
}
