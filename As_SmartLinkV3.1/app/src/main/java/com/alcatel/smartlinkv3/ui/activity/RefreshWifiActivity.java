package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.MessageUti;

public class RefreshWifiActivity extends Activity implements OnClickListener {
	private ImageView m_connectImage = null;
	private TextView m_connectTitle = null;
	private TextView m_connectTip = null;
	private Button m_connectBtn1 = null;
	//private Button m_connectBtn2 = null;

	protected MsgBroadcastReceiver m_msgReceiver;

    private Dialog mTipsDialog;
    private   long              mkeyTime; //点击2次返回 键的时间
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh);
		getWindow().setBackgroundDrawable(null);
		m_connectImage = (ImageView)this.findViewById(R.id.image_connection);
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

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mkeyTime) > 2000) {
            mkeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    private void clickBtn1() {
			wifiSetting();
	}
	
	
	private class MsgBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {	    	
	    	if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
	    		showUI();
	    		showActivity();
	    		Log.d("refreshsd", "showActivity");
	    	}else if(intent.getAction().equals(MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET)) {
	    		showUI();
	    	}
        }  	
	}

	public boolean isNoAnyConnection() {
		return !DataConnectManager.getInstance().getWifiConnected();
	}
	
	private void showUI() {
		if(isNoAnyConnection()) {
			m_connectImage.setBackgroundResource(R.drawable.device_ic);
			m_connectBtn1.setText(R.string.refresh);
		}
	}
	
	private void showActivity() {
		
		boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		
		if(!bCPEWifiConnected)
		  return;
		
		Class<?> clazz;
		if (!CPEConfig.getInstance().getQuickSetupFlag()) {
//		  clazz = QuickSetupActivity.class;
            clazz = ConnectTypeSelectActivity.class;
		} else {	
		  clazz = MainActivity.class;				
			Log.d("refreshsd", "startMainActivity");
		}
			 
	    Intent it = new Intent(this, clazz);
	    startActivity(it);
	    finish();	  
	}

	private void wifiSetting() {
		try {
    		this.unregisterReceiver(m_msgReceiver);
    	}catch(Exception e) {
    		
    	}
		
		Intent intent;
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

       	showActivity();
		
		showUI();

        dismissTipsDialog();
        showUpgradeDialog();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
    		this.unregisterReceiver(m_msgReceiver);
    	}catch(Exception e) {
    		
    	}
	}

    private void showUpgradeDialog() {
        mTipsDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mTipsDialog.setCanceledOnTouchOutside(false);
        mTipsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout deleteDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_get_connected, null);

        TextView okBtn = (TextView) deleteDialogLLayout.findViewById(R.id.get_connected_ok_btn);

        mTipsDialog.setContentView(deleteDialogLLayout);
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissTipsDialog();
//                clickBtn1();
            }
        });
        mTipsDialog.show();
    }

    private void dismissTipsDialog() {
        if (mTipsDialog != null && mTipsDialog.isShowing()) {
            mTipsDialog.dismiss();
        }
    }
}
