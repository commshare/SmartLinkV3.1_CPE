package com.alcatel.smartlinkv3.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class BootReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent){
		if ((intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))) {
			Log.d("HttpService", "BOOT RECEIVER");
			HttpService.startService();
		}
		
		else if((intent.getAction().equals(Intent.ACTION_TIME_CHANGED)))
		{
			//Ϊ�˷�ֹ�޸�ϵͳʱ�䵼��TimerʧЧ����ɱ���Լ�
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
}
