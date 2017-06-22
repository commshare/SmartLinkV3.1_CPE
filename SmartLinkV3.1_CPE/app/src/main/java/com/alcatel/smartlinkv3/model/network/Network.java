package com.alcatel.smartlinkv3.model.network;

/**
 * Created by jianqiang.li on 2017/6/22.
 */

public class Network {

    /**
     * NetworkMode : 0
     * NetselectionMode : 0
     * NetworkBand : 0
     * DomesticRoam : 0
     * DomesticRoamGuard : 0
     */

    private int NetworkMode;
    private int NetselectionMode;
    private int NetworkBand;
    private int DomesticRoam;
    private int DomesticRoamGuard;

    public int getNetworkMode() {
        return NetworkMode;
    }

    public void setNetworkMode(int NetworkMode) {
        this.NetworkMode = NetworkMode;
    }

    public int getNetselectionMode() {
        return NetselectionMode;
    }

    public void setNetselectionMode(int NetselectionMode) {
        this.NetselectionMode = NetselectionMode;
    }

    public int getNetworkBand() {
        return NetworkBand;
    }

    public void setNetworkBand(int NetworkBand) {
        this.NetworkBand = NetworkBand;
    }

    public int getDomesticRoam() {
        return DomesticRoam;
    }

    public void setDomesticRoam(int DomesticRoam) {
        this.DomesticRoam = DomesticRoam;
    }

    public int getDomesticRoamGuard() {
        return DomesticRoamGuard;
    }

    public void setDomesticRoamGuard(int DomesticRoamGuard) {
        this.DomesticRoamGuard = DomesticRoamGuard;
    }
}
