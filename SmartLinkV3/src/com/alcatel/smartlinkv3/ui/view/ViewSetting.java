package com.alcatel.smartlinkv3.ui.view;



import java.util.ArrayList;
import java.util.List;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingBackupRestoreActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingNewAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingShareActivity;
import com.alcatel.smartlinkv3.ui.activity.SystemInfoActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingNetworkActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingPowerSavingActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingUpgradeActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ViewSetting extends BaseViewImpl{
	
	
	private final int ITEM_WIFI_SETTING1 = 0;
	private final int ITEM_ACCOUNT_SETTING1 = 1;
	private final int ITEM_NETWORK_SETTING1 = 2;
	private final int ITEM_DEVICE_SETTING1 =3;
	private final int ITEM_ABOUT_SETTING1 = 4;
	
	private final int ITEM_WIFI_SETTING2 = 0;
	private final int ITEM_ACCOUNT_SETTING2 = 1;
	private final int ITEM_NETWORK_SETTING2 = 2;
	private final int ITEM_SHARE_SETTING = 3;
	private final int ITEM_DEVICE_SETTING2 = 4;
	private final int ITEM_ABOUT_SETTING2 = 5;
	
	private boolean isSharingSupported = false;
	//Previous upgrade setting was 3, device was 4, about was 5, power was 1, backup was 2.
//	private final int ITEM_UPGRADE_SETTING = 5;
//	private final int ITEM_POWER_SETTING = 6;
//	private final int ITEM_BACKUP_SETTING = 7;
	private ListView m_lvSettingListView = null;
	private UprgadeAdapter adapter;
	private List<SettingItem>list;
	private boolean m_blFirst=true;
	private settingBroadcast m_receiver;
	

	private void registerReceiver() {
		m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
		m_context.registerReceiver(m_receiver, 
				new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
		m_context.registerReceiver(m_receiver, 
				new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));
		m_context.registerReceiver(m_receiver, 
				new IntentFilter(MessageUti.SHARING_GET_FTP_SETTING_REQUSET));
	}

	public ViewSetting(Context context) {
		super(context);
		init();
		m_receiver = new settingBroadcast();
	}
	
	private void initUI(){
		list = getData(m_context);
		adapter = new UprgadeAdapter(m_context, list);
		
		m_lvSettingListView.setAdapter(adapter);
		if(isSharingSupported){
			m_lvSettingListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					switch(arg2){
					case ITEM_WIFI_SETTING2:
						goToWifiSettingPage();
						break;
					case ITEM_ACCOUNT_SETTING2:
						goToAccountSettingPage();
						break;
					case ITEM_NETWORK_SETTING2:
						goToNetworkSettingPage();
						break;
					case ITEM_SHARE_SETTING:
						goToShareSettingPage();
						break;
					case ITEM_DEVICE_SETTING2:
						goToDeviceSettingPage();
						break;
					case ITEM_ABOUT_SETTING2:
						goToAboutSettingPage();
						break;
//					case ITEM_POWER_SETTING:
//						goToPowerSettingPage();
//						break;
//					case ITEM_BACKUP_SETTING:
//						goToBackupSettingPage();
//						break;
//					case ITEM_UPGRADE_SETTING:
//						goToUpgradeSettingPage();
//						break;
					default:
						break;
					}
				}
			});
		}
		else{
			m_lvSettingListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					switch(arg2){
					case ITEM_WIFI_SETTING1:
						goToWifiSettingPage();
						break;
					case ITEM_ACCOUNT_SETTING1:
						goToAccountSettingPage();
						break;
					case ITEM_NETWORK_SETTING1:
						goToNetworkSettingPage();
						break;
					case ITEM_DEVICE_SETTING1:
						goToDeviceSettingPage();
						break;
					case ITEM_ABOUT_SETTING1:
						goToAboutSettingPage();
						break;
//					case ITEM_POWER_SETTING:
//						goToPowerSettingPage();
//						break;
//					case ITEM_BACKUP_SETTING:
//						goToBackupSettingPage();
//						break;
//					case ITEM_UPGRADE_SETTING:
//						goToUpgradeSettingPage();
//						break;
					default:
						break;
					}
				}
			});
		}
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_setting,
				null);
		m_lvSettingListView = (ListView)m_view.findViewById(R.id.settingList);
	}

	@Override
	public void onResume() {
		registerReceiver();
		int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, null);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_FTP_SETTING_REQUSET, null);
		if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
			m_blFirst = false;
//			changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
		}else {
//			changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
		}	
	}

	@Override
	public void onPause() {
		try{
			m_context.unregisterReceiver(m_receiver);
		}catch(Exception e){
			
		}
	}

	@Override
	public void onDestroy() {
	}

	private List<SettingItem> getData(Context context){
		List<SettingItem> list = new ArrayList<SettingItem>();
		
		SettingItem item = new SettingItem(context.getString(R.string.setting_wifi), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_account), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_network), false);
		list.add(item);
		
		if(isSharingSupported){
			item = new SettingItem(context.getString(R.string.setting_sharing), false);
			list.add(item);
		}
		
		item = new SettingItem(context.getString(R.string.setting_device), false);
		list.add(item);
		
		item = new SettingItem(context.getString(R.string.setting_about), false);
		list.add(item);
		
//		int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
//		if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
//			item = new SettingItem(context.getString(R.string.setting_upgrade), true);
//			list.add(item);
//		}else {
//			item = new SettingItem(context.getString(R.string.setting_upgrade), false);
//			list.add(item);
//		}
//		
//		item = new SettingItem(context.getString(R.string.setting_power), false);
//		list.add(item);
//		
//		item = new SettingItem(context.getString(R.string.setting_backup), false);
//		list.add(item);
		
		return list;
	}
	
	private void goToWifiSettingPage(){
		Intent intent = new Intent(m_context, SettingWifiActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToShareSettingPage(){
		Intent intent = new Intent(m_context, SettingShareActivity.class);
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
	
	private void goToAccountSettingPage(){
		Intent intent = new Intent(m_context, SettingAccountActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToNetworkSettingPage(){
		Intent intent = new Intent(m_context, SettingNetworkActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToDeviceSettingPage(){
		//Intent intent = new Intent(m_context, SystemInfoActivity.class);
		Intent intent = new Intent(m_context, SettingDeviceActivity.class);
		m_context.startActivity(intent);
	}
	
	private void goToAboutSettingPage(){
		Intent intent = new Intent(m_context, SettingNewAboutActivity.class);
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
		public View getView(int arg0, View convertView, ViewGroup arg2) {
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
	
//	private void changeUpgradeFlag(int itemIndex, Boolean blUpgrade){
//		if(itemIndex >= ITEM_WIFI_SETTING && itemIndex <= ITEM_UPGRADE_SETTING){
//			SettingItem item = list.get(itemIndex);
//			Boolean blOldUpgrade = item.getUpgradeFlag();
//			if(blOldUpgrade != blUpgrade){
//				item.setUpgradeFlag(blUpgrade);
//				adapter.notifyDataSetChanged();
//			}
//		}
//	}
	
	private class settingBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
				}else {

					Toast.makeText(m_context, R.string.setting_upgrade_stop_error, Toast.LENGTH_SHORT).show();
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
					int nUpgradeStatus = BusinessMannager.getInstance().getNewFirmwareInfo().getState();
					if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
						m_blFirst = false;
//						changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
					}else {
//						changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
					}
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET) || intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				boolean isFtpSupported = false;
				boolean isDlnaSupported = false;
				if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
					if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET) ){
						Log.v("CHECKING_SHARING", "FTP");
						isFtpSupported = true;
					}
					if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET) ){
						Log.v("CHECKING_SHARING", "DLNA");
						isDlnaSupported = true;
					}
				}else {
					if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET) ){
						Log.v("CHECKING_SHARING", "NOFTP");
						isFtpSupported = false;
					}
					if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET) ){
						Log.v("CHECKING_SHARING", "NODLNA");
						isFtpSupported = false;
					}
				}
				isSharingSupported = isFtpSupported || isDlnaSupported;
				initUI();
			}
		}
		
	}
}
