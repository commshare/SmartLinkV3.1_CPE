package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.ServiceState;
import com.alcatel.smartlinkv3.common.ENUM.ServiceType;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.samba.SmbLoginTask;
import com.alcatel.smartlinkv3.samba.SmbUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class StorageMainActivity extends BaseActivity implements
		OnClickListener {
	private Button m_backBtn = null;
	private RelativeLayout m_localStorage = null;

	private RelativeLayout m_noExtStorage = null;
	private LinearLayout m_sambaDisable = null;
	private RelativeLayout m_mediaStorage = null;
	private RelativeLayout m_hardDisc = null;

	private RelativeLayout m_sambaSwitch = null;

	private ProgressBar m_progressWaiting = null;

	private enum LAYOUT_TYPE {
		layout_no_storage, layout_samba_disable, layout_media_box, layout_hard_disc
	}
	
	private boolean m_isSmabaLogining = false;

	// smaba login
	private Handler m_smbLoginTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:			
				Intent it = new Intent(StorageMainActivity.this,
						LocalStorageActivity.class);
				it.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
						LocalStorageActivity.FLAG_SAMBA);
				
				String strRoot = FileUtils.addLastFileSeparator(BusinessMannager.getInstance().getSambaSettings().AccessPath);
				it.putExtra(
						LocalStorageActivity.CURRENT_DIRECTORY, strRoot);
				StorageMainActivity.this.startActivity(it);
				
				m_progressWaiting.setVisibility(View.GONE);				
			    m_isSmabaLogining = false;	
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:				
				m_progressWaiting.setVisibility(View.GONE);
				String strErr =  (String) msg.obj;			
				Toast.makeText(StorageMainActivity.this, strErr,
						Toast.LENGTH_LONG).show();
				m_isSmabaLogining = false;
				break;
			}
		}
	};

	private StorageReceiver m_receiver = new StorageReceiver();

	private class StorageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

/*			if (intent.getAction().equals(
					MessageUti.SERVICE_SET_SERVICE_STATE_REQUSET)) {

				m_progressWaiting.setVisibility(View.GONE);
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			} else if (intent.getAction().equals(
					MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					getSambaSettings();
					updateUI();
				}
			}

			else if (intent.getAction().equals(
					MessageUti.SERVICE_GET_SAMBA_SETTING_REQUSET)) {
				{
					updateUI();
				}
			}

			else if (intent.getAction().equals(
					MessageUti.SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					getSambaState();
					getSambaSettings();
				}
				updateUI();
			}*/
		}
	}

	private void registerReceiver() {
		// advanced
	/*	this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SERVICE_SET_SERVICE_STATE_REQUSET));

		this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET));

		this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SERVICE_GET_SAMBA_SETTING_REQUSET));

		this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET));*/
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m100_storage_main_view);
		m_bNeedBack = false;

		// get controls
		m_backBtn = (Button) this.findViewById(R.id.btn_back);
		m_backBtn.setOnClickListener(this);
		m_localStorage = (RelativeLayout) this
				.findViewById(R.id.local_storage_layout);
		m_localStorage.setOnClickListener(this);

		m_noExtStorage = (RelativeLayout) this
				.findViewById(R.id.item_no_ext_storage);
		m_sambaDisable = (LinearLayout) this
				.findViewById(R.id.samba_disable_layout);
		m_mediaStorage = (RelativeLayout) this
				.findViewById(R.id.item_media_box_layout);
		m_mediaStorage.setOnClickListener(this);
		m_hardDisc = (RelativeLayout) this
				.findViewById(R.id.item_hard_disc_layout);
		m_hardDisc.setOnClickListener(this);

		m_sambaSwitch = (RelativeLayout) this
				.findViewById(R.id.item_samba_service);
		m_sambaSwitch.setOnClickListener(this);

		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.waiting_progress);

		registerReceiver();
		getSambaState();
		getSambaSettings();

	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		super.onBroadcastReceive(context, intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(m_receiver);
	}

	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.local_storage_layout:
			onLocalStorageClick();
			break;

		case R.id.item_samba_service:
			enableSamba();
			break;

		case R.id.item_hard_disc_layout:
		case R.id.item_media_box_layout:
			onSambaClick();
			break;
		}
	}

	private void onLocalStorageClick() {
		Intent it = new Intent(this, LocalStorageActivity.class);
		it.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
				LocalStorageActivity.FLAG_LOCAL);
		this.startActivity(it);
	}

	private void onSambaClick() {
		
		if(!m_isSmabaLogining)
		{		
			m_isSmabaLogining = true;
			m_progressWaiting.setVisibility(View.VISIBLE);
			if (FeatureVersionManager.getInstance().isSupportDevice(
					FeatureVersionManager.VERSION_DEVICE_M100) == true) {	
				
				String name = BusinessMannager.getInstance().getSambaSettings().UserName;
				String password = BusinessMannager.getInstance().getSambaSettings().Password;
				SmbLoginTask task = new SmbLoginTask(name, password, m_smbLoginTaskHandler);
				task.start();			
			}
			else
			{
				SmbLoginTask task = new SmbLoginTask("admin", CPEConfig.getInstance()
						.getLoginPassword(), m_smbLoginTaskHandler);			
				task.start();		
			}	
		}	
	}

	private void updateUI() {
		if (FeatureVersionManager.getInstance().isSupportDevice(
				FeatureVersionManager.VERSION_DEVICE_M100) == true) {
			showCurrentView(LAYOUT_TYPE.layout_media_box);
		} else {

			if (BusinessMannager.getInstance().getStorageList().DeviceList
					.size() == 0) {
				showCurrentView(LAYOUT_TYPE.layout_no_storage);
			} else {

//				if (BusinessMannager.getInstance().getSambaServiceState() == ServiceState.Enabled) {
//					showCurrentView(LAYOUT_TYPE.layout_hard_disc);
//				} else {
//					showCurrentView(LAYOUT_TYPE.layout_samba_disable);
//				}
			}
		}
	}

	private void showCurrentView(LAYOUT_TYPE type) {
		switch (type) {
		case layout_no_storage:
			m_noExtStorage.setVisibility(View.VISIBLE);
			m_sambaDisable.setVisibility(View.GONE);
			m_mediaStorage.setVisibility(View.GONE);
			m_hardDisc.setVisibility(View.GONE);
			break;

		case layout_samba_disable:
			m_noExtStorage.setVisibility(View.GONE);
			m_sambaDisable.setVisibility(View.VISIBLE);
			m_mediaStorage.setVisibility(View.GONE);
			m_hardDisc.setVisibility(View.GONE);
			break;

		case layout_media_box:
			m_noExtStorage.setVisibility(View.GONE);
			m_sambaDisable.setVisibility(View.GONE);
			m_mediaStorage.setVisibility(View.VISIBLE);
			m_hardDisc.setVisibility(View.GONE);
			break;

		case layout_hard_disc:
			m_noExtStorage.setVisibility(View.GONE);
			m_sambaDisable.setVisibility(View.GONE);
			m_mediaStorage.setVisibility(View.GONE);
			m_hardDisc.setVisibility(View.VISIBLE);
			break;

		}
	}

	private void enableSamba() {
	/*	m_progressWaiting.setVisibility(View.VISIBLE);
		DataValue data = new DataValue();
		data.addParam("ServiceType", ServiceType.Samba.ordinal());
		data.addParam("State", ServiceState.Enabled.ordinal());
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SERVICE_SET_SERVICE_STATE_REQUSET, data);*/
	}

	private void getSambaState() {
	/*	DataValue samba = new DataValue();
		samba.addParam("ServiceType", ServiceType.Samba.ordinal());
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET, samba);*/

	}

	private void getSambaSettings() {
		/*BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SERVICE_GET_SAMBA_SETTING_REQUSET, null);*/
	}
}
