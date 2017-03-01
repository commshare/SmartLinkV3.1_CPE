package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.common.MessageUti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BaseManager {

	protected boolean m_bStopBusiness = false;
	protected Context m_context;
	protected ManagerBroadcastReceiver m_msgReceiver;
	
	public BaseManager(Context context) {
    	m_context = context;
    	m_msgReceiver = new ManagerBroadcastReceiver();
    	m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.CPE_BUSINESS_STATUS_CHANGE));
    	m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
    }
	
	protected abstract void onBroadcastReceive(Context context, Intent intent);
	protected abstract void clearData();
	protected abstract void stopRollTimer();
	
	private class ManagerBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {
	    	
	    	if(intent.getAction().equals(MessageUti.CPE_BUSINESS_STATUS_CHANGE)) {
	    		m_bStopBusiness = intent.getBooleanExtra("stop", false);
	    		if(m_bStopBusiness == true) {
	    			stopRollTimer();
	    			clearData();
	    		}
	    	}
	    	
	    	if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
				boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
				if(bCPEWifiConnected == false) {
					stopRollTimer();
					clearData();
				}
	    	}
	    	
	    	onBroadcastReceive(context,intent);
        }  	
	}

}
