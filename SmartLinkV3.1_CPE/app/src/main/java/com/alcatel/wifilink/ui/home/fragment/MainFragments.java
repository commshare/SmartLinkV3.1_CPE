package com.alcatel.wifilink.ui.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.view.CircleProgress;
import com.alcatel.wifilink.ui.view.DynamicWave;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by qianli.ma on 2017/7/19.
 */

@Deprecated
public class MainFragments extends Fragment {

    @BindView(R.id.home_circleProgress)
    CircleProgress homeCircleProgress;
    @BindView(R.id.connected_button)
    WaveLoadingView connectedButton;
    @BindView(R.id.connected_layout)
    RelativeLayout connectedLayout;
    @BindView(R.id.connect_button)
    Button connectButton;
    @BindView(R.id.connect_layout)
    RelativeLayout connectLayout;
    @BindView(R.id.rl_connectlogo)
    RelativeLayout rlConnectlogo;
    @BindView(R.id.connect_network)
    TextView connectNetwork;
    @BindView(R.id.dw_main)
    DynamicWave dwMain;
    @BindView(R.id.connct_signal)
    ImageView connctSignal;
    @BindView(R.id.left_signal)
    RelativeLayout leftSignal;
    @BindView(R.id.connct_network_type)
    TextView connctNetworkType;
    @BindView(R.id.connct_network_label)
    TextView connctNetworkLabel;
    @BindView(R.id.sigel_panel)
    RelativeLayout sigelPanel;
    @BindView(R.id.access_status)
    ImageView accessStatus;
    @BindView(R.id.access_num_label)
    TextView accessNumLabel;
    @BindView(R.id.access_label)
    TextView accessLabel;
    @BindView(R.id.access_num_layout)
    RelativeLayout accessNumLayout;
    @BindView(R.id.rl_main_wait)
    RelativeLayout rlMainWait;
    Unbinder unbinder;
    private View inflate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_home_mains, null);
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
