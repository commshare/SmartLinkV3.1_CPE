package com.alcatel.wifilink.ui.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.DataConnectManager;
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.business.model.ConnectedDeviceItemModel;
import com.alcatel.wifilink.business.model.NetworkInfoModel;
import com.alcatel.wifilink.business.model.UsageSettingModel;
import com.alcatel.wifilink.business.model.WanConnectStatusModel;
import com.alcatel.wifilink.business.power.BatteryInfo;
import com.alcatel.wifilink.business.statistics.UsageRecordResult;
import com.alcatel.wifilink.common.ENUM.ConnectionStatus;
import com.alcatel.wifilink.common.ENUM.NetworkType;
import com.alcatel.wifilink.common.ENUM.OVER_DISCONNECT_STATE;
import com.alcatel.wifilink.common.ENUM.SIMState;
import com.alcatel.wifilink.common.ENUM.SignalStrength;
import com.alcatel.wifilink.common.ENUM.UserLoginStatus;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.activity.UsageActivity;
import com.alcatel.wifilink.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.wifilink.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.wifilink.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.wifilink.ui.dialog.ErrorDialog;
import com.alcatel.wifilink.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.wifilink.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.wifilink.ui.dialog.LoginDialog;
import com.alcatel.wifilink.ui.dialog.LoginDialog.OnLoginFinishedListener;

import java.util.ArrayList;
import java.util.Locale;



public class ViewHome extends BaseViewImpl implements OnClickListener {

	private static final String  BATTERY_LEVEL = "Battery Level";

	/*frame_connect*/
	private FrameLayout m_connectLayout = null;
	private TextView m_connectToNetworkTextView;
	private Button m_connectBtn = null;
	
	private LinearLayout m_simcardlockedLayout = null;
	private TextView m_simcardlockedTextView;
	private Button m_unlockSimBtn = null;
	
	private LinearLayout m_nosimcardLayout = null;
	private TextView m_simOrServiceTextView = null;

	private boolean m_bConnectPressd = false;
	private boolean m_bConnectReturn = false;
	private long statictime = 0;
	private int staticdata = 0;
	
	/*sigel_panel*/
	private TextView m_networkTypeTextView;
	private ImageView m_signalImageView;
	private TextView m_networkLabelTextView;

	/*access_panel*/
	private TextView m_accessnumTextView;
	private TextView m_accessstatusTextView;
	private ImageView m_accessImageView;

	
	private LoginDialog m_loginDialog = null;
	private AutoLoginProgressDialog	m_autoLoginDialog = null;
	private AutoForceLoginProgressDialog m_ForceloginDlg = null;

	private String strZeroConnDuration = null;

	private Typeface typeFace = Typeface.createFromAsset(this.m_context.getAssets(),"fonts/Roboto_Light.ttf");
	
	
	private ViewConnectBroadcastReceiver m_viewConnetMsgReceiver;
	private ImageView batteryView;
	private CircleProgress circleProgress;
	private WaveLoadingView mConnectedView;
	private FrameLayout m_connectedLayout;


	private class ViewConnectBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();

			if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
				resetConnectBtnFlag();
				m_connectLayout.setVisibility(View.VISIBLE);
				m_connectedLayout.setVisibility(View.GONE);
				showConnectBtnView();
	    	} else if(intent.getAction().equals(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET)) {
				if(ok) {
					showSignalAndNetworkType();
					showNetworkState();
					showConnectBtnView();
				}
	    	} else if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
				if(ok) {
					showSignalAndNetworkType();
					resetConnectBtnFlag();
					showNetworkState();
					showConnectBtnView();
				}			
	    	} else if(intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)) {
				if(ok) {
					resetConnectBtnFlag();
					showNetworkState();
					showConnectBtnView();
				}
	    	}else if (intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)) {
				if(ok) {
					m_bConnectReturn = true;
				}else{
					//operation fail
					m_bConnectPressd = false;
					showNetworkState();
					showConnectBtnView();
				}
			} else if (intent.getAction().equals(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST)) {
				if(ok) {
					showAccessDeviceState();
				}
			} else if (intent.getAction().equals(MessageUti.POWER_GET_BATTERY_STATE)) {
				if(ok) {
					showBatteryState();
				}
			}
		}	
	}
	
	public ViewHome(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_home, null);

		circleProgress = ((CircleProgress) m_view.findViewById(R.id.home_circleProgress));
		batteryView = ((ImageView) m_view.findViewById(R.id.home_battery_image));
		mConnectedView = ((WaveLoadingView) m_view.findViewById(R.id.connected_button));
		mConnectedView.setOnClickListener(this);

		m_connectLayout = (FrameLayout) m_view.findViewById(R.id.connect_layout);
		m_connectedLayout = ((FrameLayout) m_view.findViewById(R.id.connected_layout));
		m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
		m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
		m_connectBtn.setOnClickListener(this);

		m_simcardlockedLayout = (LinearLayout) m_view.findViewById(R.id.sim_card_locked_layout);
		m_simcardlockedTextView = (TextView) m_view.findViewById(R.id.sim_card_locked_state);
		m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);
		
		m_nosimcardLayout = (LinearLayout) m_view.findViewById(R.id.no_sim_card_layout);
		m_simOrServiceTextView = (TextView) m_view.findViewById(R.id.no_sim_card_state);

		m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);
		m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);
		m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);

		m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
		m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
		m_accessstatusTextView= (TextView) m_view.findViewById(R.id.access_label);
		
		m_loginDialog = new LoginDialog(this.m_context);
		m_autoLoginDialog = new AutoLoginProgressDialog(this.m_context);
		m_ForceloginDlg = new AutoForceLoginProgressDialog(this.m_context);

		strZeroConnDuration = getView().getResources().getString(R.string.Home_zero_data);
	}

	@Override
	public void onResume() {
		m_viewConnetMsgReceiver = new ViewConnectBroadcastReceiver();
		
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_CONNECT_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_DISCONNECT_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.POWER_GET_BATTERY_STATE));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST));

		showConnectBtnView();
		showNetworkState();
		showSignalAndNetworkType();
		
		showBatteryState();
		showTrafficUsageView();
		showAccessDeviceState();
	}

	@Override
	public void onPause() {
		try {
			m_context.unregisterReceiver(m_viewConnetMsgReceiver);
		} catch (Exception e) {

		}
		m_bConnectReturn = false;
	}

	@Override
	public void onDestroy() {
		m_loginDialog.destroyDialog();
		m_autoLoginDialog.destroyDialog();
		m_ForceloginDlg.destroyDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.connect_button:
				connectBtnClick();
				break;
			case R.id.connected_button:
				connectedBtnClick();
				break;
		default:
			break;
		}
	}

	private void resetConnectBtnFlag() {
		SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (simStatus != SIMState.Accessable || !bCPEWifiConnected) {
			m_bConnectPressd = false;
			m_bConnectReturn = false;
			return;
		}

		WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
		if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
			if (m_bConnectReturn) {
				m_bConnectReturn = false;
			}
		}
	
		
	}
	
	
	private void showNetworkState() {
		
		SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
			if (simStatus != SIMState.Accessable) {
			int nStatusId = R.string.Home_sim_invalid;
			if (SIMState.InvalidSim == simStatus) {
				nStatusId = R.string.Home_sim_invalid;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.NoSim == simStatus) {
				nStatusId = R.string.Home_no_sim;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.SimCardDetected == simStatus) {
				nStatusId = R.string.Home_SimCard_Detected;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.SimLockRequired == simStatus) {
				nStatusId = R.string.Home_SimLock_Required;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.PukTimesUsedOut == simStatus) {
				nStatusId = R.string.Home_PukTimes_UsedOut;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.SimCardIsIniting == simStatus) {
				nStatusId = R.string.home_initializing;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.PinRequired == simStatus) {
				nStatusId = R.string.IDS_PIN_LOCKED;
				m_unlockSimBtn.setVisibility(View.VISIBLE);
			} else if (SIMState.PukRequired == simStatus) {
				nStatusId = R.string.Home_puk_locked;
				m_unlockSimBtn.setVisibility(View.VISIBLE);
			} else {
				nStatusId = R.string.Home_sim_invalid;
				m_unlockSimBtn.setVisibility(View.GONE);
			}
			
			if(SIMState.PinRequired == simStatus||SIMState.PukRequired == simStatus)
			{
				m_nosimcardLayout.setVisibility(View.GONE);
				m_simcardlockedLayout.setVisibility(View.VISIBLE);
				m_simcardlockedTextView.setText(nStatusId);
			}else {
				m_simOrServiceTextView.setText(nStatusId);
				m_simcardlockedLayout.setVisibility(View.GONE);
				m_nosimcardLayout.setVisibility(View.VISIBLE);
			}
			mConnectedView.setCenterTitle(strZeroConnDuration);
			m_connectLayout.setVisibility(View.GONE);

			return;
		}

		m_simcardlockedLayout.setVisibility(View.GONE);
		
		NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service) {
			m_simOrServiceTextView.setText(R.string.home_no_service);
			m_nosimcardLayout.setVisibility(View.VISIBLE);
			m_connectLayout.setVisibility(View.GONE);
			mConnectedView.setCenterTitle(strZeroConnDuration);
			return;
		}
		
		if(curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			m_simOrServiceTextView.setText(R.string.home_initializing);
			m_nosimcardLayout.setVisibility(View.VISIBLE);
			m_connectLayout.setVisibility(View.GONE);
			mConnectedView.setCenterTitle(strZeroConnDuration);
			return;
		}

		m_nosimcardLayout.setVisibility(View.GONE);
		m_connectToNetworkTextView.setText(curNetwork.m_strNetworkName);
		m_connectToNetworkTextView.setVisibility(View.VISIBLE);
		WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
		if (!m_bConnectPressd ) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				statictime = internetConnState.m_lConnectionTime;
				staticdata = (int) (internetConnState.m_lDlBytes + internetConnState.m_lUlBytes);

			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
				statictime = internetConnState.m_lConnectionTime;
				staticdata = (int) (internetConnState.m_lDlBytes + internetConnState.m_lUlBytes);
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connecting) {
			}
		} else {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
					|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			} else {
			}
		}

		int staticDataMB = staticdata / 1024 / 1024;
		mConnectedView.setCenterTitle(String.valueOf(staticDataMB));
	
	}
	
	private void showConnectBtnView() {

		SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			mConnectedView.setCenterTitle(strZeroConnDuration);
			return;
		}
		
		NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service || curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			mConnectedView.setCenterTitle(strZeroConnDuration);
			return;
		}
		WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
		if (!m_bConnectPressd) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				boolean logoutFlag = SP.getInstance(m_context).getBoolean(SettingAccountActivity.LOGOUT_FLAG, true);
				if (logoutFlag) {
					m_connectLayout.setVisibility(View.VISIBLE);
					m_connectedLayout.setVisibility(View.GONE);
				} else {
					m_connectLayout.setVisibility(View.GONE);
					m_connectedLayout.setVisibility(View.VISIBLE);
				}
				statictime = internetConnState.m_lConnectionTime;
				staticdata = (int) (internetConnState.m_lDlBytes + internetConnState.m_lUlBytes);
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				m_connectLayout.setVisibility(View.VISIBLE);
				m_connectedLayout.setVisibility(View.GONE);
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
				m_connectLayout.setVisibility(View.VISIBLE);
				m_connectedLayout.setVisibility(View.GONE);
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connecting) {
				m_connectLayout.setVisibility(View.GONE);
				m_connectedLayout.setVisibility(View.VISIBLE);
			}
		} else {
			boolean logoutFlag = SP.getInstance(m_context).getBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
			if (logoutFlag) {
				m_connectLayout.setVisibility(View.VISIBLE);
				m_connectedLayout.setVisibility(View.GONE);
			} else {
				m_connectLayout.setVisibility(View.GONE);
				m_connectedLayout.setVisibility(View.VISIBLE);
			}
			statictime = internetConnState.m_lConnectionTime;
			staticdata = (int) (internetConnState.m_lDlBytes + internetConnState.m_lUlBytes);
		}

		int staticDataMB = staticdata / 1024 / 1024;
		mConnectedView.setCenterTitle(String.valueOf(staticDataMB));
	
	}
	
	private void connectBtnClick()
	{
		if (LinkAppSettings.isLoginSwitchOff())
		{
			connect();
		}
		else
		{
			UserLoginStatus status = BusinessManager.getInstance().getLoginStatus();

			if (status == UserLoginStatus.Logout)
			{
				m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener()
				{
					public void onLoginSuccess()
					{
						connect();
					}

					public void onLoginFailed(String error_code)
					{
						if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED))
						{
							if(!FeatureVersionManager.getInstance().isSupportForceLogin())
							{
								m_loginDialog.showOtherLogin();
							} else {
								ForceLoginSelectDialog.getInstance(m_context).showDialog(() ->{
												m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
													public void onLoginSuccess()
													{
														connect();
													}

													public void onLoginFailed(String error_code)
													{
														if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
														{
															SmartLinkV3App.getInstance().setForcesLogin(true);
															ErrorDialog.getInstance(m_context).showDialog(m_context.getString(R.string.login_psd_error_msg),
																	new OnClickBtnRetry()
																	{
																		@Override
																		public void onRetry()
																		{
																			m_loginDialog.showDialog(new OnLoginFinishedListener() {
																				@Override
																				public void onLoginFinished() {
																					connect();
																				}
																			});
																		}
																	});
														}else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
														{
															m_loginDialog.showTimeout();
														}
													}
												});
											}
										);
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDialog.showTimeout();
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD))
						{
							ErrorDialog.getInstance(m_context).showDialog(m_context.getString(R.string.login_psd_error_msg),
									new OnClickBtnRetry()
									{
										@Override
										public void onRetry()
										{
											m_loginDialog.showDialog();
										}
									});
						}
					}

					@Override
					public void onFirstLogin()
					{
						m_loginDialog.showDialog(new OnLoginFinishedListener() {
							@Override
							public void onLoginFinished() {
								connect();
							}
						});
					}
				});
			}
			else if (status == UserLoginStatus.LOGIN)
			{
				connect();
			}
			else
			{
				PromptUserLogined();
			}
		}
	}

	private void connectedBtnClick() {
		m_context.startActivity(new Intent(m_context, UsageActivity.class));
	}

	private void connect()
	{
		SP.getInstance(m_context).putBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
		UsageSettingModel settings = BusinessManager.getInstance().getUsageSettings();
		UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
		WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
		if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			if (settings.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Enable && m_UsageRecordResult.MonthlyPlan > 0) {
				if ((m_UsageRecordResult.HUseData + m_UsageRecordResult.RoamUseData) >= m_UsageRecordResult.MonthlyPlan) {
					String msgRes = m_context.getString(R.string.home_usage_over_redial_message);
					Toast.makeText(m_context, msgRes, Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
	
		if (!m_bConnectPressd) {
			m_bConnectPressd = true;
		} else {
			return;
		}
		m_bConnectReturn = false;
		showNetworkState();
		showConnectBtnView();
		
		if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.WAN_DISCONNECT_REQUSET,null);
		} else {
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.WAN_CONNECT_REQUSET,null);
		}	
	}
	private CommonErrorInfoDialog m_dialog_timeout_info;
	
	private void PromptUserLogined() {
		if (null == m_dialog_timeout_info) {
			m_dialog_timeout_info = CommonErrorInfoDialog.getInstance(m_context);
		}
		m_dialog_timeout_info.showDialog(
				m_context.getString(R.string.other_login_warning_title),
				m_context.getResources().getString(
						R.string.login_login_time_used_out_msg));
	}
	
	private void showSignalAndNetworkType() {
		SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_networkTypeTextView.setVisibility(View.GONE);
			m_networkLabelTextView.setVisibility(View.VISIBLE);
			m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
		}else{
			NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
			if(curNetwork.m_NetworkType == NetworkType.No_service) {
				m_networkTypeTextView.setVisibility(View.GONE);
				m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
				m_networkLabelTextView.setVisibility(View.VISIBLE);
				return;
			}
			//show roaming
			if(curNetwork.m_bRoaming)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g_r);
			//show signal strength
			if(curNetwork.m_signalStrength == SignalStrength.Level_0)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
			if (curNetwork.m_signalStrength == SignalStrength.Level_1)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g1);
			if (curNetwork.m_signalStrength == SignalStrength.Level_2)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g2);
			if (curNetwork.m_signalStrength == SignalStrength.Level_3)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g3);
			if (curNetwork.m_signalStrength == SignalStrength.Level_4)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g4);
			if (curNetwork.m_signalStrength == SignalStrength.Level_5)
				m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
			//show network type
			if (curNetwork.m_NetworkType == NetworkType.UNKNOWN)
			{
				m_networkTypeTextView.setVisibility(View.GONE);
				m_networkLabelTextView.setVisibility(View.VISIBLE);
			}
			
			//2G
			if (curNetwork.m_NetworkType == NetworkType.EDGE
					|| curNetwork.m_NetworkType == NetworkType.GPRS) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_2g);
				m_networkTypeTextView.setTextColor(m_context.getResources().getColor(R.color.mg_blue));
			}
			
			//3G
			if (curNetwork.m_NetworkType == NetworkType.HSPA					
					|| curNetwork.m_NetworkType == NetworkType.UMTS					
					|| curNetwork.m_NetworkType == NetworkType.HSUPA) {

				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_3g);
				m_networkTypeTextView.setTextColor(m_context.getResources().getColor(R.color.mg_blue));
			}
			
			//3G+
			if (curNetwork.m_NetworkType == NetworkType.HSPA_PLUS					
					|| curNetwork.m_NetworkType == NetworkType.DC_HSPA_PLUS) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_3g_plus);
				m_networkTypeTextView.setTextColor(m_context.getResources().getColor(R.color.mg_blue));
			}
			
			//4G			
			if (curNetwork.m_NetworkType == NetworkType.LTE) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_4g);
				m_networkTypeTextView.setTextColor(m_context.getResources().getColor(R.color.mg_blue));
			}
		}
	}
		
	private void showBatteryState(){

		if(!FeatureVersionManager.getInstance().isSupportApi("PowerManagement", "GetBatteryState"))
		{
			batteryView.setVisibility(View.GONE);
			return;
		}
		int nProgress;
		BatteryInfo batteryinfo = BusinessManager.getInstance().getBatteryInfo();
		if(ConstValue.CHARGE_STATE_REMOVED == batteryinfo.getChargeState()){
			nProgress = batteryinfo.getBatterLevel();
			if (nProgress > 20 && nProgress <= 40){
				batteryView.setImageResource(R.drawable.home_ic_battery2);
			} else if ((nProgress > 40) && nProgress <= 60){
				batteryView.setImageResource(R.drawable.home_ic_battery3);
			} else if ((nProgress > 60) && nProgress <= 80){
				batteryView.setImageResource(R.drawable.home_ic_battery4);
			} else if ((nProgress > 80) && nProgress <= 100){
				batteryView.setImageResource(R.drawable.home_ic_battery5);
			} else {
				batteryView.setImageResource(R.drawable.home_ic_battery1);
			}
		}else if(ConstValue.CHARGE_STATE_CHARGING == batteryinfo.getChargeState()){
				batteryView.setImageResource(R.drawable.home_ic_battery_charging);
		}else if(ConstValue.CHARGE_STATE_COMPLETED == batteryinfo.getChargeState()){
			nProgress = batteryinfo.getBatterLevel();
			if (nProgress > 20 && nProgress <= 40){
				batteryView.setImageResource(R.drawable.home_ic_battery2);
			} else if ((nProgress > 40) && nProgress <= 60){
				batteryView.setImageResource(R.drawable.home_ic_battery3);
			} else if ((nProgress > 60) && nProgress <= 80){
				batteryView.setImageResource(R.drawable.home_ic_battery4);
			} else if ((nProgress > 80) && nProgress <= 100){
				batteryView.setImageResource(R.drawable.home_ic_battery5);
			} else {
				batteryView.setImageResource(R.drawable.home_ic_battery1);
			}
		}else if(ConstValue.CHARGE_STATE_ABORT == batteryinfo.getChargeState()){

		}

		int batterLevel = batteryinfo.getBatterLevel();

		if (batterLevel <= 30){
			circleProgress.setProgress(m_context.getResources().getColor(R.color.circle_yellow));
		}
		circleProgress.setValue(batterLevel);

		showBatteryOnProgressPosition(batterLevel);
		SP.getInstance(m_context).putInt(BATTERY_LEVEL, batterLevel);
	}

	private void showBatteryOnProgressPosition(int progressValue) {
		int startProgressValue = SP.getInstance(m_context).getInt(BATTERY_LEVEL, 0);
		float startRotateValue = (float) (3.6 * startProgressValue);
		float rotateValue = (float) (3.6 * progressValue);

		AnimationSet animationSet = new AnimationSet(true);
		RotateAnimation animation1 = new RotateAnimation(-startRotateValue, -rotateValue,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		//实例化RotateAnimation
		//以自身中心为圆心，旋转360度 正值为顺时针旋转，负值为逆时针旋转
		RotateAnimation animation2 = new RotateAnimation(startRotateValue, rotateValue,
				Animation.ABSOLUTE, 28,
				Animation.ABSOLUTE, -300);
		animationSet.addAnimation(animation1);
		animationSet.addAnimation(animation2);

		//设置动画插值器 被用来修饰动画效果,定义动画的变化率
		animationSet.setInterpolator(new LinearInterpolator());
		//设置动画执行时间
		animationSet.setDuration(1000);
		//动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		batteryView.startAnimation(animationSet);
	}

	private void showTrafficUsageView() {
		UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
		UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();

		if(statistic.HMonthlyPlan!=0){
			long hUseData = m_UsageRecordResult.HUseData;
			long hMonthlyPlan = statistic.HMonthlyPlan;
			long circleUseDataProgressValue = (hUseData * 100) / hMonthlyPlan;
			if (circleUseDataProgressValue >= 80) {
				mConnectedView.setWaveColor(m_context.getResources().getColor(R.color.wave_yellow));
			} else {
				mConnectedView.setWaveColor(m_context.getResources().getColor(R.color.circle_green));
			}
			if (circleUseDataProgressValue <= 100){
				mConnectedView.setProgressValue((int) circleUseDataProgressValue - 8);
			} else {
				mConnectedView.setProgressValue(92);
			}
		} else {
			mConnectedView.setWaveColor(m_context.getResources().getColor(R.color.circle_green));
			mConnectedView.setProgressValue(92);
		}
	}
	
	private void showAccessDeviceState(){
		ArrayList<ConnectedDeviceItemModel> connecedDeviceLstData = BusinessManager.getInstance().getConnectedDeviceList();
		m_accessnumTextView.setTypeface(typeFace);
		m_accessnumTextView.setText(String.format(Locale.ENGLISH, "%d", connecedDeviceLstData.size()));
		m_accessnumTextView.setTextColor(m_context.getResources().getColor(R.color.mg_blue));
		m_accessImageView.setImageResource(R.drawable.device_more);
		
		String strOfficial = this.m_context.getString(R.string.access_lable);
		m_accessstatusTextView.setText(strOfficial);
		
	}
}
