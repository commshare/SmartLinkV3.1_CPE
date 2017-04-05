package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageHistory;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class StatisticsManager extends BaseManager {
	private UsageSettingModel m_usageSettings = new UsageSettingModel();
	private UsageRecordResult m_usageRecord = new UsageRecordResult();
	private Timer m_rollTimer = new Timer();
	private Timer m_getUsageHistoryRollTimer = new Timer();
	
	private GetUsageSettingsTask m_getUsageSettingsTask = null;
	private GetUsageHistoryTask m_getUsageHistoryTask = null;
	
	public static String IS_CHANGED = "com.alcatel.cpe.business.statistics.ischanged";
	
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_usageSettings.clear();
		m_usageRecord.clear();
	} 
	@Override
	protected void stopRollTimer() {
		if(m_getUsageSettingsTask != null) {
			m_getUsageSettingsTask.cancel();
			m_getUsageSettingsTask = null;
		}
		//m_rollTimer.cancel();
		//m_rollTimer.purge();
		//m_rollTimer = new Timer();
		//m_getUsageHistoryRollTimer.cancel();
		//m_getUsageHistoryRollTimer.purge();
		//m_getUsageHistoryRollTimer = new Timer();
		if(m_getUsageHistoryTask != null) {
			m_getUsageHistoryTask.cancel();
			m_getUsageHistoryTask = null;
		}
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
		BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
		Boolean ok = response != null && response.isOk();

		if(MessageUti.CPE_WIFI_CONNECT_CHANGE.equals(action)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {

			}
    	}
		
		if(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
			if (ok) {
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetUsageSettingTask();
				}
			}
    	}
		
		//get usage history when have imsi
		if(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
			if (ok) {
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetUsageHistoryTask();
				}else{
					stopRollTimer();
					m_usageRecord.clear();
					m_usageSettings.clear();
				}
			}
    	}
		
		// when conneted or disconnected to get usage history or billing day changed
		if(MessageUti.WAN_CONNECT_REQUSET.equals(action) ||
				MessageUti.WAN_DISCONNECT_REQUSET.equals(action) ||
				MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET.equals(action) ||
				MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET.equals(action)) {
			if (ok) {
				getUsageHistorySingle();
			}
    	}
		
		if(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET.equals(action)) {
			if(ok) {
				m_usageRecord.clear();
				getUsageHistorySingle();
			}
		}
		
		if(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET.equals(action)) {
			boolean bBillingDayChanged = intent.getBooleanExtra(USAGE_SETTING_BILLING_DAY_CHANGE, false);
			if(ok && bBillingDayChanged) {
					getUsageHistorySingle();
			}
    	}
		
		if(MessageUti.CPE_CHANGED_BILLING_MONTH.equals(action)) {
			getUsageHistorySingle();
		}
	}  
	
	public StatisticsManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		//m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.WAN_CONNECT_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.WAN_DISCONNECT_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.CPE_CHANGED_BILLING_MONTH));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
    }
	
	public UsageSettingModel getUsageSettings() {
		return m_usageSettings;
	}
	
	public UsageRecordResult getUsageRecord() {
		return m_usageRecord;
	}
	
    
    public long GetBillingMonthTotalUsage() {
    	return m_usageSettings.HUsedData;
    	
//    	UsageRecordResult usageList = new UsageRecordResult();
//    	usageList.clone(m_usageRecord);
//    	long total = 0;
//    	total = usageList.MaxUsageData;
//    	//total += m_staSetting.getCalibrationValue();
//    	return total;
    }
	
	//GetUsageSetting ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetUsageSettingTask() {
		//GetUsageSettingsTask getUsageSettingsTask = new GetUsageSettingsTask();
		//m_rollTimer.scheduleAtFixedRate(getUsageSettingsTask, 0, 10 * 1000);
		if(m_getUsageSettingsTask == null) {
			m_getUsageSettingsTask = new GetUsageSettingsTask();
			m_rollTimer.scheduleAtFixedRate(m_getUsageSettingsTask, 0, 10 * 1000);
		}
	}
	
	public static String USAGE_SETTING_BILLING_DAY_CHANGE = "com.alcatel.cpe.business.statistics.billingdaychange";
    public static String USAGE_SETTING_MONTHLY_PLAN_CHANGE = "com.alcatel.cpe.business.statistics.monthlyplanchange";
    public static String USAGE_SETTING_USED_DATA_CHANGE = "com.alcatel.cpe.business.statistics.useddatachange";
	public static String USAGE_SETTING_USAGE_ALERT_CHANGE = "com.alcatel.cpe.business.statistics.usagealertchange";
    public static String USAGE_SETTING_TIME_LIMIT_FLAG_CHANGE = "com.alcatel.cpe.business.statistics.timelimitflagchange";
    public static String USAGE_SETTING_TIME_LIMIT_TIMES_CHANGE = "com.alcatel.cpe.business.statistics.timelimittimeschange";
    public static String USAGE_SETTING_USED_TIMES_CHANGE = "com.alcatel.cpe.business.statistics.usedtimeschange";
    public static String USAGE_SETTING_AUTO_DISCONN_FLAG_CHANGE = "com.alcatel.cpe.business.statistics.autodisconnflagchange";
    
	class GetUsageSettingsTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.GetUsageSettings(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {   
                	boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        			if(bCPEWifiConnected == false) {
        				return;
        			}
        			
        			boolean bBillingDayChange = false;
	            	boolean bHMonthlyPlanChange = false;
	            	boolean bHUsedDataChange = false;
					boolean bAlertValueChange = false;
	            	boolean bHTimeLimitFlagChange = false;
	            	boolean bHTimeLimitTimesChange = false;
	            	boolean bHUsedTimesChange = false;
	            	boolean bHAutoDisconnFlagChange = false;
	            	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		UsageSettingsResult usageSettingResult = response.getModelResult();
                    		UsageSettingModel pre = new UsageSettingModel();
                    		pre.clone(m_usageSettings);
                    		m_usageSettings.setValue(usageSettingResult);
                    		if(pre.HBillingDay != m_usageSettings.HBillingDay)
                    			bBillingDayChange = true;
                    		
                    		if(pre.HMonthlyPlan != m_usageSettings.HMonthlyPlan)
                    			bHMonthlyPlanChange = true;
                    		
                    		if(pre.HUsedData != m_usageSettings.HUsedData)
                    			bHUsedDataChange = true;

							if (pre.HUsageAlertValue != m_usageSettings.HUsageAlertValue)
								bAlertValueChange = true;
                    		
                    		if(pre.HTimeLimitFlag != m_usageSettings.HTimeLimitFlag)
                    			bHTimeLimitFlagChange = true;
                    		
                    		if(pre.HTimeLimitTimes != m_usageSettings.HTimeLimitTimes)
                    			bHTimeLimitTimesChange = true;
                    		
                    		if(pre.HUsedTimes != m_usageSettings.HUsedTimes)
                    			bHUsedTimesChange = true;
                    		
                    		if(pre.HAutoDisconnFlag != m_usageSettings.HAutoDisconnFlag)
                    			bHAutoDisconnFlagChange = true;
                    		
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    if(bBillingDayChange || bHMonthlyPlanChange || 
                    		bHUsedDataChange || bAlertValueChange || bHTimeLimitFlagChange ||
                    		bHTimeLimitTimesChange || bHUsedTimesChange || 
                    		bHAutoDisconnFlagChange) {
	                    Intent megIntent= new Intent(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET);
	                    megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
	                    megIntent.putExtra(USAGE_SETTING_BILLING_DAY_CHANGE, bBillingDayChange);
		                megIntent.putExtra(USAGE_SETTING_MONTHLY_PLAN_CHANGE, bHMonthlyPlanChange);
		                megIntent.putExtra(USAGE_SETTING_USED_DATA_CHANGE, bHUsedDataChange);
						megIntent.putExtra(USAGE_SETTING_USAGE_ALERT_CHANGE, bAlertValueChange);
		                megIntent.putExtra(USAGE_SETTING_TIME_LIMIT_FLAG_CHANGE, bHTimeLimitFlagChange);
		                megIntent.putExtra(USAGE_SETTING_TIME_LIMIT_TIMES_CHANGE, bHTimeLimitTimesChange);
		                megIntent.putExtra(USAGE_SETTING_USED_TIMES_CHANGE, bHUsedTimesChange);
		                megIntent.putExtra(USAGE_SETTING_AUTO_DISCONN_FLAG_CHANGE, bHAutoDisconnFlagChange);
	        			m_context.sendBroadcast(megIntent);
                    }
                }
            }));
        } 
	}
	
	//SetBillingDay  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setBillingDay(DataValue data) {
		final int nBillingDay = (Integer) data.getParamByKey("billing_day");
		final int nPreBillingDay = m_usageSettings.HBillingDay;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.BillingDay = nBillingDay;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreBillingDay != m_usageSettings.HBillingDay);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    }

	//setAlertValue  Request //////////////////////////////////////////////////////////////////////////////////////////
	public void setAlertValue(DataValue data){
		final int nAlertValue = (Integer) data.getParamByKey("alert_value");
		final int nPreAlertValue = m_usageSettings.HUsageAlertValue;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.UsageAlertValue = nAlertValue;

		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
			@Override
			public void onHttpRequestFinish(BaseResponse response)
			{
				String strErrcode = new String();
				int ret = response.getResultCode();
				if(ret == BaseResponse.RESPONSE_OK) {
					strErrcode = response.getErrorCode();
					if(strErrcode.length() == 0) {
						m_usageSettings.setValue(nUsageSettings);
					}else{

					}
				}else{

				}

				Intent megIntent= new Intent(MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET);
				megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
				megIntent.putExtra(IS_CHANGED, nPreAlertValue != m_usageSettings.HUsageAlertValue);
				m_context.sendBroadcast(megIntent);
			}
		}));
	}
	
	//SetUnit  Request ////////////////////////////////////////////////////////////////////////////////////////// 
		public void setUnit(DataValue data) {
			final int nUnit = (Integer) data.getParamByKey("unit");
			final int nPreUnit = m_usageSettings.HUnit;
			final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
			nUsageSettings.clone(m_usageSettings);
			nUsageSettings.Unit = nUnit;
	    	
			LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
	            @Override
				public void onHttpRequestFinish(BaseResponse response) 
	            {   
	            	String strErrcode = new String();
	                int ret = response.getResultCode();
	                if(ret == BaseResponse.RESPONSE_OK) {
	                	strErrcode = response.getErrorCode();
	                	if(strErrcode.length() == 0) {
	                		m_usageSettings.setValue(nUsageSettings);
	                		Log.v("CHECKUNIT", "Monthly Plan: " + m_usageSettings.HMonthlyPlan);
	                	}else{

	                	}
	                }else{

	                }
	 
	                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_UNIT_REQUSET);
	                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
	                megIntent.putExtra(IS_CHANGED, nPreUnit != m_usageSettings.HUnit);
	    			m_context.sendBroadcast(megIntent);
	            }
	        }));
	    }
	
	//setMonthlyPlan  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setMonthlyPlan(DataValue data) {
		final long nMonthlyPlan = (Long)data.getParamByKey("monthly_plan");
		final long nPreMonthlyPlan = m_usageSettings.HMonthlyPlan;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		
		if(nMonthlyPlan <= 0)
		{
			final ENUM.OVER_DISCONNECT_STATE nAutoDisconnFlag = (ENUM.OVER_DISCONNECT_STATE) data.getParamByKey("auto_disconn_flag");
			nUsageSettings.AutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.antiBuild(nAutoDisconnFlag);
		}
		nUsageSettings.MonthlyPlan = nMonthlyPlan;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreMonthlyPlan != m_usageSettings.HMonthlyPlan);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setUsedData  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setUsedData(DataValue data) {
		final long nUsedData = (Integer) data.getParamByKey("used_data");
		final long nPreUsedData = m_usageSettings.HUsedData;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.UsedData = nUsedData;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_USED_DATA_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreUsedData != m_usageSettings.HUsedData);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setTimeLimitFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setTimeLimitFlag(DataValue data) {
		final ENUM.OVER_TIME_STATE nTimeLimitFlag = (ENUM.OVER_TIME_STATE) data.getParamByKey("time_limit_flag");
		final ENUM.OVER_TIME_STATE nPreTimeLimitFlag = m_usageSettings.HTimeLimitFlag;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.TimeLimitFlag = ENUM.OVER_TIME_STATE.antiBuild(nTimeLimitFlag);
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreTimeLimitFlag != m_usageSettings.HTimeLimitFlag);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setTimeLimitTimes  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setTimeLimitTimes(DataValue data) {
		final int nTimeLimitTimes = (Integer) data.getParamByKey("time_limit_times");
		final int nPreTimeLimitTimes = m_usageSettings.HTimeLimitTimes;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.TimeLimitTimes = nTimeLimitTimes;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreTimeLimitTimes != m_usageSettings.HTimeLimitTimes);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setUsedTimes  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setUsedTimes(DataValue data) {
		final int nUsedTimes = (Integer) data.getParamByKey("used_times");
		final int nPreUsedTimes = m_usageSettings.HUsedTimes;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.UsedTimes = nUsedTimes;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_USED_TIMES_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreUsedTimes != m_usageSettings.HUsedTimes);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//AutoDisconnFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setAutoDisconnFlag(DataValue data) {
		final ENUM.OVER_DISCONNECT_STATE nAutoDisconnFlag = (ENUM.OVER_DISCONNECT_STATE) data.getParamByKey("auto_disconn_flag");
		final ENUM.OVER_DISCONNECT_STATE nPreAutoDisconnFlag = m_usageSettings.HAutoDisconnFlag;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.AutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.antiBuild(nAutoDisconnFlag);
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings(nUsageSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageSettings.setValue(nUsageSettings);
                	}else{

                	}
                }else{

                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(IS_CHANGED, nPreAutoDisconnFlag != m_usageSettings.HAutoDisconnFlag);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//ClearAllRecords  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void clearAllRecords(DataValue data) {
		String strCleartime = (String) data.getParamByKey("clear_time");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageHistory.SetUsageRecordClear(strCleartime, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {

                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET);
            }
        }));
    } 
	
	//GetUsageHistory ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetUsageHistoryTask() {
		if(m_getUsageHistoryTask == null) {
			m_getUsageHistoryTask = new GetUsageHistoryTask();
			m_getUsageHistoryRollTimer.scheduleAtFixedRate(m_getUsageHistoryTask, 0, 10 * 1000);
		}
	}
    
	class GetUsageHistoryTask extends TimerTask{ 
        @Override
		public void run() { 
        	getUsageHistorySingle();
        } 
	}
	
	private void getUsageHistorySingle() {
		SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable) 
			return;
		LegacyHttpClient.getInstance().sendPostRequest(new HttpUsageHistory.GetUsageRecord(new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_usageRecord = response.getModelResult();
					}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET);
            }
		}));
	}
}
