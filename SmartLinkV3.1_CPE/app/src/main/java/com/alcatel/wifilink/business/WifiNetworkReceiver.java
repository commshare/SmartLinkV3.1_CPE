package com.alcatel.wifilink.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.alcatel.wifilink.common.ConnectivityUtils;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.RestHttpClient;

public class WifiNetworkReceiver extends BroadcastReceiver{
	private static int m_nType = -1;
	private static String m_strGatewayMac = new String();
	
	@Override
	public void onReceive(Context context, Intent intent){
		if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			changeNetworkConnect(context);
			/*boolean bDisconnect = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if(bDisconnect == true) {
				DataConnectManager.getInstance().setWifiConnected(false);
				stop(context);
			}else{
				testWebServer(context);
			}	*/
    	}
	}
	
	private void stopBusiness(Context context) {
		LegacyHttpClient.getInstance().stop();
		
		Intent intent= new Intent(MessageUti.CPE_BUSINESS_STATUS_CHANGE);
		intent.putExtra("stop", true);
		context.sendBroadcast(intent);
	}
	
	private void startBusiness(Context context) {
		LegacyHttpClient.getInstance().start();
		
		Intent intent= new Intent(MessageUti.CPE_BUSINESS_STATUS_CHANGE);
		intent.putExtra("stop", false);
		context.sendBroadcast(intent);
	}
	
	private void testWebServer(Context context) {
		if(ConnectivityUtils.checkHaveWifi(context)) {
			String strServerIp = ConnectivityUtils.getServerAddress(context);
			Log.d("strServerIp", strServerIp);
			if(!strServerIp.equalsIgnoreCase("0.0.0.0")) {
				LegacyHttpClient.getInstance().setServerAddress(strServerIp);
				RestHttpClient.getInstance().setServerAddress(strServerIp);
				BusinessManager.getInstance().setServerAddress(strServerIp);
				
				startBusiness(context);
			}
		}else{
			//stop test wifi connected
			//BusinessManager.getInstance().setStopTestWifiConnect(true);
		}
	}

	private void changeNetworkConnect(Context context) {
		//int nPreType = m_nType;
		m_nType = ConnectivityUtils.getActiveConnectedType(context);
		//if(nPreType == m_nType)
		//	return;
		Log.d("strServerIp", String.valueOf(m_nType));
		if(m_nType == ConnectivityUtils.TYPE_NONE) {
			DataConnectManager.getInstance().setNoConnected();
			stopBusiness(context);
		}else if(m_nType == ConnectivityManager.TYPE_WIFI) {
			DataConnectManager.getInstance().setWifiConnected(true);
			if(m_strGatewayMac.length() > 0) {
				String strPreMac = m_strGatewayMac;
				m_strGatewayMac = ConnectivityUtils.getGatewayMac(context);
				if(!strPreMac.equalsIgnoreCase(m_strGatewayMac)) {
					DataConnectManager.getInstance().setCPEWifiConnected(false);
				}
			}else{
				m_strGatewayMac = ConnectivityUtils.getGatewayMac(context);
			}
			testWebServer(context);
		}else if(m_nType == ConnectivityManager.TYPE_MOBILE) {
			DataConnectManager.getInstance().setMobileConnected(true);
			stopBusiness(context);
		}
	}

}
