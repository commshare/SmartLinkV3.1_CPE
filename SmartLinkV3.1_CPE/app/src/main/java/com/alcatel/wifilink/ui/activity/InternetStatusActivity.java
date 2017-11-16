package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.utils.ActionbarSetting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InternetStatusActivity extends BaseActivityWithBack {

    // ip address
    @BindView(R.id.rl_internet_IPAddress)
    RelativeLayout rlInternetIPAddress;
    @BindView(R.id.tv_internet_title_IPAddress)
    TextView tvInternetTitleIPAddress;
    @BindView(R.id.tv_internet_content_IPAddress)
    TextView tvInternetContentIPAddress;

    // subnet mask
    @BindView(R.id.rl_internet_subnet)
    RelativeLayout rlInternetSubnet;
    @BindView(R.id.tv_internet_title_SubnetMask)
    TextView tvInternetTitleSubnetMask;
    @BindView(R.id.tv_internet_content_subnetmask)
    TextView tvInternetContentSubnetmask;

    // default gateway
    @BindView(R.id.rl_internet_gateway)
    RelativeLayout rlInternetGateway;
    @BindView(R.id.tv_internet_title_gateway)
    TextView tvInternetTitleGateway;
    @BindView(R.id.tv_internet_content_gateway)
    TextView tvInternetContentGateway;

    // perferred dns
    @BindView(R.id.rl_internet_dns)
    RelativeLayout rlInternetDns;
    @BindView(R.id.tv_internet_title_dns)
    TextView tvInternetTitleDns;
    @BindView(R.id.tv_internet_content_dns)
    TextView tvInternetContentDns;

    // secondary dns
    @BindView(R.id.rl_internet_secondDns)
    RelativeLayout rlInternetSecondDns;
    @BindView(R.id.tv_internet_title_secondDns)
    TextView tvInternetTitleSecondDns;
    @BindView(R.id.tv_internet_content_secondDns)
    TextView tvInternetContentSecondDns;

    // renew button
    @BindView(R.id.rv_internet_renew)
    RippleView rvInternetRenew;

    // to connect type mode ui
    @BindView(R.id.tv_internet_ethernetWanConnect)
    TextView tvInternetEthernetWanConnect;

    // action bar back button
    private ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_status);
        ButterKnife.bind(this);
        // init action bar
        initActionBar();
        // init the data
        initWanData();
    }


    /**
     * init actionbar
     */
    private void initActionBar() {
        new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                ib_back = (ImageButton) view.findViewById(R.id.ib_internet_back);
                ib_back.setOnClickListener(v -> finish());
            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbar_internet);
    }

    @OnClick({R.id.rv_internet_renew, R.id.tv_internet_ethernetWanConnect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // renew button
            case R.id.rv_internet_renew:
                getWanInfo();
                break;

            // to connect type ui [pppoe | static | dhcp]
            case R.id.tv_internet_ethernetWanConnect:
                CA.toActivity(this, EthernetWanConnectionActivity.class, false, false, false, 0);
                break;
        }
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * A1.初始化获取WAN口数据
     */
    private void initWanData() {
        getWanInfo();
    }

    /**
     * A2.重新获取wan口信息
     */
    private void getWanInfo() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                runOnUiThread(() -> {
                    String defaultWan = "0.0.0.0";

                    String ipAddress = result.getIpAddress();
                    tvInternetContentIPAddress.setText(TextUtils.isEmpty(ipAddress) ? defaultWan : ipAddress);

                    String subNetMask = result.getSubNetMask();
                    tvInternetContentSubnetmask.setText(TextUtils.isEmpty(ipAddress) ? defaultWan : subNetMask);

                    String gateway = result.getGateway();
                    tvInternetContentGateway.setText(TextUtils.isEmpty(ipAddress) ? defaultWan : gateway);

                    String primaryDNS = result.getPrimaryDNS();
                    tvInternetContentDns.setText(TextUtils.isEmpty(ipAddress) ? defaultWan : primaryDNS);

                    String secondaryDNS = result.getSecondaryDNS();
                    tvInternetContentSecondDns.setText(TextUtils.isEmpty(ipAddress) ? defaultWan : secondaryDNS);
                });
            }
        });
    }
}
