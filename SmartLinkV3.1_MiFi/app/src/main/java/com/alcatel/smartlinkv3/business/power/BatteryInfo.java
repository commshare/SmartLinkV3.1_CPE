package com.alcatel.smartlinkv3.business.power;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.ConstValue;

public class BatteryInfo extends BaseResult {

    /*
     * 	0: Charging;
                    1: Completed;
                    2: Removed;
                    3:Abort: Charger abort:error occur
     */
    private int chg_state = ConstValue.CHARGE_STATE_REMOVED;
    private int BatteryLevel = 0;//BatteryLevel  (Min: 1 Max:100)

    @Override
    public void clear() {
        chg_state = ConstValue.CHARGE_STATE_REMOVED;
        BatteryLevel = 0;
    }

    //Charge state
    public int getChargeState() {
        return chg_state;
    }

    public void setChargeState(int nChargeState) {
        chg_state = nChargeState;
    }

    //Charge state
    public int getBatterLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(int nBatteryLevel) {
        BatteryLevel = nBatteryLevel;
    }
}
