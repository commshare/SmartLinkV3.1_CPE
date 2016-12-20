package com.alcatel.smartlinkv3.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUti {
	public static String DATE_FORMATE = "yyyy-MM-dd";
	
	public static int parseInt(String strValue) {
		if(null == strValue || strValue.length() == 0)
			return 0;
		return Integer.parseInt(strValue);
	}
	
	public static boolean parseBoolean(String strValue) {
		if(null == strValue || strValue.length() == 0)
			return false;
		return Boolean.parseBoolean(strValue);
	}
	
	public static long parseLong(String strValue) {
		if(null == strValue || strValue.length() == 0)
			return 0;
		return Long.parseLong(strValue);
	}
	
	public static float parseFloat(String strValue) {
		if(null == strValue || strValue.length() == 0)
			return 0;
		return Float.parseFloat(strValue);
	}
	
	public static double parseDouble(String strValue) {
		if(null == strValue || strValue.length() == 0)
			return 0;
		return Double.parseDouble(strValue);
	}
	
	public static String parseString(String strValue) {
		if(strValue == null)
			return new String();
		return strValue;
	}
	
	public static long getBaseTimeMillSecs()
    {
        try
        {
            String begin = "1970-01-01 00:00:00";
            SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sDate.parse(begin);
            return date.getTime();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return 0;
    }
}
