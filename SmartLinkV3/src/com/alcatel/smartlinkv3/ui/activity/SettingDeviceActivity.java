package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog.OnPINError;
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
	
	private PinDialog m_dlgPin = null;
	private PukDialog m_dlgPuk = null;
	private ErrorDialog m_dlgError = null;
	
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
		
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_device_waiting_progress);
		
		m_pincode_editor = (FrameLayout) findViewById(R.id.setting_device_pincode_editor);
		m_pincode_editor.setVisibility(View.GONE);
		m_pincode_editor.setOnClickListener(this);
		
		m_dlgPin = PinDialog.getInstance(this);
		m_dlgPuk = PukDialog.getInstance(this);
		m_dlgError = ErrorDialog.getInstance(this);
		
		m_device_menu_container = (ScrollView) findViewById(R.id.device_menu_container);
		m_device_menu_container.setVisibility(View.VISIBLE);
		
		ShowWaiting(false);
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
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();

		if (sim.m_SIMState == SIMState.PinRequired) {
			// close PUK dialog
			if (null != m_dlgPuk && PukDialog.m_isShow)
				m_dlgPuk.closeDialog();
			// set the remain times
			if (null != m_dlgPin)
				m_dlgPin.updateRemainTimes(sim.m_nPinRemainingTimes);

			if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
				if (!PinDialog.m_isShow) {
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
			if (null != m_dlgPin && PinDialog.m_isShow)
				m_dlgPin.closeDialog();

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
		} else {
			closePinAndPukDialog();
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
		if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
			if (!PinDialog.m_isShow) {
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
		if (null != m_dlgPin && PinDialog.m_isShow) {
			m_dlgPin.closeDialog();
		}

		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		// set the remain times
		if (null != m_dlgPuk) {
			m_dlgPuk.updateRemainTimes(simStatus.m_nPukRemainingTimes);
		}
		if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
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
//			m_device_menu_container.setVisibility(View.VISIBLE);
//			m_pincode_editor.setVisibility(View.GONE);
			m_dlgPin.cancelUserClose();
			m_dlgPuk.cancelUserClose();
			ShowPinDialog();
			break;
		default:
			break;
		}
	}
	
	private void onBtnPincodeSetting(){
		m_device_menu_container.setVisibility(View.GONE);
		m_pincode_editor.setVisibility(View.VISIBLE);
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
				MessageUti.SIM_UNLOCK_PIN_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PUK_REQUEST));
		
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
		} else if (intent.getAction().equalsIgnoreCase(
				MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult
					&& strErrorCode.length() == 0) {
				m_dlgPin.onEnterPinResponse(true);
			} else {
				m_dlgPin.onEnterPinResponse(false);
			}
		} else if (intent.getAction().equalsIgnoreCase(
				MessageUti.SIM_UNLOCK_PUK_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult
					&& strErrorCode.length() == 0) {
				m_dlgPuk.onEnterPukResponse(true);
			} else {
				m_dlgPuk.onEnterPukResponse(false);
			}
		}
		
	}

}
