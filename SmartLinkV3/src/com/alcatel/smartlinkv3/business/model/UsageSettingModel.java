package com.alcatel.smartlinkv3.business.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.ENUM;

public class UsageSettingModel extends BaseModel{
    
	public int m_nBillingDay = 1;//(Min:1, Max:31)
	public String m_strStartBillDate = new String();//Calculated according to the billingDay.format:2013-04-23
	public String m_strEndBillDate = new String();//Calculated according to the billingDay.format:2013-04-23
	public long m_lCalibrationValue = 0;//(Unit is byte�� Min: -100*1024*1024*1024, Max: 100��1024��1024��1024)
	public long m_lLimitValue = 0;//(Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set)
	public long m_lTotalValue = 0;//(Unit is byte, Min:1, Max:100*1024��1024��1024, default 0 if not set)
	public long m_lOvertime = 5;//(Unit is minute, Min:1, Max:43200, default:5)
	public ENUM.OVER_TIME_STATE m_overtimeState = ENUM.OVER_TIME_STATE.Disable;//Enable auto disconnect by overtime: 1, Disable auto disconnect by overtime:0, Default:0
	public ENUM.OVER_FLOW_STATE m_overflowState = ENUM.OVER_FLOW_STATE.Disable;//Enable auto disconnect by total flow: 1, Disable auto disconnect by total flow:0, Default:0

	public UsageSettingModel() {
		calStartAndEndCalendar();
    }
	
	public void setBillingDay(int nBillingDay) {
		int nPre = m_nBillingDay; 
		m_nBillingDay = nBillingDay;
		if(nPre != m_nBillingDay) {
			//calulated startBillDate and endBillDate
			calStartAndEndCalendar();
		}
	}
	
	public void clone(UsageSettingModel src) {
		m_nBillingDay = src.m_nBillingDay;
		m_strStartBillDate = src.m_strStartBillDate;
		m_strEndBillDate = src.m_strEndBillDate;
		m_lCalibrationValue = src.m_lCalibrationValue;
		m_lLimitValue = src.m_lLimitValue;
		m_lTotalValue = src.m_lTotalValue;
		m_lOvertime = src.m_lOvertime;
		m_overtimeState = src.m_overtimeState;
		m_overflowState = src.m_overflowState;
		calStartAndEndCalendar();
	}
	
	public void clear() {
		// TODO Auto-generated method stub
		setBillingDay(1);
		m_lCalibrationValue = 0;
		m_lLimitValue = 0;
		m_lTotalValue = 0;
		m_lOvertime = 5;
		m_overtimeState = ENUM.OVER_TIME_STATE.Disable;
		m_overflowState = ENUM.OVER_FLOW_STATE.Disable;
	}
	
	public void setValue(UsageSettingsResult result){
		setBillingDay(result.BillingDay);
		m_lCalibrationValue = result.CalibrationValue;
		m_lLimitValue = result.LimitValue;
		m_lTotalValue = result.TotalValue;
		m_lOvertime = result.Overtime;
		m_overtimeState = ENUM.OVER_TIME_STATE.build(result.OvertimeState);
		m_overflowState = ENUM.OVER_FLOW_STATE.build(result.OverflowState);
	}
	
	public void calStartAndEndCalendar() {
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		int startDay = m_nBillingDay;
		Calendar curCalendar = Calendar.getInstance(); 
		int nowDay = curCalendar.get(Calendar.DATE);
		int nowMonth = curCalendar.get(Calendar.MONTH);
		int nowYear = curCalendar.get(Calendar.YEAR);
		int startMonth = nowMonth,startYear = nowYear;
		if(startDay > nowDay){
			if(nowMonth == 0){
				startMonth = 11;
				startYear = nowYear - 1;
			}else
				startMonth -= 1;
		}	
		startCalendar.set(Calendar.YEAR, startYear);
		startCalendar.set(Calendar.MONTH, startMonth);
		startCalendar.set(Calendar.DATE, startDay);
		if(startMonth != startCalendar.get(Calendar.MONTH)) {
			startCalendar.set(Calendar.MONTH, startMonth);
			startCalendar.set(Calendar.DATE, startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
					
		
		int startMonthDays = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if(startDay > 1){	
			int endYearTemp = startYear;
			int endMonthTemp = startMonth + 1;
			if(endMonthTemp == 12) {
				endYearTemp = startYear + 1;
				endMonthTemp = 0;
			}
			endCalendar.set(endYearTemp,endMonthTemp,1);
			int endMonthDays = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			int endDay = startDay - 1;
			if(endDay >= endMonthDays)
				endDay = endMonthDays - 1;
			
			endCalendar.set(endYearTemp,endMonthTemp,endDay);
		}
		else if(startDay == 1){
			endCalendar.set(Calendar.YEAR, startYear);
			endCalendar.set(Calendar.MONTH, startMonth);
			endCalendar.set(Calendar.DATE, startMonthDays);
		}
		Date endDate = endCalendar.getTime();	
		endCalendar.setTime(endDate);
		
		SimpleDateFormat format = new SimpleDateFormat(Const.DATE_FORMATE);
		synchronized (this) {
			m_strStartBillDate = format.format(startCalendar.getTime());
			m_strEndBillDate = format.format(endCalendar.getTime());
		}
	}

}
