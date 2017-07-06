package com.alcatel.wifilink.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.system.WanSetting;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;

public class AboutActivity extends BaseActivityWithBack implements View.OnClickListener {
    private final static String TAG = "AboutActivity";
    private TextView mDeviceNameTxt;
    private TextView mImeiTxt;
    private TextView mMacAddressTxt;
    private TextView mManagementIpTxt;
    private TextView mSubnetMaskTxt;
    private TextView mWebManager;
    private TextView mQuickGuide;
    private ImageView mIvPoint;
    private TextView mAppVersionTxt;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //control title bar
        mDeviceNameTxt = (TextView) findViewById(R.id.device_name_txt);
        mImeiTxt = (TextView) findViewById(R.id.imei_txt);
        mMacAddressTxt = (TextView) findViewById(R.id.mac_address_txt);
        mManagementIpTxt = (TextView) findViewById(R.id.management_ip_txt);
        mSubnetMaskTxt = (TextView) findViewById(R.id.subnet_mask_txt);
        mWebManager = (TextView) findViewById(R.id.web_manager);
        mQuickGuide = (TextView) findViewById(R.id.quick_guide);
        mIvPoint = (ImageView) findViewById(R.id.iv_point);
        mAppVersionTxt = (TextView) findViewById(R.id.app_version_txt);
        mWebManager.setOnClickListener(this);
        mQuickGuide.setOnClickListener(this);
        setTitle(R.string.setting_title_about);
        getDataFromNet();
    }

    private void getDataFromNet() {
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(SystemInfo result) {
                dismissLoadingDialog();
                mDeviceNameTxt.setText(result.getDeviceName());
                mImeiTxt.setText(result.getIMEI());
                mMacAddressTxt.setText(result.getMacAddress());
                mAppVersionTxt.setText(result.getAppVersion());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
            }
        });
        API.get().getWanSeting(new MySubscriber<WanSetting>() {
            @Override
            protected void onSuccess(WanSetting result) {
                Log.d(TAG, "getWanSeting: " + result.toString());
                mManagementIpTxt.setText(result.getIpAddress());
                mSubnetMaskTxt.setText(result.getSubNetMask());
            }
        });

    }

    private void showLoadingDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
    }

    private void dismissLoadingDialog() {
        mProgressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.web_manager:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://192.168.1.1");
                intent.setData(content_url);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
