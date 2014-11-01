package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.ui.activity.StorageMainActivity;
import com.alcatel.smartlinkv3.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class SdSharingActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout m_layoutStorage = null;
	private SdSharingReceiver m_sdSharingReceiver = null;
	private Button m_btnDlna = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sd_sharing_view);

		m_layoutStorage = (RelativeLayout) findViewById(R.id.layout_storage);
		m_layoutStorage.setOnClickListener(this);

		m_btnDlna = (Button) this.findViewById(R.id.enable_dlna_btn);
		m_btnDlna.setOnClickListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver();
		getDlnaSettings();
		getSDCardSpace();
		getSambaSettings();
	}

	@Override
	public void onPause() {
		super.onPause();
		this.unregisterReceiver(m_sdSharingReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
	
		switch (v.getId()) {
		case R.id.layout_storage:
			Intent intent = new Intent(this, StorageMainActivity.class);
			startActivity(intent);
			break;

		case R.id.enable_dlna_btn:
			onDlnaClick();
			break;

		default:
			break;
		}
	}

	private void onDlnaClick() {
		DlnaSettings settings = BusinessMannager.getInstance()
				.getDlnaSettings();
	
		if (settings.DlnaStatus == 0) {
			settings.DlnaStatus = 1;
		} else {
			settings.DlnaStatus = 0;
		}

		DataValue data = new DataValue();
		data.addParam("DlnaStatus", settings.DlnaStatus);
		data.addParam("DlnaName", settings.DlnaName);
		setDlnaSettings(data);
	}

	private void setDlnaSettings(DataValue data) {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
	}

	private void getDlnaSettings() {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, null);
	}

	private void showDlnaSettings() {
		DlnaSettings settings = BusinessMannager.getInstance()
				.getDlnaSettings();
		if (settings.DlnaStatus == 0) {
			m_btnDlna.setBackgroundResource(R.drawable.switch_off);
		} else {
			m_btnDlna.setBackgroundResource(R.drawable.switch_on);
		}
	}
	
	private void getSDCardSpace()
	{
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET, null);	
	}
	
	private void getSambaSettings()
	{
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET, null);	
	}
	

	private void registerReceiver() {
		m_sdSharingReceiver = new SdSharingReceiver();

		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));

		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_SET_DLNA_SETTING_REQUSET));
		
		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET));		
		
		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET));	
		
		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET));	
		
		this.registerReceiver(m_sdSharingReceiver, new IntentFilter(
				MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET));	

	}

	private class SdSharingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(
					MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)
					|| intent.getAction().equals(
							MessageUti.SHARING_SET_DLNA_SETTING_REQUSET)) {
				showDlnaSettings();
			} else if (intent.getAction().equals(
					MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET)) {
				//TODO				
			}

		}
	}

}