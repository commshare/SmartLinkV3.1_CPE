package com.alcatel.smartlinkv3.model.sharing;

/**
 * Created by ZQ on 2017/6/22.
 */

public class FTPSettings {

    /**
     *0: disable
      1: enable
     */
    private int FtpStatus;
    /**
     *  Whether allowing anonymous user to login
     *  0: disable
        1: enable
     */
    private int Anonymous;

    /**The auth type of sharing
     * 0: ReadOnly
       1: ReadWrite
     */
    private int AuthType;


    public int getFtpStatus() {
        return FtpStatus;
    }

    public void setFtpStatus(int ftpStatus) {
        FtpStatus = ftpStatus;
    }

    public int getAnonymous() {
        return Anonymous;
    }

    public void setAnonymous(int anonymous) {
        Anonymous = anonymous;
    }

    public int getAuthType() {
        return AuthType;
    }

    public void setAuthType(int authType) {
        AuthType = authType;
    }

    @Override
    public String toString() {
        return "FTPSettings{" +
                "FtpStatus=" + FtpStatus +
                ", Anonymous=" + Anonymous +
                ", AuthType=" + AuthType +
                '}';
    }

}
