package com.alcatel.wifilink.model.system;

/**
 * Created by qianli.ma on 2018/2/6 0006.
 */

public class SystemStates {
    /**
     * NetworkType : 8
     * NetworkName : UNICOM
     * Roaming : 1
     * Domestic_Roaming : 1
     * SignalStrength : 3
     * chg_state : 1
     * bat_cap : 100
     * bat_level : 4
     * ConnectionStatus : 2
     * Conprofileerror : 0
     * SmsState : 2
     * WlanState : 1
     * curr_num : 0
     * curr_num_2g : 0
     * curr_num_5g : 0
     * TotalConnNum : 1
     * UsbStatus : 0
     * UsbName : disk1
     * Ssid_2g : HH70-459D_2.4GHz
     * Ssid_5g : HH70-459D_5GHz
     * WlanState_2g : 1
     * WlanState_5g : 1
     * DeviceUptime : 100
     */

    private int NetworkType;
    private String NetworkName;
    private int Roaming;
    private int Domestic_Roaming;
    private int SignalStrength;
    private int chg_state;
    private int bat_cap;
    private int bat_level;
    private int ConnectionStatus;
    private int Conprofileerror;
    private int SmsState;
    private int WlanState;
    private int curr_num;
    private int curr_num_2g;
    private int curr_num_5g;
    private int TotalConnNum;
    private int UsbStatus;
    private String UsbName;
    private String Ssid_2g;
    private String Ssid_5g;
    private int WlanState_2g;
    private int WlanState_5g;
    private int DeviceUptime;

    public int getNetworkType() {
        return NetworkType;
    }

    public void setNetworkType(int NetworkType) {
        this.NetworkType = NetworkType;
    }

    public String getNetworkName() {
        return NetworkName;
    }

    public void setNetworkName(String NetworkName) {
        this.NetworkName = NetworkName;
    }

    public int getRoaming() {
        return Roaming;
    }

    public void setRoaming(int Roaming) {
        this.Roaming = Roaming;
    }

    public int getDomestic_Roaming() {
        return Domestic_Roaming;
    }

    public void setDomestic_Roaming(int Domestic_Roaming) {
        this.Domestic_Roaming = Domestic_Roaming;
    }

    public int getSignalStrength() {
        return SignalStrength;
    }

    public void setSignalStrength(int SignalStrength) {
        this.SignalStrength = SignalStrength;
    }

    public int getChg_state() {
        return chg_state;
    }

    public void setChg_state(int chg_state) {
        this.chg_state = chg_state;
    }

    public int getBat_cap() {
        return bat_cap;
    }

    public void setBat_cap(int bat_cap) {
        this.bat_cap = bat_cap;
    }

    public int getBat_level() {
        return bat_level;
    }

    public void setBat_level(int bat_level) {
        this.bat_level = bat_level;
    }

    public int getConnectionStatus() {
        return ConnectionStatus;
    }

    public void setConnectionStatus(int ConnectionStatus) {
        this.ConnectionStatus = ConnectionStatus;
    }

    public int getConprofileerror() {
        return Conprofileerror;
    }

    public void setConprofileerror(int Conprofileerror) {
        this.Conprofileerror = Conprofileerror;
    }

    public int getSmsState() {
        return SmsState;
    }

    public void setSmsState(int SmsState) {
        this.SmsState = SmsState;
    }

    public int getWlanState() {
        return WlanState;
    }

    public void setWlanState(int WlanState) {
        this.WlanState = WlanState;
    }

    public int getCurr_num() {
        return curr_num;
    }

    public void setCurr_num(int curr_num) {
        this.curr_num = curr_num;
    }

    public int getCurr_num_2g() {
        return curr_num_2g;
    }

    public void setCurr_num_2g(int curr_num_2g) {
        this.curr_num_2g = curr_num_2g;
    }

    public int getCurr_num_5g() {
        return curr_num_5g;
    }

    public void setCurr_num_5g(int curr_num_5g) {
        this.curr_num_5g = curr_num_5g;
    }

    public int getTotalConnNum() {
        return TotalConnNum;
    }

    public void setTotalConnNum(int TotalConnNum) {
        this.TotalConnNum = TotalConnNum;
    }

    public int getUsbStatus() {
        return UsbStatus;
    }

    public void setUsbStatus(int UsbStatus) {
        this.UsbStatus = UsbStatus;
    }

    public String getUsbName() {
        return UsbName;
    }

    public void setUsbName(String UsbName) {
        this.UsbName = UsbName;
    }

    public String getSsid_2g() {
        return Ssid_2g;
    }

    public void setSsid_2g(String Ssid_2g) {
        this.Ssid_2g = Ssid_2g;
    }

    public String getSsid_5g() {
        return Ssid_5g;
    }

    public void setSsid_5g(String Ssid_5g) {
        this.Ssid_5g = Ssid_5g;
    }

    public int getWlanState_2g() {
        return WlanState_2g;
    }

    public void setWlanState_2g(int WlanState_2g) {
        this.WlanState_2g = WlanState_2g;
    }

    public int getWlanState_5g() {
        return WlanState_5g;
    }

    public void setWlanState_5g(int WlanState_5g) {
        this.WlanState_5g = WlanState_5g;
    }

    public int getDeviceUptime() {
        return DeviceUptime;
    }

    public void setDeviceUptime(int DeviceUptime) {
        this.DeviceUptime = DeviceUptime;
    }

    @Override
    public String toString() {
        return "SystemStates{" + "NetworkType=" + NetworkType + "\n" + " NetworkName='" + NetworkName + '\'' + "\n" + " Roaming=" + Roaming + "\n" + " Domestic_Roaming=" + Domestic_Roaming + "\n" + " SignalStrength=" + SignalStrength + "\n" + " chg_state=" + chg_state + "\n" + " bat_cap=" + bat_cap + "\n" + " bat_level=" + bat_level + "\n" + " ConnectionStatus=" + ConnectionStatus + "\n" + " Conprofileerror=" + Conprofileerror + "\n" + " SmsState=" + SmsState + "\n" + " WlanState=" + WlanState + "\n" + " curr_num=" + curr_num + "\n" + " curr_num_2g=" + curr_num_2g + "\n" + " curr_num_5g=" + curr_num_5g + "\n" + " TotalConnNum=" + TotalConnNum + "\n" + " UsbStatus=" + UsbStatus + "\n" + " UsbName='" + UsbName + '\'' + "\n" + " Ssid_2g='" + Ssid_2g + '\'' + "\n" + " Ssid_5g='" + Ssid_5g + '\'' + "\n" + " WlanState_2g=" + WlanState_2g + "\n" + " WlanState_5g=" + WlanState_5g + "\n" + " DeviceUptime=" + DeviceUptime + '}';
    }
}
