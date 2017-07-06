package com.alcatel.wifilink.model.update;

/**
 * Created by ZQ on 2017/6/21.
 */

public class DeviceNewVersion {


    /**
     * current check state
     * 0: Checking
     1: New version
     2: no new version
     3: no connect
     4: service not available
     5: check error(time out etc…)
     */
    private int State;
    /**
     * The new software version number on the FOTA Sever.
     */
    private String Version;
    /**
     *  The size of new software(B)
     */
    private int total_size;

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }

    @Override
    public String toString() {
        return "DeviceNewVersion{" +
                "State=" + State +
                ", Version='" + Version + '\'' +
                ", total_size=" + total_size +
                '}';
    }
}
