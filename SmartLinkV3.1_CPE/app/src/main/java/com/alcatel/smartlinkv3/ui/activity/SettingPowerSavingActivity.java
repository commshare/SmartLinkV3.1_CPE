package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.power.PowerSavingModeInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingPowerSavingActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_smart_mode_switch=null;
	private Button m_btn_wifi_mode_switch=null;
	private TextView m_tv_battery_status=null;
	private ProgressBar m_pb_battery_status=null;
	private ImageView m_iv_battery_charge=null;
	private ProgressBar m_pb_waiting=null;
	private RelativeLayout m_rlWifi=null;
	private ImageView m_ivSeperatorWifi=null;
	private TextView m_tv_wifi_description=null;
	private LinearLayout m_power_container = null;

	private boolean m_blSmartModeSwitchOn=true;
	private boolean m_blWifiModeSwitchOn=true;
	private boolean m_blShowWifiSleep = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_power);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		ShowWaiting(false);
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

		m_power_container = (LinearLayout) findViewById(R.id.rl_power_display);
		if(!FeatureVersionManager.getInstance().isSupportApi("PowerManagement", "GetBatteryState"))
		{
			m_power_container.setVisibility(View.GONE);
		}
		m_btn_smart_mode_switch=(Button)findViewById(R.id.btn_power_smart);
		m_btn_wifi_mode_switch=(Button)findViewById(R.id.btn_power_wifi);
		m_tv_battery_status=(TextView)findViewById(R.id.tv_power_status);
		m_pb_battery_status=(ProgressBar)findViewById(R.id.pb_power_saving);
		m_iv_battery_charge=(ImageView)findViewById(R.id.iv_power_display);

		m_btn_smart_mode_switch.setOnClickListener(this);
		m_btn_wifi_mode_switch.setOnClickListener(this);
		//
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_power_waiting_progress);
		//get device name from feature list
		String strDeviceName = BusinessManager.getInstance().getFeatures().getDeviceName();
		if (0 == strDeviceName.compareToIgnoreCase("Y900")) {
			m_blShowWifiSleep = false;
		}
		m_rlWifi = (RelativeLayout)findViewById(R.id.rl_power_wifi_control);
		m_ivSeperatorWifi = (ImageView)findViewById(R.id.iv_power_seperator_1);
		m_tv_wifi_description = (TextView)findViewById(R.id.tv_wifi_description);
		if (!m_blShowWifiSleep) {
			m_rlWifi.setVisibility(View.GONE);
			m_ivSeperatorWifi.setVisibility(View.GONE);
			m_tv_wifi_description.setVisibility(View.GONE);
		}else {
			m_rlWifi.setVisibility(View.VISIBLE);
			m_ivSeperatorWifi.setVisibility(View.VISIBLE);
			//m_tv_wifi_description.setText(R.string.setting_power_wifi_description);
			m_tv_wifi_description.setVisibility(View.VISIBLE);
		}
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pb_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_waiting.setVisibility(View.GONE);
		}
		m_btn_smart_mode_switch.setEnabled(!blShow);
		m_btn_wifi_mode_switch.setEnabled(!blShow);
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
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
			ShowWaiting(true);
			break;

		case R.id.btn_power_wifi:
			onBtnWifiModeSwitch();
			ShowWaiting(true);
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
		BusinessManager.getInstance().
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
		BusinessManager.getInstance().
		sendRequestMessage(MessageUti.POWER_SET_POWER_SAVING_MODE, data);
	}

	private void showBatterystatus(){
		BatteryInfo info = BusinessManager.getInstance().getBatteryInfo();
		int nChargeState = info.getChargeState();
		if (ConstValue.CHARGE_STATE_CHARGING != nChargeState) {
			m_pb_battery_status.setVisibility(View.VISIBLE);
			m_iv_battery_charge.setVisibility(View.GONE);
			m_pb_battery_status.setProgress(info.getBatterLevel());
		}else {
			m_iv_battery_charge.setVisibility(View.VISIBLE);
			m_pb_battery_status.setVisibility(View.GONE);
		}
		String strLevel = info.getBatterLevel() + "%";
//		if (ConstValue.CHARGE_STATE_CHARGING == nChargeState) {
//			strLevel = "Charging";
//		}
		m_tv_battery_status.setText(strLevel);
	}

	private void initSwitchsState(){
		PowerSavingModeInfo info = BusinessManager.getInstance().getPowerSavingModeInfo();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.POWER_SET_POWER_SAVING_MODE));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.POWER_GET_POWER_SAVING_MODE));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.POWER_GET_BATTERY_STATE));
		
		BusinessManager.getInstance().sendRequestMessage(MessageUti.POWER_GET_POWER_SAVING_MODE, null);
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

		if(intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_POWER_SAVING_MODE)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				initSwitchsState();
			}
			ShowWaiting(false);
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.POWER_SET_POWER_SAVING_MODE)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_success);
				int nSmartMode = m_blSmartModeSwitchOn? ConstValue.ENABLE : ConstValue.DISABLE;
				BusinessManager.getInstance().getPowerSavingModeInfo().setSmartMode(nSmartMode);
				int nWiFiMode = m_blWifiModeSwitchOn? ConstValue.ENABLE : ConstValue.DISABLE;
				BusinessManager.getInstance().getPowerSavingModeInfo().setWiFiMode(nWiFiMode);
				//initSwitchsState();
			}else {
				initSwitchsState();
			}
			ShowWaiting(false);
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.POWER_GET_BATTERY_STATE)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				showBatterystatus();
			}
		}
	}
}
