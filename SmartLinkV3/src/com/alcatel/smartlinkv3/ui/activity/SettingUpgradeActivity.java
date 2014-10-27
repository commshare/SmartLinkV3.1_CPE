package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingUpgradeActivity extends Activity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_check_firmware=null;
	private Button m_btn_upgrade_app=null;
	private TextView m_tv_cur_firmware_version =null;
	private TextView m_tv_cur_app_version=null;
	private TextView m_tv_new_app_version=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//set titlebar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_upgrade);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title
		controlTitle();
		//create controls
		createControls();
	}

	private void controlTitle(){
		m_tv_titleTextView = (TextView)findViewById(R.id.tv_title_title);
		m_tv_titleTextView.setText(R.string.setting_upgrade);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}

	private void createControls(){

		m_tv_cur_firmware_version=(TextView)findViewById(R.id.tv_check_firmware);
		m_tv_cur_app_version = (TextView)findViewById(R.id.tv_current_app_version);
		m_tv_new_app_version = (TextView)findViewById(R.id.tv_new_app_version);
		m_btn_check_firmware = (Button)findViewById(R.id.btn_check_firmware);
		m_btn_upgrade_app = (Button)findViewById(R.id.btn_app_upgrade);
		m_btn_check_firmware.setOnClickListener(this);
		m_btn_upgrade_app.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			SettingUpgradeActivity.this.finish();
			break;

		case R.id.btn_app_upgrade:
			onBtnAppCheck();
			break;
			
		case R.id.btn_check_firmware:
			onBtnFirmwareCheck();
			break;
			
		default:
			break;
		}
	}

	private void onBtnFirmwareCheck(){
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.UPDATE_GET_DEVICE_NEW_VERSION, null);
	}
	
	private void onBtnAppCheck(){
		
	}
}
