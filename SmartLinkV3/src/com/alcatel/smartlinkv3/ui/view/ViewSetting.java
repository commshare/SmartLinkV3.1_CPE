package com.alcatel.smartlinkv3.ui.view;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.SettingPowerSavingActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ViewSetting extends BaseViewImpl implements OnClickListener {
	
	
	private final int ITEM_WIFI_SETTING = 0;
	private final int ITEM_POWER_SETTING = 1;
	private final int ITEM_BACKUP_SETTING = 2;
	private final int ITEM_UPGRADE_SETTING = 3;
	private final int ITEM_DEVICE_SETTING = 4;
	private final int ITEM_ABOUT_SETTING = 5;
	private TextView m_tvVersion = null;
	private ListView m_lvSettingListView = null;
	

	private void registerReceiver() {}

	public ViewSetting(Context context) {
		super(context);
		init();
		
		 

	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_setting,
				null);
		m_lvSettingListView = (ListView)m_view.findViewById(R.id.settingList);
		SimpleAdapter adapter = new SimpleAdapter(m_context, getData(m_context), R.layout.setting_item,
				new String[]{"title"},
				new int[]{R.id.settingItemText});
		m_lvSettingListView.setAdapter(adapter);
		m_lvSettingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
				case ITEM_WIFI_SETTING:
					goToWifiSettingPage();
					ImageView image = (ImageView)arg1.findViewById(R.id.flagUpgrade);
					image.setVisibility(View.GONE);
					break;
				case ITEM_POWER_SETTING:
					goToPowerSettingPage();
					break;
				case ITEM_BACKUP_SETTING:
					goToBackupSettingPage();
					break;
				case ITEM_UPGRADE_SETTING:
					goToUpgradeSettingPage();
					break;
				case ITEM_DEVICE_SETTING:
					goToDeviceSettingPage();
					break;
				case ITEM_ABOUT_SETTING:
					goToAboutSettingPage();
					break;
				}
			}
		});
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
	
	private List<Map<String, Object>> getData(Context context){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_wifi));
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_power));
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_backup));
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_upgrade));
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_device));
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", context.getString(R.string.setting_about));
		list.add(map);
		return list;
	}
	
	private void goToWifiSettingPage(){
		Toast.makeText(m_context, "Wifi setting", 1).show();
		Intent intent = new Intent(m_context, SettingWifiActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToPowerSettingPage(){
		Toast.makeText(m_context, "goToPowerSettingPage", 10).show();
		Intent intent = new Intent(m_context, SettingPowerSavingActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToBackupSettingPage(){
		Toast.makeText(m_context, "goToBackupSettingPage", 10).show();
	}
	
	private void goToUpgradeSettingPage(){
		Toast.makeText(m_context, "goToUpgradeSettingPage", 10).show();
	}
	
	private void goToDeviceSettingPage(){
		Toast.makeText(m_context, "goToDeviceSettingPage", 10).show();
	}
	
	private void goToAboutSettingPage(){
		Toast.makeText(m_context, "goToAboutSettingPage", 10).show();
	}
}
