package com.alcatel.smartlinkv3.business.wlan;

public class AP implements Cloneable{
    /**
     * 0:disabled, 1:enabled
     */
    int ApStatus = 1;
    /**
     * 0: 802.11b; 1: 802.11b/g; 2: 802.11b/g/n; 3: Auto
     */
    public int WMode = 0;

    public String Ssid = new String();
    /**
     * 0: disable; 1: enable
     */
    public int SsidHidden = 0;
    /**
     * -1: auto-2.4G -2: auto-5G
     */
    public int Channel = -1;
    /**
     * 0: disable, 1: wep, 2: WPA, 3: WPA2, 4: WPA/WPA2
     */
    public int SecurityMode = 0;
    /**
     * 0: OPEN	1: share
     */
    public int WepType = 0;
    public String WepKey = new String();
    /**
     * 0: TKIP, 1: AES, 2: AUTO
     */
    public int WpaType = 0;
    public String WpaKey = new String();
    public String CountryCode = new String();
    /**
     * 0: disable, 1: enable
     */
    public int ApIsolation = 0;
    /**
     * 2.4 G WIFI max number client
     */
    public int max_numsta = 0;
    /**
     * Current client count that connect to WIFI.
     */
    public int curr_num = 0;
    public int CurChannel = 0;
    public int Bandwidth = 0;

    public boolean isApEnabled() {
        return ApStatus == 1;
    }

    public void setApEnabled(boolean enabled) {
        ApStatus = enabled ? 1 : 0;
    }

    public boolean isSsidHiden() {
        return SsidHidden == 1;
    }

    public void setSsidHidden(boolean hidden) {
        SsidHidden = hidden ? 1 : 0;
    }

    public boolean isApIsolated() {
        return ApIsolation == 1;
    }

    public void setApIsolated(boolean isolated) {
        ApIsolation = isolated ? 1 : 0;
    }

    @Override
    public String toString() {
        return "AP{" +
                "ApStatus=" + ApStatus +
                ", WMode=" + WMode +
                ", Ssid='" + Ssid + '\'' +
                ", SsidHidden=" + SsidHidden +
                ", Channel=" + Channel +
                ", SecurityMode=" + SecurityMode +
                ", WepType=" + WepType +
                ", WepKey='" + WepKey + '\'' +
                ", WpaType=" + WpaType +
                ", WpaKey='" + WpaKey + '\'' +
                ", CountryCode='" + CountryCode + '\'' +
                ", ApIsolation=" + ApIsolation +
                ", max_numsta=" + max_numsta +
                ", curr_num=" + curr_num +
                ", CurChannel=" + CurChannel +
                ", Bandwidth=" + Bandwidth +
                '}';
    }

    @Override
    public AP clone(){
        AP ap = null;
        try {
            ap = (AP) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ap;
    }
}
