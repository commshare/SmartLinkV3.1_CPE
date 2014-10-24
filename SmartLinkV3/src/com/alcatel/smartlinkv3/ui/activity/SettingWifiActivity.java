package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingWifiActivity extends Activity implements OnClickListener{

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_wifi);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

		m_blSupportMultiWifiMode= true;
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		//init controls state
		onBtnDone();
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
			SettingWifiActivity.this.finish();
			break;
		case R.id.tv_titlebar_edit:
			onBtnEdit();
			break;
		case R.id.tv_titlebar_done:
			onBtnDone();
			break;
		default:
			break;

		}
	}

	private void onBtnEdit(){
		m_tv_done.setVisibility(View.VISIBLE);
		m_tv_edit.setVisibility(View.GONE);
		m_et_ssid.setEnabled(true);
		m_et_password.setEnabled(true);
		m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
		m_et_ssid.setBackgroundResource(R.drawable.selector_edit_bg);
		if(m_rg_wifi_mode.isShown()){
			m_rb_2point4G_wifi.setEnabled(true);
			m_rb_5G_wifi.setEnabled(true);
		}

	}

	@SuppressWarnings("deprecation")
	private void onBtnDone(){
		m_tv_edit.setVisibility(View.VISIBLE);
		m_tv_done.setVisibility(View.GONE);
		m_et_ssid.setEnabled(false);
		m_et_password.setEnabled(false);
		m_et_password.setBackgroundDrawable(null);
		m_et_ssid.setBackgroundDrawable(null);
		if(m_rg_wifi_mode.isShown()){
			m_rb_2point4G_wifi.setEnabled(false);
			m_rb_5G_wifi.setEnabled(false);
		}
	}
}
