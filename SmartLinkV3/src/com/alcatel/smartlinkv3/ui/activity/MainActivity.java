package com.alcatel.smartlinkv3.ui.activity;


import java.util.List;

import org.cybergarage.upnp.Device;

import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog.OnPINError;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog.OnPUKError;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;
import com.alcatel.smartlinkv3.ui.dialog.AddPopWindow;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;
import com.alcatel.smartlinkv3.ui.view.ViewHome;
import com.alcatel.smartlinkv3.ui.view.ViewIndex;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;
import com.alcatel.smartlinkv3.ui.view.ViewSetting;
import com.alcatel.smartlinkv3.ui.view.ViewSms;
import com.alcatel.smartlinkv3.ui.view.ViewUsage;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;

public class MainActivity extends BaseActivity implements OnClickListener,
															IDeviceChangeListener{
	private final int HOME_PAGE = 1;
	private final int SMS_PAGE = 2;
	private final int BATTERY_PAGE = 3;
	private int m_preButton = 0;
	private int m_nNewCount = 0;

	private RelativeLayout rl_top;
	//private LinearLayout ll_buttom;
	
	private ViewFlipper m_viewFlipper;
	
	private TextView m_homeBtn;
	private TextView m_microsdBtn;
	private TextView m_usageBtn;
	private RelativeLayout m_smsBtn;
	private TextView m_settingBtn;
	
	private TextView m_smsTextView;
	private TextView m_newSmsTextView;


	private TextView m_titleTextView = null;
	private Button m_Btnbar = null;

	private ViewHome m_homeView = null;
	private ViewUsage m_usageView = null;
	private ViewSms m_smsView = null;
	
	private ViewSetting m_settingView = null;
	private ViewMicroSD m_microsdView = null;

	public static DisplayMetrics m_displayMetrics = new DisplayMetrics();
	
	private PinDialog m_dlgPin = null;
	private PukDialog m_dlgPuk = null;
	private ErrorDialog m_dlgError = null;
	private LoginDialog m_loginDlg = null;

	private Button m_unlockSimBtn = null;
	private int pageIndex = 0;
	private static boolean m_blLogout = false;
	private static boolean m_blHomeLogout = false;
	
	private CommonErrorInfoDialog m_dialog_timeout_info;
	
	private RelativeLayout m_accessDeviceLayout;
	public static String PAGE_TO_VIEW_HOME = "com.alcatel.smartlinkv3.toPageViewHome";
	
	private DMSDeviceBrocastFactory mBrocastFactory;
	private AllShareProxy mAllShareProxy;
	private Device mDevice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.getWindowManager().getDefaultDisplay()
				.getMetrics(m_displayMetrics);
		rl_top = (RelativeLayout)findViewById(R.id.main_layout_top);
		//ll_buttom = (LinearLayout)findViewById(R.id.layout_bottom);
        
        RelativeLayout.LayoutParams rl_params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
       //RelativeLayout.LayoutParams ll_params = (RelativeLayout.LayoutParams) ll_buttom.getLayoutParams();// new LinearLayout.LayoutParams(
                //LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rl_params.height = (m_displayMetrics.heightPixels * 9)/100;
        //ll_params.height = m_displayMetrics.heightPixels / 10;
        rl_top.setLayoutParams(rl_params);
        //ll_buttom.setLayoutParams(ll_params);

		m_homeBtn = (TextView) this.findViewById(R.id.main_home);
		m_homeBtn.setOnClickListener(this);
		m_microsdBtn = (TextView) this.findViewById(R.id.main_microsd);
		m_microsdBtn.setOnClickListener(this);
		m_usageBtn = (TextView) this.findViewById(R.id.main_usage);
		m_usageBtn.setOnClickListener(this);
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
		m_unlockSimBtn = (Button) m_homeView.getView().findViewById(
				R.id.unlock_sim_button);
		m_unlockSimBtn.setOnClickListener(this);
		
		m_accessDeviceLayout = (RelativeLayout)m_homeView.getView().findViewById(R.id.access_num_layout);
		m_accessDeviceLayout.setOnClickListener(this);
		OnResponseAppWidget();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
		mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	mBrocastFactory.registerListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PIN_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.SIM_UNLOCK_PUK_REQUEST));
		this.registerReceiver(m_msgReceiver, new IntentFilter(
				MessageUti.USER_LOGOUT_REQUEST));
		
		
		this.registerReceiver(m_msgReceiver2, new IntentFilter(
				PAGE_TO_VIEW_HOME));
		

		m_homeView.onResume();
		m_usageView.onResume();
		m_smsView.onResume();
		m_settingView.onResume();
		m_microsdView.onResume();
		
		updateBtnState();
		toPageHomeWhenPinSimNoOk();
		
		if (m_blHomeLogout) {
			DataValue data = new DataValue();
			data.addParam("user_name", SmartLinkV3App.getInstance().getLoginUsername());
			data.addParam("password", SmartLinkV3App.getInstance().getLoginPassword());
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.USER_LOGIN_REQUEST, data);		
			m_blHomeLogout = false;
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			this.unregisterReceiver(m_msgReceiver);
		} catch (Exception e) {

		}
		m_homeView.onPause();
		m_usageView.onPause();
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
		m_usageView.onDestroy();
		m_smsView.onDestroy();
		m_settingView.onDestroy();
		m_microsdView.onDestroy();
		
		mBrocastFactory.unRegisterListener();
	}
	
	private void destroyDialogs(){
		m_dlgPin.destroyDialog();
		m_dlgPuk.destroyDialog();
		m_dlgError.destroyDialog();
		m_loginDlg.destroyDialog();
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
				m_blLogout = false;
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(PAGE_TO_VIEW_HOME)) {
			homeBtnClick();
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
		case R.id.main_usage:
			usageBtnClick();
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
		case R.id.btnbar:
			if (LoginDialog.isLoginSwitchOff()) {		
				go2Click();	
			} else {		
				UserLoginStatus status = BusinessMannager.getInstance()				
						.getLoginStatus();	
				if (status == UserLoginStatus.LoginTimeOut) {			
					PromptUserLogined();		
				} else if (status == UserLoginStatus.login) {			
					go2Click();		
				} else {			
					m_loginDlg.showDialog(new OnLoginFinishedListener() {				
						@Override				
						public void onLoginFinished() {					
							go2Click();				
						}			
					});		
				}	
			}
					
			break;
		case R.id.unlock_sim_button:
			unlockSimBtnClick(true);
			break;	
		case R.id.access_num_layout:
			accessDeviceLayoutClick();
			break;	
		}
	}
	
	private void go2Click()
	{
		if(this.pageIndex == ViewIndex.VIEW_HOME)
		{
			addBtnClick();
		}else if(this.pageIndex == ViewIndex.VIEW_USAGE){
			moreBtnClick();
		}else if(this.pageIndex == ViewIndex.VIEW_SMS){
			editBtnClick();
		}
	}
	
	private void accessDeviceLayoutClick() {
		if (LoginDialog.isLoginSwitchOff()) {		
			startDeviceManagerActivity();	
		} else {		
			UserLoginStatus status = BusinessMannager.getInstance()				
					.getLoginStatus();	
			if (status == UserLoginStatus.LoginTimeOut) {			
				PromptUserLogined();		
			} else if (status == UserLoginStatus.login) {			
				startDeviceManagerActivity();		
			} else {			
				m_loginDlg.showDialog(new OnLoginFinishedListener() {				
					@Override				
					public void onLoginFinished() {					
						startDeviceManagerActivity();				
					}			
				});		
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

	private void usageBtnClick() {
		if (m_preButton == R.id.main_usage) {
			return;
		}
		
		if (LoginDialog.isLoginSwitchOff()) {
			go2UsageView();
		} else {
			UserLoginStatus status = BusinessMannager.getInstance()
					.getLoginStatus();

			if (status == UserLoginStatus.LoginTimeOut) {
				PromptUserLogined();
			} else if (status == UserLoginStatus.login) {
				go2UsageView();
			} else {
				m_loginDlg.showDialog(new OnLoginFinishedListener() {
					@Override
					public void onLoginFinished() {
						go2UsageView();
					}
				});
			}
		}
	}
	
	private void go2UsageView() {
		SimStatusModel simStatus = BusinessMannager.getInstance()
				.getSimStatus();
		if(simStatus.m_SIMState == SIMState.Accessable) {
			setMainBtnStatus(R.id.main_usage);
			showView(ViewIndex.VIEW_USAGE);
			updateTitleUI(ViewIndex.VIEW_USAGE);
			pageIndex = ViewIndex.VIEW_USAGE;
		}
	}
	
	private void smsBtnClick() {
		if (m_preButton == R.id.tab_sms_layout) {
			return;
		}		
	
		if (LoginDialog.isLoginSwitchOff()) {
			go2SmsView();
		} else {
			UserLoginStatus status = BusinessMannager.getInstance()
					.getLoginStatus();

			if (status == UserLoginStatus.LoginTimeOut) {
				PromptUserLogined();
			} else if (status == UserLoginStatus.login) {
				go2SmsView();
			} else {
				m_loginDlg.showDialog(new OnLoginFinishedListener() {
					@Override
					public void onLoginFinished() {
						go2SmsView();
					}
				});
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
			UserLoginStatus status = BusinessMannager.getInstance()				
					.getLoginStatus();	
			if (status == UserLoginStatus.LoginTimeOut) {			
				PromptUserLogined();		
			} else if (status == UserLoginStatus.login) {			
				go2SettingView();		
			} else {			
				m_loginDlg.showDialog(new OnLoginFinishedListener() {				
					@Override				
					public void onLoginFinished() {					
						go2SettingView();				
					}			
				});		
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
		} else {
			UserLoginStatus status = BusinessMannager.getInstance()
					.getLoginStatus();

			if (status == UserLoginStatus.LoginTimeOut) {
				PromptUserLogined();
			} else if (status == UserLoginStatus.login) {
				go2MicroSDView();
			} else {
				m_loginDlg.showDialog(new OnLoginFinishedListener() {
					@Override
					public void onLoginFinished() {
						go2MicroSDView();
					}
				});
			}
		}
	}
	
	private void go2MicroSDView(){
		mAllShareProxy.startSearch();
		
		setMainBtnStatus(R.id.main_microsd);
		showView(ViewIndex.VIEW_MICROSD);
		updateTitleUI(ViewIndex.VIEW_MICROSD);
		pageIndex = ViewIndex.VIEW_MICROSD;
	}

	private void addView() {
		m_homeView = new ViewHome(this);	
		m_viewFlipper.addView(m_homeView.getView(), ViewIndex.VIEW_HOME,
				m_viewFlipper.getLayoutParams());

		m_usageView = new ViewUsage(this);
		m_viewFlipper.addView(m_usageView.getView(),
				ViewIndex.VIEW_USAGE, m_viewFlipper.getLayoutParams());
		
		m_smsView = new ViewSms(this);
		m_viewFlipper.addView(m_smsView.getView(), ViewIndex.VIEW_SMS,
				m_viewFlipper.getLayoutParams());

		m_settingView = new ViewSetting(this);
		m_viewFlipper.addView(m_settingView.getView(),
				ViewIndex.VIEW_SETTINGE, m_viewFlipper.getLayoutParams());
		
		m_microsdView = new ViewMicroSD(this);
		m_viewFlipper.addView(m_microsdView.getView(),
				ViewIndex.VIEW_MICROSD, m_viewFlipper.getLayoutParams());
		
	}

	public void showView(int viewIndex) {
		m_viewFlipper.setDisplayedChild(viewIndex);
	}

	public void updateTitleUI(int viewIndex) {
		if (viewIndex == ViewIndex.VIEW_HOME) {
			m_titleTextView.setText(R.string.main_home);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_plus_icon);
			setMainBtnStatus(R.id.main_home);
		}
		if (viewIndex == ViewIndex.VIEW_USAGE) {
			m_titleTextView.setText(R.string.main_usage);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_more_icon);
			setMainBtnStatus(R.id.main_usage);
		}
		if (viewIndex == ViewIndex.VIEW_SMS) {
			m_titleTextView.setText(R.string.sms_title);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_edit_icon);
			setMainBtnStatus(R.id.tab_sms_layout);
		}
		if (viewIndex == ViewIndex.VIEW_SETTINGE) {
			m_titleTextView.setText(R.string.main_setting);
			m_Btnbar.setVisibility(View.GONE);
			setMainBtnStatus(R.id.main_setting);
		}
		if(viewIndex == ViewIndex.VIEW_MICROSD) {
			m_titleTextView.setText(R.string.main_microsd);
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


		nDrawable = nActiveBtnId == R.id.main_usage ? R.drawable.main_usage_active
				: R.drawable.main_usage_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_usageBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_usage ? R.color.color_blue
				: R.color.color_grey;
		m_usageBtn.setTextColor(this.getResources().getColor(nTextColor));

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

	
	private void addBtnClick(){
		AddPopWindow addPopWindow = new AddPopWindow(MainActivity.this);
		addPopWindow.showPopupWindow(m_Btnbar);
	}
	
	public static void setLogoutFlag(boolean blLogout){
		m_blLogout = blLogout;
	}
	private void moreBtnClick(){
		MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
		morePopWindow.showPopupWindow(m_Btnbar);
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
			go2SettingView();
			Intent intent = new Intent(this, SettingPowerSavingActivity.class);
			this.startActivity(intent);
		} else {		
			UserLoginStatus status = BusinessMannager.getInstance()				
					.getLoginStatus();	
			if (status == UserLoginStatus.LoginTimeOut) {			
				PromptUserLogined();		
			} else if (status == UserLoginStatus.login) {			
				go2SettingView();
				Intent intent = new Intent(this, SettingPowerSavingActivity.class);
				this.startActivity(intent);
			} else {			
				m_loginDlg.showDialog(new OnLoginFinishedListener() {				
					@Override				
					public void onLoginFinished() {					
						go2SettingView();
						Intent intent = new Intent(MainActivity.this, SettingPowerSavingActivity.class);
						MainActivity.this.startActivity(intent);
					}			
				});		
			}	
		}	
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		OnResponseAppWidget();
		
	}
	
	private void OnResponseAppWidget(){
		boolean blCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blCPEWifiConnected) {
			Intent it=getIntent();
			int nPage = it.getIntExtra("com.alcatel.smartlinkv3.business.openPage", 100);
			if (nPage == SMS_PAGE) {
				smsBtnClick();
			}else if (nPage == BATTERY_PAGE) {
				widgetBatteryBtnClick();
			}else if (nPage == HOME_PAGE) {
				homeBtnClick();
			}
		}else {
			Intent itent = new Intent(this, RefreshWifiActivity.class);
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
		
		for(Device tmp : list)
		{
			if(tmp.getLocation().substring(7, 18).equalsIgnoreCase(getServerAddress(this)))
			{
				mDevice = tmp;
			}
		}
		mAllShareProxy.setDMSSelectedDevice(mDevice);
		
		Intent msdIntent= new Intent(ViewMicroSD.DLNA_DEVICES_SUCCESS);
		sendBroadcast(msdIntent);
	}
	
	public static void setHomeLogoutFlag(boolean blhomeLogout){
		m_blHomeLogout = blhomeLogout;
	}
}
