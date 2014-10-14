package com.alcatel.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alcatel.R;
import com.alcatel.ui.activity.MainActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSms extends BaseViewImpl implements OnClickListener ,OnItemClickListener {

	private ListView m_smsSummaryList = null;
	private Button m_btnNewSms = null;
	private TextView m_noSmsTv = null;
	
	private SmsBroadcastReceiver m_receiver;
	private ArrayList<Map<String, Object>> m_smsSummaryLstData = new ArrayList<Map<String, Object>>();
	
	public final static int SMS_MATCH_NUM = 7;
	
	
	public ViewSms(Context context) {
		super(context);
		m_context = context;
		init();
	}
	
	@Override
    protected void init()
    {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_sms, null);
		//Button New SMS
		//m_btnNewSms = (Button) ((MainActivity)m_context).findViewById(R.id.new_sms_button);
		//m_btnNewSms.setOnClickListener(this);
		//SMS list
		m_smsSummaryList = (ListView)m_view.findViewById(R.id.sms_list_view);
		m_smsSummaryList.setOnItemClickListener(this);
		SmsAdapter smsAdapter = new SmsAdapter(this.m_context);
		m_smsSummaryList.setAdapter(smsAdapter);
		m_noSmsTv = (TextView)m_view.findViewById(R.id.no_sms);
		
		m_receiver = new SmsBroadcastReceiver();
    }	
	
	@Override
	public void onResume() { 
	
		//BusinessMannager.getInstance().refreshSmsListAtOnce();		
		
		//m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET));		
		//m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		
		//BusinessMannager.getInstance().startGetSmsListTask();
		RefreshNewSmsNumber();
		getListSmsSummaryData();
    }
	
	@Override
	public void onPause() {	
		try{
			m_context.unregisterReceiver(m_receiver);
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//BusinessMannager.getInstance().stopGetSmsListTask();
	}

	public void onClick(View v) {	
		/*if(v.getId() == R.id.new_sms_button){
			Intent intent = new Intent();
			intent.setClass(m_context, NewSmsActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			m_context.startActivity(intent);
		}*/
	}
	
	private class SmsBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub	
			/*if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET)){				
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					RefreshNewSmsNumber();
					getListSmsSummaryData();
				}
			}	
			
			if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
					if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
						BusinessMannager.getInstance().startGetSmsListTask();
					}
				}
	    	}*/
		}
		
	}
	
	//
	private void RefreshNewSmsNumber(){
		/*int nNewSmsCount = 0;
		nNewSmsCount = BusinessMannager.getInstance().getNewSmsNumber();
		((MainActivity)m_context).updateNewSmsUI(nNewSmsCount);*/
	}
	
	private class SortSummaryListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			Map<String, Object> c1 = (Map<String, Object>) o1;
			Map<String, Object> c2 = (Map<String, Object>) o2;
			String time1 = (String) c1.get("time");
			String time2 = (String) c2.get("time");
			/*SMSTag tag1 = (SMSTag) c1.get("type");
			SMSTag tag2 = (SMSTag) c2.get("type");
			if(tag1 == SMSTag.NotRead && tag2 == SMSTag.NotRead) {
				if(time1.compareToIgnoreCase(time2) > 0)
					return -1;
				if(time1.compareToIgnoreCase(time2) == 0)
					return 0;
				return 1;
			}
			
			if(tag1 == SMSTag.NotRead)
				return -1;
			if(tag2 == SMSTag.NotRead)
				return 1;*/
			
			if(time1.compareToIgnoreCase(time2) > 0)
				return -1;
			if(time1.compareToIgnoreCase(time2) == 0)
				return 0;
			return 1;
		}
	}
	
	private void getListSmsSummaryData() {
		
		m_smsSummaryLstData.clear();
		/*ArrayList<SmsMessageModel> smsList = BusinessMannager.getInstance().getSMSList();
		for(int i = 0;i < smsList.size();i++) {
			SmsMessageModel sms = smsList.get(i);
			boolean bExist = false;
			for(int j = 0;j < m_smsSummaryLstData.size();j++) {
				Map<String, Object> summary = m_smsSummaryLstData.get(j);
				String strNumber = (String) summary.get("number");
				if(strNumber.length() > SMS_MATCH_NUM) 
					strNumber = strNumber.substring(strNumber.length() - SMS_MATCH_NUM);
				String strNumber2 = sms.m_strNumber;
				if(strNumber2.length() > SMS_MATCH_NUM) 
					strNumber2 = strNumber2.substring(strNumber2.length() - SMS_MATCH_NUM);
				if(strNumber.equalsIgnoreCase(strNumber2)) {
					bExist = true;
					boolean bHaveUnread = false;
					SMSTag tag = (SMSTag) summary.get("type");
					if(sms.m_nTag == SMSTag.NotRead || tag == SMSTag.NotRead)
						bHaveUnread = true;
					
					int nUnreadNum = (Integer) summary.get("unread_count");
					if(sms.m_nTag == SMSTag.NotRead) {
						nUnreadNum++;
						summary.put("unread_count", nUnreadNum);
					}
					
					int nCount = (Integer) summary.get("count");
					summary.put("count", nCount + 1);
					
					String strTime = (String) summary.get("time");
					if(strTime.compareToIgnoreCase(sms.m_strTime) <= 0) {
						if(bHaveUnread == true)
							summary.put("type", SMSTag.NotRead);
						else
							summary.put("type", sms.m_nTag);
						summary.put("number", sms.m_strNumber);
						summary.put("content", sms.m_strContent);
						summary.put("time", sms.m_strTime);
					}
					break;
				}
			}
			
			if(bExist == false) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number",sms.m_strNumber);
				map.put("type", sms.m_nTag);
				int nUnreadCount = 0;
				if(sms.m_nTag == SMSTag.NotRead) {
					nUnreadCount++;
				}
				map.put("unread_count", nUnreadCount);
				map.put("count", 1);
				map.put("content", sms.m_strContent);
				map.put("time", sms.m_strTime);
				m_smsSummaryLstData.add(map);
			}
		}*/
		
		//test start
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("number","13472479059");
		map.put("unread_count", 1);
		map.put("count", 1);
		map.put("content", "This is super man telephone.heiehehehehehehehehehehehehehehehe");
		map.put("time", "2014-08-19 19:23:42");
		m_smsSummaryLstData.add(map);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("number","13472479058");
		map2.put("unread_count", 0);
		map2.put("count", 1);
		map2.put("content", "This is super man telephone 2.heiehehehehehehehehehehehehehehehe");
		map2.put("time", "2014-08-19 19:22:42");
		m_smsSummaryLstData.add(map2);
		//test end
		
		Collections.sort(m_smsSummaryLstData, new SortSummaryListByTime());
	
		if(m_smsSummaryLstData.size() == 0) {
			m_noSmsTv.setVisibility(View.VISIBLE);
			m_smsSummaryList.setVisibility(View.GONE);
		}else{
			m_noSmsTv.setVisibility(View.GONE);
			m_smsSummaryList.setVisibility(View.VISIBLE);
			((SmsAdapter)m_smsSummaryList.getAdapter()).notifyDataSetChanged();
		}
	}
	

	public class SmsAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
				
		public SmsAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return m_smsSummaryLstData.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public ImageView unreadImage;
			public TextView number;
			public TextView count;
			public TextView content;
			public TextView time;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(m_context).inflate(R.layout.sms_list_item,null);	
				holder.unreadImage = (ImageView)convertView.findViewById(R.id.sms_item_unread_image);
				holder.number = (TextView)convertView.findViewById(R.id.sms_item_number);
				holder.count = (TextView)convertView.findViewById(R.id.sms_item_count);
				holder.content = (TextView)convertView.findViewById(R.id.sms_item_content);
				holder.time = (TextView)convertView.findViewById(R.id.sms_item_time);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.number.setText((String)m_smsSummaryLstData.get(position).get("number"));
			holder.content.setText((String)m_smsSummaryLstData.get(position).get("content"));
			int nCount = (Integer)m_smsSummaryLstData.get(position).get("count");
					
			int nUnreadNum = (Integer)m_smsSummaryLstData.get(position).get("unread_count");
			if(nUnreadNum == 0) {
				holder.unreadImage.setVisibility(View.INVISIBLE);
			}else{
				holder.unreadImage.setVisibility(View.VISIBLE);
			}
			
			holder.count.setText(String.valueOf(nCount));
			
			String strTime = (String) m_smsSummaryLstData.get(position).get("time");
			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date summaryDate = null;
			try {
				summaryDate = sDate.parse(strTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				
			}
			
			String strTimeText = new String();
			if(summaryDate != null) {
				Date now = new Date();
				if(now.getYear() == summaryDate.getYear() && 
						now.getMonth() == summaryDate.getMonth() && 
						now.getDate() == summaryDate.getDate()) {
					SimpleDateFormat format = new SimpleDateFormat("HH:mm");
					strTimeText = format.format(summaryDate);
				}else{
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					strTimeText = format.format(summaryDate);
				}
			}
			holder.time.setText(strTimeText);
			
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub
		/*Intent intent = new Intent();
		intent.setClass(m_context, ViewSmsItemActivity.class);
		intent.putExtra("sms_number", (String)m_smsSummaryLstData.get(position).get("number"));		
		this.m_context.startActivity(intent);
		
		
		int nUnreadNum = (Integer)m_smsSummaryLstData.get(position).get("unread_count");		
		int nNewSmsCount = BusinessMannager.getInstance().getNewSmsNumber();
		
		((MainActivity)m_context).updateNewSmsUI(nNewSmsCount - nUnreadNum);*/
		
		
	}
}
