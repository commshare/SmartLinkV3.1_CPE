package com.alcatel.wifilink.ftp.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpClientProxy {

	private FTPClient ftpClient = new FTPClient();
	private pubLog logger = new pubLog();

	private FtpClientModel config;
	public FtpTransferIRetrieveListener listener;
	private volatile boolean stopDownload = false;
	private volatile int reply = 0;

	public FtpClientProxy() {
		this.stopDownload = false;
	}

	protected FtpClientProxy(FtpClientModel cfg) {
		this.config = cfg;
	}

	public int init() {

		return 0;
	}

	public int getReply() {
		return ftpClient.getReplyCode();
	}

	public FtpClientModel getConfig() {
		return this.config;
	}

	public synchronized void setConfig(FtpClientModel config) {
		this.config = config;
	}

	public pubLog getlogInstance() {
		return logger;
	}

	public synchronized void setFtpListener(
			FtpTransferIRetrieveListener listener) {
		this.listener = listener;
	}

	public synchronized void setStopDownload() {
		this.stopDownload = true;
	}

	public synchronized void setStartDownload() {
		this.stopDownload = false;
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

	public boolean isFtpConnected() throws IOException {
		return ftpClient.isConnected();
	}

	public void ftpSetting() throws IOException {
		ftpClient.setFileType(FTPClient.FILE_STRUCTURE);
		ftpClient.enterLocalPassiveMode();
		// ftpClient.setControlEncoding("GBK");
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.setListHiddenFiles(true);
		// ftpClient.setConnectTimeout(5000);
		// ftpClient.enterLocalActiveMode();
		// ftpClient.enterRemotePassiveMode();
		// ftpClient.enterRemoteActiveMode(InetAddress.getByName(config.address),config.port);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	public boolean login() {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			boolean result = ftpClient.login(config.username, config.password);
			if (!result) {
				return false;
			}

			ftpSetting();

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public FTPFile getFTPFile(String remotePath) {
		try {
			// mlistFile() need server support,it returns null sometimes
			FTPFile f = ftpClient.mlistFile(remotePath);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public synchronized void setRestartOffset(long len) {
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
		boolean result = false;
		File file = new File(filePath);

		if (file.exists()) {
			result = true;
		} else {
			result = false;
			throw new Exception("File is not exist!");
		}

		return result;
	}

	public Boolean changeDirectory(String remoteFoldPath) throws Exception {
		return ftpClient.changeWorkingDirectory(convertToUTF8(remoteFoldPath));
	}

	public boolean createLocalFolder(String strFolder) {
		File file = new File(strFolder);

		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			}
		}
		return false;
	}

	public FTPFile getFileByName(String remoteDir, String name)
			throws Exception {
		List<FTPFile> files = this.getFileList(remoteDir);
		if (files != null) {
			for (FTPFile f : files) {
				if (name.equalsIgnoreCase(f.getName())) {
					return f;
				}
			}
		}
		return null;
	}

	public boolean renameFile(String fromFile, String toFile)
			throws IOException {
		if (!ftpClient.rename(convertToUTF8(fromFile), convertToUTF8(toFile))) {
			return false;
		}
		return true;
	}

	public boolean moveFile(String fromFile, String toFile) throws IOException {
		if (!ftpClient.rename(convertToUTF8(fromFile), convertToUTF8(toFile))) {
			return false;
		}
		return true;
	}

	public String getParentPathFromURL(String remoteURL) {
		String parentPath = "";
		// get file name from the path
		String fName = remoteURL.trim();
		String fileName = fName.substring(fName.lastIndexOf("/") + 1);
		parentPath = remoteURL.substring(0,
				remoteURL.length() - fileName.length() - 1);

		return parentPath;
	}

	public String getFileNameFromURL(String remoteURL) {
		String fName = remoteURL.trim();
		String fileName = fName.substring(fName.lastIndexOf("/") + 1);

		return fileName;
	}

	public FTPFile getFtpFileByPath(String remotePath) throws Exception {

		String parentPath = this.getParentPathFromURL(remotePath);
		String filename = getFileNameFromURL(remotePath);

		List<FTPFile> parentFtp = this.getFileList(parentPath);
		logger.i("parentPath: " + parentPath);
		if (parentFtp != null) {
			String name = "";
			for (FTPFile f : parentFtp) {
				name = f.getName();
				if (name.equals(".") || name.equals("..")) {
					// skip parent directory and the directory itself
					continue;
				}
				if (filename.equalsIgnoreCase(name)) {
					return f;
				}
			}
		}

		logger.w("can not find parentFtp by the remotePath: " + remotePath);
		return null;
	}

	// TODO
	public boolean isDirectory(String remote) throws Exception {
		FTPFile remoteFile = ftpClient.mlistFile(convertToUTF8(remote));

		if (remoteFile == null) {
			logger.i("remote file is not exist or have not permerssion!");
			FTPFile ftpFile = this.getFtpFileByPath(remote);

			if (ftpFile == null) {
				logger.w("FTPFile is null!");
			}

			if (ftpFile.isDirectory()) {
				return true;
			}

		} else {
			if (remoteFile.isDirectory()) {
				return true;
			}
		}

		return false;
	}

	public boolean downloadAndsubFiles(String local, String remote)
			throws Exception {
		FtpDownloadStatus result = FtpDownloadStatus.Download_From_Break_Failed;
		boolean success = false;

		boolean isDirectory = this.isDirectory(remote);

		if (isDirectory) {
			logger.i("file name [" + remote + "]" + " is a directroy");
			createLocalFolder(local);
		} else {
			logger.i("file name [" + remote + "]" + " is a file");
			result = download(local, remote);
			if ((result == FtpDownloadStatus.Download_New_Success)
					|| (result == FtpDownloadStatus.Download_From_Break_Success)) {
				success = true;
			} else {
				success = false;
			}
			return success;
		}

		logger.i("remoteFile = " + remote);
		FTPFile[] list = ftpClient.listFiles(convertToUTF8(remote));

		if (list == null || list.length == 0) {
			logger.w("getFileList() return null!");
			return false;
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
					logger.w("download directory [" + remote + "/" + filename
							+ "]" + "fail!");
					logger.i("result: " + result);
					// break;
					return false;
				}
			} else {
				result = download(local + "/" + filename, remote + "/"
						+ filename);

				if ((result != FtpDownloadStatus.Download_New_Success)
						&& (result != FtpDownloadStatus.Download_From_Break_Success)) {
					logger.w("download file [" + remote + "/" + filename + "]"
							+ "fail!");
					logger.i("result: " + result);
					// break;
					return false;
				}
			}
		}

		success = true;
		return success;
	}

	public FtpDownloadStatus download(String local, String remote)
			throws Exception {
		FtpDownloadStatus result;
		listener.onStart(local);
		setStartDownload();

		List<FTPFile> files = this.getFileList(remote);

		if (files.size() != 1) {
			logger.v("remote file" + remote + " is not exist!");
			return FtpDownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files.get(0).getSize();
		File f = new File(local);

		File parentDir = f.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdir();
		}

		if (f.exists()) {
			long localSize = f.length();
			if (localSize >= lRemoteSize) {
				// logger.i("fail,the size of local file is bigger then remote file!");
				listener.onError("local has the file yet!", 0);
				listener.onDone();
				// TODO
				return FtpDownloadStatus.Download_From_Break_Success;
			}

			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(new String(remote
					.getBytes("UTF-8"), "iso-8859-1"));

			byte[] bytes = new byte[config.bufferSize];
			long step = lRemoteSize / 100;
			long localNewSize = localSize;
			int c;

			while ((c = in.read(bytes)) != -1) {
				if (stopDownload) {
					setStartDownload();
					listener.onCancel("");
					break;
				}

				out.write(bytes, 0, c);
				localNewSize += c;

				if (step != 0) {
					long lastProcess = localSize / step;
					long nowProcess = localNewSize / step;

					if (nowProcess >= lastProcess) {
						if (nowProcess % 10 == 0)
							logger.i("Download Process:" + nowProcess);
						listener.onTrack(remote, nowProcess);
						// TODO: update the process
					}
				}

			}

			in.close();
			out.close();

			boolean isDo = ftpClient.completePendingCommand();

			if (isDo) {
				result = FtpDownloadStatus.Download_From_Break_Success;
				listener.onDone();
			} else {
				result = FtpDownloadStatus.Download_From_Break_Failed;
				listener.onError("download fail!", -1);
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(new String(remote
					.getBytes("UTF-8"), "iso-8859-1"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				if (stopDownload) {
					setStartDownload();
					listener.onCancel("");
					break;
				}

				out.write(bytes, 0, c);
				localSize += c;

				if (step != 0) {
					int nowProcess = (int) (localSize / step);
					if (nowProcess > 0) {
						process = nowProcess;
						if (process % 10 == 0)
							logger.i("Download Process:" + process);
						listener.onTrack(remote, process);
						// TODO: update the process
					}
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
				listener.onError("download fail!", -1);
			}
		}

		return result;
	}

	public FtpUploadStatus upload(String local, String remote) throws Exception {
		FtpUploadStatus result;

		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);

			if (remoteFileName.equalsIgnoreCase("")) {
				logger.v("Errer : Remote file name is empty!");
				return FtpUploadStatus.Parameter_Error;
			}

			if (createFtpDirecroty(remote) == FtpUploadStatus.Create_Directory_Fail) {
				return FtpUploadStatus.Create_Directory_Fail;
			}
		}

		List<FTPFile> files = this.getFileList(remoteFileName);

		if (files.size() == 1) {
			long remoteSize = files.get(0).getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return FtpUploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return FtpUploadStatus.Remote_Bigger_Local;
			}

			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			if (result == FtpUploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(convertToUTF8(remoteFileName))) {
					return FtpUploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {

			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		return result;
	}

	public boolean uploadAndsubFiles(String local, String remote)
			throws Exception {
		File localFile = new File(local);
		boolean result = false;

		if (localFile.isFile()) {
			logger.i("localfile is a file: " + local);
			// upload a file
			FtpUploadStatus status = upload(local, remote);

			if ((status == FtpUploadStatus.Upload_New_File_Success)
					|| (status == FtpUploadStatus.Upload_From_Break_Success)) {
				result = true;
			} else {
				result = false;
			}

			return result;
		} else {
			logger.i("localfile is a directory: " + local);
			logger.i("create remote directory: " + remote);
			// create remtoe directory,it's a parent directory
			ftpClient.makeDirectory(convertToUTF8(remote));
		}

		File[] localFiles = localFile.listFiles();

		for (File currentFile : localFiles) {
			String localFileName = currentFile.getName();
			if (currentFile.isDirectory()) {
				createFtpDirecroty(remote + "/" + localFileName);
				result = uploadAndsubFiles(local + "/" + localFileName, remote
						+ "/" + localFileName);
			} else {
				FtpUploadStatus status = upload(local + "/" + localFileName,
						remote + "/" + localFileName);

				if ((status == FtpUploadStatus.Upload_New_File_Success)
						|| (status == FtpUploadStatus.Upload_From_Break_Success)) {
					result = true;
				} else {
					result = false;
				}
			}
		}

		return true;
	}

	public List<FTPFile> getFileList(String remotePath) throws Exception {
		List<FTPFile> ftpfiles = Arrays.asList(ftpClient
				.listFiles(convertToUTF8(remotePath)));

		// under linux,filter out parent directory and the directory itself
		List<FTPFile> files = new ArrayList<FTPFile>();
		String name = "";
		for (FTPFile f : ftpfiles) {
			name = f.getName();
			if (name.equals(".") || name.equals("..")) {
				// skip parent directory and the directory itself
				continue;
			}
			files.add(f);
		}

		return files;
	}

	public Boolean deleteFtpServerFile(String remoteFilePath) throws Exception {
		return ftpClient.deleteFile(convertToUTF8(remoteFilePath));
	}

	public boolean createFold(String remoteFoldPath) throws Exception {

		boolean flag = ftpClient.makeDirectory(convertToUTF8(remoteFoldPath));
		if (!flag) {
			throw new Exception("create fold fail!");
		}
		return false;
	}

	public boolean deleteFold(String remoteFoldPath) throws Exception {
		return ftpClient.removeDirectory(convertToUTF8(remoteFoldPath));
	}

	public String convertToUTF8(String str) throws UnsupportedEncodingException {
		return new String(str.getBytes("UTF-8"), "iso-8859-1");
	}

	public boolean deleteFoldAndsubFiles(String remoteFoldPath)
			throws Exception {
		boolean success = false;

		boolean isDirectory = this.isDirectory(remoteFoldPath);

		// it's a file
		if (!isDirectory) {
			success = deleteFtpServerFile(remoteFoldPath);
			return success;
		}

		List<FTPFile> list = this.getFileList(remoteFoldPath);

		for (FTPFile ftpFile : list) {
			String name = ftpFile.getName();
			String remoteFile = remoteFoldPath + "/" + name;

			if (name.equals(".") || name.equals("..")) {
				// skip parent directory and the directory itself
				success = true;
				continue;
			}

			if (ftpFile.isDirectory()) {
				success = deleteFoldAndsubFiles(remoteFile);
				if (!success)
					return success;
			} else {
				success = deleteFtpServerFile(remoteFile);
				if (!success)
					return success;
			}
		}

		success = deleteFold(remoteFoldPath);
		return success;
	}

	public FtpUploadStatus createFtpDirecroty(String remote) throws IOException {

		FtpUploadStatus status = FtpUploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);

		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(convertToUTF8(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			logger.i("end: " + end);
			while (true) {
				String subDirectory = new String(remote.substring(start, end)
						.getBytes("UTF-8"), "iso-8859-1");
				logger.i("subDirectory: " + subDirectory);
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
				.getBytes("UTF-8"), "iso-8859-1"));

		if (out == null) {
			logger.i("Remote OutputStream is null!");
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
			/*
			 * if (localreadbytes / step != process) { process = localreadbytes
			 * / step; logger.v("Upload Process:" + process); // TODO: Report
			 * the process }
			 */
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

	public Boolean mergeFile(String localPath, String desFile, String mergeStr) {
		FileInputStream inputStream = null;
		FileOutputStream fileOutStream = null;
		try {
			inputStream = new FileInputStream(localPath + "/" + desFile);
			byte allBytes[] = new byte[inputStream.available()];
			inputStream.read(allBytes);
			fileOutStream = new FileOutputStream(localPath + "/" + desFile);
			fileOutStream.write(mergeStr.getBytes());
			fileOutStream.write(allBytes);
		}

		catch (IOException e) {
			return false;
		} finally {
			try {
				fileOutStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	public boolean mkdir(String remotePath) throws IOException {
		return ftpClient.makeDirectory(convertToUTF8(remotePath));
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
