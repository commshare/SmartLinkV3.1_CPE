package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SystemInfoModel;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

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
import android.widget.Toast;

public class SystemInfoActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private TextView m_tv_swVersion_value=null;
	private TextView m_tv_hwVersion_value=null;
	private TextView m_tv_device_name_value=null;
	private TextView m_tv_imei_value=null;
	private TextView m_tv_mac_value=null;
	private TextView m_tv_ip_value=null;
	private TextView m_tv_subnet_value=null;
//	private Button m_btn_PowerOff=null;
//	private Button m_btn_reboot=null;
//	private Button m_btn_reset=null;
	private ProgressBar m_pb_waiting=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_system_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
//		ShowWaiting(false);
		//set system information
		setSystemInfo();
	}

	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_device_title);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}

	private void createControls(){
		//system information values
		m_tv_swVersion_value = (TextView)findViewById(R.id.tv_sw_version_info);
		m_tv_hwVersion_value = (TextView)findViewById(R.id.tv_hw_version_info);
		m_tv_device_name_value = (TextView)findViewById(R.id.tv_device_name_info);
		m_tv_imei_value = (TextView)findViewById(R.id.tv_imei_info);
		m_tv_mac_value = (TextView)findViewById(R.id.tv_mac_address_info);
		m_tv_ip_value = (TextView)findViewById(R.id.tv_ip_address_info);
		m_tv_subnet_value = (TextView)findViewById(R.id.tv_subnet_mask_info);
		//buttons
//		m_btn_PowerOff = (Button)findViewById(R.id.btn_power_off);
//		m_btn_reboot = (Button)findViewById(R.id.btn_reboot);
//		m_btn_reset = (Button)findViewById(R.id.btn_reset);
//		m_btn_PowerOff.setOnClickListener(this);
//		m_btn_reboot.setOnClickListener(this);
//		m_btn_reset.setOnClickListener(this);
		//
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_device_waiting_progress);
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pb_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_waiting.setVisibility(View.GONE);
		}
//		m_btn_PowerOff.setEnabled(!blShow);
//		m_btn_reboot.setEnabled(!blShow);
//		m_btn_reset.setEnabled(!blShow);
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			SystemInfoActivity.this.finish();
			break;

//		case R.id.btn_power_off:
//			onBtnPowerOff();
//			ShowWaiting(true);
//			break;
//		case R.id.btn_reboot:
//			onBtnReboot();
//			ShowWaiting(true);
//			break;
//		case R.id.btn_reset:
//			onBtnReset();
//			ShowWaiting(true);
//			break;

		default:
			break;
		}
	}

//	private void onBtnPowerOff(){
//		BusinessMannager.getInstance().
//		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF, null);
//	}
//	
//	private void onBtnReboot(){
//		BusinessMannager.getInstance().
//		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_REBOOT, null);
//	}
//	
//	private void onBtnReset(){
//		BusinessMannager.getInstance().
//		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_RESET, null);
//	}
	
	private void setSystemInfo(){
		SystemInfoModel systemInfo = BusinessMannager.getInstance().getSystemInfoModel();
		m_tv_swVersion_value.setText(systemInfo.getSwVersion());
		m_tv_hwVersion_value.setText(systemInfo.getHwVersion());
		m_tv_device_name_value.setText(systemInfo.getDeviceName());
//		m_tv_imei_value.setText(systemInfo.getIMEI());
//		m_tv_mac_value.setText(systemInfo.getMacAddress());
//		m_tv_ip_value.setText(systemInfo.getIP());
//		m_tv_subnet_value.setText(systemInfo.getSubnet());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_REBOOT));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_RESET));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.LAN_GET_LAN_SETTINGS));
		
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET, null);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.LAN_GET_LAN_SETTINGS, null);
		ShowWaiting(true);
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
		if(intent.getAction().equalsIgnoreCase(MessageUti.LAN_GET_LAN_SETTINGS)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.unknown_error);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				m_tv_ip_value.setText(BusinessMannager.getInstance().getSystemInfoModel().getIP());
				m_tv_subnet_value.setText(BusinessMannager.getInstance().getSystemInfoModel().getSubnet());
				m_tv_imei_value.setText(BusinessMannager.getInstance().getSystemInfoModel().getIMEI());
				m_tv_mac_value.setText(BusinessMannager.getInstance().getSystemInfoModel().getMacAddress());
			}
			else{
				Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			}
			ShowWaiting(false);
		}
		
	}
//		
//		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET)){
//			ShowWaiting(false);
//			setSystemInfo();
//		}
//		
//		
//		
//		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_RESET)){
//			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
//			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
//			String strTost = getString(R.string.setting_reset_failed);
//			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
//				strTost = getString(R.string.setting_reset_success);
//			}
//			ShowWaiting(false);
//			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
//		}
//		
//		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF)){
//			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
//			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
//			String strTost = getString(R.string.setting_power_off_failed);
//			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
//				strTost = getString(R.string.setting_power_off_success);
//			}
//			ShowWaiting(false);
//			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
//		}
//	}
}
