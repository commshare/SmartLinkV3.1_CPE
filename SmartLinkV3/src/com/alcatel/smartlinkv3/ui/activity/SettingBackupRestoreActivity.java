package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.EnumRestoreErrorStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingBackupRestoreActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_backup=null;
	private Button m_btn_restore=null;
	private ProgressBar m_pbWaitingBar=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_backup);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		prepareTitlebar();
		createControls();
		ShowWaiting(false);
	}

	private void prepareTitlebar(){
		m_tv_titleTextView = (TextView)findViewById(R.id.tv_title_title);
		m_tv_titleTextView.setText(R.string.setting_backup);
		//back button and text
				m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
				m_tv_back = (TextView)findViewById(R.id.tv_title_back);
				m_ib_back.setOnClickListener(this);
				m_tv_back.setOnClickListener(this);
	}
	
	private void createControls(){
		m_btn_backup = (Button)findViewById(R.id.btn_backup);
		m_btn_restore = (Button)findViewById(R.id.btn_restore);
		m_btn_backup.setOnClickListener(this);
		m_btn_restore.setOnClickListener(this);
		m_pbWaitingBar=(ProgressBar)findViewById(R.id.pb_backup_waiting_progress);
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pbWaitingBar.setVisibility(View.VISIBLE);
		}else {
			m_pbWaitingBar.setVisibility(View.GONE);
		}
		m_btn_backup.setEnabled(!blShow);
		m_btn_restore.setEnabled(!blShow);
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			SettingBackupRestoreActivity.this.finish();
			break;

		case R.id.btn_backup:
			onBtnBackup();
			ShowWaiting(true);
			break;
			
		case R.id.btn_restore:
			onBtnRestore();
			ShowWaiting(true);
			break;
		default:
			break;
		}
	}
	
	private void onBtnBackup(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_APP_BACKUP, null);
	}
	
	private void onBtnRestore(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP, null);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_APP_BACKUP));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
		if (SIMState.Accessable != sim.m_SIMState) {
			m_btn_backup.setEnabled(false);
			m_btn_restore.setEnabled(false);
		}else {
			m_btn_backup.setEnabled(true);
			m_btn_restore.setEnabled(true);
		}
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
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_APP_BACKUP)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_backup_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				strTost = getString(R.string.setting_backup_success);
			}
			
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			ShowWaiting(false);
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_restore_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				RestoreError info = BusinessMannager.getInstance().getRestoreError();
				int nErrorStatus = info.getRestoreError();
				EnumRestoreErrorStatus status = EnumRestoreErrorStatus.build(nErrorStatus);
				if (status == 
						EnumRestoreErrorStatus.RESTORE_ERROR_NO_BACKUP_FILE) {
					strTost = getString(R.string.setting_restore_no_backup_file);
				}else if (EnumRestoreErrorStatus.RESTORE_ERROR_SUCCESSFUL == status) {
					strTost = getString(R.string.setting_restore_success);
				}
			}
			
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			ShowWaiting(false);
		}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					m_btn_backup.setEnabled(true);
					m_btn_restore.setEnabled(true);
				}else{
					m_btn_backup.setEnabled(false);
					m_btn_restore.setEnabled(false);
				}
			}
    	}
	}
}
