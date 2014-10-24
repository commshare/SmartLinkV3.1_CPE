package com.alcatel.smartlinkv3.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SMSContactItemModel;
import com.alcatel.smartlinkv3.business.model.SmsContactMessagesModel;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.ActivitySmsDetail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSms extends BaseViewImpl implements OnClickListener ,OnItemClickListener,OnItemLongClickListener {

	private ListView m_smsContactMessagesList = null;
	private Button m_btnNewSms = null;
	private TextView m_noSmsTv = null;
	
	private SmsBroadcastReceiver m_receiver;
	private ArrayList<SMSSummaryItem> m_smsContactMessagesLstData = new ArrayList<SMSSummaryItem>();
	
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
		m_smsContactMessagesList = (ListView)m_view.findViewById(R.id.sms_list_view);
		m_smsContactMessagesList.setOnItemClickListener(this);
		m_smsContactMessagesList.setOnItemLongClickListener(this);
		SmsAdapter smsAdapter = new SmsAdapter(this.m_context);
		m_smsContactMessagesList.setAdapter(smsAdapter);
		m_noSmsTv = (TextView)m_view.findViewById(R.id.no_sms);
		
		m_receiver = new SmsBroadcastReceiver();
    }	
	
	@Override
	public void onResume() { 
	
		if(BusinessMannager.getInstance().getSMSInit() == SMSInit.Complete)
			BusinessMannager.getInstance().getContactMessagesAtOnceRequest();		
		
		m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET));		
		m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET));
		
		RefreshNewSmsNumber();
		getListSmsSummaryData();
		displayUI();
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
	}

	public void onClick(View v) {	
		
	}
	
	private class SmsBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub	
			if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)){				
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					displayUI();
				}
			}
			if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET)){				
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					displayUI();
					RefreshNewSmsNumber();
					getListSmsSummaryData();
				}
			}				
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
			SMSSummaryItem c1 = (SMSSummaryItem) o1;
			SMSSummaryItem c2 = (SMSSummaryItem) o2;
			String time1 = (String) c1.strSummaryTime;
			String time2 = (String) c2.strSummaryTime;

			if(time1.compareToIgnoreCase(time2) > 0)
				return -1;
			if(time1.compareToIgnoreCase(time2) == 0)
				return 0;
			return 1;
		}
	}
	
	private void getListSmsSummaryData() {
		
		m_smsContactMessagesLstData.clear();
		SmsContactMessagesModel messages = BusinessMannager.getInstance().getContactMessages();
		for(int i = 0;i < messages.SMSContactList.size();i++) {
			SMSContactItemModel sms = messages.SMSContactList.get(i);
			SMSSummaryItem item = new SMSSummaryItem();
			for(int j = 0;j < sms.PhoneNumber.size();j++) {
				if(j == sms.PhoneNumber.size() - 1) 
					item.strNumber = sms.PhoneNumber.get(j);
				else
					item.strNumber = sms.PhoneNumber.get(j) + ";";
			}
			item.nUnreadNumber = sms.UnreadCount;
			item.nCount = sms.TSMSCount;
			item.strSummaryContent = sms.SMSContent;
			item.strSummaryTime = sms.SMSTime;
			item.nContactid = sms.ContactId;
			m_smsContactMessagesLstData.add(item);
		}
		
		//test start
		/*SMSSummaryItem item = new SMSSummaryItem();
		item.strNumber = "13472479059";
		item.nUnreadNumber = 1;
		item.nCount = 1;
		item.strSummaryContent = "This is super man telephone.heiehehehehehehehehehehehehehehehe";
		item.strSummaryTime = "2014-08-19 19:23:42";
		m_smsContactMessagesLstData.add(item);
		
		SMSSummaryItem item2 = new SMSSummaryItem();
		item2.strNumber = "13472479058";
		item2.nUnreadNumber = 0;
		item2.nCount = 1;
		item2.strSummaryContent = "This is super man telephone 2.heiehehehehehehehehehehehehehehehe";
		item2.strSummaryTime = "2014-08-19 19:22:42";
		m_smsContactMessagesLstData.add(item2);*/
		//test end
		
		Collections.sort(m_smsContactMessagesLstData, new SortSummaryListByTime());
	
		displayUI();
		((SmsAdapter)m_smsContactMessagesList.getAdapter()).notifyDataSetChanged();
	}
	
	private void displayUI() {
		if(BusinessMannager.getInstance().getSMSInit() == SMSInit.Initing) {
			m_noSmsTv.setText(R.string.sms_init);
			Drawable d = m_context.getResources().getDrawable(R.drawable.sms_init);
			d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
			m_noSmsTv.setCompoundDrawables(null, d, null, null);
			m_noSmsTv.setVisibility(View.VISIBLE);
			m_smsContactMessagesList.setVisibility(View.GONE);
		}else{
			SmsContactMessagesModel messages = BusinessMannager.getInstance().getContactMessages();
			if(messages.SMSContactList.size() == 0) {
				m_noSmsTv.setText(R.string.sms_empty);
				Drawable d = m_context.getResources().getDrawable(R.drawable.sms_empty);
				d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
				m_noSmsTv.setCompoundDrawables(null, d, null, null);
				m_noSmsTv.setVisibility(View.VISIBLE);
				m_smsContactMessagesList.setVisibility(View.GONE);
			}else{
				m_noSmsTv.setVisibility(View.GONE);
				m_smsContactMessagesList.setVisibility(View.VISIBLE);
			}
		}
	}
	

	public class SmsAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
				
		public SmsAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return m_smsContactMessagesLstData.size();
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
			
			holder.number.setText((String)m_smsContactMessagesLstData.get(position).strNumber);
			holder.content.setText((String)m_smsContactMessagesLstData.get(position).strSummaryContent);
			int nCount = (Integer)m_smsContactMessagesLstData.get(position).nCount;
					
			int nUnreadNum = (Integer)m_smsContactMessagesLstData.get(position).nUnreadNumber;
			if(nUnreadNum == 0) {
				holder.unreadImage.setVisibility(View.INVISIBLE);
			}else{
				holder.unreadImage.setVisibility(View.VISIBLE);
			}
			
			holder.count.setText(String.valueOf(nCount));
			
			String strTime = (String) m_smsContactMessagesLstData.get(position).strSummaryTime;
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
		Intent intent = new Intent();
		intent.setClass(m_context, ActivitySmsDetail.class);
		intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_SMS_NUMBER, (String)m_smsContactMessagesLstData.get(position).strNumber);
		intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_CONTACT_ID, m_smsContactMessagesLstData.get(position).nContactid);
		this.m_context.startActivity(intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		// TODO Auto-generated method stub
		/*Intent intent = new Intent();
		intent.setClass(m_context, ActivitySMSDelete.class);	
		this.m_context.startActivity(intent);*/
		return false;
	}
	
	public static class SMSSummaryItem {
		public String strNumber = new String();
		public int nContactid = 0;
		public int nUnreadNumber = 0;
		public int nCount = 0;
		public String strSummaryContent = new String();
		public String strSummaryTime = new String();
		public boolean bSelected = false;
	}

	
}
