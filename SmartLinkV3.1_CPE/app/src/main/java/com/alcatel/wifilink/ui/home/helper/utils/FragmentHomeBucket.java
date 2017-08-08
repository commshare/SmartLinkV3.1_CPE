package com.alcatel.wifilink.ui.home.helper.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.home.fragment.MainFragment;
import com.alcatel.wifilink.ui.home.fragment.SettingFragment;
import com.alcatel.wifilink.ui.home.fragment.SmsFragments;
import com.alcatel.wifilink.ui.home.fragment.WifiFragment;

/**
 * @作者 qianli
 * @开发时间 下午5:59:51
 * @功能描述 Fragment工厂(主页)
 * @SVN更新者 $Author$
 * @SVN更新时间 $Date$
 * @SVN当前版本 $Rev$
 */
public class FragmentHomeBucket {

    public static final String MAIN_FRA = "MAIN";
    public static final String WIFI_FRA = "WIFI";
    public static final String SMS_FRA = "SMS";
    public static final String SETTING_FRA = "SETTING";

    /**
     * 根据当前位置返回对应的Fragment
     *
     * @param flag 传递进来的位置
     * @return 对应位置的Fragment
     */
    public static Fragment getFragment(Activity activity, FragmentHomeEnum flag) {

        Fragment fragment = null;
        if (flag.equals(FragmentHomeEnum.MAIN)) {
            fragment = new MainFragment(activity);
        } else if (flag.equals(FragmentHomeEnum.WIFI)) {
            fragment = new WifiFragment();
        } else if (flag.equals(FragmentHomeEnum.SMS)) {
            // fragment = new SmsFragment();
            fragment = new SmsFragments();
        } else if (flag.equals(FragmentHomeEnum.SETTING)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    /**
     * 隐藏或者显示ID(使用SHOW HIDE方式)
     *
     * @param fm
     * @param containerId
     * @param en
     */
    public static void showOrHideFragment(Context context, FragmentManager fm, int containerId, FragmentHomeEnum en) {
        FragmentTransaction ft = fm.beginTransaction();// transation
        Fragment mainFragment = fm.findFragmentByTag(MAIN_FRA);
        Fragment wifiFragment = fm.findFragmentByTag(WIFI_FRA);
        Fragment smsFragment = fm.findFragmentByTag(SMS_FRA);
        Fragment settingFragment = fm.findFragmentByTag(SETTING_FRA);
        if (mainFragment != null) {
            ft.hide(mainFragment);
        }
        if (wifiFragment != null) {
            ft.hide(wifiFragment);
        }
        if (smsFragment != null) {
            ft.hide(smsFragment);
        }
        if (settingFragment != null) {
            ft.hide(settingFragment);
        }
        switch (en) {
            case MAIN:/* main */
                if (mainFragment == null) {
                    mainFragment = new MainFragment((Activity) context);
                    ft.add(containerId, mainFragment, MAIN_FRA);
                } else {
                    ft.show(mainFragment);
                }
                break;
            case WIFI:/* wifi */
                if (wifiFragment == null) {
                    wifiFragment = new WifiFragment();
                    ft.add(containerId, wifiFragment, WIFI_FRA);
                } else {
                    ft.show(wifiFragment);
                }
                break;
            case SMS:/* sms */
                if (smsFragment == null) {
                    smsFragment = new SmsFragments((Activity) context);
                    ft.add(containerId, smsFragment, SMS_FRA);
                } else {
                    ft.show(smsFragment);
                }
                break;
            case SETTING:/* setting */
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    ft.add(containerId, settingFragment, SETTING_FRA);
                } else {
                    ft.show(settingFragment);
                }
                break;
            default:
                break;
        }
        ft.commit();
    }

    public static void afterSwitchLanguageReloadPage(Context context, FragmentManager fm, int containerId) {
        FragmentTransaction ft = fm.beginTransaction();// transation
        Fragment mainFragment = fm.findFragmentByTag(MAIN_FRA);
        Fragment wifiFragment = fm.findFragmentByTag(WIFI_FRA);
        Fragment smsFragment = fm.findFragmentByTag(SMS_FRA);
        Fragment settingFragment = fm.findFragmentByTag(SETTING_FRA);
        if (mainFragment != null) {
            ft.remove(mainFragment);
            mainFragment = new MainFragment((Activity) context);
            ft.add(containerId, mainFragment, MAIN_FRA);
        }
        if (wifiFragment != null) {
            ft.remove(wifiFragment);
            wifiFragment = new WifiFragment();
            ft.add(containerId, wifiFragment, WIFI_FRA);
        }
        if (smsFragment != null) {
            ft.remove(smsFragment);
            smsFragment = new SmsFragments((Activity) context);
            ft.add(containerId, smsFragment, SMS_FRA);
        }
        if (settingFragment != null) {
            ft.remove(settingFragment);
            settingFragment = new SettingFragment();
            ft.add(containerId, settingFragment, SETTING_FRA);
        }
        ft.show(settingFragment);
        ft.commit();
    }
}
