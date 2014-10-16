package com.alcatel.smartlinkv3.ui.activity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.view.ViewSms.SMSSummaryItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;


public class ActivitySMSDelete extends Activity implements OnItemClickListener{
	private ListView m_smsSummaryList = null;

	private ArrayList<SMSSummaryItem> m_smsSummaryLstData = new ArrayList<SMSSummaryItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_delete_list_view);
		
		m_smsSummaryList = (ListView)this.findViewById(R.id.sms_list_view);
		m_smsSummaryList.setOnItemClickListener(this);
		SmsAdapter smsAdapter = new SmsAdapter(this);
		m_smsSummaryList.setAdapter(smsAdapter);
		
		getListSmsSummaryData();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class SortSummaryListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SMSSummaryItem c1 = (SMSSummaryItem) o1;
			SMSSummaryItem c2 = (SMSSummaryItem) o2;
			String time1 = (String) c1.strSummaryTime;
			String time2 = (String) c2.strSummaryTime;
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
		SMSSummaryItem item = new SMSSummaryItem();
		item.strNumber = "13472479059";
		item.nUnreadNumber = 1;
		item.nCount = 1;
		item.bSelected = true;
		item.strSummaryContent = "This is super man telephone.heiehehehehehehehehehehehehehehehe";
		item.strSummaryTime = "2014-08-19 19:23:42";
		m_smsSummaryLstData.add(item);
		
		SMSSummaryItem item2 = new SMSSummaryItem();
		item2.strNumber = "13472479058";
		item2.nUnreadNumber = 0;
		item2.nCount = 1;
		item2.bSelected = false;
		item2.strSummaryContent = "This is super man telephone 2.heiehehehehehehehehehehehehehehehe";
		item2.strSummaryTime = "2014-08-19 19:22:42";
		m_smsSummaryLstData.add(item2);
		//test end
		
		Collections.sort(m_smsSummaryLstData, new SortSummaryListByTime());

		((SmsAdapter)m_smsSummaryList.getAdapter()).notifyDataSetChanged();
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
			public CheckBox itemCheckBox;
			public TextView number;
			public TextView count;
			public TextView content;
			public TextView time;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(ActivitySMSDelete.this).inflate(R.layout.sms_delete_list_item,null);	
				holder.itemCheckBox = (CheckBox)convertView.findViewById(R.id.select);
				holder.number = (TextView)convertView.findViewById(R.id.sms_item_number);
				holder.count = (TextView)convertView.findViewById(R.id.sms_item_count);
				holder.content = (TextView)convertView.findViewById(R.id.sms_item_content);
				holder.time = (TextView)convertView.findViewById(R.id.sms_item_time);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.number.setText((String)m_smsSummaryLstData.get(position).strNumber);
			holder.content.setText((String)m_smsSummaryLstData.get(position).strSummaryContent);
			int nCount = (Integer)m_smsSummaryLstData.get(position).nCount;
					
			int nUnreadNum = (Integer)m_smsSummaryLstData.get(position).nUnreadNumber;
			
			holder.count.setText(String.valueOf(nCount));
			
			String strTime = (String) m_smsSummaryLstData.get(position).strSummaryTime;
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
			
			boolean bChecked = (Boolean)m_smsSummaryLstData.get(position).bSelected;
			holder.itemCheckBox.setChecked(bChecked);
			final CheckBox checkbox = holder.itemCheckBox;
			holder.itemCheckBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean bChecked = checkbox.isChecked();
					m_smsSummaryLstData.get(position).bSelected = bChecked;
				}	
			});
			
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
