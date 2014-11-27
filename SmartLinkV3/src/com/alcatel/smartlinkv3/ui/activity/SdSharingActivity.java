package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.business.sharing.SambaSettings;
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
	private Button m_btnSamba = null;
	private ImageButton m_btnback = null;
	private TextView m_tvback = null;
	private TextView m_tvDlnaName = null;
	private TextView m_tvSdcardUsage = null;
	private TextView m_tvSdcardStatus = null;
	private ProgressBar m_sdcardProgress;
	private ProgressBar m_progressWaiting = null;
	private ProgressBar m_sambaProgressWaiting = null;
	
	
	private boolean m_dlnaClicked = false;
	private boolean m_sambaClicked = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sd_sharing_view);
		m_bNeedBack = false;

		m_layoutStorage = (RelativeLayout) findViewById(R.id.layout_storage);
		m_layoutStorage.setOnClickListener(this);

		m_btnDlna = (Button) this.findViewById(R.id.enable_dlna_btn);
		m_btnDlna.setOnClickListener(this);
		
		m_btnSamba = (Button) this.findViewById(R.id.enable_samba_btn);
		m_btnSamba.setOnClickListener(this);

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
		m_sambaProgressWaiting = (ProgressBar) this.findViewById(R.id.samba_waiting_progress);
		m_sambaProgressWaiting.setVisibility(View.GONE);
		
		m_progressWaiting = (ProgressBar) this.findViewById(R.id.waiting_progress);
		m_progressWaiting.setVisibility(View.GONE);

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
		m_progressWaiting.setVisibility(View.GONE);
		m_sambaProgressWaiting.setVisibility(View.GONE);
		m_dlnaClicked = false;
		m_sambaClicked = false;
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
			
		case R.id.enable_samba_btn:
			onSambaClick();
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
	
	private void onSambaClick() {
		if(m_dlnaClicked == true || m_sambaClicked == true) 
			return;
		m_sambaClicked = true;
		
		m_sambaProgressWaiting.setVisibility(View.VISIBLE);
		SambaSettings settings = BusinessMannager.getInstance().getSambaSettings();
		
		if(settings.SambaStatus == 0) { //0: disable
			DataValue dataSamba = new DataValue();
			dataSamba.addParam("SambaStatus", 1);
			BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET, dataSamba);
			
			DlnaSettings dlnaSettings = BusinessMannager.getInstance().getDlnaSettings();
			DataValue dataDlna = new DataValue();
			dataDlna.addParam("DlnaName", dlnaSettings.DlnaName);
			dataDlna.addParam("DlnaStatus", 0);
			BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, dataDlna);
		}else{
			DataValue dataSamba = new DataValue();
			dataSamba.addParam("SambaStatus", 0);
			BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET, dataSamba);
		}
	
	}

	private void onDlnaClick() {
		if(m_dlnaClicked == true || m_sambaClicked == true) 
			return;
		m_dlnaClicked = true;
		m_progressWaiting.setVisibility(View.VISIBLE);
		DlnaSettings settings = BusinessMannager.getInstance().getDlnaSettings();
		DataValue dataDlna = new DataValue();
		dataDlna.addParam("DlnaName", settings.DlnaName);
		if (settings.DlnaStatus == 0) {			
			dataDlna.addParam("DlnaStatus", 1);			
			setDlnaSettings(dataDlna);			
			
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, dataDlna);				
		
			DataValue dataSamba = new DataValue();
			dataSamba.addParam("SambaStatus", 0);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET, dataSamba);
			
			
		} else {
			settings.DlnaStatus = 0;
			dataDlna.addParam("DlnaStatus", settings.DlnaStatus);			
			setDlnaSettings(dataDlna);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, dataDlna);	
		}

	
	}

	private void setDlnaSettings(DataValue data) {
		
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
	
	private void showSambaSettings() {
		SambaSettings settings = BusinessMannager.getInstance().getSambaSettings();
		if (settings.SambaStatus == 0) {
			m_btnSamba.setBackgroundResource(R.drawable.switch_off);
		} else {
			m_btnSamba.setBackgroundResource(R.drawable.switch_on);
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
		if (settings.DlnaName.length() == 0) {
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

			if (intent.getAction().equals(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showDlnaSettings();
				}
			}else if ( intent.getAction().equals(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET)) {
				m_dlnaClicked = false;
				m_progressWaiting.setVisibility(View.GONE);

				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showDlnaSettings();
				}
			}else if (intent.getAction().equals(MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showSambaSettings();
				}
			}else if ( intent.getAction().equals(MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET)) {
				m_sambaClicked = false;
				m_sambaProgressWaiting.setVisibility(View.GONE);

				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					showSambaSettings();
				}
			}else if (intent.getAction().equals(
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