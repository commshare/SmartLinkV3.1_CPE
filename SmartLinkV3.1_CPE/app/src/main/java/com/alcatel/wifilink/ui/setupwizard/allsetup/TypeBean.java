package com.alcatel.wifilink.ui.setupwizard.allsetup;

/**
 * Created by qianli.ma on 2017/7/24.
 */

/* 辅助bean, 用于跳转到home activity时进行连接模式的判断 */
public class TypeBean {

    public String type;

    public TypeBean(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TypeBean{" + "type='" + type + '\'' + '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
