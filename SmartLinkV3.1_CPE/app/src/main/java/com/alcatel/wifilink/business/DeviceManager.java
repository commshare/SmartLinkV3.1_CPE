package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.device.BlockDeviceList;
import com.alcatel.wifilink.business.device.ConnectedDeviceList;
import com.alcatel.wifilink.business.device.HttpDevice;
import com.alcatel.wifilink.business.model.ConnectedDeviceItemModel;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.EnumDeviceType;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceManager extends BaseManager {
	
	private Timer m_rollTimer = new Timer();
	private GetConnectedDeviceTask m_getGetConnectedDeviceTask = null;
	private GetBlockDeviceListTask m_getGetBlockDeviceListTask = null;

	private ArrayList<ConnectedDeviceItemModel> m_connectedDeviceList = new ArrayList<>();
	private ArrayList<ConnectedDeviceItemModel> m_blockDeviceList = new ArrayList<>();

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected) {
				startGetConnectedDeviceTask();
				startGetBlockDeviceListTask();
			}
    	}
	}

	DeviceManager(Context context) {
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
	
	ArrayList<ConnectedDeviceItemModel> getConnectedDeviceList()
	{
//		ArrayList<ConnectedDeviceItemModel> dest = new ArrayList<>(m_connectedDeviceList.size());
//		dest.ensureCapacity(m_connectedDeviceList.size());
//		Collections.copy(dest, m_connectedDeviceList);
//		return dest;
		return (ArrayList<ConnectedDeviceItemModel>) m_connectedDeviceList.clone();
	}
	
	ArrayList<ConnectedDeviceItemModel> getBlockDeviceList()
	{
		ArrayList<ConnectedDeviceItemModel> dest = new ArrayList<>();
		Collections.copy(dest, m_blockDeviceList);
		return dest;
//		return (ArrayList<ConnectedDeviceItemModel>) m_blockDeviceList.clone();
	}

	// GetConnectedDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	private void startGetConnectedDeviceTask() {
		if(m_getGetConnectedDeviceTask == null) {
			m_getGetConnectedDeviceTask = new GetConnectedDeviceTask();
			m_rollTimer.scheduleAtFixedRate(m_getGetConnectedDeviceTask, 0, 30 * 1000);
		}
	}

	private void stopGetConnectedDeviceTask() {
		if(m_getGetConnectedDeviceTask != null) {
			m_getGetConnectedDeviceTask.cancel();
			m_getGetConnectedDeviceTask = null;
		}
	}

	public void getGetConnectedDeviceTaskAtOnceRequest(){
		GetConnectedDeviceTask task = new GetConnectedDeviceTask();
		m_rollTimer.schedule(task, 0);
	}

	private class GetConnectedDeviceTask extends TimerTask{
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpDevice.GetConnectedDeviceList(new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(BaseResponse response) {
					m_connectedDeviceList.clear();
					if (response.isOk()) {
							ConnectedDeviceList result = response.getModelResult();
							for(int i = 0;i < result.ConnectedList.size();i++) {
								ConnectedDeviceItemModel item = new ConnectedDeviceItemModel();
								item.buildFromResult(result.ConnectedList.get(i));
								m_connectedDeviceList.add(item);
							}
					}
//					Intent megIntent = new Intent(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST);
//					megIntent.putExtra(MessageUti.HTTP_RESPONSE,response);
//					m_context.sendBroadcast(megIntent);
				}
			}));
        } 
	}

	// GetBlockDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	private void startGetBlockDeviceListTask() {
		if(m_getGetBlockDeviceListTask == null) {
			m_getGetBlockDeviceListTask = new GetBlockDeviceListTask();
			m_rollTimer.scheduleAtFixedRate(m_getGetBlockDeviceListTask, 0, 30 * 1000);
		}
	}

	private void stopGetBlockDeviceListTask() {
		if(m_getGetBlockDeviceListTask != null) {
			m_getGetBlockDeviceListTask.cancel();
			m_getGetBlockDeviceListTask = null;
		}
	}

	public void getGetBlockDeviceListTaskAtOnceRequest(){
		GetBlockDeviceListTask task = new GetBlockDeviceListTask();
		m_rollTimer.schedule(task, 0);
	}
	
	private class GetBlockDeviceListTask extends TimerTask{
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpDevice.GetBlockDeviceList(new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(BaseResponse response) {
					//TODO, need clear if error?
					m_blockDeviceList.clear();
					if (response.isOk()) {
							BlockDeviceList result = response.getModelResult();
							for(int i = 0;i < result.BlockList.size();i++) {
								ConnectedDeviceItemModel item = new ConnectedDeviceItemModel();
								item.buildFromResult(result.BlockList.get(i));
								m_blockDeviceList.add(item);
							}
					}
//					Intent megIntent = new Intent(MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST);
//					megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
//					m_context.sendBroadcast(megIntent);
				}
			}));
        } 
	}


	// SetConnectedDeviceBlock
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setConnectedDeviceBlock(DataValue data) {
		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpDevice.SetConnectedDeviceBlock(name, mac,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
//
//								Intent megIntent = new Intent(MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK);
//								megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
//								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// SetDeviceUnblock
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDeviceUnlock(DataValue data) {
		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpDevice.SetDeviceUnlock(name, mac,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
//								Intent megIntent = new Intent(
//										MessageUti.DEVICE_SET_DEVICE_UNLOCK);
//								megIntent.putExtra(MessageUti.HTTP_RESPONSE,
//										response);
//								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// SetDeviceName
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDeviceName(DataValue data) {
		String name = (String) data.getParamByKey("DeviceName");
		String mac = (String) data.getParamByKey("MacAddress");
		EnumDeviceType type = (EnumDeviceType) data.getParamByKey("DeviceType");
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpDevice.SetDeviceName(name, mac, EnumDeviceType.antiBuild(type),
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
//
//								Intent megIntent = new Intent(MessageUti.DEVICE_SET_DEVICE_NAME);
//								megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
//								m_context.sendBroadcast(megIntent);

							}
						}));
	}

}
