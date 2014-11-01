package com.alcatel.smartlinkv3.appwidget;

import com.alcatel.smartlinkv3.R;

import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingPowerSavingActivity;
import com.alcatel.smartlinkv3.ui.view.ViewIndex;

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
		//		if(intent.getAction().equals("cn.com.karl.widget.click")){  
		//			 Toast.makeText(context, "click widget calendar", 1).show(); 
		//        }
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		for(int i = 0; i < appWidgetIds.length; i++){
			//create remote view
			RemoteViews remoteViews = 
					new RemoteViews(context.getPackageName(),R.layout.smart_link_app_widget);
			updateUI(remoteViews);
			if (m_blDeviceConnected) {

				//power intent
				Intent intent = new Intent(context, SettingPowerSavingActivity.class);
				//create a pending intent
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_battery, pendingIntent);
				//SMS intent
				Intent smsIntent = new Intent(context,MainActivity.class);
				PendingIntent smsPendingIntent = 
						PendingIntent.getActivity(context, 0, smsIntent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, smsPendingIntent);
				//signal intent
				Intent signalIntent = new Intent(context,MainActivity.class);
				PendingIntent signalPendingIntent = 
						PendingIntent.getActivity(context, 0, signalIntent, 0);
				remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, signalPendingIntent);
			}
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
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private void updateUI(RemoteViews remoteViews){
		m_blWifiConnected = DataConnectManager.getInstance().getWifiConnected();
		m_blInternetConnected = DataConnectManager.getInstance().getMobileConnected();
		m_blDeviceConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		updateSignal(remoteViews);
		updateWifi(remoteViews);
		updateInternet(remoteViews);
		updateSms(remoteViews);
		updateBattery(remoteViews);
	}

	private void updateSignal(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_3);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
		}
	}

	private void updateWifi(RemoteViews remoteViews){
		if (m_blWifiConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_wifi, R.drawable.widget_btn_wifi_4);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_status, View.VISIBLE);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_wifi, R.drawable.widget_btn_wifi_0);
			remoteViews.setViewVisibility(R.id.iv_widget_wifi_status, View.GONE);
		}
	}

	private void updateInternet(RemoteViews remoteViews){
		if (m_blInternetConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_internet, R.drawable.widget_internet_connected);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_status, View.VISIBLE);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_internet, R.drawable.widget_internet_disconnected);
			remoteViews.setViewVisibility(R.id.iv_widget_internet_status, View.GONE);
		}
	}

	private void updateSms(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_active);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_grey);
		}
	}

	private void updateBattery(RemoteViews remoteViews){
		if (m_blDeviceConnected) {
			remoteViews.setImageViewResource(R.id.ib_widget_battery, R.drawable.widget_battery_full);
		}else {
			remoteViews.setImageViewResource(R.id.ib_widget_battery, R.drawable.widget_battery_empty);
		}
	}
}
