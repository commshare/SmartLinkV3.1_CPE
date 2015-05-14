package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.PowerManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public abstract class BaseActivity extends Activity{
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
    	this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
    	this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGIN_REQUEST));

    	if(CPEConfig.getInstance().getAutoLoginFlag())
		{
    		UserLoginStatus status = BusinessMannager.getInstance()				
					.getLoginStatus();	
    		if (status == UserLoginStatus.Logout) {
			DataValue data = new DataValue();
			data.addParam("user_name", CPEConfig.getInstance().getLoginUsername());
			data.addParam("password", CPEConfig.getInstance().getLoginPassword());
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.USER_LOGIN_REQUEST, data);
			MainActivity.setAutoProFlag(true);
    		}
		}

    	//showActivity(this);
    	//back2MainActivity(this);
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
		String msgRes = null;
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
    		//showActivity(context);
    	}else if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				//back2MainActivity(context);
			}
		}else if(intent.getAction().equals(MessageUti.USER_LOGOUT_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				//backMainActivity(context);
			}
		}else if (intent.getAction().equalsIgnoreCase(
				MessageUti.USER_LOGIN_REQUEST)) {			
			MainActivity.setAutoProFlag(false);
			Intent intent3= new Intent(MainActivity.AUTO_LOGIN_RETURN_STOP_SHOW_PROGRESS);
			context.sendBroadcast(intent3);
		
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult&& strErrorCode.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)){
				CPEConfig.getInstance().setAutoLoginFlag(false);
			}else if(BaseResponse.RESPONSE_OK == nResult&& strErrorCode.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)){
				msgRes = this.getString(R.string.login_login_time_used_out_msg);
				Toast.makeText(this, msgRes,Toast.LENGTH_SHORT).show();
				CPEConfig.getInstance().setAutoLoginFlag(false);
			}else if(BaseResponse.RESPONSE_OK == nResult&& strErrorCode.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD)){
				msgRes = this.getString(R.string.login_psd_error_msg);
				Toast.makeText(this, msgRes,Toast.LENGTH_SHORT).show();
				CPEConfig.getInstance().setAutoLoginFlag(false);
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
	
//	private void back2MainActivity(Context context) {
//		if(m_bNeedBack == false) 
//			return;
//		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
//		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
//		
//		if(bCPEWifiConnected == true && sim.m_SIMState != SIMState.Accessable) {
//			if(this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName()) == false) {
//				dismissAllDialog();	
//				Intent intent = new Intent(context, MainActivity.class);	
//				context.startActivity(intent);
//				finish();
//			}
//		}
//	}
//	
//	private void backMainActivity(Context context) {
//		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
//		UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
//		
//		if(bCPEWifiConnected == true && m_loginStatus != UserLoginStatus.login) {
//			dismissAllDialog();
//			if(this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName()) == false) {
//				Intent intent = new Intent(context, MainActivity.class);	
//				context.startActivity(intent);
//				finish();
//			}else {
//				Intent intent2= new Intent(MainActivity.PAGE_TO_VIEW_HOME);
//				context.sendBroadcast(intent2);
//			}
//		}
//	}
//	
//	private void showActivity(Context context) {
//	
//		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();		
//		
//		if(bCPEWifiConnected == true && 
//				this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())	)
//			{
//			dismissAllDialog();	
//			Intent intent = new Intent(context, MainActivity.class);		
//			context.startActivity(intent);
//			finish();									
//			
//		}else if(bCPEWifiConnected == false && !this.getClass().getName().equalsIgnoreCase(RefreshWifiActivity.class.getName())) {
//			dismissAllDialog();		
//			Intent intent = new Intent(context, RefreshWifiActivity.class);
//			context.startActivity(intent);	
//			finish();
//		}	
//	}

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
