package com.alcatel.smartlinkv3.ui.setupwizard.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatel.smartlinkv3.business.wanguide.StatusBean;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;
import com.alcatel.smartlinkv3.ui.setupwizard.allsetup.SetupWizardActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NetModeStatusFragment extends Fragment {

    private SetupWizardActivity activity;
    private final int SUCCESS = 1;
    private final int FAILED = 0;
    private StatusBean statusBean;// 状态对象


    public NetModeStatusFragment(Activity activity) {
        this.activity = (SetupWizardActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* EVENTBUS观察者--> StatusBean */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getConnectStatus(StatusBean statusBean) {
        this.statusBean = statusBean;
        initData();
    }

    private void initData() {
        int status = statusBean.getStatus();
        //status = 0;
        // 根据连接状态切换界面
        if (status == SUCCESS) {
            // 1.显示成功页
            activity.mRl_Success.setVisibility(View.VISIBLE);
            // 1.1.设置成功略过选择页--> 进入快速启动
            CPEConfig.getInstance().setQuickSetupFlag();
            // 2.延迟2秒跳转到setting页
            ChangeActivity.toActivity(getActivity(), SettingWifiActivity.class, true, true, false, 2000);
        } else if (status == FAILED) {
            // 显示失败页
            activity.mRl_Failed.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
