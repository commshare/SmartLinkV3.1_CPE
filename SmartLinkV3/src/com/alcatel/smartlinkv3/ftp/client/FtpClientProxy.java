package com.alcatel.smartlinkv3.ftp.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


import android.R.bool;
import android.util.Log;

public class FtpClientProxy {

	FTPClient ftpClient = new FTPClient();
	pubLog logger = new pubLog();

	FtpClientModel config;
	FtpTransferIRetrieveListener listener;
	volatile boolean stopDownload = false;

	public FtpClientProxy() {

	}

	protected FtpClientProxy(FtpClientModel cfg) {
		this.config = cfg;
	}

	public int init() {

		return 0;
	}

	public FtpClientModel getConfig() {
		return config;
	}

	public void setConfig(FtpClientModel config) {
		this.config = config;
	}

	public pubLog getlogInstance() {
		return logger;
	}

	public void setFtpListener(FtpTransferIRetrieveListener listener) {
		this.listener = listener;
	}

	public void setStopDownload() {
		stopDownload = true;
	}

	public void setStartDownload() {
		stopDownload = false;
	}

	public boolean connect() {
		try {
			FTPClientConfig ftpClientConfig = new FTPClientConfig(
					FTPClientConfig.SYST_UNIX);
			ftpClientConfig.setLenientFutureDates(true);
			ftpClient.configure(ftpClientConfig);
			logger.i("connect host = " + config.host + ",port = " + config.port);
			ftpClient.connect(config.host, config.port);
			int reply = this.ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				return false;
			}
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean login() {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			boolean b = ftpClient.login(config.username, config.password);
			if (!b) {
				return false;
			}
			ftpClient.setFileType(FTPClient.FILE_STRUCTURE);
			ftpClient.enterLocalPassiveMode();
			// ftpClient.setControlEncoding("UTF-8");
			// ftpClient.setConnectTimeout(5000);
			// ftpClient.enterLocalActiveMode();
			// ftpClient.enterRemotePassiveMode();
			// ftpClient.enterRemoteActiveMode(InetAddress.getByName(config.address),config.port);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public FTPFile[] getFTPFiles(String remoteDir) {
		try {
			return ftpClient.listFiles(remoteDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public FTPFile getFTPFile(String remotePath) {
		try {
			Log.d("", "getFTPFile.........." + remotePath);
			// mlistFile() has bugs or need server support,it returns null sometimes
			FTPFile f = ftpClient.mlistFile(remotePath);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("", "getFTPFile null..........");
		return null;
	}

	public InputStream getRemoteFileStream(String remotePath) {
		InputStream ios;
		try {
			ios = ftpClient.retrieveFileStream(remotePath);
			return ios;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setRestartOffset(long len) {
		// file offset position
		ftpClient.setRestartOffset(len); 
	}

	public boolean isDone() {
		try {
			return ftpClient.completePendingCommand();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkFileExist(String filePath) throws Exception {
		boolean flag = false;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("File is not exist!");
		} else {
			flag = true;
		}
		return flag;
	}

	public Boolean changeDirectory(String remoteFoldPath) throws Exception {
		return ftpClient.changeWorkingDirectory(remoteFoldPath);
	}

	boolean createLocalFolder(String strFolder) {
		File file = new File(strFolder);

		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public FTPFile getFileByName(String remoteDir, String name) {
		FTPFile[] files = this.getFTPFiles(remoteDir);
		if (files != null) {
			for (FTPFile f : files) {
				if (name.equalsIgnoreCase(f.getName())) {
					return f;
				}
			}
		}
		return null;
	}
	
	public boolean moveFile(String fromFile, String toFile) throws IOException {
		if (!ftpClient.rename(fromFile, toFile)) {
			return false;
		}
		return true;
	}
	
	
	public String getPathFromURL(String remoteURL) {
		String path = "";
		// get file name from the path
		String fName = remoteURL.trim();
		String fileName = fName.substring(fName.lastIndexOf("/") + 1);

		path = remoteURL.replace(fileName, "");

		return path;
	}
	
	public FTPFile getParentFtpFile(String remotePath){
		FTPFile ftp = null;
		
        		
		return ftp;
	}
	
	// TODO
	public boolean isDirectory(String remote) throws IOException{
		
		FTPFile remoteFile = ftpClient.mlistFile(remote);
		
		if(remote == null){
			
		}
		
		return true;
	}
	
	public boolean downloadAndsubFiles(String local, String remote)
			throws Exception {
		FtpDownloadStatus result = FtpDownloadStatus.Download_From_Break_Failed;
		boolean success = false;

		// mlistFile() has bugs or permision problem?,it returns null sometimes.
		/*
		 * FTPFile remoteFile = ftpClient.mlistFile(remote);
		 * 
		 * if (null == remoteFile) { logger.w("mlistFile() return null!");
		 * return false; }
		 * 
		 * if (remoteFile.isFile()) { result = download(local, remote); if
		 * (result == FtpDownloadStatus.Download_New_Success) success = true;
		 * return success; }
		 * 
		 * if (remoteFile.isDirectory()) { createLocalFolder(local); }
		 */
		
		
		
		if(true){
			FTPFile remoteFile = ftpClient.mlistFile("/sdcard/231.jpg");
			if(remoteFile == null){
				logger.w("remote test is null");
			}
			if(remoteFile.isDirectory()){
				logger.w("remote test is a Directory!");
			}
			logger.w("remote test is a file ");
			
			
			String tmp = getPathFromURL(remote);
			logger.w("remote test is a file ");
			return true;
		}
		
		
		FTPFile[] list = ftpClient.listFiles(remote);

		if (list == null) {
			logger.w("getFileList() return null!");
			return false;
		}
		
		logger.i("list.length: " + list.length);
		
		if (list.length != 1) {
			logger.w("this is a derectory?");
			createLocalFolder(local);
		} else {
			result = download(local, remote);
		/*	if (result != FtpDownloadStatus.Download_New_Success) {
				logger.w("download file [" + remote + "]" + "fail!");
				return false;
			}
			return true;*/
		}

		for (FTPFile ftpFile : list) {
			String filename = ftpFile.getName();
			if (filename.equals(".") || filename.equals("..")) {
				// skip parent directory and the directory itself
				continue;
			}

			logger.i("download file name : " + filename);
			if (ftpFile.isDirectory()) {
				createLocalFolder(local + "/" + filename);
				logger.i("create local dir : " + local + "/" + filename);
				success = downloadAndsubFiles(local + "/" + filename, remote
						+ "/" + filename);

				if (!success) {
					logger.w("download file 1 [" + remote + "/" + filename
							+ "]" + "fail!");
					break;
				}
			} else {
				result = download(local + "/" + filename, remote + "/"
						+ filename);

				if (result != FtpDownloadStatus.Download_New_Success) {
					logger.w("download file 2 [" + remote + "/" + filename
							+ "]" + "fail!");
					break;
				}
			}
		}

		success = true;
		return success;
	}

	public FtpDownloadStatus download(String local, String remote)
			throws IOException {
		FtpDownloadStatus result;
		listener.onStart();

		FTPFile[] files = ftpClient.listFiles(new String(
				remote.getBytes("GBK"), "iso-8859-1"));

		if (files.length != 1) {
			logger.v("remote file" + remote + " is not exist!");
			return FtpDownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);

		File parentDir = f.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdir();
		}

		logger.i("new File :" + local);

		if (f.exists()) {
			long localSize = f.length();
			if (localSize >= lRemoteSize) {
				logger.v("fail,the size of local file is bigger then remote file!");
				listener.onError("local has the file yet!", 0);
				return FtpDownloadStatus.Local_Bigger_Remote;
			}

			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(new String(remote
					.getBytes("GBK"), "iso-8859-1"));

			byte[] bytes = new byte[config.bufferSize];

			long step = lRemoteSize / 100;
			long process = localSize / step;
			int c;

			while ((c = in.read(bytes)) != -1) {
				if (stopDownload) {
					listener.onCancel("");
					break;
				}

				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.v("Download Process:" + process);
					listener.onTrack(process);
					// TODO: update the process
				}
			}

			in.close();
			out.close();

			boolean isDo = ftpClient.completePendingCommand();

			if (isDo) {
				result = FtpDownloadStatus.Download_From_Break_Success;
			} else {
				result = FtpDownloadStatus.Download_From_Break_Failed;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(new String(remote
					.getBytes("GBK"), "iso-8859-1"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				if (stopDownload) {
					listener.onCancel("");
					break;
				}

				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.v("Download Process:" + process);
					listener.onTrack(process);
					// TODO: update the process
				}
			}

			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();

			if (upNewStatus) {
				result = FtpDownloadStatus.Download_New_Success;
				listener.onDone();
			} else {
				result = FtpDownloadStatus.Download_New_Failed;
			}
		}

		return result;
	}

	public FtpUploadStatus upload(String local, String remote)
			throws IOException {
		ftpClient.setControlEncoding("GBK");
		FtpUploadStatus result;

		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);

			if (remoteFileName.equalsIgnoreCase("")) {
				logger.v("Errer : Remote file name is empty!");
				return FtpUploadStatus.Parameter_Error;
			}

			if (createDirecroty(remote, ftpClient) == FtpUploadStatus.Create_Directory_Fail) {
				return FtpUploadStatus.Create_Directory_Fail;
			}
		}

		// check the remote file if exist
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName
				.getBytes("GBK"), "iso-8859-1"));

		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return FtpUploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return FtpUploadStatus.Remote_Bigger_Local;
			}

			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			if (result == FtpUploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return FtpUploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {

			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		return result;
	}

	public List<FTPFile> getFileList(String remotePath) throws Exception {
		List<FTPFile> ftpfiles = Arrays.asList(ftpClient.listFiles(remotePath));
		return ftpfiles;
	}

	public List<FTPFile> getFileList() throws Exception {
		List<FTPFile> ftpfiles = Arrays.asList(ftpClient.listFiles());
		return ftpfiles;
	}

	public Boolean deleteFtpServerFile(String remoteFilePath) throws Exception {
		return ftpClient.deleteFile(remoteFilePath);
	}

	public boolean createFold(String remoteFoldPath) throws Exception {

		boolean flag = ftpClient.makeDirectory(remoteFoldPath);
		if (!flag) {
			throw new Exception("create fold fail!");
		}
		return false;
	}

	public boolean deleteFold(String remoteFoldPath) throws Exception {

		return ftpClient.removeDirectory(remoteFoldPath);
	}

	public boolean deleteFoldAndsubFiles(String remoteFoldPath)
			throws Exception {

		boolean success = false;
		List<FTPFile> list = this.getFileList(remoteFoldPath);

		if (list == null || list.size() == 0) {
			return deleteFold(remoteFoldPath);
		}

		for (FTPFile ftpFile : list) {

			String name = ftpFile.getName();
			if (ftpFile.isDirectory()) {
				success = deleteFoldAndsubFiles(remoteFoldPath + "/" + name);
				if (!success)
					break;
			} else {
				success = deleteFtpServerFile(remoteFoldPath + "/" + name);
				if (!success)
					break;
			}
		}

		if (!success)
			return false;
		success = deleteFold(remoteFoldPath);
		return success;
	}

	public boolean deleteFiles(String remoteFilePath) throws Exception {
		boolean success = false;
		boolean result = false;

		/*
		 * FTPFile remoteFile = ftpClient.mlistFile(remoteFilePath); if
		 * (remoteFile.isFile()) { result = deleteFtpServerFile(remoteFilePath);
		 * if (result == true) success = true; return success; }
		 */

		FTPFile[] list = ftpClient.listFiles(remoteFilePath);

		if (list == null) {
			logger.w("getFileList() return null!");
			return false;
		}
		// it's a file
		if (list.length == 1) {
			logger.i("file!");
			result = deleteFtpServerFile(remoteFilePath);
			return result;
		}

		// it's a empty directory
		if (list.length == 0) {
			logger.i("directory!");
			result = deleteFoldAndsubFiles(remoteFilePath);
			return result;
		}
		// it's a directory,and have sub directories and files
		for (FTPFile ftpFile : list) {
			String name = ftpFile.getName();
			logger.i("delete filename: " + name);
			if (ftpFile.isDirectory()) {
				success = deleteFoldAndsubFiles(remoteFilePath);
				if (!success)
					break;
			} else {
				result = deleteFtpServerFile(remoteFilePath);
				if (result != true)
					break;
			}
		}

		success = true;

		return success;
	}

	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	public FtpUploadStatus createDirecroty(String remote, FTPClient ftpClient)
			throws IOException {

		FtpUploadStatus status = FtpUploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(new String(directory
						.getBytes("GBK"), "iso-8859-1"))) {

			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);

			while (true) {
				String subDirectory = new String(remote.substring(start, end)
						.getBytes("GBK"), "iso-8859-1");
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						logger.v("Create directory fail!");
						return FtpUploadStatus.Create_Directory_Fail;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				if (end <= start) {
					break;
				}
			}
		}

		return status;
	}

	public FtpUploadStatus uploadFile(String remoteFile, File localFile,
			FTPClient ftpClient, long remoteSize) throws IOException {
		FtpUploadStatus status;

		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;

		RandomAccessFile raf = new RandomAccessFile(localFile, "r");

		OutputStream out = ftpClient.appendFileStream(new String(remoteFile
				.getBytes("GBK"), "iso-8859-1"));

		if (out == null) {
			logger.v("Remote OutputStream is null!");
			raf.close();
			return FtpUploadStatus.File_Non_Exits;
		}

		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}

		byte[] bytes = new byte[1024];
		int c;
		while ((c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			if (localreadbytes / step != process) {
				process = localreadbytes / step;
				logger.v("Upload Process:" + process);
				// TODO: Report the process
			}
		}

		out.flush();
		raf.close();
		out.close();

		boolean result = ftpClient.completePendingCommand();
		if (remoteSize > 0) {
			status = result ? FtpUploadStatus.Upload_From_Break_Success
					: FtpUploadStatus.Upload_From_Break_Failed;
		} else {
			status = result ? FtpUploadStatus.Upload_New_File_Success
					: FtpUploadStatus.Upload_New_File_Failed;
		}

		return status;
	}

	public void close() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
