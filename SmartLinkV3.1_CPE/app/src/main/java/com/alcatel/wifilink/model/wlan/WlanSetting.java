package com.alcatel.wifilink.model.wlan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by qianli.ma on 2017/11/20 0020.
 */

public class WlanSetting implements Cloneable, Serializable {

    private int WiFiOffTime;
    private AP2GBean AP2G;
    private AP2GGuestBean AP2G_guest;
    private AP5GBean AP5G;
    private AP5GGuestBean AP5G_guest;

    //深克隆
    public WlanSetting deepClone() {
        WlanSetting stu = null;
        try {
            stu = null;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bo);
            oos.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            stu = (WlanSetting) oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stu;
    }

    public int getWiFiOffTime() {
        return WiFiOffTime;
    }

    public void setWiFiOffTime(int WiFiOffTime) {
        this.WiFiOffTime = WiFiOffTime;
    }

    public AP2GBean getAP2G() {
        return AP2G;
    }

    public void setAP2G(AP2GBean AP2G) {
        this.AP2G = AP2G;
    }

    public AP2GGuestBean getAP2G_guest() {
        return AP2G_guest;
    }

    public void setAP2G_guest(AP2GGuestBean AP2G_guest) {
        this.AP2G_guest = AP2G_guest;
    }

    public AP5GBean getAP5G() {
        return AP5G;
    }

    public void setAP5G(AP5GBean AP5G) {
        this.AP5G = AP5G;
    }

    public AP5GGuestBean getAP5G_guest() {
        return AP5G_guest;
    }

    public void setAP5G_guest(AP5GGuestBean AP5G_guest) {
        this.AP5G_guest = AP5G_guest;
    }

    public static class AP2GBean implements Serializable {
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

    public static class AP2GGuestBean implements Serializable {
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

    public static class AP5GBean implements Serializable {
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

    public static class AP5GGuestBean implements Serializable {
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
}
