package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingDeviceActivity extends BaseActivity implements OnClickListener{
	
	private static TextView m_tv_title = null;
	private static ImageButton m_ib_back=null;
	private static TextView m_tv_back=null;
	
	private static FrameLayout m_system_info = null;
	private static FrameLayout m_upgrade_system = null;
	private static FrameLayout m_backup_and_reset = null;
	private static FrameLayout m_power_saving = null;
	private static FrameLayout m_pin_code = null;
	private static FrameLayout m_web_version = null;
	private static FrameLayout m_restart = null;
	private static FrameLayout m_power_off = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_device);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		
		controlTitlebar();
		initUi();

	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_device);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}
	
	private void initUi(){
		m_system_info = (FrameLayout) findViewById(R.id.device_system_info);
		m_system_info.setOnClickListener(this);
		m_upgrade_system = (FrameLayout) findViewById(R.id.device_upgrade_system);
		m_upgrade_system.setOnClickListener(this);
		m_backup_and_reset = (FrameLayout) findViewById(R.id.device_backup_and_reset);
		m_backup_and_reset.setOnClickListener(this);
		m_power_saving = (FrameLayout) findViewById(R.id.device_power_saving);
		m_power_saving.setOnClickListener(this);
		m_pin_code = (FrameLayout) findViewById(R.id.device_pin_code);
		m_pin_code.setOnClickListener(this);
		m_web_version = (FrameLayout) findViewById(R.id.device_web_version);
		m_web_version.setOnClickListener(this);
		m_restart = (FrameLayout) findViewById(R.id.device_restart);
		m_restart.setOnClickListener(this);
		m_power_off = (FrameLayout) findViewById(R.id.device_power_off);
		m_power_off.setOnClickListener(this);
	}
	
	private void goToSystemInfoPage(){
		Intent intent = new Intent(this, SystemInfoActivity.class);
		startActivity(intent);
	}
	
	private void goToBackupSettingPage(){
		Intent intent = new Intent(this, SettingBackupRestoreActivity.class);
		startActivity(intent);
	}
	
	private void goToPowerSettingPage(){
		Intent intent = new Intent(this, SettingPowerSavingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			this.onBackPressed();
			break;
		case R.id.device_system_info:
			goToSystemInfoPage();
			break;
		case R.id.device_upgrade_system:
			break;
		case R.id.device_backup_and_reset:
			goToBackupSettingPage();
			break;
		case R.id.device_power_saving:
			goToPowerSettingPage();
			break;
		case R.id.device_pin_code:
			break;
		case R.id.device_web_version:
			break;
		case R.id.device_restart:
			break;
		case R.id.device_power_off:
			break;
		default:
			break;
		}
	}

}
