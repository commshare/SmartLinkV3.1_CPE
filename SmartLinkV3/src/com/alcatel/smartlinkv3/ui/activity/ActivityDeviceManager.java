package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;


public class ActivityDeviceManager extends Activity implements OnClickListener{
	private ListView m_connecedDeviceList = null;
	private ListView m_blockedDeviceList = null;
	private LinearLayout m_back = null;

	private ArrayList<DeviceItem> m_connecedDeviceLstData = new ArrayList<DeviceItem>();
	private ArrayList<DeviceItem> m_blockedDeviceLstData = new ArrayList<DeviceItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_manage_view);
		m_back = (LinearLayout)this.findViewById(R.id.back_layout);
		m_back.setOnClickListener(this);
		
		m_connecedDeviceList = (ListView)this.findViewById(R.id.connected_devices);
		ConnectedDevAdapter connectedDevAdapter = new ConnectedDevAdapter(this);
		m_connecedDeviceList.setAdapter(connectedDevAdapter);
		
		m_blockedDeviceList = (ListView)this.findViewById(R.id.block_devices);
		BlockedDevAdapter blockedDevAdapter = new BlockedDevAdapter(this);
		m_blockedDeviceList.setAdapter(blockedDevAdapter);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		getListData();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back_layout:
			OnBtnBack();
			break;
		default:
			break;
		}
	}
	
	//
	private void OnBtnBack() {
		this.finish();
	}

	/*private class SortSummaryListByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SMSSummaryItem c1 = (SMSSummaryItem) o1;
			SMSSummaryItem c2 = (SMSSummaryItem) o2;
			String time1 = (String) c1.strSummaryTime;
			String time2 = (String) c2.strSummaryTime;

			if(time1.compareToIgnoreCase(time2) > 0)
				return -1;
			if(time1.compareToIgnoreCase(time2) == 0)
				return 0;
			return 1;
		}
	}*/
	
	
	private void getConnectedDeviceList()	
	{
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST, null);	
	}
			
	
	private void getListData() {
		
		m_connecedDeviceLstData.clear();
		m_blockedDeviceLstData.clear();
		
		getConnectedDeviceList();
		//test start
		DeviceItem item = new DeviceItem();
		item.bBlocked = false;
		item.nDevType = 0;
		item.strDevName = "super man";
		item.strDevAlias = "zhoudequan";
		item.strIP = "192.168.0.1";
		item.strMac = "ff:ff:ff:ff:ff";
		m_connecedDeviceLstData.add(item);
		
		DeviceItem item2 = new DeviceItem();
		item2.bBlocked = false;
		item2.nDevType = 1;
		item2.strDevName = "super man 2";
		item2.strDevAlias = "zhoudequan2";
		item2.strIP = "192.168.0.2";
		item2.strMac = "ff:ff:ff:ff:ff";
		m_connecedDeviceLstData.add(item2);
		
		DeviceItem item3 = new DeviceItem();
		item3.bBlocked = false;
		item3.nDevType = 0;
		item3.strDevName = "super man3";
		item3.strDevAlias = "zhoudequan3";
		item3.strIP = "192.168.0.3";
		item3.strMac = "ff:ff:ff:ff:ff";
		m_connecedDeviceLstData.add(item3);
		
		DeviceItem item4 = new DeviceItem();
		item4.bBlocked = false;
		item4.nDevType = 0;
		item4.strDevName = "super man4";
		item4.strDevAlias = "zhoudequan4";
		item4.strIP = "192.168.0.4";
		item4.strMac = "ff:ff:ff:ff:ff";
		m_connecedDeviceLstData.add(item4);
		
		DeviceItem bitem4 = new DeviceItem();
		bitem4.bBlocked = true;
		bitem4.nDevType = 2;
		bitem4.strDevName = "super manb1";
		bitem4.strDevAlias = "zhoudequanb1";
		bitem4.strIP = "192.168.0.5";
		bitem4.strMac = "ff:ff:ff:ff:ff";
		m_blockedDeviceLstData.add(bitem4);
		
		DeviceItem bitem5 = new DeviceItem();
		bitem5.bBlocked = true;
		bitem5.nDevType = 2;
		bitem5.strDevName = "super manb2";
		bitem5.strDevAlias = "zhoudequanb2";
		bitem5.strIP = "192.168.0.6";
		bitem5.strMac = "ff:ff:ff:ff:ff";
		m_blockedDeviceLstData.add(bitem5);
		//test end
		
		//Collections.sort(m_smsSummaryLstData, new SortSummaryListByTime());

		((ConnectedDevAdapter)m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
		((BlockedDevAdapter)m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
	}
	

	public class ConnectedDevAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
				
		public ConnectedDevAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return m_connecedDeviceLstData.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public Button blockBtn;
			public ImageView icon;
			public EditText deviceName;
			public ImageView modifyDeviceName;
			public TextView ip;
			public TextView mac;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(ActivityDeviceManager.this).inflate(R.layout.device_manage_connected_item,null);	
				holder.blockBtn = (Button)convertView.findViewById(R.id.block_button);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.deviceName = (EditText)convertView.findViewById(R.id.device_description);
				holder.modifyDeviceName = (ImageView)convertView.findViewById(R.id.edit_image);
				holder.ip = (TextView)convertView.findViewById(R.id.ip);
				holder.mac = (TextView)convertView.findViewById(R.id.mac);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			String displayName = m_connecedDeviceLstData.get(position).strDevAlias;
			if(displayName.length() == 0) {
				displayName = m_connecedDeviceLstData.get(position).strDevName;
			}
			holder.deviceName.setText(displayName);
			
			holder.ip.setText("IP:" + m_connecedDeviceLstData.get(position).strIP);
			holder.mac.setText("MAC:" + m_connecedDeviceLstData.get(position).strMac);
			
			return convertView;
		}
		
	}
	
	public class BlockedDevAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
				
		public BlockedDevAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return m_blockedDeviceLstData.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public Button unblockBtn;
			public ImageView icon;
			public TextView deviceName;
			public TextView mac;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(ActivityDeviceManager.this).inflate(R.layout.device_manage_block_item,null);	
				holder.unblockBtn = (Button)convertView.findViewById(R.id.unblock_button);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.deviceName = (TextView)convertView.findViewById(R.id.device_description);
				holder.mac = (TextView)convertView.findViewById(R.id.mac);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			String displayName = m_blockedDeviceLstData.get(position).strDevAlias;
			if(displayName.length() == 0) {
				displayName = m_blockedDeviceLstData.get(position).strDevName;
			}
			holder.deviceName.setText(displayName);
			
			holder.mac.setText("MAC:" + m_blockedDeviceLstData.get(position).strMac);
			
			return convertView;
		}
		
	}

	
	public static class DeviceItem {
		public int nDevType = 0;
		public String strDevName = new String();
		public String strDevAlias = new String();
		public String strIP = new String();
		public String strMac = new String();
		public boolean bBlocked = false;
	}
}
