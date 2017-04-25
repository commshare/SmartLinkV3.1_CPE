package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity{
	protected ActivityBroadcastReceiver m_msgReceiver = null;
	protected ActivityBroadcastReceiver m_msgReceiver2 = null;
	private ArrayList<Dialog> m_dialogManager = new ArrayList<Dialog>();
	protected boolean m_bNeedBack = true;//whether need to back main activity.
	public int alertValue;
	private int alertCount;

	protected void addToDialogManager(Dialog dialog) {
		m_dialogManager.add(dialog);
	}
	
	private void dismissAllDialog() {
		for(int i = 0;i < m_dialogManager.size();i++) {
			m_dialogManager.get(i).dismiss();
		}
	}
	@Override
	protected void onResume() {	
       	super.onResume();
		if (m_msgReceiver == null)
       		m_msgReceiver = new ActivityBroadcastReceiver();
       	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));  
    	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)); 
    	if (m_msgReceiver2 == null)
    		m_msgReceiver2 = new ActivityBroadcastReceiver();
    	if(!FeatureVersionManager.getInstance().isSupportForceLogin())
		{
    		this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
		}
    	this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_HEARTBEAT_REQUEST));
    	this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_COMMON_ERROR_32604_REQUEST));
    	
    	showActivity(this);
    	if(!FeatureVersionManager.getInstance().isSupportForceLogin())
		{
    		backMainActivityOnResume(this);
		}

		showIfAlertDialog();
	}

	private void showIfAlertDialog() {
		int usageRecord;
		int usageSetting;
		UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
		UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();

		String usageRecordSt = CommonUtil.ConvertTrafficToStringFromMB(this, m_UsageRecordResult.HUseData);
		if (usageRecordSt.contains("MB")){
			usageRecordSt = usageRecordSt.substring(0, usageRecordSt.lastIndexOf('M'));
			usageRecord = ((int) Double.parseDouble(usageRecordSt));
		} else {
			usageRecordSt = usageRecordSt.substring(0, usageRecordSt.lastIndexOf('G'));
			usageRecord = 1024 * ((int) Double.parseDouble(usageRecordSt));
		}

		String usageSettingSt = CommonUtil.ConvertTrafficToStringFromMB(this, statistic.HMonthlyPlan);
		if (usageSettingSt.contains("MB")){
			usageSettingSt = usageSettingSt.substring(0, usageSettingSt.lastIndexOf('M'));
			usageSetting = ((int) Double.parseDouble(usageSettingSt));
		} else {
			usageSettingSt = usageSettingSt.substring(0, usageSettingSt.lastIndexOf('G'));
			usageSetting = 1024 * ((int) Double.parseDouble(usageSettingSt));
		}

		alertValue = SharedPrefsUtil.getInstance(this).getInt(UsageSettingActivity.SETTING_USAGE_ALERT_VALUE, 0);
		if (usageSetting != 0 && alertCount == 0){
			if ((100 * usageRecord / usageSetting) >= alertValue){
				new AlertDialog.Builder(this)
						.setTitle("WARNING")
						.setMessage("traffic over usage restrictions")
						.setPositiveButton("true", null)
						.show();
			}
			alertCount++;
		}
	}

	@Override
	protected void onPause() {	
    	super.onPause();
    	try {
			if (m_msgReceiver != null)
    			unregisterReceiver(m_msgReceiver);
			if (m_msgReceiver2 != null)
				unregisterReceiver(m_msgReceiver2);
    		//checkLogin();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

	public static View getRootView(Activity activity)
	{
		return ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
    		this.unregisterReceiver(m_msgReceiver2); 		
    		//checkLogin();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	}

	protected void onBroadcastReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
		Boolean ok = response != null && response.isOk();
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
    		showActivity(context);
    	}else if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			if(ok) {
				back2MainActivity(context);
			}
		}else if(intent.getAction().equals(MessageUti.USER_LOGOUT_REQUEST)) {
			if(ok && isForeground(this)) {
				backMainActivity(context);
			}
		}else if(intent.getAction().equals(MessageUti.USER_HEARTBEAT_REQUEST)){
			if(response.isValid() && response.getErrorCode().equals(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
				backMainActivity(context);
				kickoffLogout();
			}
		}else if(intent.getAction().equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)){
			if(response.getErrorCode().equals(ErrorCode.ERR_COMMON_ERROR_32604) && isForeground(this)) {
				backMainActivity(context);
				kickoffLogout();
			}
		}
	}
	
	private class ActivityBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {	    	
	    	onBroadcastReceive(context,intent);
        }  	
	}
	
	private void back2MainActivity(Context context) {
		if(m_bNeedBack)
			return;
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		SimStatusModel sim = BusinessManager.getInstance().getSimStatus();
		
		if(bCPEWifiConnected && sim.m_SIMState != SIMState.Accessable) {
//			String strInfo = getString(R.string.home_sim_not_accessible);
//			Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
			if(!this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName())) {
				dismissAllDialog();	
				Intent intent = new Intent(context, MainActivity.class);	
				context.startActivity(intent);
				finish();
			}
		}
	}
	
	private void backMainActivityOnResume(Context context) {
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
		
		if(bCPEWifiConnected && m_loginStatus != UserLoginStatus.LOGIN) {
			if(!this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName())) {
				Intent intent = new Intent(context, MainActivity.class);	
				context.startActivity(intent);
				finish();
			}else {
				Intent intent2= new Intent(MainActivity.PAGE_TO_VIEW_HOME);
				context.sendBroadcast(intent2);
			}
		}
	}
	
	private void backMainActivity(Context context) {
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
		
		if(bCPEWifiConnected && m_loginStatus != UserLoginStatus.LOGIN) {
			dismissAllDialog();
			if(!this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName())) {
				Intent intent = new Intent(context, MainActivity.class);	
				context.startActivity(intent);
				finish();
			}else {
				Intent intent2= new Intent(MainActivity.PAGE_TO_VIEW_HOME);
				context.sendBroadcast(intent2);
			}
		}
	}
	
	private void showActivity(Context context) {
	
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();		
		
		if(bCPEWifiConnected &&
				this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())	)
			{
			dismissAllDialog();	
			Intent intent = new Intent(context, MainActivity.class);		
			context.startActivity(intent);
			finish();									
			
		}else if(!bCPEWifiConnected && !this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())) {
			dismissAllDialog();		
			Intent intent = new Intent(context, RefreshWifiActivity.class);
			context.startActivity(intent);	
			finish();
		}	
	}
	
	public boolean isForeground(Context context) {

	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
	           }
	    }
	    return false;
	}
	
	public void kickoffLogout() {
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
		if (m_loginStatus != null && m_loginStatus == UserLoginStatus.Logout) {
//			MainActivity.setKickoffLogoutFlag(true);
			String strInfo = getString(R.string.login_kickoff_logout_successful);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();	
		}
	}

//	private void checkLogin()
//	{
//    	PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);  
//    	boolean bScreenOn = pm.isScreenOn();
//    	if(bScreenOn == false) {
//    		LoginDialog.setAlreadyLogin(false);
//    	}
//		
//    	ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);    	
//    	ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//    	if(!cn.getPackageName().equalsIgnoreCase("com.alcatel.cpe")) {
//    		LoginDialog.setAlreadyLogin(false);
//    	}  
//    	//back键退出程序界面的处理
//    	else if(cn.getPackageName().equalsIgnoreCase("com.alcatel.cpe") 
//    			&&  cn.getClassName().equalsIgnoreCase("com.alcatel.cpe.ui.activity.LoadingActivity"))
//    	{
//    		LoginDialog.setAlreadyLogin(false);
//    	}
//	   
//	}
}
