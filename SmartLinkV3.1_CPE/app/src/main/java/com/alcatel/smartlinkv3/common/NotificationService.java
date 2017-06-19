package com.alcatel.smartlinkv3.common;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.StatisticsManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;

import java.util.List;

public class NotificationService extends Service {
	private static enum ALERT_TYPE {
		 BatteryLimit, UsageLimit, Upgrade
	}

	private boolean m_isNeedToAlertUsageLimit = true;
	private boolean m_AlertUsageLimitLessOneTime = true;
	private boolean m_AlertUsageLimitOverOneTime = true;
	
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
			String action = intent.getAction();
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();

			if (DataConnectManager.getInstance().getCPEWifiConnected()) {
				if (MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET.equals(action)) {
					if (ok) {
						//	m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
					}
				}
				
//				if (intent.getAction().equals(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET) || 
//						intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET)) {
//					int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
//					String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
//					if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
//						boolean bIsChange = intent.getBooleanExtra(StatisticsManager.IS_CHANGED,false);
//						if(bIsChange == true) {
//							m_isNeedToAlertUsageLimit = true;					
//							m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());						
//						}
//					}
//				}
				
//				if (intent.getAction().equals(MessageUti.CPE_CHANGED_ALERT_SWITCH)) {
//					m_isNeedToAlertUsageLimit = true;					
//					m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
//				}
				
				if(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET.equals(action)) {

					boolean bIsChange = intent.getBooleanExtra(StatisticsManager.IS_CHANGED,false);
					if(bIsChange && ok) {
						m_isNeedToAlertUsageLimit = true;
	//					m_isNeedToAlertBatteryLimit  = false;
	//					m_isNeedToAlertUpgrade = false;

						m_AlertUsageLimitLessOneTime = true;
						m_AlertUsageLimitOverOneTime = true;
						m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
					}
				}
				
				if(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET.equals(action)) {
					if (ok) {
		    			m_isNeedToAlertUsageLimit = true;	
						m_isNeedToAlertBatteryLimit  = false;
						m_isNeedToAlertUpgrade = false;
						
						m_AlertUsageLimitLessOneTime = true;
						m_AlertUsageLimitOverOneTime = true;
						m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
		    		}
		    	}
				
				if(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
					if(ok) {
						SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
						if(simStatus.m_SIMState != ENUM.SIMState.Accessable) {
							m_isNeedToAlertUsageLimit = true;	
							m_isNeedToAlertBatteryLimit  = true;
							m_isNeedToAlertUpgrade = true;
							
							m_nm.cancel(ALERT_TYPE.UsageLimit.ordinal());
						}
					}
		    	}		
//battery				
				if (MessageUti.POWER_GET_BATTERY_STATE.equals(action)) {
					if(ok) {
						BatteryInfo  batteryinfo = BusinessManager.getInstance().getBatteryInfo();
						if(batteryinfo.getChargeState() == ConstValue.CHARGE_STATE_CHARGING)
						{
							m_isNeedToAlertBatteryLimit = false;	
//							m_isNeedToAlertUsageLimit  = false;
//							m_isNeedToAlertUpgrade = false;
							
							m_AlertBatteryLimit2OneTime = true;
							m_AlertBatteryLimit1OneTime = true;
							m_nm.cancel(ALERT_TYPE.BatteryLimit.ordinal());
						}else
						{
							m_isNeedToAlertBatteryLimit = true;
//							m_isNeedToAlertUsageLimit = false;
//							m_isNeedToAlertUpgrade = false;
//							if(m_AlertBatteryLimit1OneTime || m_AlertBatteryLimit2OneTime)
//							{
//								m_nm.cancel(ALERT_TYPE.BatteryLimit.ordinal());
//							}
						}
						}
					}
//upgrade
				if (MessageUti.UPDATE_GET_DEVICE_NEW_VERSION.equals(action)) {
					if(ok) {
						m_isNeedToAlertUpgrade = true;
//						m_isNeedToAlertBatteryLimit = false;
//						m_isNeedToAlertUsageLimit = false;
						
						if(m_AlertUpgradeOneTime)
						{
							m_nm.cancel(ALERT_TYPE.Upgrade.ordinal());
						}
					}
				}
				alert();
			} else {
				m_isNeedToAlertUsageLimit = true;
				m_AlertUsageLimitLessOneTime = true;
				m_AlertUsageLimitOverOneTime = true;	
				m_isNeedToAlertBatteryLimit = true;
				m_AlertBatteryLimit2OneTime = true;
				m_AlertBatteryLimit1OneTime = true;
				m_isNeedToAlertUpgrade = true;
				m_nm.cancelAll();			
			}

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		BusinessManager.getInstance();
		//BusinessManager.getInstance().m_wifiNetworkReceiver.testWebServer(this);
		Log.d("NotificationService", "pchong   HttpService onCreate");
		m_msgReceiver = new NotificationBroadcastReceiver();
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.CPE_WIFI_CONNECT_CHANGE));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));
		
		
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
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
			startHttpService(context);
		}
	}

	private static void startHttpService(Context context) {
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
		UsageSettingModel settings = BusinessManager.getInstance()
				.getUsageSettings();
		UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();

		BatteryInfo  batteryinfo = BusinessManager.getInstance().getBatteryInfo();
		
		DeviceNewVersionInfo newVersioninfo = BusinessManager.getInstance().getNewFirmwareInfo();
		
		//SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
		

		if (settings.HMonthlyPlan > 0
				&& m_isNeedToAlertUsageLimit == true
				&&(((m_UsageRecordResult.HUseData >= settings.HMonthlyPlan)&&m_AlertUsageLimitOverOneTime == true)
						||(m_UsageRecordResult.HUseData < settings.HMonthlyPlan&&isOverMonthlyPlan(settings,m_UsageRecordResult) && (m_AlertUsageLimitLessOneTime==true)))) {
//			if (simState.m_SIMState != SIMState.Accessable) 
//				return;
			
			if((m_UsageRecordResult.HUseData >= settings.HMonthlyPlan) && m_AlertUsageLimitOverOneTime == true)
			{
					showNotification(ALERT_TYPE.UsageLimit, m_UsageRecordResult.HUseData/settings.HMonthlyPlan);
					m_isNeedToAlertUsageLimit = false;
					m_AlertUsageLimitOverOneTime = false;
					
			}else if((m_AlertUsageLimitLessOneTime == true)&&isOverMonthlyPlan(settings,m_UsageRecordResult) &&(m_AlertUsageLimitLessOneTime = true))
			{
					showNotification(ALERT_TYPE.UsageLimit, m_UsageRecordResult.HUseData/settings.HMonthlyPlan);
					m_isNeedToAlertUsageLimit = false;
					m_AlertUsageLimitLessOneTime = false;
			}
		}else if ( m_isNeedToAlertBatteryLimit == true &&((isOverBattery2(batteryinfo.getBatterLevel())&&m_AlertBatteryLimit2OneTime == true)
				||((isOverBattery1(batteryinfo.getBatterLevel()))&&(m_AlertBatteryLimit1OneTime == true))))
		{
				if(isOverBattery2(batteryinfo.getBatterLevel()))
				{
					if(m_AlertBatteryLimit2OneTime == true)
					{
						showNotification(ALERT_TYPE.BatteryLimit, batteryinfo.getBatterLevel());
						m_isNeedToAlertBatteryLimit = false;
						m_AlertBatteryLimit2OneTime = false;
					}
				}else if(isOverBattery1(batteryinfo.getBatterLevel()))
				{
					if(m_AlertBatteryLimit1OneTime == true)
					{
						showNotification(ALERT_TYPE.BatteryLimit, batteryinfo.getBatterLevel());
						m_isNeedToAlertBatteryLimit = false;
						m_AlertBatteryLimit1OneTime = false;
					}
				}
			}else if(isHaveNewVersion(newVersioninfo.getState())
						&& m_isNeedToAlertUpgrade == true&&m_AlertUpgradeOneTime == true){
			showNotification(ALERT_TYPE.Upgrade, newVersioninfo.getState());
			m_isNeedToAlertUpgrade = false;
			m_AlertUpgradeOneTime = false;
		}
	}
	
	private boolean isOverMonthlyPlan(UsageSettingModel usagesetting ,UsageRecordResult m_UsageRecordResult)
	{
		boolean bOverPlan = false;
		double standard = usagesetting.HMonthlyPlan/10;
		double ft = usagesetting.HMonthlyPlan - m_UsageRecordResult.HUseData;
		if (ft <= standard) {
			bOverPlan = true;
		}
		return bOverPlan;
		
	}
	
	private boolean isOverBattery2(int BatteryLevel) {
		boolean bOverBattery = false;
		if (BatteryLevel <= 20 && BatteryLevel>10) {
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
		Intent it = new Intent(this, HomeActivity.class);	
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
		UsageSettingModel settings = BusinessManager.getInstance().getUsageSettings();
		switch(type)
		{
		case UsageLimit:
			if(used>=1)
			{
				strContent = this.getResources().getString(R.string.usage_limit_over_notification_content);
			}else
			{
				strContent = String.format(this.getResources().getString(R.string.usage_limit_less_notification_content), CommonUtil.ConvertTrafficToStringFromMB(this, (long)(settings.HMonthlyPlan/10)));
			}
			break;	
			
		case BatteryLimit:	
			if(used <= 10)
			{
				strContent = String.format(this.getResources().getString(R.string.battery_limit_notification_content1), used);
			}else
			{
				strContent = String.format(this.getResources().getString(R.string.battery_limit_notification_content2), used);
			}
		break;
			
		case Upgrade:
			strContent= this.getResources().getString(R.string.upgrade_notification_content);
			break;		
		}
		return strContent;
	}
}
