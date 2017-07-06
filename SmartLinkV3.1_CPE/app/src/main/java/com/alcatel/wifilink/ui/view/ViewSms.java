package com.alcatel.wifilink.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.model.SMSContactItemModel;
import com.alcatel.wifilink.business.model.SmsContactMessagesModel;
import com.alcatel.wifilink.common.DataUti;
import com.alcatel.wifilink.common.ENUM.EnumSMSType;
import com.alcatel.wifilink.common.ENUM.SMSInit;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.activity.ActivitySmsDetail;
import com.alcatel.wifilink.ui.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ViewSms extends BaseViewImpl implements OnClickListener ,OnItemClickListener,OnItemLongClickListener {

	private ListView m_smsContactMessagesList = null;
//	private Button m_btnNewSms = null;
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
	
		if(BusinessManager.getInstance().getSMSInit() == SMSInit.Complete)
			BusinessManager.getInstance().getContactMessagesAtOnceRequest();
		
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
			String action = intent.getAction();
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();
			if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)){				
				if(ok) {
					displayUI();
					RefreshNewSmsNumber();
				}
			} else if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET)){
				if(ok) {
					displayUI();
					RefreshNewSmsNumber();
					getListSmsSummaryData();
				}
			}				
		}
		
	}
	
	//
	private void RefreshNewSmsNumber(){
		if(BusinessManager.getInstance().getSMSInit() == SMSInit.Initing) {
			((MainActivity)m_context).updateNewSmsUI(-1);
		}else{
			int nNewSmsCount;
			nNewSmsCount = BusinessManager.getInstance().getNewSmsNumber();
			((MainActivity)m_context).updateNewSmsUI(nNewSmsCount);
		}
	}
	
	private class SortSummaryListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SMSSummaryItem c1 = (SMSSummaryItem) o1;
			SMSSummaryItem c2 = (SMSSummaryItem) o2;
			Date d1 = DataUti.formatDateFromString(c1.strSummaryTime);
			Date d2 = DataUti.formatDateFromString(c2.strSummaryTime);
			
			if(d1.after(d2))
				return -1;
			if(d1.equals(d2))
				return 0;
			return 1;
		}
	}
	
	private void getListSmsSummaryData() {
		
		m_smsContactMessagesLstData.clear();
		SmsContactMessagesModel messages = BusinessManager.getInstance().getContactMessages();
		for(int i = 0;i < messages.SMSContactList.size();i++) {
			SMSContactItemModel sms = messages.SMSContactList.get(i);
			SMSSummaryItem item = new SMSSummaryItem();
			for(int j = 0;j < sms.PhoneNumber.size();j++) {
				if(j == sms.PhoneNumber.size() - 1) 
					item.strNumber += sms.PhoneNumber.get(j);
				else
					item.strNumber = item.strNumber + sms.PhoneNumber.get(j) + ";";
			}
			item.nUnreadNumber = sms.UnreadCount;
			item.nCount = sms.TSMSCount;
			item.enumSmsType = sms.SMSType;
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
		if(BusinessManager.getInstance().getSMSInit() == SMSInit.Initing) {
			m_noSmsTv.setText(R.string.sms_init);
			Drawable d = m_context.getResources().getDrawable(R.drawable.sms_init);
			d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
			m_noSmsTv.setCompoundDrawables(null, d, null, null);
			m_noSmsTv.setVisibility(View.VISIBLE);
			m_smsContactMessagesList.setVisibility(View.GONE);
		}else{
			SmsContactMessagesModel messages = BusinessManager.getInstance().getContactMessages();
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
			public ImageView sentFailedImage;
			public TextView count;
			public TextView totalcount;
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
				holder.sentFailedImage = (ImageView)convertView.findViewById(R.id.sms_item_send_failed);
				holder.count = (TextView)convertView.findViewById(R.id.sms_item_count);
				holder.totalcount = (TextView)convertView.findViewById(R.id.sms_item_totalcount);
				holder.content = (TextView)convertView.findViewById(R.id.sms_item_content);
				holder.time = (TextView)convertView.findViewById(R.id.sms_item_time);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			SMSSummaryItem smsItem = m_smsContactMessagesLstData.get(position);
			holder.number.setText(smsItem.strNumber);
			holder.content.setText(smsItem.strSummaryContent);
					
			int nUnreadNum = smsItem.nUnreadNumber;
			if(nUnreadNum == 0) {
				holder.unreadImage.setVisibility(View.INVISIBLE);
			}else{
				holder.unreadImage.setVisibility(View.VISIBLE);
			}
						
			Date summaryDate = DataUti.formatDateFromString(smsItem.strSummaryTime);
			
			String strTimeText = new String();
			if(summaryDate != null) {
				Date now = new Date();
				if(now.getYear() == summaryDate.getYear() && 
						now.getMonth() == summaryDate.getMonth() && 
						now.getDate() == summaryDate.getDate()) {
					SimpleDateFormat format = new SimpleDateFormat("HH:mm");
					strTimeText = format.format(summaryDate);
				}else{
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					strTimeText = format.format(summaryDate);
				}
			}
			holder.time.setText(strTimeText);
			
			//Read, Unread, Sent, SentFailed, Report, Flash,Draft;
			switch(smsItem.enumSmsType)
			{
			case SentFailed:
                holder.sentFailedImage.setVisibility(View.VISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsItem.nCount));
                holder.count.setVisibility(View.INVISIBLE);
				break;
			case Draft:
				holder.sentFailedImage.setVisibility(View.INVISIBLE);
				holder.totalcount.setVisibility(View.VISIBLE);
				holder.totalcount.setTextColor(m_context.getResources().getColor(R.color.color_grey));
				holder.totalcount.setText(String.format(m_context.getString(R.string.sms_list_view_draft),  smsItem.nCount));
				holder.count.setVisibility(View.INVISIBLE);
				break;
			default:
				holder.sentFailedImage.setVisibility(View.INVISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsItem.nCount));
                holder.count.setVisibility(View.INVISIBLE);
				holder.count.setText(String.format(m_context.getResources().getString(R.string.sms_unread_num), smsItem.nUnreadNumber));
				break;
			}
			
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub	
		Intent intent = new Intent();
		intent.setClass(m_context, ActivitySmsDetail.class);
		intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_SMS_NUMBER, m_smsContactMessagesLstData.get(position).strNumber);
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
		public EnumSMSType enumSmsType = EnumSMSType.Read;
		public int nCount = 0;
		public String strSummaryContent = new String();
		public String strSummaryTime = new String();
		public boolean bSelected = false;
	}

	
}
