package com.alcatel.ui.view;



import com.alcatel.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class ViewSetting extends BaseViewImpl implements OnClickListener {
	
	
	
	private TextView m_tvVersion = null;
	

	private void registerReceiver() {}

	public ViewSetting(Context context) {
		super(context);
		init();
		
		 

	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_setting,
				null);
		//scroll = (FriendlyScrollView) m_view.findViewById(R.id.scroll);
	}

	@Override
	public void onResume() {
		registerReceiver();
		getWlanSettings();
		getServiceState();
		getDeviceInfo();
	}

	@Override
	public void onPause() {}

	@Override
	public void onDestroy() {
	}

	@Override
	public void onClick(View v) {	

	
	}	
	
	private void getServiceState() {}
	
	private void getServiceSettings() {}
	
	private void getWlanSettings() {}
	
	private void getDeviceInfo()
	{}
	
	private void launchPrivateCloudApp()
	{}
	
}
