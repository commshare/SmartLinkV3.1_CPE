package com.alcatel.smartlinkv3.business.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.text.format.DateFormat;

import com.alcatel.smartlinkv3.business.calllog.CallLog;
import com.alcatel.smartlinkv3.common.DataUti;

public class CallLogModel{
	
	public int m_Id = 0;
	public String m_TelNumber = new String();
	public String m_TelName = new String();
	public Date m_Time = new Date();
	public long m_DurationTime = 0;
	/*1:in call log
	2:out call log
	3:missed call log*/
	public int m_Type = 0;	
	
	public void clone(CallLogModel src) {
		if(src == null)
			return;
		this.m_Id = src.m_Id;
		this.m_TelNumber = src.m_TelNumber;
		this.m_TelName = src.m_TelName;
		this.m_Time = src.m_Time;
		this.m_DurationTime = src.m_DurationTime;
		this.m_Type = src.m_Type;
	}

	
	public void setValue(CallLog result){
		m_Id = result.Id;
		m_TelNumber = result.TelNumber;
		m_TelName = result.TelName;
		//m_Time = DataUti.getBaseTimeMillSecs() + result.Time * 1000;
		
		 String utcFormat = "1970-01-01 00:00:00";
         SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         sDate.setTimeZone(TimeZone.getTimeZone("UTC"));
         Date date = null;
		try {
			date = sDate.parse(utcFormat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		SimpleDateFormat sLocalDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sLocalDate.setTimeZone(TimeZone.getDefault()); 
		String localTime = sLocalDate.format(date.getTime() + result.Time * 1000);
		try {
			m_Time = sLocalDate.parse(localTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_DurationTime = result.DurationTime;
		m_Type = result.Type;
	}
}
