package com.alcatel.smartlinkv3.appwidget;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.UsageDataMode;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.SignalStrength;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SmartLinkWidget extends AppWidgetProvider
{

    private boolean m_blDeviceConnected = false;
    private boolean m_blWifiConnected = false;
    private boolean m_blInternetConnected = false;
    private final int HOME_PAGE = 1;
    private final int SMS_PAGE = 2;
    private final int BATTERY_PAGE = 3;
    private final int USAGE_PAGE = 4;
    private float scale;

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
	// TODO Auto-generated method stub
	super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context)
    {
	// TODO Auto-generated method stub
	super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context)
    {
	// TODO Auto-generated method stub
	super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
	// TODO Auto-generated method stub
	super.onReceive(context, intent);
	scale = context.getResources().getDisplayMetrics().density;
	if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)){ 
		updateUIs(context);
	}
	if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET))
	{
	    updateUIs(context);
	}

	if (intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_BATTERY_STATE))
	{
	    updateUIs(context);
	}

	if (intent.getAction().equalsIgnoreCase(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET))
	{
	    updateUIs(context);
	}

	if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET))
	{
	    updateUIs(context);
	}

	if (intent.getAction().equalsIgnoreCase(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET))
	{
	    updateUIs(context);
	}

	if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED"))
	{
	}

	connectControls(context);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
	// TODO Auto-generated method stub
	// for(int i = 0; i < appWidgetIds.length; i++){
	// create remote view
	scale = context.getResources().getDisplayMetrics().density;
	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_link_app_widget);
	updateUI(remoteViews, context);

	// power intent
	Intent intent = new Intent(context, MainActivity.class);
	// create a pending intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery, pendingIntent);
	// Intent ibBatteryintent = new Intent(context, MainActivity.class);

	// create a pending intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_charge, pendingIntent);
	// SMS intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", SMS_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, pendingIntent);
	// signal intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, pendingIntent);

	// internet intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 4, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);

	// usage plan
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", USAGE_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_usage, pendingIntent);

	// wifi control
	/*
	 * Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS); if
	 * (android.os.Build.VERSION.SDK_INT > 10) { wifiIntent = new
	 * Intent(android.provider.Settings.ACTION_WIFI_SETTINGS); } else {
	 * wifiIntent = new Intent(); ComponentName component = new
	 * ComponentName("com.android.settings",
	 * "com.android.settings.WirelessSettings");
	 * wifiIntent.setComponent(component);
	 * wifiIntent.setAction("android.intent.action.VIEW"); } PendingIntent
	 * wifiPendingIntent = PendingIntent.getActivity(context, 5, wifiIntent,
	 * 0); remoteViews.setOnClickPendingIntent(R.id.ib_widget_wifi,
	 * wifiPendingIntent);
	 */

	appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateUI(RemoteViews remoteViews, Context context)
    {
	m_blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
	ConnectionStatus status = BusinessMannager.getInstance().getConnectStatus().m_connectionStatus;
	m_blInternetConnected = false;
	if (ConnectionStatus.Connected == status)
	{
	    m_blInternetConnected = true;
	}
	m_blDeviceConnected = DataConnectManager.getInstance().getCPEWifiConnected();
	updateSignal(remoteViews);
	updateUsage(remoteViews, context);
	updateInternet(remoteViews);
	updateSms(remoteViews);
	updateBattery(remoteViews);
    }

    private void updateSignal(RemoteViews remoteViews)
    {
	if (m_blDeviceConnected)
	{
	    SignalStrength eSignal = BusinessMannager.getInstance().getNetworkInfo().m_signalStrength;
	    if (SignalStrength.Level_0 == eSignal)
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
	    } else if (SignalStrength.Level_1 == eSignal)
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_1);
	    } else if (SignalStrength.Level_2 == eSignal)
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_2);
	    } else if (SignalStrength.Level_3 == eSignal)
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_3);
	    } else if (SignalStrength.Level_4 == eSignal)
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_4);
	    } else
	    {
		remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
	    }
	} else
	{
	    remoteViews.setImageViewResource(R.id.ib_widget_signal, R.drawable.widget_signal_0);
	}
    }

    private void updateInternet(RemoteViews remoteViews)
    {
	if (m_blInternetConnected)
	{
	    remoteViews.setImageViewResource(R.id.ib_widget_internet, R.drawable.widget_internet_connected);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
	    // View.VISIBLE);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
	    // View.GONE);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
	    // View.GONE);
	} else
	{
	    remoteViews.setImageViewResource(R.id.ib_widget_internet, R.drawable.widget_internet_disconnected);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
	    // View.VISIBLE);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
	    // View.GONE);
	    // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
	    // View.GONE);
	}
    }

    private void updateSms(RemoteViews remoteViews)
    {
	if (m_blDeviceConnected)
	{
	    remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_active);
	    int nUnreadNumber = BusinessMannager.getInstance().getNewSmsNumber();
	    String strNumberString = "";
	    if (nUnreadNumber > 0 && 10 > nUnreadNumber)
	    {
		strNumberString += nUnreadNumber;
	    } else if (10 <= nUnreadNumber)
	    {
		strNumberString = "";
	    }
	    remoteViews.setTextViewText(R.id.tv_widget_new_sms_number, strNumberString);
	    if (0 < nUnreadNumber && 10 > nUnreadNumber)
	    {
		remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
	    } else if (10 <= nUnreadNumber)
	    {
		remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
	    } else
	    {
		remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
	    }
	} else
	{
	    remoteViews.setImageViewResource(R.id.ib_widget_sms, R.drawable.widget_sms_no_new_grey);
	    remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
	    remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
	}
    }

    private void updateBattery(RemoteViews remoteViews)
    {
	if (m_blDeviceConnected)
	{
	    int nProgress = BusinessMannager.getInstance().getBatteryInfo().getBatterLevel();
	    int nState = BusinessMannager.getInstance().getBatteryInfo().getChargeState();
	    if (0 != nState)
	    {
		remoteViews.setProgressBar(R.id.pb_widget_battery, 100, nProgress, false);
		remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
	    } else
	    {
		remoteViews.setProgressBar(R.id.pb_widget_battery, 100, nProgress, false);
		remoteViews.setViewVisibility(R.id.pb_widget_battery, View.GONE);
		remoteViews.setViewVisibility(R.id.ib_widget_charge, View.VISIBLE);
	    }
	} else
	{
	    remoteViews.setProgressBar(R.id.pb_widget_battery, 100, 0, false);
	    remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
	    remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
	}
    }

    private void updateUIs(Context context)
    {
	AppWidgetManager am = AppWidgetManager.getInstance(context);
	ComponentName com = new ComponentName(context.getPackageName(), "com.alcatel.smartlinkv3.appwidget.SmartLinkWidget");
	int[] nIds = am.getAppWidgetIds(com);
	for (int i = 0; i < nIds.length; i++)
	{
	    // create remote view
	    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_link_app_widget);
	    updateUI(remoteViews, context);
	    //
	    am.updateAppWidget(nIds[i], remoteViews);
	}
    }

    private void connectControls(Context context)
    {

	AppWidgetManager am = AppWidgetManager.getInstance(context);
	ComponentName com = new ComponentName(context.getPackageName(), "com.alcatel.smartlinkv3.appwidget.SmartLinkWidget");
	int[] nIds = am.getAppWidgetIds(com);
	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_link_app_widget);
	updateUI(remoteViews, context);
	// power intent
	Intent intent = new Intent(context, MainActivity.class);
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
	// create a pending intent
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery, pendingIntent);
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
	// create a pending intent
	pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_charge, pendingIntent);
	// SMS intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", SMS_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, pendingIntent);
	// signal intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, pendingIntent);

	// internet intent
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 4, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);

	// usage plan
	intent.putExtra("com.alcatel.smartlinkv3.business.openPage", USAGE_PAGE);
	pendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.ib_widget_usage, pendingIntent);

	// wifi control
	// Intent wifiIntent = new Intent(Settings.Action.);

	/*
	 * if (android.os.Build.VERSION.SDK_INT > 10)
	 * 
	 * { wifiIntent = new
	 * Intent(android.provider.Settings.ACTION_WIFI_SETTINGS); } else {
	 * wifiIntent = new Intent(); ComponentName component = new
	 * ComponentName("com.android.settings",
	 * "com.android.settings.WirelessSettings");
	 * wifiIntent.setComponent(component);
	 * wifiIntent.setAction("android.intent.action.VIEW"); } PendingIntent
	 * wifiPendingIntent = PendingIntent.getActivity(context, 5, wifiIntent,
	 * 0); remoteViews.setOnClickPendingIntent(R.id.ib_widget_wifi,
	 * wifiPendingIntent);
	 */
	am.updateAppWidget(nIds, remoteViews);
    }

    private void updateUsage(RemoteViews remoteViews, Context context)
    {
	
	UsageSettingModel statistic = BusinessMannager.getInstance().getUsageSettings();
	UsageRecordResult m_UsageRecordResult = BusinessMannager.getInstance().getUsageRecord();

	UsageDataMode usedUsageDataMode = CommonUtil.ConvertTrafficToUsageModelFromMB((long) m_UsageRecordResult.HUseData);
	UsageDataMode monthPlanUsageDataMode = CommonUtil.ConvertTrafficToUsageModelFromMB((long) statistic.HMonthlyPlan);

	double usedDataTmp = usedUsageDataMode.getUsageData();
	NumberFormat nFormat = NumberFormat.getInstance();
	nFormat.setMaximumFractionDigits(2);
	if (monthPlanUsageDataMode.getUsageUnit() > usedUsageDataMode.getUsageUnit())
	{
	    usedDataTmp =  (double) (Math.round((usedDataTmp / 1024)*100)/100.0);
	} else if (monthPlanUsageDataMode.getUsageUnit() < usedUsageDataMode.getUsageUnit() && monthPlanUsageDataMode.getUsageData() > 0)
	{
	    usedDataTmp = (double) (Math.round((usedDataTmp * 1024)*100)/100.0);
	}
	Log.d("widget.HUseData.record", m_UsageRecordResult.HUseData + "-----------------");
	Log.d("widget.HMonthlyPlan", statistic.HMonthlyPlan + "-----------------");
	Log.d("widget.HUnit", statistic.HUnit + "-----------------");
	Log.d("widget.HUsedData", statistic.HUsedData + "-----------------");
	Log.d("widget.HBillingDay", statistic.HBillingDay + "-----------------");
	Log.d("widget.usedDataTmp", usedDataTmp + "-----------------");
	Log.d("widget.usedUsageDataMode.getUsageData()", usedUsageDataMode.getUsageData() + "-----------------");
	Log.d("widget.monthPlanUsageDataMode.getUsageData()", monthPlanUsageDataMode.getUsageData() + "-----------------");
	Log.d("widget.monthPlanUsageDataModegetUsageUnit()", monthPlanUsageDataMode.getUsageUnit() + "-----------------");

	String dataUnitString = "";
	if (monthPlanUsageDataMode.getUsageData() == 0)
	{
	    dataUnitString = usedUsageDataMode.getUsageUnit() == 0 ? context.getResources().getString(R.string.home_MB) : context.getResources().getString(R.string.home_GB);
	} else
	{
	    dataUnitString = monthPlanUsageDataMode.getUsageUnit() == 0 ? context.getResources().getString(R.string.home_MB) : context.getResources().getString(R.string.home_GB);
	}
	remoteViews.setImageViewBitmap(R.id.ib_widget_usage, drawTrafficCircle(monthPlanUsageDataMode.getUsageData(), usedDataTmp, dataUnitString));
    }

    private Bitmap drawTrafficCircle(double monthDataPlan, double usedDataUsage, String dataUnitString)
    {
	//monthDataPlan = 0;
	//usedDataUsage = 0;
	
	int height = dipToPx(68);
	int width = dipToPx(68);
	float center = width / 2;
	float ringWidth = dipToPx(16);
	float innerCircle = dipToPx(20);

	Log.d("innerCircle", innerCircle + "");
	Bitmap circleBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	Canvas canvas = new Canvas(circleBitmap);
	Paint paint = new Paint();
	Rect bounds = new Rect();
	Rect unitBounds = new Rect();
	DecimalFormat transfToInteger = new DecimalFormat("0");
	String dataPlanToaltUsage = ""+transfToInteger.format(monthDataPlan);
	String dataUsedString = ""+transfToInteger.format(usedDataUsage);
	String dataPlanUnit = dataUnitString;
	int dataUsedAngle = 0;
	String usegeStatus = "Left";
	paint.setAntiAlias(true);
	paint.setStrokeWidth(dipToPx(8));
	paint.setTextSize(14);

	paint.setStyle(Style.STROKE);
	RectF rectArc = new RectF(center - (innerCircle - 2 + ringWidth / 2), center - (innerCircle - 2 + ringWidth / 2), center + (innerCircle - 2 + ringWidth / 2), center + (innerCircle - 2 + ringWidth / 2));
	
	
	if (monthDataPlan == 0)
	{
	    dataUsedAngle = 0;
	    usegeStatus = "Used";
	    dataPlanToaltUsage = dataUsedString;
	    //grey
	    paint.setARGB(255, 188, 187, 190);
	    canvas.drawArc(rectArc, 270, (360-dataUsedAngle), false, paint);
	    //blue
	    paint.setARGB(255, 0, 190, 245);
	    canvas.drawArc(rectArc, 0, dataUsedAngle, false, paint);
	    //text color
	    paint.setARGB(255, 5, 137, 207);
	} else if (monthDataPlan >= usedDataUsage)
	{
	    dataUsedAngle = Integer.parseInt(transfToInteger.format(360 * usedDataUsage / monthDataPlan));
	    //grey
	    paint.setARGB(255, 188, 187, 190);
	    canvas.drawArc(rectArc, 270, (360-dataUsedAngle), false, paint);
	    //blue
	    paint.setARGB(255, 0, 190, 245);
	    canvas.drawArc(rectArc, 270, -dataUsedAngle, false, paint);
	  //text color
	    paint.setARGB(255, 5, 137, 207);
	} else if (monthDataPlan < usedDataUsage)
	{
	    dataUsedAngle = Integer.parseInt(transfToInteger.format(360 * (usedDataUsage-monthDataPlan) / monthDataPlan));
	    dataUsedAngle = dataUsedAngle > 360 ? 360 : dataUsedAngle;
	    //grey
	    paint.setARGB(255, 188, 187, 190);
	    canvas.drawArc(rectArc, 270, (360-dataUsedAngle), false, paint);
	    //blue
	    paint.setARGB(255,249, 19, 19);
	    canvas.drawArc(rectArc, 270, -dataUsedAngle, false, paint);
	  //text color
	   
	}

	paint.setStyle(Style.FILL);
	//float width = paint.measureText(text);
	paint.getTextBounds(dataPlanToaltUsage, 0, dataPlanToaltUsage.length(), bounds);
	paint.getTextBounds(dataPlanUnit, 0, dataPlanUnit.length(), unitBounds);
	canvas.drawText(dataPlanToaltUsage, width / 2 - bounds.centerX() - unitBounds.width() / 2 + dipToPx(4), height / 2 - bounds.centerY() - bounds.height() / 2, paint);
	paint.setTextSize(9);
	canvas.drawText(dataPlanUnit, width / 2 - unitBounds.centerX() + bounds.width() / 2 + dipToPx(5), height / 2 - unitBounds.centerY() - bounds.height() / 2, paint);
	paint.getTextBounds(usegeStatus, 0, usegeStatus.length(), bounds);
	canvas.drawText(usegeStatus, width / 2 - bounds.centerX(), height / 2 - bounds.centerY() + bounds.height() * 3 / 4, paint);

	return circleBitmap;
    }

    /**
     * change dp to px
     */
    private int dipToPx(float dpValue)
    {
	return (int) (dpValue * scale + 0.5f);
    }

    /**
     * change px to dp
     */
    private int pxToDip(float pxValue)
    {
	return (int) (pxValue / scale + 0.5f);
    }
}
