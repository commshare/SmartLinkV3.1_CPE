package com.alcatel.smartlinkv3.common;

import java.math.BigDecimal;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.model.UsageDataMode;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;


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
	
	public static UsageDataMode ConvertTrafficToUsageModelFromMB(long traffic){
		BigDecimal trafficMB;
		BigDecimal trafficGB;
		
		UsageDataMode usageDataMode = new UsageDataMode();
		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1024);
		BigDecimal divideM = new BigDecimal(1024l * 1024l);
		trafficMB = temp.divide(divideM, 2, BigDecimal.ROUND_HALF_UP);
		if(trafficMB.compareTo(divide) >= 0){
			trafficGB = trafficMB.divide(divide,2,BigDecimal.ROUND_HALF_UP);
			usageDataMode.setUsageData(trafficGB.doubleValue());
			usageDataMode.setUsageUnit(1);
		}else{
			usageDataMode.setUsageData(trafficMB.doubleValue());
			usageDataMode.setUsageUnit(0);
		}
		return usageDataMode;
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

	//ScreenDimen
	public static int getScreenWidthPixels(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	public static int dipToPx(Context context, int dip) {
		return (int) (dip * getScreenDensity(context) + 0.5f);
	}

	private static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
					.getMetrics(dm);
			return dm.density;
		} catch (Exception e) {
			return DisplayMetrics.DENSITY_DEFAULT;
		}
	}

}
