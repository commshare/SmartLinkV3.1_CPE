package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.Status;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SdSharingActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout m_layoutStorage = null;
	private SdSharingReceiver m_sdSharingReceiver = null;
	private Button m_btnDlna = null;
	private ImageButton m_btnback = null;
	private TextView m_tvback = null;
	private TextView m_tvDlnaName = null;
	private TextView m_tvSdcardUsage = null;
	private TextView m_tvSdcardStatus = null;
	private ProgressBar m_sdcardProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sd_sharing_view);
		m_bNeedBack = false;

		m_layoutStorage = (RelativeLayout) findViewById(R.id.layout_storage);
		m_layoutStorage.setOnClickListener(this);

		m_btnDlna = (Button) this.findViewById(R.id.enable_dlna_btn);
		m_btnDlna.setOnClickListener(this);

		m_tvback = (TextView) this.findViewById(R.id.Back);
		m_tvback.setOnClickListener(this);

		m_btnback = (ImageButton) this.findViewById(R.id.btn_back);
		m_btnback.setOnClickListener(this);

		m_tvDlnaName = (TextView) this.findViewById(R.id.tv_dlna_name);
		m_tvSdcardUsage = (TextView) this.findViewById(R.id.tv_sdcard_usage);

		m_tvSdcardStatus = (TextView) this
				.findViewById(R.id.sdcard_sharing_status);
		String format = this.getResources().getString(R.string.sdcard_usage);
		String usage;
		usage = String.format(format, 0, 0);
		m_tvSdcardUsage.setText(usage);
		
		m_sdcardProgress = (ProgressBar) this.findViewById(R.id.sdcard_usage_progress);

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
		getSambaSettings();
		getSDCardSpace();
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
			onStorageClick();
			break;

		case R.id.enable_dlna_btn:
			onDlnaClick();
			break;

		case R.id.Back:
		case R.id.btn_back:
			onBackClick();
			break;

		default:
			break;
		}
	}

	private void onStorageClick() {
		Intent intent = new Intent(this, StorageMainActivity.class);
		startActivity(intent);
	}

	private void onBackClick() {
		this.finish();
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

	private void showSdcardUsage() {
		SDCardSpace space = BusinessMannager.getInstance().getSDCardSpace();
		String format = SdSharingActivity.this.getResources().getString(
				R.string.sdcard_usage);
		String usage = String.format(format, space.UsedSpace, space.TotalSpace);
		m_tvSdcardUsage.setText(usage);		
		float used = Float.parseFloat(space.UsedSpace);
		float total = Float.parseFloat(space.TotalSpace);
		
		if(total > 0.1)
		{
			m_tvSdcardStatus.setVisibility(View.GONE);
			float nPos = ( used /total ) * 100;		
			m_sdcardProgress.setProgress((int) nPos);	
		}
		else
		{
			m_tvSdcardStatus.setVisibility(View.VISIBLE);
			format = this.getResources()
					.getString(R.string.sdcard_usage);
			usage = String.format(format, "0", "0");
			m_tvSdcardUsage.setText(usage);	
		}
		
		

	}

	private void showDlnaSettings() {
		DlnaSettings settings = BusinessMannager.getInstance()
				.getDlnaSettings();
		if (settings.DlnaStatus == 0) {
			m_btnDlna.setBackgroundResource(R.drawable.switch_off);
		} else {
			m_btnDlna.setBackgroundResource(R.drawable.switch_on);
		}

		String format = this.getResources()
				.getString(R.string.dlna_name);
		String description;
		if (settings.DlnaName.isEmpty()) {
			description = String.format(format, BusinessMannager.getInstance()
					.getSystemInfoModel().getDeviceName());
		} else {
			description = String.format(format, settings.DlnaName);
		}

		m_tvDlnaName.setText(description);

	}

	private void getSDCardSpace() {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET, null);
	}

	private void getSambaSettings() {
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

				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showDlnaSettings();
				}
			} else if (intent.getAction().equals(
					MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showSdcardUsage();
				}
			} else if (intent.getAction().equals(
					MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					
					 if(Status.build(BusinessMannager.getInstance().getSDCardStatus().SDcardStatus)  == Status.Disabled)
					 {						
						m_tvSdcardStatus.setVisibility(View.VISIBLE);
						String format = SdSharingActivity.this.getResources()
								.getString(R.string.sdcard_usage);
						String usage = String.format(format, "0", "0");
						m_tvSdcardUsage.setText(usage);						
						m_sdcardProgress.setProgress(0);
					} else {						
						m_tvSdcardStatus.setVisibility(View.GONE);
						getSDCardSpace();
					}
				}
			}
		}
	}

}