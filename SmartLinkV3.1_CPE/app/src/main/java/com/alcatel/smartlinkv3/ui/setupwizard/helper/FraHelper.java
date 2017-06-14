package com.alcatel.smartlinkv3.ui.setupwizard.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alcatel.smartlinkv3.ui.setupwizard.allsetup.SetupWizardActivity;

public class FraHelper {

    /**
     * 提交切换新的Fragment
     *
     * @param activity
     * @param fm       Fragment管理+
     * @param idRes    在哪个UI切换
     * @param flag     切换标记--> 通过FragmentBucket生产出Fragment
     */
    public static void commit(SetupWizardActivity activity, FragmentManager fm, int idRes, FragmentEnum flag) {
        Fragment fragment = FragmentBucket.getFragment(activity, flag);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(idRes, fragment);
        ft.commitAllowingStateLoss();
    }

}
