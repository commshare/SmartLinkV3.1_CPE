package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.utils.ActionbarSetting;
import com.alcatel.smartlinkv3.utils.DataUtils;


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

    private ImageButton mbackBtn;
    private Button mMoreBtn;
    private TimerHelper timerHelper;
    private int timeLimitTimes = 0;
    int nProgress;

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
                mMoreBtn.setOnClickListener(UsageActivity.this);
            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbarusage);
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
        // when user click this button & request the clear action then refresh ui
        morePopWindow.setOnClearUsageRecord(() -> updateUI());
        // clear action
        morePopWindow.showPopupWindow(mMoreBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /* update ui */
    private void updateUI() {
        // get usage setting
        getUsageSetting();

        // get usage record
        getUsageRecord();
    }

    /* **** getUsageSetting **** */
    private void getUsageSetting() {
        API.get().getUsageSetting(new MySubscriber<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting result) {
                // set ui by result
                updateUsageSetting(result);
            }
        });
    }

    /* **** getUsageRecord **** */
    private void getUsageRecord() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord result) {
                updateUsageRecord(result);
            }
        });
    }


    /*  */
    public void updateUsageSetting(UsageSetting result) {
        timeLimitTimes = result.getTimeLimitTimes();
        if (result.getMonthlyPlan() != 0) {
            m_homedataprogressdec.setVisibility(View.GONE);
            m_homedata.setVisibility(View.VISIBLE);
        } else {
            m_homedata.setVisibility(View.GONE);
            m_homedataprogress.setProgress(0);
            m_homedataprogressdec.setVisibility(View.VISIBLE);
            m_homewarn.setVisibility(View.GONE);
        }
    }

    private void updateUsageRecord(UsageRecord result) {
        int useData = result.getHUseData();
        m_homedata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, useData) + " of " + CommonUtil.ConvertTrafficToStringFromMB(mContext, result.getMonthlyPlan()));
        if (result.getMonthlyPlan() <= 0) {// user have no set plan
            return;
        }
        nProgress = useData * m_homedataprogress.getMax() / result.getMonthlyPlan();
        if (nProgress > m_homedataprogress.getMax())
            nProgress = m_homedataprogress.getMax();
        m_homedataprogress.setProgress(nProgress);
        if (useData > result.getMonthlyPlan()) {
            m_homewarn.setVisibility(View.VISIBLE);
        } else {
            m_homewarn.setVisibility(View.GONE);
        }
        int traffic_HD = result.getHCurrUseDL();
        int traffic_HU = result.getHCurrUseUL();
        m_homedowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_HD));
        m_homeupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_HU));


        int traffic_RD = result.getRCurrUseDL();
        int traffic_RU = result.getRCurrUseUL();
        int RoamUseData = result.getRoamUseData();
        m_roamingdowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_RD));
        m_roamingupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) traffic_RU));
        m_roamingdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long) RoamUseData));


        String durationformat = getResources().getString(R.string.usage_duration);

        int connectTime = result.getCurrConnTimes();
        int totalTime = result.getTConnTimes();
        String strCurrDuration = String.format(durationformat, connectTime / 3600, (connectTime % 3600) / 60);
        m_durationtime.setText(strCurrDuration);
        String strTotalDuration = String.format(durationformat, totalTime / 3600, (totalTime % 3600) / 60);
        m_durationtotaltime.setText(strTotalDuration);

        int HTimeLimitTimes = timeLimitTimes;
        if (connectTime > (HTimeLimitTimes * 60)) {
            m_durationwarn.setVisibility(View.VISIBLE);
        } else {
            m_durationwarn.setVisibility(View.GONE);
        }
    }
}

