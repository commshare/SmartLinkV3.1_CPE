package com.alcatel.smartlinkv3.ui.view;


import java.util.ArrayList;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog.OnClickBottonConfirm;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.common.ENUM.SignalStrength;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.common.ENUM.NetworkType;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.OVER_DISCONNECT_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ViewHome extends BaseViewImpl implements OnClickListener {
	private static final String TAG = ViewHome.class.getSimpleName();
	
	/*frame_connect  start*/
	
	
	private ProgressBar m_connectWaiting = null;
	
	private RelativeLayout m_connectLayout = null;
	private TextView m_connectToNetworkTextView;
	private TextView m_connectToLabel;
	private Button m_connectBtn = null;
	
	private LinearLayout m_simcardlockedLayout = null;
	private TextView m_simcardlockedTextView;
	private Button m_unlockSimBtn = null;
	
	private LinearLayout m_nosimcardLayout = null;
	private TextView m_simOrServiceTextView = null;
	
	private TextView m_timestatusTextView = null;
	private TextView m_datastatusTextView = null;
	
	private boolean m_bConnectPressd = false;
	private boolean m_bConnectReturn = false;
	long statictime = 0;
	long staticdata = 0;
	/*frame_connect  end*/
	
	/*sigel_panel  start*/
	private TextView m_networkTypeTextView;
	private ImageView m_networkRoamImageView;
	private ImageView m_signalImageView;
	private TextView m_networkLabelTextView;
	/*sigel_panel  end*/
	
	/*battery_panel  start*/
	private TextView m_batteryscaleTextView;
	private ProgressBar m_batteryProgress = null;
	private ImageView m_batterychargingImageView = null;
	private RelativeLayout m_batteryscalelayout; 
	private RelativeLayout m_batterydescriptionlayout;
	private TextView m_batterydescriptionTextView;
	/*battery_panel  end*/
	
	/*access_panel  start*/
	private TextView m_accessnumTextView;
	private TextView m_accessstatusTextView;
	private ImageView m_accessImageView;
	private RelativeLayout m_accessDeviceLayout;
	//private ArrayList<ConnectedDeviceItemModel> m_connecedDeviceLstData = new ArrayList<ConnectedDeviceItemModel>();
	/*access_panel  end*/
	
	private LoginDialog m_loginDialog = null;
	private AutoLoginProgressDialog	m_autoLoginDialog = null;
	private AutoForceLoginProgressDialog m_ForceloginDlg = null;
	
	private String home_connected_duration = null;
	private String home_connected_zero_duration = null;
	private String strZeroConnDuration = null;

	Typeface typeFace = Typeface.createFromAsset(this.m_context.getAssets(),"fonts/Roboto_Light.ttf");
	
	
	private ViewConnetBroadcastReceiver m_viewConnetMsgReceiver;

	private class ViewConnetBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
				resetConnectBtnFlag();
				showConnctBtnView();
	    	}
			
			if(intent.getAction().equals(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					showSignalAndNetworkType();
					showNetworkState();
					showConnctBtnView();
					//updateBtnState();
				}
	    	}
			
			if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK) {
					showSignalAndNetworkType();
					resetConnectBtnFlag();
					showNetworkState();
					showConnctBtnView();
					//showTotalDataUI();
					//updateStatisticsUI();
					//updateBtnState();
				}			
	    	}
			
			if(intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					resetConnectBtnFlag();
					showNetworkState();
					showConnctBtnView();
				}
	    	}
			
			if (intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					m_bConnectReturn = true;
				}else{
					//operation fail
					m_bConnectPressd = false;
					showNetworkState();
					showConnctBtnView();
				}
			}
			
			if (intent.getAction().equals(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
					showAccessDeviceState();
				}
			}
			
			if (intent.getAction().equals(MessageUti.POWER_GET_BATTERY_STATE)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
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
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_home,null);
		
		m_connectLayout = (RelativeLayout) m_view.findViewById(R.id.connect_layout);
		m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
		m_connectToLabel = (TextView) m_view.findViewById(R.id.connect_label);
		m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
		m_connectBtn.setOnClickListener(this);
		
		m_simcardlockedLayout = (LinearLayout) m_view.findViewById(R.id.sim_card_locked_layout);
		m_simcardlockedTextView = (TextView) m_view.findViewById(R.id.sim_card_locked_state);
		m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);
		
		m_nosimcardLayout = (LinearLayout) m_view.findViewById(R.id.no_sim_card_layout);
		m_simOrServiceTextView = (TextView) m_view.findViewById(R.id.no_sim_card_state);
		
		m_connectWaiting = (ProgressBar) m_view.findViewById(R.id.waiting_progress);
		
		m_timestatusTextView = (TextView) m_view.findViewById(R.id.time_status);
		m_datastatusTextView = (TextView) m_view.findViewById(R.id.data_status);
		
		
		m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);
		m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);
		m_networkRoamImageView = (ImageView) m_view.findViewById(R.id.connect_roam);
		m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);
		
		m_batteryscaleTextView = (TextView) m_view.findViewById(R.id.battery_scale_label);
		m_batteryProgress = (ProgressBar) m_view.findViewById(R.id.battery_progress);
		m_batterychargingImageView = (ImageView) m_view.findViewById(R.id.connct_charging);
		m_batteryscalelayout= (RelativeLayout) m_view.findViewById(R.id.battery_scale_layout);
		m_batterydescriptionlayout= (RelativeLayout) m_view.findViewById(R.id.battery_description_layout);
		m_batterydescriptionTextView = (TextView) m_view.findViewById(R.id.battery_description_label);
		
		
		m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
		m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
		m_accessstatusTextView= (TextView) m_view.findViewById(R.id.access_label);
		
		m_loginDialog = new LoginDialog(this.m_context);
		m_autoLoginDialog = new AutoLoginProgressDialog(this.m_context);
		m_ForceloginDlg = new AutoForceLoginProgressDialog(this.m_context);
		
		home_connected_duration = this.getView().getResources().getString(R.string.home_connected_duration);
		home_connected_zero_duration = this.getView().getResources().getString(R.string.Home_zero_data);
		
		strZeroConnDuration = String.format(home_connected_zero_duration, "0.00");

	}

	@Override
	public void onResume() {
		m_viewConnetMsgReceiver = new ViewConnetBroadcastReceiver();
		
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_CONNECT_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.WAN_DISCONNECT_REQUSET));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.POWER_GET_BATTERY_STATE));
		m_context.registerReceiver(m_viewConnetMsgReceiver, new IntentFilter(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST));
		
		
		
		showConnctBtnView();
		showNetworkState();
		showSignalAndNetworkType();
		
		showBatteryState();
		showAccessDeviceState();
	}

	@Override
	public void onPause() {
		try {
			m_context.unregisterReceiver(m_viewConnetMsgReceiver);
		} catch (Exception e) {

		}
		
		m_bConnectPressd = false;
		m_bConnectReturn = false;
		showConnctBtnView();

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
		default:
			break;
		}
	}
	
	
	
	private void resetConnectBtnFlag() {
		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (simStatus != SIMState.Accessable
				|| bCPEWifiConnected == false) {
			m_bConnectPressd = false;
			m_bConnectReturn = false;
			return;
		}

		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
			if (m_bConnectReturn) {
				m_bConnectPressd = false;
				m_bConnectReturn = false;
			}
		}
	
		
	}
	
	
	private void showNetworkState() {
		
		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_connectWaiting.setVisibility(View.GONE);
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
				nStatusId = R.string.Home_SimCard_IsIniting;
				m_unlockSimBtn.setVisibility(View.GONE);
			} else if (SIMState.PinRequired == simStatus) {
				nStatusId = R.string.Home_pin_locked;
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
			m_timestatusTextView.setText(R.string.Home_zero_time);
			m_datastatusTextView.setText(strZeroConnDuration);
			m_connectLayout.setVisibility(View.GONE);
			return;
		}

		m_simcardlockedLayout.setVisibility(View.GONE);
		
		NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service) {
			m_connectWaiting.setVisibility(View.GONE);
			m_simOrServiceTextView.setText(R.string.home_no_service);
			m_nosimcardLayout.setVisibility(View.VISIBLE);
			m_connectLayout.setVisibility(View.GONE);
			m_timestatusTextView.setText(R.string.Home_zero_time);
			m_datastatusTextView.setText(strZeroConnDuration);
			return;
		}
		
		if(curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			m_connectWaiting.setVisibility(View.GONE);
			m_simOrServiceTextView.setText(R.string.home_initializing);
			m_nosimcardLayout.setVisibility(View.VISIBLE);
			m_connectLayout.setVisibility(View.GONE);
			m_timestatusTextView.setText(R.string.Home_zero_time);
			m_datastatusTextView.setText(strZeroConnDuration);
			return;
		}

		m_nosimcardLayout.setVisibility(View.GONE);
		m_connectLayout.setVisibility(View.VISIBLE);
		m_connectToNetworkTextView.setText(curNetwork.m_strNetworkName);
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (m_bConnectPressd == false) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				m_connectToLabel.setText(R.string.home_connected_to);
				m_connectWaiting.setVisibility(View.GONE);
				statictime = (long)internetConnState.m_lConnectionTime;
				staticdata = (long)internetConnState.m_lDlBytes+internetConnState.m_lUlBytes;

			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				m_connectToLabel.setText(R.string.home_disconnecting_to);
				m_connectWaiting.setVisibility(View.VISIBLE);
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
				m_connectToLabel.setText(R.string.home_disconnected_to);
				m_connectWaiting.setVisibility(View.GONE);
				statictime = (long)internetConnState.m_lConnectionTime;
				staticdata = (long)internetConnState.m_lDlBytes+internetConnState.m_lUlBytes;
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connecting) {
				m_connectToLabel.setText(R.string.home_connecting_to);
				m_connectWaiting.setVisibility(View.VISIBLE);
			}
		} else {
			m_connectWaiting.setVisibility(View.VISIBLE);
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
					|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				m_connectToLabel.setText(R.string.home_disconnecting_to);
			} else {
				m_connectToLabel.setText(R.string.home_connecting_to);
			}
		}
		
		String strConnDuration = String.format(home_connected_duration, statictime/3600, (statictime%3600)/60);
		m_timestatusTextView.setText(strConnDuration);
		m_datastatusTextView.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, staticdata));
	
	}
	
	private void showConnctBtnView() {

		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_connectWaiting.setVisibility(View.GONE);
			m_connectBtn.setVisibility(View.INVISIBLE);
			m_timestatusTextView.setText(R.string.Home_zero_time);
			m_datastatusTextView.setText(strZeroConnDuration);
			return;
		}
		
		NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service || curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			m_connectWaiting.setVisibility(View.GONE);
			m_connectBtn.setVisibility(View.INVISIBLE);
			m_timestatusTextView.setText(R.string.Home_zero_time);
			m_datastatusTextView.setText(strZeroConnDuration);
			return;
		}
		m_connectBtn.setVisibility(View.VISIBLE);
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (m_bConnectPressd == false) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				m_connectBtn.setBackgroundResource(R.drawable.switch_on);
				m_connectBtn.setEnabled(true);
				m_connectWaiting.setVisibility(View.GONE);
				statictime = (long)internetConnState.m_lConnectionTime;
				staticdata = (long)internetConnState.m_lDlBytes+internetConnState.m_lUlBytes;
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				m_connectBtn.setBackgroundResource(R.drawable.switch_off);
				m_connectBtn.setEnabled(false);
				m_connectWaiting.setVisibility(View.VISIBLE);
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
				m_connectBtn.setBackgroundResource(R.drawable.switch_off);
				m_connectBtn.setEnabled(true);				
				m_connectWaiting.setVisibility(View.GONE);
			}
			
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connecting) {
				m_connectBtn.setBackgroundResource(R.drawable.switch_on);
				m_connectBtn.setEnabled(false);
				m_connectWaiting.setVisibility(View.VISIBLE);
			}
		} else {
			m_connectWaiting.setVisibility(View.VISIBLE);
			m_connectBtn.setEnabled(false);
			m_connectBtn.setVisibility(View.VISIBLE);
		}
		
		String strConnDuration = String.format(home_connected_duration, statictime/3600, (statictime%3600)/60);
		m_timestatusTextView.setText(strConnDuration);
		m_datastatusTextView.setText(CommonUtil.ConvertTrafficToStringFromMB(this.m_context, staticdata));
	
	}
	
	private void connectBtnClick() 
	{		
		if (LoginDialog.isLoginSwitchOff()) 
		{
			connect();	
		}
		else 
		{
			UserLoginStatus status = BusinessMannager.getInstance().getLoginStatus();

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
							if(FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") != true)
							{
								m_loginDialog.getCommonErrorInfoDialog().showDialog(m_context.getString(R.string.other_login_warning_title),	m_loginDialog.getOtherUserLoginString());
							}else
							{
							ForceLoginSelectDialog.getInstance(m_context).showDialog(m_context.getString(R.string.other_login_warning_title), m_context.getString(R.string.login_other_user_logined_error_forcelogin_msg),
									new OnClickBottonConfirm() 
							{
								public void onConfirm() 
								{
									m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
										public void onLoginSuccess() 				
										{
											connect();
										}

										public void onLoginFailed(String error_code)
										{
											if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
											{
												SmartLinkV3App.getInstance().setIsforcesLogin(true);
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
												m_loginDialog.getCommonErrorInfoDialog().showDialog(m_context.getString(R.string.other_login_warning_title),	m_loginDialog.getLoginTimeUsedOutString());
											}
										}
									});
								}
							});
							}
						}
						else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT))
						{
							m_loginDialog.getCommonErrorInfoDialog().showDialog(m_context.getString(R.string.other_login_warning_title),	m_loginDialog.getLoginTimeUsedOutString());
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
						}else{}				
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
			else if (status == UserLoginStatus.login) 
			{
				connect();	
			} 
			else 
			{
				PromptUserLogined();
			}
		}	
	}

	private void connect()
	{
		UsageSettingModel settings = BusinessMannager.getInstance().getUsageSettings();
		UsageRecordResult m_UsageRecordResult = BusinessMannager.getInstance().getUsageRecord();
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			if (settings.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Enable && m_UsageRecordResult.MonthlyPlan > 0) {
				if ((m_UsageRecordResult.HUseData + m_UsageRecordResult.RoamUseData) >= m_UsageRecordResult.MonthlyPlan) {
					//show warning dialog
					//m_connectWarningDialog.showDialog();
					String msgRes = m_context.getString(R.string.home_usage_over_redial_message);
					Toast.makeText(m_context, msgRes, Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
	
		if (m_bConnectPressd == false)
			m_bConnectPressd = true;
		else
			return;
	
		m_bConnectReturn = false;
		showNetworkState();
		showConnctBtnView();	
		
		if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
				|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.WAN_DISCONNECT_REQUSET,null);
		} else {
			BusinessMannager.getInstance().sendRequestMessage(
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
		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_networkTypeTextView.setVisibility(View.GONE);
			m_networkLabelTextView.setVisibility(View.VISIBLE);
			m_networkRoamImageView.setVisibility(View.GONE);
			m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
		}else{
			NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
			if(curNetwork.m_NetworkType == NetworkType.No_service) {
				m_networkTypeTextView.setVisibility(View.GONE);
				m_networkRoamImageView.setVisibility(View.GONE);
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
				m_networkLabelTextView.setVisibility(View.VISIBLE);
				return;
			}
			//show roaming
			if(curNetwork.m_bRoaming == true) 
				m_networkRoamImageView.setVisibility(View.VISIBLE);
			else
				m_networkRoamImageView.setVisibility(View.GONE);
			//show signal strength
			if(curNetwork.m_signalStrength == SignalStrength.Level_0)
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
			if (curNetwork.m_signalStrength == SignalStrength.Level_1)
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_1);
			if (curNetwork.m_signalStrength == SignalStrength.Level_2)
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_2);
			if (curNetwork.m_signalStrength == SignalStrength.Level_3)
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_3);
			if (curNetwork.m_signalStrength == SignalStrength.Level_4)
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_4);
			//show network type
			if (curNetwork.m_NetworkType == NetworkType.UNKNOWN)
			{
				m_networkTypeTextView.setVisibility(View.GONE);
				m_networkLabelTextView.setVisibility(View.VISIBLE);
			}
			
			//2G
			if (curNetwork.m_NetworkType == NetworkType.EDGE
					|| curNetwork.m_NetworkType == NetworkType.GPRS) {
				m_networkLabelTextView.setVisibility(View.GONE);
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_2g);
			}
			
			//3G
			if (curNetwork.m_NetworkType == NetworkType.HSPA					
					|| curNetwork.m_NetworkType == NetworkType.UMTS					
					|| curNetwork.m_NetworkType == NetworkType.HSUPA) {
				m_networkLabelTextView.setVisibility(View.GONE);
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_3g);
			}
			
			//3G+
			if (curNetwork.m_NetworkType == NetworkType.HSPA_PLUS					
					|| curNetwork.m_NetworkType == NetworkType.DC_HSPA_PLUS) {
				m_networkLabelTextView.setVisibility(View.GONE);
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_3g_plus);
			}
			
			//4G			
			if (curNetwork.m_NetworkType == NetworkType.LTE) {
				m_networkLabelTextView.setVisibility(View.GONE);
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setTypeface(typeFace);
				m_networkTypeTextView.setText(R.string.home_network_type_4g);
			}
		}
	}
		
	private void showBatteryState(){
		int nProgress =0;
		BatteryInfo batteryinfo = BusinessMannager.getInstance().getBatteryInfo();
		if(ConstValue.CHARGE_STATE_REMOVED == batteryinfo.getChargeState()){
			m_batteryProgress.setVisibility(View.VISIBLE);
			m_batteryscalelayout.setVisibility(View.VISIBLE);
			m_batterydescriptionlayout.setVisibility(View.GONE);
			m_batterychargingImageView.setVisibility(View.GONE);
			m_batteryscaleTextView.setTypeface(typeFace);
			m_batteryscaleTextView.setText(Integer.toString(batteryinfo.getBatterLevel()));
			nProgress = (int)batteryinfo.getBatterLevel();
	    	if (nProgress > m_batteryProgress.getMax())
				nProgress = m_batteryProgress.getMax();
	    	m_batteryProgress.setProgress(nProgress);
		}else if(ConstValue.CHARGE_STATE_CHARGING == batteryinfo.getChargeState()){		
			m_batteryProgress.setVisibility(View.GONE);
			m_batteryscalelayout.setVisibility(View.VISIBLE);
			m_batterydescriptionlayout.setVisibility(View.GONE);
			m_batterychargingImageView.setVisibility(View.VISIBLE);
			m_batteryscaleTextView.setTypeface(typeFace);
			m_batteryscaleTextView.setText(Integer.toString(batteryinfo.getBatterLevel()));
		}else if(ConstValue.CHARGE_STATE_COMPLETED == batteryinfo.getChargeState()){
			m_batterychargingImageView.setVisibility(View.GONE);
			m_batteryProgress.setVisibility(View.VISIBLE);
			m_batterydescriptionlayout.setVisibility(View.GONE);
			m_batteryProgress.setProgress(m_batteryProgress.getMax());
			m_batteryscalelayout.setVisibility(View.VISIBLE);
			m_batteryscaleTextView.setTypeface(typeFace);
			m_batteryscaleTextView.setText(Integer.toString(batteryinfo.getBatterLevel()));
		}else if(ConstValue.CHARGE_STATE_ABORT == batteryinfo.getChargeState()){
			
		}
	}
	
	private void showAccessDeviceState(){
		ArrayList<ConnectedDeviceItemModel> connecedDeviceLstData = BusinessMannager.getInstance().getConnectedDeviceList();
		m_accessnumTextView.setTypeface(typeFace);
		m_accessnumTextView.setText(Integer.toString(connecedDeviceLstData.size()));
		
		String strOfficial = this.m_context.getString(R.string.access_lable);
		String strHtmlOfficial = "<u>"+strOfficial+"</u>";
		m_accessstatusTextView.setText(Html.fromHtml(strHtmlOfficial));	
		
	}
}
