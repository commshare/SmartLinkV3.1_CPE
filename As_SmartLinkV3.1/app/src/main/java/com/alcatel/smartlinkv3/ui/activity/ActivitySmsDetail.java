package com.alcatel.smartlinkv3.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.SMSManager;
import com.alcatel.smartlinkv3.business.model.SMSContentItemModel;
import com.alcatel.smartlinkv3.business.model.SmsContentMessagesModel;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSDelFlag;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSType;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.InquireDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.view.ViewSms;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ParseException;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class ActivitySmsDetail extends BaseActivity implements OnClickListener,
		OnScrollListener, TextWatcher {
	// public static final int ONE_SMS_LENGTH = 160;
	public static final String INTENT_EXTRA_SMS_NUMBER = "sms_number";
	public static final String INTENT_EXTRA_CONTACT_ID = "contact_id";

	private Timer m_timer = new Timer();
	private GetSMSContentTask m_getSMSContentTask = null;

	private TextView m_tvTitle = null;
	private Button m_btnSend = null;
	private Button m_btnDelete = null;
	private LinearLayout m_btnBack = null;
	private EditText m_etContent = null;

	private ListView m_smsListView = null;
	private ArrayList<SMSDetailItem> m_smsListData = new ArrayList<SMSDetailItem>();
	private String m_smsNumber = new String();
	private int m_nContactID = 0;

//	private TextView m_tvCnt = null;

	private ProgressBar m_progressWaiting = null;

	private boolean m_bDeleteContact = true;
	private boolean m_bIsLastOneMessage = false;

	private boolean m_bDeleteSingleEnable = true;
	private boolean m_bDeleteEnd = false;
	// private boolean m_bNeedFinish = false;
	private boolean m_bSendEnd = false;
	private SendStatus m_sendStatus = SendStatus.None;

	private boolean m_bFristGet = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_details_view);
		getWindow().setBackgroundDrawable(null);
		// Edit Text
		m_etContent = (EditText) this
				.findViewById(R.id.ID_SMS_DETAIL_EDIT_CONTENT);
		m_etContent.addTextChangedListener(this);

//		m_tvCnt = (TextView) findViewById(R.id.sms_cnt);
		String text = getOneSmsLenth(new String()) + "/1";
//		m_tvCnt.setText(text);
		// Text View
		m_tvTitle = (TextView) this.findViewById(R.id.ID_SMS_DETAIL_TITLE);
		// Button
		m_btnSend = (Button) this.findViewById(R.id.ID_SMS_DETAIL_BUTTON_SEND);
		m_btnSend.setOnClickListener(this);
		m_btnBack = (LinearLayout) this.findViewById(R.id.back_layout);
		m_btnBack.setOnClickListener(this);
		m_btnDelete = (Button) findViewById(R.id.delete_button);
		m_btnDelete.setOnClickListener(this);
		//
		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.sms_waiting_progress);

		m_smsNumber = this.getIntent().getStringExtra(INTENT_EXTRA_SMS_NUMBER);
		m_nContactID = this.getIntent().getIntExtra(INTENT_EXTRA_CONTACT_ID, 0);
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
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET));
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET));
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_GET_SEND_STATUS_REQUSET));
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_DELETE_SMS_REQUSET));
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_SEND_SMS_REQUSET));
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(
				MessageUti.SMS_SAVE_SMS_REQUSET));

		startGetSmsContentTask();
	}

	@Override
	public void onPause() {
		super.onPause();
		m_progressWaiting.setVisibility(View.GONE);

		m_etContent.setEnabled(true);
		// m_btnSend.setEnabled(true);
		m_bDeleteSingleEnable = true;
		// m_etContent.setText("");

		// m_bNeedFinish = false;
		m_bSendEnd = false;
		m_bDeleteEnd = false;

		stopGetSmsContentTask();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		super.onBroadcastReceive(context, intent);

		if (intent.getAction()
				.equalsIgnoreCase(MessageUti.SMS_SAVE_SMS_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String msgRes = null;
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				msgRes = this.getString(R.string.sms_save_success);
			} else if (strErrorCode
					.endsWith(ErrorCode.ERR_SAVE_SMS_SIM_IS_FULL)) {
				msgRes = this
						.getString(R.string.sms_error_message_full_storage);
			} else {
				msgRes = this.getString(R.string.sms_save_error);
			}
			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
			this.finish();
		}

		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				startGetSmsContentTask();
			}
		}

		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				SmsContentMessagesModel smsContent = intent
						.getParcelableExtra(SMSManager.SMS_CONTENT_LIST_EXTRA);
				getSmsListData(smsContent);
				m_btnDelete.setEnabled(true);
				if (m_bFristGet == true) {
					m_bFristGet = false;
					m_progressWaiting.setVisibility(View.GONE);
				}
			}
		}

		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				if (m_bDeleteEnd == true) {
					m_bDeleteEnd = false;
					m_progressWaiting.setVisibility(View.GONE);
					// if (m_progress_dialog != null &&
					// m_progress_dialog.isShowing()) {
					// m_progress_dialog.dismiss();
					// }
					m_etContent.setEnabled(true);
					if (m_etContent.getText().toString() != null
							&& m_etContent.getText().toString().length() > 0)
						m_btnSend.setEnabled(true);
					else
						m_btnSend.setEnabled(false);
					m_btnDelete.setEnabled(true);
					m_bDeleteSingleEnable = true;
				} else if (m_bSendEnd == true) {
					m_bSendEnd = false;
					m_progressWaiting.setVisibility(View.GONE);
					// if (m_progress_dialog != null &&
					// m_progress_dialog.isShowing()) {
					// m_progress_dialog.dismiss();
					// }
					m_etContent.setEnabled(true);
					m_btnDelete.setEnabled(true);
					m_bDeleteSingleEnable = true;
					// m_etContent.setText("");
					// m_btnSend.setEnabled(false);
					if (m_sendStatus == SendStatus.Success) {
						m_etContent.setText("");
						m_btnSend.setEnabled(false);
					} else if (m_sendStatus == SendStatus.Fail
							|| m_sendStatus == SendStatus.Fail_Memory_Full) {
						m_btnSend.setEnabled(true);
					}
				}
			}
		}

		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SMS_DELETE_SMS_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			m_bDeleteEnd = true;
			boolean bDeleteSeccuss = false;
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				bDeleteSeccuss = true;
			}

			if (m_bDeleteContact == true) {
				if (bDeleteSeccuss == true) {
					BusinessMannager.getInstance()
							.getContactMessagesAtOnceRequest();
					String msgRes = this
							.getString(R.string.sms_delete_multi_success);
					//Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
					this.finish();
				} else {
					String strMsg = getString(R.string.sms_delete_multi_error);
					Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
					m_progressWaiting.setVisibility(View.GONE);
					BusinessMannager.getInstance()
							.getContactMessagesAtOnceRequest();
					getSmsContentAtOnceRequest();
				}
			} else {
				m_progressWaiting.setVisibility(View.GONE);
				if (bDeleteSeccuss == true) {
					Toast.makeText(this,
							getString(R.string.sms_delete_success),
							Toast.LENGTH_SHORT).show();
					BusinessMannager.getInstance()
							.getContactMessagesAtOnceRequest();
					if (m_bIsLastOneMessage == true) {
						this.finish();
					}
				} else {
					Toast.makeText(this, getString(R.string.sms_delete_error),
							Toast.LENGTH_SHORT).show();
				}
				getSmsContentAtOnceRequest();
			}
		}

		if (intent.getAction()
				.equalsIgnoreCase(MessageUti.SMS_SEND_SMS_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				BusinessMannager.getInstance()
						.getContactMessagesAtOnceRequest();
				// getSmsContentAtOnceRequest();
			} else if (strErrorCode.endsWith(ErrorCode.ERR_SMS_SIM_IS_FULL)) {
				String msgRes = this
						.getString(R.string.sms_error_message_full_storage);
				// Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
				ToastUtil.showMessage(this, msgRes, Toast.LENGTH_SHORT);
				m_progressWaiting.setVisibility(View.GONE);
				// if (m_progress_dialog != null &&
				// m_progress_dialog.isShowing()) {
				// m_progress_dialog.dismiss();
				// }
				m_etContent.setEnabled(true);
				m_btnSend.setEnabled(true);
				m_btnDelete.setEnabled(true);
				m_bDeleteSingleEnable = true;
				m_bSendEnd = true;
				m_sendStatus = SendStatus.Fail_Memory_Full;
				getSmsContentAtOnceRequest();
			} else {
				String msgRes = this.getString(R.string.sms_error_send_error);
				Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
				m_progressWaiting.setVisibility(View.GONE);
				// if (m_progress_dialog != null &&
				// m_progress_dialog.isShowing()) {
				// m_progress_dialog.dismiss();
				// }
				m_etContent.setEnabled(true);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(m_etContent.getWindowToken(), 0);
				m_btnSend.setEnabled(true);
				m_btnDelete.setEnabled(true);
				m_bDeleteSingleEnable = true;
				m_bSendEnd = true;
				m_sendStatus = SendStatus.Fail;
				getSmsContentAtOnceRequest();
			}
		}

		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SMS_GET_SEND_STATUS_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (nResult == BaseResponse.RESPONSE_OK
					&& strErrorCode.length() == 0) {
				int nSendReslt = intent.getIntExtra(Const.SMS_SNED_STATUS, 0);
				SendStatus sendStatus = SendStatus.build(nSendReslt);
				m_sendStatus = sendStatus;
				boolean bEnd = false;
				if (sendStatus == SendStatus.Fail) {
					String msgRes = this
							.getString(R.string.sms_error_send_error);
					Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
					bEnd = true;
				}
				if (sendStatus == SendStatus.Fail_Memory_Full) {
					String msgRes = this
							.getString(R.string.sms_error_message_full_storage);
					// Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
					ToastUtil.showMessage(this, msgRes, Toast.LENGTH_SHORT);
					bEnd = true;
				}
				if (sendStatus == SendStatus.Success) {
					String msgRes = this.getString(R.string.sms_send_success);
					Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
					bEnd = true;
				}

				if (bEnd == true) {
					m_bSendEnd = true;
					getSmsContentAtOnceRequest();
				}
			} else {
				m_progressWaiting.setVisibility(View.GONE);
				// if (m_progress_dialog != null &&
				// m_progress_dialog.isShowing()) {
				// m_progress_dialog.dismiss();
				// }
				m_etContent.setEnabled(true);
				m_btnSend.setEnabled(true);
				m_btnDelete.setEnabled(true);
				m_bDeleteSingleEnable = true;
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_layout:
			onBackPressed();
			break;

		case R.id.ID_SMS_DETAIL_BUTTON_SEND:
			OnBtnSend();
			break;

		case R.id.delete_button:
			OnBtnDelete();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		OnBtnBack();
		super.onBackPressed();
	}

	//
	private void OnBtnBack() {
		BusinessMannager.getInstance().getContactMessagesAtOnceRequest();
		String strContent = m_etContent.getText().toString();
		String strNumber = m_smsNumber;
		if (strContent != null)
			strContent = strContent.trim();
		if (strContent != null && strContent.length() > 0 && strNumber != null
				&& strNumber.length() > 0) {
			DataValue data = new DataValue();
			//data.addParam("SMSId", m_nContactID);
			data.addParam("Content", strContent);
			data.addParam("Number", strNumber);
			//add by zhanghao for same Draft message display many time in draft message box 20160403
			SMSDetailItem item = m_smsListData.get(m_smsListData.size() - 1);
			if (null != item && item.eSMSType == EnumSMSType.Draft) 
			{
				//DataValue data1 = new DataValue();
				//data1.addParam("DelFlag", EnumSMSDelFlag.Delete_message);
				//data1.addParam("ContactId", item.nContactID);
				data.addParam("SMSId", item.nSMSID);
				//BusinessMannager.getInstance().sendRequestMessage(
						//MessageUti.SMS_DELETE_SMS_REQUSET, data1);
			}
			//end to add
			
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SMS_SAVE_SMS_REQUSET, data);
		} else {
			this.finish();
			if (m_smsListData.size() == 0)
				return;
			SMSDetailItem item = m_smsListData.get(m_smsListData.size() - 1);
			if (null != item && item.eSMSType == EnumSMSType.Draft) {
				DataValue data = new DataValue();
				data.addParam("DelFlag", EnumSMSDelFlag.Delete_message);
				data.addParam("ContactId", item.nContactID);
				data.addParam("SMSId", item.nSMSID);
				BusinessMannager.getInstance().sendRequestMessage(
						MessageUti.SMS_DELETE_SMS_REQUSET, data);
			}
		}
	}

	private void OnBtnDelete() {
		final InquireDialog inquireDlg = new InquireDialog(
				ActivitySmsDetail.this);
		inquireDlg.m_titleTextView.setText(R.string.dialog_delete_title);
		inquireDlg.m_contentTextView.setText(R.string.dialog_delete_content);
		inquireDlg.m_contentDescriptionTextView
				.setText(R.string.dialog_delete_content_description);
		inquireDlg.m_confirmBtn.setText(R.string.delete);
		inquireDlg.showDialog(new OnInquireApply() {
			@Override
			public void onInquireApply() {
				// TODO Auto-generated method stub

				m_bDeleteContact = true;
				m_progressWaiting.setVisibility(View.VISIBLE);
				m_etContent.setEnabled(false);
				m_btnSend.setEnabled(false);
				m_btnDelete.setEnabled(false);
				m_bDeleteSingleEnable = false;

				boolean bHaveSms = false;
				for (int i = 0; i < m_smsListData.size(); i++) {
					SMSDetailItem item = m_smsListData.get(i);
					if (item.bIsDateItem == false) {
						bHaveSms = true;
						break;
					}
				}

				if (bHaveSms == true) {
					DataValue data = new DataValue();
					data.addParam("DelFlag",
							EnumSMSDelFlag.Delete_contact_messages);
					data.addParam("ContactId", m_nContactID);
					BusinessMannager.getInstance().sendRequestMessage(
							MessageUti.SMS_DELETE_SMS_REQUSET, data);
				}
				inquireDlg.closeDialog();
			}
		});
	}

	private int getDeleteMessagesNumber() {
		int nNumber = 0;
		for (int i = 0; i < m_smsListData.size(); i++) {
			SMSDetailItem item = m_smsListData.get(i);
			if (item.bIsDateItem == false)
				nNumber++;
		}
		return nNumber;
	}

	//
	private void OnBtnSend() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(m_etContent.getWindowToken(), 0);

		DataValue data = new DataValue();
		data.addParam("content", m_etContent.getText().toString());
		data.addParam("phone_number", m_tvTitle.getText().toString());
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SMS_SEND_SMS_REQUSET, data);
		m_progressWaiting.setVisibility(View.VISIBLE);
		// m_progress_dialog = ProgressDialog.show(ActivitySmsDetail.this, null,
		// getString(R.string.sending),true, false);
		m_btnSend.setEnabled(false);
		m_etContent.setEnabled(false);
		m_btnDelete.setEnabled(false);
		m_bDeleteSingleEnable = false;
	}

	private void showNewSmsCnt(CharSequence s) {

	}

	public static int getOneSmsLenth(String strSmsContent) {
		if (null == strSmsContent)
			return 160;

		if (strSmsContent.length() < strSmsContent.getBytes().length) {
			return 67;
		} else {
			return 160;
		}

	}

	//
	public void ShowErrorMsg(String strTitle, String strMsg) {
		/*
		 * String strOK = getString(R.string.IDS_SMS_ERROR_BUTTON_OK); new
		 * AlertDialog.Builder(this).setTitle(strTitle).setMessage(strMsg)
		 * .setPositiveButton(strOK, null).show();
		 */
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		String strInfo = s == null ? null : s.toString();
		if (s != null && strInfo.length() > 0) {
			m_btnSend.setEnabled(true);

		} else {
			m_btnSend.setEnabled(false);
		}

		int sms[] = SmsMessage.calculateLength(s, false);
		int msgCount = sms[0];
		int codeUnitCount = sms[1];
		int codeUnitsRemaining = sms[2];
		int codeUnitSize = sms[3];

//		m_tvCnt.setText(codeUnitsRemaining + "/" + msgCount);

		if (msgCount > 10) {
			int nMax= 670;
			if(codeUnitSize == 1)
			{
				nMax = 1530;
			}
			int selEndIndex = Selection.getSelectionEnd(s);
			String newStr = strInfo.substring(0, nMax);
			m_etContent.setText(newStr);

			Editable editable = m_etContent.getText();
			int newLen = editable.length();

			if (selEndIndex > newLen) {
				selEndIndex = editable.length();
			}

			Selection.setSelection(editable, selEndIndex);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	private class SortSmsListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SMSContentItemModel c1 = (SMSContentItemModel) o1;
			SMSContentItemModel c2 = (SMSContentItemModel) o2;
			Date d1 = DataUti.formatDateFromString(c1.SMSTime);
			Date d2 = DataUti.formatDateFromString(c2.SMSTime);
			if (d1.after(d2) == true)
				return 1;
			if (d1.equals(d2) == true)
				return 0;
			return -1;
		}
	}


	private void getSmsListData(SmsContentMessagesModel smsContent) {
		Collections.sort(smsContent.SMSContentList, new SortSmsListByTime());
		m_smsListData.clear();

		for (int i = 0; i < smsContent.SMSContentList.size(); i++) {
			SMSContentItemModel sms = smsContent.SMSContentList.get(i);

			Date smsDate = DataUti.formatDateFromString(sms.SMSTime);

			if (i == 0) {
				SMSDetailItem item = new SMSDetailItem();
				item.bIsDateItem = true;
				if (smsDate != null) {
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					item.strTime = format.format(smsDate);
				}

				m_smsListData.add(item);
			} else {
				Date preSmsDate = DataUti.formatDateFromString(smsContent.SMSContentList
						.get(i - 1).SMSTime);
				if (!(smsDate.getYear() == preSmsDate.getYear()
						&& smsDate.getMonth() == preSmsDate.getMonth() && smsDate
							.getDate() == preSmsDate.getDate())) {
					SMSDetailItem item = new SMSDetailItem();
					item.bIsDateItem = true;
					if (smsDate != null) {
						SimpleDateFormat format = new SimpleDateFormat(
								"dd/MM/yyyy");
						item.strTime = format.format(smsDate);
					}

					m_smsListData.add(item);
				}
			}

			SMSDetailItem item = new SMSDetailItem();
			item.bIsDateItem = false;
			item.strContent = sms.SMSContent;
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			item.strTime = format.format(smsDate);
			// item.strTime = sms.SMSTime;
			item.eSMSType = sms.SMSType;
			item.nContactID = m_nContactID;
			item.nSMSID = sms.SMSId;
			m_smsListData.add(item);
		}

		DarftSMSDisplay();

		// test
		/*
		 * SMSDetailItem item3 = new SMSDetailItem(); item3.bIsDateItem = true;
		 * item3.strTime = "2014-12-33"; m_smsListData.add(item3);
		 * 
		 * SMSDetailItem item = new SMSDetailItem(); item.bIsDateItem = false;
		 * item.bSendFailed = false; item.strContent =
		 * "zhoudeqaun is super man!!!!!!!!come onfdsklfjsdjfsdfjdskfdsfdsjfsdjfahehvhflsdhf"
		 * ; item.strTime = "2014-12-33 33:33"; item.nType = 0;
		 * m_smsListData.add(item);
		 * 
		 * SMSDetailItem item2 = new SMSDetailItem(); item2.bIsDateItem = false;
		 * item2.bSendFailed = true; item2.strContent = "zhoudeqaun is ";
		 * item2.strTime = "2014-12-33 33:33"; item2.nType = 1;
		 * m_smsListData.add(item2);
		 */
		// test

		((SmsDetailListAdapter) m_smsListView.getAdapter())
				.notifyDataSetChanged();
	}

	private void DarftSMSDisplay() {
		if (m_smsListData.size() == 0)
			return;
		SMSDetailItem item = m_smsListData.get(m_smsListData.size() - 1);
		if (item.eSMSType == EnumSMSType.Draft && !m_etContent.isFocused()) {
			m_etContent.setEnabled(true);
			m_etContent.setText(item.strContent);
		}
	}

	private class SmsDetailListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SmsDetailListAdapter(Context context) {
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

		public final class ViewHolder {
			public FrameLayout dateLayout;
			public TextView date;
			public RelativeLayout smsContentLayout;
			public TextView smsContent;
			public TextView smsDate;
			public ImageView sentFail;
			public TextView sendFailText;
			public View placeHolder;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ActivitySmsDetail.this)
						.inflate(R.layout.sms_detail_item, null);
				holder.dateLayout = (FrameLayout) convertView
						.findViewById(R.id.sms_detail_date_layout);
				holder.date = (TextView) convertView
						.findViewById(R.id.sms_detail_date_textview);
				holder.smsContentLayout = (RelativeLayout) convertView
						.findViewById(R.id.sms_detail_content_layout);
				holder.smsDate = (TextView) convertView
						.findViewById(R.id.sms_detail_date);
				holder.smsContent = (TextView) convertView
						.findViewById(R.id.sms_detail_content);
				holder.sentFail = (ImageView) convertView
						.findViewById(R.id.sms_sent_fail_image);
				holder.sendFailText = (TextView) convertView
						.findViewById(R.id.sms_sent_fail_text);
				holder.placeHolder = (View) convertView
						.findViewById(R.id.place_holder);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == m_smsListData.size() - 1)
				holder.placeHolder.setVisibility(View.VISIBLE);
			else
				holder.placeHolder.setVisibility(View.GONE);

			if (m_smsListData.get(position).bIsDateItem == true) {
				holder.date.setText(m_smsListData.get(position).strTime);
				holder.dateLayout.setVisibility(View.VISIBLE);
				holder.smsContentLayout.setVisibility(View.GONE);
			} else {
				holder.dateLayout.setVisibility(View.GONE);
				holder.smsContentLayout.setVisibility(View.VISIBLE);
				holder.smsDate.setText(m_smsListData.get(position).strTime);

				holder.smsContent
						.setText((String) m_smsListData.get(position).strContent);
				holder.smsContentLayout
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								// TODO Auto-generated method stub
								if (m_bDeleteSingleEnable == false)
									return true;
								final InquireDialog inquireDlg = new InquireDialog(
										ActivitySmsDetail.this);
								inquireDlg.m_titleTextView
										.setText(R.string.dialog_delete_title);
								inquireDlg.m_contentTextView
										.setText(R.string.dialog_delete_single_sms_content);
								inquireDlg.m_contentDescriptionTextView
										.setText("");
								inquireDlg.m_confirmBtn
										.setText(R.string.delete);
								inquireDlg.showDialog(new OnInquireApply() {
									@Override
									public void onInquireApply() {
										// TODO Auto-generated method stub
										m_bDeleteContact = false;
										m_progressWaiting
												.setVisibility(View.VISIBLE);
										// m_progress_dialog =
										// ProgressDialog.show(ActivitySmsDetail.this,
										// null,
										// getString(R.string.deleting),true,
										// false);
										m_etContent.setEnabled(false);
										m_btnSend.setEnabled(false);
										m_btnDelete.setEnabled(false);
										m_bDeleteSingleEnable = false;
										if (getDeleteMessagesNumber() == 1) {
											m_bIsLastOneMessage = true;
										} else {
											m_bIsLastOneMessage = false;
										}
										DataValue data = new DataValue();
										data.addParam("DelFlag",
												EnumSMSDelFlag.Delete_message);
										data.addParam(
												"ContactId",
												m_smsListData.get(position).nContactID);
										data.addParam(
												"SMSId",
												m_smsListData.get(position).nSMSID);
										BusinessMannager
												.getInstance()
												.sendRequestMessage(
														MessageUti.SMS_DELETE_SMS_REQUSET,
														data);
										inquireDlg.closeDialog();
									}
								});
								return true;
							}

						});

				// LayoutParams contentLayout = (LayoutParams)
				// holder.smsContentLayout.getLayoutParams();
				LayoutParams contentLayout = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				final EnumSMSType type = m_smsListData.get(position).eSMSType;
				int nContentLayoutBg = R.drawable.selector_sms_detail_receive;
				holder.smsContentLayout.setVisibility(View.VISIBLE);
				switch (type) {
				case Report:
					holder.smsContent.setText(R.string.sms_report_message);
				case Read:
				case Unread:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
							R.id.sms_layout);
					nContentLayoutBg = R.drawable.selector_sms_detail_receive;
					contentLayout.setMargins(30, 10, 80, 10);
					holder.smsContent.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(R.color.color_black));
					
					holder.smsDate.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(R.color.color_grey));
					holder.sendFailText.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(R.color.color_grey));
					holder.sentFail
							.setImageResource(R.drawable.warning_blue_bg);
					break;
				case Draft:
					if (position == m_smsListData.size() - 1) {
						holder.smsContentLayout.setVisibility(View.GONE);
					}
				default:
					contentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
							R.id.sms_layout);
					nContentLayoutBg = R.drawable.selector_sms_detail_out;
					contentLayout.setMargins(80, 10, 30, 10);
					holder.smsContent.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(R.color.color_white));
					holder.smsDate.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(
									R.color.color_sms_detail_send_grey));
					holder.sendFailText.setTextColor(ActivitySmsDetail.this
							.getResources().getColor(
									R.color.color_sms_detail_send_grey));
					holder.sentFail
							.setImageResource(R.drawable.sms_failed_white_bg);
					break;
				}
				holder.smsContentLayout.setLayoutParams(contentLayout);
				holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);

				if (type == EnumSMSType.SentFailed) {
					holder.sentFail.setVisibility(View.VISIBLE);
					holder.sendFailText.setVisibility(View.VISIBLE);
				} else {
					holder.sentFail.setVisibility(View.GONE);
					holder.sendFailText.setVisibility(View.GONE);
				}

				holder.smsContentLayout
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (type == EnumSMSType.SentFailed) {
									FailedSMSClick(m_smsListData.get(position));
								}
							}
						});
			}

			return convertView;
		}
	}

	private void FailedSMSClick(SMSDetailItem item) {
		m_etContent.setEnabled(true);
		m_etContent.setText(item.strContent);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (visibleItemCount <= totalItemCount) {
			m_smsListView.setStackFromBottom(true);
		} else {
			m_smsListView.setStackFromBottom(false);
		}
	}

	public static class SMSDetailItem {
		public boolean bIsDateItem = false;
		public String strContent = new String();
		public String strTime = new String();
		public EnumSMSType eSMSType = EnumSMSType.Read;
		public int nContactID = 0;
		public int nSMSID = 0;
	}

	class GetSMSContentTask extends TimerTask {
		@Override
		public void run() {
			DataValue data = new DataValue();
			data.addParam("ContactId", m_nContactID);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET, data);
		}
	}

	private void startGetSmsContentTask() {
		if (BusinessMannager.getInstance().getSMSInit() == SMSInit.Initing)
			return;

		if (m_getSMSContentTask == null) {
			m_getSMSContentTask = new GetSMSContentTask();
			m_timer.scheduleAtFixedRate(m_getSMSContentTask, 0, 30 * 1000);
		}
	}

	public void getSmsContentAtOnceRequest() {
		if (BusinessMannager.getInstance().getSMSInit() == SMSInit.Initing)
			return;

		GetSMSContentTask task = new GetSMSContentTask();
		m_timer.schedule(task, 0);
	}

	private void stopGetSmsContentTask() {
		if (m_getSMSContentTask != null) {
			m_getSMSContentTask.cancel();
			m_getSMSContentTask = null;
		}
	}
}
