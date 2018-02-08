package com.alcatel.wifilink.rx.bean;

/**
 * Created by qianli.ma on 2018/2/7 0007.
 */

public class FeedbackTypeBean {
    private String typeName;
    private boolean isSelected;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "FeedbackTypeBean{" + "typeName='" + typeName + '\'' + ", isSelected=" + isSelected + '}';
    }
}
