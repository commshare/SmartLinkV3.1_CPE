package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.view.ViewSms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class ActivitySmsDetail extends Activity implements OnClickListener,OnScrollListener, TextWatcher {
	public static final int ONE_SMS_LENGTH = 67;
	public static final String INTENT_EXTRA_SMS_NUMBER = "sms_number";
	
	private TextView m_tvTitle = null;
	private Button m_btnSend = null;
	private Button m_btnBack = null;
	private EditText m_etContent = null;
	
	private ListView m_smsListView = null;
	private ArrayList<SMSDetailItem> m_smsListData = new ArrayList<SMSDetailItem>();
	private String m_smsNumber = new String();
	
	private TextView m_tvCnt = null;
	
	//private ProgressBar m_progressWaiting = null;
	private ProgressDialog m_progress_dialog = null;
	
	private int m_deleteCounter  = 0;
	private int m_deleteSuccessNum  = 0;
	private int m_deleteNum  = 0;
	private boolean m_bDeleteMult = false;
	private boolean m_bIsLastOneMessage = false;
	
	private boolean m_bDeleteSingleEnable = true;
	private boolean m_bDeleteEnd = false;
	private boolean m_bNeedFinish = false;
	//private SendStatus m_sendStatus = SendStatus.None;
	private boolean m_bSendEnd = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_details_view);
		// Edit Text
		m_etContent = (EditText) this.findViewById(R.id.ID_SMS_DETAIL_EDIT_CONTENT);
		m_etContent.addTextChangedListener(this);
		
		m_tvCnt = (TextView)findViewById(R.id.sms_cnt);
		String text = ONE_SMS_LENGTH+"/1"; 
		m_tvCnt.setText(text);
		// Text View
		m_tvTitle = (TextView) this.findViewById(R.id.ID_SMS_DETAIL_TITLE);
		// Button
		m_btnSend = (Button) this.findViewById(R.id.ID_SMS_DETAIL_BUTTON_SEND);
		m_btnSend.setOnClickListener(this);
		m_btnBack = (Button) this.findViewById(R.id.ID_SMS_DETAIL_BUTTON_BACK);
		m_btnBack.setOnClickListener(this);
		//
		//m_progressWaiting = (ProgressBar) this.findViewById(R.id.sms_waiting_progress);
		
		m_smsNumber = this.getIntent().getStringExtra(INTENT_EXTRA_SMS_NUMBER);
		m_tvTitle.setText(m_smsNumber);
		m_smsListView = (ListView) this.findViewById(R.id.sms_detail_list_view);
		SmsDetailListAdapter smsListAdapter = new SmsDetailListAdapter(this);
		m_smsListView.setAdapter(smsListAdapter);
		m_smsListView.setOnScrollListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET));
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_SEND_SMS_REQUSET));		
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SEND_STATUS_REQUSET));
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_DELETE_SMS_REQUSET));
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_MODIFY_SMS_READ_STATUS_REQUSET));
		getSmsListData();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
			m_progress_dialog.dismiss();
		}	
		
		//m_progressWaiting.setVisibility(View.GONE);

		m_etContent.setEnabled(true);
		m_btnSend.setEnabled(true);
		m_bDeleteSingleEnable = true;
		//m_etContent.setText("");
		
		m_bNeedFinish = false;
		m_bSendEnd = false;
		m_bDeleteEnd = false;
	}
	
	/*@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		super.onBroadcastReceive(context, intent);
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_MODIFY_SMS_READ_STATUS_REQUSET)){				
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				BusinessMannager.getInstance().refreshSmsListAtOnce();
			}else{
				String msgRes = this.getString(R.string.IDS_SMS_CHANGE_READ_ERROR);
    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET)){				
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				if(m_bDeleteEnd == true) {
					m_bDeleteEnd = false;
					//m_progressWaiting.setVisibility(View.GONE);					
					if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
						m_progress_dialog.dismiss();
					}
					m_etContent.setEnabled(true);
					if(m_etContent.getText().toString() != null && m_etContent.getText().toString().length() > 0)
						m_btnSend.setEnabled(true);
					else
						m_btnSend.setEnabled(false);
	    			m_btnDelete.setEnabled(true);
	    			m_bDeleteSingleEnable = true;
					if(m_bNeedFinish == true){
						m_bNeedFinish = false;
						this.finish();
					}
				}else if(m_bSendEnd == true){
					m_bSendEnd = false;
					//m_progressWaiting.setVisibility(View.GONE);
					if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
						m_progress_dialog.dismiss();
					}
					m_etContent.setEnabled(true);
					m_btnDelete.setEnabled(true);
					m_bDeleteSingleEnable = true;
					if(m_sendStatus == SendStatus.Success) {
						m_etContent.setText("");
						m_btnSend.setEnabled(false);
	    			}else if(m_sendStatus == SendStatus.Fail || m_sendStatus == SendStatus.Fail_Memory_Full) {
						m_btnSend.setEnabled(true);
	    			}
				}
				
				getSmsListData();
			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_DELETE_SMS_REQUSET)){				
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				m_deleteSuccessNum++;
			}
			m_deleteCounter++;
			
			if(m_deleteCounter == m_deleteNum) {
				m_bDeleteEnd = true;
				if(m_bDeleteMult == true) {//delete all sms
					if(m_deleteNum == m_deleteSuccessNum) {
						BusinessMannager.getInstance().refreshSmsListAtOnce();
						String msgRes = this.getString(R.string.IDS_SMS_DELETE_MULTI_SUCCESS);
		    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
		    			m_bNeedFinish = true;
		    			///this.finish();
					}else{
						String strMsg = String.format(getString(R.string.IDS_SMS_DELETE_MULTI_ERROR), m_deleteSuccessNum,m_deleteNum - m_deleteSuccessNum);
						Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
						BusinessMannager.getInstance().refreshSmsListAtOnce();
					}
				}else{//delete single sms
					if(m_deleteNum == m_deleteSuccessNum) {
		    			Toast.makeText(this, getString(R.string.IDS_SMS_DELETE_SUCCESS), Toast.LENGTH_SHORT).show();
		    			BusinessMannager.getInstance().refreshSmsListAtOnce();
		    			if(m_bIsLastOneMessage == true) {
		    				m_bNeedFinish = true;
		    				//this.finish();
		    			}
					}else{
						Toast.makeText(this, getString(R.string.IDS_SMS_DELETE_ERROR_CONTENT), Toast.LENGTH_SHORT).show();
					}
				}
					
			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_SEND_SMS_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {					
				//BusinessMannager.getInstance().refreshSmsListAtOnce();
			}
			else if(strErrorCode.endsWith(ErrorCode.ERR_SMS_SIM_IS_FULL))
			{
				String msgRes = this.getString(R.string.IDS_SMS_ERRORMESSAGE_FULLERROR);
				Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    			//m_progressWaiting.setVisibility(View.GONE);
    			if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
    				m_progress_dialog.dismiss();
    			}
    			m_etContent.setEnabled(true);
    			m_btnSend.setEnabled(true);
    			m_btnDelete.setEnabled(true);
    			m_bDeleteSingleEnable = true;
			}			
			else{
				String msgRes = this.getString(R.string.IDS_SMS_ERROR_SEND_ERROR);
    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    			//m_progressWaiting.setVisibility(View.GONE);
    			if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
    				m_progress_dialog.dismiss();
    			}
    			m_etContent.setEnabled(true);
    			m_btnSend.setEnabled(true);
    			m_btnDelete.setEnabled(true);
    			m_bDeleteSingleEnable = true;
			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SEND_STATUS_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {	
				int nSendReslt = intent.getIntExtra(Const.SMS_SNED_STATUS, 0);
				SendStatus sendStatus = SendStatus.build(nSendReslt);
				m_sendStatus = sendStatus;
				boolean bEnd = false;
				if(sendStatus == SendStatus.Fail){
					String msgRes = this.getString(R.string.IDS_SMS_ERROR_SEND_ERROR);
	    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
	    			bEnd = true;
				}
				if(sendStatus == SendStatus.Fail_Memory_Full) {
					String msgRes = this.getString(R.string.IDS_SMS_ERRORMESSAGE_FULLERROR);
	    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
	    			bEnd = true;
				}
				if(sendStatus == SendStatus.Success) {
					String msgRes = this.getString(R.string.IDS_SMS_ERROR_SEND_SUCCESS);
	    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
	    			bEnd = true;
				}
				
				if(bEnd == true){
					m_bSendEnd = true;
					BusinessMannager.getInstance().refreshSmsListAtOnce();
				}
			}else{
				//m_progressWaiting.setVisibility(View.GONE);
				if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
					m_progress_dialog.dismiss();
				}
				m_etContent.setEnabled(true);
				m_btnSend.setEnabled(true);
				m_btnDelete.setEnabled(true);
				m_bDeleteSingleEnable = true;
			}
		}
	}*/

	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.ID_SMS_DETAIL_BUTTON_BACK:
			OnBtnBack();
			break;

		case R.id.ID_SMS_DETAIL_BUTTON_SEND:
			OnBtnSend();
			break;
		default:
			break;
		}
	}

	//
	private void OnBtnBack() {
		//BusinessMannager.getInstance().refreshSmsListAtOnce();
		ActivitySmsDetail.this.finish();
	}

	private boolean isSameNumber(String strNumber1,String strNumber2) {
		if(strNumber1.length() > ViewSms.SMS_MATCH_NUM) 
			strNumber1 = strNumber1.substring(strNumber1.length() - ViewSms.SMS_MATCH_NUM);
		if(strNumber2.length() > ViewSms.SMS_MATCH_NUM) 
			strNumber2 = strNumber2.substring(strNumber2.length() - ViewSms.SMS_MATCH_NUM);
		
		return strNumber1.equalsIgnoreCase(strNumber2);
	}

	//
	private void OnBtnSend() {
		/*InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

		DataValue data = new DataValue();
		data.addParam("content", m_etContent.getText().toString());
		data.addParam("phone_number", m_tvTitle.getText().toString());
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SMS_SEND_SMS_REQUSET, data);
		//m_progressWaiting.setVisibility(View.VISIBLE);
		m_progress_dialog = ProgressDialog.show(ActivitySmsDetail.this, null, 
				getString(R.string.sending),true, false);
		m_btnSend.setEnabled(false);
		m_etContent.setEnabled(false);
		m_btnDelete.setEnabled(false);
		m_bDeleteSingleEnable = false;*/
	}
	
	private void showNewSmsCnt(CharSequence s)
	{
		
		int nRemain = ONE_SMS_LENGTH - s.length() % ONE_SMS_LENGTH;
		
		if(s.length() >= ONE_SMS_LENGTH  && nRemain == ONE_SMS_LENGTH )
		{			 
			nRemain = 0;
		}
		
		int nCnt = 1;
		
		if(s.length() > ONE_SMS_LENGTH)
		{
			nCnt = (s.length() -1 ) / ONE_SMS_LENGTH +1;	
		}	
		
		m_tvCnt.setText(nRemain + "/" + nCnt);		
	}

	//
	public void ShowErrorMsg(String strTitle, String strMsg) {
		/*String strOK = getString(R.string.IDS_SMS_ERROR_BUTTON_OK);
		new AlertDialog.Builder(this).setTitle(strTitle).setMessage(strMsg)
				.setPositiveButton(strOK, null).show();*/
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	
		String strInfo = s == null?null:s.toString();
		if (s != null && strInfo.length() > 0) {
			m_btnSend.setEnabled(true);
		} else {
			m_btnSend.setEnabled(false);
		}
		
		showNewSmsCnt(s);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub	
	}
	
	/*private class SortSmsListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SmsMessageModel c1 = (SmsMessageModel) o1;
			SmsMessageModel c2 = (SmsMessageModel) o2;
			if(c1.m_strTime.compareToIgnoreCase(c2.m_strTime) > 0)
				return 1;
			if(c1.m_strTime.compareToIgnoreCase(c2.m_strTime) == 0)
				return 0;
			return -1;
		}
	}
	
	private ArrayList<SmsMessageModel> getSmsByNumber() {
		ArrayList<SmsMessageModel> smsResult = new ArrayList<SmsMessageModel>();
		ArrayList<SmsMessageModel> smsList = BusinessMannager.getInstance().getSMSList();
		for(int i = 0;i < smsList.size();i++) {
			SmsMessageModel sms = smsList.get(i);

			if(isSameNumber(sms.m_strNumber,m_smsNumber)) {
				smsResult.add(sms);
			}
		}
		Collections.sort(smsResult, new SortSmsListByTime());
		
		return smsResult;
	}*/
	
	/*private void setSMSReadTag(ArrayList<SmsMessageModel> smsList) {
		for(int i = 0;i < smsList.size();i++) {
			SmsMessageModel sms = smsList.get(i);
			if(sms.m_nTag == SMSTag.NotRead) {				
				DataValue data = new DataValue();
				data.addParam("id", sms.m_nId);
				data.addParam("status", true);
				BusinessMannager.getInstance().sendRequestMessage(MessageUti.SMS_MODIFY_SMS_READ_STATUS_REQUSET, data);
				
				
			}
		}
	}*/
	
	private void getSmsListData() {
		m_smsListData.clear();
		/*ArrayList<SmsMessageModel> smsList = getSmsByNumber();
		setSMSReadTag(smsList);		
		
		for(int i = 0;i < smsList.size();i++) {
			SmsMessageModel sms = smsList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			
			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date smsDate = null;
			try {
				smsDate = sDate.parse(sms.m_strTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date now = new Date();
			Date yestoday = new Date(now.getTime() - 1 * 24 * 3600 * 1000);
			
			String strTime = new String();
			if(smsDate != null) {
				if(now.getYear() == smsDate.getYear() && 
						now.getMonth() == smsDate.getMonth() && 
						now.getDate() == smsDate.getDate()) {
					SimpleDateFormat format = new SimpleDateFormat(" HH:mm");
					strTime = this.getResources().getString(R.string.call_log_today) + format.format(smsDate);
				}else if(yestoday.getYear() == smsDate.getYear() && 
						yestoday.getMonth() == smsDate.getMonth() && 
								yestoday.getDate() == smsDate.getDate()) {
					SimpleDateFormat format = new SimpleDateFormat(" HH:mm");
					strTime = this.getResources().getString(R.string.call_log_yestoday) + format.format(smsDate);
				}else{
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					strTime = format.format(smsDate);
				}
			}
			
			map.put("date", strTime);
			map.put("id", sms.m_nId);
			map.put("storeIn", sms.m_storeIn);
			map.put("type", sms.m_nTag);
			map.put("content", sms.m_strContent);
			
			m_smsListData.add(map);
		}*/
		
		//test
		SMSDetailItem item3 = new SMSDetailItem();
		item3.bIsDateItem = true;
		item3.strTime = "2014-12-33";
		m_smsListData.add(item3);
		
		SMSDetailItem item = new SMSDetailItem();
		item.bIsDateItem = false;
		item.bSendFailed = true;
		item.strContent = "zhoudeqaun is super man!!!!!!!!come onfdsklfjsdjfsdfjdskfdsfdsjfsdjfahehvhflsdhf";
		item.strTime = "2014-12-33 33:33";
		item.nType = 0;
		m_smsListData.add(item);
		
		SMSDetailItem item2 = new SMSDetailItem();
		item2.bIsDateItem = false;
		item2.bSendFailed = false;
		item2.strContent = "zhoudeqaun is ";
		item2.strTime = "2014-12-33 33:33";
		item2.nType = 1;
		m_smsListData.add(item2);
		//test
	
		((SmsDetailListAdapter)m_smsListView.getAdapter()).notifyDataSetChanged();
	}
	
	private class SmsDetailListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
			
		public SmsDetailListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_smsListData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public LinearLayout dateLayout;
			public TextView date;
			public RelativeLayout smsContentLayout;
			public TextView smsContent;	
			public TextView smsDate;	
			public ImageView sentFail;
			public TextView sendFailText;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(ActivitySmsDetail.this).inflate(R.layout.sms_detail_item,null);	
				holder.dateLayout = (LinearLayout)convertView.findViewById(R.id.sms_detail_date_layout);
				holder.date = (TextView)convertView.findViewById(R.id.sms_detail_date_textview);
				holder.smsContentLayout = (RelativeLayout)convertView.findViewById(R.id.sms_detail_content_layout);
				holder.smsDate = (TextView)convertView.findViewById(R.id.sms_detail_date);
				holder.smsContent = (TextView)convertView.findViewById(R.id.sms_detail_content);
				holder.sentFail = (ImageView)convertView.findViewById(R.id.sms_sent_fail_image);
				holder.sendFailText = (TextView)convertView.findViewById(R.id.sms_sent_fail_text);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			if(m_smsListData.get(position).bIsDateItem == true) {
				holder.date.setText(m_smsListData.get(position).strTime);
				holder.dateLayout.setVisibility(View.VISIBLE);
				holder.smsContentLayout.setVisibility(View.GONE);
			}else{
				holder.dateLayout.setVisibility(View.GONE);
				holder.smsContentLayout.setVisibility(View.VISIBLE);
				holder.smsDate.setText(m_smsListData.get(position).strTime);
				holder.smsContent.setText((String)m_smsListData.get(position).strContent);
				holder.smsContentLayout.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						/*if(m_bDeleteSingleEnable == false)
							return true;
						final InquireDialog inquireDlg = new InquireDialog(ActivitySmsDetail.this);
						inquireDlg.m_titleTextView.setText(R.string.dialog_delete_title);
						inquireDlg.m_contentTextView.setText(R.string.dialog_delete_single_sms_content);
						inquireDlg.m_contentDescriptionTextView.setText("");
						inquireDlg.m_confirmBtn.setText(R.string.delete);
						inquireDlg.showDialog(new OnInquireApply() {
							@Override
							public void onInquireApply() {
								// TODO Auto-generated method stub
								m_deleteCounter  = 0;
								m_deleteSuccessNum  = 0;
								m_deleteNum = 1;
								m_bDeleteMult = false;
								//m_progressWaiting.setVisibility(View.VISIBLE);
								m_progress_dialog = ProgressDialog.show(ActivitySmsDetail.this, null, 
										getString(R.string.deleting),true, false);
				    			m_etContent.setEnabled(false);
				    			m_btnSend.setEnabled(false);
				    			m_btnDelete.setEnabled(false);
				    			m_bDeleteSingleEnable = false;
				    			if(m_smsListData.size() == 1) {
				    				m_bIsLastOneMessage = true;
				    			}else{
				    				m_bIsLastOneMessage = false;
				    			}
								DataValue data = new DataValue();
								data.addParam("id", (Integer)m_smsListData.get(position).get("id"));
								data.addParam("store_in",(SMSStoreIn)m_smsListData.get(position).get("storeIn"));
								BusinessMannager.getInstance().sendRequestMessage(MessageUti.SMS_DELETE_SMS_REQUSET, data);
								inquireDlg.closeDialog();
							}	
						});*/
						return true;
					}
					
				});
				LayoutParams contentLayout = (LayoutParams) holder.smsContentLayout.getLayoutParams();
				//LayoutParams contentLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				
				/*SMSTag tag = (SMSTag) m_smsListData.get(position).get("type");
				switch(tag)
				{
				case Read:
				case NotRead:	
				case Report:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT,R.id.sms_detail_content_layout);
					holder.smsContent.setBackgroundResource(R.drawable.selector_sms_detail_receive);
					break;
				case Sent:
				case NotSent:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,R.id.sms_detail_content_layout);
					holder.smsContent.setBackgroundResource(R.drawable.selector_sms_detail_out);
					break;
				default:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,R.id.sms_detail_content_layout);
					holder.smsContent.setBackgroundResource(R.drawable.selector_sms_detail_out);
					break;
				}*/
				int type = m_smsListData.get(position).nType;
				int nContentLayoutBg = R.drawable.selector_sms_detail_receive;
				switch(type)
				{
				case 0:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT,R.id.sms_detail_content_layout);
					nContentLayoutBg = R.drawable.selector_sms_detail_receive;
					contentLayout.setMargins(30, 10, 200, 10);
					break;
				default:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,R.id.sms_detail_content_layout);
					nContentLayoutBg = R.drawable.selector_sms_detail_out;
					contentLayout.setMargins(200, 10, 30, 10);
					break;
				}
				holder.smsContentLayout.setLayoutParams(contentLayout);
				holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);
				
				if(m_smsListData.get(position).bSendFailed == true) {
					holder.sentFail.setVisibility(View.VISIBLE);
					holder.sendFailText.setVisibility(View.VISIBLE);
				}else{
					holder.sentFail.setVisibility(View.GONE);
					holder.sendFailText.setVisibility(View.GONE);
				}
			}
			
			return convertView;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(visibleItemCount <= totalItemCount) {
			m_smsListView.setStackFromBottom(false);
		}else{
			m_smsListView.setStackFromBottom(true);
		}
	}
	
	public static class SMSDetailItem {
		public boolean bIsDateItem = false;
		public String strContent = new String();
		public String strTime = new String();
		public int nType = 0;
		public boolean bSendFailed = false;
	}
}
