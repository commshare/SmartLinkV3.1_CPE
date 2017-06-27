package com.alcatel.smartlinkv3.model.profile;

/**
 * Created by jianqiang.li on 2017/6/27.
 */

public class ProfileList {

    /**
     * ProfileID : 1
     * ProfileName : China Unicom
     * APN : 3GNET
     * Password :
     * AuthType : -1
     * DailNumber : *99#
     * UserName :
     * Default : 1
     * IsPredefine : 0
     * IPAdrress :
     * PdpType : -1
     */

    private int ProfileID;
    private String ProfileName;
    private String APN;
    private String Password;
    private int AuthType;
    private String DailNumber;
    private String UserName;
    private int Default;
    private int IsPredefine;
    private String IPAdrress;
    private int PdpType;

    public int getProfileID() {
        return ProfileID;
    }

    public void setProfileID(int ProfileID) {
        this.ProfileID = ProfileID;
    }

    public String getProfileName() {
        return ProfileName;
    }

    public void setProfileName(String ProfileName) {
        this.ProfileName = ProfileName;
    }

    public String getAPN() {
        return APN;
    }

    public void setAPN(String APN) {
        this.APN = APN;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public int getAuthType() {
        return AuthType;
    }

    public void setAuthType(int AuthType) {
        this.AuthType = AuthType;
    }

    public String getDailNumber() {
        return DailNumber;
    }

    public void setDailNumber(String DailNumber) {
        this.DailNumber = DailNumber;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public int getDefault() {
        return Default;
    }

    public void setDefault(int Default) {
        this.Default = Default;
    }

    public int getIsPredefine() {
        return IsPredefine;
    }

    public void setIsPredefine(int IsPredefine) {
        this.IsPredefine = IsPredefine;
    }

    public String getIPAdrress() {
        return IPAdrress;
    }

    public void setIPAdrress(String IPAdrress) {
        this.IPAdrress = IPAdrress;
    }

    public int getPdpType() {
        return PdpType;
    }

    public void setPdpType(int PdpType) {
        this.PdpType = PdpType;
    }
}
