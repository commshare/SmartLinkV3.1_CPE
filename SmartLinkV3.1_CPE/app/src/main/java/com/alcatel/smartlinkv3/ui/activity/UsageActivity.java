package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.model.Usage.UsageRecord;
import com.alcatel.smartlinkv3.model.Usage.UsageSetting;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;
import com.alcatel.smartlinkv3.ui.home.helper.main.ApiEngine;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;

import static com.alcatel.smartlinkv3.ui.activity.ActivityDeviceManager.disableABCShowHideAnimation;


public class UsageActivity extends BaseActivityWithBack implements View.OnClickListener {

    private Context mContext;

    /*home_panel  start*/
    private ImageView m_homewarn;
    private TextView m_homedata;
    private TextView m_homedataprogressdec;

    private ProgressBar m_homedataprogress;
    private TextView m_homedowndata;
    private TextView m_homeupdata;
                /*home_panel  end*/

    /*roaming_panel  start*/
    private ImageView m_roamingwarn;
    private TextView m_roamingdata;
    private TextView m_roamingdowndata;
    private TextView m_roamingupdata;
                /*roaming_panel  end*/

    /*duration_panel  start*/
    private ImageView m_durationwarn;
    private TextView m_durationtotaltime;
    private TextView m_durationtime;
                /*duration_panel  end*/

    private ViewUsageBroadcastReceiver m_viewUsageMsgReceiver;
    private ImageButton mbackBtn;
    private Button mMoreBtn;
    private TimerHelper timerHelper;
    private UsageRecord home_usageRecord;
    private UsageSetting home_usageSetting;

    public UsageActivity() {
        this.mContext = this;
    }

    private class ViewUsageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();
            if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET)) {
                if (ok) {
                    updateUI();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {
                String msgRes;
                if (ok) {
                    msgRes = getString(R.string.usage_clear_history_success);
                    Toast.makeText(mContext, msgRes, Toast.LENGTH_SHORT).show();
                } else {
                    msgRes = mContext.getString(R.string.usage_clear_history_fail);
                    Toast.makeText(mContext, msgRes, Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
                if (ok) {
                    updateUI();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_usage);
        initStatus();
        initActionBar();
        initView();
        initTimer();

    }

    /**
     * 接收定时器接口
     */
    private void initTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                ApiEngine.getUsageRecord();
                ApiEngine.getUsageSetting();
                runOnUiThread(() -> updateUI());
                System.out.println();
            }
        };
        timerHelper.start(5000);
    }

    /**
     * 初始化状态
     */
    private void initStatus() {
        ApiEngine.getUsageRecord();
        ApiEngine.getUsageSetting();

        home_usageRecord = ApiEngine.home_usageRecord;
        home_usageSetting = ApiEngine.home_usageSetting;

    }

    /**
     * 初始化Actionbar
     */
    private void initActionBar() {
        // initView action bar
        ActionBar supportActionBar = getSupportActionBar();
        View inflate = View.inflate(this, R.layout.actionbarusage, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
        supportActionBar.setCustomView(inflate, lp);
        disableABCShowHideAnimation(supportActionBar);
        supportActionBar.setDisplayShowHomeEnabled(false);
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar.show();
        findActionbarView(supportActionBar);
    }


    /**
     * 查找控件
     *
     * @param supportActionBar
     */
    private void findActionbarView(ActionBar supportActionBar) {
        View customView = supportActionBar.getCustomView();
        mbackBtn = (ImageButton) customView.findViewById(R.id.usage_back);
        mMoreBtn = (Button) customView.findViewById(R.id.usage_more);
        mbackBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
    }

    protected void initView() {
        m_homewarn = (ImageView) findViewById(R.id.home_warn);
        m_homedata = (TextView) findViewById(R.id.home_data);
        m_homedataprogress = (ProgressBar) findViewById(R.id.usage_status_progress);
        m_homedowndata = (TextView) findViewById(R.id.home_data_down_status);
        m_homeupdata = (TextView) findViewById(R.id.home_data_up_status);
        m_homedataprogressdec = (TextView) findViewById(R.id.usage_status_describtion);
        m_roamingwarn = (ImageView) findViewById(R.id.roaming_warn);
        m_roamingdata = (TextView) findViewById(R.id.roaming_data);
        m_roamingdowndata = (TextView) findViewById(R.id.roaming_data_down_status);
        m_roamingupdata = (TextView) findViewById(R.id.roaming_data_up_status);

        m_durationwarn = (ImageView) findViewById(R.id.duration_warn);
        m_durationtotaltime = (TextView) findViewById(R.id.duration_total_time);
        m_durationtime = (TextView) findViewById(R.id.duration_time_status);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.usage_back:
                finish();
                break;
            case R.id.usage_more:
                moreBtnClick();
                break;
            default:
                break;
        }
    }

    private void moreBtnClick() {
        MorePopWindow morePopWindow = new MorePopWindow(this);
        morePopWindow.showPopupWindow(mMoreBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // m_viewUsageMsgReceiver = new ViewUsageBroadcastReceiver();
        // registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET));
        // registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
        // registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));

        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(m_viewUsageMsgReceiver);
        } catch (Exception e) {

        }
    }

    private void updateUI() {
        int nProgress;
        // TOAT: usagerecord
        // UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
        //UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();

        if (home_usageSetting != null & home_usageSetting.getMonthlyPlan() != 0) {
            m_homedataprogressdec.setVisibility(View.GONE);
            m_homedata.setVisibility(View.VISIBLE);
            int useData = home_usageRecord != null ? home_usageRecord.getHUseData() : 0;
            m_homedata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, useData) + " of " + CommonUtil.ConvertTrafficToStringFromMB(mContext, home_usageSetting.getMonthlyPlan()));
            nProgress = (int) (useData * m_homedataprogress.getMax() / home_usageSetting.getMonthlyPlan());
            Log.v("text", "p11111   nProgress=" + nProgress);
            if (nProgress > m_homedataprogress.getMax())
                nProgress = m_homedataprogress.getMax();
            m_homedataprogress.setProgress(nProgress);
            if (useData > home_usageSetting.getMonthlyPlan()) {
                m_homewarn.setVisibility(View.VISIBLE);
            } else {
                m_homewarn.setVisibility(View.GONE);
            }
        } else {
            m_homedata.setVisibility(View.GONE);
            m_homedataprogress.setProgress(0);
            m_homedataprogressdec.setVisibility(View.VISIBLE);
            m_homewarn.setVisibility(View.GONE);

        }

        int traffic_HD = home_usageRecord == null ? 0 : home_usageRecord.getHCurrUseDL();
        int traffic_HU = home_usageRecord == null ? 0 : home_usageRecord.getHCurrUseUL();
        m_homedowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_HD));
        m_homeupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_HU));


        int traffic_RD = home_usageRecord == null ? 0 : home_usageRecord.getRCurrUseDL();
        int traffic_RU = home_usageRecord == null ? 0 : home_usageRecord.getRCurrUseUL();
        int RoamUseData = home_usageRecord == null ? 0 : home_usageRecord.getRoamUseData();
        m_roamingdowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_RD));
        m_roamingupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_RU));
        m_roamingdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) RoamUseData));


        String durationformat = getResources().getString(R.string.usage_duration);

        int connectTime = home_usageRecord == null ? 0 : home_usageRecord.getCurrConnTimes();
        int totalTime = home_usageRecord == null ? 0 : home_usageRecord.getTConnTimes();
        Log.v("time", "pccccc CurrConnTimes=" + connectTime + "TConnTimes=" + totalTime);
        String strCurrDuration = String.format(durationformat, connectTime / 3600, (connectTime % 3600) / 60);
        m_durationtime.setText(strCurrDuration);
        String strTotalDuration = String.format(durationformat, totalTime / 3600, (totalTime % 3600) / 60);
        m_durationtotaltime.setText(strTotalDuration);

        int HTimeLimitTimes = home_usageSetting == null ? 0 : home_usageSetting.getTimeLimitTimes();
        if (connectTime > (HTimeLimitTimes * 60)) {
            m_durationwarn.setVisibility(View.VISIBLE);
        } else {
            m_durationwarn.setVisibility(View.GONE);
        }

    }
}
