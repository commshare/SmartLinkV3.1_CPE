package com.alcatel.smartlinkv3.common;

import java.math.BigDecimal;

import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;


public class CommonUtil {
	public static String ConvertTrafficToStringFromMB(Context context,long traffic){
		BigDecimal trafficMB;
		BigDecimal trafficGB;

		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1024);
		BigDecimal divideM = new BigDecimal(1024l * 1024l);
		trafficMB = temp.divide(divideM, 2, BigDecimal.ROUND_HALF_UP);
		if(trafficMB.compareTo(divide) >= 0){
			trafficGB = trafficMB.divide(divide,2,BigDecimal.ROUND_HALF_UP);
			return trafficGB + context.getResources().getString(R.string.home_GB);
		}else{
			return trafficMB + context.getResources().getString(R.string.home_MB);
		}
	}
	
	/*public static String ConvertTrafficToString(Context context,long traffic){
		BigDecimal trafficB;
		BigDecimal trafficKB;
		BigDecimal trafficMB;
		BigDecimal trafficGB;
		
		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1024);
		//trafficB = temp.divide(divide, 2, 1);
		trafficB = temp;
		if(trafficB.compareTo(divide) >= 0){
			trafficKB = trafficB.divide(divide,2,1);
			if(trafficKB.compareTo(divide) >= 0){
				trafficMB = trafficKB.divide(divide,2,1);
				if(trafficMB.compareTo(divide) >= 0){
					trafficGB = trafficMB.divide(divide,2,1);
					return trafficGB.doubleValue() + context.getResources().getString(R.string.home_GB);
				}else{
					return trafficMB.doubleValue() + context.getResources().getString(R.string.home_MB);
				}
			}else{
				return trafficKB.doubleValue() + context.getResources().getString(R.string.home_KB);
			}
		}else{
			return trafficB.doubleValue() + context.getResources().getString(R.string.home_B);
		}
	}*/
	
	
	@SuppressWarnings("deprecation")
	public static String getIp(Context ctx){  
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();      
        return Formatter.formatIpAddress(dhcpInfo.ipAddress);  
    } 
	
	public static void openWebPage(Context context, String strWeb){
		Uri uri = Uri.parse(strWeb);
		Intent it = new Intent(Intent.ACTION_VIEW,uri);
		context.startActivity(it);
	}
}
