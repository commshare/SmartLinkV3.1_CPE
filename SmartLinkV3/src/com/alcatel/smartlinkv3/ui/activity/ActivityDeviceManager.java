package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.os.Bundle;
import android.util.Log;
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

	private ConnectedDeviceList m_connecedDeviceLstData = new ConnectedDeviceList();
	private BlockDeviceList m_blockedDeviceLstData = new BlockDeviceList();
	private DeviceReceiver m_deviceReceiver = null;

	private class DeviceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(
					MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK
						&& strErrorCode.length() == 0) {					
					updateConnectedDeviceUI();				
				}
			} else if (intent.getAction().equals(
					MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK
						&& strErrorCode.length() == 0) {
					
					updateBlockDeviceUI();					
				}				
			} else if (intent.getAction().equals(
					MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK
						&& strErrorCode.length() == 0) {				
					getListData();					
				}				
			} else if (intent.getAction().equals(
					MessageUti.DEVICE_SET_DEVICE_UNLOCK)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK
						&& strErrorCode.length() == 0) {				
					getListData();					
				}				
			}
			
			else if (intent.getAction().equals(
					MessageUti.DEVICE_SET_DEVICE_NAME)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK
						&& strErrorCode.length() == 0) {				
					getListData();					
				}				
			}
			
		}
	}

	private void registerReceiver() {
		m_deviceReceiver = new DeviceReceiver();

		this.registerReceiver(m_deviceReceiver, new IntentFilter(
				MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST));

		this.registerReceiver(m_deviceReceiver, new IntentFilter(
				MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST));

		this.registerReceiver(m_deviceReceiver, new IntentFilter(
				MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK));

		this.registerReceiver(m_deviceReceiver, new IntentFilter(
				MessageUti.DEVICE_SET_DEVICE_UNLOCK));

		this.registerReceiver(m_deviceReceiver, new IntentFilter(
				MessageUti.DEVICE_SET_DEVICE_NAME));
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
		strConnectedCnt = String.format(strConnectedCnt, 1);
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
		registerReceiver();
		getListData();
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

	private void getConnectedDeviceList() {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST, null);
	}

	private void getBlockDeviceList() {
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST, null);
	}
	
	private void setConnectedDeviceBlock(String strDeviceName, String strMac )
	{
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK, data);	
	}
	
	private void setDeviceUnlock(String strDeviceName, String strMac )
	{
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_SET_DEVICE_UNLOCK, data);	
	}
	
	private void setDeviceName(String strDeviceName, String strMac, int nDeviceType)
	{
		DataValue data = new DataValue();
		data.addParam("DeviceName", strDeviceName);
		data.addParam("MacAddress", strMac);
		data.addParam("DeviceType", nDeviceType);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.DEVICE_SET_DEVICE_NAME, data);	
	}	

	private void getListData() {

		m_connecedDeviceLstData.clear();
		m_blockedDeviceLstData.clear();
		getConnectedDeviceList();
		getBlockDeviceList();
	}
	
	private void updateConnectedDeviceUI()
	{
		m_connecedDeviceLstData = BusinessMannager.getInstance()
				.getConnectedDeviceList();
		((ConnectedDevAdapter) m_connecedDeviceList.getAdapter())
				.notifyDataSetChanged();
		
		String strConnectedCnt = this.getResources().getString(R.string.device_manage_connected);		
		strConnectedCnt = String.format(strConnectedCnt, m_connecedDeviceLstData.ConnectedList.size());
		m_txConnectedCnt.setText(strConnectedCnt);
		
	}
	
	private void updateBlockDeviceUI()
	{
		m_blockedDeviceLstData = BusinessMannager.getInstance()
				.getBlockDeviceList();
		((BlockedDevAdapter) m_blockedDeviceList.getAdapter())
				.notifyDataSetChanged();
		
		String strBlockdCnt = this.getResources().getString(R.string.device_manage_block);		
		strBlockdCnt = String.format(strBlockdCnt, m_blockedDeviceLstData.BlockList.size());
		m_txBlockCnt.setText(strBlockdCnt);		
	}

	public class ConnectedDevAdapter extends BaseAdapter {

		public ConnectedDevAdapter(Context context) {
		}

		public int getCount() {
			return m_connecedDeviceLstData.ConnectedList.size();
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
			public EditText deviceName;
			public ImageView modifyDeviceName;
			public TextView ip;
			public TextView mac;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ActivityDeviceManager.this)
						.inflate(R.layout.device_manage_connected_item, null);
				holder.blockBtn = (Button) convertView
						.findViewById(R.id.block_button);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.deviceName = (EditText) convertView
						.findViewById(R.id.device_description);
				holder.modifyDeviceName = (ImageView) convertView
						.findViewById(R.id.edit_image);
				holder.ip = (TextView) convertView.findViewById(R.id.ip);
				holder.mac = (TextView) convertView.findViewById(R.id.mac);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final String displayName = m_connecedDeviceLstData.ConnectedList.get(position).DeviceName;
			holder.deviceName.setText(displayName);			
			holder.ip.setText("IP:"+ m_connecedDeviceLstData.ConnectedList.get(position).IPAddress);
			final String mac = m_connecedDeviceLstData.ConnectedList.get(position).MacAddress;
			holder.mac.setText("MAC:"+ mac);
			final int type = m_connecedDeviceLstData.ConnectedList.get(position).DeviceType;
			
			holder.blockBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					setConnectedDeviceBlock(displayName, mac);
				}
			
			});
			
			
			holder.deviceName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				
					if (actionId == EditorInfo.IME_ACTION_DONE
							|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
						String strName = v.getText().toString();
						
						if(!strName.equals(displayName))
						{
							setDeviceName(strName, mac, type);
						}											
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
			return m_blockedDeviceLstData.BlockList.size();
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
			
			final String displayName = m_blockedDeviceLstData.BlockList.get(position).DeviceName;
			holder.deviceName.setText(displayName);
			final String mac = m_blockedDeviceLstData.BlockList.get(position).MacAddress;			
			holder.mac.setText("MAC:" + mac);
			
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
