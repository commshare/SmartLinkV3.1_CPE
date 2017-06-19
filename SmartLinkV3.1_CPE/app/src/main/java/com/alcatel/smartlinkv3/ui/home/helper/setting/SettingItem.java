package com.alcatel.smartlinkv3.ui.home.helper.setting;

public class SettingItem {
        //Item name
        private String m_strItem;
        private Boolean m_blHasUpgrade;

        public SettingItem(String strItem, Boolean blHasUpgrade) {
            super();
            this.m_strItem = strItem;
            this.m_blHasUpgrade = blHasUpgrade;
        }

        public Boolean getUpgradeFlag() {
            return m_blHasUpgrade;
        }

        public void setUpgradeFlag(Boolean blHasUpgrade) {
            m_blHasUpgrade = blHasUpgrade;
        }

        public String getItemName() {
            return m_strItem;
        }
    }
