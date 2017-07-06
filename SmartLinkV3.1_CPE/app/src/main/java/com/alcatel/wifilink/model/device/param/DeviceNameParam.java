package com.alcatel.wifilink.model.device.param;

/**
 * Created by qianli.ma on 2017/6/23.
 */

public class DeviceNameParam {

    public String DeviceName;
    public String MacAddress;
    public int DeviceType;

    public DeviceNameParam(String deviceName, String macAddress, int deviceType) {
        DeviceName = deviceName;
        MacAddress = macAddress;
        DeviceType = deviceType;
    }

    @Override
    public String toString() {
        return "DeviceNameParam{" + "DeviceName='" + DeviceName + '\'' + ", MacAddress='" + MacAddress + '\'' + ", DeviceType=" + DeviceType + '}';
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

    public int getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(int deviceType) {
        DeviceType = deviceType;
    }
}
