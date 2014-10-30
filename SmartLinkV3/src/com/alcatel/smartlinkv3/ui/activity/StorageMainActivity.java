package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.Status;
import com.alcatel.smartlinkv3.common.MessageUti;
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
		layout_no_storage, layout_samba_disable, layout_hard_disc
	}


	private StorageReceiver m_receiver = new StorageReceiver();

	private class StorageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(
					MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET)) {
				m_progressWaiting.setVisibility(View.GONE);
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			} else if (intent.getAction().equals(
					MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					getSambaSettings();
					updateUI();
				}
			}
		}
	}

	private void registerReceiver() {

		this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET));

		this.registerReceiver(m_receiver, new IntentFilter(
				MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_main_view);
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
		m_hardDisc = (RelativeLayout) this
				.findViewById(R.id.item_hard_disc_layout);
		m_hardDisc.setOnClickListener(this);

		m_sambaSwitch = (RelativeLayout) this
				.findViewById(R.id.item_samba_service);
		m_sambaSwitch.setOnClickListener(this);

		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.waiting_progress);

		registerReceiver();
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
		
		Intent it = new Intent(StorageMainActivity.this,
				LocalStorageActivity.class);
		it.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
				LocalStorageActivity.FLAG_SAMBA);	
		 this.startActivity(it);	
	}

	private void updateUI() {

		if (BusinessMannager.getInstance().getStorageList().DeviceList.size() == 0) {
			showCurrentView(LAYOUT_TYPE.layout_no_storage);
		} else {

			if (Status
					.build(BusinessMannager.getInstance().getSambaSettings().SambaStatus) == Status.Enabled) {
				showCurrentView(LAYOUT_TYPE.layout_hard_disc);
			} else {
				showCurrentView(LAYOUT_TYPE.layout_samba_disable);
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

		case layout_hard_disc:
			m_noExtStorage.setVisibility(View.GONE);
			m_sambaDisable.setVisibility(View.GONE);
			m_mediaStorage.setVisibility(View.GONE);
			m_hardDisc.setVisibility(View.VISIBLE);
			break;

		}
	}

	private void enableSamba() {
		m_progressWaiting.setVisibility(View.VISIBLE);
		DataValue data = new DataValue();
		data.addParam("SambaStatus", 1);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET, data);
	}

	private void getSambaSettings() {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET, null);
	}
}
