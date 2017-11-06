package com.alcatel.smartlinkv3.rx.impl.wlan;

import java.util.List;

/**
 * Created by qianli.ma on 2017/10/27 0027.
 */

public class WlanResult {

    private int curr_num;
    private int WlanAPMode;
    private int WiFiOffTime;
    private List<APListBean> APList;

    public int getCurr_num() {
        return curr_num;
    }

    public void setCurr_num(int curr_num) {
        this.curr_num = curr_num;
    }

    public int getWlanAPMode() {
        return WlanAPMode;
    }

    public void setWlanAPMode(int WlanAPMode) {
        this.WlanAPMode = WlanAPMode;
    }

    public int getWiFiOffTime() {
        return WiFiOffTime;
    }

    public void setWiFiOffTime(int WiFiOffTime) {
        this.WiFiOffTime = WiFiOffTime;
    }

    public List<APListBean> getAPList() {
        return APList;
    }

    public void setAPList(List<APListBean> APList) {
        this.APList = APList;
    }

    public static class APListBean {
        private int ApIsolation;
        private int ApStatus;
        private int Bandwidth;
        private int Channel;
        private String CountryCode;
        private int CurChannel;
        private int curr_num;
        private int max_numsta;
        private int SecurityMode;
        private String Ssid;
        private int SsidHidden;
        private String WepKey;
        private int WepType;
        private int WlanAPID;
        private int WMode;
        private String WpaKey;
        private int WpaType;

        public int getApIsolation() {
            return ApIsolation;
        }

        public void setApIsolation(int ApIsolation) {
            this.ApIsolation = ApIsolation;
        }

        public int getApStatus() {
            return ApStatus;
        }

        public void setApStatus(int ApStatus) {
            this.ApStatus = ApStatus;
        }

        public int getBandwidth() {
            return Bandwidth;
        }

        public void setBandwidth(int Bandwidth) {
            this.Bandwidth = Bandwidth;
        }

        public int getChannel() {
            return Channel;
        }

        public void setChannel(int Channel) {
            this.Channel = Channel;
        }

        public String getCountryCode() {
            return CountryCode;
        }

        public void setCountryCode(String CountryCode) {
            this.CountryCode = CountryCode;
        }

        public int getCurChannel() {
            return CurChannel;
        }

        public void setCurChannel(int CurChannel) {
            this.CurChannel = CurChannel;
        }

        public int getCurr_num() {
            return curr_num;
        }

        public void setCurr_num(int curr_num) {
            this.curr_num = curr_num;
        }

        public int getMax_numsta() {
            return max_numsta;
        }

        public void setMax_numsta(int max_numsta) {
            this.max_numsta = max_numsta;
        }

        public int getSecurityMode() {
            return SecurityMode;
        }

        public void setSecurityMode(int SecurityMode) {
            this.SecurityMode = SecurityMode;
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

        public String getWepKey() {
            return WepKey;
        }

        public void setWepKey(String WepKey) {
            this.WepKey = WepKey;
        }

        public int getWepType() {
            return WepType;
        }

        public void setWepType(int WepType) {
            this.WepType = WepType;
        }

        public int getWlanAPID() {
            return WlanAPID;
        }

        public void setWlanAPID(int WlanAPID) {
            this.WlanAPID = WlanAPID;
        }

        public int getWMode() {
            return WMode;
        }

        public void setWMode(int WMode) {
            this.WMode = WMode;
        }

        public String getWpaKey() {
            return WpaKey;
        }

        public void setWpaKey(String WpaKey) {
            this.WpaKey = WpaKey;
        }

        public int getWpaType() {
            return WpaType;
        }

        public void setWpaType(int WpaType) {
            this.WpaType = WpaType;
        }

        @Override
        public String toString() {
            return "APListBean{" + "ApIsolation=" + ApIsolation + ", ApStatus=" + ApStatus + ", Bandwidth=" + Bandwidth + ", Channel=" + Channel + ", CountryCode='" + CountryCode + '\'' + ", CurChannel=" + CurChannel + ", curr_num=" + curr_num + ", max_numsta=" + max_numsta + ", SecurityMode=" + SecurityMode + ", Ssid='" + Ssid + '\'' + ", SsidHidden=" + SsidHidden + ", WepKey='" + WepKey + '\'' + ", WepType=" + WepType + ", WlanAPID=" + WlanAPID + ", WMode=" + WMode + ", WpaKey='" + WpaKey + '\'' + ", WpaType=" + WpaType + '}';
        }
    }
}
