package com.alcatel.smartlinkv3.ui.dialog;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginDialog implements OnClickListener, OnKeyListener, TextWatcher {
	private Context m_context;
	private static final String REG_STR = "[^a-zA-Z0-9-\\+!@\\$#\\^&\\*]";
	public boolean m_bIsShow = false;
	private Dialog m_dlgLogin = null;
	private ProgressDialog m_dlgProgress = null;
	private View m_vLogin = null;
	private EditText m_etPassword;
	private Button m_btnApply;
	private TextView m_tvPasswordError;
	private Animation m_shake = null;

	// private static LoginDialog m_instance;
	private String m_strMsgInvalidPassword;
	private String m_strMsgWrongPassword;
	private String m_strMsgOtherUserLogined;
	private String m_strMsgLoginTimeUsedOut;
	private AuthenficationBroadcastReviever m_auReceiver;
	private static OnLoginFinishedListener m_callback;
	private boolean m_bOtherUserLoginError = false;
	private boolean m_bLoginTimeUsedOutError = false;
	
	private String m_password = "";

	// just for test
	public static final String USER_NAME = "admin";
	private CommonErrorInfoDialog m_dialog_err_info;
	
	
/*	private static boolean m_bAlreadyLogin = false;
	
	public static void  setAlreadyLogin(boolean bAlreadyLogin) {
		m_bAlreadyLogin = bAlreadyLogin;
	}
	
	public static boolean getAlreadyLogin() {
		return m_bAlreadyLogin;
	}*/

	public LoginDialog(Context context) {
		m_context = context;
		m_auReceiver = new AuthenficationBroadcastReviever();

		if (null == m_dialog_err_info) {
			m_dialog_err_info = CommonErrorInfoDialog.getInstance(m_context);
		}
		createDialog();
	}

	private class AuthenficationBroadcastReviever extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equalsIgnoreCase(
					MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
				boolean bCPEWifiConnected = DataConnectManager.getInstance()
						.getCPEWifiConnected();
				if (!bCPEWifiConnected) {
					closeDialog();
				}
			} else if (arg1.getAction().equalsIgnoreCase(
					MessageUti.USER_LOGIN_REQUEST)) {
				int nRet = arg1.getIntExtra(MessageUti.RESPONSE_RESULT, -1);
				String strErrorCode = arg1
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nRet
						&& strErrorCode.length() == 0) {

					CPEConfig.getInstance().setLoginPassword(m_password);
					CPEConfig.getInstance().setLoginUsername(USER_NAME);
					CPEConfig.getInstance().setAutoLoginFlag(true);
					//setAlreadyLogin(true);
					m_bOtherUserLoginError = false;
					m_bLoginTimeUsedOutError = false;
					closeDialog();
					if (null != m_callback) {
						m_callback.onLoginFinished();
						m_callback = null;
					}

				} else if (BaseResponse.RESPONSE_OK == nRet
						&& strErrorCode
								.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
					m_bOtherUserLoginError = true;
					m_bLoginTimeUsedOutError = false;
					m_dialog_err_info.showDialog(
							m_context.getString(R.string.other_login_warning_title),
							m_strMsgOtherUserLogined);
					if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
						m_dlgProgress.dismiss();
					}
					closeDialog();
//					SetErrorMsg(nRet, strErrorCode);
//					m_vLogin.startAnimation(m_shake);
					//setAlreadyLogin(false);

				} else if (BaseResponse.RESPONSE_OK == nRet
						&& strErrorCode
						.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
					m_bLoginTimeUsedOutError = true;
					m_bOtherUserLoginError = false;
					m_dialog_err_info.showDialog(
							m_context.getString(R.string.other_login_warning_title),
							m_strMsgLoginTimeUsedOut);
					if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
						m_dlgProgress.dismiss();
					}
					closeDialog();
		//			SetErrorMsg(nRet, strErrorCode);
		//			m_vLogin.startAnimation(m_shake);
					//setAlreadyLogin(false);

				}else {
					m_bOtherUserLoginError = false;
					m_bLoginTimeUsedOutError = false;
					//SetErrorMsg(nRet, strErrorCode);
					m_vLogin.startAnimation(m_shake);
					//setAlreadyLogin(false);
				}
			}
		}
	}

	public void showDialog(OnLoginFinishedListener callback) {
		m_callback = callback;
	/*	if (getAlreadyLogin()) {
			m_callback.onLoginFinished();
		} else {*/

			if (m_dlgLogin != null) {
				m_tvPasswordError.setVisibility(View.GONE);
				m_btnApply.setEnabled(false);
				m_etPassword.setText("");
				m_etPassword.requestFocus();
				m_dlgLogin.show();
				m_bIsShow = true;
			}
			m_vLogin.clearAnimation();
		//}
	}

	private void SetErrorMsg(int nResponseResult, String err) {
		if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
			m_dlgProgress.dismiss();
		}

		if (m_dlgLogin != null) {
			m_tvPasswordError.setVisibility(View.VISIBLE);
			if (BaseResponse.RESPONSE_OK == nResponseResult
					&& err.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
				m_tvPasswordError.setText(m_strMsgOtherUserLogined);
			} else if(BaseResponse.RESPONSE_OK == nResponseResult
					&& err.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)){
				m_tvPasswordError.setText(m_strMsgLoginTimeUsedOut);
			}else {
				m_tvPasswordError.setText(m_strMsgWrongPassword);
			}
			m_btnApply.setEnabled(false);
			m_etPassword.setText("");
			m_etPassword.requestFocus();
		}
	}

	private void showDialog() {
		if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
			m_dlgProgress.dismiss();
		}

		if (m_dlgLogin != null) {		
			m_btnApply.setEnabled(false);
			m_etPassword.setText("");
			m_etPassword.requestFocus();
			m_tvPasswordError.setText("");			
			if (!m_bIsShow) {
				m_dlgLogin.show();
			}
			m_bIsShow = true;
		}
		m_vLogin.clearAnimation();
	}

	public void closeDialog() {
		InputMethodManager imm = (InputMethodManager) m_context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(m_vLogin.getWindowToken(), 0);

		if (m_dlgProgress != null && m_dlgProgress.isShowing())
			m_dlgProgress.dismiss();

		if (m_dlgLogin.isShowing()) {
			m_dlgLogin.dismiss();
		}

		m_bIsShow = false;
	}

	public void destroyDialog() {
		closeDialog();
		m_dialog_err_info.closeDialog();
		try {
			m_context.unregisterReceiver(m_auReceiver);
		} catch (Exception e) {
			
		}
	}

	private void createDialog() {
		m_strMsgInvalidPassword = m_context.getResources().getString(
				R.string.login_invalid_password);
		m_strMsgWrongPassword = m_context.getResources().getString(
				R.string.login_prompt_str);
		m_strMsgOtherUserLogined = m_context.getResources().getString(
				R.string.login_other_user_logined_error_msg);
		m_strMsgLoginTimeUsedOut = m_context.getResources().getString(
				R.string.login_login_time_used_out_msg);

		LayoutInflater factory = LayoutInflater.from(m_context);
		m_vLogin = factory.inflate(R.layout.login_view, null);

		m_vLogin.setOnKeyListener(this);
		m_dlgLogin = new Dialog(m_context, R.style.dialog);
		Window window = m_dlgLogin.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		m_dlgLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_dlgLogin.setContentView(m_vLogin);
		m_dlgLogin.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

		m_tvPasswordError = (TextView) m_vLogin
				.findViewById(R.id.login_label_prompt);

		m_etPassword = (EditText) m_vLogin.findViewById(R.id.login_edit_view);
		m_etPassword.setOnKeyListener(this);
		m_etPassword.addTextChangedListener(this);

		m_btnApply = (Button) m_vLogin.findViewById(R.id.login_apply_btn);
		m_btnApply.setOnClickListener(this);

		Button closeBtn = (Button) m_vLogin
				.findViewById(R.id.login_close_btn);
		closeBtn.setOnClickListener(this);

		m_dlgLogin.setCancelable(false);

		m_context.registerReceiver(m_auReceiver, new IntentFilter(
				MessageUti.USER_LOGIN_REQUEST));
		m_context.registerReceiver(m_auReceiver, new IntentFilter(
				MessageUti.CPE_WIFI_CONNECT_CHANGE));

		m_shake = AnimationUtils.loadAnimation(m_context,
				R.anim.input_error_shake);
		m_shake.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// Hide admin dialog
				if (m_bIsShow) {
					m_dlgLogin.dismiss();
				}
				m_bIsShow = false;
				// Show error dialog
				String strTitle = "";
				if (m_bOtherUserLoginError) {
					strTitle = m_strMsgOtherUserLogined;
				} else if(m_bLoginTimeUsedOutError){
					strTitle = m_strMsgLoginTimeUsedOut;
				}else{
					strTitle = m_context
							.getString(R.string.login_psd_error_msg);
				}
				if (m_dlgProgress != null && m_dlgProgress.isShowing())
					m_dlgProgress.dismiss();
				
				ErrorDialog.getInstance(m_context).showDialog(strTitle,
						new OnClickBtnRetry() {

							@Override
							public void onRetry() {
								showDialog();
							}
						});
			}
		});

	}

	private void apply() {
		m_password = m_etPassword.getText().toString();
		Pattern pattern = Pattern.compile(REG_STR);
		Matcher matcher = pattern.matcher(m_password);
		if (matcher.find()) {
			m_tvPasswordError.setText(m_strMsgInvalidPassword);
			m_tvPasswordError.setVisibility(View.VISIBLE);
			return;
		}

		InputMethodManager imm = (InputMethodManager) m_context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(m_vLogin.getWindowToken(), 0);

		String strTitle = m_context
				.getString(R.string.login_check_dialog_title);
		String strContent = m_context
				.getString(R.string.login_check_dialog_content) + "...";
		m_dlgProgress = ProgressDialog.show(m_context, strTitle, strContent,
				true, false);
		m_dlgProgress.setCancelable(true);
		m_tvPasswordError.setVisibility(View.GONE);

		DataValue data = new DataValue();
		data.addParam("user_name", USER_NAME);
		data.addParam("password", m_password);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.USER_LOGIN_REQUEST, data);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() >= 1) {
			m_btnApply.setEnabled(true);
		} else {
			m_btnApply.setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			m_dlgLogin.dismiss();
			m_bIsShow = false;
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_apply_btn:
			apply();
			break;

		case R.id.login_close_btn:
			closeDialog();
			break;
		}
	}

	//
	public interface OnLoginFinishedListener {
		public void onLoginFinished();
	}

	public static boolean isLoginSwitchOff() {
		String strLoginFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/CPE/LoginDisable";
		File f = new File(strLoginFile);
		return f.exists();
	}

}
