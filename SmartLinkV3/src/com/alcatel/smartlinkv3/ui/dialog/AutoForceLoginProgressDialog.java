package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AutoForceLoginProgressDialog 
{
	private ProgressDialog m_dlgProgress = null;
	private Context m_context = null;
	private static OnAutoForceLoginFinishedListener s_callback;	
	private String loginCheckDialogTitle;
	private String loginCheckDialogContent;
	private AuthenficationBroadcastReviever m_auReceiver;
	
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
	
	public void autoForceLoginAndShowDialog(OnAutoForceLoginFinishedListener callback ,String password ,String username)
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
			data.addParam("user_name", username);
			data.addParam("password", password);
		}
		
		BusinessMannager.getInstance().sendRequestMessage(
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
		public void onReceive(Context arg0, Intent arg1) 
		{

			if (arg1.getAction().equalsIgnoreCase(
					MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) 
			{
				boolean bCPEWifiConnected = DataConnectManager.getInstance()
						.getCPEWifiConnected();
				if (!bCPEWifiConnected) 
				{
					closeDialog();
				}
			} 
			else if (arg1.getAction().equalsIgnoreCase(
					MessageUti.USER_FORCE_LOGIN_REQUEST)) 
			{
				if(m_dlgProgress != null && !m_dlgProgress.isShowing())
					return;
				closeDialog();
				int nRet = arg1.getIntExtra(MessageUti.RESPONSE_RESULT, -1);
				String strErrorCode = arg1.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (null != s_callback) 
				{
					if(BaseResponse.RESPONSE_OK == nRet && strErrorCode.length() == 0 )
					{
						s_callback.onLoginSuccess();					
					}
					else
					{
						if(strErrorCode == null)
							strErrorCode = "";
						s_callback.onLoginFailed(strErrorCode);
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
