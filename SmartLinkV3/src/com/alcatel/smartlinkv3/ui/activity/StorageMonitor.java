package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.ServiceType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class StorageMonitor {		
	
	private static StorageReceiver m_receiver = new StorageReceiver();
	
	private static class StorageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
/*
		    if (intent.getAction().equals(
					MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					if(BusinessMannager.getInstance().getSambaServiceState() == ServiceState.Disabled)	
						backToMainStorageActivity(context);
				}				
			}
			
			else if (intent.getAction().equals(
					MessageUti.SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET)) {				
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					
					if (BusinessMannager.getInstance().getStorageList().DeviceList.size() > 0)
					{
						getSambaState();						
					}
					else
					{
						backToMainStorageActivity(context);
					}										
				}			
			}*/
		}
	}
	
	public static void registerReceiver(Context context)
	{	
	/*	context.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET));
	
		context.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET));*/
	}	
	
	public static void unregisterReceiver(Context context)
	{	
		context.unregisterReceiver(m_receiver);	
	}	
	
	public static void getSambaState() {	
	/*	DataValue samba = new DataValue();
		samba.addParam("ServiceType", ServiceType.Samba.ordinal());
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET, samba);*/
	}
	
	public static void backToMainStorageActivity(Context context)
	{
		Intent it = new Intent(context, SdSharingActivity.class);
		context.startActivity(it);
	}

}
