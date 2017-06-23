package com.alcatel.smartlinkv3.model.network;

/**
 * Created by qianli.ma on 2017/6/21.
 */

public class NetworkInfos {


    public String PLMN;
    public int NetworkType;
    public String NetworkName;
    public String SpnName;
    public String LAC;
    public String CellId;
    public String RncId;
    public int Roaming;
    public int Domestic_Roaming;
    public int SignalStrength;
    public String mcc;
    public String mnc;
    public String SINR;
    public String RSRP;
    public String RSSI;
    public String eNBID;
    public String CGI;
    public String CenterFreq;
    public String TxPWR;
    public int LTE_state;
    public String PLMN_name;
    public int Band;
    public String DL_channel;
    public String UL_channel;
    public String RSRQ;
    public int EcIo;
    public int RSCP;

    public String getPLMN() {
        return PLMN;
    }

    public void setPLMN(String PLMN) {
        this.PLMN = PLMN;
    }

    public int getNetworkType() {
        return NetworkType;
    }

    public void setNetworkType(int networkType) {
        NetworkType = networkType;
    }

    public String getNetworkName() {
        return NetworkName;
    }

    public void setNetworkName(String networkName) {
        NetworkName = networkName;
    }

    public String getSpnName() {
        return SpnName;
    }

    public void setSpnName(String spnName) {
        SpnName = spnName;
    }

    public String getLAC() {
        return LAC;
    }

    public void setLAC(String LAC) {
        this.LAC = LAC;
    }

    public String getCellId() {
        return CellId;
    }

    public void setCellId(String cellId) {
        CellId = cellId;
    }

    public String getRncId() {
        return RncId;
    }

    public void setRncId(String rncId) {
        RncId = rncId;
    }

    public int getRoaming() {
        return Roaming;
    }

    public void setRoaming(int roaming) {
        Roaming = roaming;
    }

    public int getDomestic_Roaming() {
        return Domestic_Roaming;
    }

    public void setDomestic_Roaming(int domestic_Roaming) {
        Domestic_Roaming = domestic_Roaming;
    }

    public int getSignalStrength() {
        return SignalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        SignalStrength = signalStrength;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getSINR() {
        return SINR;
    }

    public void setSINR(String SINR) {
        this.SINR = SINR;
    }

    public String getRSRP() {
        return RSRP;
    }

    public void setRSRP(String RSRP) {
        this.RSRP = RSRP;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String geteNBID() {
        return eNBID;
    }

    public void seteNBID(String eNBID) {
        this.eNBID = eNBID;
    }

    public String getCGI() {
        return CGI;
    }

    public void setCGI(String CGI) {
        this.CGI = CGI;
    }

    public String getCenterFreq() {
        return CenterFreq;
    }

    public void setCenterFreq(String centerFreq) {
        CenterFreq = centerFreq;
    }

    public String getTxPWR() {
        return TxPWR;
    }

    public void setTxPWR(String txPWR) {
        TxPWR = txPWR;
    }

    public int getLTE_state() {
        return LTE_state;
    }

    public void setLTE_state(int LTE_state) {
        this.LTE_state = LTE_state;
    }

    public String getPLMN_name() {
        return PLMN_name;
    }

    public void setPLMN_name(String PLMN_name) {
        this.PLMN_name = PLMN_name;
    }

    public int getBand() {
        return Band;
    }

    public void setBand(int band) {
        Band = band;
    }

    public String getDL_channel() {
        return DL_channel;
    }

    public void setDL_channel(String DL_channel) {
        this.DL_channel = DL_channel;
    }

    public String getUL_channel() {
        return UL_channel;
    }

    public void setUL_channel(String UL_channel) {
        this.UL_channel = UL_channel;
    }

    public String getRSRQ() {
        return RSRQ;
    }

    public void setRSRQ(String RSRQ) {
        this.RSRQ = RSRQ;
    }

    public int getEcIo() {
        return EcIo;
    }

    public void setEcIo(int ecIo) {
        EcIo = ecIo;
    }

    public int getRSCP() {
        return RSCP;
    }

    public void setRSCP(int RSCP) {
        this.RSCP = RSCP;
    }

    @Override
    public String toString() {
        return "NetworkInfos{" + "PLMN='" + PLMN + '\'' + ", NetworkType=" + NetworkType + ", NetworkName='" + NetworkName + '\'' + ", SpnName='" + SpnName + '\'' + ", LAC='" + LAC + '\'' + ", CellId='" + CellId + '\'' + ", RncId='" + RncId + '\'' + ", Roaming=" + Roaming + ", Domestic_Roaming=" + Domestic_Roaming + ", SignalStrength=" + SignalStrength + ", mcc='" + mcc + '\'' + ", mnc='" + mnc + '\'' + ", SINR='" + SINR + '\'' + ", RSRP='" + RSRP + '\'' + ", RSSI='" + RSSI + '\'' + ", eNBID='" + eNBID + '\'' + ", CGI='" + CGI + '\'' + ", CenterFreq='" + CenterFreq + '\'' + ", TxPWR='" + TxPWR + '\'' + ", LTE_state=" + LTE_state + ", PLMN_name='" + PLMN_name + '\'' + ", Band=" + Band + ", DL_channel='" + DL_channel + '\'' + ", UL_channel='" + UL_channel + '\'' + ", RSRQ='" + RSRQ + '\'' + ", EcIo=" + EcIo + ", RSCP=" + RSCP + '}';
    }
}
