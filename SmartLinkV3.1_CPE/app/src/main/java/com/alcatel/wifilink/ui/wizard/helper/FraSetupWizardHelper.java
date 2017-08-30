package com.alcatel.wifilink.ui.wizard.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alcatel.wifilink.ui.wizard.allsetup.SetupWizardActivity;

public class FraSetupWizardHelper {

    /**
     * 提交切换新的Fragment
     *
     * @param activity
     * @param fm       Fragment管理+
     * @param idRes    在哪个UI切换
     * @param flag     切换标记--> 通过FragmentBucket生产出Fragment
     */
    public static void commit(SetupWizardActivity activity, FragmentManager fm, int idRes, FragmentSetupWizardEnum flag) {
        Fragment fragment = FragmentSetupWizardBucket.getFragment(activity, flag);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(idRes, fragment);
        ft.commitAllowingStateLoss();
    }

}
