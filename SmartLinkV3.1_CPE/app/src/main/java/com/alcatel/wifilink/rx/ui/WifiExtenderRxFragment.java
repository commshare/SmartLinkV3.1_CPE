package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2018/2/5 0005.
 */

public class WifiExtenderRxFragment extends Fragment implements FragmentBackHandler {

    @BindView(R.id.iv_wifiExtender_back)
    ImageView ivBack;// 返回键
    @BindView(R.id.tv_wifiExtender_scan)
    TextView tvScan;// 扫描键

    @BindView(R.id.tv_wifiExtender_not_connect_tip)
    TextView tvNotConnectTip;// 掉线提示

    @BindView(R.id.iv_wifiExtender_panel_socket)
    ImageView ivPanelSocket;// wifi extender开关
    @BindView(R.id.tv_wifiExtender_not_connect_panel_des)
    TextView tvNotConnectPanelDes;// 连接描述

    @BindView(R.id.rl_wifiExtender_had_connected)
    PercentRelativeLayout rlHadConnected;// 已连接的布局
    @BindView(R.id.tv_wifiExtender_had_connected_hotDot_name)
    TextView tvHadConnectedHotDotName;// 已连接热点名称
    @BindView(R.id.iv_wifiExtender_had_connected_wifi)
    ImageView ivHadConnectedWifi;// 已连接热点的强度
    @BindView(R.id.iv_wifiExtender_had_connected_lock)
    ImageView ivHadConnectedLock;// 已连接的热点是否带密码

    @BindView(R.id.rcv_wifiExtender_available_network)
    RecyclerView rcvWifiExtenderAvailableNetwork;// 待连接的热点列表

    Unbinder unbinder;
    private View inflate;
    private HomeRxActivity activity;
    // TODO: 2018/2/6 0006  关联数据

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (HomeRxActivity) getActivity();
        inflate = View.inflate(getActivity(), R.layout.fra_wifi_extender_rx, null);
        resetUi();
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            resetUi();
        }
    }

    private void resetUi() {
        if (activity == null) {
            activity = (HomeRxActivity) getActivity();
        }
        activity.tabFlag = Cons.TAB_MOBILE_NETWORK;
        activity.llNavigation.setVisibility(View.GONE);
        activity.rlBanner.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_wifiExtender_back, // 返回
                     R.id.tv_wifiExtender_scan, // 扫描
                     R.id.iv_wifiExtender_panel_socket // 开关
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifiExtender_back: // 返回setting界面
                activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SETTING]);
                break;
            case R.id.tv_wifiExtender_scan:
                // TODO: 2018/2/6 0006 
                break;
            case R.id.iv_wifiExtender_panel_socket:
                // TODO: 2018/2/6 0006 
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        // 返回setting界面
        activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SETTING]);
        return true;
    }
}
