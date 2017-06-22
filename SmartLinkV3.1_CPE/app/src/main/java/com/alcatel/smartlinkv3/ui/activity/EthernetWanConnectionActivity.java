package com.alcatel.smartlinkv3.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class EthernetWanConnectionActivity extends BaseActivityWithBack implements OnClickListener {

    private ImageView mSelectedPppopImg;
    private ImageView mSelectedDhcpImg;
    private ImageView mSelectedStaticIpImg;
    private LinearLayout mPppopLayout;
    private TextView mDhcpTextview;
    private LinearLayout mStaticIpLayout;

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
