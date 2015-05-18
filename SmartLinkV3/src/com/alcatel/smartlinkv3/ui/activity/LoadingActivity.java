package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class LoadingActivity extends Activity {
	private final int SPLASH_DISPLAY_INTERNAL = 1000;
	private final String SHAREDPREFERENCES_NAME = "first_pref";
	boolean isFirstIn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		checkPreference();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void checkPreference() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		if (isFirstIn) {
			goGuide();
		} else {
			goHome();
		}
	}

	private void goHome() {
		Thread searchingThread = new Thread() {
			@Override
			public void run() {
				if (!checkConnectState())
					searchTimeOut();
				else
					startMainActivity();
			}
		};

		new Handler().postDelayed(searchingThread, SPLASH_DISPLAY_INTERNAL);
	}

	private void goGuide() {
		Intent intent = new Intent(LoadingActivity.this, GuideActivity.class);
		LoadingActivity.this.startActivity(intent);
		LoadingActivity.this.finish();
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
