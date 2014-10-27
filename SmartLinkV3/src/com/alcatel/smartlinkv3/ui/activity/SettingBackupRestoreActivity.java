package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingBackupRestoreActivity extends Activity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private Button m_btn_backup=null;
	private Button m_btn_restore=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_backup);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		prepareTitlebar();
		createControls();
		
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
			break;
			
		case R.id.btn_restore:
			onBtnRestore();
			break;
		default:
			break;
		}
	}
	
	private void onBtnBackup(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_BACKUP, null);
	}
	
	private void onBtnRestore(){
		String strFile="";
		DataValue data = new DataValue();
		data.addParam("FileName", strFile);
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_RESTORE, data);
	}
}
