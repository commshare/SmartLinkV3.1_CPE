package com.alcatel.smartlinkv3.ui.dialog;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

public class AutoForceLoginProgressDialog 
{
	private ProgressDialog m_dlgProgress = null;
	private Context m_context = null;
	private static OnAutoForceLoginFinishedListener s_callback;	
	private String loginCheckDialogTitle;
	private String loginCheckDialogContent;
	private AuthenficationBroadcastReviever m_auReceiver;
	private static boolean m_isUserFirstLogin =false;
	
	public AutoForceLoginProgressDialog(Context context) 
	{
		m_context = context;		
		loginCheckDialogTitle = m_context.getString(R.string.login_check_dialog_title);
		loginCheckDialogContent = m_context	.getString(R.string.login_check_dialog_content) + "...";
		
		m_auReceiver = new AuthenficationBroadcastReviever();
		
		m_context.registerReceiver(m_auReceiver, new IntentFilter(
				MessageUti.USER_FORCE_LOGIN_REQUEST));
		m_context.registerReceiver(m_auReceiver, new IntentFilter(
				MessageUti.CPE_WIFI_CONNECT_CHANGE));
	}
	public void setCallback(OnAutoForceLoginFinishedListener callback){
		s_callback = callback;	
	}
	
	public void autoForceLoginAndShowDialog(OnAutoForceLoginFinishedListener callback)
	{
		s_callback = callback;		
		DataValue data = new DataValue();
		
		if(CPEConfig.getInstance().getAutoLoginFlag())
		{
			
			data.addParam("user_name", CPEConfig.getInstance().getLoginUsername());
			data.addParam("password", CPEConfig.getInstance().getLoginPassword());

		}
		else
		{
			m_isUserFirstLogin = true;
			data.addParam("user_name", SmartLinkV3App.getInstance().getLoginUsername());
			data.addParam("password", SmartLinkV3App.getInstance().getLoginPassword());
		}
		
		BusinessManager.getInstance().sendRequestMessage(
				MessageUti.USER_FORCE_LOGIN_REQUEST, data);
		
		if(m_dlgProgress != null && m_dlgProgress.isShowing())
			return;
		
			
		m_dlgProgress = ProgressDialog.show(m_context, loginCheckDialogTitle, loginCheckDialogContent, true, false);
		m_dlgProgress.setCancelable(true);
	}
	
	public void closeDialog()
	{
		if(m_dlgProgress != null && m_dlgProgress.isShowing())
			m_dlgProgress.dismiss();
	}
	
	public void destroyDialog()
	{
		try 
		{
			m_context.unregisterReceiver(m_auReceiver);
		} 
		catch (Exception e) 
		{			
		}
	}
	
	private class AuthenficationBroadcastReviever extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();

			if (intent.getAction().equalsIgnoreCase(
					MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) 
			{
				boolean bCPEWifiConnected = DataConnectManager.getInstance()
						.getCPEWifiConnected();
				if (!bCPEWifiConnected) 
				{
					closeDialog();
				}
			} 
			else if (intent.getAction().equalsIgnoreCase(
					MessageUti.USER_FORCE_LOGIN_REQUEST)) 
			{
				
				if(m_dlgProgress != null && !m_dlgProgress.isShowing())
					return;
				closeDialog();

				if (null != s_callback) 
				{
					if(ok )
					{
						if(m_isUserFirstLogin||SmartLinkV3App.getInstance().IsForcesLogin())
						{
							CPEConfig.getInstance().setLoginPassword(SmartLinkV3App.getInstance().getLoginPassword());
							CPEConfig.getInstance().setLoginUsername(SmartLinkV3App.getInstance().getLoginUsername());
							SmartLinkV3App.getInstance().setLoginPassword("");;
							SmartLinkV3App.getInstance().setLoginUsername("");
							m_isUserFirstLogin = false;
							SmartLinkV3App.getInstance().setForcesLogin(false);
						}
						s_callback.onLoginSuccess();					
					}
					else
					{
						s_callback.onLoginFailed(response.getErrorCode());
					}
					s_callback = null;
				}				
			}
		}
	}
	
	public interface OnAutoForceLoginFinishedListener 
	{
		public void onLoginSuccess();
		public void onLoginFailed(String error_code);
//		public void onFirstLogin();
	}	
}
