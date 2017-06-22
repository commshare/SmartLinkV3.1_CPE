package com.alcatel.smartlinkv3.model.system;

/**
 * Created by yaodong.zhang on 2017/6/21.
 */

public class SystemInfo {

    /**
     * SwVersion : HH70_IA_02.00_10
     * HwVersion : HH70-V-V1.0
     * WebUiVersion : HH70_JRDRESOURCE_IA_05_2078
     * HttpApiVersion : TCL-HTTP
     * AppVersion : V1.0
     * DeviceName : HH70
     * IMEI : 869486010150315
     * sn :
     * MacAddress : 8c:dc:d4:31:ba:b5
     * IMSI : 460010322682758
     * ICCID : 89860116851018021319
     * MsisdnMark : 1
     * MSISDN : 8618620322862
     * SwVersionMain : HH70_IA_02.00_10
     * MacAddress5G : 85:55:55:31:ba:b5
     */

    private String SwVersion;
    private String HwVersion;
    private String WebUiVersion;
    private String HttpApiVersion;
    private String AppVersion;
    private String DeviceName;
    private String IMEI;
    private String sn;
    private String MacAddress;
    private String IMSI;
    private String ICCID;
    private int MsisdnMark;
    private String MSISDN;
    private String SwVersionMain;
    private String MacAddress5G;

    public String getSwVersion() {
        return SwVersion;
    }

    public void setSwVersion(String SwVersion) {
        this.SwVersion = SwVersion;
    }

    public String getHwVersion() {
        return HwVersion;
    }

    public void setHwVersion(String HwVersion) {
        this.HwVersion = HwVersion;
    }

    public String getWebUiVersion() {
        return WebUiVersion;
    }

    public void setWebUiVersion(String WebUiVersion) {
        this.WebUiVersion = WebUiVersion;
    }

    public String getHttpApiVersion() {
        return HttpApiVersion;
    }

    public void setHttpApiVersion(String HttpApiVersion) {
        this.HttpApiVersion = HttpApiVersion;
    }

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

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String MacAddress) {
        this.MacAddress = MacAddress;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public int getMsisdnMark() {
        return MsisdnMark;
    }

    public void setMsisdnMark(int MsisdnMark) {
        this.MsisdnMark = MsisdnMark;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getSwVersionMain() {
        return SwVersionMain;
    }

    public void setSwVersionMain(String SwVersionMain) {
        this.SwVersionMain = SwVersionMain;
    }

    public String getMacAddress5G() {
        return MacAddress5G;
    }

    public void setMacAddress5G(String MacAddress5G) {
        this.MacAddress5G = MacAddress5G;
    }
}
