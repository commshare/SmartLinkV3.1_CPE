package com.alcatel.smartlinkv3.ftp.client;

import java.io.File;
import java.util.Properties;

import android.content.Context;

import com.alcatel.smartlinkv3.ftp.client.FtpClientModel;
import com.alcatel.smartlinkv3.ftp.client.FtpManager.ERROR;

public class FtpConfig {

	public FtpClientModel m_FtpSetting = new FtpClientModel();
	private Context mContext;

	protected FtpConfig() {
	}

	protected FtpConfig(Context mContext) {
		this.mContext = mContext;
	}

	@SuppressWarnings("null")
	public FtpClientModel getFtpConfigFromFile(Context mContext) {
		FtpClientModel mFtpClient = new FtpClientModel();
		
		if (null == mContext) {
			return null;
		}

		if (!isConfigfileExist()) {
			return null;
		}

		mFtpClient.username = getUserName(mContext);
		mFtpClient.password = getPassWord(mContext);
		mFtpClient.port = Integer.parseInt(getPort(mContext));
		mFtpClient.bufferSize = Integer.parseInt(getBufferSize(mContext));
		mFtpClient.localDir = getLocalDir(mContext);
		mFtpClient.remoteDir = getRemoteDir(mContext);
		mFtpClient.ftpClientConfigDir = getFtpClientConfigDir(mContext);
		mFtpClient.ftpClientConfigFileName = getFtpClientConfigFileName(mContext);
		mFtpClient.ftpClientConfigFile = getFtpClientConfigFile(mContext);
		
		return mFtpClient;
	}

	public boolean setFtpConfigToFile(FtpClientModel m_FtpSetting) {
		if (!isConfigfileExist()) {
			return false;
		}

		if (null != m_FtpSetting) {
			if (null != m_FtpSetting.host) {
				sethost(mContext, m_FtpSetting.host);
			}
			if (0 != m_FtpSetting.port) {
				setPort(mContext, Integer.toString(m_FtpSetting.port));
			}
			if (null != m_FtpSetting.username) {
				setUserName(mContext, m_FtpSetting.username);
			}
			if (null != m_FtpSetting.password) {
				setPassWord(mContext, m_FtpSetting.password);
			}
			if (0 != m_FtpSetting.bufferSize) {
				setBufferSize(mContext,
						Integer.toString(m_FtpSetting.bufferSize));
			}
			if (null != m_FtpSetting.localDir) {
				setLocalDir(mContext, m_FtpSetting.localDir);
			}

			if (null != m_FtpSetting.remoteDir) {
				setRemoteDir(mContext, m_FtpSetting.remoteDir);
			}

			if (null != m_FtpSetting.ftpClientConfigDir) {
				setFtpClientConfigDir(mContext, m_FtpSetting.ftpClientConfigDir);
			}

			if (null != m_FtpSetting.ftpClientConfigFileName) {
				setFtpClientConfigFileName(mContext,
						m_FtpSetting.ftpClientConfigFileName);
			}
			if (null != m_FtpSetting.ftpClientConfigFile) {
				setftpClientConfigFile(mContext,
						m_FtpSetting.ftpClientConfigFile);
			}
		}
		
		return true;
	}

	public void sethost(Context mContext, String host) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.host", host);
	}

	public void setPort(Context mContext, String port) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.port", port);
	}

	public void setUserName(Context mContext, String username) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.username", username);
	}

	public void setBufferSize(Context mContext, String size) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.buffersize", size);
	}

	public void setPassWord(Context mContext, String password) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.password", password);
	}

	public void setLocalDir(Context mContext, String localDir) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.localdir", localDir);
	}

	public void setRemoteDir(Context mContext, String remoteDir) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.remotedir", remoteDir);
	}

	public void setFtpClientConfigDir(Context mContext,
			String ftpClientConfigDir) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.configdir", ftpClientConfigDir);
	}

	public void setFtpClientConfigFileName(Context mContext,
			String ftpClientConfigFileName) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.configfilename", ftpClientConfigFileName);
	}

	public void setftpClientConfigFile(Context mContext,
			String ftpClientConfigFile) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				"ftp.client.configfile", ftpClientConfigFile);
	}

	public String getHost(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.host");
	}

	public String getPort(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.port");
	}

	public String getUserName(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.username");
	}

	public String getPassWord(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.password");
	}

	public String getBufferSize(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.buffersize");
	}

	public String getLocalDir(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.localdir");
	}

	public String getRemoteDir(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.remotedir");
	}

	public String getFtpClientConfigDir(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.configdir");
	}

	public String getFtpClientConfigFileName(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.configfilename");
	}

	public String getFtpClientConfigFile(Context mContext) {
		return get_value_from_configfile(mContext, "ftp.client.configfile");
	}

	public void set_value_to_configfile(Context mContext, String key,
			String value) {
		set_value_of_properties(mContext, m_FtpSetting.ftpClientConfigFile,
				key, value);
	}

	public String get_value_from_configfile(Context mContext, String key) {
		return get_value_of_properties(mContext,
				m_FtpSetting.ftpClientConfigFile, key);
	}

	public void set_value_of_properties(Context mContext, String file,
			String key, String value) {

		PubPropertiesConfig propConf = new PubPropertiesConfig();
		Properties prop = propConf.loadConfig(mContext, file);
		// prop.list(System.out);
		prop.setProperty(key, value);
		propConf.saveConfig(mContext, file, prop);

	}

	public String get_value_of_properties(Context mContext, String file,
			String key) {
		PubPropertiesConfig propConf = new PubPropertiesConfig();
		Properties prop = propConf.loadConfig(mContext, file);
		// prop.list(System.out);
		return prop.getProperty(key);

	}

	public boolean isConfigfileExist() {
		if ((new File(m_FtpSetting.ftpClientConfigFile)).exists()) {
			return true;
		}

		return false;
	}

	public void createConfigDir() {
		File file = new File(m_FtpSetting.ftpClientConfigDir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

}