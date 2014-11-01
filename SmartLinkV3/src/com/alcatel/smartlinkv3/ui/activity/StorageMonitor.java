package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.Status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class StorageMonitor {		
	
	private static StorageReceiver m_receiver = new StorageReceiver();
	
	private static class StorageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		    if (intent.getAction().equals(
					MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					if (Status.build(BusinessMannager.getInstance().getSambaSettings().SambaStatus) == Status.Disabled)
					{
						backToMainStorageActivity(context);
					}				
				}				
			}
			
			else if (intent.getAction().equals(
					MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET)) {				
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					
					if(Status.build(BusinessMannager.getInstance().getSambaSettings().SambaStatus) == Status.Disabled)
					{
						backToMainStorageActivity(context);	
					}
					else
					{
						getSambaSettings();
					}													
				}			
			}
		}
	}
	
	public static void registerReceiver(Context context)
	{		
		
		context.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET));			
	
		context.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET));		
	}	
	
	public static void unregisterReceiver(Context context)
	{	
		context.unregisterReceiver(m_receiver);	
	}	
	
	public static void getSambaSettings()
	{
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET, null);	
	}
	
	
	public static void backToMainStorageActivity(Context context)
	{
		Intent it = new Intent(context, StorageMainActivity.class);
		context.startActivity(it);
	}

}
