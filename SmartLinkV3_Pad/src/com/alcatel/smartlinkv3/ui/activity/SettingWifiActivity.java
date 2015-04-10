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
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.view.CustomSpinner;
import com.alcatel.smartlinkv3.ui.view.CustomSpinner.OnSpinnerItemSelectedListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
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
import android.widget.ProgressBar;
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
	private RadioGroup m_rg_wifi_mode;
	private RadioButton m_rb_2point4G_wifi;
	private RadioButton m_rb_5G_wifi;
	private TextView m_tv_no_password;
	private Button m_btn_psd_switch;
	private ProgressBar m_pb_waiting=null;
	private TextView m_tv_ssid;
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

		//control title bar
		controlTitlebar();
		//create controls
		createControls();
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
		m_rb_2point4G_wifi.setOnClickListener(this);
		m_rb_5G_wifi.setOnClickListener(this);
		//
		if(!m_ib_show_password.isShown()){
			m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else {
			m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}

		m_ib_hide_password.setOnClickListener(this);
		m_ib_show_password.setOnClickListener(this);
		//
		m_tv_no_password = (TextView)findViewById(R.id.tv_no_psd);
		m_btn_psd_switch = (Button)findViewById(R.id.btn_psd_switch);
		m_btn_psd_switch.setOnClickListener(this);

		m_err_dialog = CommonErrorInfoDialog.getInstance(this);//
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_wifi_waiting_progress);
		m_tv_ssid = (TextView)findViewById(R.id.tv_ssid);
		//init spiner
		initSpiners();
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pb_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_waiting.setVisibility(View.GONE);
		}
		m_tv_edit.setEnabled(!blShow);
		m_tv_done.setEnabled(!blShow);
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
			
		case R.id.rb_2point4G_wifi:
		case R.id.rb_5G_wifi:
			onWifModeChanged();
			break;
		default:
			break;

		}
	}

	private void onWifModeChanged(){
		m_nPreWlanAPMode = WlanFrequency.antiBuild(
				BusinessMannager.getInstance().getWlanFrequency());
		m_nWlanAPMode = WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ);
		if (m_rb_5G_wifi.isChecked()) {
			m_nWlanAPMode = WlanFrequency.antiBuild(WlanFrequency.Frequency_5GHZ);
		}

		if (WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ) == m_nWlanAPMode) {
			m_strPreSsid = BusinessMannager.getInstance().getSsid();
			SecurityMode mode = BusinessMannager.getInstance().getSecurityMode();
			m_nPreSecurityMode = SecurityMode.antiBuild(mode);	
			if (SecurityMode.Disable == mode) {
				m_nPreType = -1;
			}
			else if (SecurityMode.WEP == mode) {
				m_nPreType = WEPEncryption.antiBuild(
						BusinessMannager.getInstance().getWEPEncryption());
			}else {
				m_nPreType = WPAEncryption.antiBuild(
						BusinessMannager.getInstance().getWPAEncryption());
				if (SecurityMode.Disable == mode) {
					m_strPreKey = "";
				}else {
					m_strPreKey = BusinessMannager.getInstance().getWifiPwd();
				}
			}		
		}else {
			m_strPreSsid = BusinessMannager.getInstance().getSsid_5G();
			SecurityMode mode = BusinessMannager.getInstance().getSecurityMode_5G();
			m_nPreSecurityMode = SecurityMode.antiBuild(mode);	
			if (SecurityMode.Disable == mode) {
				m_nPreType = -1;
			}
			else if (SecurityMode.WEP == mode) {
				m_nPreType = WEPEncryption.antiBuild(
						BusinessMannager.getInstance().getWEPEncryption_5G());
			}else {
				m_nPreType = WPAEncryption.antiBuild(
						BusinessMannager.getInstance().getWPAEncryption_5G());
				if (SecurityMode.Disable == mode) {
					m_strPreKey = "";
				}else {
					m_strPreKey = BusinessMannager.getInstance().getWifiPwd_5G();
				}
			}
		}

		m_strSsid = m_strPreSsid;

		m_nSecurityMode = m_nPreSecurityMode;

		m_nType = m_nPreType;

		m_strKey = m_strPreKey;
		initSpinersUI();
	}
	
	private void onBtnPasswordSwitch(){
		if(m_blPasswordOpened){
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			m_et_password.setText("");
			m_et_password.setEnabled(false);
			m_et_password.setVisibility(View.GONE);
			m_ib_hide_password.setVisibility(View.GONE);
			m_ib_show_password.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.GONE);
			m_ll_encryption.setVisibility(View.GONE);
			m_tv_psd_type_title.setVisibility(View.GONE);
		}else {
			m_blPasswordOpened = true;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_et_password.setText(m_strKey);
			m_et_password.setEnabled(true);
			m_et_password.setVisibility(View.VISIBLE);
			//m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
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

		m_tv_edit.setFocusable(false);
		m_tv_edit.setFocusableInTouchMode(false);
		m_tv_edit.setVisibility(View.GONE);
		m_et_ssid.setEnabled(true);
		m_et_ssid.setVisibility(View.VISIBLE);
		m_tv_ssid.setVisibility(View.GONE);
		m_et_password.setEnabled(true);
		m_et_password.setVisibility(View.VISIBLE);
		//m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
//		m_et_ssid.setBackgroundResource(R.drawable.selector_edit_bg);
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
		//SecurityMode securityMode = BusinessMannager.getInstance().getSecurityMode();
		if (SecurityMode.Disable == SecurityMode.build(m_nSecurityMode)) {
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			m_et_password.setText("");
			m_et_password.setEnabled(false);
			m_et_password.setVisibility(View.GONE);
			//m_et_password.setBackgroundDrawable(null);
			m_ll_encryption.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.GONE);
			m_tv_psd_type_title.setVisibility(View.GONE);

		}else {
			m_blPasswordOpened = true;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_et_password.setText(m_strKey);
			m_ll_encryption.setVisibility(View.VISIBLE);
			m_ll_security.setVisibility(View.VISIBLE);
			m_tv_psd_type_title.setVisibility(View.VISIBLE);
		}
		m_et_ssid.setFocusable(true);
		m_et_ssid.setFocusableInTouchMode(true);
		m_et_ssid.requestFocus();
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

		//
		synchValues();
		setControlsDoneStatus();
	}

	@SuppressWarnings("deprecation")
	private void setControlsDoneStatus(){
		if (WlanSupportMode.Mode2Point4GAnd5G != BusinessMannager.getInstance().getWlanSupportMode()) {
			m_rg_wifi_mode.setVisibility(View.GONE);
		}else {
			m_rg_wifi_mode.setVisibility(View.VISIBLE);
		}
		
		if (WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ) == m_nWlanAPMode) {
			m_rb_2point4G_wifi.setChecked(true);
		}else{
			m_rb_5G_wifi.setChecked(true);
		}

		m_tv_edit.setVisibility(View.VISIBLE);
		m_tv_done.setVisibility(View.GONE);
		m_et_ssid.setEnabled(false);
		m_tv_ssid.setText(m_strPreSsid);
		m_tv_ssid.setVisibility(View.VISIBLE);
		m_et_ssid.setVisibility(View.GONE);
		m_et_password.setEnabled(false);
		//m_et_password.setVisibility(View.GONE);
		//m_et_password.setBackgroundDrawable(null);
//		m_et_ssid.setBackgroundDrawable(null);
		m_et_password.setPadding(0, 0, 200, 0);
		if(m_rg_wifi_mode.isShown()){
			m_rb_2point4G_wifi.setEnabled(false);
			m_rb_5G_wifi.setEnabled(false);
		}
		//show password switch
		m_btn_psd_switch.setVisibility(View.GONE);
		SecurityMode securityMode = SecurityMode.build(m_nSecurityMode);
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
			if(!m_ib_show_password.isShown()){
				m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}else {
				m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}
		}
		m_ll_encryption.setVisibility(View.GONE);
		m_ll_security.setVisibility(View.GONE);
		m_tv_psd_type_title.setVisibility(View.GONE);
		m_tv_edit.setFocusable(true);
		m_tv_edit.setFocusableInTouchMode(true);
		m_tv_edit.requestFocus();
	}
	private boolean isSettingsChanged(){
		boolean blChanged = false;
		m_nWlanAPMode = WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ);
		if (!m_rb_2point4G_wifi.isChecked()) {
			m_nWlanAPMode = WlanFrequency.antiBuild(WlanFrequency.Frequency_5GHZ);
		}

		m_strSsid = m_et_ssid.getText().toString();

		if (m_blPasswordOpened) {
			m_nSecurityMode = m_securitySpinner.getSelectedItemPosition()+1;
			m_nType = m_encryptionSpinner.getSelectedItemPosition();
			m_strKey = m_et_password.getText().toString();
		}else {
			m_nSecurityMode = SecurityMode.antiBuild(SecurityMode.Disable);
			m_nType = -1;
		}

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
		ShowWaiting(true);
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
		Resources res = getResources();
		String[] wepMode = res.getStringArray(R.array.setting_wep_mode_array);
		for (int i = 0; i < wepMode.length; i++) {
			m_wepTypeOptions.add(wepMode[i]);
		}
	}

	private void initSpinersUI() {
		SecurityMode securityMode = SecurityMode.build(m_nPreSecurityMode);
		ArrayAdapter<String> securityAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_simple_item,R.id.spinner_sample_lable, m_securityOptions);
		m_securitySpinner.setAdapter(securityAdapter,m_securityOptions);
		m_et_ssid.setText(m_strPreSsid);
		if(securityMode == SecurityMode.Disable) {
			m_securitySpinner.setSelection(0);
			m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
			m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
			m_encryptionSpinner.setAdapter(m_wepEncryptionadapter,m_wepTypeOptions);
			m_encryptionSpinner.setSelection(0);
			if (WlanFrequency.Frequency_24GHZ == WlanFrequency.build(m_nPreWlanAPMode)) {
				m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
				m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			}else {
				m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd_5G();
				m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd_5G();
			}
			m_et_password.setVisibility(View.GONE);
			m_ib_hide_password.setVisibility(View.GONE);
			m_ib_show_password.setVisibility(View.GONE);
			m_ll_encryption.setVisibility(View.GONE);
			m_ll_security.setVisibility(View.GONE);
			m_tv_psd_type_title.setVisibility(View.GONE);
			m_blPasswordOpened = false;
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
			return;
		}else{
			m_blPasswordOpened = true;
			m_et_password.setVisibility(View.VISIBLE);
			m_et_password.setEnabled(true);
			m_ib_hide_password.setVisibility(View.VISIBLE);
			m_ib_show_password.setVisibility(View.GONE);
			m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
			m_ll_encryption.setVisibility(View.VISIBLE);
			m_ll_security.setVisibility(View.VISIBLE);
			m_tv_psd_type_title.setVisibility(View.VISIBLE);
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
			m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
			m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			if (WlanFrequency.Frequency_5GHZ == WlanFrequency.build(m_nPreWlanAPMode)) {
				wepType = BusinessMannager.getInstance().getWEPEncryption_5G();
				m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd_5G();
				m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd_5G();
			}
			if(wepType == WEPEncryption.Open)
				m_encryptionSpinner.setSelection(0);
			else 
				m_encryptionSpinner.setSelection(1);
			m_et_password.setText(m_curWEPPassword);
		}else{
			m_encryptionPrompt.setText(R.string.setting_wifi_password_wpa_encryption_tip);
			m_passwordPrompt.setText(R.string.setting_wifi_password_wpa_psw_tip);
			WPAEncryption wpaType = BusinessMannager.getInstance().getWPAEncryption();
			WModeEnum wmode = BusinessMannager.getInstance().getWMode();
			m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd();
			m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd();
			if (WlanFrequency.Frequency_5GHZ == WlanFrequency.build(m_nPreWlanAPMode)) {
				wpaType = BusinessMannager.getInstance().getWPAEncryption_5G();
				wmode = BusinessMannager.getInstance().getWMode_5G();
				m_curWPAPassword = BusinessMannager.getInstance().getWifiPwd_5G();
				m_curWEPPassword = BusinessMannager.getInstance().getWifiPwd_5G();
			}
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

	private void initValues(){
		m_nPreWlanAPMode = WlanFrequency.antiBuild(
				BusinessMannager.getInstance().getWlanFrequency());
		m_nWlanAPMode = m_nPreWlanAPMode;

		if (WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ) == m_nWlanAPMode) {
			m_strPreSsid = BusinessMannager.getInstance().getSsid();
			SecurityMode mode = BusinessMannager.getInstance().getSecurityMode();
			m_nPreSecurityMode = SecurityMode.antiBuild(mode);	
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
			m_strPreKey = BusinessMannager.getInstance().getWifiPwd();
//			if (SecurityMode.Disable == mode) {
//				m_strPreKey = "";
//			}else {
//				m_strPreKey = BusinessMannager.getInstance().getWifiPwd();
//			}		
		}else {
			m_strPreSsid = BusinessMannager.getInstance().getSsid_5G();
			SecurityMode mode = BusinessMannager.getInstance().getSecurityMode_5G();
			m_nPreSecurityMode = SecurityMode.antiBuild(mode);	
			if (SecurityMode.Disable == mode) {
				m_nPreType = -1;
			}
			else if (SecurityMode.WEP == mode) {
				m_nPreType = WEPEncryption.antiBuild(
						BusinessMannager.getInstance().getWEPEncryption_5G());
			}else {
				m_nPreType = WPAEncryption.antiBuild(
						BusinessMannager.getInstance().getWPAEncryption_5G());
			}
			m_strPreKey = BusinessMannager.getInstance().getWifiPwd_5G();
//			if (SecurityMode.Disable == mode) {
//				m_strPreKey = "";
//			}else {
//				m_strPreKey = BusinessMannager.getInstance().getWifiPwd_5G();
//			}
		}

		m_strSsid = m_strPreSsid;

		m_nSecurityMode = m_nPreSecurityMode;

		m_nType = m_nPreType;

		m_strKey = m_strPreKey;
	}
	private void synchValues(){
		m_nPreSecurityMode = m_nWlanAPMode;

		m_strPreSsid = m_strSsid;

		m_nPreSecurityMode = m_nSecurityMode;

		m_nPreType = m_nType;

		m_strPreKey = m_strKey;
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
		registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));
		


		initValues();
		//init controls state
		initSpinersUI();
		setControlsDoneStatus();
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET, null);
		ShowWaiting(true);
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
				initValues();
				//init controls state
				initSpinersUI();
				setControlsDoneStatus();
				ShowWaiting(false);
			}
		}
		
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				//init controls state
				initSpinersUI();
				setControlsDoneStatus();
			}
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_wifi_set_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_wifi_set_success);
			}else {
				initValues();
				initSpinersUI();
				setControlsDoneStatus();
			}

			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			ShowWaiting(false);
		}
	}

	/*
	 * Wep key 
		涓嶈兘涓虹┖
		濡傛灉鏄痥ey闀垮害鏄�鎴栨槸13锛屽垯闇�婊¤冻浠ヤ笅鏉′欢锛�
		1.	ASCII鐮佹暟瀛楀ぇ浜�2骞朵笖灏忎簬127浣嗕笉鍖呭惈34銆�8銆�8銆�9銆�2瀵瑰簲鐨勫瓧绗�

		濡傛灉wep key闀垮害涓�0鎴栨槸26锛宬ey鍙兘鍖呭惈0鍒�鍜屽ぇ灏忓啓26涓嫳鏂囧瓧姣�

		WPA
		WPA2
		WPA/WPA2
		闇�鍚屾椂婊¤冻3涓潯浠讹細
		1.	涓嶈兘涓虹┖
		2.	Key.length>7 && Key.length<64
		3.	Key鍙寘鍚獳SCII鐮佹暟瀛楀ぇ浜�2骞朵笖灏忎簬127浣嗕笉鍖呭惈34銆�8銆�8銆�9銆�2瀵瑰簲鐨勫瓧绗�


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
