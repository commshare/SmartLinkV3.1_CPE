package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

public class SettingWifiActivity extends Activity implements OnClickListener{

	private EditText m_et_password;
	private ImageButton m_ib_show_password;
	private ImageButton m_ib_hide_password;
	private ImageButton m_ib_back;
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
		m_ib_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingWifiActivity.this.finish();
			}
		});
	}
	
	private void createControls(){
		m_et_password = (EditText)findViewById(R.id.edit_password);
		m_ib_show_password = (ImageButton)findViewById(R.id.ib_show_password);
		m_ib_hide_password = (ImageButton)findViewById(R.id.ib_hide_password);
		if(!m_ib_show_password.isShown()){
			m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else {
			m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
		
		m_ib_hide_password.setOnClickListener(this);
		m_ib_show_password.setOnClickListener(this);
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
		
		}
	}
}
