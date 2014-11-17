package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceType;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

public class ActivityDeviceManager extends Activity implements OnClickListener {
	private ListView m_connecedDeviceList = null;
	private ListView m_blockedDeviceList = null;
	private LinearLayout m_back = null;
	private TextView m_txConnectedCnt;
	private TextView m_txBlockCnt;
	private ImageView m_refresh;

	private ArrayList<ConnectedDeviceItemModel> m_connecedDeviceLstData = new ArrayList<ConnectedDeviceItemModel>();
	private ArrayList<ConnectedDeviceItemModel> m_blockedDeviceLstData = new ArrayList<ConnectedDeviceItemModel>();
	private DeviceReceiver m_deviceReceiver = null;
	private String m_strLocalMac = new String();

	private class DeviceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK&& strErrorCode.length() == 0) {					
					updateConnectedDeviceUI();				
				}
			} else if (intent.getAction().equals(MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK&& strErrorCode.length() == 0) {
					
					updateBlockDeviceUI();					
				}				
			} else if (intent.getAction().equals(MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {				
					getListData();					
				}				
			} else if (intent.getAction().equals(MessageUti.DEVICE_SET_DEVICE_UNLOCK)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {				
					getListData();					
				}				
			}
			
			else if (intent.getAction().equals(MessageUti.DEVICE_SET_DEVICE_NAME)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {				
					getListData();					
				}				
			}
			
		}
	}

	private void registerReceiver() {
		m_deviceReceiver = new DeviceReceiver();
		this.registerReceiver(m_deviceReceiver, new IntentFilter(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST));
		this.registerReceiver(m_deviceReceiver, new IntentFilter(MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST));
		this.registerReceiver(m_deviceReceiver, new IntentFilter(MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK));
		this.registerReceiver(m_deviceReceiver, new IntentFilter(MessageUti.DEVICE_SET_DEVICE_UNLOCK));
		this.registerReceiver(m_deviceReceiver, new IntentFilter(MessageUti.DEVICE_SET_DEVICE_NAME));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_manage_view);
		m_back = (LinearLayout) this.findViewById(R.id.back_layout);
		m_back.setOnClickListener(this);

		m_connecedDeviceList = (ListView) this
				.findViewById(R.id.connected_devices);
		ConnectedDevAdapter connectedDevAdapter = new ConnectedDevAdapter(this);
		m_connecedDeviceList.setAdapter(connectedDevAdapter);

		m_blockedDeviceList = (ListView) this.findViewById(R.id.block_devices);
		BlockedDevAdapter blockedDevAdapter = new BlockedDevAdapter(this);
		m_blockedDeviceList.setAdapter(blockedDevAdapter);
		
		m_txConnectedCnt = (TextView) this.findViewById(R.id.tx_connected_cnt);
		m_txBlockCnt =  (TextView) this.findViewById(R.id.tx_block_cnt);		
		String strConnectedCnt = this.getResources().getString(R.string.device_manage_connected);		
		strConnectedCnt = String.format(strConnectedCnt, 0);
		m_txConnectedCnt.setText(strConnectedCnt);
		
		String strBlockdCnt = this.getResources().getString(R.string.device_manage_block);		
		strBlockdCnt = String.format(strBlockdCnt, 0);
		m_txBlockCnt.setText(strBlockdCnt);		
		
		m_refresh = (ImageView) this.findViewById(R.id.refresh);
		m_refresh.setOnClickListener(this);
		
	}

	@Override
	public void onResume() {
		super.onResume();	
		getLocalMacAddress();
		registerReceiver();
		getListData();
		updateConnectedDeviceUI();
		updateBlockDeviceUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		this.unregisterReceiver(m_deviceReceiver);
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
		switch (arg0.getId()) {
		case R.id.back_layout:
			OnBtnBack();
			break;
			
		case R.id.refresh:
			getListData();			
		default:
			break;
		}
	}

	private void OnBtnBack() {
		this.finish();
	}
	
	private void setConnectedDeviceBlock(String strDeviceName, String strMac ) {
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK, data);	
	}
	
	private void setDeviceUnlock(String strDeviceName, String strMac ) {
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.DEVICE_SET_DEVICE_UNLOCK, data);	
	}
	
	private void setDeviceName(String strDeviceName, String strMac, EnumDeviceType nDeviceType)
	{
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		data.addParam("DeviceType", nDeviceType);
		BusinessMannager.getInstance().sendRequestMessage(MessageUti.DEVICE_SET_DEVICE_NAME, data);	
	}	

	private void getListData() {
		BusinessMannager.getInstance().getGetConnectedDeviceTaskAtOnceRequest();
		BusinessMannager.getInstance().getGetBlockDeviceListTaskAtOnceRequest();
	}
	
	private void updateConnectedDeviceUI()
	{
		ArrayList<ConnectedDeviceItemModel> data = (ArrayList<ConnectedDeviceItemModel>) m_connecedDeviceLstData.clone();
		m_connecedDeviceLstData = BusinessMannager.getInstance().getConnectedDeviceList();
		for(int i = 0;i <m_connecedDeviceLstData.size();i++) {
			ConnectedDeviceItemModel item = m_connecedDeviceLstData.get(i);
			if(item.MacAddress.equalsIgnoreCase(m_strLocalMac))
			{
				m_connecedDeviceLstData.remove(i);
				m_connecedDeviceLstData.add(0, item);
				break;
			}
		}
		
		for(int i = 0;i <m_connecedDeviceLstData.size();i++) {
			for(int j = 0;j < data.size();j++) {
				if(m_connecedDeviceLstData.get(i).MacAddress.equalsIgnoreCase(data.get(j).MacAddress)&& 
						m_connecedDeviceLstData.get(i).IPAddress.equalsIgnoreCase(data.get(j).IPAddress)) {
					m_connecedDeviceLstData.get(i).bEditStatus = data.get(j).bEditStatus;
					break;
				}
			}
		}
		
		((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
		
		String strConnectedCnt = this.getResources().getString(R.string.device_manage_connected);		
		strConnectedCnt = String.format(strConnectedCnt, m_connecedDeviceLstData.size());
		m_txConnectedCnt.setText(strConnectedCnt);
		
	}
	
	private void updateBlockDeviceUI()
	{
		m_blockedDeviceLstData = BusinessMannager.getInstance().getBlockDeviceList();
		((BlockedDevAdapter) m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
		
		String strBlockdCnt = this.getResources().getString(R.string.device_manage_block);		
		strBlockdCnt = String.format(strBlockdCnt, m_blockedDeviceLstData.size());
		m_txBlockCnt.setText(strBlockdCnt);		
	}

	private void getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		m_strLocalMac = info.getMacAddress();
	}
	
	public class ConnectedDevAdapter extends BaseAdapter {

		public ConnectedDevAdapter(Context context) {
		}

		public int getCount() {
			return m_connecedDeviceLstData.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public final class ViewHolder {
			public Button blockBtn;
			public ImageView icon;
			public TextView deviceNameTextView;
			public EditText deviceNameEditView;
			public RelativeLayout deviceNameLayout;
			public ImageView modifyDeviceName;
			public TextView ip;
			public TextView mac;
		}

		ViewHolder holder = null;
		public View getView(final int position, View convertView,ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ActivityDeviceManager.this).inflate(R.layout.device_manage_connected_item, null);
				holder.blockBtn = (Button) convertView.findViewById(R.id.block_button);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.deviceNameTextView = (TextView) convertView.findViewById(R.id.device_description_textview);
				holder.deviceNameEditView = (EditText) convertView.findViewById(R.id.device_description_editview);
				holder.deviceNameLayout = (RelativeLayout)convertView.findViewById(R.id.device_name_layout);
				holder.modifyDeviceName = (ImageView) convertView.findViewById(R.id.edit_image);
				holder.ip = (TextView) convertView.findViewById(R.id.ip);
				holder.mac = (TextView) convertView.findViewById(R.id.mac);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ConnectedDeviceItemModel model = m_connecedDeviceLstData.get(position);
			final String displayName = model.DeviceName;
			holder.deviceNameTextView.setText(displayName);
			if(holder.deviceNameEditView.isFocused() == false)
				holder.deviceNameEditView.setText(displayName);
			holder.ip.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_ip), model.IPAddress));
			final String mac = model.MacAddress;
			holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));
			final EnumDeviceType type = model.DeviceType;
			
			if(mac.equalsIgnoreCase(m_strLocalMac))
			{
				holder.blockBtn.setVisibility(View.GONE);
				holder.deviceNameEditView.setVisibility(View.GONE);
				holder.icon.setBackgroundResource(R.drawable.connected_profile);
				holder.modifyDeviceName.setVisibility(View.GONE);
			}
			else
			{
				holder.blockBtn.setVisibility(View.VISIBLE);
				holder.icon.setBackgroundResource(R.drawable.connected_device);
				holder.modifyDeviceName.setVisibility(View.VISIBLE);
				if(model.bEditStatus == true) {
					holder.deviceNameEditView.setVisibility(View.VISIBLE);
					holder.deviceNameTextView.setVisibility(View.GONE);
					holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_finished);
				}else{
					holder.deviceNameEditView.setVisibility(View.GONE);
					holder.deviceNameTextView.setVisibility(View.VISIBLE);
					holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
				}
			}
			
			holder.modifyDeviceName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					if(m_connecedDeviceLstData.get(position).bEditStatus == true) {
						holder.deviceNameEditView.setVisibility(View.GONE);
						holder.deviceNameTextView.setVisibility(View.VISIBLE);
						m_connecedDeviceLstData.get(position).bEditStatus = false;
						holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
						String strName = holder.deviceNameEditView.getText().toString();
						if(!strName.equals(displayName))
						{
							setDeviceName(strName, mac, type);
						}	
					}else{
						m_connecedDeviceLstData.get(position).bEditStatus = true;
						holder.deviceNameEditView.setVisibility(View.VISIBLE);
						holder.deviceNameTextView.setVisibility(View.GONE);
						holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_finished);
					}
					
					((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
				}
			
			});
			
			holder.blockBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					setConnectedDeviceBlock(displayName, mac);
				}
			
			});
			
			
			holder.deviceNameEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				
					if (actionId == EditorInfo.IME_ACTION_DONE
							|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
						String strName = v.getText().toString();
						
						if(!strName.equals(displayName))
						{
							setDeviceName(strName, mac, type);
						}	
						m_connecedDeviceLstData.get(position).bEditStatus = false;
						holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
					}

					return false;
				}

			});

			return convertView;
		}

	}

	public class BlockedDevAdapter extends BaseAdapter {

		public BlockedDevAdapter(Context context) {

		}

		public int getCount() {
			return m_blockedDeviceLstData.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public final class ViewHolder {
			public Button unblockBtn;
			public ImageView icon;
			public TextView deviceName;
			public TextView mac;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ActivityDeviceManager.this)
						.inflate(R.layout.device_manage_block_item, null);
				holder.unblockBtn = (Button) convertView
						.findViewById(R.id.unblock_button);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.deviceName = (TextView) convertView
						.findViewById(R.id.device_description);
				holder.mac = (TextView) convertView.findViewById(R.id.mac);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}			
			
			final String displayName = m_blockedDeviceLstData.get(position).DeviceName;
			holder.deviceName.setText(displayName);
			final String mac = m_blockedDeviceLstData.get(position).MacAddress;			
			holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));
			
			holder.unblockBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					setDeviceUnlock(displayName, mac);
				}			
			});			
			
			return convertView;
		}

	}

}
