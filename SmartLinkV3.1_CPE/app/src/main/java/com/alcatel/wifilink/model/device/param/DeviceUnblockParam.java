package com.alcatel.wifilink.model.device.param;

/**
 * Created by qianli.ma on 2017/6/23.
 */

public class DeviceUnblockParam {

    public String DeviceName;
    public String MacAddress;

    public DeviceUnblockParam(String deviceName, String macAddress) {
        DeviceName = deviceName;
        MacAddress = macAddress;
    }

    @Override
    public String toString() {
        return "DeviceUnblockParam{" + "DeviceName='" + DeviceName + '\'' + ", MacAddress='" + MacAddress + '\'' + '}';
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
