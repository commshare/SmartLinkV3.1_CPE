package com.alcatel.smartlinkv3.model.device.param;

/**
 * Created by qianli.ma on 2017/6/23.
 */

public class ConnectedDeviceBlockParam {

    public String DeviceName;
    public String MacAddress;

    public ConnectedDeviceBlockParam(String deviceName, String macAddress) {
        DeviceName = deviceName;
        MacAddress = macAddress;
    }

    @Override
    public String toString() {
        return "ConnectedDeviceBlockParam{" + "DeviceName='" + DeviceName + '\'' + ", MacAddress='" + MacAddress + '\'' + '}';
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }
}
