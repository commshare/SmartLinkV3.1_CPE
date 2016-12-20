package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.PackageUtils;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RefreshWifiActivity extends Activity implements OnClickListener {
	private ImageView m_connectImage = null;
	private TextView m_connectTitle = null;
	private TextView m_connectTip = null;
	private Button m_connectBtn1 = null;
	//private Button m_connectBtn2 = null;

	protected MsgBroadcastReceiver m_msgReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh);
		m_connectImage = (ImageView)this.findViewById(R.id.image_connection);
		m_connectTitle = (TextView)this.findViewById(R.id.textview_refresh_title);
		m_connectTip = (TextView)this.findViewById(R.id.textview_refresh_tip);
		m_connectBtn1 = (Button)this.findViewById(R.id.btn_refresh);
		m_connectBtn1.setOnClickListener(this);	
	}	

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_refresh://m_connectBtn1
			clickBtn1();
			break;
		default:
			break;
		}
	}
	
	private void clickBtn1() {
		//if(isNoAnyConnection() == true) {
			wifiSetting();
		//}
	}
	
	
	private class MsgBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {	    	
	    	if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
	    		showUI();
	    		showActivity(context);
	    		Log.d("refreshsd", "showActivity");
	    	}else if(intent.getAction().equals(MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET)) {
	    		showUI();
	    	}
        }  	
	}
	
	private boolean isHaveWifi() {
		if(DataConnectManager.getInstance().getWifiConnected() == false) {
			return false;
		}
		
		return true;
	}
	
	public boolean isNoAnyConnection() {
		if(isHaveWifi() == false) {
			return true;
		}
		
		return false;
	}
	
	private void showUI() {
		if(isNoAnyConnection() == true) {
			m_connectImage.setBackgroundResource(R.drawable.no_connection);
			m_connectTitle.setText(R.string.refresh_wifi_title);
			m_connectTip.setText(R.string.refresh_wifi_tip);
			m_connectBtn1.setText(R.string.refresh);
		}
	}
	
	private void showActivity(Context context) {
		
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		
		if(bCPEWifiConnected == true) {	
			startMainActivity();	
			Log.d("refreshsd", "startMainActivity");
		}
	}

	private void wifiSetting() {
		try {
    		this.unregisterReceiver(m_msgReceiver);
    	}catch(Exception e) {
    		
    	}
		
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
		} else {
			intent = new Intent();
			ComponentName component = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			intent.setComponent(component);
			intent.setAction("android.intent.action.VIEW");
		}

		this.startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_msgReceiver = new MsgBroadcastReceiver();
       	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));  
       	this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET)); 

       	showActivity(this);
		
		showUI();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
    		this.unregisterReceiver(m_msgReceiver);
    	}catch(Exception e) {
    		
    	}
	}

	private void startMainActivity() {
		Intent it = new Intent(this, MainActivity.class);
		this.startActivity(it);
		this.finish();
	}
	
}
