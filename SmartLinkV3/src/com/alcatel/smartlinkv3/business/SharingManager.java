package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.business.service.DlnaSettings;
import com.alcatel.smartlinkv3.business.service.FtpSettings;
import com.alcatel.smartlinkv3.business.service.HttpService;
import com.alcatel.smartlinkv3.business.service.SambaSettings;
import com.alcatel.smartlinkv3.business.service.ServiceStateResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.ServiceState;
import com.alcatel.smartlinkv3.common.ENUM.ServiceType;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;

public class SharingManager extends BaseManager {

	private ServiceState m_dlnaState = ServiceState.Disabled;
	private ServiceState m_sambaState = ServiceState.Disabled;
	private ServiceState m_ftpState = ServiceState.Disabled;

	private DlnaSettings m_dlnaSettings = new DlnaSettings();
	
	private SambaSettings m_sambaSettings = new SambaSettings();
	private FtpSettings m_ftpSettings = new FtpSettings();

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {

	}

	public SharingManager(Context context) {
		super(context);
	}

	@Override
	protected void clearData() {

	}

	@Override
	protected void stopRollTimer() {

	}

	public ServiceState getDlnaServiceState() {
		return m_dlnaState;
	}

	public ServiceState getSambaServiceState() {
		return m_sambaState;
	}

	public ServiceState getFtpServiceState() {
		return m_ftpState;
	}
	
	public DlnaSettings getDlnaSettings() {
		return m_dlnaSettings;
	}
	
	public SambaSettings getSambaSettings() {
		return m_sambaSettings;
	}
	
	public FtpSettings getFtpSettings() {
		return m_ftpSettings;
	}
	
	private void getStateByServiceType(ServiceType type, ServiceState state) {

		switch (type) {
		case Samba:
			m_sambaState = state;
			break;
		case DLNA:
			m_dlnaState = state;
			break;
		case FTP:
			m_ftpState = state;
			break;
		default:
			break;

		}
	}

	// Set service state
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void setServiceState(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"SetServiceState") != true)
			return;

		final int nServiceType = (Integer) data.getParamByKey("ServiceType");
		final int nState = (Integer) data.getParamByKey("State");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.SetServiceState("10.3", nServiceType, nState,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {

										getStateByServiceType(
												ServiceType.build(nServiceType),
												ServiceState.build(nState));

									} else {

									}
								} else {

								}

								Intent megIntent = new Intent(
										MessageUti.SERVICE_SET_SERVICE_STATE_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}*/

	// get service state
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void getServiceState(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"GetServiceState") != true)
			return;

		int nServiceType = (Integer) data.getParamByKey("ServiceType");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.GetServiceState("10.4", nServiceType,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {

								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										ServiceStateResult result = response
												.getModelResult();

										ServiceType type = ServiceType
												.build(result.ServiceType);
										ServiceState state = ServiceState
												.build(result.State);

										getStateByServiceType(type, state);

									} else {

									}
								} else {
									// Log
								}

								Intent megIntent = new Intent(
										MessageUti.SERVICE_GET_SERVICE_STATE_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}*/

	// SetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void setSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"Samba.SetSettings") != true)
			return;

		int nDevType = (Integer) data.getParamByKey("DevType");
		int nAnonymous = (Integer) data.getParamByKey("Anonymous");
		String strUserName = (String) data.getParamByKey("UserName");
		String strPassword = (String) data.getParamByKey("Password");
		int nAuthType = (Integer) data.getParamByKey("AuthType");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.SetSambaSetting("10.6", nDevType, nAnonymous,
						strUserName, strPassword, nAuthType,
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
										MessageUti.SERVICE_SET_SAMBA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}*/

	// GetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void getSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"Samba.GetSettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.GetSambaSetting("10.5",
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
										MessageUti.SERVICE_GET_SAMBA_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}*/

	// SetDlnaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDlnaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetDLNASettings") != true)
			return;

		int status = (Integer) data.getParamByKey("DlnaStatus");
		String name = (String) data.getParamByKey("DlnaName");
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.SetDlnaSetting("14.2", status, name,
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
				new HttpService.GetDlnaSetting("14.1",
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

	// SetFtpSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void setFtpSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"FTP.SetSettings") != true)
			return;

		int nDevType = (Integer) data.getParamByKey("DevType");
		int nAnonymous = (Integer) data.getParamByKey("Anonymous");
		String strUserName = (String) data.getParamByKey("UserName");
		String strPassword = (String) data.getParamByKey("Password");
		int nAuthType = (Integer) data.getParamByKey("AuthType");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.SetFtpSetting("10.10", nDevType, nAnonymous,
						strUserName, strPassword, nAuthType,
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
										MessageUti.SERVICE_SET_FTP_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}*/

	// GetFtpSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
/*	public void getFtpSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Services",
				"FTP.GetSettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpService.GetFtpSetting("10.9",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_ftpSettings = response
												.getModelResult();
									} else {
										m_ftpSettings.clear();
									}
								} else {
									m_ftpSettings.clear();
								}
								Intent megIntent = new Intent(
										MessageUti.SERVICE_GET_FTP_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}
*/
}
