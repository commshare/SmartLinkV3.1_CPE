package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.HttpSharing;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.business.sharing.SambaSettings;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;

public class SharingManager extends BaseManager {

	private DlnaSettings m_dlnaSettings = new DlnaSettings();
	private SambaSettings m_sambaSettings = new SambaSettings();
	private SDCardSpace m_sdcardSpace = new SDCardSpace();

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
	}

	public SharingManager(Context context) {
		super(context);
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void stopRollTimer() {
		// TODO Auto-generated method stub

	}

	public DlnaSettings getDlnaSettings() {
		return m_dlnaSettings;
	}

	public SambaSettings getSambaSettings() {
		return m_sambaSettings;
	}

	// SetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetSambaStatus") != true)
			return;

		int status = (Integer) data.getParamByKey("SambaStatus");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpSharing.SetSambaSetting("14.4", status,
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
										MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// GetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetSambaStatus") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpSharing.GetSambaSetting("10.5",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_sambaSettings = response
												.getModelResult();
									} else {
										m_sambaSettings.clear();
									}
								} else {
									m_sambaSettings.clear();
								}
								Intent megIntent = new Intent(
										MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// SetDlnaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDlnaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetDLNASettings") != true)
			return;

		int status = (Integer) data.getParamByKey("DlnaStatus");
		String name = (String) data.getParamByKey("DlnaName");
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpSharing.SetDlnaSetting("14.2", status, name,
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
										MessageUti.SHARING_SET_DLNA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// GetDlnaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getDlnaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpSharing.GetDlnaSetting("14.1",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_dlnaSettings = response
												.getModelResult();
									} else {
										m_dlnaSettings.clear();
									}
								} else {
									m_dlnaSettings.clear();
								}
								Intent megIntent = new Intent(
										MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// GetSDCardSpace
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void GetSDCardSpace(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetSDCardSpace") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpSharing.GetSDCardSpace("14.7",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_sdcardSpace = response
												.getModelResult();
									} else {
										m_sdcardSpace.clear();
									}
								} else {
									m_sdcardSpace.clear();
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
