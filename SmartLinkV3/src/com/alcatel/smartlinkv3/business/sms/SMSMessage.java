package com.alcatel.smartlinkv3.business.sms;


public class SMSMessage{
    
	public int Id = 0;
	public int StoreIn = 0;//0 : ME  //M850 1 : SIM2 : Device  //H850
	public int Tag = 0;//0 : read1 : not read2 : sent3 : not sent4 : report5 : flash
	public String Content = new String();
	public String Number = new String();
	public String Time = new String();//the time of the inbox, sent and draft does not have time format: YYYY-MM-DD hh:mm:ss
}
