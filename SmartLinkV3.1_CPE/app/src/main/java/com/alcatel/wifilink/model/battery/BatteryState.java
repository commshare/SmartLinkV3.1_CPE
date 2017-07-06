package com.alcatel.wifilink.model.battery;

/**
 * Created by qianli.ma on 2017/6/21.
 */

public class BatteryState {
    public int chg_state;
    public int bat_cap;
    public int bat_level;
    public int BatteryLevel;

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

    public int getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        BatteryLevel = batteryLevel;
    }
}
