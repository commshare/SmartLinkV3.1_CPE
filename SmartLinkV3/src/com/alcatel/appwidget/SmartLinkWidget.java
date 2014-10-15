package com.alcatel.appwidget;

import com.alcatel.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SmartLinkWidget extends AppWidgetProvider {

	private String[] months={"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};  
    private String[] days={"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
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
		if(intent.getAction().equals("cn.com.karl.widget.click")){  
            Toast.makeText(context, "点击了widget日历", 1).show();  
        }
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.smart_link_app_widget);  
        Time time=new Time();  
        time.setToNow();  
        String month=time.year+" "+months[time.month];  
        remoteViews.setTextViewText(R.id.txtDay, new Integer(time.monthDay).toString());  
        remoteViews.setTextViewText(R.id.txtMonth, month);  
        remoteViews.setTextViewText(R.id.txtWeekDay, days[time.weekDay]);  
        Intent intent=new Intent("cn.com.karl.widget.click");  
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, intent, 0);  
        remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);  
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
