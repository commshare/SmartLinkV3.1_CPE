package com.alcatel.smartlinkv3.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.UsageDataMode;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.model.WanConnectStatusModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.OVER_DISCONNECT_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SignalStrength;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
//
public class SmartLinkWidget extends AppWidgetProvider {

  private boolean m_blDeviceConnected = false;
  private boolean m_blWifiConnected = false;
  private boolean m_blInternetConnected = false;
  private final int HOME_PAGE = 1;
  private final int SMS_PAGE = 2;
  private final int BATTERY_PAGE = 3;
  private final int USAGE_PAGE = 4;
  private float scale;
  private float fontScale;
  private final String tagString = "smartlink.widget.smartLinkWidget";

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
    scale = context.getResources().getDisplayMetrics().density;
    fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
    if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
      updateUIs(context);
    }
    if (intent.getAction()
        .equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
      updateUIs(context);
    }

    if (intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_BATTERY_STATE)) {
      updateUIs(context);
    }

    if (intent.getAction().equalsIgnoreCase(
        MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET)) {
      updateUIs(context);
    }

    if (intent.getAction().equalsIgnoreCase(
        MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET)) {
      updateUIs(context);
    }

    if (intent.getAction().equalsIgnoreCase(
        MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)) {
      updateUIs(context);
    }
    if (intent.getAction().equalsIgnoreCase(MessageUti.WIDGET_GET_INTERNET_SWITCH)) {
      switchInternetConnection(context);
    }

    if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {

    }

    connectControls(context);
    // Log.d(tagString, "action---------" + intent.getAction());

  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {
    // TODO Auto-generated method stub
    // for(int i = 0; i < appWidgetIds.length; i++){
    // create remote view
    scale = context.getResources().getDisplayMetrics().density;
    fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
        R.layout.smart_link_app_widget);
    updateUI(remoteViews, context);

    // power intent
    Intent intent = new Intent(context, HomeActivity.class);
    // create a pending intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery, pendingIntent);
    remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery_low, pendingIntent);
    // Intent ibBatteryintent = new Intent(context, MainActivity.class);

    // create a pending intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 1, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_charge, pendingIntent);
    // SMS intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", SMS_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 2, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, pendingIntent);
    // signal intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 3, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, pendingIntent);

    // usage plan
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", USAGE_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 4, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_usage, pendingIntent);

    boolean m_blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
    UserLoginStatus status = BusinessManager.getInstance().getLoginStatus();
    if (m_blWifiConnected && status == UserLoginStatus.LOGIN) {
      Intent localIntent = new Intent(MessageUti.WIDGET_GET_INTERNET_SWITCH);
      pendingIntent = PendingIntent.getBroadcast(context, 5, localIntent,
          PendingIntent.FLAG_CANCEL_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);
      // Log.d(tagString, "status----------------" +
      // status+"--wifi disconnect:"+m_blWifiConnected);
    } else {
      intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
      pendingIntent = PendingIntent.getActivity(context, 5, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);
      // Log.d(tagString, "status----------------" +
      // status+"--wifi connect:"+m_blWifiConnected);
    }

    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  private void updateUI(RemoteViews remoteViews, Context context) {
    ConnectionStatus status = BusinessManager.getInstance().getWanConnectStatus().m_connectionStatus;
    m_blInternetConnected = false;
    if (ConnectionStatus.Connected == status) {
      m_blInternetConnected = true;
    }
    m_blDeviceConnected = DataConnectManager.getInstance().getCPEWifiConnected();
    updateSignal(remoteViews);
    updateUsage(remoteViews, context);
    updateInternet(remoteViews);
    updateSms(remoteViews);
    updateBattery(remoteViews);
  }

  private void updateSignal(RemoteViews remoteViews) {
    if (m_blDeviceConnected) {
      SignalStrength eSignal = BusinessManager.getInstance().getNetworkInfo().m_signalStrength;
      if (SignalStrength.Level_0 == eSignal) {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_0);
      } else if (SignalStrength.Level_1 == eSignal) {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_1);
      } else if (SignalStrength.Level_2 == eSignal) {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_2);
      } else if (SignalStrength.Level_3 == eSignal) {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_3);
      } else if (SignalStrength.Level_4 == eSignal) {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_4);
      } else {
        remoteViews.setImageViewResource(R.id.ib_widget_signal,
            R.drawable.widget_signal_0);
      }
    } else {
      remoteViews.setImageViewResource(R.id.ib_widget_signal,
          R.drawable.widget_signal_0);
    }
  }

  private void updateInternet(RemoteViews remoteViews) {
    if (m_blInternetConnected) {
      remoteViews.setImageViewResource(R.id.ib_widget_internet,
          R.drawable.widget_internet_connected);
      // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
      // View.VISIBLE);
      // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
      // View.GONE);
      // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
      // View.GONE);
    } else {
      remoteViews.setImageViewResource(R.id.ib_widget_internet,
          R.drawable.widget_internet_disconnected);

      // remoteViews.setImageViewResource(R.id.ib_widget_internet,
      // R.drawable.widget_internet);

      // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
      // View.VISIBLE);
      // remoteViews.setViewVisibility(R.id.iv_widget_internet_on,
      // View.GONE);
      // remoteViews.setViewVisibility(R.id.iv_widget_internet_off,
      // View.GONE);
    }
  }

  private void updateSms(RemoteViews remoteViews) {
    if (m_blDeviceConnected) {
      remoteViews.setImageViewResource(R.id.ib_widget_sms,
          R.drawable.widget_sms_no_new_active);
      int nUnreadNumber = BusinessManager.getInstance().getNewSmsNumber();
      String strNumberString = "";
      if (nUnreadNumber > 0 && 10 > nUnreadNumber) {
        strNumberString += nUnreadNumber;
      } else if (10 <= nUnreadNumber) {
        strNumberString = "";
      }
      remoteViews.setTextViewText(R.id.tv_widget_new_sms_number, strNumberString);
      if (0 < nUnreadNumber && 10 > nUnreadNumber) {
        remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
      } else if (10 <= nUnreadNumber) {
        remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
      } else {
        remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
      }
    } else {
      remoteViews.setImageViewResource(R.id.ib_widget_sms,
          R.drawable.widget_sms_no_new_grey);
      remoteViews.setViewVisibility(R.id.tv_widget_new_sms_number, View.GONE);
      remoteViews.setViewVisibility(R.id.iv_widget_new_sms_plus9, View.GONE);
    }
  }

  private void updateBattery(RemoteViews remoteViews) {
    if (m_blDeviceConnected) {
      int nProgress = BusinessManager.getInstance().getBatteryInfo().getBatterLevel();
      int nState = BusinessManager.getInstance().getBatteryInfo().getChargeState();
      if (0 != nState) {
      	if (nProgress > 20)
				{
      		remoteViews.setProgressBar(R.id.pb_widget_battery, 100, nProgress, false);
      		remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
      		remoteViews.setViewVisibility(R.id.pb_widget_battery_low, View.GONE);
      		remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
				}else {
					remoteViews.setProgressBar(R.id.pb_widget_battery_low, 100, nProgress, false);
					remoteViews.setViewVisibility(R.id.pb_widget_battery_low, View.VISIBLE);
      		remoteViews.setViewVisibility(R.id.pb_widget_battery, View.GONE);
      		remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
				}
      } else {
        remoteViews.setProgressBar(R.id.pb_widget_battery, 100, 0, false);
        remoteViews.setViewVisibility(R.id.pb_widget_battery, View.GONE);
      	remoteViews.setViewVisibility(R.id.pb_widget_battery_low, View.GONE);
      	remoteViews.setViewVisibility(R.id.ib_widget_charge, View.VISIBLE);
//      	remoteViews.setViewVisibility(R.id.ib_widget_charge_front, View.VISIBLE);
      }
    } else {
      remoteViews.setProgressBar(R.id.pb_widget_battery, 100, 0, false);
      remoteViews.setViewVisibility(R.id.pb_widget_battery, View.VISIBLE);
      remoteViews.setViewVisibility(R.id.ib_widget_charge, View.GONE);
    	remoteViews.setViewVisibility(R.id.pb_widget_battery_low, View.GONE);
    }
  }

  private void updateUIs(Context context) {
    AppWidgetManager am = AppWidgetManager.getInstance(context);
    ComponentName com = new ComponentName(context.getPackageName(),
        "com.alcatel.smartlinkv3.appwidget.SmartLinkWidget");
    int[] nIds = am.getAppWidgetIds(com);
    for (int i = 0; i < nIds.length; i++) {
      // create remote view
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
          R.layout.smart_link_app_widget);
      updateUI(remoteViews, context);
      //
      am.updateAppWidget(nIds[i], remoteViews);
    }
  }

  private void connectControls(Context context) {

    AppWidgetManager am = AppWidgetManager.getInstance(context);
    ComponentName com = new ComponentName(context.getPackageName(),
        "com.alcatel.smartlinkv3.appwidget.SmartLinkWidget");
    int[] nIds = am.getAppWidgetIds(com);
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
        R.layout.smart_link_app_widget);
    updateUI(remoteViews, context);
    // power intent
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
    // create a pending intent
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery, pendingIntent);
    remoteViews.setOnClickPendingIntent(R.id.pb_widget_battery_low, pendingIntent);
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", BATTERY_PAGE);
    // create a pending intent
    pendingIntent = PendingIntent.getActivity(context, 1, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_charge, pendingIntent);
    // SMS intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", SMS_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 2, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_sms, pendingIntent);
    // signal intent
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 3, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_signal, pendingIntent);

    // usage plan
    intent.putExtra("com.alcatel.smartlinkv3.business.openPage", USAGE_PAGE);
    pendingIntent = PendingIntent.getActivity(context, 4, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.ib_widget_usage, pendingIntent);

    boolean blCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
    UserLoginStatus status = BusinessManager.getInstance().getLoginStatus();
    if (blCPEWifiConnected && status == UserLoginStatus.LOGIN) {
      Intent localIntent = new Intent(MessageUti.WIDGET_GET_INTERNET_SWITCH);
      pendingIntent = PendingIntent.getBroadcast(context, 5, localIntent,
          PendingIntent.FLAG_CANCEL_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);
      // Log.d(tagString, "status----------------" +
      // status+"--wifi disconnect:"+blCPEWifiConnected);
    } else {
      intent.putExtra("com.alcatel.smartlinkv3.business.openPage", HOME_PAGE);
      pendingIntent = PendingIntent.getActivity(context, 5, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.ib_widget_internet, pendingIntent);
      // Log.d(tagString, "status----------------" +
      // status+"--wifi disconnect:"+blCPEWifiConnected);
    }

    am.updateAppWidget(nIds, remoteViews);
  }

  private void updateUsage(RemoteViews remoteViews, Context context) {

    UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();
    UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance()
        .getUsageRecord();

    UsageDataMode usedUsageDataMode = CommonUtil
        .ConvertTrafficToUsageModelFromMB((long) m_UsageRecordResult.HUseData);
    UsageDataMode monthPlanUsageDataMode = CommonUtil
        .ConvertTrafficToUsageModelFromMB((long) statistic.HMonthlyPlan);

    double usedDataTmp = usedUsageDataMode.getUsageData();
    NumberFormat nFormat = NumberFormat.getInstance();
    nFormat.setMaximumFractionDigits(2);
    if (monthPlanUsageDataMode.getUsageUnit() > usedUsageDataMode.getUsageUnit()) {
      usedDataTmp = (double) (Math.round((usedDataTmp / 1024) * 100) / 100.0);
    } else if (monthPlanUsageDataMode.getUsageUnit() < usedUsageDataMode.getUsageUnit()
        && monthPlanUsageDataMode.getUsageData() > 0) {
      usedDataTmp = (double) (Math.round((usedDataTmp * 1024) * 100) / 100.0);
    }

    String dataUnitString = "";
    if (monthPlanUsageDataMode.getUsageData() == 0) {
      dataUnitString = usedUsageDataMode.getUsageUnit() == 0 ?
          context.getResources().getString(R.string.home_MB) :
          context.getResources().getString(R.string.home_GB);
    } else {
      dataUnitString = monthPlanUsageDataMode.getUsageUnit() == 0 ? 
          context.getResources().getString(R.string.home_MB) :
          context.getResources().getString(R.string.home_GB);
    }
    remoteViews.setImageViewBitmap(R.id.ib_widget_usage,
            drawTrafficCircle(monthPlanUsageDataMode.getUsageData(),
            usedDataTmp,
            dataUnitString,context));
  }

  private Bitmap drawTrafficCircle(double monthDataPlan, double usedDataUsage,
      String dataUnitString,Context context) {
    // monthDataPlan = 0;
    // usedDataUsage = 0;

    int height = dipToPx(64);
    int width = dipToPx(64);
    float center = width / 2;
    float borderRingWidth = dipToPx(3);
    float ringWidth = dipToPx(7);
    //float innerCircle = height/2 - ringWidth/2 - maxRingWidth/2;
    

    // Log.d(tagString, "innerCircle" + ":" + innerCircle + "");
    Bitmap circleBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(circleBitmap);
    Paint paint = new Paint();
    Rect bounds = new Rect();
    Rect unitBounds = new Rect();
    DecimalFormat transfToInteger = new DecimalFormat("0");
    DecimalFormat transfToDouble = new DecimalFormat("0.0");
    String dataLeft = "" +  transfToDouble.format(Math.abs(monthDataPlan - usedDataUsage));
    String dataPlanToaltUsage = "" + transfToInteger.format(monthDataPlan);
    String dataUsed = "" + transfToDouble.format(usedDataUsage);
    String dataUsageShowStr = dataLeft;
    String dataPlanUnit = dataUnitString;
    int dataUsedAngle = 0;
    String usegeStatus = context.getResources().getString(R.string.widget_usage_left);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(ringWidth);
    paint.setTextSize(spTopx(12));
    if (dataUsageShowStr.length() > 5)
		{
    	 paint.setTextSize(spTopx(10));
		}

    paint.setStyle(Style.STROKE);
    //StrokeWidth will  overstep ringwidth/2
    RectF rectArc = new RectF(0 + ringWidth/2 + borderRingWidth,0 + ringWidth/2 + borderRingWidth,width - ringWidth/2 - borderRingWidth,width - ringWidth/2 - borderRingWidth);

    if (monthDataPlan == 0) {
      dataUsedAngle = 0;
      usegeStatus =  context.getResources().getString(R.string.widget_usage_used);
      dataUsageShowStr = dataUsed;
      if (dataUsageShowStr.length() > 5)
  		{
      	 paint.setTextSize(spTopx(10));
  		}
      // grey
      paint.setARGB(255, 188, 187, 190);
      canvas.drawArc(rectArc, 270, (360 - dataUsedAngle), false, paint);
      // blue
      paint.setARGB(255, 0, 190, 245);
      canvas.drawArc(rectArc, 0, dataUsedAngle, false, paint);
      // text color
      paint.setARGB(255, 5, 137, 207);
    } else if (monthDataPlan >= usedDataUsage) {
      dataUsedAngle = Integer.parseInt(transfToInteger.format(360 * usedDataUsage
          / monthDataPlan));
      // grey
      paint.setARGB(255, 188, 187, 190);
      canvas.drawArc(rectArc, 270, (360 - dataUsedAngle), false, paint);
      // blue
      paint.setARGB(255, 0, 190, 245);
      canvas.drawArc(rectArc, 270, -dataUsedAngle, false, paint);
      // text color
      paint.setARGB(255, 5, 137, 207);
    } else if (monthDataPlan < usedDataUsage) {
    	 usegeStatus =  context.getResources().getString(R.string.widget_usage_excess);
      dataUsedAngle = Integer.parseInt(transfToInteger.format(360
          * (usedDataUsage - monthDataPlan) / monthDataPlan));
      dataUsedAngle = dataUsedAngle > 360 ? 360 : dataUsedAngle;
      // grey
      paint.setARGB(255, 188, 187, 190);
      canvas.drawArc(rectArc, 270, (360 - dataUsedAngle), false, paint);
      // blue
      paint.setARGB(255, 249, 19, 19);
      canvas.drawArc(rectArc, 270, -dataUsedAngle, false, paint);
      // text color

    }

    paint.setStyle(Style.FILL);
    // float width = paint.measureText(text);
    paint.getTextBounds(dataUsageShowStr, 0, dataUsageShowStr.length(), bounds);
    paint.getTextBounds(dataPlanUnit, 0, dataPlanUnit.length(), unitBounds);
    canvas.drawText(dataUsageShowStr,
        width / 2 - bounds.centerX() - unitBounds.width() / 2 + dipToPx(2),
        height / 2 - bounds.centerY() - bounds.height() / 2, 
        paint);
    paint.setTextSize(spTopx(8));
    canvas.drawText(dataPlanUnit, 
            width / 2 - unitBounds.centerX() + bounds.width() / 2 + dipToPx(3),
            height / 2 - unitBounds.centerY() - bounds.height() / 2, 
            paint);
    paint.getTextBounds(usegeStatus, 0, usegeStatus.length(), bounds);
    canvas.drawText(usegeStatus, width / 2 - bounds.centerX(),
        height / 2 - bounds.centerY() + bounds.height() * 3 / 4, paint);

    return circleBitmap;
  }

  public void switchInternetConnection(Context context) {
    // Log.d(tagString+".switch:",
    // "-------------action---------internet switch open");
    UsageSettingModel settings = BusinessManager.getInstance().getUsageSettings();
    UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance()
        .getUsageRecord();
    WanConnectStatusModel internetConnState = BusinessManager.getInstance()
        .getWanConnectStatus();
    if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected
        || internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
      if (settings.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Enable
          && m_UsageRecordResult.MonthlyPlan > 0) {
        if ((m_UsageRecordResult.HUseData + m_UsageRecordResult.RoamUseData) >= m_UsageRecordResult.MonthlyPlan) {
          // show warning dialog
          // m_connectWarningDialog.showDialog();
          String msgRes = context.getString(R.string.home_usage_over_redial_message);
          Toast.makeText(context, msgRes, Toast.LENGTH_LONG).show();
          return;
        }
      }
    }

    if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
        || internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
      // Log.d(tagString+".switch:", "switch---------disconnect");
      BusinessManager.getInstance().sendRequestMessage(
          MessageUti.WAN_DISCONNECT_REQUSET, null);
    } else {
      // Log.d(tagString+".switch:", "switch---------connect");
      BusinessManager.getInstance().sendRequestMessage(MessageUti.WAN_CONNECT_REQUSET,
          null);
    }
    // RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
    // R.layout.smart_link_app_widget);
    // remoteViews.setImageViewResource(R.id.ib_widget_internet,
    // R.drawable.widget_internet);
  }

  /**
   * change dp to px
   */
  private int dipToPx(double dpValue) {
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * change px to dp
   */
  private int pxToDip(float pxValue) {
    return (int) (pxValue / scale + 0.5f);
  }
  
  private int pxToSp(float pxValue){
  	return (int) (pxValue / fontScale + 0.5f); 
  }
  public  int spTopx(float spValue) { 
    return (int) (spValue * fontScale + 0.5f); 
  } 
}
