package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
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
				stopBussiness(context);
			}else{
				testWebServer(context);
			}	*/
    	}
	}
	
	private void stopBussiness(Context context) {
		HttpRequestManager.GetInstance().stopBussiness();
		
		Intent intent= new Intent(MessageUti.CPE_BUSINESS_STATUS_CHANGE);
		intent.putExtra("stop", true);
		context.sendBroadcast(intent);
	}
	
	private void startBussiness(Context context) {
		HttpRequestManager.GetInstance().startBussiness();
		
		Intent intent= new Intent(MessageUti.CPE_BUSINESS_STATUS_CHANGE);
		intent.putExtra("stop", false);
		context.sendBroadcast(intent);
	}
	
	private void testWebServer(Context context) {
		if(checkHaveWifi(context)) {
			String strServerIp = getServerAddress(context);
			Log.d("strServerIp", strServerIp);
			if(!strServerIp.equalsIgnoreCase("0.0.0.0")) {
				HttpRequestManager.GetInstance().setServerAddress(strServerIp);
				BusinessMannager.getInstance().setServerAddress(strServerIp);
				
				startBussiness(context);
			}
		}else{
			//stop test wifi connected
			//BusinessMannager.getInstance().setStopTestWifiConnect(true);
		}
	}
	
	private String getServerAddress(Context ctx){  
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();  
        return Formatter.formatIpAddress(dhcpInfo.gateway);  
    } 
	
	private String getGatewayMac(Context ctx) {
		WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
         WifiInfo wifiinfo = wifi_service.getConnectionInfo(); 
         return wifiinfo.getBSSID();
	}
	
	private boolean checkHaveWifi(Context context) {
		boolean isHave = false;
		/*State wifiState = null; 
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
        if (wifiState != null && State.CONNECTED == wifiState) {  
        	isHave = true;
        }
        
        return isHave;*/
		
		int nType = getActiveConnectedType(context);
		if(nType == ConnectivityManager.TYPE_WIFI)
			return true;
		return false;
        
       
	}
	
	private void changeNetworkConnect(Context context) {
		//int nPreType = m_nType;
		m_nType = getActiveConnectedType(context);
		//if(nPreType == m_nType)
		//	return;
		Log.d("strServerIp", String.valueOf(m_nType));
		if(m_nType == -1) {
			DataConnectManager.getInstance().setNoConnected();
			stopBussiness(context);
		}else if(m_nType == ConnectivityManager.TYPE_WIFI) {
			DataConnectManager.getInstance().setWifiConnected(true);
			if(m_strGatewayMac.length() > 0) {
				String strPreMac = m_strGatewayMac;
				m_strGatewayMac = getGatewayMac(context);
				if(strPreMac.equalsIgnoreCase(m_strGatewayMac) == false) {
					DataConnectManager.getInstance().setCPEWifiConnected(false);
				}
			}else{
				m_strGatewayMac = getGatewayMac(context);
			}
			testWebServer(context);
		}else if(m_nType == ConnectivityManager.TYPE_MOBILE) {
			DataConnectManager.getInstance().setMobileConnected(true);
			stopBussiness(context);
		}
	}
	
	private int getActiveConnectedType(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if(mNetworkInfo == null)
				return -1;
			DetailedState state = mNetworkInfo.getDetailedState();
			if (mNetworkInfo.isConnected() == true && state == DetailedState.CONNECTED) { 
				return mNetworkInfo.getType(); 
			} 
		} 
		return -1; 
	} 
}
