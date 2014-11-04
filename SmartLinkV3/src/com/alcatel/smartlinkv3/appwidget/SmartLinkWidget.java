package com.alcatel.smartlinkv3.appwidget;

import com.alcatel.smartlinkv3.R;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.SignalStrength;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;

public class SmartLinkWidget extends AppWidgetProvider {

	private boolean m_blDeviceConnected=false;
	private boolean m_blWifiConnected = false;
	private boolean m_blInternetConnected = false;
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
				if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)){ 
					 updateUIs(context);
		        }
				
				if (intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_BATTERY_STATE)) {
					updateUIs(context);
				}
				
				if (intent.getAction().equalsIgnoreCase(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET)) {
					updateUIs(context);
				}
				
				if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET)) {
					updateUIs(context);
				}
				
				if (intent.getAction().equalsIgnoreCase(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)) {
					updateUIs(context);
				}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		//for(int i = 0; i < appWidgetIds.length; i++){
			//create remote view
			RemoteViews remoteViews = 
					new RemoteViews(context.getPackageName(),R.layout.smart_link_app_widget);
			updateUI(remoteViews);
			//if (m_blDeviceConnected) {

				//power intent
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("com.alcatel.smartlinkv3.business.openPage", 2);
				//create a pending intent
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery, pendingIntent);
				Intent ibBatteryintent = new Intent(context, MainActivity.class);
				ibBatteryintent.putExtra("com.alcatel.smartlinkv3.business.openPage", 2);
				//create a pending intent
				PendingIntent ibBatterypendingIntent = PendingIntent.getActivity(context, 1, ibBatteryintent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_charge, ibBatterypendingIntent);
				//SMS intent
				Intent smsIntent = new Intent(context,MainActivity.class);
				smsIntent.putExtra("com.alcatel.smartlinkv3.business.openPage", 1);
				PendingIntent smsPendingIntent = 
						PendingIntent.getActivity(context, 2, smsIntent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, smsPendingIntent);
				//signal intent
				Intent signalIntent = new Intent(context,MainActivity.class);
				PendingIntent signalPendingIntent = 
						PendingIntent.getActivity(context, 3, signalIntent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, signalPendingIntent);
			//}
			//wifi control
			Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			if (android.os.Build.VERSION.SDK_INT > 10) {
				wifiIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
			} else {
				wifiIntent = new Intent();
				ComponentName component = new ComponentName("com.android.settings",
						"com.android.settings.WirelessSettings");
				wifiIntent.setComponent(component);
				wifiIntent.setAction("android.intent.action.VIEW");
			}
			PendingIntent wifiPendingIntent = 
					PendingIntent.getActivity(context, 0, wifiIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.ib_widget_wifi, wifiPendingIntent);
			//
			//appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
			appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		//}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private void updateUI(RemoteViews remoteViews){
		m_blWifiConnected = DataConnectManager.getInstance().getWifiConnected();
		ConnectionStatus status = BusinessMannager.getInstance().getConnectStatus().m_connectionStatus;
		m_blInternetConnected = false;
		if (ConnectionStatus.Connected == status) {
			m_blInternetConnected = true;
		}
		m_blDeviceConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		updateSignal(remoteViews);
		updateWifi(remoteViews);
		updateInternet(remoteViews);
		updateSms(remoteViews);
		updateBattery(remoteViews);
	}

	private void updateSignal(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			SignalStrength eSignal = BusinessMannager.getInstance().getNetworkInfo().m_signalStrength;
			if (SignalStrength.Level_0 == eSignal) {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
			}else if (SignalStrength.Level_1 == eSignal) {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_1);				
			}else if (SignalStrength.Level_2 == eSignal) {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_2);				
			}else if (SignalStrength.Level_3 == eSignal) {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_3);				
			}else if (SignalStrength.Level_4 == eSignal) {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_4);				
			}else {
				remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
			}
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
		}
	}

	private void updateWifi(RemoteViews remoteViews){
		if (m_blWifiConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_wifi, R.drawable.widget_btn_wifi_4);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_on, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_off, View.GONE);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_wifi, R.drawable.widget_btn_wifi_0);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_off, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_on, View.GONE);
		}
	}

	private void updateInternet(RemoteViews remoteViews){
		if (m_blInternetConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_internet, 
					R.drawable.widget_internet_connected);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_on, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_off, View.GONE);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_internet, 
					R.drawable.widget_internet_disconnected);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_off, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_on, View.GONE);
		}
	}

	private void updateSms(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_active);
			int nUnreadNumber = BusinessMannager.getInstance().getNewSmsNumber();
			String strNumberString = "";
			if(nUnreadNumber > 0 && 10 > nUnreadNumber){
				strNumberString += nUnreadNumber;
			}else if (10 <= nUnreadNumber) {
				strNumberString = "";
			}
			remoteViews.setTextViewText(R.id.tv_widget_new_sms_number, strNumberString);
			if (0 < nUnreadNumber && 10 > nUnreadNumber) {
				remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
			}else if (10 <= nUnreadNumber) {
				remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
			}else {
				remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
			}
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_grey);
			remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
			remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
		}
	}

	private void updateBattery(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			int nProgress = BusinessMannager.getInstance().getBatteryInfo().getBatterLevel();
			int nState = BusinessMannager.getInstance().getBatteryInfo().getChargeState();
			if (0 != nState) {
				remoteViews.setProgressBar(R.id.pb_widget_battery, 100, nProgress, false);
				remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
			}else {
				remoteViews.setProgressBar(R.id.pb_widget_battery, 100, nProgress, false);
				remoteViews.setViewVisibility(R.id.pb_widget_battery, View.GONE);
				remoteViews.setViewVisibility(R.id.ib_widget_charge, View.VISIBLE);
			}
		}else {
			remoteViews.setProgressBar(R.id.pb_widget_battery, 100, 0, false);
			remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
		}
	}
	
	private void updateUIs(Context context){
		AppWidgetManager am = AppWidgetManager.getInstance(context);
		ComponentName com = new ComponentName(context.getPackageName(),
				"com.alcatel.smartlinkv3.appwidget.SmartLinkWidget");
		int[] nIds = am.getAppWidgetIds(com);
		for(int i = 0; i < nIds.length; i++){
			//create remote view
			RemoteViews remoteViews = 
					new RemoteViews(context.getPackageName(),R.layout.smart_link_app_widget);
			updateUI(remoteViews);
			//
			am.updateAppWidget(nIds[i], remoteViews);
		}
	}
}
