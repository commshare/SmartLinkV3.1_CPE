package com.alcatel.smartlinkv3.samba;

import org.cybergarage.http.HTTPServerList;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SmbService extends Service
{
	
	private SmbHttpServer m_httpServer = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	} 
	
	@Override
	public void onCreate()
	{
		super.onCreate(); 
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		m_httpServer = new SmbHttpServer();
		m_httpServer.start();
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		HTTPServerList httpServerList = m_httpServer.getHttpServerList();
		
		if(httpServerList != null)
		{
			httpServerList.stop(); 
			httpServerList.close(); 
			httpServerList.clear(); 
			m_httpServer.interrupt();			
		}		
	}

}
