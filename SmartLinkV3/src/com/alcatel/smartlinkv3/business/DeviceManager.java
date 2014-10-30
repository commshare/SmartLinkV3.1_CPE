package com.alcatel.smartlinkv3.business;

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
	
	// GetConnectedDeviceList
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getConnectedDeviceList(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("ConnectionDevices",
				"GetConnectedDeviceList") != true)
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
										MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET);
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
