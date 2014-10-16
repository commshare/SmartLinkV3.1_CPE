package com.alcatel.smartlinkv3.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageHistoryItemModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageHistory;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageHistoryResult;
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

public class StatisticsManager extends BaseManager {
	private UsageSettingModel m_usageSettings = new UsageSettingModel();
	private ArrayList<UsageHistoryItemModel> m_usageHistory = new ArrayList<UsageHistoryItemModel>();
	private Timer m_rollTimer = new Timer();
	private Timer m_getUsageHistoryRollTimer = new Timer();
	
	private GetUsageSettingsTask m_getUsageSettingsTask = null;
	private GetUsageHistoryTask m_getUsageHistoryTask = null;
	
	public static String IS_CHANGED = "com.alcatel.cpe.business.statistics.ischanged";
	
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_usageSettings.clear();
		m_usageHistory.clear();
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
					m_usageHistory.clear();
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
	
	public ArrayList<UsageHistoryItemModel> getUsageHistory() {
		return m_usageHistory;
	}
	
    public long GetTodayUsage() {
    	Calendar caNow = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(Const.DATE_FORMATE);
		String strDay = format.format(caNow.getTime());
    	ArrayList<UsageHistoryItemModel> usageList = new ArrayList<UsageHistoryItemModel>();   	
    	usageList = (ArrayList<UsageHistoryItemModel>) m_usageHistory.clone();
    	long total = 0;
    	for(int i = 0;i < usageList.size();i++){
    		UsageHistoryItemModel dayUsage = usageList.get(i);
    		if(strDay.equalsIgnoreCase(dayUsage.m_strDate)) {
    			total += dayUsage.m_lDownloadBytes;
    			total += dayUsage.m_lUploadBytes;
    			break;
    		}
    	}
    	return total;
    }
    
    public long GetBillingMonthTotalUsage() {
    	ArrayList<UsageHistoryItemModel> usageList = new ArrayList<UsageHistoryItemModel>();
    	usageList = (ArrayList<UsageHistoryItemModel>) m_usageHistory.clone();
    	long total = 0;
    	for(int i = 0;i < usageList.size();i++){
    		UsageHistoryItemModel dayUsage = usageList.get(i);
    		if(m_usageSettings.m_strStartBillDate.compareTo(dayUsage.m_strDate) <= 0 &&
    				m_usageSettings.m_strEndBillDate.compareTo(dayUsage.m_strDate) >= 0) {
    			total += dayUsage.m_lDownloadBytes;
    			total += dayUsage.m_lUploadBytes;
    		}
    	}
    	//total += m_staSetting.getCalibrationValue();
    	return total;
    }
	
	//GetUsageSetting ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetUsageSettingTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "GetUsageSettings") != true)
			return;
		
		//GetUsageSettingsTask getUsageSettingsTask = new GetUsageSettingsTask();
		//m_rollTimer.scheduleAtFixedRate(getUsageSettingsTask, 0, 10 * 1000);
		if(m_getUsageSettingsTask == null) {
			m_getUsageSettingsTask = new GetUsageSettingsTask();
			m_rollTimer.scheduleAtFixedRate(m_getUsageSettingsTask, 0, 30 * 1000);
		}
	}
	
	public static String USAGE_SETTING_BILLING_DAY_CHANGE = "com.alcatel.cpe.business.statistics.billingdaychange";
    public static String USAGE_SETTING_CALIBRATION_CHANGE = "com.alcatel.cpe.business.statistics.calibrationchange";
    public static String USAGE_SETTING_LIMIT_CHANGE = "com.alcatel.cpe.business.statistics.limitchange";
    public static String USAGE_SETTING_TOTAL_CHANGE = "com.alcatel.cpe.business.statistics.totalchange";
    public static String USAGE_SETTING_OVERTIME_VALUE_CHANGE = "com.alcatel.cpe.business.statistics.overtimechange";
    public static String USAGE_SETTING_OVERTIME_STATE_CHANGE = "com.alcatel.cpe.business.statistics.overtimestatechange";
    public static String USAGE_SETTING_OVERFLOW_STATE_CHANGE = "com.alcatel.cpe.business.statistics.overflowstatechange";
    
	class GetUsageSettingsTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.GetUsageSettings("7.10", new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {   
                	boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        			if(bCPEWifiConnected == false) {
        				return;
        			}
        			
        			boolean bBillingDayChange = false;
	            	boolean bCalibrationValueChange = false;
	            	boolean bLimitValueChange = false;
	            	boolean bTotalValueChange = false;
	            	boolean bOverTimeValueChange = false;
	            	boolean bOverTimeStateChange = false;
	            	boolean bOverFlowStateChange = false;
	            	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		UsageSettingsResult usageSettingResult = response.getModelResult();
                    		UsageSettingModel pre = new UsageSettingModel();
                    		pre.clone(m_usageSettings);
                    		m_usageSettings.setValue(usageSettingResult);
                    		if(pre.m_nBillingDay != m_usageSettings.m_nBillingDay)
                    			bBillingDayChange = true;
                    		
                    		if(pre.m_lCalibrationValue != m_usageSettings.m_lCalibrationValue)
                    			bCalibrationValueChange = true;
                    		
                    		if(pre.m_lLimitValue != m_usageSettings.m_lLimitValue)
                    			bLimitValueChange = true;
                    		
                    		if(pre.m_lTotalValue != m_usageSettings.m_lTotalValue)
                    			bTotalValueChange = true;
                    		
                    		if(pre.m_lOvertime != m_usageSettings.m_lOvertime)
                    			bOverTimeValueChange = true;
                    		
                    		if(pre.m_overtimeState != m_usageSettings.m_overtimeState)
                    			bOverTimeStateChange = true;
                    		
                    		if(pre.m_overflowState != m_usageSettings.m_overflowState)
                    			bOverFlowStateChange = true;
                    		
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    if(bBillingDayChange || bCalibrationValueChange || 
	            			bLimitValueChange || bTotalValueChange || 
	            			bOverTimeValueChange || bOverTimeStateChange || 
	            			bOverFlowStateChange) {
	                    Intent megIntent= new Intent(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET);
	                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
	                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
	                    megIntent.putExtra(USAGE_SETTING_BILLING_DAY_CHANGE, bBillingDayChange);
		                megIntent.putExtra(USAGE_SETTING_CALIBRATION_CHANGE, bCalibrationValueChange);
		                megIntent.putExtra(USAGE_SETTING_LIMIT_CHANGE, bLimitValueChange);
		                megIntent.putExtra(USAGE_SETTING_TOTAL_CHANGE, bTotalValueChange);
		                megIntent.putExtra(USAGE_SETTING_OVERTIME_VALUE_CHANGE, bOverTimeValueChange);
		                megIntent.putExtra(USAGE_SETTING_OVERTIME_STATE_CHANGE, bOverTimeStateChange);
		                megIntent.putExtra(USAGE_SETTING_OVERFLOW_STATE_CHANGE, bOverFlowStateChange);
	        			m_context.sendBroadcast(megIntent);
                    }
                }
            }));
        } 
	}
	
	//SetBillingDay  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setBillingDay(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetBillingDay") != true)
			return;
		
		final int nBillingDay = (Integer) data.getParamByKey("billing_day");
		final int nPreBillingDay = m_usageSettings.m_nBillingDay;
		m_usageSettings.setBillingDay(nBillingDay);
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetBillingDay("7.3",nBillingDay, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_nBillingDay != nBillingDay)
                			m_usageSettings.setBillingDay(nBillingDay);
                	}else{
                		m_usageSettings.setBillingDay(nPreBillingDay);
                	}
                }else{
                	m_usageSettings.setBillingDay(nPreBillingDay);
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, nPreBillingDay != m_usageSettings.m_nBillingDay);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SetLimitValue  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setLimitValue(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetLimitValue") != true)
			return;
		
		final long lLimitValue = (Long) data.getParamByKey("limit_value");
		final long lPreLimitValue = m_usageSettings.m_lLimitValue;
		m_usageSettings.m_lLimitValue = lLimitValue;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetLimitValue("7.7",lLimitValue, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_lLimitValue != lLimitValue)
                			m_usageSettings.m_lLimitValue = lLimitValue;
                	}else{
                		m_usageSettings.m_lLimitValue = lPreLimitValue;
                	}
                }else{
                	m_usageSettings.m_lLimitValue = lPreLimitValue;
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_LIMIT_VALUE_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, lPreLimitValue != m_usageSettings.m_lLimitValue);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SetTotalValue  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setTotalValue(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetTotalValue") != true)
			return;
		
		final long lTotalValue = (Long) data.getParamByKey("total_value");
		final long lPreTotalValue = m_usageSettings.m_lTotalValue;
		m_usageSettings.m_lTotalValue = lTotalValue;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetTotalValue("7.9",lTotalValue, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_lTotalValue != lTotalValue)
                			m_usageSettings.m_lTotalValue = lTotalValue;
                		if(m_usageSettings.m_lTotalValue <= 0)//close usage alert 
                			CPEConfig.getInstance().setNotificationSwitch(false);
                	}else{
                		m_usageSettings.m_lTotalValue = lPreTotalValue;
                	}
                }else{
                	m_usageSettings.m_lTotalValue = lPreTotalValue;
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_TOTAL_VALUE_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, lPreTotalValue != m_usageSettings.m_lTotalValue);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SetDisconnectOvertime  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setDisconnectOvertime(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetDisconnectOvertime") != true)
			return;
		
		final int nOverTime = (Integer) data.getParamByKey("over_time");
		final long lPreOverTime = m_usageSettings.m_lOvertime;
		m_usageSettings.m_lOvertime = nOverTime;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetDisconnectOvertime("7.15",nOverTime, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_lOvertime != nOverTime)
                			m_usageSettings.m_lOvertime = nOverTime;
                	}else{
                		m_usageSettings.m_lOvertime = lPreOverTime;
                	}
                }else{
                	m_usageSettings.m_lOvertime = lPreOverTime;
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_DISCONNECT_OVER_TIME_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, lPreOverTime != m_usageSettings.m_lOvertime);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SetDisconnectOvertimeState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setDisconnectOvertimeState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetDisconnectOvertimeState") != true)
			return;
		
		final ENUM.OVER_TIME_STATE overTimeStatus = (ENUM.OVER_TIME_STATE) data.getParamByKey("over_time_status");
		final ENUM.OVER_TIME_STATE preOverTimeStatus = m_usageSettings.m_overtimeState;
		m_usageSettings.m_overtimeState = overTimeStatus;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetDisconnectOvertimeState("7.16",overTimeStatus == ENUM.OVER_TIME_STATE.Enable?true:false, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_overtimeState != overTimeStatus)
                			m_usageSettings.m_overtimeState = overTimeStatus;
                	}else{
                		m_usageSettings.m_overtimeState = preOverTimeStatus;
                	}
                }else{
                	m_usageSettings.m_overtimeState = preOverTimeStatus;
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_DISCONNECT_OVER_TIME_STATUS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, preOverTimeStatus != m_usageSettings.m_overtimeState);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SetDisconnectOverflowState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setDisconnectOverflowState(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "SetDisconnectOverflowState") != true)
			return;
		
		final ENUM.OVER_FLOW_STATE overFlowStatus = (ENUM.OVER_FLOW_STATE) data.getParamByKey("over_flow_status");
		final ENUM.OVER_FLOW_STATE preOverFlowStatus = m_usageSettings.m_overflowState;
		m_usageSettings.m_overflowState = overFlowStatus;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageSettings.SetDisconnectOverflowState("7.17",overFlowStatus == ENUM.OVER_FLOW_STATE.Enable?true:false, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		if(m_usageSettings.m_overflowState != overFlowStatus)
                			m_usageSettings.m_overflowState = overFlowStatus;
                	}else{
                		m_usageSettings.m_overflowState = preOverFlowStatus;
                	}
                }else{
                	m_usageSettings.m_overflowState = preOverFlowStatus;
                }
 
                Intent megIntent= new Intent(MessageUti.STATISTICS_SET_DISCONNECT_OVER_FLOW_STATUS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(IS_CHANGED, preOverFlowStatus != m_usageSettings.m_overflowState);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//ClearAllRecords  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void clearAllRecords(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "ClearAllRecords") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageHistory.ClearAllRecords("7.14", new IHttpFinishListener() {           
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
		if(FeatureVersionManager.getInstance().isSupportApi("Statistics", "GetUsageHistory") != true)
			return;
		
		if(m_getUsageHistoryTask == null) {
			m_getUsageHistoryTask = new GetUsageHistoryTask();
			m_getUsageHistoryRollTimer.scheduleAtFixedRate(m_getUsageHistoryTask, 0, 30 * 1000);
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
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUsageHistory.GetUsageHistory("7.1",m_usageSettings.m_strStartBillDate,
				m_usageSettings.m_strEndBillDate, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		UsageHistoryResult usageHistoryResult = response.getModelResult();
                		m_usageHistory.clear();
                		for(int i = 0;i < usageHistoryResult.UsageHistoryList.size();i++) {
                			UsageHistoryItemModel usageItem = new UsageHistoryItemModel();
                			usageItem.setValue(usageHistoryResult.UsageHistoryList.get(i));
                			m_usageHistory.add(usageItem);
                		}
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
