package com.alcatel.wifilink.model.system;

/**
 * Created by tao.j on 2017/6/20.
 */

public class SysStatus {
    /**
     * 0: NO SERVER
     1: GPRS
     2: EDGE
     3: HSPA
     4: HSUPA
     5: UMTS
     6: HSPA+
     7: DC-HSPA+
     8: LTE
     9: LTE+
     10: WCDMA
     11: CDMA
     12: GSM
     */
    private int NetworkType;
    /**
     * operator name
     */
    private String NetworkName;
    /**
     * 0: Roaming 1: No roaming
     */
    private int Roaming;
    /**
     * 0: national roaming 1: International roaming
     */
    private int Domestic_Roaming;

    /**
     * RSSI value
     -1: No service
     0: level 0
     1: level 1
     2: level 2
     3: level 3
     4: level 4
     */
    private int SignalStrength;

    /**
     * 0: disconnected
     1: connecting
     2: connected
     3: disconnecting
     */
    private int ConnectionStatus;
    /**
     * profile error code
     0: No error
     1: APN Error
     2: Pdp error
     */
    private int Conprofileerror;

    /**
     * 0: no SMS
     1: SMS full
     2: Noral
     3: New SMS
     */
    private int SmsState;

    /**
     * Current 2.4GHz WIFI client
     */
    private int curr_num_2g;

    /**
     * Current 5GHz WIFI client
     */
    private int curr_num_5g;

    /**
     * 0: off
     1: on
     2: WPS
     */
    private int WlanState;

    /**
     *  Current 2.4GHz WIFI State
     0: off
     1: on
     2: WPS
     */
    private int WlanState_2g;

    /**
     * Current 5GHz WIFI State
     0: off
     1: on
     2: WPS
     */
    private int WlanState_5g;
    /**
     * usb status
     0: Not Insert
     1: USB storage
     2: USB print
     */
    private int UsbStatus;

    /**
     * USB disk name
     */
    private String UsbName;

    /**
     * Current 2.4GHz WIFI ssid
     */
    private String Ssid_2g;

    /**
     * Current 5GHz WIFI ssid
     */
    private String Ssid_5g;

    public int getNetworkType() {
        return NetworkType;
    }

    public String getNetworkName() {
        return NetworkName;
    }

    public int getRoaming() {
        return Roaming;
    }

    public int getDomestic_Roaming() {
        return Domestic_Roaming;
    }

    public int getSignalStrength() {
        return SignalStrength;
    }

    public int getConnectionStatus() {
        return ConnectionStatus;
    }

    public int getConprofileerror() {
        return Conprofileerror;
    }

    public int getSmsState() {
        return SmsState;
    }

    public int getCurr_num_2g() {
        return curr_num_2g;
    }

    public int getCurr_num_5g() {
        return curr_num_5g;
    }

    public int getWlanState() {
        return WlanState;
    }

    public int getWlanState_2g() {
        return WlanState_2g;
    }

    public int getWlanState_5g() {
        return WlanState_5g;
    }

    public int getUsbStatus() {
        return UsbStatus;
    }

    public String getUsbName() {
        return UsbName;
    }

    public String getSsid_2g() {
        return Ssid_2g;
    }

    public String getSsid_5g() {
        return Ssid_5g;
    }

    @Override
    public String toString() {
        return "SysStatus{" +
                "NetworkType=" + NetworkType +
                ", NetworkName='" + NetworkName + '\'' +
                ", Roaming=" + Roaming +
                ", Domestic_Roaming=" + Domestic_Roaming +
                ", SignalStrength=" + SignalStrength +
                ", ConnectionStatus=" + ConnectionStatus +
                ", Conprofileerror=" + Conprofileerror +
                ", SmsState=" + SmsState +
                ", curr_num_2g=" + curr_num_2g +
                ", curr_num_5g=" + curr_num_5g +
                ", WlanState=" + WlanState +
                ", WlanState_2g=" + WlanState_2g +
                ", WlanState_5g=" + WlanState_5g +
                ", UsbStatus=" + UsbStatus +
                ", UsbName='" + UsbName + '\'' +
                ", Ssid_2g='" + Ssid_2g + '\'' +
                ", Ssid_5g='" + Ssid_5g + '\'' +
                '}';
    }
}
