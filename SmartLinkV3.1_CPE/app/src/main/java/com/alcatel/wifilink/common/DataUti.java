package com.alcatel.wifilink.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.ParseException;
import android.util.Log;

public class DataUti {
	public final static String TAG = "DateUtils";
	public final static String DATE_FORMATE = "yyyy-MM-dd";
	
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
	
	public static Date formatDateFromString(String time) {
		String pattern;
		if (time.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "dd-MM-yyyy HH:mm:ss";
		} else if (time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		} else if (time.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "dd/MM/yyyy HH:mm:ss";
		} else if (time.matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "yyyy/MM/dd HH:mm:ss";
		} else if (time.matches("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "dd.MM.yyyy HH:mm:ss";
		} else if (time.matches("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			pattern = "yyyy.MM.dd HH:mm:ss";
		} else {
			return null;
		}
		SimpleDateFormat sDate = new SimpleDateFormat(pattern);
		
		Date smsDate = null;
		try {
			smsDate = sDate.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsDate;
	}
}
