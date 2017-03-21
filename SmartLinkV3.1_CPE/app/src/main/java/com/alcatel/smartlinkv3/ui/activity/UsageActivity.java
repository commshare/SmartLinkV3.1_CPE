package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;

/**
 * Created by chao.qu on 2017/1/5.
 */

public class UsageActivity extends BaseActivity implements View.OnClickListener {

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

    private UsageActivity.ViewUsageBroadcastReceiver m_viewUsageMsgReceiver;
    private ImageButton mbackBtn;
    private Button mMoreBtn;

    public UsageActivity() {
        this.mContext = this;
    }

    private class ViewUsageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
                    updateUI();
                }
            }else if (intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {

                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                String msgRes;
                if (nResult == 0 && strErrorCode.length() == 0) {
                    msgRes = getString(R.string.usage_clear_history_success);
                    Toast.makeText(mContext, msgRes,Toast.LENGTH_SHORT).show();
                }else{
                    msgRes = mContext.getString(R.string.usage_clear_history_fail);
                    Toast.makeText(mContext, msgRes,Toast.LENGTH_SHORT).show();
                }
            }else if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {

                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    updateUI();
                }else{
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_usage);
        init();
    }

    protected void init() {
        mbackBtn = ((ImageButton) findViewById(R.id.usage_back));
        mbackBtn.setOnClickListener(this);

        mMoreBtn = ((Button) findViewById(R.id.usage_more));
        mMoreBtn.setOnClickListener(this);

        m_homewarn = (ImageView) findViewById(R.id.home_warn);
        m_homedata = (TextView)findViewById(R.id.home_data);
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
        switch (view.getId()){
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

    private void moreBtnClick(){
        MorePopWindow morePopWindow = new MorePopWindow(this);
        morePopWindow.showPopupWindow(mMoreBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_viewUsageMsgReceiver = new ViewUsageBroadcastReceiver();
        registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET));
        registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
        registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));

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

    private void updateUI(){
        int nProgress;
        UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
        UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();

        if(statistic.HMonthlyPlan!=0)
        {
            m_homedataprogressdec.setVisibility(View.GONE);
            m_homedata.setVisibility(View.VISIBLE);
            m_homedata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, m_UsageRecordResult.HUseData)+" of "
                    + CommonUtil.ConvertTrafficToStringFromMB(mContext, statistic.HMonthlyPlan));
            nProgress = (int)(m_UsageRecordResult.HUseData*m_homedataprogress.getMax()/statistic.HMonthlyPlan);
            Log.v("text", "p11111   nProgress="+nProgress);
            if (nProgress > m_homedataprogress.getMax())
                nProgress = m_homedataprogress.getMax();
            m_homedataprogress.setProgress(nProgress);
            if(m_UsageRecordResult.HUseData > statistic.HMonthlyPlan)
            {
                m_homewarn.setVisibility(View.VISIBLE);
            }else
            {
                m_homewarn.setVisibility(View.GONE);
            }
        }else {
            m_homedata.setVisibility(View.GONE);
            m_homedataprogress.setProgress(0);
            m_homedataprogressdec.setVisibility(View.VISIBLE);
            m_homewarn.setVisibility(View.GONE);

        }
        m_homedowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long)m_UsageRecordResult.HCurrUseDL));
        m_homeupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long)m_UsageRecordResult.HCurrUseUL));


        m_roamingdowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long)m_UsageRecordResult.RCurrUseDL));
        m_roamingupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long)m_UsageRecordResult.RCurrUseUL));
        m_roamingdata.setText(CommonUtil.ConvertTrafficToStringFromMB(mContext, (long)m_UsageRecordResult.RoamUseData));


        String durationformat = getResources().getString(R.string.usage_duration);
        Log.v("time", "pccccc CurrConnTimes="+m_UsageRecordResult.CurrConnTimes+"TConnTimes="+m_UsageRecordResult.TConnTimes);
        String strCurrDuration = String.format(durationformat, m_UsageRecordResult.CurrConnTimes/3600, (m_UsageRecordResult.CurrConnTimes%3600)/60);
        m_durationtime.setText(strCurrDuration);
        String strTotalDuration = String.format(durationformat, m_UsageRecordResult.TConnTimes/3600, (m_UsageRecordResult.TConnTimes%3600)/60);
        m_durationtotaltime.setText(strTotalDuration);

        if(m_UsageRecordResult.CurrConnTimes > (statistic.HTimeLimitTimes*60))
        {
            m_durationwarn.setVisibility(View.VISIBLE);
        }else
        {
            m_durationwarn.setVisibility(View.GONE);
        }

    }
}
