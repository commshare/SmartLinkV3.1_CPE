package com.alcatel.smartlinkv3.ui.view;



import java.util.ArrayList;
import java.util.List;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingBackupRestoreActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingPowerSavingActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingUpgradeActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ViewSetting extends BaseViewImpl{
	
	
	private final int ITEM_WIFI_SETTING = 0;
	private final int ITEM_POWER_SETTING = 1;
	private final int ITEM_BACKUP_SETTING = 2;
	private final int ITEM_UPGRADE_SETTING = 3;
	private final int ITEM_DEVICE_SETTING = 4;
	private final int ITEM_ABOUT_SETTING = 5;
	private ListView m_lvSettingListView = null;
	private UprgadeAdapter adapter;
	private List<SettingItem>list;
	private boolean m_blFirst=true;
	

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
		list = getData(m_context);
		adapter = new UprgadeAdapter(m_context, list);
		
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
					changeUpgradeFlag(ITEM_UPGRADE_SETTING, false);
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
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onDestroy() {
	}

	private List<SettingItem> getData(Context context){
		List<SettingItem> list = new ArrayList<SettingItem>();
		
		SettingItem item = new SettingItem(context.getString(R.string.setting_wifi), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_power), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_backup), false);
		list.add(item);
		
		int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
		if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
			item = new SettingItem(context.getString(R.string.setting_upgrade), true);
			list.add(item);
		}else {
			item = new SettingItem(context.getString(R.string.setting_upgrade), false);
			list.add(item);
		}
		
		item = new SettingItem(context.getString(R.string.setting_device), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_about), false);
		list.add(item);
		return list;
	}
	
	private void goToWifiSettingPage(){
		Intent intent = new Intent(m_context, SettingWifiActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToPowerSettingPage(){
		Intent intent = new Intent(m_context, SettingPowerSavingActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToBackupSettingPage(){
		Intent intent = new Intent(m_context, SettingBackupRestoreActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToUpgradeSettingPage(){
		Intent intent = new Intent(m_context, SettingUpgradeActivity.class);
		intent.putExtra("First", m_blFirst);
		m_blFirst = false;
		m_context.startActivity(intent);
	}
	
	private void goToDeviceSettingPage(){
		Intent intent = new Intent(m_context, SettingDeviceActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToAboutSettingPage(){
		Intent intent = new Intent(m_context, SettingAboutActivity.class);
		m_context.startActivity(intent);
	}
	
	public class UprgadeAdapter extends BaseAdapter{

		private Context context;
		private List<SettingItem> listSettingItmes;
		
		public UprgadeAdapter(Context context, List<SettingItem>list){
			this.context =context;
			this.listSettingItmes = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listSettingItmes.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listSettingItmes.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View itemView = LayoutInflater.from(this.context).inflate(R.layout.setting_item,
					null);
			//
			SettingItem item = listSettingItmes.get(arg0);
			TextView tvItemNameTextView = (TextView)itemView.findViewById(R.id.settingItemText);
			ImageView ivUpgrade = (ImageView)itemView.findViewById(R.id.flagUpgrade);
			tvItemNameTextView.setText(item.getItemName());
			Boolean blUpgrade = item.getUpgradeFlag();
			if(blUpgrade){
				ivUpgrade.setVisibility(View.VISIBLE);				
			}else{
				ivUpgrade.setVisibility(View.GONE);
			}
			return itemView;
		}
		
	}
	
	public class SettingItem{
		//Item name
		private String m_strItem;
		private Boolean m_blHasUpgrade;
		
		public SettingItem(String strItem, Boolean blHasUpgrade){
			super();
			this.m_strItem = strItem;
			this.m_blHasUpgrade = blHasUpgrade;
		}
		
		public Boolean getUpgradeFlag(){
			return m_blHasUpgrade;
		}
		
		public void setUpgradeFlag(Boolean blHasUpgrade){
			m_blHasUpgrade = blHasUpgrade;
		}
		
		public String getItemName(){
			return m_strItem;
		}
	}
	
	private void changeUpgradeFlag(int itemIndex, Boolean blUpgrade){
		if(itemIndex >= ITEM_WIFI_SETTING && itemIndex <= ITEM_ABOUT_SETTING){
			SettingItem item = list.get(itemIndex);
			Boolean blOldUpgrade = item.getUpgradeFlag();
			if(blOldUpgrade != blUpgrade){
				item.setUpgradeFlag(blUpgrade);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
