package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog.OnInquireApply;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SettingUpgradeActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_check_firmware=null;
	private Button m_btn_upgrade_app=null;
	private TextView m_tv_cur_firmware_version =null;
	private TextView m_tv_cur_app_version=null;
	private TextView m_tv_new_app_version=null;
	private TextView m_tv_new_firmware_version = null;
	private ProgressBar m_pb_waiting=null;
	private boolean m_blHasNewFirmware = false;
	private boolean m_blHasNewApp = false;
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
		m_tv_new_firmware_version = (TextView)findViewById(R.id.tv_new_firmware_version);
		m_btn_check_firmware = (Button)findViewById(R.id.btn_check_firmware);
		m_btn_upgrade_app = (Button)findViewById(R.id.btn_app_upgrade);
		m_btn_check_firmware.setOnClickListener(this);
		m_btn_upgrade_app.setOnClickListener(this);
		//
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_upgrade_waiting_progress);
		
		updateNewDeviceInfo(); 
		String strCurAppVersion = getString(R.string.setting_upgrade_current_app_version);
		strCurAppVersion += BusinessMannager.getInstance().getAppVersion();
		m_tv_cur_app_version.setText(strCurAppVersion);
		m_tv_new_app_version.setText("");
		String strCurFWVersion = getString(R.string.setting_upgrade_device_version);
		strCurFWVersion += BusinessMannager.getInstance().getSystemInfo().getSwVersion();
		m_tv_cur_firmware_version.setText(strCurFWVersion);
		if (m_blHasNewApp) {
			m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_upgrade);
		}else {
			m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_check);
		}
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pb_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_waiting.setVisibility(View.GONE);
		}
		m_btn_check_firmware.setEnabled(!blShow);
		m_btn_upgrade_app.setEnabled(!blShow);
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
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
			ShowWaiting(true);
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
		if (m_blHasNewFirmware) {
			final InquireDialog inquireDlg = new InquireDialog(this);
			inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
			inquireDlg.m_contentTextView
					.setText(R.string.setting_upgrade_firmware_warning);
			inquireDlg.m_contentDescriptionTextView.setText("");
			inquireDlg.m_confirmBtn
					.setBackgroundResource(R.drawable.selector_common_button);
			inquireDlg.m_confirmBtn.setText(R.string.ok);
			inquireDlg.showDialog(new OnInquireApply() {
				
				@Override
				public void onInquireApply() {
					// TODO Auto-generated method stub
					//upgrade firmware
					ShowWaiting(true);
				}
			});
		}else {
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION, null);
			ShowWaiting(true);
			
		}
	}
	
	private void onBtnAppCheck(){
		m_tv_new_app_version.setText(R.string.setting_upgrade_no_new_version);
		ShowWaiting(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		if(intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				updateNewDeviceInfo();
			}else {
				ShowWaiting(false);
				String strNew = getString(R.string.setting_upgrade_check_failed);
				setNewDeviceVersion(strNew);
				
			}
		}
	}
	
	private void setNewDeviceVersion(String strNewVesion){
		m_tv_new_firmware_version.setText(strNewVesion);
	}
	
	private void updateNewDeviceInfo(){
		DeviceNewVersionInfo info = BusinessMannager.getInstance().getNewFirmwareInfo();
		int nState = info.getState();
		EnumDeviceCheckingStatus eStatus = EnumDeviceCheckingStatus.build(nState);
		if (EnumDeviceCheckingStatus.DEVICE_CHECKING == eStatus) {
			//waiting
			ShowWaiting(true);
		}else if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == eStatus) {
			m_blHasNewFirmware = true;
			String strNew = getString(R.string.setting_upgrade_device_version);
			strNew += info.getVersion();
			setNewDeviceVersion(strNew);
			m_btn_check_firmware.setText(R.string.setting_upgrade_btn_upgrade);
			ShowWaiting(false);
		}else if (EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION == eStatus) {
			ShowWaiting(false);
			String strNew = getString(R.string.setting_upgrade_no_new_version);
			setNewDeviceVersion(strNew);
		}else if (EnumDeviceCheckingStatus.DEVICE_NO_CONNECT == eStatus) {
			ShowWaiting(false);
			String strNew = getString(R.string.setting_upgrade_no_connection);
			setNewDeviceVersion(strNew);
		}else if (EnumDeviceCheckingStatus.DEVICE_NOT_AVAILABLE == eStatus) {
			ShowWaiting(false);
			String strNew = getString(R.string.setting_upgrade_not_available);
			setNewDeviceVersion(strNew);
		}else if (EnumDeviceCheckingStatus.DEVICE_CHECK_ERROR == eStatus) {
			ShowWaiting(false);
			String strNew = getString(R.string.setting_upgrade_check_error);
			setNewDeviceVersion(strNew);
		}else {
			ShowWaiting(false);
		}
	}
	
}
