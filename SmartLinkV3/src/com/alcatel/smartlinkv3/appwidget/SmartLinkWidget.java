package com.alcatel.smartlinkv3.appwidget;

import com.alcatel.smartlinkv3.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SmartLinkWidget extends AppWidgetProvider {

	private String[] months={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aut","Sep","Oct","Nov","Dec"};  
    private String[] days={"Sun","Mon","Tue","Wen","Thu","Fri","Sat"};
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
			 Toast.makeText(context, "click widget calendar", 1).show(); 
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
