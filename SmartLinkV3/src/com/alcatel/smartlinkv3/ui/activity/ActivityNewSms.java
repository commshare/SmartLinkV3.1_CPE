package com.alcatel.smartlinkv3.ui.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityNewSms extends BaseActivity implements OnClickListener {
	private TextView m_btnCancel = null;
	private LinearLayout m_btnBack = null;
	private Button m_btnSend = null;
	private EditText m_etNumber = null;
	private EditText m_etContent = null;
	private static final String NUMBER_REG_Ex = "^([pw+*#\\d]){1}(\\d|[;pw*#]){0,99}$";
	private String m_preMatchNumber = new String();
	
	private TextView m_tvCnt = null;
	
	private ProgressBar m_progressWaiting = null;
	
	private SendStatus m_sendStatus = SendStatus.None;
	private boolean m_bSendEnd = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_new_view);

		// get controls
		m_btnCancel = (TextView) findViewById(R.id.cancel);
		m_btnCancel.setOnClickListener(this);
		m_btnBack = (LinearLayout) findViewById(R.id.back_layout);
		m_btnBack.setOnClickListener(this);
		m_btnSend = (Button) findViewById(R.id.send_btn);
		m_btnSend.setEnabled(false);
		m_btnSend.setOnClickListener(this);
		m_etNumber = (EditText) findViewById(R.id.edit_number);
		m_etNumber.setText("");
		m_etContent = (EditText) findViewById(R.id.edit_content);
		m_etContent.setText("");
		setEditTextChangedListener();
		
		m_tvCnt = (TextView)findViewById(R.id.sms_cnt);
		String text = ActivitySmsDetail.ONE_SMS_LENGTH+"/1"; 
		m_tvCnt.setText(text);
		
		m_progressWaiting = (ProgressBar) this.findViewById(R.id.sms_new_waiting_progress);
	}
	
	private void setEditTextChangedListener() {
		m_etNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				// TODO Auto-generated method stub
				String editable = m_etNumber.getText().toString();  
		        String str = stringFilter(editable.toString());
		        if(!editable.equals(str)){
		        	m_etNumber.setText(str);
		              //set cursor position
		        	m_etNumber.setSelection(start);
		        }

				String strNumber = str;
				String strContent = m_etContent.getText().toString();
				if(strNumber != null && strContent != null && strNumber.length() != 0 && strContent.length() != 0) {
					m_btnSend.setEnabled(true);
				}else{
					m_btnSend.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		m_etContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				// TODO Auto-generated method stub
				String strNumber = m_etNumber.getText() == null?null:m_etNumber.getText().toString();
				String strContent = s == null?null:s.toString();
				if(strNumber != null && strContent != null && strNumber.length() != 0 && strContent.length() != 0) {
					m_btnSend.setEnabled(true);
				}else{
					m_btnSend.setEnabled(false);
				}
				
				showNewSmsCnt(s);
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	
	private String stringFilter(String str) {                           
		Pattern   p   =   Pattern.compile(NUMBER_REG_Ex);     
		Matcher   m   =   p.matcher(str); 
		//return   m.replaceAll("").trim();
		if(m.matches() || str == null || str.length() == 0) {
			m_preMatchNumber = str;
			return str;
		}else{
			return m_preMatchNumber;
		}
	}


	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		
		/*if(intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET)){				
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				if(m_bSendEnd == true){
					m_bSendEnd = false;
					m_progressWaiting.setVisibility(View.GONE);
					m_btnSend.setEnabled(true);
	    			m_etNumber.setEnabled(true);
	    			m_etContent.setEnabled(true);
	    			if(m_sendStatus == SendStatus.Success) {
	    				this.finish();
	    			}
				}
			}
		}*/
		
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
    			m_progressWaiting.setVisibility(View.GONE);
    			m_btnSend.setEnabled(true);
    			m_etNumber.setEnabled(true);
    			m_etContent.setEnabled(true);	
			}
			else {
				String msgRes = this.getString(R.string.IDS_SMS_ERROR_SEND_ERROR);
    			Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    			m_progressWaiting.setVisibility(View.GONE);
    			m_btnSend.setEnabled(true);
    			m_etNumber.setEnabled(true);
    			m_etContent.setEnabled(true);
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
					//BusinessMannager.getInstance().refreshSmsListAtOnce();
					
					m_progressWaiting.setVisibility(View.GONE);
					m_btnSend.setEnabled(true);
	    			m_etNumber.setEnabled(true);
	    			m_etContent.setEnabled(true);
	    			if(sendStatus == SendStatus.Success) {
	    				this.finish();
	    			}
				}
			}else{
				m_progressWaiting.setVisibility(View.GONE);
    			m_btnSend.setEnabled(true);
    			m_etNumber.setEnabled(true);
    			m_etContent.setEnabled(true);
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_SEND_SMS_REQUSET));		
		this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SEND_STATUS_REQUSET));
		//this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET));
	}

	@Override
	public void onPause() {
		super.onPause();
		m_progressWaiting.setVisibility(View.GONE);
		if(m_etNumber.getText().toString() != null && m_etNumber.getText().toString().length() != 0 &&
				m_etContent.getText().toString() != null && m_etContent.getText().toString().length() != 0)
			m_btnSend.setEnabled(true);
		m_etNumber.setEnabled(true);
		m_etContent.setEnabled(true);
		//m_etNumber.setText("");
		//m_etContent.setText("");
		
		m_bSendEnd = false;
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cancel:
		case R.id.back_layout:
			OnBtnCancel();
			break;

		case R.id.send_btn:
			OnBtnSend();
			break;
		default:
			break;
		}
	}

	//
	private void OnBtnCancel() {
		
		this.finish();
	}

	//
	private void OnBtnSend() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

		DataValue data = new DataValue();
		data.addParam("content", m_etContent.getText().toString());
		data.addParam("phone_number", m_etNumber.getText().toString());
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SMS_SEND_SMS_REQUSET, data);
		
		m_progressWaiting.setVisibility(View.VISIBLE);
		m_btnSend.setEnabled(false);
		m_etNumber.setEnabled(false);
		m_etContent.setEnabled(false);
	}
	
	private void showNewSmsCnt(CharSequence s)
	{
		
		int nRemain = ActivitySmsDetail.ONE_SMS_LENGTH - s.length() % ActivitySmsDetail.ONE_SMS_LENGTH;
		
		if(s.length() >= ActivitySmsDetail.ONE_SMS_LENGTH  && nRemain == ActivitySmsDetail.ONE_SMS_LENGTH )
		{			 
			nRemain = 0;
		}
		
		int nCnt = 1;
		
		if(s.length() > ActivitySmsDetail.ONE_SMS_LENGTH)
		{
			nCnt = (s.length() -1 ) / ActivitySmsDetail.ONE_SMS_LENGTH +1;	
		}	
		
		m_tvCnt.setText(nRemain + "/" + nCnt);		
	}
}
