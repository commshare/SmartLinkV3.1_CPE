package com.alcatel.smartlinkv3.ui.view;

import java.util.ArrayList;
import java.util.Map;

import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ViewUsage extends BaseViewImpl {
	private static final String TAG = ViewUsage.class.getSimpleName();
	
	/*home_panel  start*/
	private ImageView m_homewarn;
	private TextView m_homedata;
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
				if (nResult == 0 && strErrorCode.length() == 0) {
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
	
	private void updateUI(){
		int nProgress =0;
		UsageRecordResult m_UsageRecordResult = BusinessMannager.getInstance().getUsageRecord();
		
		m_homedowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HCurrUseDL));
		m_homeupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HCurrUseUL));
		m_homedata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.HUseData)+" of "
					+ CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.MaxUsageData));
		if(m_UsageRecordResult.HUseData > m_UsageRecordResult.MaxUsageData)
		{
			m_homewarn.setVisibility(View.VISIBLE);
		}else
		{
			m_homewarn.setVisibility(View.GONE);
		}
	    if(m_UsageRecordResult.MaxUsageData!=0)
	    {
	    	nProgress = (int) (m_UsageRecordResult.HUseData*m_homedataprogress.getMax() / m_UsageRecordResult.MaxUsageData);
	    	if (nProgress > m_homedataprogress.getMax())
				nProgress = m_homedataprogress.getMax();
	    	m_homedataprogress.setProgress(nProgress);
	    }else {
	    	m_homedataprogress.setProgress(0);
		}
	    
	    
	    m_roamingdowndata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RCurrUseDL));
	    m_roamingupdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RCurrUseUL));
	    m_roamingdata.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.RoamUseData));

		
	    m_durationtime.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.CurrConnTimes));
		m_durationtotaltime.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, (long)m_UsageRecordResult.TConnTimes));
		if(m_UsageRecordResult.HUseData > m_UsageRecordResult.MaxUsageData)
		{
			m_durationwarn.setVisibility(View.VISIBLE);
		}else
		{
			m_durationwarn.setVisibility(View.GONE);
		}
		
	}

}

