package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WModeEnum;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.view.CustomSpinner;
import com.alcatel.smartlinkv3.ui.view.CustomSpinner.OnSpinnerItemSelectedListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingWifiActivity extends BaseActivity 
implements OnClickListener,OnSpinnerItemSelectedListener{

	private String STRING_WEP = "WEP";
	private String STRING_WPA = "WPA";
	private String STRING_WPA2 = "WPA2";
	private String STRING_WPA_WPA2 = "WPA/WPA2";
	private String STRING_AUTO = "Auto";
	private String STRING_AES = "AES";
	private String STRING_TKIP = "TKIP";
	private String STRING_OPEN = "open";
	private String STRING_SHARE = "share";
	//
	private boolean m_blPasswordOpened = false;
	private int m_nPreWlanAPMode = -1;
	private int m_nWlanAPMode = -1;
	private String m_strPreSsid = "";
	private String m_strSsid = "";
	private int m_nPreSecurityMode=0;
	private int m_nSecurityMode=0;
	private int m_nPreType=0;
	private int m_nType=0;
	private String m_strPreKey = "";
	private String m_strKey="";
	//
	private EditText m_et_password;
	private EditText m_et_ssid;
	private ImageButton m_ib_show_password;
	private ImageButton m_ib_hide_password;
	private ImageButton m_ib_back;
	private TextView m_tv_back;
	private TextView m_tv_edit;
	private TextView m_tv_done;
	private FrameLayout m_fl_titlebar;
	private Boolean m_blSupportMultiWifiMode=false;
	private RadioGroup m_rg_wifi_mode;
	private RadioButton m_rb_2point4G_wifi;
	private RadioButton m_rb_5G_wifi;
	private TextView m_tv_no_password;
	private Button m_btn_psd_switch;
	//spiners
	private LinearLayout m_ll_security;
	private LinearLayout m_ll_encryption;
	private TextView m_tv_psd_type_title;
	private CustomSpinner m_securitySpinner;
	private CustomSpinner m_encryptionSpinner;
	private TextView m_passwordPrompt;
	private TextView m_encryptionPrompt;
	private String m_curWPAPassword = "";
	private String m_curWEPPassword = "";

	private ArrayList<String> m_securityOptions = null;
	private ArrayList<String> m_wpaTypeOptions = null;
	private ArrayList<String> m_wpaTypeOptions2 = null;
	private ArrayList<String> m_wepTypeOptions = null;

	private ArrayAdapter<String> m_wepEncryptionadapter = null;
	private ArrayAdapter<String> m_wpaEncryptionadapter = null;
	private ArrayAdapter<String> m_wpaEncryptionadapter2 = null;
	//
	private CommonErrorInfoDialog m_err_dialog;
	private String m_strErrorInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_wifi);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

		m_blSupportMultiWifiMode= true;
		synchValues();
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		//init controls state
		setControlsDoneStatus();
	}

	private void controlTitlebar(){
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_fl_titlebar = (FrameLayout)findViewById(R.id.fl_edit_or_done);
		m_fl_titlebar.setVisibility(View.VISIBLE);
		m_tv_edit = (TextView)findViewById(R.id.tv_titlebar_edit);
		m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_tv_edit.setVisibility(View.VISIBLE);
		m_tv_done.setVisibility(View.GONE);
		//
		m_tv_edit.setOnClickListener(this);
		m_tv_done.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
		m_ib_back.setOnClickListener(this);
	}

	private void createControls(){
		m_et_ssid = (EditText)findViewById(R.id.edit_ssid);
		m_et_password = (EditText)findViewById(R.id.edit_password);
		m_ib_show_password = (ImageButton)findViewById(R.id.ib_show_password);
		m_ib_hide_password = (ImageButton)findViewById(R.id.ib_hide_password);
		m_rg_wifi_mode = (RadioGroup)findViewById(R.id.rg_wifi_mode);
		m_rb_2point4G_wifi = (RadioButton)findViewById(R.id.rb_2point4G_wifi);
		m_rb_5G_wifi = (RadioButton)findViewById(R.id.rb_5G_wifi);
		//
		if(!m_ib_show_password.isShown()){
			m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else {
			m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}

		m_ib_hide_password.setOnClickListener(this);
		m_ib_show_password.setOnClickListener(this);

		if(m_blSupportMultiWifiMode){
			m_rg_wifi_mode.setVisibility(View.VISIBLE);
		}else {
			m_rg_wifi_mode.setVisibility(View.GONE);
		}

		if (WlanSupportMode.Mode2Point4GAnd5G != BusinessMannager.getInstance().getWlanSupportMode()) {
			m_rg_wifi_mode.setVisibility(View.GONE);
		}

		//
		m_tv_no_password = (TextView)findViewById(R.id.tv_no_psd);
		m_btn_psd_switch = (Button)findViewById(R.id.btn_psd_switch);
		m_btn_psd_switch.setOnClickListener(this);

		m_err_dialog = CommonErrorInfoDialog.getInstance(this);
		//init spiner
		initSpiners();
	}

	private void initSpiners(){
		initSpinnerListString();
		m_wpaEncryptionadapter = new ArrayAdapter<String>(this, 
				R.layout.custom_spinner_simple_item,
				R.id.spinner_sample_lable, 
				m_wpaTypeOptions);
		m_wpaEncryptionadapter2 = new ArrayAdapter<String>(this, 
				R.layout.custom_spinner_simple_item,
				R.id.spinner_sample_lable, 
				m_wpaTypeOptions2);
		m_wepEncryptionadapter = new ArrayAdapter<String>(this, 
				R.layout.custom_spinner_simple_item,
				R.id.spinner_sample_lable, 
				m_wepTypeOptions);
		m_securitySpinner = (CustomSpinner)findViewById(R.id.security_spinner); 
		m_securitySpinner.SetOnSpinnerItemSelectedListener(this);
		m_encryptionPrompt = (TextView)findViewById(R.id.tv_encryption);
		m_encryptionSpinner = (CustomSpinner)findViewById(R.id.encryption_spinner); 
		m_encryptionSpinner.SetOnSpinnerItemSelectedListener(this);
		m_passwordPrompt = (TextView)findViewById(R.id.tv_psd_type_title);
		m_ll_security = (LinearLayout)findViewById(R.id.ll_security);
		m_ll_encryption = (LinearLayout)findViewById(R.id.ll_encryption);
		m_tv_psd_type_title = (TextView)findViewById(R.id.tv_psd_type_title);
		initSpinersUI();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nbtnID = v.getId();
		switch(nbtnID){
		case R.id.ib_hide_password:
			m_ib_show_password.setVisibility(View.VISIBLE);
			m_ib_hide_password.setVisibility(View.GONE);
			m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			break;
		case R.id.ib_show_password:
			m_ib_hide_password.setVisibility(View.VISIBLE);
			m_ib_show_password.setVisibility(View.GONE);
			m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			//hide keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			boolean isOpen=imm.isActive();
			if (isOpen) {
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}

			SettingWifiActivity.this.finish();
			break;
		case R.id.tv_titlebar_edit:
			onBtnEdit();
			break;
		case R.id.tv_titlebar_done:
			onBtnDone();
			break;

		case R.id.btn_psd_switch:
			onBtnPasswordSwitch();
			break;
		default:
			break;

		}
	}

	private void onBtnPasswordSwitch(){
		if(m_blPasswordOpened){
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			m_strKey="";
			m_et_password.setText(m_strKey);
			m_ib_hide_password.setVisibility(View.GONE);
			m_ib_show_password.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.GONE);
			m_ll_encryption.setVisibility(View.GONE);
			m_tv_psd_type_title.setVisibility(View.GONE);
		}else {
			m_blPasswordOpened = true;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_strKey=m_strPreKey;
			m_et_password.setText(m_strKey);
			m_ib_hide_password.setVisibility(View.VISIBLE);
			m_ib_show_password.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.VISIBLE);
			m_ll_encryption.setVisibility(View.VISIBLE);
			m_tv_psd_type_title.setVisibility(View.VISIBLE);
		}
		//
		if(!m_ib_show_password.isShown()){
			m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else {
			m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
	}
	private void onBtnEdit(){
		m_tv_done.setVisibility(View.VISIBLE);
		m_tv_edit.setVisibility(View.GONE);
		m_et_ssid.setEnabled(true);
		m_et_password.setEnabled(true);
		m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
		m_et_ssid.setBackgroundResource(R.drawable.selector_edit_bg);
		m_et_password.setPadding(0, 0, 200, 0);
		if(m_rg_wifi_mode.isShown()){
			m_rb_2point4G_wifi.setEnabled(true);
			m_rb_5G_wifi.setEnabled(true);
		}
		//
		//hide text of 'no passwrod'
		m_tv_no_password.setVisibility(View.GONE);
		//show password switch
		m_btn_psd_switch.setVisibility(View.VISIBLE);
		SecurityMode securityMode = BusinessMannager.getInstance().getSecurityMode();
		if (SecurityMode.Disable == securityMode) {
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			m_et_password.setText("");
			m_ll_encryption.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.GONE);
			m_tv_psd_type_title.setVisibility(View.GONE);

		}else {
			m_blPasswordOpened = true;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_ll_encryption.setVisibility(View.VISIBLE);
			m_ll_security.setVisibility(View.VISIBLE);
			m_tv_psd_type_title.setVisibility(View.VISIBLE);
		}

	}

	private void onBtnDone(){
		//
		boolean blHasChange = isSettingsChanged();
		if (blHasChange) {
			boolean blCheckSsid = checkSsid();
			if (!blCheckSsid) {
				m_strErrorInfo = getString(R.string.setting_ssid_invalid);
				m_err_dialog.showDialog(
						getString(R.string.setting_wifi_error_title), m_strErrorInfo);
				return;
			}
			if (SecurityMode.Disable != SecurityMode.build(m_nSecurityMode)) {
				boolean blCheckPsd = checkPassword(m_strKey);
				if (!blCheckPsd) {
					m_err_dialog.showDialog(
							getString(R.string.setting_wifi_error_title), m_strErrorInfo);
					return;
				}
			}
			setWlanSettingItems();
		}

		setControlsDoneStatus();
		//
		synchValues();
	}

	@SuppressWarnings("deprecation")
	private void setControlsDoneStatus(){

		m_tv_edit.setVisibility(View.VISIBLE);
		m_tv_done.setVisibility(View.GONE);
		m_et_ssid.setEnabled(false);
		m_et_password.setEnabled(false);
		m_et_password.setBackgroundDrawable(null);
		m_et_ssid.setBackgroundDrawable(null);
		m_et_password.setPadding(0, 0, 200, 0);
		if(m_rg_wifi_mode.isShown()){
			m_rb_2point4G_wifi.setEnabled(false);
			m_rb_5G_wifi.setEnabled(false);
		}
		//show password switch
		m_btn_psd_switch.setVisibility(View.GONE);
		SecurityMode securityMode = BusinessMannager.getInstance().getSecurityMode();
		if (SecurityMode.Disable == securityMode) {
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			m_btn_psd_switch.setVisibility(View.GONE);
			m_tv_no_password.setVisibility(View.VISIBLE);
			m_et_password.setText("");
			m_ib_hide_password.setVisibility(View.GONE);
			m_ib_show_password.setVisibility(View.GONE);

		}else {
			m_blPasswordOpened = true;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_btn_psd_switch.setVisibility(View.GONE);
			m_tv_no_password.setVisibility(View.GONE);
			m_ib_hide_password.setVisibility(View.VISIBLE);
			m_ib_show_password.setVisibility(View.GONE);
		}
		m_ll_encryption.setVisibility(View.GONE);
		m_ll_security.setVisibility(View.GONE);
		m_tv_psd_type_title.setVisibility(View.GONE);
	}
	private boolean isSettingsChanged(){
		boolean blChanged = false;
		m_nWlanAPMode = 0;
		if (!m_rb_2point4G_wifi.isChecked()) {
			m_nWlanAPMode = 1;
		}

		m_strSsid = m_et_ssid.getText().toString();

		if (m_blPasswordOpened) {
			m_nSecurityMode = m_securitySpinner.getSelectedItemPosition()+1;
			m_nType = m_encryptionSpinner.getSelectedItemPosition();
		}else {
			m_nSecurityMode = SecurityMode.antiBuild(SecurityMode.Disable);
			m_nType = -1;
		}

		m_strKey = m_et_password.getText().toString();
		if (SecurityMode.Disable != SecurityMode.build(m_nSecurityMode)) {
			if (m_nPreWlanAPMode != m_nWlanAPMode
					|| 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid)
					|| m_nSecurityMode != m_nPreSecurityMode
					|| m_nType != m_nPreType
					|| 0 != m_strKey.compareToIgnoreCase(m_strPreKey)) {
				blChanged = true;
			}
		}else {
			if (m_nPreWlanAPMode != m_nWlanAPMode
					|| 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid)
					|| m_nSecurityMode != m_nPreSecurityMode) {
				blChanged = true;
			}
		}
		return blChanged;
	}
	private void setWlanSettingItems(){
		DataValue data = new DataValue();
		data.addParam("WlanAPMode", m_nWlanAPMode);
		data.addParam("Ssid", m_strSsid);
		data.addParam("Password", m_strKey);
		data.addParam("Security", m_nSecurityMode);
		data.addParam("Encryption", m_nType);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.WLAN_SET_WLAN_SETTING_REQUSET, data);
	}

	private void initSpinnerListString() {
		m_securityOptions = new ArrayList<String>();
		m_securityOptions.add(STRING_WEP);
		m_securityOptions.add(STRING_WPA);
		m_securityOptions.add(STRING_WPA2);
		m_securityOptions.add(STRING_WPA_WPA2);
		m_wpaTypeOptions = new ArrayList<String>();
		m_wpaTypeOptions.add(STRING_TKIP);
		m_wpaTypeOptions.add(STRING_AES);
		m_wpaTypeOptions.add(STRING_AUTO);
		m_wpaTypeOptions2 = new ArrayList<String>();
		m_wpaTypeOptions2.add(STRING_AES);
		m_wepTypeOptions = new ArrayList<String>();
		m_wepTypeOptions.add(STRING_OPEN);
		m_wepTypeOptions.add(STRING_SHARE);
	}

	private void initSpinersUI() {
		SecurityMode securityMode = BusinessMannager.getInstance().getSecurityMode();
		ArrayAdapter<String> securityAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_simple_item,R.id.spinner_sample_lable, m_securityOptions);
		m_securitySpinner.setAdapter(securityAdapter,m_securityOptions);
		m_et_ssid.setText(BusinessMannager.getInstance().getSsid());
		if(securityMode == SecurityMode.Disable) {
			m_securitySpinner.setSelection(0);
			m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
			m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
			m_encryptionSpinner.setAdapter(m_wepEncryptionadapter,m_wepTypeOptions);
			m_encryptionSpinner.setSelection(0);
			m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
			m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			m_et_password.setText(m_curWEPPassword);
			return;
		}

		if(securityMode == SecurityMode.WEP)
			m_securitySpinner.setSelection(0);
		else if(securityMode == SecurityMode.WPA)
			m_securitySpinner.setSelection(1);
		else if(securityMode == SecurityMode.WPA2)
			m_securitySpinner.setSelection(2);
		else if(securityMode == SecurityMode.WPA_WPA2)
			m_securitySpinner.setSelection(3);
		else
			m_securitySpinner.setSelection(0);

		//
		if(securityMode == SecurityMode.WEP) {
			m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
			m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
			m_encryptionSpinner.setAdapter(m_wepEncryptionadapter,m_wepTypeOptions);
			WEPEncryption wepType = BusinessMannager.getInstance().getWEPEncryption();
			if(wepType == WEPEncryption.Open)
				m_encryptionSpinner.setSelection(0);
			else 
				m_encryptionSpinner.setSelection(1);
			m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
			m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			m_et_password.setText(m_curWEPPassword);
		}else{
			m_encryptionPrompt.setText(R.string.setting_wifi_password_wpa_encryption_tip);
			m_passwordPrompt.setText(R.string.setting_wifi_password_wpa_psw_tip);
			//m_encryptionSpinner.setAdapter(m_wpaEncryptionadapter,m_wpaTypeOptions);
			WPAEncryption wpaType = BusinessMannager.getInstance().getWPAEncryption();
			WModeEnum wmode = BusinessMannager.getInstance().getWMode();
			if(wmode == WModeEnum.WMode_802_11a_n || wmode == WModeEnum.WMode_802_11g_n) {
				m_encryptionSpinner.setAdapter(m_wpaEncryptionadapter2,m_wpaTypeOptions2);
				m_encryptionSpinner.setSelection(0);
			}else{
				m_encryptionSpinner.setAdapter(m_wpaEncryptionadapter,m_wpaTypeOptions);
				if(wpaType == WPAEncryption.AUTO)
					m_encryptionSpinner.setSelection(2);
				else if(wpaType == WPAEncryption.AES)
					m_encryptionSpinner.setSelection(1);
				else
					m_encryptionSpinner.setSelection(0);
			}
			m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
			m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			m_et_password.setText(m_curWPAPassword);
		}
	}

	@Override
	public void onSpinnerItemSelected(CustomSpinner view, int position) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.security_spinner:
			if(m_securityOptions.get(position).equalsIgnoreCase(STRING_WEP)) {
				m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
				m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
				m_encryptionSpinner.setAdapter(m_wepEncryptionadapter,m_wepTypeOptions);
				m_encryptionSpinner.setSelection(0);//open
				m_curWPAPassword = m_et_password.getEditableText().toString();
				m_et_password.setText(m_curWEPPassword);
			}else{
				m_encryptionPrompt.setText(R.string.setting_wifi_password_wpa_encryption_tip);
				m_passwordPrompt.setText(R.string.setting_wifi_password_wpa_psw_tip);
				WModeEnum wmode = BusinessMannager.getInstance().getWMode();
				if(wmode == WModeEnum.WMode_802_11a_n || wmode == WModeEnum.WMode_802_11g_n) {
					m_encryptionSpinner.setAdapter(m_wpaEncryptionadapter2,m_wpaTypeOptions2);
					m_encryptionSpinner.setSelection(0);//AES
				}else{
					m_encryptionSpinner.setAdapter(m_wpaEncryptionadapter,m_wpaTypeOptions);
					m_encryptionSpinner.setSelection(2);//auto
				}
				m_curWEPPassword = m_et_password.getEditableText().toString();
				m_et_password.setText(m_curWPAPassword);
			}
			break;
		case R.id.encryption_spinner:
			break;
		}
	}

	private void synchValues(){
		m_nPreWlanAPMode = WlanFrequency.antiBuild(
				BusinessMannager.getInstance().getWlanFrequency());
		m_nWlanAPMode = m_nPreSecurityMode;

		m_strPreSsid = BusinessMannager.getInstance().getSsid();
		m_strSsid = m_strPreSsid;

		SecurityMode mode = BusinessMannager.getInstance().getSecurityMode();
		m_nPreSecurityMode = SecurityMode.antiBuild(mode);
		m_nSecurityMode = m_nPreSecurityMode;

		if (SecurityMode.Disable == mode) {
			m_nPreType = -1;
		}
		else if (SecurityMode.WEP == mode) {
			m_nPreType = WEPEncryption.antiBuild(
					BusinessMannager.getInstance().getWEPEncryption());
		}else {
			m_nPreType = WPAEncryption.antiBuild(
					BusinessMannager.getInstance().getWPAEncryption());
		}
		m_nType = m_nPreType;

		m_strPreKey = BusinessMannager.getInstance().getWifiPwd();
		if (SecurityMode.Disable == mode) {
			m_strPreKey = "";
		}
		m_strKey = m_strPreKey;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));

		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		m_err_dialog.closeDialog();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		if(intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
			}
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_success);
			}

			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Wep key 
		不能为空
		如果是key长度是5或是13，则需要满足以下条件：
		1.	ASCII码数字大于32并且小于127但不包含34、38、58、59、92对应的字符

		如果wep key长度为10或是26，key只能包含0到9和大小写26个英文字母

		WPA
		WPA2
		WPA/WPA2
		需要同时满足3个条件：
		1.	不能为空
		2.	Key.length>7 && Key.length<64
		3.	Key只包含ASCII码数字大于32并且小于127但不包含34、38、58、59、92对应的字符


	 */
	private boolean checkPassword(String strPsw) {
		int nLength = strPsw.length();
		boolean bCorrect = true;
		if(SecurityMode.build(m_nSecurityMode) == SecurityMode.WEP) {
			if(strPsw == null || !(nLength == 5 || nLength == 13 || nLength == 10 || nLength == 26)) {
				bCorrect = false;
			}else{
				if(nLength == 5 || nLength == 13) {
					for(int i = 0;i < nLength;i++) {
						char c = strPsw.charAt(i);
						if(!(c > 32 && c < 127 && c != 34 &&  c != 38 &&  c != 58 &&  c != 59 &&  c != 92)) {
							bCorrect = false;
							break;
						}
					}
				}

				if(nLength == 10 || nLength == 26) {
					for(int i = 0;i < nLength;i++) {
						char c = strPsw.charAt(i);
						if(!(c >= '0' && c <= '9' || c >= 'a' &&  c <= 'z' ||  c >= 'A' &&  c <= 'Z')) {
							bCorrect = false;
							break;
						}
					}
				}
			}
			if(bCorrect == true){
				return true;
			}else{
				m_strErrorInfo = getString(R.string.setting_wep_password_error_prompt);
				return false;
			}
		}else{
			if(strPsw == null || !(nLength > 7 && nLength < 64)) {
				bCorrect = false;
			}else{
				for(int i = 0;i < nLength;i++) {
					char c = strPsw.charAt(i);
					if(!(c > 32 && c < 127 && c != 34 &&  c != 38 &&  c != 58 &&  c != 59 &&  c != 92)) {
						bCorrect = false;
						break;
					}
				}
			}

			if(bCorrect == true){
				return true;
			}else{
				m_strErrorInfo = getString(R.string.setting_wpa_password_error_prompt);
				return false;
			}
		}
	}

	private boolean checkSsid(){
		boolean blRes = true;
		String strSsid = m_strSsid;
		for (int i = 0; i < strSsid.length(); i++) {
			char c = strSsid.charAt(i);
			if (47 == c || 27 == c || 61 == c || 34 == c || 92 == c || 38 == c) {
				blRes = false;
				break;
			}
		}
		return blRes;
	}
}
