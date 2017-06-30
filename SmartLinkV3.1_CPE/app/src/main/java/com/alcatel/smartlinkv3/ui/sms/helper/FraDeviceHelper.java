package com.alcatel.smartlinkv3.ui.sms.helper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FraDeviceHelper {

    /**
     * 提交切换新的Fragment
     *
     * @param fm    Fragment管理+
     * @param idRes 在哪个UI切换
     * @param flag  切换标记--> 通过FragmentBucket生产出Fragment
     */
    public static Fragment commit(Activity activity, FragmentManager fm, int idRes, FragmentDeviceEnum flag) {
        Fragment fragment = FragmentDeviceBucket.getFragment(activity, flag);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(idRes, fragment);
        ft.commitAllowingStateLoss();
        return fragment;
    }

}
