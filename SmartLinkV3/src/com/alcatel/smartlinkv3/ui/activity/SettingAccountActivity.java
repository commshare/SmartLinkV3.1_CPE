package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingAccountActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private TextView m_tv_done;
	private LinearLayout m_logout_and_changepwd;
	private LinearLayout m_inputpwd;
	private FrameLayout m_fl_titlebar;
	private TextView m_change_password;
	private TextView m_logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_account);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		controlTitlebar();
		initUi();
	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_account);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		
		m_fl_titlebar = (FrameLayout)findViewById(R.id.fl_edit_or_done);
		m_fl_titlebar.setVisibility(View.VISIBLE);
		
		m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
		m_tv_done.setVisibility(View.GONE);
		
		findViewById(R.id.tv_titlebar_edit).setVisibility(View.GONE);;
		
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
		m_tv_done.setOnClickListener(this);
	}
	
	private void initUi(){
		m_logout_and_changepwd = (LinearLayout)findViewById(R.id.logout_and_change_password);
		m_logout_and_changepwd.setVisibility(View.VISIBLE);
		
		m_inputpwd = (LinearLayout)findViewById(R.id.input_password);
		m_inputpwd.setVisibility(View.GONE);
		
		m_change_password = (TextView) findViewById(R.id.setting_change_password);
		m_logout = (TextView) findViewById(R.id.setting_logout);
		
		m_change_password.setOnClickListener(this);
		m_logout.setOnClickListener(this);
	}
	
	private void changePwdClick(){
		m_logout_and_changepwd.setVisibility(View.GONE);
		m_inputpwd.setVisibility(View.VISIBLE);
		m_tv_done.setVisibility(View.VISIBLE);
	}
	
	private void doneChangePassword(){
		m_tv_done.setVisibility(View.GONE);
		m_logout_and_changepwd.setVisibility(View.VISIBLE);
		m_inputpwd.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			SettingAccountActivity.this.finish();
			break;
		case R.id.setting_change_password:
			changePwdClick();
			break;
		case R.id.tv_titlebar_done:
			doneChangePassword();
			break;
		default:
			break;
		}
	}
}
