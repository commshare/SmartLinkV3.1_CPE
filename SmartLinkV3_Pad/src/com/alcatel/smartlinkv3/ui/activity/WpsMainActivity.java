package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;



public class WpsMainActivity extends BaseActivity implements OnClickListener{

	private ImageButton bnBack;
	private TextView tvback;
	
	private RadioGroup mRadioGroup;  
	private RadioButton mRadio0,mRadio1;
	private LinearLayout mWpsPin; 
	private LinearLayout mPbc;
	
	private EditText m_wpspinValue;
	private Button m_wpspinBtn;
	
	private ImageButton m_bnpbc;
	public static final String ERR_SET_WPS_PIN_FAILED = "050601";
	public static final String ERR_SET_WPS_PIN_NOT_SUPPORT = "050602";
	public static final String ERR_SET_WPS_PIN_IS_WORKING = "050603";
	
	private WpsSettingReceiver m_wpsreceiver = new WpsSettingReceiver();
	
	private class WpsSettingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MessageUti.WLAN_SET_WPS_PBC_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				String msgRes = null;
				if (nResult == 0 && strErrorCode.length() == 0) {
					msgRes = WpsMainActivity.this.getString(R.string.wps_setpbcsuccess);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else {
					msgRes = WpsMainActivity.this.getString(R.string.wps_setpbcfailed);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}
			} else if (intent.getAction().equals(MessageUti.WLAN_SET_WPS_PIN_REQUSET)) {

				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				String msgRes = null;
				if (nResult == 0 && strErrorCode.length() == 0) {
					msgRes = WpsMainActivity.this.getString(R.string.wps_setwpspinsuccess);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else if (nResult == 0 && strErrorCode
						.equalsIgnoreCase(ERR_SET_WPS_PIN_FAILED)) {
					msgRes = WpsMainActivity.this.getString(R.string.wps_setwpspinmodefailed);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else if(nResult == 0 && strErrorCode
						.equalsIgnoreCase(ERR_SET_WPS_PIN_NOT_SUPPORT)){
					msgRes = WpsMainActivity.this.getString(R.string.wps_setwpspinnotsupport);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else if(nResult == 0 && strErrorCode
						.equalsIgnoreCase(ERR_SET_WPS_PIN_IS_WORKING)){
					msgRes = WpsMainActivity.this.getString(R.string.wps_setwpspinisworking);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else {
					msgRes = WpsMainActivity.this.getString(R.string.wps_setwpspinfailed);
					Toast.makeText(WpsMainActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}		
			}
		}
	}
	
	private void registerReceiver() {
		// advanced
		this.registerReceiver(m_wpsreceiver, new IntentFilter(
				MessageUti.WLAN_SET_WPS_PBC_REQUSET));
		this.registerReceiver(m_wpsreceiver, new IntentFilter(
				MessageUti.WLAN_SET_WPS_PIN_REQUSET));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wps_view);
		m_bNeedBack = false;
		
		init();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void init(){
        mRadioGroup=(RadioGroup) findViewById(R.id.radioGroup);  
        mRadioGroup.setOnCheckedChangeListener(new RadioButtonOnCheckedChangeListenerImpl()); 
        
        mRadio0 = (RadioButton)findViewById(R.id.radio0);  
        mRadio1 = (RadioButton)findViewById(R.id.radio1); 
        
        mWpsPin=(LinearLayout)findViewById(R.id.include_pin_view);
        mPbc=(LinearLayout)findViewById(R.id.include_pbc_view);
        
        bnBack = (ImageButton) findViewById(R.id.btn_back); 
        bnBack.setOnClickListener(this);
        tvback = (TextView) this.findViewById(R.id.Back);
		tvback.setOnClickListener(this);
        
        m_wpspinValue = (EditText) this.findViewById(R.id.wps_pin_value);
		m_wpspinValue.setText("");
        m_wpspinBtn = (Button) findViewById(R.id.wps_save_button); 
        m_wpspinBtn.setOnClickListener(this);
        
        m_bnpbc = (ImageButton) findViewById(R.id.pbc_button); 
        m_bnpbc.setOnClickListener(this);
        

	}
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver();
		initWpsPinEdit();
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
    		this.unregisterReceiver(m_wpsreceiver);
    	}catch(Exception e) {
    		
    	}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
		case R.id.Back:
			this.finish();
			break;
		
		case R.id.wps_save_button:
			onBtnWpsPinSaveClick();
			break;
			
		case R.id.pbc_button:
			onBtnPbcClick();
			break;

		}
		
	}
	
	private class RadioButtonOnCheckedChangeListenerImpl implements OnCheckedChangeListener {  
         @Override  
         public void onCheckedChanged(RadioGroup group, int checkedId) {  

        	 if (checkedId == mRadio0.getId())  
                {  
        		 	mWpsPin.setVisibility(View.GONE);
        		 	mPbc.setVisibility(View.VISIBLE); 
                }  
                else if(checkedId == mRadio1.getId())
                {  
                	mWpsPin.setVisibility(View.VISIBLE);
        		 	mPbc.setVisibility(View.GONE);
                }  
         }  
     } 

	
	private void initWpsPinEdit() {
		m_wpspinValue = (EditText) this.findViewById(R.id.wps_pin_value);
		m_wpspinValue.setText("");
	}
	
	public void onBtnWpsPinSaveClick() {

		String strwpsPin = m_wpspinValue.getText().toString();
		DataValue data = new DataValue();
		data.addParam("WpsPin", strwpsPin);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.WLAN_SET_WPS_PIN_REQUSET, data);
		
	}
	
	public void onBtnPbcClick() {
		DataValue data = new DataValue();
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.WLAN_SET_WPS_PBC_REQUSET, data);
	}

}