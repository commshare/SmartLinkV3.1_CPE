package com.alcatel.wifilink.ui.wizard.helper;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.alcatel.wifilink.ui.wizard.fragment.ConnectTypeFragment;
import com.alcatel.wifilink.ui.wizard.fragment.NetModeStatusFragment;
import com.alcatel.wifilink.ui.wizard.fragment.PinCodeFragment;
import com.alcatel.wifilink.ui.wizard.fragment.PukCodeFragment;
import com.alcatel.wifilink.ui.wizard.fragment.NetModeFragment;

/**
 * @作者 qianli
 * @开发时间 下午5:59:51
 * @功能描述 Fragment工厂(主页)
 * @SVN更新者 $Author$
 * @SVN更新时间 $Date$
 * @SVN当前版本 $Rev$
 */
public class FragmentBucket {

    /**
     * 根据当前位置返回对应的Fragment
     *
     * @param flag 传递进来的位置
     * @return 对应位置的Fragment
     */
    public static Fragment getFragment(Activity activity, FragmentEnum flag) {

        Fragment fragment = null;
        if (flag.equals(FragmentEnum.CONNECTTYPE_FRA)) {
            fragment = new ConnectTypeFragment(activity);
        } else if (flag.equals(FragmentEnum.PIN_FRA)) {
            fragment = new PinCodeFragment(activity);
        } else if (flag.equals(FragmentEnum.PUK_FRA)) {
            fragment = new PukCodeFragment(activity);
        } else if (flag.equals(FragmentEnum.WAN_NETMODE_CHOICE_FRA)) {
            fragment = new NetModeFragment(activity);
        } else if (flag.equals(FragmentEnum.WAN_NETMODE_STATE_FRA)) {
            fragment = new NetModeStatusFragment(activity);
        }
        return fragment;
    }

}
