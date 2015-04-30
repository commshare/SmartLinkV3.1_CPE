package com.alcatel.smartlinkv3.ftp.client;

public class FtpClientModel {
	public String host;
	public int port;
	public int bufferSize = 1024 * 4;

	public String username;
	public String password;

	public String localDir;
	public String remoteDir;

	public String ftpClientConfigDir;
	public String ftpClientConfigFileName;
	public String ftpClientConfigFile;

	public FtpClientModel() {
		host = "192.168.1.103";
		port = 2221;
		bufferSize = 1024 * 4;
		username = "admin";
		password = "admin";

		localDir = "/mnt/sdcard/ftpconf/";
		remoteDir = "/"; // root dir

		ftpClientConfigDir = "/mnt/sdcard/ftpconf/";
		ftpClientConfigFileName = "ftpClientConfig.properties";
		ftpClientConfigFile = ftpClientConfigDir + ftpClientConfigFileName;
	}

}