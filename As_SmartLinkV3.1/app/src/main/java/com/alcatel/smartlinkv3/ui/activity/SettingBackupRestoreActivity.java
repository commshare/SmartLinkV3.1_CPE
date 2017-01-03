package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.EnumRestoreErrorStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class SettingBackupRestoreActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private ProgressBar m_pbWaitingBar=null;
	private boolean  m_bRestore = false;
    private TextView mBackupTv;
    private TextView mRestoreTv;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_backup);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_3);
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
        mBackupTv = (TextView) findViewById(R.id.device_backup_backup);
        mRestoreTv = (TextView) findViewById(R.id.device_backup_restore);
        mBackupTv.setOnClickListener(this);
        mRestoreTv.setOnClickListener(this);
		m_pbWaitingBar=(ProgressBar)findViewById(R.id.pb_backup_waiting_progress);
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pbWaitingBar.setVisibility(View.VISIBLE);
		}else {
			m_pbWaitingBar.setVisibility(View.GONE);
		}
        mBackupTv.setClickable(!blShow);
		if(m_bRestore)
		{
            mRestoreTv.setClickable(false);
		}
		else
		{
            mRestoreTv.setClickable(!blShow);
		}
		
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

		case R.id.device_backup_backup:
			onBtnBackup();
			ShowWaiting(true);
			break;
			
		case R.id.device_backup_restore:
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
        mRestoreTv.setClickable(false);
		m_bRestore = true;
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
            mBackupTv.setClickable(false);
            mRestoreTv.setClickable(false);
            Toast.makeText(getApplicationContext(), getString(R.string.Home_no_sim), Toast.LENGTH_SHORT).show();
		}else {
            mBackupTv.setClickable(true);
            mRestoreTv.setClickable(true);
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
                    mRestoreTv.setClickable(true);
				}else if (EnumRestoreErrorStatus.RESTORE_ERROR_SUCCESSFUL == status) {
					strTost = getString(R.string.setting_restore_success);
				}
			}
			else
			{
				m_bRestore = false;
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
                    mRestoreTv.setClickable(true);
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
                    mBackupTv.setClickable(true);
					if(m_bRestore)
					{
                        mRestoreTv.setClickable(false);
					}
					else
					{
                        mRestoreTv.setClickable(true);
					}
				}else{
                    mBackupTv.setClickable(false);
                    mRestoreTv.setClickable(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.Home_no_sim), Toast.LENGTH_SHORT).show();
				}
			}
    	}
	}
}
