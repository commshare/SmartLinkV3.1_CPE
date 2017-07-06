package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;

public class EthernetWanConnectionActivity extends BaseActivityWithBack implements OnClickListener {
    private static final String TAG = "EthernetWanConnectionActivity";

    private ImageView mSelectedPppopImg;
    private ImageView mSelectedDhcpImg;
    private ImageView mSelectedStaticIpImg;
    private LinearLayout mPppopLayout;
    private TextView mDhcpTextview;
    private LinearLayout mStaticIpLayout;

    //pppoe
    private EditText mPppoeAccount;
    private EditText mPppoePassword;
    private TextView mPppoeMtu;

    //Static Ip
    private EditText mStaticIpAddress;
    private EditText mStaticIpSubnetMask;
    private EditText mStaticIpDefaultGateway;
    private EditText mStaticIpPreferredDns;
    private EditText mStaticIpSecondaryDns;
    private TextView mStaticIpMtu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethernet_wan_connection);
        setTitle("Ethernet WAN connection");

        mSelectedPppopImg = (ImageView) findViewById(R.id.pppoe_selected_img);
        mSelectedDhcpImg = (ImageView) findViewById(R.id.dhcp_selected_img);
        mSelectedStaticIpImg = (ImageView) findViewById(R.id.static_ip_selected_img);
        mPppopLayout = (LinearLayout) findViewById(R.id.linearlayout_pppoe);
        mDhcpTextview = (TextView) findViewById(R.id.textview_dhcp);
        mStaticIpLayout = (LinearLayout) findViewById(R.id.linearlayout_static_ip);

        findViewById(R.id.ethernet_wan_connection_pppoe).setOnClickListener(this);
        findViewById(R.id.ethernet_wan_connection_dhcp).setOnClickListener(this);
        findViewById(R.id.ethernet_wan_connection_static_ip).setOnClickListener(this);

        //pppoe
        mPppoeAccount = (EditText) findViewById(R.id.pppoe_account);
        mPppoePassword = (EditText) findViewById(R.id.pppoe_password);
        mPppoeMtu = (TextView) findViewById(R.id.pppoe_mtu);

        //static ip
        mStaticIpAddress = (EditText) findViewById(R.id.static_ip_address);
        mStaticIpSubnetMask = (EditText) findViewById(R.id.static_ip_subnet_mask);
        mStaticIpDefaultGateway = (EditText) findViewById(R.id.static_ip_default_gateway);
        mStaticIpPreferredDns = (EditText) findViewById(R.id.static_ip_preferred_dns);
        mStaticIpSecondaryDns = (EditText) findViewById(R.id.static_ip_secondary_dns);
        mStaticIpMtu = (TextView) findViewById(R.id.static_ip_mtu);
        getWanSettings();
    }

    private void getWanSettings() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>(){
            @Override
            protected void onSuccess(WanSettingsResult result) {
                Log.v(TAG,"account"+result);
                mPppoeAccount.setText(result.getAccount().toString());
                mPppoePassword.setText(result.getPassword().toString());
                mPppoeMtu.setText(result.getPppoeMtu()+"");
                mStaticIpAddress.setText(result.getIpAddress().toString());
                mStaticIpSubnetMask.setText(result.getSubNetMask().toString());
                mStaticIpDefaultGateway.setText(result.getGateway().toString());
                mStaticIpPreferredDns.setText(result.getPrimaryDNS().toString());
                mStaticIpSecondaryDns.setText(result.getSecondaryDNS().toString());
                mStaticIpMtu.setText(result.getMtu()+"");
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getWanSettings error");
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int nID = v.getId();
        switch (nID) {
            case R.id.ethernet_wan_connection_pppoe:
                mSelectedPppopImg.setVisibility(View.VISIBLE);
                mSelectedDhcpImg.setVisibility(View.GONE);
                mSelectedStaticIpImg.setVisibility(View.GONE);
                mPppopLayout.setVisibility(View.VISIBLE);
                mDhcpTextview.setVisibility(View.GONE);
                mStaticIpLayout.setVisibility(View.GONE);
                break;
            case R.id.ethernet_wan_connection_dhcp:
                mSelectedPppopImg.setVisibility(View.GONE);
                mSelectedDhcpImg.setVisibility(View.VISIBLE);
                mSelectedStaticIpImg.setVisibility(View.GONE);
                mPppopLayout.setVisibility(View.GONE);
                mDhcpTextview.setVisibility(View.VISIBLE);
                mStaticIpLayout.setVisibility(View.GONE);
                break;
            case R.id.ethernet_wan_connection_static_ip:
                mSelectedPppopImg.setVisibility(View.GONE);
                mSelectedDhcpImg.setVisibility(View.GONE);
                mSelectedStaticIpImg.setVisibility(View.VISIBLE);
                mPppopLayout.setVisibility(View.GONE);
                mDhcpTextview.setVisibility(View.GONE);
                mStaticIpLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}
