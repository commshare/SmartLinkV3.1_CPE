package com.alcatel.smartlinkv3.rx.model;

/**
 * Created by qianli.ma on 2017/12/1 0001.
 */

public class SystemResult {

    private String DeviceName;
    private String IMEI;
    private String HwVersion;
    private String SwVersion;
    private String HttpApiVersion;
    private String WebUiVersion;
    private String MacAddress;
    private String MSISDN;

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getHwVersion() {
        return HwVersion;
    }

    public void setHwVersion(String hwVersion) {
        HwVersion = hwVersion;
    }

    public String getSwVersion() {
        return SwVersion;
    }

    public void setSwVersion(String swVersion) {
        SwVersion = swVersion;
    }

    public String getHttpApiVersion() {
        return HttpApiVersion;
    }

    public void setHttpApiVersion(String httpApiVersion) {
        HttpApiVersion = httpApiVersion;
    }

    public String getWebUiVersion() {
        return WebUiVersion;
    }

    public void setWebUiVersion(String webUiVersion) {
        WebUiVersion = webUiVersion;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }
}
