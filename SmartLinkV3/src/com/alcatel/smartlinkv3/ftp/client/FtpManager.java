package com.alcatel.smartlinkv3.ftp.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.alcatel.smartlinkv3.ftp.client.FtpConfig;
import com.alcatel.smartlinkv3.ftp.client.pubLog;

public class FtpManager {

	FtpClientModel m_ftpClient = null;
	pubLog logger = null;
	FtpClientProxy ftpProxy = null;
	Context mContext = null;

	volatile boolean isInit = false;
	volatile boolean isConnect = false;
	volatile boolean isLogin = false;

	FtpManagerIRetrieveListener ftpManagerListener;

	public FtpManager() {

	}

	protected FtpManager(Context mContext) {
		this.mContext = mContext;
	}

	public void setConfig(Context mContext, FtpClientModel m_config) {
		if (!isInit) {
			logger.w("FtpManager module is not initialization yet!");
		}

		ftpProxy.setConfig(m_config);
		// backup the setting to the config file
		boolean iRet = setFtpConfigToFile(mContext, m_config);

		if (!iRet) {
			logger.w("save ftp config to file is failure!");
		}
	}

	public int init(Context mContext) throws IOException {
		if (isInit) {
			return 0;
		}

		// check network

		// read the config file
		FtpConfig config = new FtpConfig(mContext);
		m_ftpClient = config.getFtpConfigFromFile(mContext);

		if (null == m_ftpClient) {
			m_ftpClient = new FtpClientModel(); // use default settting
		}

		// check parament

		// set log
		logger = new pubLog();
		logger.setTag("ftpClient");
		logger.setDebug(true);

		ftpProxy = new FtpClientProxy();
		// ftpProxy.setConfig(m_ftpClient);

		if (null != ftpProxy) {
			pubLog proxyLog = ftpProxy.getlogInstance();
			proxyLog.setDebug(true);
			// pubLog.setDebug(true);
			proxyLog.setTag("FtpProxy");
		}

		// set the ftp client proxy config

		// check the config dir and file
		createDirsFiles(mContext);

		isConnect = false;
		isLogin = false;
		isInit = true;

		return 0;
	}

	public boolean isFtpConnected() throws IOException {
		return ftpProxy.isFtpConnected();
	}
	
	public boolean setFtpConfigToFile(Context mContext,
			FtpClientModel m_FtpClient) {

		if (null == mContext) {
			logger.w("the context is null!");
			return false;
		}

		if (null == m_FtpClient) {
			logger.w("the model data is null!");
			return false;
		}

		m_ftpClient = m_FtpClient;

		FtpConfig cfg = new FtpConfig();

		boolean iRet = cfg.setFtpConfigToFile(m_ftpClient);

		if (!iRet) {
			ftpManagerListener.onStatus(FtpMessage.CONFIG_FILE_NO_FOUND,
					ERROR.FILE_NO_FOUNT);
			logger.w(FtpMessage.CONFIG_FILE_NO_FOUND);
			return false;
		}

		return true;
	}

	private void createDirsFiles(Context mContext) throws IOException {
		boolean iRet = false;

		iRet = createLocalFolder(m_ftpClient.ftpClientConfigDir);

		if (!iRet) {
			logger.w("create ftp config folder fail!");
		}

		// copy ftp client setting file from assets
		copyfile(mContext, m_ftpClient.ftpClientConfigFileName,
				m_ftpClient.ftpClientConfigDir,
				m_ftpClient.ftpClientConfigFileName);

	}

	public void copyfile(Context mContext, String ASSETS_NAME, String savePath,
			String saveName) {
		String filename = savePath + "/" + saveName;

		File dir = new File(savePath);
		if (!dir.exists())
			dir.mkdir();
		try {
			if (!(new File(filename)).exists()) {
				InputStream is = mContext.getResources().getAssets()
						.open(ASSETS_NAME);
				FileOutputStream fos = new FileOutputStream(filename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean createLocalFolder(String strFolder) {
		File file = new File(strFolder);

		if (!file.exists()) {
			if (!file.mkdir())
				return false;
		}

		return true;
	}

	// reset all config to initial status
	public void reset(Context mContext) throws IOException {
		createDirsFiles(mContext);
		new FtpConfig().setFtpConfigToFile(new FtpClientModel());
	}

	public void setTransferFtpListener(FtpTransferIRetrieveListener listener) {
		ftpProxy.listener = listener;
	}

	public void setFtpManagerListener(FtpManagerIRetrieveListener listener) {
		this.ftpManagerListener = listener;
	}

	public void setFtpStopDownload() {
		ftpProxy.setStopDownload();
	}

	public List<FTPFile> showListFile(String remoteDir) throws Exception {
		return ftpProxy.getFileList(remoteDir);
	}

	public boolean ftpConnect() {
		boolean iRet = false;
		
		iRet = ftpProxy.connect();
		
		if(iRet){
			isConnect = true;
			return true;
		}else{
			isConnect = false;
			return false;
		}
	}

	public boolean ftpLogin() {
		boolean iRet = false;

		iRet = ftpProxy.login();

		if (iRet) {
			isLogin = true;
			return true;
		} else {
			isLogin = false;
			return false;
		}
	}
	
	public boolean connectLogin() {
		boolean iRet = false;

		ftpManagerListener.onStart();

		iRet = ftpProxy.connect();

		if (!iRet) {
			ftpManagerListener.onStatus(FtpMessage.CONNECT_FAIL,
					ERROR.CONNECT_ERROR);
			return false;
		}
		ftpManagerListener.onStatus(FtpMessage.CONNECT_SUCCESS, ERROR.SUCCESS);

		isConnect = true;

		iRet = ftpProxy.login();

		if (!iRet) {
			ftpManagerListener.onStatus(FtpMessage.USER_LOGIN_FAIL,
					ERROR.LOGIN_ERROR);
			return false;
		}

		ftpManagerListener.onStatus(FtpMessage.USER_LOGIN_SUCCESS,
				ERROR.SUCCESS);

		isLogin = true;
		return iRet;
	}

	public void close() {
		ftpProxy.close();
		isInit = false;
		isConnect = false;
		isLogin = false;
	}

	public boolean download(String localFile, String remoteFile) {
		boolean result = false;

		if (!isLogin) {
			ftpManagerListener.onStatus(FtpMessage.FILE_DOWNLOAD_ERROR,
					ERROR.FILE_DOWNLOAD_ERROR);
			logger.w("download failure,not login yet!");
			return false;
		}

		try {
			result = ftpProxy.downloadAndsubFiles(localFile, remoteFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result) {
			ftpManagerListener.onStatus(FtpMessage.FILE_DOWNLOAD_SUCCESS,
					ERROR.SUCCESS);
		}

		return result;
	}

	public boolean deleteFiles(String remoteFilePath) {
		boolean iStatus = false;

		if (!isLogin) {
			ftpManagerListener.onStatus(FtpMessage.FILE_DOWNLOAD_ERROR,
					ERROR.FILE_DOWNLOAD_ERROR);
		}

		try {
			iStatus = ftpProxy.deleteFoldAndsubFiles(remoteFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (iStatus) {
			ftpManagerListener.onStatus(FtpMessage.FILE_DOWNLOAD_SUCCESS,
					ERROR.SUCCESS);
			logger.i("delete file [" + remoteFilePath + "]" + "success!");
		} else {
			logger.i("delete file [" + remoteFilePath + "]" + "fail!");
		}

		return iStatus;
	}

	public boolean upload(String localFile, String remoteFile)
			throws Exception {
		boolean iStatus = false;

		if (!isLogin) {
			ftpManagerListener.onStatus(FtpMessage.FILE_UPLOAD_ERROR,
					ERROR.FILE_UPLOAD_ERROR);
		}

		iStatus = ftpProxy.uploadAndsubFiles(localFile, remoteFile);

		if (iStatus) {
			ftpManagerListener.onStatus(FtpMessage.FILE_UPLOAD_SUCCESS,
					ERROR.SUCCESS);
		}

		return iStatus;
	}

	public boolean rename(String fromFile, String toFile) throws IOException {
		boolean iStatus = false;

		if (!isLogin) {
			return false;
		}

		iStatus = ftpProxy.renameFile(fromFile, toFile);

		if (iStatus) {
			ftpManagerListener.onStatus(FtpMessage.FILE_RENAME_SUCCESS,
					ERROR.SUCCESS);
		}

		return iStatus;
	}
	
	
	public boolean move(String fromFile, String toFile) throws IOException {
		boolean iStatus = false;

		if (!isLogin) {
			return false;
		}
		
		iStatus = ftpProxy.moveFile(fromFile, toFile);

		if (iStatus) {
			ftpManagerListener.onStatus(FtpMessage.FILE_RENAME_SUCCESS,
					ERROR.SUCCESS);
		}

		return iStatus;
	}
	

	public boolean createFolder(String remotePath) throws IOException {
		boolean result = false;
		
		if (!isLogin) {
			return false;
		}

		result = ftpProxy.mkdir(remotePath);

		if (!result) {
			return false;
		}
		
		
		return result;
	}

	public int getReply() {
		return ftpProxy.getReply();
	}
	
	public static class ERROR {
		public static final int CONNECT_ERROR = 9001;
		public static final int LOGIN_ERROR = 9002;
		public static final int FILE_NO_FOUNT = 9003;
		public static final int FILE_DOWNLOAD_ERROR = 9004;
		public static final int FILE_UPLOAD_ERROR = 9005;

		public static final int SUCCESS = 0;
	}

}
