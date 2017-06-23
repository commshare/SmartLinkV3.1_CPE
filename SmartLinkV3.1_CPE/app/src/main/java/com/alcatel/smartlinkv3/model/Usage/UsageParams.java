package com.alcatel.smartlinkv3.model.Usage;

/**
 * Created by qianli.ma on 2017/6/22.
 */

public class UsageParams {

    public UsageParams(String clear_time) {
        this.clear_time = clear_time;
    }

    public String clear_time;

    public String getClear_time() {
        return clear_time;
    }

    public void setClear_time(String clear_time) {
        this.clear_time = clear_time;
    }

    @Override
    public String toString() {
        return "UsageParams{" + "clear_time='" + clear_time + '\'' + '}';
    }
}
