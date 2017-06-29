package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.model.Usage.UsageRecord;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.utils.ActionbarSetting;
import com.alcatel.smartlinkv3.utils.DataUtils;

import static com.alcatel.smartlinkv3.R.id.tv_network_settings;


public class UsageActivity extends BaseActivityWithBack implements View.OnClickListener {

    private Context mContext;
    private TextView mHomeData;
    private TextView mTvHomeTime;
    private TextView mRoamingData;
    private TextView mTvRoamingTime;
    private Button mBtReset;
    private TextView mTvNetworkSettings;

    private ImageButton mbackBtn;
    private Button mMoreBtn;
    private TimerHelper timerHelper;
    private boolean mIsLimited;

    public UsageActivity() {
        this.mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_usage);
        // init action bar
        initActionBar();
        // init layout ui
        initView();
        // init status
        updateUI();
        // init Timer
        initTimer();

    }

    /**
     * 接收定时器接口
     */
    private void initTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                updateUI();
            }
        };
        timerHelper.start(5000);
    }

    /**
     * 初始化Actionbar
     */
    private void initActionBar() {
        // initView action bar
        new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                mbackBtn = (ImageButton) view.findViewById(R.id.usage_back);
                mMoreBtn = (Button) view.findViewById(R.id.usage_more);
                mbackBtn.setOnClickListener(UsageActivity.this);
                mMoreBtn.setVisibility(View.GONE);
            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbarusage);
    }

    protected void initView() {
        mHomeData = (TextView) findViewById(R.id.home_data);
        mTvHomeTime = (TextView) findViewById(R.id.tv_home_time);
        mRoamingData = (TextView) findViewById(R.id.roaming_data);
        mTvRoamingTime = (TextView) findViewById(R.id.tv_roaming_time);
        mBtReset = (Button) findViewById(R.id.bt_reset);
        mTvNetworkSettings = (TextView) findViewById(tv_network_settings);
        mBtReset.setOnClickListener(this);
        mTvNetworkSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.usage_back:
                finish();
                break;
            case R.id.bt_reset:
                resetDataUsage();
                break;
            case tv_network_settings:
                goToMobileNetworkSettingPage();
                break;
            default:
                break;
        }
    }

    private void resetDataUsage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.reset_monthly_data_usage_statistics);
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                API.get().setUsageRecordClear(DataUtils.getCurrent(), new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        updateUI();
                    }
                });
            }
        });
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /* update ui */
    private void updateUI() {
        getUsageRecord();
    }

    private void goToMobileNetworkSettingPage() {
        // to setting network activity
        Intent intent = new Intent(this, SettingNetworkActivity.class);
        startActivity(intent);
    }

    /* **** getUsageRecord **** */
    private void getUsageRecord() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord result) {
                int monthlyPlan = result.getMonthlyPlan();
                if (monthlyPlan == 0) {
                    mIsLimited = true;
                } else {
                    mIsLimited = false;
                }
                updateUsageRecord(result);
            }
        });
    }

    //
    private void updateUsageRecord(UsageRecord result) {
        int useData = result.getHUseData();
        if (mIsLimited) {
            mHomeData.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, useData) + " / " + CommonUtil.ConvertTrafficToStringFromMB(mContext, result.getMonthlyPlan()));

        } else {
            mHomeData.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, useData));

        }
        if (result.getMonthlyPlan() <= 0) {// user have no set plan
            return;
        }
        mRoamingData.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) result.getRoamUseData()));
        String durationformat = getResources().getString(R.string.usage_duration);

        int connectTime = result.getCurrConnTimes();
        int totalTime = result.getTConnTimes();
        String strCurrDuration = String.format(durationformat, connectTime / 3600, (connectTime % 3600) / 60);
        mTvHomeTime.setText(strCurrDuration);
        String strTotalDuration = String.format(durationformat, totalTime / 3600, (totalTime % 3600) / 60);
        mTvRoamingTime.setText(strTotalDuration);
    }
}

