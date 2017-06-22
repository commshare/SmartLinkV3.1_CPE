package com.alcatel.smartlinkv3.model.sharing;

/**
 * Created by ZQ on 2017/6/22.
 */

public class DLNASettings {

    /**
     *0: disable
      1: enable
     */
    private int DlnaStatus;
    /**
     *   Name of dlna server
     */
    private String DlnaName;


    public int getDlnaStatus() {
        return DlnaStatus;
    }

    public void setDlnaStatus(int dlnaStatus) {
        DlnaStatus = dlnaStatus;
    }

    public String getDlnaName() {
        return DlnaName;
    }

    public void setDlnaName(String dlnaName) {
        DlnaName = dlnaName;
    }

    @Override
    public String toString() {
        return "DLNASettings{" +
                "DlnaStatus=" + DlnaStatus +
                ", DlnaName='" + DlnaName + '\'' +
                '}';
    }
}
