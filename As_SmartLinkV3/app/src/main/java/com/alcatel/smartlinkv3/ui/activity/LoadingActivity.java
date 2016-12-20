package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public class LoadingActivity extends Activity {

	private static final int TIME_OUT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
	}

	@Override
	protected void onResume() {

		super.onResume();	

		Thread searchingThread = new Thread() {
			@Override
			public void run() {
				if (!checkConnectState())
					searchTimeOut();
				else
					startMainActivity();
			}
		};

		new Handler().postDelayed(searchingThread, TIME_OUT);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void startMainActivity() {

		Intent it = new Intent(this, MainActivity.class);
		this.startActivity(it);
		this.finish();
	}

	private void startRefreshWifiActivity() {

		Intent it = new Intent(this, RefreshWifiActivity.class);
		this.startActivity(it);
		finish();
	}

	private void searchTimeOut() {
		startRefreshWifiActivity();
	}

	private boolean checkConnectState() {
		return DataConnectManager.getInstance().getCPEWifiConnected();
	}

}
