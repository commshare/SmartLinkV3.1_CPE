package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.EnumConnectMode;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceType;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

public class ActivityDeviceManager extends BaseActivity implements OnClickListener {
	private ListView m_connecedDeviceList = null;
	private ListView m_blockedDeviceList = null;
	private LinearLayout m_back = null;
	private TextView m_txConnectedCnt;
	private TextView m_txBlockCnt;
	private ImageView m_refresh;
	private ProgressBar m_waiting = null;
	
	private boolean m_bIsWorking = false;
	private boolean m_bEnableRefresh = false;

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
				m_waiting.setVisibility(View.GONE);
				if (nResult == BaseResponse.RESPONSE_OK&& strErrorCode.length() == 0) {	
					if(m_bEnableRefresh == true || haveEditItem() == false) {
						resetConnectEditFlag();
						m_bEnableRefresh = false;
						updateConnectedDeviceUI();	
					}
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
				m_bIsWorking = false;
				m_waiting.setVisibility(View.GONE);
				((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
				((BlockedDevAdapter) m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
			} else if (intent.getAction().equals(MessageUti.DEVICE_SET_DEVICE_UNLOCK)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {				
					getListData();					
				}	
				m_bIsWorking = false;
				m_waiting.setVisibility(View.GONE);
				((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
				((BlockedDevAdapter) m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
			}
			
			else if (intent.getAction().equals(MessageUti.DEVICE_SET_DEVICE_NAME)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {				
					getListData();					
				}else{
					String msgRes = ActivityDeviceManager.this.getString(R.string.device_manage_set_name_failed);
					Toast.makeText(ActivityDeviceManager.this, msgRes, Toast.LENGTH_SHORT).show();
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
		m_bNeedBack = false;
		
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
		
		m_waiting = (ProgressBar) this.findViewById(R.id.waiting_progress);
		
	}

	@Override
	public void onResume() {
		super.onResume();	
		getLocalMacAddress();
		registerReceiver();
		//getListData();
		updateConnectedDeviceUI();
		updateBlockDeviceUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		m_bIsWorking = false;
		m_bEnableRefresh = false;
		resetConnectEditFlag();
		m_waiting.setVisibility(View.GONE);
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
			m_bEnableRefresh = true;
			m_waiting.setVisibility(View.VISIBLE);
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
			m_connecedDeviceLstData.get(i).strEditString = m_connecedDeviceLstData.get(i).DeviceName;
			for(int j = 0;j < data.size();j++) {
				if(m_connecedDeviceLstData.get(i).MacAddress.equalsIgnoreCase(data.get(j).MacAddress)&& 
						m_connecedDeviceLstData.get(i).IPAddress.equalsIgnoreCase(data.get(j).IPAddress)) {
					m_connecedDeviceLstData.get(i).bEditStatus = data.get(j).bEditStatus;
					if(data.get(j).bEditStatus == true)
						m_connecedDeviceLstData.get(i).strEditString = data.get(j).strEditString;
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
		
		private String m_focusedMac = new String();
		private int m_currentSelection = 0;

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
		private String m_strEditString = new String();
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
			
			//Log.d("dddd", "position:" + String.valueOf(position));
			
			if(m_bIsWorking == true)
				holder.blockBtn.setEnabled(false);
			else
				holder.blockBtn.setEnabled(true);

			final ConnectedDeviceItemModel model = m_connecedDeviceLstData.get(position);
			final String displayName = model.DeviceName;
			holder.deviceNameTextView.setText(displayName);
			
			if(model.ConnectMode == EnumConnectMode.USB_CONNECT) 
				holder.blockBtn.setEnabled(false);
			else
				holder.blockBtn.setEnabled(true);
			
			holder.ip.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_ip), model.IPAddress));
			final String mac = model.MacAddress;
			holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));
			final EnumDeviceType type = model.DeviceType;
			
			if(mac.equalsIgnoreCase(m_strLocalMac))
			{
				holder.blockBtn.setVisibility(View.GONE);
				holder.deviceNameEditView.setVisibility(View.GONE);
				holder.deviceNameTextView.setVisibility(View.VISIBLE);
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
					holder.deviceNameEditView.setText(m_strEditString);
					holder.deviceNameEditView.requestFocus();
					int nSelection = m_strEditString.length() == 0?0:m_strEditString.length();
					holder.deviceNameEditView.setSelection(nSelection);
					holder.deviceNameTextView.setVisibility(View.GONE);
					holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_finished);
				}else{
					holder.deviceNameEditView.setVisibility(View.GONE);
					holder.deviceNameTextView.setVisibility(View.VISIBLE);
					holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
				}
			}
			
			holder.deviceNameEditView.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					String strText = arg0.toString();
					String strNewText = strText.replaceAll("[,\":;&]", "");
					if(strNewText.equals(arg0.toString()) == false) {
						arg0 = arg0.replace(0, arg0.length(), strNewText);
					}
					m_strEditString = strNewText;
					//Log.d("dddd", "m_strEditString s:" + strText);
					//Log.d("dddd", "m_strEditString:" + m_strEditString);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			holder.modifyDeviceName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_connecedDeviceLstData.get(position).bEditStatus == true) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
						
						//holder.deviceNameTextView.setVisibility(View.VISIBLE);
						m_connecedDeviceLstData.get(position).bEditStatus = false;
						//holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
						//String strName = holder.deviceNameEditView.getText().toString().trim();
						String strName = m_strEditString.trim();
						//holder.deviceNameEditView.setVisibility(View.GONE);
						//Log.d("dddd", "onclick:" + strName);
						if(strName.length() == 0 || strName.length() > 31) {
							String msgRes = ActivityDeviceManager.this.getString(R.string.device_manage_name_empty);
							Toast.makeText(ActivityDeviceManager.this, msgRes, Toast.LENGTH_SHORT).show();
						}
						if(strName.length() != 0 && !strName.equals(displayName))
						{
							setDeviceName(strName, mac, type);
							m_connecedDeviceLstData.get(position).DeviceName = strName;
						}	
					}else{
						resetConnectEditFlag();
						m_strEditString = m_connecedDeviceLstData.get(position).DeviceName;
						m_connecedDeviceLstData.get(position).bEditStatus = true;
					}
					
					((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
				}
			
			});
			
			holder.blockBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					m_bIsWorking = true;
					m_bEnableRefresh = true;
					m_waiting.setVisibility(View.VISIBLE);
					holder.blockBtn.setEnabled(false);
					resetConnectEditFlag();
					setConnectedDeviceBlock(displayName, mac);
					m_connecedDeviceLstData.remove(position);
					((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
					((BlockedDevAdapter) m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
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
	
	private boolean haveEditItem() {
		for(int i = 0;i < m_connecedDeviceLstData.size();i++) {
			if(m_connecedDeviceLstData.get(i).bEditStatus == true)
				return true;
		}
		return false;
	}
	
	private void resetConnectEditFlag() {
		for(int i = 0;i < m_connecedDeviceLstData.size();i++) {
			m_connecedDeviceLstData.get(i).bEditStatus = false;
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

		ViewHolder holder = null;
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			
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
			
			if(m_bIsWorking == true)
				holder.unblockBtn.setEnabled(false);
			else
				holder.unblockBtn.setEnabled(true);
			
			final String displayName = m_blockedDeviceLstData.get(position).DeviceName;
			holder.deviceName.setText(displayName);
			final String mac = m_blockedDeviceLstData.get(position).MacAddress;			
			holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));
			
			holder.unblockBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					m_bIsWorking = true;
					m_waiting.setVisibility(View.VISIBLE);
					m_bEnableRefresh = true;
					holder.unblockBtn.setEnabled(false);
					setDeviceUnlock(displayName, mac);
					m_blockedDeviceLstData.remove(position);
					((ConnectedDevAdapter) m_connecedDeviceList.getAdapter()).notifyDataSetChanged();
					((BlockedDevAdapter) m_blockedDeviceList.getAdapter()).notifyDataSetChanged();
				}			
			});			
			
			return convertView;
		}

	}

}
