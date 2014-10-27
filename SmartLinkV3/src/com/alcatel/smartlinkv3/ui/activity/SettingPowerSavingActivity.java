package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.power.PowerSavingModeInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.ConstValue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SettingPowerSavingActivity extends Activity implements OnClickListener{

	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_smart_mode_switch=null;
	private Button m_btn_wifi_mode_switch=null;
	private TextView m_tv_battery_status=null;
	private ProgressBar m_pb_battery_status=null;
	private ImageView m_iv_battery_charge=null;
	
	private boolean m_blSmartModeSwitchOn=true;
	private boolean m_blWifiModeSwitchOn=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_power);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		//showBatterystatus
		showBatterystatus();
		//
		initSwitchsState();
	}

	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_power);

		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}

	private void createControls(){
		
		m_btn_smart_mode_switch=(Button)findViewById(R.id.btn_power_smart);
		m_btn_wifi_mode_switch=(Button)findViewById(R.id.btn_power_wifi);
		m_tv_battery_status=(TextView)findViewById(R.id.tv_power_status);
		m_pb_battery_status=(ProgressBar)findViewById(R.id.pb_power_saving);
		m_iv_battery_charge=(ImageView)findViewById(R.id.iv_power_display);
		
		m_btn_smart_mode_switch.setOnClickListener(this);
		m_btn_wifi_mode_switch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int nID=v.getId();
		switch (nID) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			SettingPowerSavingActivity.this.finish();
			break;
			
		case R.id.btn_power_smart:
			onBtnSmartModeSwitch();
			break;
			
		case R.id.btn_power_wifi:
			onBtnWifiModeSwitch();
			break;

		default:
			break;
		}
	}

	private void onBtnSmartModeSwitch(){
		int nSmartMode=ConstValue.ENABLE;
		int nWifiMode= ConstValue.ENABLE;
		if (m_blSmartModeSwitchOn) {
			m_blSmartModeSwitchOn = false;
			m_btn_smart_mode_switch.setBackgroundResource(R.drawable.switch_off);
			nSmartMode = ConstValue.DISABLE;
		}else {
			m_blSmartModeSwitchOn = true;
			m_btn_smart_mode_switch.setBackgroundResource(R.drawable.switch_on);
		}
		
		if (!m_blWifiModeSwitchOn) {
			nWifiMode=ConstValue.DISABLE;
		}
		PowerSavingModeInfo info=new PowerSavingModeInfo();
		info.setSmartMode(nSmartMode);
		info.setWiFiMode(nWifiMode);
		DataValue data = new DataValue();
		data.addParam("PowerSavingMode", info);
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.POWER_SET_POWER_SAVING_MODE, data);
	}
	
	private void onBtnWifiModeSwitch(){
		int nSmartMode=ConstValue.ENABLE;
		int nWifiMode= ConstValue.ENABLE;
		if (m_blWifiModeSwitchOn) {
			m_blWifiModeSwitchOn = false;
			m_btn_wifi_mode_switch.setBackgroundResource(R.drawable.switch_off);
			nWifiMode = ConstValue.DISABLE;
		}else {
			m_blWifiModeSwitchOn = true;
			m_btn_wifi_mode_switch.setBackgroundResource(R.drawable.switch_on);
		}
		
		if (!m_blSmartModeSwitchOn) {
			nSmartMode=ConstValue.DISABLE;
		}
		PowerSavingModeInfo info=new PowerSavingModeInfo();
		info.setSmartMode(nSmartMode);
		info.setWiFiMode(nWifiMode);
		DataValue data = new DataValue();
		data.addParam("PowerSavingMode", info);
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.POWER_SET_POWER_SAVING_MODE, data);
	}
	
	private void showBatterystatus(){
		BatteryInfo info = BusinessMannager.getInstance().getBatteryInfo();
		int nChargeState = info.getChargeState();
		if (ConstValue.CHARGE_STATE_CHARGING != nChargeState) {
			m_pb_battery_status.setVisibility(View.VISIBLE);
			m_iv_battery_charge.setVisibility(View.GONE);
		}else {
			m_iv_battery_charge.setVisibility(View.VISIBLE);
			m_pb_battery_status.setVisibility(View.GONE);
		}
		String strLevel = info.getBatterLevel() + "%";
		m_tv_battery_status.setText(strLevel);
	}
	
	private void initSwitchsState(){
		PowerSavingModeInfo info = BusinessMannager.getInstance().getPowerSavingModeInfo();
		if (ConstValue.ENABLE == info.getSmartMode()) {
			m_btn_smart_mode_switch.setBackgroundResource(R.drawable.switch_on);
			m_blSmartModeSwitchOn = true;
		}else {
			m_btn_smart_mode_switch.setBackgroundResource(R.drawable.switch_off);
			m_blSmartModeSwitchOn = false;
		}
		
		if (ConstValue.ENABLE == info.getWiFiMode()) {
			m_btn_wifi_mode_switch.setBackgroundResource(R.drawable.switch_on);
			m_blWifiModeSwitchOn = true;
		}else {
			m_btn_wifi_mode_switch.setBackgroundResource(R.drawable.switch_off);
			m_blWifiModeSwitchOn = false;
		}
	}
}
