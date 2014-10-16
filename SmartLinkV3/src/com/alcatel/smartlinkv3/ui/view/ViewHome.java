package com.alcatel.smartlinkv3.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.ActivityDeviceManager;
import com.alcatel.smartlinkv3.ui.activity.ActivitySmsDetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ViewHome extends BaseViewImpl implements OnClickListener {
	private static final String TAG = ViewHome.class.getSimpleName();
	
	private RelativeLayout m_accessDeviceLayout;
	
	public ViewHome(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_home,null);
		m_accessDeviceLayout = (RelativeLayout)m_view.findViewById(R.id.access_num_layout);
		m_accessDeviceLayout.setOnClickListener(this);
	}

	@Override
	public void onResume() {}

	@Override
	public void onPause() {}

	@Override
	public void onDestroy() {}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.access_num_layout:
			accessDeviceLayoutClick();
			break;
		default:
			break;
		}
	}
	
	private void accessDeviceLayoutClick() {
		Intent intent = new Intent();
		intent.setClass(m_context, ActivityDeviceManager.class);	
		this.m_context.startActivity(intent);
	}
}
