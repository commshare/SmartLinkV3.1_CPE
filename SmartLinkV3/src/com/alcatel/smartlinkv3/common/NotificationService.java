package com.alcatel.smartlinkv3.common;

import java.util.List;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.StatisticsManager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.OVER_TIME_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class NotificationService extends Service {
	private static enum ALERT_TYPE {
		 BatteryLimit, UsageLimit, NewMessage, Upgrade
	}

	private boolean m_isNeedToAlertUsageLimit = true;
	private boolean m_AlertUsageLimitOneTime = true;
	
	private boolean m_isNeedToAlertBatteryLimit = true;
	private boolean m_AlertBatteryLimit2OneTime = true;
	private boolean m_AlertBatteryLimit1OneTime = true;
	
	private boolean m_isNeedToAlertUpgrade = true;
	private boolean m_AlertUpgradeOneTime = true;
	
	
	private NotificationManager m_nm ;

	private NotificationBroadcastReceiver m_msgReceiver;

	private class NotificationBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DataConnectManager.getInstance().getCPEWifiConnected()) {
				if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
						boolean bBillingDayChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_BILLING_DAY_CHANGE,false);
						boolean bCalibrationValueChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_MONTHLY_PLAN_CHANGE,false);
						boolean bLimitValueChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_USED_DATA_CHANGE,false);
						boolean bTotalValueChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_TIME_LIMIT_FLAG_CHANGE,false);
						boolean bOverTimeValueChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_TIME_LIMIT_TIMES_CHANGE,false);
						boolean bOverTimeStateChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_USED_TIMES_CHANGE,false);
						boolean bOverFlowStateChange = intent.getBooleanExtra(StatisticsManager.USAGE_SETTING_AUTO_DISCONN_FLAG_CHANGE,false);

						if (bBillingDayChange || bCalibrationValueChange
								|| bLimitValueChange || bTotalValueChange
								|| bOverFlowStateChange) {
							m_isNeedToAlertUsageLimit = true;						
							m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
						} else if (bOverTimeValueChange || bOverTimeStateChange) {
						}

					}
				}
				
				if (intent.getAction().equals(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET) || 
						intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET)) {
					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
						boolean bIsChange = intent.getBooleanExtra(StatisticsManager.IS_CHANGED,false);
						if(bIsChange == true) {
							m_isNeedToAlertUsageLimit = true;					
							m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());						
						}
					}
				}
				
				if (intent.getAction().equals(MessageUti.CPE_CHANGED_ALERT_SWITCH)) {
					m_isNeedToAlertUsageLimit = true;					
					m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
				}
				
				if(intent.getAction().equals(MessageUti.CPE_CHANGED_BILLING_MONTH)) {
					m_isNeedToAlertUsageLimit = true;						
					m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
				}
				
				if(intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {
					int nResult = intent.getIntExtra(
							MessageUti.RESPONSE_RESULT, 0);
					String strErrorCode = intent
							.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if (nResult == BaseResponse.RESPONSE_OK
							&& strErrorCode.length() == 0) {
		    			m_isNeedToAlertUsageLimit = true;					
						m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
		    		}
		    	}
				
				if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
						SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
						if(simStatus.m_SIMState != ENUM.SIMState.Accessable) {
							m_isNeedToAlertUsageLimit = true;
							m_nm.cancelAll();
						}
					}
		    	}		
//battery				
				if (intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_BATTERY_STATE)) {
					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
						BatteryInfo  batteryinfo = BusinessMannager.getInstance().getBatteryInfo();
						if(batteryinfo.getChargeState() == ConstValue.CHARGE_STATE_CHARGING)
						{
							m_isNeedToAlertBatteryLimit = false;
							m_AlertBatteryLimit2OneTime = true;
							m_AlertBatteryLimit1OneTime = true;
						}else
						{
							m_isNeedToAlertBatteryLimit = true;
						}
							m_nm.cancelAll();
						}
					}
//upgrade
				if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
					if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
						m_isNeedToAlertUpgrade = true;
							m_nm.cancelAll();
					}
				}
				alert();
			} else {
				m_isNeedToAlertUsageLimit = true;
				m_isNeedToAlertBatteryLimit = true;
				m_isNeedToAlertUpgrade = true;
				m_nm.cancelAll();			
			}

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		BusinessMannager.getInstance();
		//BusinessMannager.getInstance().m_wifiNetworkReceiver.testWebServer(this);
		Log.d("HttpService", "HttpService onCreate");
		m_msgReceiver = new NotificationBroadcastReceiver();
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.CPE_WIFI_CONNECT_CHANGE));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_DATA_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_TIMES_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.CPE_CHANGED_BILLING_MONTH));
		
		
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.POWER_GET_BATTERY_STATE));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.CPE_CHANGED_ALERT_SWITCH));
		
		
		m_nm = (NotificationManager) this
  				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			this.unregisterReceiver(m_msgReceiver);
		} catch (Exception e) {

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public static void startService() {
		Context context = SmartLinkV3App.getInstance().getApplicationContext();
		if (!isServiceRunning(context)) {
			Log.d("NotificationService", "startService");
			startHttpService(context);
		}
	}

	private static void startHttpService(Context context) {
		Log.d("NotificationService", "startNotificationService");
		Intent Intent = new Intent(context, NotificationService.class);
		context.startService(Intent);
	}

	private static boolean isServiceRunning(Context context) {
		boolean bRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(1000);
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equalsIgnoreCase(
					NotificationService.class.getName())) {
				bRunning = true;
				break;
			}
		}

		return bRunning;
	}

	private void alert() {
		UsageSettingModel settings = BusinessMannager.getInstance()
				.getUsageSettings();
		long lTotalUsedUsage = BusinessMannager.getInstance()
				.GetBillingMonthTotalUsage();

		BatteryInfo  batteryinfo = BusinessMannager.getInstance().getBatteryInfo();
		
		DeviceNewVersionInfo newVersioninfo = BusinessMannager.getInstance().getNewFirmwareInfo();
		
		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState != SIMState.Accessable) 
			return;

		if (settings.HMonthlyPlan > 0
				&& settings.HUsedData > settings.HMonthlyPlan
				&& lTotalUsedUsage >= settings.HMonthlyPlan
				&& m_isNeedToAlertUsageLimit == true && 
				CPEConfig.getInstance().getNotificationSwitch() == true) {
			Log.e("alert", "usage alert");
			showNotification(ALERT_TYPE.UsageLimit, lTotalUsedUsage);
			m_isNeedToAlertUsageLimit = false;
			
		} else if ( m_isNeedToAlertBatteryLimit == true ) {
			if(m_AlertBatteryLimit2OneTime == true)
			{
				if(isOverBattery2(batteryinfo.getBatterLevel()))
				{
					showNotification(ALERT_TYPE.BatteryLimit, batteryinfo.getBatterLevel());
					m_isNeedToAlertBatteryLimit = false;
					m_AlertBatteryLimit2OneTime = false;
				}
			}else if(m_AlertBatteryLimit1OneTime == true)
			{
				if(isOverBattery1(batteryinfo.getBatterLevel()))
				{
					showNotification(ALERT_TYPE.BatteryLimit, batteryinfo.getBatterLevel());
					m_isNeedToAlertBatteryLimit = false;
					m_AlertBatteryLimit1OneTime = false;
				}
			}
		} else if(isHaveNewVersion(newVersioninfo.getState())
						&& m_isNeedToAlertUpgrade == true&&m_AlertUpgradeOneTime == true){
			Log.e("alert", "upgrade alert");
			showNotification(ALERT_TYPE.Upgrade, newVersioninfo.getState());
			m_isNeedToAlertUpgrade = false;
			m_AlertUpgradeOneTime = false;
		}
	}
	
	private boolean isOverBattery2(int BatteryLevel) {
		boolean bOverBattery = false;
		if (BatteryLevel <= 20 && BatteryLevel>=10) {
			bOverBattery = true;
		}
		return bOverBattery;
	}

	private boolean isOverBattery1(int BatteryLevel) {
		boolean bOverBattery = false;
		if (BatteryLevel <= 10 && BatteryLevel > 0) {
			bOverBattery = true;
		}
		return bOverBattery;
	}
	
	private boolean isHaveNewVersion(int istate) {
		boolean bnewVersion = false;
		if (1 == istate) {
			bnewVersion = true;
		}
		return bnewVersion;
	}
	
	private void showNotification(ALERT_TYPE type, long used) {		
		Intent it = new Intent(this, MainActivity.class);	
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				it, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification n = new Notification();
		RemoteViews contentView =new RemoteViews(this.getPackageName(), R.layout.custom_notify_view);	
		contentView.setTextViewText(R.id.notification_title,  getNotificationTitle(type));	
		contentView.setTextViewText(R.id.notification_content, getNotificationContent(type, used));
		n.icon = R.drawable.ic_launcher;
		n.contentView = contentView;	
		n.contentIntent = contentIntent;
		n.when = System.currentTimeMillis();
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		n.defaults=Notification.DEFAULT_SOUND;  
		
		m_nm.notify(type.ordinal(), n);	
	
	}
	
	private String getNotificationTitle(ALERT_TYPE type)
	{
		String strTitle = "";
		switch(type)
		{
		case UsageLimit:
			strTitle = this.getResources().getString(R.string.usage_limit_notification_title);
			break;	
			
		case BatteryLimit:	
			strTitle = this.getResources().getString(R.string.battery_limit_notification_title);
			break;
		
		case Upgrade:
			strTitle = this.getResources().getString(R.string.upgrade_notification_title);
			break;
		}
		return strTitle;
	}
	
	private String getNotificationContent(ALERT_TYPE type, long used)
	{
		String strContent = "";
		switch(type)
		{
		case UsageLimit:
			String str = this.getResources().getString(R.string.usage_limit_notification_content);
			strContent =  String.format(str, CommonUtil.ConvertTrafficToStringFromMB(this, used));
			break;	
			
		case BatteryLimit:	
			if(used <= 10)
			{
				strContent= this.getResources().getString(R.string.battery_limit_notification_content1);
			}else
			{
				strContent= this.getResources().getString(R.string.battery_limit_notification_content2);
			}
		break;
			
		case Upgrade:
			strContent= this.getResources().getString(R.string.upgrade_notification_content);
			break;		
		}
		return strContent;
	}
}
