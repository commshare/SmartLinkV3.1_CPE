package com.alcatel.wifilink.ui.home.helper.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.alcatel.wifilink.ui.home.fragment.MainFragment;
import com.alcatel.wifilink.ui.home.fragment.SmsFragment;
import com.alcatel.wifilink.ui.home.fragment.SettingFragment;
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
            fragment = new SmsFragment();
        } else if (flag.equals(FragmentHomeEnum.SETTING)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

}
