package com.alcatel.smartlinkv3.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.sharing.SDcardStatus;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog.OnClickBottonConfirm;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog.OnPINError;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog.OnPUKError;
import com.alcatel.smartlinkv3.ui.view.ViewHome;
import com.alcatel.smartlinkv3.ui.view.ViewIndex;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;
import com.alcatel.smartlinkv3.ui.view.ViewSetting;
import com.alcatel.smartlinkv3.ui.view.ViewSms;
import com.alcatel.smartlinkv3.ui.view.ViewWifiKey;

import org.cybergarage.upnp.Device;

import java.util.List;

public class MainActivity extends BaseActivity implements OnClickListener, IDeviceChangeListener {

	public static String PAGE_TO_VIEW_HOME = "com.alcatel.smartlinkv3.toPageViewHome";

	final int HOME_PAGE = 1;
	final int SMS_PAGE = 2;
	final int BATTERY_PAGE = 3;
	final int USAGE_PAGE = 4;
	int m_preButton = 0;
	int m_nNewCount = 0;

	private RelativeLayout rl_top;
	
	private ViewFlipper m_viewFlipper;
	
	private TextView m_homeBtn;
	private TextView m_microsdBtn;
	private TextView m_wifiKeyBtn;
	private RelativeLayout m_smsBtn;
	private TextView m_settingBtn;
	
	private TextView m_smsTextView;
	private TextView m_newSmsTextView;


	private TextView m_titleTextView;
	private Button m_Btnbar;

	private ViewHome m_homeView;
	private static ViewWifiKey m_wifiKeyView;
	private ViewSms m_smsView;
	
	private ViewSetting m_settingView;
	private ViewMicroSD m_microsdView;

	public static DisplayMetrics m_displayMetrics = new DisplayMetrics();
	
	private PinDialog m_dlgPin;
	private PukDialog m_dlgPuk;
	private ErrorDialog m_dlgError;
	private LoginDialog m_loginDlg;
	private AutoForceLoginProgressDialog m_ForceloginDlg;
	private AutoLoginProgressDialog	m_autoLoginDialog;

	Button m_unlockSimBtn;
	private int pageIndex;
	static boolean m_blLogout;
	static boolean m_blkickoff_Logout;
	
	private CommonErrorInfoDialog m_dialog_timeout_info;
	
	RelativeLayout m_accessDeviceLayout;

	private DMSDeviceBrocastFactory mBrocastFactory;
	private AllShareProxy mAllShareProxy;
	private ThumbnailLoader thumbnailLoader;
	private static Device mDevice;
	private android.app.FragmentManager fm;
	private String wifi_key_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawable(null);
		this.getWindowManager().getDefaultDisplay()
				.getMetrics(m_displayMetrics);
		fm = getFragmentManager();
		rl_top = (RelativeLayout)findViewById(R.id.main_layout_top);
		wifi_key_status = getResources().getString(R.string.wifi_key_edit);

        RelativeLayout.LayoutParams rl_params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        rl_params.height = (m_displayMetrics.heightPixels * 9)/100;
        rl_top.setLayoutParams(rl_params);

		m_homeBtn = (TextView) this.findViewById(R.id.main_home);
		m_homeBtn.setOnClickListener(this);
		m_microsdBtn = (TextView) this.findViewById(R.id.main_microsd);
		m_microsdBtn.setOnClickListener(this);
		m_wifiKeyBtn = (TextView) this.findViewById(R.id.main_wifiKey);
		m_wifiKeyBtn.setOnClickListener(this);
		m_smsBtn = (RelativeLayout) this.findViewById(R.id.tab_sms_layout);
		m_smsBtn.setOnClickListener(this);
		m_settingBtn = (TextView) this.findViewById(R.id.main_setting);
		m_settingBtn.setOnClickListener(this);

		m_viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);

		m_smsTextView = (TextView) this.findViewById(R.id.main_sms);
		m_newSmsTextView = (TextView) this.findViewById(R.id.new_sms_count);
		
		m_titleTextView = (TextView) this.findViewById(R.id.main_title);
		
		m_Btnbar = (Button) this.findViewById(R.id.btnbar);
		m_Btnbar.setOnClickListener(this);

		addView();

		setMainBtnStatus(R.id.main_home);
		showView(ViewIndex.VIEW_HOME);
		updateTitleUI(ViewIndex.VIEW_HOME);
		pageIndex = ViewIndex.VIEW_HOME;
		
		m_dlgPin = PinDialog.getInstance(this);
		m_dlgPuk = PukDialog.getInstance(this);
		m_dlgError = ErrorDialog.getInstance(this);
		m_loginDlg = new LoginDialog(this);
		m_ForceloginDlg = new AutoForceLoginProgressDialog(this);
		m_autoLoginDialog = new AutoLoginProgressDialog(this);		
		m_unlockSimBtn = (Button) m_homeView.getView().findViewById(
				R.id.unlock_sim_button);
		m_unlockSimBtn.setOnClickListener(this);
		
		m_accessDeviceLayout = (RelativeLayout)m_homeView.getView().findViewById(R.id.access_num_layout);
		m_accessDeviceLayout.setOnClickListener(this);
		OnResponseAppWidget();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
		mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	mBrocastFactory.registerListener(this);
    
    	thumbnailLoader = new ThumbnailLoader(this);  
    	showMicroView();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PIN_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PUK_REQUEST));
		this.registerReceiver(m_msgReceiver2, new IntentFilter(
				MessageUti.USER_LOGOUT_REQUEST));
		
		
		this.registerReceiver(m_msgReceiver2, new IntentFilter(
				PAGE_TO_VIEW_HOME));
		this.registerReceiver(m_msgReceiver2, new IntentFilter(
				MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));

		registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
		registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
		registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));

		m_homeView.onResume();
		m_wifiKeyView.onResume();
		m_smsView.onResume();
		m_settingView.onResume();
		m_microsdView.onResume();
		
		updateBtnState();
		toPageHomeWhenPinSimNoOk();
		showMicroView();

		m_Btnbar.setText(getString(R.string.wifi_key_Done));
		wifiEditClick();
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			this.unregisterReceiver(m_msgReceiver);
		} catch (Exception e) {

		}
		m_homeView.onPause();
		m_wifiKeyView.onPause();
		m_smsView.onPause();
		m_settingView.onPause();
		m_microsdView.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyDialogs();

		m_homeView.onDestroy();
		m_wifiKeyView.onDestroy();
		m_wifiKeyView = null;
		m_smsView.onDestroy();
		m_settingView.onDestroy();
		m_microsdView.onDestroy();
		
		mBrocastFactory.unRegisterListener();
		mAllShareProxy.exitSearch();
		thumbnailLoader.clearCache();
	}
	
	private void destroyDialogs(){
		m_dlgPin.destroyDialog();
		m_dlgPuk.destroyDialog();
		m_dlgError.destroyDialog();
		m_loginDlg.destroyDialog();
		m_ForceloginDlg.destroyDialog();
		m_autoLoginDialog.destroyDialog();
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		super.onBroadcastReceive(context, intent);

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
		
		if (intent.getAction().equalsIgnoreCase(
				MessageUti.USER_LOGOUT_REQUEST)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
					BaseResponse.RESPONSE_OK);
			String strErrorCode = intent
					.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult
					&& strErrorCode.length() == 0){
				if (m_blLogout) {
					String strInfo = getString(R.string.login_logout_successful);
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();					
				}
				
				if (m_blkickoff_Logout) {
					String strInfo = getString(R.string.login_kickoff_logout_successful);
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();					
				}
				m_blLogout = false;
				m_blkickoff_Logout = false;
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(PAGE_TO_VIEW_HOME)) {
			homeBtnClick();
		}
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
				if(BusinessMannager.getInstance().getDlnaSettings().getDlnaStatus() > 0)
				{
					mAllShareProxy.startSearch();
				}else
				{
					mAllShareProxy.exitSearch();
				}
				
			}
		}
	}
	
	private void simRollRequest() {
		updateBtnState();
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
		toPageHomeWhenPinSimNoOk();

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
	
	public void updateNewSmsUI(int nNewSmsCount) {
		m_nNewCount = nNewSmsCount;
		int nActiveBtnId = m_preButton;
		/*int nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_sms_placeholder
				: R.drawable.main_sms_placeholder;
		Drawable d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_smsTextView.setCompoundDrawables(null, d, null, null);

		if (nNewSmsCount <= 0) {
			m_newSmsTextView.setVisibility(View.GONE);
			nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_sms_no_new_active
					: R.drawable.main_sms_no_new_grey;
			d = getResources().getDrawable(nDrawable);
			d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
			m_smsTextView.setCompoundDrawables(null, d, null, null);
		} else if (nNewSmsCount < 10) {
			m_newSmsTextView.setVisibility(View.VISIBLE);
			m_newSmsTextView.setText(String.valueOf(nNewSmsCount));
			nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_new_sms_tab_9_plus_active
					: R.drawable.main_new_sms_tab_9_plus_grey;
			m_newSmsTextView.setBackgroundResource(nDrawable);
		} else {
			m_newSmsTextView.setVisibility(View.VISIBLE);
			m_newSmsTextView.setText("");
			nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_new_sms_tab_active
					: R.drawable.main_new_sms_tab_grey;
			m_newSmsTextView.setBackgroundResource(nDrawable);
		}*/
		int nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_sms_no_new_active
				: R.drawable.main_sms_no_new_grey;
		Drawable d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_smsTextView.setCompoundDrawables(null, d, null, null);
		int nTextColor = nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.color.color_blue
				: R.color.color_grey;
		m_smsTextView.setTextColor(this.getResources().getColor(nTextColor));
		
		if (nNewSmsCount <= 0) {
			m_newSmsTextView.setVisibility(View.GONE);
		} else if (nNewSmsCount < 10) {
			m_newSmsTextView.setVisibility(View.VISIBLE);
			m_newSmsTextView.setText(String.valueOf(nNewSmsCount));
			nDrawable = R.drawable.tab_sms_new;
			m_newSmsTextView.setBackgroundResource(nDrawable);
		} else {
			m_newSmsTextView.setVisibility(View.VISIBLE);
			m_newSmsTextView.setText("");
			nDrawable = R.drawable.tab_sms_new_9_plus;
			m_newSmsTextView.setBackgroundResource(nDrawable);
		}
	}
	
	private void updateBtnState() {
		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			m_smsBtn.setEnabled(true);
		} else {
			m_smsBtn.setEnabled(false);
		}
	}

	private void toPageHomeWhenPinSimNoOk() {
		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState != SIMState.Accessable) {
			if (m_preButton == R.id.tab_sms_layout) {
				setMainBtnStatus(R.id.main_home);
				showView(ViewIndex.VIEW_HOME);
				updateTitleUI(ViewIndex.VIEW_HOME);
			}

			unlockSimBtnClick(false);
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


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_home:
			homeBtnClick();
			break;
		case R.id.main_wifiKey:
			wifiKeyBtnClick();
			break;
		case R.id.tab_sms_layout:
			smsBtnClick();
			break;
		case R.id.main_setting:
			settingBtnClick();
			break;
		case R.id.main_microsd:
			microsdBtnClick();
			break;
		case R.id. btnbar:
			if (LoginDialog.isLoginSwitchOff()) {		
				go2Click();	
			} else {		
				UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();	
				if (status == UserLoginStatus.Logout) 
				{
					m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
					{
						public void onLoginSuccess() 				
						{
							go2Click();
						}

						public void onLoginFailed(String error_code)
						{
							if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
							{
								if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
								{
									m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
								}else
								{

								ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
										new OnClickBottonConfirm() 
								{
									public void onConfirm() 
									{
										m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
											public void onLoginSuccess() 				
											{
												go2Click();
											}

											public void onLoginFailed(String error_code)
											{
												if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
												{
													SmartLinkV3App.getInstance().setIsforcesLogin(true);
													ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
															new OnClickBtnRetry() 
													{
														@Override
														public void onRetry() 
														{
															m_loginDlg.showDialog(new OnLoginFinishedListener() {
																@Override
																public void onLoginFinished() {
																	go2Click();
																}
															});
														}
													});
												}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
												{
													m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
												}
											}
										});
									}
								});
								}
							}
							else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
							{
								m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
							}
							else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
							{
								ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
										new OnClickBtnRetry() 
								{
									@Override
									public void onRetry() 
									{
										m_loginDlg.showDialog(new OnLoginFinishedListener() {
											@Override
											public void onLoginFinished() {
												go2Click();
											}
										});
									}
								});
							}else{}				
						}

						@Override
						public void onFirstLogin() 
						{
							m_loginDlg.showDialog(new OnLoginFinishedListener() {
								@Override
								public void onLoginFinished() {
									go2Click();
								}
							});
						}					
					});			
					
				} else if (status == UserLoginStatus.login) {			
					go2Click();		
				} else {		
					PromptUserLogined();	
				}	
			}
					
			break;
		case R.id.unlock_sim_button:
			unlockSimBtnClick(true);
			break;
		case R.id.access_num_layout:
			accessDeviceLayoutClick();
			break;
		default:
			break;
		}
	}
	
	private void go2Click()
	{
		if(this.pageIndex == ViewIndex.VIEW_WIFIKEY){
			wifiEditClick();
		}else if(this.pageIndex == ViewIndex.VIEW_SMS){
			editBtnClick();
		}
	}

	private void wifiEditClick() {
		if (m_Btnbar.getText().equals(getResources().getString(R.string.wifi_key_edit))){
			wifi_key_status = getResources().getString(R.string.wifi_key_Done);
			m_Btnbar.setText(wifi_key_status);
			//下面要变为编辑状态
			Intent intent = new Intent();
			intent.setAction(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST);
			sendBroadcast(intent);
		} else {
			if (!m_wifiKeyView.getTypeSelectionFragmentVisible()) {
				wifi_key_status = getResources().getString(R.string.wifi_key_edit);
				m_Btnbar.setText(wifi_key_status);
				m_wifiKeyView.setTypeSelectionFragmentVisible(false);
				if (m_wifiKeyView.getM_isTypeSelecttionDone()){
					m_wifiKeyView.setM_isTypeSelecttionDone(false);
					m_wifiKeyView.setTypeSelectionFragmentVisible(false);
				}
			} else {
				if (m_wifiKeyView.getM_isTypeSelecttionDone()){
					m_wifiKeyView.setM_isTypeSelecttionDone(false);
				}
			}

			//下面要变为完成编辑状态
			Intent intent = new Intent();
			intent.setAction(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST);
			sendBroadcast(intent);
		}
	}

	private void accessDeviceLayoutClick() {
		if (LoginDialog.isLoginSwitchOff()) {		
			startDeviceManagerActivity();	
		} else {		
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();	
			if (status == UserLoginStatus.Logout) 
			{
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						startDeviceManagerActivity();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
								m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{

							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											startDeviceManagerActivity();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																startDeviceManagerActivity();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											startDeviceManagerActivity();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								startDeviceManagerActivity();
							}
						});
					}					
				});				
			} 
			else if (status == UserLoginStatus.login) 
			{			
				startDeviceManagerActivity();		
			} 
			else 
			{
				PromptUserLogined();
			}	
		}	
	}
	
	private void startDeviceManagerActivity()
	{
		Intent intent = new Intent();
		intent.setClass(this, ActivityDeviceManager.class);	
		this.startActivity(intent);
	}

	private void homeBtnClick() {
		if (m_preButton == R.id.main_home) {
			return;
		}
		setMainBtnStatus(R.id.main_home);
		showView(ViewIndex.VIEW_HOME);
		updateTitleUI(ViewIndex.VIEW_HOME);
		pageIndex = ViewIndex.VIEW_HOME;
	}

	private void wifiKeyBtnClick() {
		if (m_preButton == R.id.main_wifiKey) {
			return;
		}
		
		if (LoginDialog.isLoginSwitchOff()) {
			go2WifiKeyView();
		} else {
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();

			if (status == UserLoginStatus.Logout) 
			{
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						go2WifiKeyView();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											go2WifiKeyView();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																go2WifiKeyView();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											go2WifiKeyView();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								go2WifiKeyView();
							}
						});
					}					
				});					
				
			} else if (status == UserLoginStatus.login) {
				go2WifiKeyView();
			} else {
				PromptUserLogined();
			}
		}
	}
	
	private void go2WifiKeyView() {
		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		if(simStatus.m_SIMState == SIMState.Accessable) {
			setMainBtnStatus(R.id.main_wifiKey);
			showView(ViewIndex.VIEW_WIFIKEY);
			updateTitleUI(ViewIndex.VIEW_WIFIKEY);
			pageIndex = ViewIndex.VIEW_WIFIKEY;
		}
	}
	
	private void smsBtnClick() {
		if (m_preButton == R.id.tab_sms_layout) {
			return;
		}		
	
		if (LoginDialog.isLoginSwitchOff()) {
			go2SmsView();
		} else {
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();

			if (status == UserLoginStatus.Logout) 
			{
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						go2SmsView();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
								m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											go2SmsView();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																go2SmsView();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											go2SmsView();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								go2SmsView();
							}
						});						
					}					
				});				
				
			} else if (status == UserLoginStatus.login) {
				go2SmsView();
			} else {
				PromptUserLogined();
			}
		}	
	}

	private void go2SmsView() {
		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		if(simStatus.m_SIMState == SIMState.Accessable) {
			setMainBtnStatus(R.id.tab_sms_layout);
			showView(ViewIndex.VIEW_SMS);
			updateTitleUI(ViewIndex.VIEW_SMS);
			pageIndex = ViewIndex.VIEW_SMS;
		}
	}

	private void settingBtnClick() {
		if (m_preButton == R.id.main_setting) {		
			return;	
		}	
		if (LoginDialog.isLoginSwitchOff()) {		
			go2SettingView();	
		} else {		
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();	
			if (status == UserLoginStatus.Logout) 
			{				
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						go2SettingView();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
								m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											go2SettingView();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																go2SettingView();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											go2SettingView();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								go2SettingView();
							}
						});
					}					
				});				
				
			} else if (status == UserLoginStatus.login) {			
				go2SettingView();		
			} else {
				PromptUserLogined();
			}	
		}	
	}

	private void go2SettingView() {
		setMainBtnStatus(R.id.main_setting);
		showView(ViewIndex.VIEW_SETTINGE);
		updateTitleUI(ViewIndex.VIEW_SETTINGE);
		pageIndex = ViewIndex.VIEW_SETTINGE;
	}
	
	private void microsdBtnClick() {
		if (m_preButton == R.id.main_microsd) {
			return;
		}
		
		if (LoginDialog.isLoginSwitchOff()) {
			go2MicroSDView();
		} else{
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();

			if (status == UserLoginStatus.Logout) 
			{
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						go2MicroSDView();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
								m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											go2MicroSDView();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																go2MicroSDView();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											go2MicroSDView();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								go2MicroSDView();
							}
						});
					}					
				});					
				
			} else if (status == UserLoginStatus.login) {
				go2MicroSDView();
			} else {
				PromptUserLogined();
			}
		}
	}
	
	private void go2MicroSDView(){
		SDcardStatus m_sdcardstatus = BusinessMannager.getInstance().getSDCardStatus();
		if(m_sdcardstatus.SDcardStatus > 0)
		{
			if((FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"GetDLNASettings"))|| (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900")))
			{
				BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, null);
			}
			
			if(FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"GetFtpStatus"))
			{
				BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_FTP_SETTING_REQUSET, null);
			}

			setMainBtnStatus(R.id.main_microsd);
			showView(ViewIndex.VIEW_MICROSD);
			updateTitleUI(ViewIndex.VIEW_MICROSD);
			pageIndex = ViewIndex.VIEW_MICROSD;
		}else
		{
			String strInfo = getString(R.string.microsd_no_sdcard);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		m_wifiKeyView.revertWifiModeSetting();
	}

	public static ViewWifiKey getWifiKeyView(){
		if (m_wifiKeyView != null){
			return m_wifiKeyView;
		} else {
			return null;
		}
	}

	private void addView() {
		m_homeView = new ViewHome(this);
		m_viewFlipper.addView(m_homeView.getView(), ViewIndex.VIEW_HOME,
				m_viewFlipper.getLayoutParams());

		m_wifiKeyView = new ViewWifiKey(this, fm);
		m_viewFlipper.addView(m_wifiKeyView.getView(),
				ViewIndex.VIEW_WIFIKEY, m_viewFlipper.getLayoutParams());
		m_wifiKeyView.setOnBackPressCallback(new ViewWifiKey.OnBackPressCallback() {
			@Override
			public void onBackPress() {
				MainActivity.super.onBackPressed();
			}
		});
		
		m_microsdView = new ViewMicroSD(this);
		m_viewFlipper.addView(m_microsdView.getView(),
				ViewIndex.VIEW_MICROSD, m_viewFlipper.getLayoutParams());
		
		m_smsView = new ViewSms(this);
		m_viewFlipper.addView(m_smsView.getView(), ViewIndex.VIEW_SMS,
				m_viewFlipper.getLayoutParams());

		m_settingView = new ViewSetting(this);
		m_viewFlipper.addView(m_settingView.getView(),
				ViewIndex.VIEW_SETTINGE, m_viewFlipper.getLayoutParams());
		
	}

	public void showView(int viewIndex) {
		m_viewFlipper.setDisplayedChild(viewIndex);
	}

	public void updateTitleUI(int viewIndex) {
		if (viewIndex == ViewIndex.VIEW_HOME) {
			rl_top.setVisibility(View.GONE);
			m_Btnbar.setVisibility(View.GONE);
			setMainBtnStatus(R.id.main_home);
		}
		if (viewIndex == ViewIndex.VIEW_WIFIKEY) {
			rl_top.setVisibility(View.VISIBLE);
			m_titleTextView.setText(R.string.wifi_key);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundColor(getResources().getColor(R.color.main_title_background));
			m_Btnbar.setText(wifi_key_status);
			m_Btnbar.setTextColor(getResources().getColor(R.color.color_white));
			m_Btnbar.setTextSize(20);
			setMainBtnStatus(R.id.main_wifiKey);
		}
		if (viewIndex == ViewIndex.VIEW_SMS) {
			rl_top.setVisibility(View.VISIBLE);
			m_titleTextView.setText(R.string.sms_title);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_edit_icon);
			m_Btnbar.setText("");
			setMainBtnStatus(R.id.tab_sms_layout);
		}
		if (viewIndex == ViewIndex.VIEW_SETTINGE) {
			rl_top.setVisibility(View.VISIBLE);
			m_titleTextView.setText(R.string.main_setting);
			m_Btnbar.setVisibility(View.GONE);
			setMainBtnStatus(R.id.main_setting);
		}
		if(viewIndex == ViewIndex.VIEW_MICROSD) {
			rl_top.setVisibility(View.VISIBLE);
			m_titleTextView.setText(R.string.main_sdsharing);
			m_Btnbar.setVisibility(View.GONE);
		}
	}

	private void setMainBtnStatus(int nActiveBtnId) {
		m_preButton = nActiveBtnId;
		int nDrawable = nActiveBtnId == R.id.main_home ? R.drawable.main_home_active
				: R.drawable.main_home_grey;
		Drawable d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_homeBtn.setCompoundDrawables(null, d, null, null);
		int nTextColor = nDrawable = nActiveBtnId == R.id.main_home ? R.color.color_blue
				: R.color.color_grey;
		m_homeBtn.setTextColor(this.getResources().getColor(nTextColor));


		nDrawable = nActiveBtnId == R.id.main_wifiKey ? R.drawable.tab_wifi_pre
				: R.drawable.tab_wifi_nor;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 12, d.getMinimumWidth() - 10, d.getMinimumHeight() - 10);
		m_wifiKeyBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_wifiKey ? R.color.color_blue
				: R.color.color_grey;
		m_wifiKeyBtn.setTextColor(this.getResources().getColor(nTextColor));

		updateNewSmsUI(m_nNewCount);

		nDrawable = nActiveBtnId == R.id.main_setting ? R.drawable.main_setting_active
				: R.drawable.main_setting_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_settingBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_setting ? R.color.color_blue
				: R.color.color_grey;
		m_settingBtn.setTextColor(this.getResources().getColor(nTextColor));
		
		nDrawable = nActiveBtnId == R.id.main_microsd ? R.drawable.main_microssd_active
				: R.drawable.main_microssd_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_microsdBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_microsd ? R.color.color_blue
				: R.color.color_grey;
		m_microsdBtn.setTextColor(this.getResources().getColor(nTextColor));
	}
	
	public static void setLogoutFlag(boolean blLogout){
		m_blLogout = blLogout;
	}
	
	public static void setKickoffLogoutFlag(boolean blLogout){
		m_blkickoff_Logout = blLogout;
	}
	
	private void editBtnClick(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityNewSms.class);	
		this.startActivity(intent);
	}
	
	private void unlockSimBtnClick(boolean blCancelUserClose) {
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
		if (SIMState.PinRequired == sim.m_SIMState) {
			if (blCancelUserClose) {
				m_dlgPin.cancelUserClose();
				m_dlgPuk.cancelUserClose();
			}
			ShowPinDialog();
		} else if (SIMState.PukRequired == sim.m_SIMState) {
			if (blCancelUserClose) {
				m_dlgPin.cancelUserClose();
				m_dlgPuk.cancelUserClose();
			}
			ShowPukDialog();
		}
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

	//
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
	
	private void PromptUserLogined() {
		if (null == m_dialog_timeout_info) {
			m_dialog_timeout_info = CommonErrorInfoDialog.getInstance(this);
		}
		m_dialog_timeout_info.showDialog(
				this.getString(R.string.other_login_warning_title),
				this.getResources().getString(
						R.string.login_login_time_used_out_msg));
	}

	private void widgetBatteryBtnClick() {
		if (LoginDialog.isLoginSwitchOff()) {		
			go2SettingPowerSavingActivity();
		} else {
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();

			if (status == UserLoginStatus.Logout) 
			{				
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess() 				
					{
						go2SettingPowerSavingActivity();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"))
							{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(MainActivity.this).showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											go2SettingPowerSavingActivity();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
												ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
														new OnClickBtnRetry() 
												{
													@Override
													public void onRetry() 
													{
														m_loginDlg.showDialog(new OnLoginFinishedListener() {
															@Override
															public void onLoginFinished() {
																go2SettingPowerSavingActivity();
															}
														});
													}
												});
											}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
											{
												m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),	m_loginDlg.getLoginTimeUsedOutString());
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(MainActivity.this).showDialog(getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry() 
							{
								@Override
								public void onRetry() 
								{
									m_loginDlg.showDialog(new OnLoginFinishedListener() {
										@Override
										public void onLoginFinished() {
											go2SettingPowerSavingActivity();
										}
									});
								}
							});
						}
					}

					@Override
					public void onFirstLogin() 
					{
						m_loginDlg.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								go2SettingPowerSavingActivity();								
							}
						});
					}					
				});				
				
			} else if (status == UserLoginStatus.login) {
				go2SettingPowerSavingActivity();
			} else {
				PromptUserLogined();
			}
		}	
	}

	private void go2SettingPowerSavingActivity()
	{
		go2SettingView();
		Intent intent = new Intent(this, SettingPowerSavingActivity.class);
		this.startActivity(intent);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		OnResponseAppWidget();
		
	}
	
	private void OnResponseAppWidget(){
		if (DataConnectManager.getInstance().getCPEWifiConnected() && CPEConfig.getInstance().getInitialLaunchedFlag() && CPEConfig.getInstance().getQuickSetupFlag()) {
		    Intent it=getIntent();
		    int nPage = it.getIntExtra("com.alcatel.smartlinkv3.business.openPage", 100);
		    if (nPage == SMS_PAGE) {
			smsBtnClick();
		    }else if (nPage == BATTERY_PAGE) {
			widgetBatteryBtnClick();
		    }else if (nPage == HOME_PAGE) {
			homeBtnClick();
		    }else if(nPage == USAGE_PAGE){
		   	wifiKeyBtnClick();
		    }
		}else {
		    Intent itent = new Intent(this, LoadingActivity.class);
		    startActivity(itent);
		    this.finish();
		}
	}
	
	@Override
	public void onDeviceChange(boolean isSelDeviceChange) {
		updateDeviceList();
	}
	
	private String getServerAddress(Context ctx){  
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo(); 
        return Formatter.formatIpAddress(dhcpInfo.gateway);  
    } 
	
	private void updateDeviceList(){
		List<Device> list = mAllShareProxy.getDMSDeviceList();
		String str1;
		String str2;
		
		
		for(Device tmp : list)
		{
			str1 = tmp.getLocation().substring(7);
			str2 = str1.substring(0,str1.indexOf(":"));
			if(str2.equalsIgnoreCase(getServerAddress(this)))
			{
				mDevice = tmp;
			}
		}
		
		mAllShareProxy.setDMSSelectedDevice(mDevice);
		
		Intent msdIntent= new Intent(ViewMicroSD.DLNA_DEVICES_SUCCESS);
		sendBroadcast(msdIntent);
		
	}
	
	public void showMicroView()
	{
		if(BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900"))
		{
            //暂时屏蔽SD卡的选项
//			m_microsdBtn.setVisibility(View.VISIBLE);
			return;
		}
		
		if(BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y858"))
		{
			m_microsdBtn.setVisibility(View.GONE);
			return;
		}
		
		boolean bSupport = FeatureVersionManager.getInstance().isSupportModule("Sharing");
		if(bSupport) {
			if((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus"))
					|| (FeatureVersionManager.getInstance().isSupportApi("Sharing",
							"GetDLNASettings")))
			{
                //暂时屏蔽SD卡的选项
//				m_microsdBtn.setVisibility(View.VISIBLE);
			}
		}else{
			m_microsdBtn.setVisibility(View.GONE);
		}
	}

}
