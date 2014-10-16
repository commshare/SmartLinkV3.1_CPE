package com.alcatel.smartlinkv3.ui.activity;



import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BaseActivity extends Activity{
	protected ActivityBroadcastReceiver m_msgReceiver;
	//private ArrayList<Dialog> m_dialogManager = new ArrayList<Dialog>();
	protected boolean m_bNeedBack = true;//whether need to back main activity.
	
	protected void addToDialogManager(Dialog dialog) {
		//m_dialogManager.add(dialog);
	}
	
	private void dismissAllDialog() {
		/*
		for(int i = 0;i < m_dialogManager.size();i++) {
			m_dialogManager.get(i).dismiss();
		}*/
	}
	@Override
	protected void onResume() {	
       	super.onResume();
       	m_msgReceiver = new ActivityBroadcastReceiver();
       //	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));  
    //	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)); 

    	showActivity(this);
    	back2MainActivity(this);
	}
	
	@Override
	protected void onPause() {	
    	super.onPause();
    	try {
    		//this.unregisterReceiver(m_msgReceiver); 		
    		//checkLogin();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	protected void onBroadcastReceive(Context context, Intent intent)
	{
		/*
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
    		showActivity(context);
    	}else if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				back2MainActivity(context);
			}
		}*/
	}
	
	private class ActivityBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {	    	
	    	onBroadcastReceive(context,intent);
        }  	
	}
	
	private void back2MainActivity(Context context) {}
	
	private void showActivity(Context context) {}
	
/*	private void checkLogin()
	{
    	PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);  
    	boolean bScreenOn = pm.isScreenOn();
    	if(bScreenOn == false) {
    		LoginDialog.setAlreadyLogin(false);
    	}
		
    	ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);    	
    	ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
    	if(!cn.getPackageName().equalsIgnoreCase("com.alcatel.cpe")) {
    		LoginDialog.setAlreadyLogin(false);
    	}  
    	//back���˳��������Ĵ���
    	else if(cn.getPackageName().equalsIgnoreCase("com.alcatel.cpe") 
    			&&  cn.getClassName().equalsIgnoreCase("com.alcatel.cpe.ui.activity.LoadingActivity"))
    	{
    		LoginDialog.setAlreadyLogin(false);
    	}
	   
	}*/
}
