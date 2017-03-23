package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.FtpSettings;
import com.alcatel.smartlinkv3.business.sharing.HttpSharing;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.business.sharing.SDcardStatus;
import com.alcatel.smartlinkv3.business.sharing.SambaSettings;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class SharingManager extends BaseManager {

	private DlnaSettings m_dlnaSettings = new DlnaSettings();
	private SambaSettings m_sambaSettings = new SambaSettings();
	private SDCardSpace m_sdcardSpace = new SDCardSpace();
	private SDcardStatus m_sdcardStatus = new SDcardStatus();
	private FtpSettings m_ftpSettings = new FtpSettings();
	
	private Timer m_getSDCardTimer = new Timer();
	private GetSDcardStatusTask m_getSDCardTask = null;
	
	private GetFtpStatusTask m_ftp_task = null;
	private GetDlnaStatusTask m_dlna_task = null;
	
	private Timer m_getShareStatusTimer = new Timer();
	

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				startGetSDCardStatusTask();				
			} else {
				stopRollTimer();				
			}
		}
	}

	public SharingManager(Context context) {
		super(context);
	}

	@Override
	protected void clearData() {
		
	}
	
	@Override
	public void stopRollTimer() {
		if (null != m_getSDCardTask) {
			m_getSDCardTask.cancel();
			m_getSDCardTask = null;
		}
		if (null != m_ftp_task) {
			m_ftp_task.cancel();
			m_ftp_task = null;
		}
		if (null != m_dlna_task) {
			m_dlna_task.cancel();
			m_dlna_task = null;
		}		
	}

	public DlnaSettings getDlnaSettings() {
		return m_dlnaSettings;
	}

	public SambaSettings getSambaSettings() {
		return m_sambaSettings;
	}
	
	public SDCardSpace getSDCardSpace()	{
		return m_sdcardSpace;
	}
	
	public SDcardStatus getSDCardStatus()	{
		return m_sdcardStatus;
	}
	
	public FtpSettings getFtpSettings(){
		return m_ftpSettings;
	}
	
	//SetUSBcardSetting//////////////////////////////////
	
	public void switchOnDlna(){
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetDLNASettings") != true && BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") !=true){
			DataValue data = new DataValue();
			data.addParam("DlnaStatus", 1);
			data.addParam("DlnaName", m_dlnaSettings.DlnaName);
			
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
		}
		else{
		
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.SetUSBcardSetting(0,
							new IHttpFinishListener() {
								@Override
								public void onHttpRequestFinish(
										BaseResponse response) {
									String strErrcode = new String();
									int ret = response.getResultCode();
									if (ret == BaseResponse.RESPONSE_OK) {
										strErrcode = response.getErrorCode();
										if (strErrcode.length() == 0) {
	
											final int nPreStatus = m_dlnaSettings.DlnaStatus;
											m_dlnaSettings.DlnaStatus = 1;
											LegacyHttpClient.getInstance().sendPostRequest(
													new HttpSharing.SetDlnaSetting(1, m_dlnaSettings.DlnaName,
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
																			m_dlnaSettings.DlnaStatus = nPreStatus;
																		}
																	} else {
																		m_dlnaSettings.DlnaStatus = nPreStatus;
																	}

																	sendBroadcast(response, MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
																}
															}));
											
										} else {
											
										}
									} else {
										
									}

									sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
								}
							}));
		}
	}
	
	public void switchOffDlna(){
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetDLNASettings") != true && BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") !=true){
			DataValue data = new DataValue();
			data.addParam("DlnaStatus", 0);
			data.addParam("DlnaName", m_dlnaSettings.DlnaName);
			
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
		}
		else{
		
			final int nPreStatus = m_dlnaSettings.DlnaStatus;
			m_dlnaSettings.DlnaStatus = 0;
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.SetDlnaSetting(0, m_dlnaSettings.DlnaName,
							new IHttpFinishListener() {
								@Override
								public void onHttpRequestFinish(
										BaseResponse response) {
									String strErrcode = new String();
									int ret = response.getResultCode();
									if (ret == BaseResponse.RESPONSE_OK) {
										strErrcode = response.getErrorCode();
										if (strErrcode.length() == 0) {
											
											LegacyHttpClient.getInstance().sendPostRequest(
													new HttpSharing.SetUSBcardSetting(1,
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

																	sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
																}
															}));
											
										} else {
											m_dlnaSettings.DlnaStatus = nPreStatus;
										}
									} else {
										m_dlnaSettings.DlnaStatus = nPreStatus;
									}

									sendBroadcast(response, MessageUti.SHARING_SET_DLNA_SETTING_SPECIAL_REQUSET);
								}
							}));
		}
	}
	
	public void setUSBcardSetting(DataValue data){
		int status = (Integer) data.getParamByKey("USBcardStatus");
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.SetUSBcardSetting(status,
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

								sendBroadcast(response, MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
							}
						}));
	}
	
	// SetSambaSetting
		// //////////////////////////////////////////////////////////////////////////////////////////
		public void setFtpSetting(DataValue data) {
			if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"SetFtpStatus") != true)
				return;

			int status = (Integer) data.getParamByKey("FtpStatus");
			final int nPreStatus = m_ftpSettings.FtpStatus;
			m_ftpSettings.FtpStatus = status;
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.SetFtpSetting( status,
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
											m_ftpSettings.FtpStatus = nPreStatus;
										}
									} else {
										m_ftpSettings.FtpStatus = nPreStatus;
									}

									sendBroadcast(response, MessageUti.SHARING_SET_FTP_SETTING_REQUSET);
								}
							}));
		}

		// GetSambaSetting
		// //////////////////////////////////////////////////////////////////////////////////////////
		public void getFtpSetting(DataValue data) {
			if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"GetFtpStatus") != true){
				sendBroadcast(BaseResponse.EMPTY, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
				return;
			}
				

			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.GetFtpSetting(
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
									sendBroadcast(response, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
								}
							}));
		}
	
	
	// SetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetSambaStatus") != true)
			return;

		int status = (Integer) data.getParamByKey("SambaStatus");
		final int nPreStatus = m_sambaSettings.SambaStatus;
		m_sambaSettings.SambaStatus = status;
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.SetSambaSetting(status,
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
										m_sambaSettings.SambaStatus = nPreStatus;
									}
								} else {
									m_sambaSettings.SambaStatus = nPreStatus;
								}

								sendBroadcast(response, MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET);
							}
						}));
	}

	// GetSambaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getSambaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetSambaStatus") != true)
			return;

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.GetSambaSetting(
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
								sendBroadcast(response, MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET);
							}
						}));
	}

	// SetDlnaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setDlnaSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"SetDLNASettings") != true && BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") !=true){
			return;
		}

		int status = (Integer) data.getParamByKey("DlnaStatus");
		String name = (String) data.getParamByKey("DlnaName");
		final int nPreStatus = m_dlnaSettings.DlnaStatus;
		m_dlnaSettings.DlnaStatus = status;
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.SetDlnaSetting(status, name,
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
										m_dlnaSettings.DlnaStatus = nPreStatus;
									}
								} else {
									m_dlnaSettings.DlnaStatus = nPreStatus;
								}

								sendBroadcast(response, MessageUti.SHARING_SET_DLNA_SETTING_REQUSET);
							}
						}));
	}

	// GetDlnaSetting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getDlnaSetting(DataValue data) {
		if(BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") !=true)
		{
			if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"GetDLNASettings") != true){
				sendBroadcast(BaseResponse.EMPTY, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
				return;
			}
		}

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.GetDlnaSetting(
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
								sendBroadcast(response, MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
							}
						}));
	}

	// GetSDCardSpace
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getSDCardSpace(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetSDCardSpace") != true)
			return;

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.GetSDCardSpace(
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
								sendBroadcast(response, MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET);
							}
						}));
	}
	
	// GetSDcardStatus
	// //////////////////////////////////////////////////////////////////////////////////////////	
	
	private void startGetSDCardStatusTask() {
		if (m_getSDCardTask == null) {
			m_getSDCardTask = new GetSDcardStatusTask();
			m_getSDCardTimer.scheduleAtFixedRate(
					m_getSDCardTask, 0, 10000);
		}
	}
	
	class GetSDcardStatusTask extends TimerTask {		
		@Override
		public void run() {
			getSDcardStatus(null);
		}
	}
	
	public void getSDcardStatus(DataValue data) {
//		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
//				"GetSDcardStatus") != true)
//			return;

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpSharing.GetSDcardStatus(
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_sdcardStatus = response
												.getModelResult();
									} else {
										m_sdcardStatus.clear();
									}
								} else {
									m_sdcardStatus.clear();
								}
								sendBroadcast(response, MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET);
							}
						}));
	}
	
	class GetFtpStatusTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.GetFtpSetting(
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
									sendBroadcast(response, MessageUti.SHARING_GET_FTP_SETTING_REQUSET);
								}
							}));
		}
		
	}
	
	class GetDlnaStatusTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSharing.GetDlnaSetting(
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
									sendBroadcast(response, MessageUti.SHARING_GET_DLNA_SETTING_REQUSET);
								}
							}));
		}
		
	}
	
	public void startGetFtpStatus(){
		if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetFtpStatus") != true){
			return;
		}
		
		if(m_ftp_task == null) {
			m_ftp_task = new GetFtpStatusTask();
			m_getShareStatusTimer.scheduleAtFixedRate(m_ftp_task, 0, 5 * 1000);
		}
	}
	
	public void startGetDlnaStatus(){
		if(BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900") !=true)
		{
			if (FeatureVersionManager.getInstance().isSupportApi("Sharing",
					"GetDLNASettings") != true){
				return;
			}
		}
		
		if(m_dlna_task == null) {
			m_dlna_task = new GetDlnaStatusTask();
			m_getShareStatusTimer.scheduleAtFixedRate(m_dlna_task, 0, 5 * 1000);
		}
	}

}
