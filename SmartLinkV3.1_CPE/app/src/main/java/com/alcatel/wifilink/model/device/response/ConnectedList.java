package com.alcatel.wifilink.model.device.response;

import java.util.List;

public class ConnectedList {

    public List<Device> ConnectedList;

    public class Device {
        public int id;
        public String DeviceName;
        public String MacAddress;
        public String IPAddress;
        public int AssociationTime;
        public int DeviceType;
        public int ConnectMode;
        public int InternetRight;
        public int StorageRight;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getIPAddress() {
            return IPAddress;
        }

        public void setIPAddress(String IPAddress) {
            this.IPAddress = IPAddress;
        }

        public int getAssociationTime() {
            return AssociationTime;
        }

        public void setAssociationTime(int associationTime) {
            AssociationTime = associationTime;
        }

        public int getDeviceType() {
            return DeviceType;
        }

        public void setDeviceType(int deviceType) {
            DeviceType = deviceType;
        }

        public int getConnectMode() {
            return ConnectMode;
        }

        public void setConnectMode(int connectMode) {
            ConnectMode = connectMode;
        }

        public int getInternetRight() {
            return InternetRight;
        }

        public void setInternetRight(int internetRight) {
            InternetRight = internetRight;
        }

        public int getStorageRight() {
            return StorageRight;
        }

        public void setStorageRight(int storageRight) {
            StorageRight = storageRight;
        }

        @Override
        public String toString() {
            return "Device{" + "id=" + id + ", DeviceName='" + DeviceName + '\'' + ", MacAddress='" + MacAddress + '\'' + ", IPAddress='" + IPAddress + '\'' + ", AssociationTime=" + AssociationTime + ", DeviceType=" + DeviceType + ", ConnectMode=" + ConnectMode + ", InternetRight=" + InternetRight + ", StorageRight=" + StorageRight + '}';
        }
    }

    @Override
    public String toString() {
        return "ConnectedList{" + "ConnectedList=" + ConnectedList + '}';
    }

    public List<Device> getConnectedList() {
        return ConnectedList;
    }

    public void setConnectedList(List<Device> connectedList) {
        ConnectedList = connectedList;
    }
}
