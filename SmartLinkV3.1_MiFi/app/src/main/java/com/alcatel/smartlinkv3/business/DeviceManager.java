package com.alcatel.smartlinkv3.business;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.SMSManager.GetContactMessagesTask;
import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.business.device.HttpDevice;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.business.sms.HttpSms;
import com.alcatel.smartlinkv3.business.sms.SmsContactListResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceType;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;

public class DeviceManager extends BaseManager {
	
	private Timer m_rollTimer = new Timer();
	GetConnectedDeviceTask m_getGetConnectedDeviceTask = null;
	GetBlockDeviceListTask m_getGetBlockDeviceListTask = null;

	private ArrayList<ConnectedDeviceItemModel> m_connectedDeviceList = new ArrayList<ConnectedDeviceItemModel>();
	private ArrayList<ConnectedDeviceItemModel> m_blockDeviceList = new ArrayList<ConnectedDeviceItemModel>();

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				startGetConnectedDeviceTask();
				startGetBlockDeviceListTask();
			}
    	}
	}

	public DeviceManager(Context context) {
		super(context);
	}

	@Override
	protected void clearData() {

	}

	@Override
	protected void stopRollTimer() {
		stopGetConnectedDeviceTask();
		stopGetBlockDeviceListTask();
	}	
	
	public ArrayList<ConnectedDeviceItemModel> getConnectedDeviceList()
	{
		return (ArrayList<ConnectedDeviceItemModel>) m_connectedDeviceList.clone();
	}
	
	public ArrayList<ConnectedDeviceItemModel> getBlockDeviceList()
	{
		return (ArrayList<ConnectedDeviceItemModel>) m_blockDeviceList.clone();
	}

	// GetConnectedDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void startGetConnectedDeviceTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("ConnectionDevices", "GetConnectedDeviceList") != true)
			return;
		
		if(m_getGetConnectedDeviceTask == null) {
			m_getGetConnectedDeviceTask = new GetConnectedDeviceTask();
			m_rollTimer.scheduleAtFixedRate(m_getGetConnectedDeviceTask, 0, 30 * 1000);
		}
	}
	
	public void stopGetConnectedDeviceTask() {
		if(m_getGetConnectedDeviceTask != null) {
			m_getGetConnectedDeviceTask.cancel();
			m_getGetConnectedDeviceTask = null;
		}
	}
	
	public void getGetConnectedDeviceTaskAtOnceRequest(){
		if(FeatureVersionManager.getInstance().isSupportApi("ConnectionDevices", "GetConnectedDeviceList") != true)
			return;
		GetConnectedDeviceTask task = new GetConnectedDeviceTask();
		m_rollTimer.schedule(task, 0);
	}
	
	class GetConnectedDeviceTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpDevice.GetConnectedDeviceList("12.1",new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(
						BaseResponse response) {
					String strErrcode = new String();
					int ret = response.getResultCode();
					if (ret == BaseResponse.RESPONSE_OK) {
						strErrcode = response.getErrorCode();
						if (strErrcode.length() == 0) {
							m_connectedDeviceList.clear();
							ConnectedDeviceList result = response.getModelResult();
							for(int i = 0;i < result.ConnectedList.size();i++) {
								ConnectedDeviceItemModel item = new ConnectedDeviceItemModel();
								item.buildFromResult(result.ConnectedList.get(i));
								m_connectedDeviceList.add(item);
							}
						} else {
							m_connectedDeviceList.clear();
						}
					} else {
						m_connectedDeviceList.clear();
					}
					Intent megIntent = new Intent(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST);
					megIntent.putExtra(MessageUti.RESPONSE_RESULT,ret);
					megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,strErrcode);
					m_context.sendBroadcast(megIntent);
				}
			}));
        } 
	}

	// GetBlockDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void startGetBlockDeviceListTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("ConnectionDevices", "GetBlockDeviceList") != true)
			return;
		
		if(m_getGetBlockDeviceListTask == null) {
			m_getGetBlockDeviceListTask = new GetBlockDeviceListTask();
			m_rollTimer.scheduleAtFixedRate(m_getGetBlockDeviceListTask, 0, 30 * 1000);
		}
	}
	
	public void stopGetBlockDeviceListTask() {
		if(m_getGetBlockDeviceListTask != null) {
			m_getGetBlockDeviceListTask.cancel();
			m_getGetBlockDeviceListTask = null;
		}
	}
	
	public void getGetBlockDeviceListTaskAtOnceRequest(){
		if(FeatureVersionManager.getInstance().isSupportApi("ConnectionDevices", "GetBlockDeviceList") != true)
			return;
		GetBlockDeviceListTask task = new GetBlockDeviceListTask();
		m_rollTimer.schedule(task, 0);
	}
	
	class GetBlockDeviceListTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpDevice.GetBlockDeviceList("12.2",new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(
						BaseResponse response) {
					String strErrcode = new String();
					int ret = response.getResultCode();
					if (ret == BaseResponse.RESPONSE_OK) {
						strErrcode = response.getErrorCode();
						if (strErrcode.length() == 0) {
							m_blockDeviceList.clear();
							BlockDeviceList result = response.getModelResult();
							for(int i = 0;i < result.BlockList.size();i++) {
								ConnectedDeviceItemModel item = new ConnectedDeviceItemModel();
								item.buildFromResult(result.BlockList.get(i));
								m_blockDeviceList.add(item);
							}
						} else {
							m_blockDeviceList.clear();
						}
					} else {
						m_blockDeviceList.clear();
					}
					Intent megIntent = new Intent(
							MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST);
					megIntent.putExtra(MessageUti.RESPONSE_RESULT,
							ret);
					megIntent.putExtra(
							MessageUti.RESPONSE_ERROR_CODE,
							strErrcode);
					m_context.sendBroadcast(megIntent);
				}
			}));
        } 
	}


	// SetConnectedDeviceBlock
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setConnectedDeviceBlock(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi(
				"ConnectionDevices", "SetConnectedDeviceBlock") != true)
			return;

		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.SetConnectedDeviceBlock("12.3", name, mac,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {

									} else {

									}
								} else {

								}
								Intent megIntent = new Intent(
										MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// SetDeviceUnblock
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDeviceUnlock(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi(
				"ConnectionDevices", "SetDeviceUnlock") != true)
			return;

		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.SetDeviceUnlock("12.4", name, mac,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {

									} else {

									}
								} else {

								}
								Intent megIntent = new Intent(
										MessageUti.DEVICE_SET_DEVICE_UNLOCK);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// SetDeviceName
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDeviceName(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi(
				"ConnectionDevices", "SetDeviceName") != true)
			return;

		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");
		EnumDeviceType type = (EnumDeviceType) data.getParamByKey("DeviceType");
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.SetDeviceName("12.5", name, mac, EnumDeviceType.antiBuild(type),
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {

									} else {

									}
								} else {

								}
								Intent megIntent = new Intent(
										MessageUti.DEVICE_SET_DEVICE_NAME);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

}
