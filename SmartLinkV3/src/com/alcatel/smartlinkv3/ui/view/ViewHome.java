package com.alcatel.smartlinkv3.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alcatel.smartlinkv3.ui.dialog.InquireDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog.OnInquireApply;
import com.alcatel.smartlinkv3.common.ENUM.OVER_FLOW_STATE;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.common.ENUM.SignalStrength;
import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.common.ENUM.NetworkType;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.ActivityDeviceManager;
import com.alcatel.smartlinkv3.ui.activity.ActivitySmsDetail;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ViewHome extends BaseViewImpl implements OnClickListener {
	private static final String TAG = ViewHome.class.getSimpleName();
	
	/*frame_connect  start*/
	private Button m_connectBtn = null;
	private Button m_unlockSimBtn = null;
	private ProgressBar m_connectWaiting = null;
	
	private TextView m_connectToNetworkTextView;
	private TextView m_connectToLabel;
	private TextView m_simOrServiceTextView = null;
	private LinearLayout m_simOrServiceStateLayout = null;
	private LinearLayout m_connectToLayout = null;
	
	private boolean m_bConnectPressd = false;
	private boolean m_bConnectReturn = false;
	/*frame_connect  end*/
	
	/*sigel_panel  start*/
	private TextView m_networkTypeTextView;
	private ImageView m_networkRoamImageView;
	private ImageView m_signalImageView;
	/*sigel_panel  end*/
	
	/*battery_panel  start*/
	private TextView m_batteryscaleTextView;
	private ProgressBar m_batteryProgress = null;
	/*battery_panel  end*/
	
	/*access_panel  start*/
	private TextView m_accessnumTextView;
	private ImageView m_accessImageView;
	private RelativeLayout m_accessDeviceLayout;
	/*access_panel  end*/
	
	private LoginDialog m_loginDialog = null;
	
	
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
				
				//showTotalDataUI();
				//updateStatisticsUI();
				
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
		}	
	}
	
	public ViewHome(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_home,null);
		
		m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
		m_connectBtn.setOnClickListener(this);
		m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
		m_connectToLabel = (TextView) m_view.findViewById(R.id.connect_label);
		m_connectToLayout = (LinearLayout) m_view.findViewById(R.id.connect_to_layout);
		
		m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);
		m_simOrServiceTextView = (TextView) m_view.findViewById(R.id.connect_label);
		m_simOrServiceStateLayout = (LinearLayout) m_view.findViewById(R.id.sim_or_service_layout);
		
		m_connectWaiting = (ProgressBar) m_view.findViewById(R.id.waiting_progress);
		
		
		m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);
		m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);
		m_networkRoamImageView = (ImageView) m_view.findViewById(R.id.connect_roam);
		
		m_batteryscaleTextView = (TextView) m_view.findViewById(R.id.battery_scale_label);
		m_batteryProgress = (ProgressBar) m_view.findViewById(R.id.battery_progress);
		
		m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
		m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
		m_accessDeviceLayout = (RelativeLayout)m_view.findViewById(R.id.access_num_layout);
		m_accessDeviceLayout.setOnClickListener(this);
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect_button:
			connectBtnClick();
			break;
		case R.id.access_num_layout:
			accessDeviceLayoutClick();
			break;
		default:
			break;
		}
	}
	
	private void accessDeviceLayoutClick() {
		Intent intent = new Intent();
		intent.setClass(m_context, ActivityDeviceManager.class);	
		this.m_context.startActivity(intent);
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
	
	private void showSignalAndNetworkType() {
		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_networkTypeTextView.setVisibility(View.INVISIBLE);
			m_networkRoamImageView.setVisibility(View.INVISIBLE);
			m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
		}else{
			NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
			if(curNetwork.m_NetworkType == NetworkType.No_service) {
				m_networkTypeTextView.setVisibility(View.INVISIBLE);
				m_networkRoamImageView.setVisibility(View.INVISIBLE);
				m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
				
				return;
			}
			//show roaming
			if(curNetwork.m_bRoaming == true) 
				m_networkRoamImageView.setVisibility(View.VISIBLE);
			else
				m_networkRoamImageView.setVisibility(View.INVISIBLE);
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
			//if (curNetwork.m_signalStrength == SignalStrength.Level_5)
			//	m_signalImageView.setBackgroundResource(R.drawable.home_signal_5);
			//show network type
			if (curNetwork.m_NetworkType == NetworkType.UNKNOWN)
				m_networkTypeTextView.setVisibility(View.INVISIBLE);
			
			//2G
			if (curNetwork.m_NetworkType == NetworkType.EDGE
					|| curNetwork.m_NetworkType == NetworkType.GSM 
					|| curNetwork.m_NetworkType == NetworkType.GPRS) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setText(R.string.home_network_type_2g);
			}
			
			//3G
			if (curNetwork.m_NetworkType == NetworkType.UMTS) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setText(R.string.home_network_type_3g);
			}
			//H			
			if (curNetwork.m_NetworkType == NetworkType.HSPA					
					|| curNetwork.m_NetworkType == NetworkType.HSDPA					
					|| curNetwork.m_NetworkType == NetworkType.HSUPA) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setText(R.string.home_network_type_h);
			}
			
			//H+
			if (curNetwork.m_NetworkType == NetworkType.DC_HSPA_PLUS					
					|| curNetwork.m_NetworkType == NetworkType.HSPA_PLUS) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setText(R.string.home_network_type_h_plus);
			}		
		
			//4G			
			if (curNetwork.m_NetworkType == NetworkType.LTE) {
				m_networkTypeTextView.setVisibility(View.VISIBLE);
				m_networkTypeTextView.setText(R.string.home_network_type_4g);
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
			} else if (SIMState.PinRequired == simStatus) {
				nStatusId = R.string.Home_pin_locked;
				m_unlockSimBtn.setVisibility(View.VISIBLE);
			} else if (SIMState.PukRequired == simStatus) {
				nStatusId = R.string.Home_puk_locked;
				m_unlockSimBtn.setVisibility(View.VISIBLE);
			} else {
				nStatusId = R.string.home_initializing;
				m_unlockSimBtn.setVisibility(View.GONE);
			}

			m_simOrServiceTextView.setText(nStatusId);
			m_simOrServiceStateLayout.setVisibility(View.VISIBLE);
			m_connectToLayout.setVisibility(View.GONE);
			return;
		}

		m_unlockSimBtn.setVisibility(View.GONE);
		
		NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service) {
			m_connectWaiting.setVisibility(View.GONE);
			m_simOrServiceTextView.setText(R.string.home_no_service);
			m_simOrServiceStateLayout.setVisibility(View.VISIBLE);
			m_connectToLayout.setVisibility(View.GONE);
			return;
		}
		
		if(curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			m_connectWaiting.setVisibility(View.GONE);
			m_simOrServiceTextView.setText(R.string.home_initializing);
			m_simOrServiceStateLayout.setVisibility(View.VISIBLE);
			m_connectToLayout.setVisibility(View.GONE);
			return;
		}

		m_simOrServiceStateLayout.setVisibility(View.GONE);
		m_connectToLayout.setVisibility(View.VISIBLE);
		m_connectToNetworkTextView.setText(curNetwork.m_strNetworkName);
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (m_bConnectPressd == false) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				m_connectToLabel.setText(R.string.home_connected_to);
				m_connectWaiting.setVisibility(View.GONE);
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				m_connectToLabel.setText(R.string.home_disconnecting_to);
				m_connectWaiting.setVisibility(View.VISIBLE);
			}
			if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
				m_connectToLabel.setText(R.string.home_disconnected_to);
				m_connectWaiting.setVisibility(View.GONE);
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
	}
	
	private void showConnctBtnView() {

		SIMState simStatus = BusinessMannager.getInstance().getSimStatus().m_SIMState;
		if (simStatus != SIMState.Accessable) {
			m_connectWaiting.setVisibility(View.GONE);
			m_connectBtn.setVisibility(View.INVISIBLE);
			return;
		}
		
		NetworkInfoModel curNetwork = BusinessMannager.getInstance().getNetworkInfo();
		if(curNetwork.m_NetworkType == NetworkType.No_service || curNetwork.m_NetworkType == NetworkType.UNKNOWN) {
			m_connectWaiting.setVisibility(View.GONE);
			m_connectBtn.setVisibility(View.INVISIBLE);
			return;
		}
		m_connectBtn.setVisibility(View.VISIBLE);
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		if (m_bConnectPressd == false) {
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
				m_connectBtn.setBackgroundResource(R.drawable.switch_on);
				m_connectBtn.setEnabled(true);
				m_connectWaiting.setVisibility(View.GONE);
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
	
	}
	
		private void connectBtnClick() {	
			
			if (LoginDialog.isLoginSwitchOff()) {
				connect();	
			} else {
				UserLoginStatus status = BusinessMannager.getInstance()
						.getLoginStatus();
	
				if (status == UserLoginStatus.OthersLogined) {
					PromptUserLogined();
				} else if (status == UserLoginStatus.selfLogined) {
					connect();	
				} else {
					m_loginDialog.showDialog(new OnLoginFinishedListener() {
						@Override
						public void onLoginFinished() {
							connect();	
						}
					});
				}
			}	
		}

		private void connect()
		{
//			UsageSettingsResult settings = BusinessMannager.getInstance().getUsageSettings();
//			if (settings.m_overflowState == OVER_FLOW_STATE.Enable && settings.m_lTotalValue > 0) {
//				long lTotalUsedUsage = BusinessMannager.getInstance().GetBillingMonthTotalUsage();
//				if (lTotalUsedUsage >= settings.m_lTotalValue) {
//					//show warning dialog
//					//m_connectWarningDialog.showDialog();
//					String msgRes = m_context.getString(R.string.home_usage_over_redial_message);
//					Toast.makeText(m_context, msgRes, Toast.LENGTH_LONG).show();
//					return;
//				}
//panchong			}
		
			if (m_bConnectPressd == false)
				m_bConnectPressd = true;
			else
				return;
		
			m_bConnectReturn = false;
			showNetworkState();
			showConnctBtnView();
			ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
			if (internetConnState.m_connectionStatus == ConnectionStatus.Connected
					|| internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
				BusinessMannager.getInstance().sendRequestMessage(
						MessageUti.WAN_DISCONNECT_REQUSET,null);
			} else {
				BusinessMannager.getInstance().sendRequestMessage(
								MessageUti.WAN_CONNECT_REQUSET,null);
			}	
		}
		private void PromptUserLogined() {
			final InquireDialog inquireDlg = new InquireDialog(m_context);
			inquireDlg.m_titleTextView.setText(R.string.login_check_dialog_title);
			inquireDlg.m_contentTextView
					.setText(R.string.login_other_user_logined_error_msg);
			inquireDlg.m_contentDescriptionTextView.setText("");
			inquireDlg.m_confirmBtn
					.setBackgroundResource(R.drawable.selector_common_button);
			inquireDlg.m_confirmBtn.setText(R.string.ok);
			inquireDlg.showDialog(new OnInquireApply() {
				@Override
				public void onInquireApply() {
					inquireDlg.closeDialog();
				}
			});
		}
		
		private void showBatteryState(){
			
		}
		
		private void showAccessDeviceState(){
			
		}
}
