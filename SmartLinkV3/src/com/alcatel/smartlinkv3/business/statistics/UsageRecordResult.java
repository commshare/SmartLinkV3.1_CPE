package com.alcatel.smartlinkv3.business.statistics;

import java.util.ArrayList;

import android.R.integer;

import com.alcatel.smartlinkv3.business.BaseResult;

public class UsageRecordResult extends BaseResult{
	
	public long HUseData = 0;//The total used data in home network.
	public int HCurrUseUL = 0;//Current use upload data in home network.
	public int HCurrUseDL = 0;//Current use download data in home network.
	public int RoamUseData = 0;//The total used data in roaming network.
	public int RCurrUseUL = 0;//Current use upload data in roaming network.
	public int RCurrUseDL = 0;//Current use download data in roaming network.
	public int TConnTimes = 0;//The total connected Internet times.
	public int CurrConnTimes = 0;//Current connected Internet times.
	public int MonthlyPlan = 0;//The Max usage date that the device should use, when the real usage is more than this data, the connection must disconnect.
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		HUseData = 0;
		HCurrUseUL = 0;
		HCurrUseDL = 0;
		RoamUseData = 0;
		RCurrUseUL = 0;
		RCurrUseDL = 0;
		TConnTimes = 0;
		CurrConnTimes = 0;
		MonthlyPlan = 0;
	}
	
	public void clone(UsageRecordResult src) {	
		if(src == null)
			return ;
		HUseData = src.HUseData; 
		HCurrUseUL = src.HCurrUseUL;
		HCurrUseDL = src.HCurrUseDL;
		RoamUseData = src.RoamUseData;
		RCurrUseUL = src.RCurrUseUL;
		RCurrUseDL = src.RCurrUseDL;
		TConnTimes = src.TConnTimes;
		CurrConnTimes = src.CurrConnTimes;
		MonthlyPlan  = src.MonthlyPlan;
	}
	
	public void setValue(UsageRecordResult result) {	
		HUseData = result.HUseData; 
		HCurrUseUL = result.HCurrUseUL;
		HCurrUseDL = result.HCurrUseDL;
		RoamUseData = result.RoamUseData;
		RCurrUseUL = result.RCurrUseUL;
		RCurrUseDL = result.RCurrUseDL;
		TConnTimes = result.TConnTimes;
		CurrConnTimes = result.CurrConnTimes;
		MonthlyPlan = result.MonthlyPlan;
	}
}
