package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinStateDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireCancle;
import com.alcatel.smartlinkv3.ui.dialog.PinStateDialog.OnPINError;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog.OnPUKError;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingDeviceActivity extends BaseActivity implements OnClickListener{
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	
	private FrameLayout m_system_info = null;
	private FrameLayout m_upgrade_system = null;
	private FrameLayout m_backup_and_reset = null;
	private FrameLayout m_power_saving = null;
	private FrameLayout m_pin_code = null;
	private FrameLayout m_web_version = null;
	private FrameLayout m_restart = null;
	private FrameLayout m_power_off = null;
	
	private ProgressBar m_pb_waiting=null;
	
	private FrameLayout m_pincode_editor = null;
	private ScrollView m_device_menu_container = null;
	
	private PinStateDialog m_dlgPin = null;
	private PukDialog m_dlgPuk = null;
	private ErrorDialog m_dlgError = null;
	
	private TextView m_switch_button = null;
	
	private boolean isPinRequired;
	
	private boolean m_pin_state;
	private ENUM.PinState m_requested_pinState = ENUM.PinState.NotAvailable;
	
	private boolean m_blFirst=true;
	private ImageView m_hiddable_divider = null;
	
	private TextView m_pin_notice = null;
	
	private ENUM.PinState m_PrePinState = ENUM.PinState.NotAvailable;
	
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
		m_hiddable_divider = (ImageView)findViewById(R.id.device_hidable_divider);
		if(BusinessMannager.getInstance().getSystemInfo().getWebUiVersion().length() == 0){
			m_hiddable_divider.setVisibility(View.GONE);
			m_web_version.setVisibility(View.GONE);
		}
//		m_webversion_desc.setText(webVersion);
		m_restart = (FrameLayout) findViewById(R.id.device_restart);
		m_restart.setOnClickListener(this);
		m_power_off = (FrameLayout) findViewById(R.id.device_power_off);
		m_power_off.setOnClickListener(this);
		
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_device_waiting_progress);
		
		m_switch_button = (TextView) findViewById(R.id.btn_default_switch);
		
		m_pincode_editor = (FrameLayout) findViewById(R.id.setting_device_pincode_editor);
		m_pincode_editor.setVisibility(View.GONE);
		m_pincode_editor.setOnClickListener(this);
		
		m_dlgPin = PinStateDialog.getInstance(this);
		m_dlgPuk = PukDialog.getInstance(this);
		m_dlgError = ErrorDialog.getInstance(this);
		
		m_device_menu_container = (ScrollView) findViewById(R.id.device_menu_container);
		m_device_menu_container.setVisibility(View.VISIBLE);
		
		ShowWaiting(false);
		
		isPinRequired = false;
		if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified){
			m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_on);
			m_requested_pinState = ENUM.PinState.Disable;
		}
		else if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable){
			m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_off);
			m_requested_pinState = ENUM.PinState.PinEnableVerified;
		}
		m_PrePinState = BusinessMannager.getInstance().getSimStatus().m_PinState;
		
		m_pin_notice = (TextView) findViewById(R.id.setting_device_pincode_editor_notice);
		m_pin_notice.setVisibility(View.GONE);
	}
	
	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pb_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_waiting.setVisibility(View.GONE);
		}
		m_system_info.setEnabled(!blShow);
		m_power_off.setEnabled(!blShow);
		m_restart.setEnabled(!blShow);
		m_backup_and_reset.setEnabled(!blShow);
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
		m_upgrade_system.setEnabled(!blShow);
		m_power_saving.setEnabled(!blShow);
		m_pin_code.setEnabled(!blShow);
		m_web_version.setEnabled(!blShow);
	}
	
	
	private void simRollRequest() {
		final SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();

		if(sim.m_PinState != m_PrePinState && sim.m_PinState != ENUM.PinState.RequirePUK){
			m_PrePinState = sim.m_PinState;
			if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified){
				m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_on);
				m_requested_pinState = ENUM.PinState.Disable;
			}
			else if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable){
				m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_off);
				m_requested_pinState = ENUM.PinState.PinEnableVerified;
			}
			closePinAndPukDialog();
			return;
		}
		if (isPinRequired && sim.m_nPinRemainingTimes > 0) {
			// close PUK dialog
			if (null != m_dlgPuk && PukDialog.m_isShow)
				m_dlgPuk.closeDialog();
			// set the remain times
			if (null != m_dlgPin)
				m_dlgPin.updateRemainTimes(sim.m_nPinRemainingTimes);

			if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
				if (!PinStateDialog.m_isShow) {
					m_dlgPin.showDialog(sim.m_nPinRemainingTimes,
							new OnPINError() {
								@Override
								public void onPinError() {
									String strMsg = getString(R.string.pin_error_waring_title);
									m_dlgError.showDialog(strMsg,
											new OnClickBtnRetry() {
												@Override
												public void onRetry() {
													m_dlgPin.showDialog();
												}
											});
								}
							});
				} else {
					m_dlgPin.onSimStatusReady(sim);
				}
			}
		} else if (sim.m_SIMState == SIMState.PukRequired) {// puk
			// close PIN dialog
			m_pin_notice.setVisibility(View.VISIBLE);
			if (null != m_dlgPin && PinStateDialog.m_isShow)
				m_dlgPin.closeDialog();
			
			final InquireReplaceDialog inquireDlg = new InquireReplaceDialog(
					SettingDeviceActivity.this);
			inquireDlg.setCancelDisabled();
			inquireDlg.m_titleTextView.setText(R.string.dialog_warning_title);
			inquireDlg.m_contentTextView
					.setText(R.string.dialog_warning_error_pin_code_error_3times);
			inquireDlg.m_confirmBtn.setText(R.string.confirm);
			inquireDlg.showDialog(new OnInquireApply(){

					@Override
					public void onInquireApply() {
						// TODO Auto-generated method stub
						inquireDlg.closeDialog();
						// set the remain times
						if (null != m_dlgPuk)
							m_dlgPuk.updateRemainTimes(sim.m_nPukRemainingTimes);
	
						if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
							if (!PukDialog.m_isShow) {
								m_dlgPuk.showDialog(sim.m_nPukRemainingTimes,
										new OnPUKError() {
	
											@Override
											public void onPukError() {
												String strMsg = getString(R.string.puk_error_waring_title);
												m_dlgError.showDialog(strMsg,
														new OnClickBtnRetry() {
	
															@Override
															public void onRetry() {
																m_dlgPuk.showDialog();
															}
														});
											}
										});
							} else {
								m_dlgPuk.onSimStatusReady(sim);
							}
						}
					}
				}, new OnInquireCancle(){

					@Override
					public void onInquireCancel() {
						// TODO Auto-generated method stub
						inquireDlg.closeDialog();
						closePinAndPukDialog();
					}
					
				});
			
		} else {
			closePinAndPukDialog();
			if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified){
				m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_on);
				m_requested_pinState = ENUM.PinState.Disable;
			}
			else if(BusinessMannager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable){
				m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_off);
				m_requested_pinState = ENUM.PinState.PinEnableVerified;
			}
		}
	}
	
	private void closePinAndPukDialog() {
		if (m_dlgPin != null)
			m_dlgPin.closeDialog();

		if (m_dlgPuk != null)
			m_dlgPuk.closeDialog();

		if (m_dlgError != null)
			m_dlgError.closeDialog();
	}
	
	
	private void ShowPinDialog() {
		// close PUK dialog
		if (null != m_dlgPuk && PukDialog.m_isShow) {
			m_dlgPuk.closeDialog();
		}

		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		// set the remain times
		if (null != m_dlgPin) {
			m_dlgPin.updateRemainTimes(simStatus.m_nPinRemainingTimes);
		}
		if (null != m_dlgPin) {
			if (!PinStateDialog.m_isShow) {
				m_dlgPin.showDialog(simStatus.m_nPinRemainingTimes,
						new OnPINError() {

							@Override
							public void onPinError() {
								String strMsg = getString(R.string.pin_error_waring_title);
								m_dlgError.showDialog(strMsg,
										new OnClickBtnRetry() {

											@Override
											public void onRetry() {
												m_dlgPin.showDialog();
											}
										});
							}
						});
			}
		}
	}
	
	private void ShowPukDialog() {
		// close PIN dialog
		if (null != m_dlgPin && PinStateDialog.m_isShow) {
			m_dlgPin.closeDialog();
		}
		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		// set the remain times
		if (null != m_dlgPuk) {
			m_dlgPuk.updateRemainTimes(simStatus.m_nPukRemainingTimes);
		}
		if (null != m_dlgPuk) {
			if (!PukDialog.m_isShow) {
				m_dlgPuk.showDialog(simStatus.m_nPukRemainingTimes,
						new OnPUKError() {

							@Override
							public void onPukError() {
								String strMsg = getString(R.string.puk_error_waring_title);
								m_dlgError.showDialog(strMsg,
										new OnClickBtnRetry() {

											@Override
											public void onRetry() {
												m_dlgPuk.showDialog();
											}
										});

							}
						});
			}
		}
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
	
	private void goToUpgradeSettingPage(){
		Intent intent = new Intent(this, SettingUpgradeActivity.class);
		intent.putExtra("First", m_blFirst);
		m_blFirst = false;
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
			goToUpgradeSettingPage();
			break;
		case R.id.device_backup_and_reset:
			goToBackupSettingPage();
			break;
		case R.id.device_power_saving:
			goToPowerSettingPage();
			break;
		case R.id.device_pin_code:
			onBtnPincodeSetting();
			break;
		case R.id.device_web_version:
			String strTemp = "http://" + BusinessMannager.getInstance().getServerAddress();
			CommonUtil.openWebPage(this, strTemp);
			break;
		case R.id.device_restart:
			onBtnRestart();
			ShowWaiting(true);
			break;
		case R.id.device_power_off:
			onBtnPowerOff();
			ShowWaiting(true);
			break;
		case R.id.setting_device_pincode_editor:
//			onDoneEditPincodeSetting();
//			m_dlgPin.cancelUserClose();
//			m_dlgPuk.cancelUserClose();
			ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
			if(internetConnState.m_connectionStatus != ConnectionStatus.Disconnected){
				if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting){
					String strInfo = getString(R.string.setting_network_try_again);
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
					return;
				}
				else{
					String strInfo = getString(R.string.setting_network_disconnect_first);
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
					return;
				}
			}
			SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
			if(simStatus.m_SIMState == SIMState.Accessable || 
				simStatus.m_SIMState == SIMState.PinRequired || 
				simStatus.m_SIMState == SIMState.PukRequired){
				if(simStatus.m_nPinRemainingTimes == 0){
					ShowPukDialog();
				}
				else if(simStatus.m_nPinRemainingTimes > 0){
					ShowPinDialog();
				}
			}
			else{
				String strInfo = getString(R.string.home_sim_not_accessible);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	private void onDoneEditPincodeSetting(){
//		m_device_menu_container.setVisibility(View.VISIBLE);
//		m_pincode_editor.setVisibility(View.GONE);
		if(m_pin_state){
			m_pin_state = false;
			m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_off);
		}
		else{
			m_pin_state = true;
			m_switch_button.setBackgroundResource(R.drawable.pwd_switcher_on);
		}
		
	}
	
	private void onBtnPincodeSetting(){
		if(BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.NoSim ||
			BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.InvalidSim||
			BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.SimCardIsIniting){
			String strInfo = getString(R.string.home_sim_not_accessible);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		m_device_menu_container.setVisibility(View.GONE);
		m_pincode_editor.setVisibility(View.VISIBLE);
		if(BusinessMannager.getInstance().getSimStatus().m_nPinRemainingTimes <= 0){
			m_pin_notice.setVisibility(View.VISIBLE);
		}
	}
	
	private void onBtnPowerOff(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF, null);
	}
	
	private void onBtnRestart(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_REBOOT, null);
	}
	
	private void onBtnReset(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_RESET, null);
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
		
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PUK_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_CHANGE_PIN_STATE_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.USER_LOGOUT_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
		this.registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
		
		int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
		if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
			m_blFirst = false;
//			changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
		}else {
//			changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
		}	
		
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET, null);
		ShowWaiting(true);
	}
	
	
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_REBOOT)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_reboot_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_reboot_success);
			}
			ShowWaiting(false);
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET)){
			ShowWaiting(false);
		}
		
		
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_RESET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_reset_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_reset_success);
			}
			ShowWaiting(false);
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_power_off_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_power_off_success);
			}
			ShowWaiting(false);
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
		}
		
		if (intent.getAction().equalsIgnoreCase(
				MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult&& strErrorCode.length() == 0) {
				simRollRequest();
			}
			else{
				closePinAndPukDialog();
				String strTost = getString(R.string.unknown_error);
				Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			}
		} else if (intent.getAction().equalsIgnoreCase(
				MessageUti.SIM_CHANGE_PIN_STATE_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult
					&& strErrorCode.length() == 0) {
				m_dlgPin.onEnterPinResponse(true);
				isPinRequired = false;
			} else {
				m_dlgPin.onEnterPinResponse(false);
				isPinRequired = true;
			}
		}
		else if (intent.getAction().equalsIgnoreCase(
				MessageUti.SIM_UNLOCK_PUK_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult&& strErrorCode.length() == 0) {
				m_pin_notice.setVisibility(View.GONE);
				m_dlgPuk.onEnterPukResponse(true);
			}
			else{
				m_dlgPuk.onEnterPukResponse(false);
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
			}else {

				Toast.makeText(getBaseContext(), R.string.setting_upgrade_stop_error, Toast.LENGTH_SHORT).show();
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
				int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
				if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
					m_blFirst = false;
//					changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
				}else {
//					changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
				}
			}
		}
		
		
	}

}
