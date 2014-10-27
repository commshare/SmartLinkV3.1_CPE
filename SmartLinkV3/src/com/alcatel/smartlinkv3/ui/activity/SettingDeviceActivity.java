package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SystemInfoModel;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingDeviceActivity extends Activity implements OnClickListener{

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
	private Button m_btn_PowerOff=null;
	private Button m_btn_reboot=null;
	private Button m_btn_reset=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_device);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		//set system information
		setSystemInfo();
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
		m_btn_PowerOff = (Button)findViewById(R.id.btn_power_off);
		m_btn_reboot = (Button)findViewById(R.id.btn_reboot);
		m_btn_reset = (Button)findViewById(R.id.btn_reset);
		m_btn_PowerOff.setOnClickListener(this);
		m_btn_reboot.setOnClickListener(this);
		m_btn_reset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			SettingDeviceActivity.this.finish();
			break;

		case R.id.btn_power_off:
			onBtnPowerOff();
			break;
		case R.id.btn_reboot:
			onBtnReboot();
			break;
		case R.id.btn_reset:
			onBtnReset();
			break;

		default:
			break;
		}
	}

	private void onBtnPowerOff(){
		
	}
	
	private void onBtnReboot(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_REBOOT, null);
	}
	
	private void onBtnReset(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_RESET, null);
	}
	
	private void setSystemInfo(){
		SystemInfoModel systemInfo = BusinessMannager.getInstance().getSystemInfoModel();
		m_tv_swVersion_value.setText(systemInfo.getSwVersion());
		m_tv_hwVersion_value.setText(systemInfo.getHwVersion());
		m_tv_device_name_value.setText(systemInfo.getDeviceName());
		m_tv_imei_value.setText(systemInfo.getIMEI());
		m_tv_mac_value.setText(systemInfo.getMacAddress());
		m_tv_ip_value.setText(systemInfo.getIP());
		m_tv_subnet_value.setText(systemInfo.getSubnet());
	}
}
