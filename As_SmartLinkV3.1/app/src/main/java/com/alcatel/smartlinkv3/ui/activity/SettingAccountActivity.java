package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingAccountActivity extends BaseActivity implements OnClickListener{

	public static final String  LOGOUT_FLAG = "LogoutFlag";

	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private TextView m_tv_done;
	private LinearLayout m_inputpwd;
	private TextView m_notice;
	private FrameLayout m_fl_titlebar;

	private EditText m_current_password;
	private EditText m_new_password;
	private EditText m_confirm_password;
	
	private IntentFilter m_change_password_filter;
	private PassWordChangeReceiver m_password_change_receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_account_new);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		controlTitlebar();
		initUi();
	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_account);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		
		m_fl_titlebar = (FrameLayout)findViewById(R.id.fl_edit_or_done);
		m_fl_titlebar.setVisibility(View.VISIBLE);
		
		m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
		m_tv_done.setVisibility(View.VISIBLE);
		
		findViewById(R.id.tv_titlebar_edit).setVisibility(View.GONE);
		
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
		m_tv_done.setOnClickListener(this);
	}
	
	private void initUi(){
		m_notice = (TextView)findViewById(R.id.password_notice);

		m_current_password = (EditText) findViewById(R.id.current_password);
		
		m_new_password = (EditText) findViewById(R.id.new_password);
		
		m_confirm_password = (EditText) findViewById(R.id.confirm_password);
		
		m_change_password_filter = new IntentFilter(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
		m_change_password_filter.addAction(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
		m_password_change_receiver = new PassWordChangeReceiver();
	}
	
	private void doneChangePassword(){
		String currentPwd = m_current_password.getText().toString();
		String newPwd = m_new_password.getText().toString();
		String confirmPwd = m_confirm_password.getText().toString();
		if(currentPwd.length() == 0){
			String strInfo = getString(R.string.input_current_password);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		if(!newPwd.equals(confirmPwd)){
			String strInfo = getString(R.string.inconsistent_new_password);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		if(confirmPwd.length() < 4 || confirmPwd.length() > 16){
			String strInfo = getString(R.string.change_passowrd_invalid_password);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		String splChrs = "[^a-zA-Z0-9-\\+!@\\$#\\^&\\*]" ;
		Pattern pattern = Pattern.compile(splChrs);
		Matcher matcher = pattern.matcher(confirmPwd);
		if(matcher.find()){
			String strInfo = getString(R.string.login_invalid_password);
			Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		
		userChangePassword(LoginDialog.USER_NAME, currentPwd, confirmPwd);
		
		m_current_password.setText(null);
		m_new_password.setText(null);
		m_confirm_password.setText(null);
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(m_current_password.getWindowToken(),0);
		imm.hideSoftInputFromWindow(m_new_password.getWindowToken(),0);
		imm.hideSoftInputFromWindow(m_confirm_password.getWindowToken(),0);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			SettingAccountActivity.this.finish();
			break;
		case R.id.tv_titlebar_done:
			doneChangePassword();
			break;
		default:
			break;
		}
	}
	
	public void userChangePassword(String UserName, String CurrentPassword, String NewPassword){
		UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
		if (m_loginStatus != null && m_loginStatus == UserLoginStatus.login) {
			DataValue data = new DataValue();
			data.addParam("user_name", UserName);
			data.addParam("current_password", CurrentPassword);
			data.addParam("new_password", NewPassword);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.USER_CHANGE_PASSWORD_REQUEST, data);
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
//		IntentFilter filter = new IntentFilter();  
//		filter.addAction(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
//		PassWordChangeReceiver receiver = new PassWordChangeReceiver();
		registerReceiver(m_password_change_receiver, m_change_password_filter);  
	}
	
	@Override
	protected void onResume(){
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_password_change_receiver, m_change_password_filter);  
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(m_password_change_receiver);  
	}
	
	private class PassWordChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.USER_CHANGE_PASSWORD_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
						String strInfo = getString(R.string.change_password_successful);
						Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
						finish();
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					if(strErrorCode.equals(ErrorCode.CURRENT_PASSWORD_IS_WRONG)){
						String strInfo = getString(R.string.wrong_current_password);
						Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
					}
					else if(strErrorCode.equals(ErrorCode.CHANGE_PASSWORD_FAILED)){
						String strInfo = getString(R.string.change_password_failed);
						Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
					}
					else{
						String strInfo = getString(R.string.unknown_error);
						Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
					}
				}
				else{
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			else{
				String strInfo = getString(R.string.unknown_error);
				Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
