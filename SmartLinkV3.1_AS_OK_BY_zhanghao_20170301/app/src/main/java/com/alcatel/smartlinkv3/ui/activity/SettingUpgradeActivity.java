package com.alcatel.smartlinkv3.ui.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.business.update.DeviceUpgradeStateInfo;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceUpgradeStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog.OnInquireApply;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingUpgradeActivity extends BaseActivity implements OnClickListener{

	private final int MSG_GET_NEW_VERSION = 10000000;
	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_check_firmware=null;
	private Button m_btn_upgrade_app=null;
	private TextView m_tv_cur_firmware_version =null;
	private TextView m_tv_cur_app_version=null;
	private TextView m_tv_new_app_version=null;
	private TextView m_tv_new_firmware_version = null;
	private TextView m_tv_update_progress=null;
	private ProgressBar m_pb_check_fw_waiting=null;
	private ProgressBar m_pb_check_app_waiting=null;
	private ProgressBar m_pb_waiting=null;
	private boolean m_blHasNewFirmware = false;
	private boolean m_blHasNewApp = false;
	private boolean m_blUpdating = false;
	private static String m_strNewFirmwareInfo="";
	private int m_nUpdradeFWProgress=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//set titlebar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_upgrade);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title
		controlTitle();
		//create controls
		createControls();
	}

	private void controlTitle(){
		m_tv_titleTextView = (TextView)findViewById(R.id.tv_title_title);
		m_tv_titleTextView.setText(R.string.setting_upgrade);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}

	private void createControls(){

		m_tv_cur_firmware_version=(TextView)findViewById(R.id.tv_check_firmware);
		m_tv_cur_app_version = (TextView)findViewById(R.id.tv_current_app_version);
		m_tv_new_app_version = (TextView)findViewById(R.id.tv_new_app_version);
		m_tv_new_firmware_version = (TextView)findViewById(R.id.tv_new_firmware_version);
		m_btn_check_firmware = (Button)findViewById(R.id.btn_check_firmware);
		m_btn_upgrade_app = (Button)findViewById(R.id.btn_app_upgrade);
		m_btn_check_firmware.setOnClickListener(this);
		m_btn_upgrade_app.setOnClickListener(this);
		//
		m_pb_waiting = (ProgressBar)findViewById(R.id.pb_upgrade_waiting_progress);
		m_pb_check_app_waiting=(ProgressBar)findViewById(R.id.check_app_waiting_progress);
		m_pb_check_fw_waiting=(ProgressBar)findViewById(R.id.check_firmware_waiting_progress);
		m_tv_update_progress = (TextView)findViewById(R.id.tv_progress_update);

		//updateNewDeviceInfo(); 
		String strCurAppVersion = getString(R.string.setting_upgrade_current_app_version);
		strCurAppVersion += BusinessMannager.getInstance().getAppVersion();
		m_tv_cur_app_version.setText(strCurAppVersion);
		m_tv_new_app_version.setText("");
		String strCurFWVersion = getString(R.string.setting_upgrade_device_version);
		strCurFWVersion += BusinessMannager.getInstance().getSystemInfo().getSwVersion();
		m_tv_cur_firmware_version.setText(strCurFWVersion);
		m_tv_new_firmware_version.setText("");
		Intent it = getIntent();
		boolean blFirst = it.getBooleanExtra("First", true);
		if (blFirst) {
			m_strNewFirmwareInfo = "";
			updateNewDeviceInfo(false);
		}else {
			updateNewDeviceInfo(true);
		}
		if (m_blHasNewApp) {
			m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_upgrade);
		}else {
			m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_check);
		}
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_blUpdating = true;
			m_pb_waiting.setVisibility(View.VISIBLE);
			m_tv_update_progress.setVisibility(View.VISIBLE);
			String strProgress = m_nUpdradeFWProgress+"%";
			m_tv_update_progress.setText(strProgress);
		}else {
			m_blUpdating = false;
			m_pb_waiting.setVisibility(View.GONE);
			m_tv_update_progress.setVisibility(View.GONE);
			m_nUpdradeFWProgress = 0;
			String strProgress = m_nUpdradeFWProgress+"%";
			m_tv_update_progress.setText(strProgress);
		}
		m_btn_check_firmware.setEnabled(!blShow);
		m_btn_upgrade_app.setEnabled(!blShow);
	}

	private void showCheckFWWaiting(boolean blShow){
		m_btn_check_firmware.setEnabled(!blShow);
		if (blShow) {
			m_pb_check_fw_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_check_fw_waiting.setVisibility(View.GONE);
		}
	}
	
	private void showCheckAppWaiting(boolean blShow){
		m_btn_upgrade_app.setEnabled(!blShow);
		if (blShow) {
			m_pb_check_app_waiting.setVisibility(View.VISIBLE);
		}else {
			m_pb_check_app_waiting.setVisibility(View.GONE);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			onBtnBack();
			break;

		case R.id.btn_app_upgrade:
			ConnectStatusModel status = BusinessMannager.getInstance().getConnectStatus();
			ConnectionStatus result = status.m_connectionStatus;
			if (result != ConnectionStatus.Connected) {
				m_tv_new_app_version.setText(R.string.setting_upgrade_no_connection);
			}else {
				onBtnAppCheck();
			}
			break;

		case R.id.btn_check_firmware:
			status = BusinessMannager.getInstance().getConnectStatus();
			result = status.m_connectionStatus;
			if (result != ConnectionStatus.Connected) {
				m_tv_new_firmware_version.setText(R.string.setting_upgrade_no_connection);
				m_strNewFirmwareInfo = "";
			}else {
				onBtnFirmwareCheck();
			}
			break;

		default:
			break;
		}
	}

	private void onBtnBack(){
		if (m_pb_waiting.isShown() && m_blUpdating) {
			showStopUpdateDialog();
		}else {
			SettingUpgradeActivity.this.finish();				
		}
	}
	private void onBtnFirmwareUpdate()
	{	
			final InquireDialog inquireDlg = new InquireDialog(this);
			inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
			inquireDlg.m_contentTextView.setGravity(Gravity.LEFT);
			inquireDlg.m_contentTextView.setText(R.string.setting_upgrade_firmware_warning);
			inquireDlg.m_contentDescriptionTextView.setText("");
			inquireDlg.m_confirmBtn.setBackgroundResource(R.drawable.selector_common_button);
			inquireDlg.m_confirmBtn.setText(R.string.ok);
			inquireDlg.showDialog(new OnInquireApply() 
			{

				@Override
				public void onInquireApply() 
				{
					// TODO Auto-generated method stub
					//upgrade firmware
					ShowWaiting(true);
					inquireDlg.closeDialog();
					BusinessMannager.getInstance().sendRequestMessage(
							MessageUti.UPDATE_SET_DEVICE_START_FOTA_UPDATE, null);
				}
			});
		
	}
	private void onBtnFirmwareCheck(){
		if (m_blHasNewFirmware) {
			m_nUpdradeFWProgress = 0;
			final InquireDialog inquireDlg = new InquireDialog(this);
			inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
			inquireDlg.m_contentTextView.setGravity(Gravity.LEFT);
			inquireDlg.m_contentTextView
			.setText(R.string.setting_upgrade_firmware_warning);
			inquireDlg.m_contentDescriptionTextView.setText("");
			inquireDlg.m_confirmBtn
			.setBackgroundResource(R.drawable.selector_common_button);
			inquireDlg.m_confirmBtn.setText(R.string.ok);
			inquireDlg.showDialog(new OnInquireApply() {

				@Override
				public void onInquireApply() {
					// TODO Auto-generated method stub
					//upgrade firmware
					ShowWaiting(true);
					inquireDlg.closeDialog();
					BusinessMannager.getInstance().sendRequestMessage(
							MessageUti.UPDATE_SET_DEVICE_START_UPDATE, null);
				}
			});
		}else {
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION, null);
			showCheckFWWaiting(true);

		}
	}

	private void onBtnAppCheck(){
		if (m_blHasNewApp) {
			if (hasGooglePlayAccount()) {
				Intent intent = new Intent(Intent.ACTION_VIEW);							
				intent.setData(Uri.parse("market://details?id=com.alcatel.smartlinkv3"));							
				startActivity(intent);
			}else {
				Toast.makeText(this, R.string.setting_upgrade_config_google_play, Toast.LENGTH_LONG).show();
			}
		}else {
			checkNewVersion();
		}
	}

	private boolean hasGooglePlayAccount(){
		boolean blHas = false;
		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccounts();
		for (Account account:accounts) {
			Log.d("Account", "account.name="+account.name);
			Log.d("Account", "account.type="+account.type);
			if (account.type.equalsIgnoreCase("com.google")) {
				blHas = true;
				break;
			}
		}
		return blHas;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));

		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_DEVICE_START_UPDATE));
		
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_DEVICE_START_FOTA_UPDATE));
		
		
		
		//UPDATE_SET_FOTA_START_Download

		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET));
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET, null);
		m_pb_waiting.setVisibility(View.VISIBLE);
		setNewDeviceVersion("");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		if(intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				//do nothing
			}else {
				showCheckFWWaiting(false);
				String strNew = getString(R.string.setting_upgrade_set_check_new_version_failed);
				setNewDeviceVersion(strNew);

			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				//do nothing
				m_pb_waiting.setVisibility(View.GONE);
			}else {
				m_pb_waiting.setVisibility(View.GONE);
				Toast.makeText(this, R.string.setting_upgrade_start_update_failed, Toast.LENGTH_SHORT).show();
			}
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				updateNewDeviceInfo(true);
			}else {
				showCheckFWWaiting(false);
				String strNew = getString(R.string.setting_upgrade_check_failed);
				setNewDeviceVersion(strNew);

			}
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_START_UPDATE)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()){
				//do nothing
			}else {
				ShowWaiting(false);
				Toast.makeText(this, R.string.setting_upgrade_start_update_failed, Toast.LENGTH_SHORT).show();
			}
		}

		if(intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()){
				//do nothing
				DeviceUpgradeStateInfo info = BusinessMannager.getInstance().getUpgradeStateInfo();
				EnumDeviceUpgradeStatus status = EnumDeviceUpgradeStatus.build(info.getStatus());
				int nProgress = info.getProcess();
				m_pb_waiting.setProgress(nProgress);
				if (EnumDeviceUpgradeStatus.DEVICE_UPGRADE_NOT_START == status){
					ShowWaiting(false);
					Toast.makeText(this, R.string.setting_upgrade_not_start, Toast.LENGTH_SHORT).show();
				}else if (EnumDeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE == status) {
					ShowWaiting(false);
					if (!FeatureVersionManager.getInstance().
							isSupportApi("System", "AccessSqliteDB")) 
					{
						Toast.makeText(this, R.string.setting_upgrade_complete, Toast.LENGTH_SHORT).show();
					}
					else
					{
						BusinessMannager.getInstance().sendRequestMessage(MessageUti.UPDATE_SET_DEVICE_START_FOTA_UPDATE, null);
						Toast.makeText(this, R.string.setting_upgrade_complete, Toast.LENGTH_SHORT).show();
					}
				}else if (EnumDeviceUpgradeStatus.DEVICE_UPGRADE_UPDATING == status) {
					m_nUpdradeFWProgress = info.getProcess();
					String strProgress = m_nUpdradeFWProgress+"%";
					m_tv_update_progress.setText(strProgress);
				}
			}else {
				ShowWaiting(false);
//				Toast.makeText(this, R.string.setting_upgrade_get_update_state_failed, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void setNewDeviceVersion(String strNewVesion){
		m_tv_new_firmware_version.setText(strNewVesion);
	}

	private void updateNewDeviceInfo(boolean blNeedBackupNewVersionInfo){
		DeviceNewVersionInfo info = BusinessMannager.getInstance().getNewFirmwareInfo();
		int nState = info.getState();
		EnumDeviceCheckingStatus eStatus = EnumDeviceCheckingStatus.build(nState);
		if (EnumDeviceCheckingStatus.DEVICE_CHECKING == eStatus) {
			//waiting
			showCheckFWWaiting(true);
		}else if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == eStatus) {
			m_blHasNewFirmware = true;
			String strNew = getString(R.string.setting_upgrade_new_device_version);
			strNew += info.getVersion();
			if (blNeedBackupNewVersionInfo) {
				m_strNewFirmwareInfo = strNew;
			}
			m_btn_check_firmware.setText(R.string.setting_upgrade_btn_upgrade);
			showCheckFWWaiting(false);
		}else if (EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION == eStatus) {
			showCheckFWWaiting(false);
			String strNew = getString(R.string.setting_upgrade_no_new_version);
			if (blNeedBackupNewVersionInfo) {
				m_strNewFirmwareInfo = strNew;
			}
		}else if (EnumDeviceCheckingStatus.DEVICE_NO_CONNECT == eStatus) {
			showCheckFWWaiting(false);
			String strNew = getString(R.string.setting_upgrade_no_connection);
			if (blNeedBackupNewVersionInfo) {
				m_strNewFirmwareInfo = strNew;
			}
		}else if (EnumDeviceCheckingStatus.DEVICE_NOT_AVAILABLE == eStatus) {
			showCheckFWWaiting(false);
			String strNew = getString(R.string.setting_upgrade_not_available);
			if (blNeedBackupNewVersionInfo) {
				m_strNewFirmwareInfo = strNew;
			}
		}else if (EnumDeviceCheckingStatus.DEVICE_CHECK_ERROR == eStatus) {
			showCheckFWWaiting(false);
			String strNew = getString(R.string.setting_upgrade_check_error);
			if (blNeedBackupNewVersionInfo) {
				m_strNewFirmwareInfo = strNew;
			}
		}else {
			showCheckFWWaiting(false);
		}
		setNewDeviceVersion(m_strNewFirmwareInfo);
	}

	private void checkNewVersion(){
		
		showCheckAppWaiting(true);
		//when checking new version, clear last checking result.
		m_tv_new_app_version.setText("");

		new Thread() {
			public void run() {

				HttpParams myParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(myParams, 2000);
				HttpConnectionParams.setSoTimeout(myParams, 2000);
				HttpClient httpclient = new DefaultHttpClient(myParams);

				HttpPost post = new HttpPost("https://play.google.com/store/apps/details?id=com.alcatel.smartlinkv3");

				HttpResponse response;
				int nMsgId = 0;
				String strNewVersionString = "";
				try {
					response = httpclient.execute(post);
					int nStatusCode = response.getStatusLine().getStatusCode();
					if (nStatusCode == HttpStatus.SC_OK) {					
						String strRes = EntityUtils.toString(response.getEntity(), "utf-8");	
						int nPos = strRes.indexOf("softwareVersion");						
						if (nPos != -1) {
							Log.e("@@@", strRes);
							strRes = strRes.substring(nPos);
							int start = strRes.indexOf(">");						
							int end = strRes.indexOf("<");
							strRes = strRes.substring(start+1, end);			
							strRes = strRes.trim();
							Log.e("@@@", strRes);
							nMsgId = MSG_GET_NEW_VERSION;
							strNewVersionString = strRes;
						}

					}
				}  catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (Exception e) {
					e.printStackTrace();
				}

				Message msg = new Message();
				msg.what = nMsgId;
				Bundle data = new Bundle();
				data.putString("version", strNewVersionString);
				msg.setData(data);
				handler.sendMessage(msg);
				
			}
		}.start();
	}

	private void showStopUpdateDialog(){
		final InquireDialog inquireDlg = new InquireDialog(this);
		inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
		inquireDlg.m_contentTextView.setGravity(Gravity.LEFT);
		inquireDlg.m_contentTextView
		.setText(R.string.setting_upgrade_firmware_warning);
		inquireDlg.m_contentDescriptionTextView.setText("");
		inquireDlg.m_confirmBtn
		.setBackgroundResource(R.drawable.selector_common_button);
		inquireDlg.m_confirmBtn.setText(R.string.ok);
		inquireDlg.showDialog(new OnInquireApply() {

			@Override
			public void onInquireApply() {
				// TODO Auto-generated method stub
				//upgrade firmware
				ShowWaiting(true);
				inquireDlg.closeDialog();
				BusinessMannager.getInstance().sendRequestMessage(
						MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE, null);
				SettingUpgradeActivity.this.finish();
			}
		});
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			showCheckAppWaiting(false);
			switch (msg.what) {
			case MSG_GET_NEW_VERSION:
				String strNewVersion = msg.getData().getString("version");
				String currVer = BusinessMannager.getInstance().getAppVersion();		
				m_tv_new_app_version.setText(getString(R.string.setting_upgrade_new_app_version)+
						msg.getData().getString("version"));												
				if(currVer.compareToIgnoreCase(strNewVersion) < 0)
				{
					m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_upgrade);
					m_blHasNewApp = true;
				}else {
					m_tv_new_app_version.setText(R.string.setting_upgrade_no_new_version);
					m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_check);
					m_blHasNewApp = false;
				}
				break;

			default:
				m_tv_new_app_version.setText(R.string.setting_upgrade_check_failed);
				m_btn_upgrade_app.setText(R.string.setting_upgrade_btn_check);
				m_blHasNewApp = false;
				break;
			}
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v("UpgradeActivity", "OnKeyDown:"+event.getKeyCode());
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			onBtnBack();
		}
		return super.onKeyDown(keyCode, event);
	}

}
