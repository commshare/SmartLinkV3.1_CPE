package com.alcatel.smartlinkv3.business.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.alcatel.smartlinkv3.business.sms.SMSMessage;
import com.alcatel.smartlinkv3.common.ENUM;

public class SmsMessageModel{
    
	public int m_nId = 0;
	public ENUM.SMSStoreIn m_storeIn = ENUM.SMSStoreIn.SIM;//0 : ME  //M850 1 : SIM2 : Device  //H850
	public ENUM.SMSTag m_nTag = ENUM.SMSTag.Read;//0 : read1 : not read2 : sent3 : not sent4 : report5 : flash
	public String m_strContent = new String();
	public String m_strNumber = new String();
	public String m_strTime = new String();//the time of the inbox, sent and draft does not have time format: YYYY-MM-DD hh:mm:ss

	
	
	
	public void setValue(SMSMessage result){
		m_nId = result.Id;
		m_storeIn = ENUM.SMSStoreIn.build(result.StoreIn);
		m_nTag = ENUM.SMSTag.build(result.Tag);
		m_strContent = result.Content;
		m_strNumber = result.Number;
		//m_strTime = result.Time;
		
		if(result.Time != null && result.Time.length() != 0){
	        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        sDate.setTimeZone(TimeZone.getTimeZone("UTC"));
	        Date date = null;
			try {
				date = sDate.parse(result.Time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			SimpleDateFormat sLocalDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sLocalDate.setTimeZone(TimeZone.getDefault()); 
			m_strTime = sLocalDate.format(date.getTime());
		}
	}
}
