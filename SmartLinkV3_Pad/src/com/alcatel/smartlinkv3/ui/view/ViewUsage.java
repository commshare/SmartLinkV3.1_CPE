package com.alcatel.smartlinkv3.ui.view;

import java.util.ArrayList;
import java.util.Map;

import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.ActivityNewSms;
import com.alcatel.smartlinkv3.ui.activity.UsageSettingActivity;
import com.alcatel.smartlinkv3.ui.activity.WpsMainActivity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ViewUsage extends BaseViewImpl implements OnClickListener{
	private static final String TAG = ViewUsage.class.getSimpleName();
	
	/*home_panel  start*/
	private ImageView m_homewarn;
	private TextView m_homedata;
	private TextView m_homedataprogressdec;
	
	private Button m_homeSetMonthlyBtn;
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
				String msgRes = null;
				if (nResult == 0 && strErrorCode.length() == 0) {
					msgRes = m_context.getString(R.string.usage_clear_history_success);
					Toast.makeText(m_context, msgRes,Toast.LENGTH_SHORT).show();
				}else{
					msgRes = m_context.getString(R.string.usage_clear_history_fail);
					Toast.makeText(m_context, msgRes,Toast.LENGTH_SHORT).show();
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


	public ViewUsage(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_usage,null);
		
		m_homewarn = (ImageView) m_view.findViewById(R.id.home_warn);
		m_homedata = (TextView) m_view.findViewById(R.id.home_data);
		m_homedataprogress = (ProgressBar) m_view.findViewById(R.id.usage_status_progress);
		m_homedowndata = (TextView) m_view.findViewById(R.id.home_data_down_status);
		m_homeupdata = (TextView) m_view.findViewById(R.id.home_data_up_status);
		m_homeSetMonthlyBtn = (Button) m_view.findViewById(R.id.home_set_monthly_data_plan);
		m_homeSetMonthlyBtn.setOnClickListener(this);
		m_homedataprogressdec = (TextView) m_view.findViewById(R.id.usage_status_describtion);
		
		m_roamingwarn = (ImageView) m_view.findViewById(R.id.roaming_warn);
		m_roamingdata = (TextView) m_view.findViewById(R.id.roaming_data);
		m_roamingdowndata = (TextView) m_view.findViewById(R.id.roaming_data_down_status);
		m_roamingupdata = (TextView) m_view.findViewById(R.id.roaming_data_up_status);
		
		m_durationwarn = (ImageView) m_view.findViewById(R.id.duration_warn);
		m_durationtotaltime = (TextView) m_view.findViewById(R.id.duration_total_time);
		m_durationtime = (TextView) m_view.findViewById(R.id.duration_time_status);

	}

	@Override
	public void onResume() {
		m_viewUsageMsgReceiver = new ViewUsageBroadcastReceiver();
		m_context.registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET));
		m_context.registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
		m_context.registerReceiver(m_viewUsageMsgReceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));

		updateUI();
	}

	@Override
	public void onPause() {
		try {
			m_context.unregisterReceiver(m_viewUsageMsgReceiver);
		} catch (Exception e) {

		}

	}

	@Override
	public void onDestroy() {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_set_monthly_data_plan:
			go2UsageSettingActivity();
			break;
		default:
			break;
		}
	}
	
	private void go2UsageSettingActivity(){	
		Intent intent = new Intent();
		intent.setClass(m_context, UsageSettingActivity.class);	
		this.m_context.startActivity(intent);
	
	}
	
	private void updateUI(){
		int nProgress =0;
		UsageRecordResult m_UsageRecordResult = BusinessMannager.getInstance().getUsageRecord();
		UsageSettingModel statistic = BusinessMannager.getInstance().getUsageSettings();
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();

		
		if(statistic.HMonthlyPlan!=0)
	    {
			m_homeSetMonthlyBtn.setVisibility(View.GONE);
			m_homedataprogressdec.setVisibility(View.GONE);
			m_homedata.setVisibility(View.VISIBLE);
			m_homedata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HUseData)+" of "
					+ CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)statistic.HMonthlyPlan));
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
	    	m_homeSetMonthlyBtn.setVisibility(View.VISIBLE);
	    	if(sim.m_SIMState != SIMState.Accessable) {
	    		m_homeSetMonthlyBtn.setEnabled(false);
	    	}else {
	    		m_homeSetMonthlyBtn.setEnabled(true);
			}
	    	m_homedataprogress.setProgress(0);
	    	m_homedataprogressdec.setVisibility(View.VISIBLE);
	    	m_homewarn.setVisibility(View.GONE);
	    	
		}
		m_homedowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HCurrUseDL));
		m_homeupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HCurrUseUL));

	    
	    m_roamingdowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RCurrUseDL));
	    m_roamingupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RCurrUseUL));
	    m_roamingdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RoamUseData));

		
	    String durationformat = this.getView().getResources().getString(R.string.usage_duration);
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

