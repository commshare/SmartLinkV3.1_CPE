package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.business.device.HttpDevice;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;

public class DeviceManager extends BaseManager {

	private ConnectedDeviceList m_connectedDeviceList = new ConnectedDeviceList();
	private BlockDeviceList m_blockDeviceList = new BlockDeviceList();

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
	}

	public DeviceManager(Context context) {
		super(context);
	}

	@Override
	protected void clearData() {

	}

	@Override
	protected void stopRollTimer() {

	}	
	
	public ConnectedDeviceList getConnectedDeviceList()
	{
		return m_connectedDeviceList;
	}
	
	public BlockDeviceList getBlockDeviceList()
	{
		return m_blockDeviceList;
	}

	// GetConnectedDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getConnectedDeviceList(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi(
				"ConnectionDevices", "GetConnectedDeviceList") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.GetConnectedDeviceList("12.1",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_connectedDeviceList = response
												.getModelResult();
									} else {
										m_connectedDeviceList.clear();
									}
								} else {
									m_connectedDeviceList.clear();
								}
								Intent megIntent = new Intent(
										MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// GetBlockDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getBlockDeviceList(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi(
				"ConnectionDevices", "GetBlockDeviceList") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.GetBlockDeviceList("12.2",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_blockDeviceList = response
												.getModelResult();
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
		int type = (Integer) data.getParamByKey("DeviceType");
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpDevice.SetDeviceName("12.5", name, mac, type,
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
