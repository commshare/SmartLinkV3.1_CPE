package com.alcatel.smartlinkv3.rx.model;

/**
 * Created by qianli.ma on 2017/12/1 0001.
 */

public class SystemResult {

    private String AppVersion;
    private String DeviceName;
    private String HttpApiVersion;
    private String HwVersion;
    private String ICCID;
    private String IMEI;
    private String IMSI;
    private String MacAddress;
    private String MSISDN;
    private int MsisdnMark;
    private int sn;
    private String SwVersion;
    private String WebUiVersion;

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String AppVersion) {
        this.AppVersion = AppVersion;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName = DeviceName;
    }

    public String getHttpApiVersion() {
        return HttpApiVersion;
    }

    public void setHttpApiVersion(String HttpApiVersion) {
        this.HttpApiVersion = HttpApiVersion;
    }

    public String getHwVersion() {
        return HwVersion;
    }

    public void setHwVersion(String HwVersion) {
        this.HwVersion = HwVersion;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String MacAddress) {
        this.MacAddress = MacAddress;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public int getMsisdnMark() {
        return MsisdnMark;
    }

    public void setMsisdnMark(int MsisdnMark) {
        this.MsisdnMark = MsisdnMark;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getSwVersion() {
        return SwVersion;
    }

    public void setSwVersion(String SwVersion) {
        this.SwVersion = SwVersion;
    }

    public String getWebUiVersion() {
        return WebUiVersion;
    }

    public void setWebUiVersion(String WebUiVersion) {
        this.WebUiVersion = WebUiVersion;
    }
}
