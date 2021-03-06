package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.business.lan.HttpLan;
import com.alcatel.smartlinkv3.business.lan.LanInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LanManager extends BaseManager {

	private LanInfo m_lanInfo=new LanInfo();
	public LanManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
					getLaninfo(null);
			}else {
				clearData();
				BusinessMannager.getInstance().getSystemInfoModel().clear();
			}
		}
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub

		m_lanInfo.clear();
	}

	@Override
	protected void stopRollTimer() {
		// TODO Auto-generated method stub

	}

	//get Lan information
	public void getLaninfo(DataValue dataValue){
		if(!FeatureVersionManager.getInstance().isSupportApi("LAN", "GetLanSettings")){
			return;
		}

		boolean bCPEWifiConnected = DataConnectManager.getInstance()
				.getCPEWifiConnected();
		if (bCPEWifiConnected){
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpLan.getLanSettingsRequest("11.1", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int ret = response.getResultCode();
							String strErrcode = response.getErrorCode();
							if (ret == BaseResponse.RESPONSE_OK
									&& strErrcode.length() == 0) {
								m_lanInfo = response
										.getModelResult();
								BusinessMannager.getInstance().getSystemInfoModel().
								setIP(m_lanInfo.getIPv4IPAddress());
								BusinessMannager.getInstance().getSystemInfoModel().
								setSubnet(m_lanInfo.getSubnetMask());
								BusinessMannager.getInstance().getSystemInfoModel().
								setMacAddress(m_lanInfo.getMacAddress());
							}
							
							Intent megIntent= new Intent(MessageUti.LAN_GET_LAN_SETTINGS);
			                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
			                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
			    			m_context.sendBroadcast(megIntent);
						}
					}));
		}
	}
}
