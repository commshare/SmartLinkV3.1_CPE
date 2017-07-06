package com.alcatel.wifilink.model.device.other;

import com.alcatel.wifilink.model.device.response.ConnectedList;

/**
 * Created by qianli.ma on 2017/6/24.
 */

public class ConnectModel {
    public ConnectedList.Device device;
    public boolean isEdit;

    public ConnectModel(ConnectedList.Device device, boolean isEdit) {
        this.device = device;
        this.isEdit = isEdit;
    }

    @Override
    public String toString() {
        return "ConnectModel{" + "device=" + device + ", isEdit=" + isEdit + '}';
    }

    public ConnectedList.Device getDevice() {
        return device;
    }

    public void setDevice(ConnectedList.Device device) {
        this.device = device;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
