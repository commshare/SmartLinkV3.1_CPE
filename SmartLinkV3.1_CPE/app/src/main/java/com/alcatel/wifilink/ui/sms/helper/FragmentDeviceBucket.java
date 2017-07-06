package com.alcatel.wifilink.ui.sms.helper;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.alcatel.wifilink.ui.sms.fragment.ui.DeviceBlockFragment;
import com.alcatel.wifilink.ui.sms.fragment.ui.DeviceConnectFragment;

/**
 * @作者 qianli
 * @开发时间 下午5:59:51
 * @功能描述 Fragment工厂(主页)
 * @SVN更新者 $Author$
 * @SVN更新时间 $Date$
 * @SVN当前版本 $Rev$
 */
public class FragmentDeviceBucket {

    /**
     * 根据当前位置返回对应的Fragment
     *
     * @param flag 传递进来的位置
     * @return 对应位置的Fragment
     */
    public static Fragment getFragment(Activity activity, FragmentDeviceEnum flag) {

        Fragment fragment = null;
        if (flag.equals(FragmentDeviceEnum.CONNECT)) {
            fragment = new DeviceConnectFragment(activity);
        } else if (flag.equals(FragmentDeviceEnum.BLOCK)) {
            fragment = new DeviceBlockFragment(activity);
        }
        return fragment;
    }

}
