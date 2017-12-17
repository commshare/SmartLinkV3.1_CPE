package com.alcatel.wifilink.ui.activity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.DataConnectManager;
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.common.ENUM.SIMState;
import com.alcatel.wifilink.common.ENUM.UserLoginStatus;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragmentActivity extends FragmentActivity{
	protected ActivityBroadcastReceiver m_msgReceiver;
	protected ActivityBroadcastReceiver m_msgReceiver2;
	private ArrayList<Dialog> m_dialogManager = new ArrayList<Dialog>();
	protected boolean m_bNeedBack = true;//whether need to back main activity.

	
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
       	m_msgReceiver = new ActivityBroadcastReceiver();
		this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
		this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));

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
			backHomeActivityOnResume(this);
		}
	}
	
	@Override
	protected void onPause() {	
    	super.onPause();
    	try {
    		this.unregisterReceiver(m_msgReceiver); 		
    		//checkLogin();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
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
				back2HomeActivity(context);
			}
		}else if(intent.getAction().equals(MessageUti.USER_LOGOUT_REQUEST)) {
			if(ok && isForeground(this)) {
				backHomeActivity(context);
			}
		}else if(intent.getAction().equals(MessageUti.USER_HEARTBEAT_REQUEST)){
			if(response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
				backHomeActivity(context);
				kickoffLogout();
			}
		}else if(intent.getAction().equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)){
			if(response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_COMMON_ERROR_32604) && isForeground(this)) {
				backHomeActivity(context);
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

	private void back2HomeActivity(Context context) {
		if(m_bNeedBack == false)
			return;
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		SimStatusModel sim = BusinessManager.getInstance().getSimStatus();

		if(bCPEWifiConnected == true && sim.m_SIMState != SIMState.Accessable) {
//			String strInfo = getString(R.string.home_sim_not_accessible);
//			Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
			if(this.getClass().getName().equalsIgnoreCase(HomeActivity.class.getName()) == false) {
				dismissAllDialog();
				Intent intent = new Intent(context, HomeActivity.class);
				context.startActivity(intent);
				finish();
			}
		}
	}

	private void backHomeActivityOnResume(Context context) {
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();

		if(bCPEWifiConnected == true && m_loginStatus != UserLoginStatus.LOGIN) {
			if(this.getClass().getName().equalsIgnoreCase(HomeActivity.class.getName()) == false) {
				Intent intent = new Intent(context, HomeActivity.class);
				context.startActivity(intent);
				finish();
			}else {
				Intent intent2= new Intent(HomeActivity.PAGE_TO_VIEW_HOME);
				context.sendBroadcast(intent2);
			}
		}
	}

	private void backHomeActivity(Context context) {
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();

		if(bCPEWifiConnected == true && m_loginStatus != UserLoginStatus.LOGIN) {
			dismissAllDialog();
			if(this.getClass().getName().equalsIgnoreCase(HomeActivity.class.getName()) == false) {
				Intent intent = new Intent(context, HomeActivity.class);
				context.startActivity(intent);
				finish();
			}else {
				Intent intent2= new Intent(HomeActivity.PAGE_TO_VIEW_HOME);
				context.sendBroadcast(intent2);
			}
		}
	}

	private void showActivity(Context context) {

		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();

		if(bCPEWifiConnected == true &&
				this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())	)
			{
			dismissAllDialog();
			Intent intent = new Intent(context, HomeActivity.class);
			context.startActivity(intent);
			finish();

		}else if(bCPEWifiConnected == false && !this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())) {
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
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
	                	return true;
	                }else{
	                    return false;
	                }
	           }
	    }
	    return false;
	}

	public void kickoffLogout() {
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
		if (m_loginStatus != null && m_loginStatus == UserLoginStatus.Logout) {
//			HomeActivity.setKickoffLogoutFlag(true);
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
//    	ComponentName cn = am.getRunningTasks(1).getInstant(0).topActivity;
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
