package com.alcatel.wifilink.business.wlan;

public class AP implements Cloneable{
    /**
     * ApStatus : 1
     * WMode : 3
     * Ssid : WebPocket-BAB5
     * SsidHidden : 0
     * Channel : 0
     * SecurityMode : 2
     * WepType : 0
     * WepKey : 1234567890
     * WpaType : 1
     * WpaKey : GE747TNT
     * ApIsolation : 0
     * max_numsta : 15
     * curr_num : 0
     * CurChannel : 8
     * CountryCode : IT
     * Bandwidth : 0
     */

    private int ApStatus;
    private int WMode;
    private String Ssid;
    private int SsidHidden;
    private int Channel;
    private int SecurityMode;
    private int WepType;
    private String WepKey;
    private int WpaType;
    private String WpaKey;
    private int ApIsolation;
    private int max_numsta;
    private int curr_num;
    private int CurChannel;
    private String CountryCode;
    private int Bandwidth;


//    /**
//     * 0:disabled, 1:enabled
//     */
//    int ApStatus = 1;
//    /**
//     * 0: 802.11b; 1: 802.11b/g; 2: 802.11b/g/n; 3: Auto
//     */
//    public int WMode = 0;
//
//    public String Ssid = new String();
//    /**
//     * 0: disable; 1: enable
//     */
//    public int SsidHidden = 0;
//    /**
//     * -1: auto-2.4G -2: auto-5G
//     */
//    public int Channel = -1;
//    /**
//     * 0: disable, 1: wep, 2: WPA, 3: WPA2, 4: WPA/WPA2
//     */
//    public int SecurityMode = 0;
//    /**
//     * 0: OPEN	1: share
//     */
//    public int WepType = 0;
//    public String WepKey = new String();
//    /**
//     * 0: TKIP, 1: AES, 2: AUTO
//     */
//    public int WpaType = 0;
//    public String WpaKey = new String();
//    public String CountryCode = new String();
//    /**
//     * 0: disable, 1: enable
//     */
//    public int ApIsolation = 0;
//    /**
//     * 2.4 G WIFI max number client
//     */
//    public int max_numsta = 0;
//    /**
//     * Current client count that connect to WIFI.
//     */
//    public int curr_num = 0;
//    public int CurChannel = 0;
//    public int Bandwidth = 0;
//
    public boolean isApEnabled() {
        return ApStatus == 1;
    }
//
    public void setApEnabled(boolean enabled) {
        ApStatus = enabled ? 1 : 0;
    }
//
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

    public int getApStatus() {
        return ApStatus;
    }

    public void setApStatus(int ApStatus) {
        this.ApStatus = ApStatus;
    }

    public int getWMode() {
        return WMode;
    }

    public void setWMode(int WMode) {
        this.WMode = WMode;
    }

    public String getSsid() {
        return Ssid;
    }

    public void setSsid(String Ssid) {
        this.Ssid = Ssid;
    }

    public int getSsidHidden() {
        return SsidHidden;
    }

    public void setSsidHidden(int SsidHidden) {
        this.SsidHidden = SsidHidden;
    }

    public int getChannel() {
        return Channel;
    }

    public void setChannel(int Channel) {
        this.Channel = Channel;
    }

    public int getSecurityMode() {
        return SecurityMode;
    }

    public void setSecurityMode(int SecurityMode) {
        this.SecurityMode = SecurityMode;
    }

    public int getWepType() {
        return WepType;
    }

    public void setWepType(int WepType) {
        this.WepType = WepType;
    }

    public String getWepKey() {
        return WepKey;
    }

    public void setWepKey(String WepKey) {
        this.WepKey = WepKey;
    }

    public int getWpaType() {
        return WpaType;
    }

    public void setWpaType(int WpaType) {
        this.WpaType = WpaType;
    }

    public String getWpaKey() {
        return WpaKey;
    }

    public void setWpaKey(String WpaKey) {
        this.WpaKey = WpaKey;
    }

    public int getApIsolation() {
        return ApIsolation;
    }

    public void setApIsolation(int ApIsolation) {
        this.ApIsolation = ApIsolation;
    }

    public int getMax_numsta() {
        return max_numsta;
    }

    public void setMax_numsta(int max_numsta) {
        this.max_numsta = max_numsta;
    }

    public int getCurr_num() {
        return curr_num;
    }

    public void setCurr_num(int curr_num) {
        this.curr_num = curr_num;
    }

    public int getCurChannel() {
        return CurChannel;
    }

    public void setCurChannel(int CurChannel) {
        this.CurChannel = CurChannel;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String CountryCode) {
        this.CountryCode = CountryCode;
    }

    public int getBandwidth() {
        return Bandwidth;
    }

    public void setBandwidth(int Bandwidth) {
        this.Bandwidth = Bandwidth;
    }
}
