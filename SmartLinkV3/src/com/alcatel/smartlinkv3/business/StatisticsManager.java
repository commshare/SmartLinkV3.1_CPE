package com.alcatel.smartlinkv3.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageHistory;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

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
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {

			}
    	}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetUsageSettingTask();
				}
			}
    	}
		
		//get usage history when have imsi
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
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
		if(intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET) || 
				intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET) || 
				intent.getAction().equals(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				getUsageHistorySingle();
			}
    	}
		
		if(intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				getUsageHistorySingle();
			}
		}
		
		if(intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				boolean bBillingDayChanged = intent.getBooleanExtra(USAGE_SETTING_BILLING_DAY_CHANGE, false);
				if(bBillingDayChanged == true)
					getUsageHistorySingle();
			}
    	}
		
		if(intent.getAction().equals(MessageUti.CPE_CHANGED_BILLING_MONTH)) {
			getUsageHistorySingle();
		}
	}  
	
	public StatisticsManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		//m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.WAN_CONNECT_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.WAN_DISCONNECT_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
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
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "GetUsageSettings") != true)
			return;
		
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
    public static String USAGE_SETTING_TIME_LIMIT_FLAG_CHANGE = "com.alcatel.cpe.business.statistics.timelimitflagchange";
    public static String USAGE_SETTING_TIME_LIMIT_TIMES_CHANGE = "com.alcatel.cpe.business.statistics.timelimittimeschange";
    public static String USAGE_SETTING_USED_TIMES_CHANGE = "com.alcatel.cpe.business.statistics.usedtimeschange";
    public static String USAGE_SETTING_AUTO_DISCONN_FLAG_CHANGE = "com.alcatel.cpe.business.statistics.autodisconnflagchange";
    
	class GetUsageSettingsTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.GetUsageSettings("7.3", new IHttpFinishListener() {           
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
                    		bHUsedDataChange || bHTimeLimitFlagChange || 
                    		bHTimeLimitTimesChange || bHUsedTimesChange || 
                    		bHAutoDisconnFlagChange) {
	                    Intent megIntent= new Intent(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET);
	                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
	                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
	                    megIntent.putExtra(USAGE_SETTING_BILLING_DAY_CHANGE, bBillingDayChange);
		                megIntent.putExtra(USAGE_SETTING_MONTHLY_PLAN_CHANGE, bHMonthlyPlanChange);
		                megIntent.putExtra(USAGE_SETTING_USED_DATA_CHANGE, bHUsedDataChange);
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
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final int nBillingDay = (Integer) data.getParamByKey("billing_day");
		final int nPreBillingDay = m_usageSettings.HBillingDay;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.BillingDay = nBillingDay;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreBillingDay != m_usageSettings.HBillingDay);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    }
	
	//SetUnit  Request ////////////////////////////////////////////////////////////////////////////////////////// 
		public void setUnit(DataValue data) {
			if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
				return;
			
			final int nUnit = (Integer) data.getParamByKey("unit");
			final int nPreUnit = m_usageSettings.HUnit;
			final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
			nUsageSettings.clone(m_usageSettings);
			nUsageSettings.Unit = nUnit;
	    	
			HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
	                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
	                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
	                megIntent.putExtra(IS_CHANGED, nPreUnit != m_usageSettings.HUnit);
	    			m_context.sendBroadcast(megIntent);
	            }
	        }));
	    }
	
	//setMonthlyPlan  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setMonthlyPlan(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
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
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreMonthlyPlan != m_usageSettings.HMonthlyPlan);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setUsedData  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setUsedData(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final long nUsedData = (Integer) data.getParamByKey("used_data");
		final long nPreUsedData = m_usageSettings.HUsedData;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.UsedData = nUsedData;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreUsedData != m_usageSettings.HUsedData);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setTimeLimitFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setTimeLimitFlag(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final ENUM.OVER_TIME_STATE nTimeLimitFlag = (ENUM.OVER_TIME_STATE) data.getParamByKey("time_limit_flag");
		final ENUM.OVER_TIME_STATE nPreTimeLimitFlag = m_usageSettings.HTimeLimitFlag;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.TimeLimitFlag = ENUM.OVER_TIME_STATE.antiBuild(nTimeLimitFlag);
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreTimeLimitFlag != m_usageSettings.HTimeLimitFlag);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setTimeLimitTimes  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setTimeLimitTimes(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final int nTimeLimitTimes = (Integer) data.getParamByKey("time_limit_times");
		final int nPreTimeLimitTimes = m_usageSettings.HTimeLimitTimes;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.TimeLimitTimes = nTimeLimitTimes;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreTimeLimitTimes != m_usageSettings.HTimeLimitTimes);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//setUsedTimes  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setUsedTimes(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final int nUsedTimes = (Integer) data.getParamByKey("used_times");
		final int nPreUsedTimes = m_usageSettings.HUsedTimes;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.UsedTimes = nUsedTimes;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreUsedTimes != m_usageSettings.HUsedTimes);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//AutoDisconnFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setAutoDisconnFlag(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageSettings") != true)
			return;
		
		final ENUM.OVER_DISCONNECT_STATE nAutoDisconnFlag = (ENUM.OVER_DISCONNECT_STATE) data.getParamByKey("auto_disconn_flag");
		final ENUM.OVER_DISCONNECT_STATE nPreAutoDisconnFlag = m_usageSettings.HAutoDisconnFlag;
		final UsageSettingsResult nUsageSettings = new UsageSettingsResult();
		nUsageSettings.clone(m_usageSettings);
		nUsageSettings.AutoDisconnFlag = ENUM.OVER_DISCONNECT_STATE.antiBuild(nAutoDisconnFlag);
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetUsageSettings("7.4",nUsageSettings, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreAutoDisconnFlag != m_usageSettings.HAutoDisconnFlag);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//ClearAllRecords  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void clearAllRecords(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetUsageRecordClear") != true)
			return;
		String strCleartime = (String) data.getParamByKey("clear_time");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageHistory.SetUsageRecordClear("7.2",strCleartime, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//GetUsageHistory ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetUsageHistoryTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "GetUsageRecord") != true)
			return;
		
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
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable) 
			return;
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageHistory.GetUsageRecord("7.1", new IHttpFinishListener() {           
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
                
                Intent megIntent= new Intent(MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
		}));
	}
}
