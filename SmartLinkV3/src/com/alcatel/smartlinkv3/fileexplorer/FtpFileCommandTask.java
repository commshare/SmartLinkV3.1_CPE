package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.R.bool;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.alcatel.smartlinkv3.ftp.client.FtpClientModel;
import com.alcatel.smartlinkv3.ftp.client.FtpManager;
import com.alcatel.smartlinkv3.ftp.client.FtpManagerIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.FtpTransferIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.pubLog;
import com.alcatel.smartlinkv3.samba.SmbHttpServer;

//TODO: CallBack
public class FtpFileCommandTask {
	// ftp command
	private static final int CONNECT = 1;
	private static final int SHOWFILES = 3;
	private static final int DOWNLOAD = 4;
	private static final int UPLOAD = 5;

	private static final int DELETE = 8;
	private static final int PAUSE_DOWNLOAD = 9;
	private static final int CLOSE = -2;
	private static final int MOVE = 10;
	private static final int COPY = 11;
	private static final int RENAME = 12;
	private static final int SHARE = 13;
	private static final int CREATE_FOLDER = 14;
	// message type
	private static final int MSG_SHOW_TOAST = 1;
	private static final int MSG_SHARE_FILE = 2;
	private static final int MSG_REFRESH_UI = 10;

	private FtpCommandProc ftpTask = new FtpCommandProc();
	private FtpManager ftp = null;
	private Thread thread = null;
	private boolean running = false;

	private Context mContext = null;
	private pubLog logger = null;
	private FtpClientModel m_ftp = null;

	private FtpCommandListener m_FtpCommandListener;
	// private Handler handler = null;
	private boolean isLogin = false;
	private boolean isInit = false;

	private ArrayList<ShareFileInfo> mShareFiles = new ArrayList<ShareFileInfo>();

	private OnCallResponse mOnCallResponse;

	public void setOnCallResponse(OnCallResponse response) {
		this.mOnCallResponse = response;
	}

	public interface OnCallResponse {
		void callResponse(Object obj);
	}

	public OnCallResponse getOnCallResponse() {
		return this.mOnCallResponse;
	}

	public interface FtpCommandListener {
		// ui thread
		void ftpMsgHandler(Message msg);
	}

	public void setFtpCommandListener(FtpCommandListener listener) {
		this.m_FtpCommandListener = listener;
	}

	public void setFtpTransferListener(
			FtpTransferIRetrieveListener TransferListener) {
		ftp.setTransferFtpListener(TransferListener);
	}

	public boolean init(Context mContext) {
		if (isInit) {
			return true;
		}

		ftp = new FtpManager();

		this.mContext = mContext;
		try {
			ftp.init(mContext);
			ftp.reset(mContext);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger = new pubLog();
		logger.setTag("ftpClient");
		logger.setDebug(true);
		logger.v("ftp init success!");

		// if download or upload,this is provide transfer status
		ftp.setTransferFtpListener(TransferListener);

		// ftp operatetion status
		ftp.setFtpManagerListener(FtpManagerListener);

		m_ftp = new FtpClientModel();

		if (true) {
			m_ftp.host = getServerAddress(mContext);
			m_ftp.port = 21;
		}

		m_ftp.localDir = Environment.getExternalStorageDirectory().getPath()
				+ "/LinkApp";

		logger.v("Local directory: " + m_ftp.localDir);
		logger.v("Server ip: " + m_ftp.host);

		ftp.setConfig(mContext, m_ftp);
		thread = new Thread(ftpTask);

		this.isInit = true;
		return true;
	}

	public void setConfig(FtpClientModel m_ftp) {
		this.m_ftp = m_ftp;
	}

	public FtpClientModel getConfig() {
		return this.m_ftp;
	}

	public void start() {
		running = true;
		thread.start();
		ftp_connect();
	}

	public void stop() {
		try {
			ftp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		running = false;
		thread.stop();
	}

	public void ftp_connect() {
		ftpTask.awakenCMD(CONNECT);
	}

	public void ftp_showfiles(String remotePath) {
		ftpTask.setRemoteRootPath(remotePath);
		ftpTask.awakenCMD(SHOWFILES);
	}

	public void ftp_download(ArrayList<FileInfo> remoteFiles) {
		ftpTask.setRemoteFiles(remoteFiles);
		ftpTask.awakenCMD(DOWNLOAD);
	}

	public void ftp_upload(ArrayList<File> localFiles, String remotePath) {
		ftpTask.setLocalFiles(localFiles);
		ftpTask.setRemotePath(remotePath);
		ftpTask.awakenCMD(UPLOAD);
	}

	public void ftp_delete(ArrayList<FileInfo> remoteFiles) {
		ftpTask.setRemoteFiles(remoteFiles);
		ftpTask.awakenCMD(DELETE);
	}

	public void ftp_move(ArrayList<FileInfo> remoteFiles, String remotePath) {
		ftpTask.setMoveFile(remoteFiles, remotePath);
		ftpTask.awakenCMD(MOVE);
	}

	public void ftp_create_folder(String remotePath) {
		ftpTask.setRemotePath(remotePath);
		ftpTask.awakenCMD(CREATE_FOLDER);
	}

	public void ftp_copy(ArrayList<FileInfo> remoteFiles, String remotePath) {
		ftpTask.setRemoteRootPath(remotePath);
		ftpTask.setRemoteFiles(remoteFiles);
		ftpTask.awakenCMD(COPY);
	}

	public void ftp_rename(String fromFile, String toFile) {
		ftpTask.setRename(fromFile, toFile);
		ftpTask.awakenCMD(RENAME);
	}

	public void ftp_share(ArrayList<FileInfo> remoteFiles) {
		ftpTask.setShareFiles(remoteFiles);
		ftpTask.awakenCMD(SHARE);
	}

	public void ftp_close() {
		ftpTask.awakenCMD(CLOSE);
	}

	class FtpCommandProc implements Runnable {
		public int CMD = -1;
		private String remoteRootPath = "/";
		private String remotePath = "/";
		private ArrayList<FileInfo> remoteFiles = new ArrayList<FileInfo>();
		private ArrayList<File> localFiles = new ArrayList<File>();
		private ArrayList<FileInfo> shareFiles = new ArrayList<FileInfo>();
		// TODO
		private ArrayList<FileInfo> fromFiles = null;
		private String fromFile = null;
		private String toFile = null;

		public void setRemoteRootPath(String remoteRootPath) {
			this.remoteRootPath = remoteRootPath;
		}

		public void setRemotePath(String remotePath) {
			this.remotePath = remotePath;
		}

		public void setRemoteFiles(ArrayList<FileInfo> remoteFiles) {
			this.remoteFiles = remoteFiles;
		}

		public void setShareFiles(ArrayList<FileInfo> shareFiles) {
			this.shareFiles = shareFiles;
		}

		public void setLocalFiles(ArrayList<File> localFiles) {
			this.localFiles = localFiles;
		}

		public void setRename(String fromFile, String toFile) {
			this.fromFile = fromFile;
			this.toFile = toFile;
		}

		public void setMoveFile(ArrayList<FileInfo> fromFiles, String toFile) {
			this.fromFiles = fromFiles;
			this.toFile = toFile;
		}

		public synchronized void awakenCMD(int cmd) {
			this.CMD = cmd;
			notifyAll();
		}

		private synchronized void runCmd() throws IOException {
			while (CMD == -1) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			switch (CMD) {
			case CONNECT:
				logger.v("ftp is connecting!....");
				ftpConnect();
				CMD = -1;
				break;
			case 2:
				// ftpChangeDir();
				CMD = -1;
				break;
			case SHOWFILES:
				showFiles(remoteRootPath);
				CMD = -1;
				break;
			case DOWNLOAD:
				// TODO: need a thread pool
				if (remoteFiles.size() == 0) {
					logger.w("download fail,file size is 0 on task");
					CMD = -1;
					break;
				}

				logger.i("download file size: " + remoteFiles.size());

				for (FileInfo f : remoteFiles) {
					logger.i("download file lists: " + f.fileName);
				}

				logger.i("on downloanning...");
				downloadFiles(remoteFiles);
				CMD = -1;
				break;
			case UPLOAD:
				// TODO: need a thread pool
				if (true) {
					if (localFiles.size() == 0) {
						logger.w("upload fail,file size is 0 on task");
						CMD = -1;
						break;
					}

					uploadFiles(localFiles, remotePath);
				}

				// test
				if (false) {
					try {
						ftp.upload("/mnt/sdcard/LinkApp/test", "/sdcard/test");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}

				CMD = -1;
				break;

			case DELETE:
				if (remoteFiles.size() == 0) {
					logger.w("delete fail,file size is 0 on task");
					CMD = -1;
					break;
				}

				logger.i("delete file size: " + remoteFiles.size());

				for (FileInfo f : remoteFiles) {
					logger.i("delete file lists: " + f.fileName);
				}
				deleteFiles(remoteFiles);
				CMD = -1;
				break;

			case MOVE:
				if ((this.fromFiles != null) && (this.toFile != null)) {
					moveFiles(this.fromFiles, this.toFile);
				}

				CMD = -1;
				break;

			case COPY:
				CMD = -1;
				break;

			case CREATE_FOLDER:
				if (this.remotePath != null) {
					createFolder(this.remotePath);
				}

				CMD = -1;
				break;
			case RENAME:
				if ((this.fromFile != null) && (this.toFile != null)) {
					renameFile(this.fromFile, this.toFile);
				}

				CMD = -1;
				break;
			case SHARE:
				if (this.shareFiles != null) {
					shareFiles(this.shareFiles);
				}

				CMD = -1;
				break;
			case CLOSE:
				try {
					ftp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				CMD = -1;
				running = false;
				break;

			default:
				break;
			}
		}

		@Override
		public void run() {
			logger.v("enter runnable...");
			while (running) {
				try {
					runCmd();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	FtpTransferIRetrieveListener TransferListener = new FtpTransferIRetrieveListener() {
		@Override
		public void onTrack(long now) {
			// changeProgressText((int) now);
			logger.i("transfer progress: " + now);
		}

		@Override
		public void onStart() {
			Log.d("", "transfer onStart.....");
			// getEVE();
		}

		@Override
		public void onError(Object obj, int type) {
			logger.i("transfer error: " + obj);
			// sendMsg(obj + "");
		}

		public void onDone() {
			logger.i("transfer done......");
			// changeProgressText(100);
		}

		@Override
		public void onCancel(Object obj) {
			logger.i("transfer cancel.....");
		}
	};

	FtpManagerIRetrieveListener FtpManagerListener = new FtpManagerIRetrieveListener() {
		@Override
		public void onTrack(long now) {
		}

		@Override
		public void onStart() {
			Log.d("", "onStart.............");
			// getEVE();
		}

		@Override
		public void onStatus(Object obj, int type) {
			/*
			 * Toast.makeText(MainActivity.this, obj + "",
			 * Toast.LENGTH_SHORT).show();
			 */
		}

		public void onDone() {

		}

		@Override
		public void onCancel(Object obj) {
			Log.d("", "onCancel...............");
		}
	};

	private void sendMsg(int msgType, Object obj) {
		Message msg = new Message();
		msg.what = msgType;
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	private void ftpConnect() {
		if (isLogin) {
			return;
		}

		if (ftp.connectLogin()) {
			sendMsg(MSG_SHOW_TOAST, "ftp login success!");
			logger.v("ftp login success!");
			isLogin = true;
			handler.sendEmptyMessage(2);
		} else {
			sendMsg(MSG_SHOW_TOAST, "ftp login fail!");
		}
	}

	private void deleteFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the remote files!");
			logger.w("delete fail,can't not find the remote file!");
			return;
		}

		for (FileInfo f : remote) {
			logger.i("delete file lists: " + f.fileName);
			String remotePath = f.filePath + File.separator + f.fileName;
			result = ftp.deleteFiles(remotePath);

			if (result) {
				sendMsg(MSG_SHOW_TOAST, "delete file [" + remotePath
						+ "] success!");
			} else {
				sendMsg(MSG_SHOW_TOAST, "delete file [" + remotePath
						+ "] fail!");
			}
		}

	}

	private void renameFile(String fromFile, String toFile) throws IOException {
		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}
		if (null == fromFile) {
			logger.w("source file [" + fromFile + "] can't not be null!");
			return;
		}

		if (null == toFile) {
			logger.w("obj file [" + toFile + "] can't not be null!");
			return;
		}

		boolean result = ftp.rename(fromFile, toFile);

		if (result) {
			sendMsg(MSG_SHOW_TOAST, "rename file [" + fromFile + "] success!");
		} else {
			sendMsg(MSG_SHOW_TOAST, "rename file [" + fromFile + "] fail!");
		}

	}

	private void moveFiles(ArrayList<FileInfo> fromFiles, String remotePath)
			throws IOException {
		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}
		if (null == fromFiles) {
			logger.w("source file [" + fromFiles + "] can't not be null!");
			return;
		}

		if (null == remotePath) {
			logger.w("obj file [" + remotePath + "] can't not be null!");
			return;
		}

		for (FileInfo f : fromFiles) {
			String fromFile = f.filePath + File.separator + f.fileName;
			String toFile = remotePath + File.separator + f.fileName;

			logger.i("move fromFile = " + fromFile + ", move toFile = "
					+ toFile);

			boolean result = ftp.move(fromFile, toFile);

			if (result) {
				sendMsg(MSG_SHOW_TOAST, "move file [" + fromFile + "] success!");
			} else {
				sendMsg(MSG_SHOW_TOAST, "move file [" + fromFile + "] fail!");
			}
		}

	}

	private void uploadFiles(ArrayList<File> local, String remotePath)
			throws IOException {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((local == null) || (local.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the local files!");
			logger.w("download fail,can't not find the local files!");
			return;
		}

		for (File f : local) {
			String localPath = f.getPath();
			result = ftp.upload(localPath,
					remotePath + File.separator + f.getName());
			if (!result) {
				sendMsg(MSG_SHOW_TOAST,
						"upload fail: localPath = " + localPath
								+ ",remotePath = " + remotePath
								+ File.separator + f.getName());
				logger.w("upload fail: localPath = " + localPath
						+ ",remotePath = " + remotePath + File.separator
						+ f.getName());
				return;
			}
		}
		sendMsg(MSG_SHOW_TOAST, "upload success!");
	}

	private boolean downloadFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return false;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the remote files!");
			logger.w("download fail,can't not find the remote file!");
			return false;
		}

		for (FileInfo f : remote) {
			String remotePath = f.filePath + File.separator + f.fileName;
			String localPath = m_ftp.localDir + File.separator + f.fileName;

			logger.i("download filename: " + f.fileName);
			logger.i("download remotePath: " + remotePath);
			logger.i("download localPath: " + localPath);

			result = ftp.download(localPath, remotePath);

			if (!result) {
				sendMsg(MSG_SHOW_TOAST, "download failure: " + remotePath);
				return false;
			}

		}
		sendMsg(MSG_SHOW_TOAST, "download success!");
		result = true;
		return result;
	}

	private void showFiles(String path) {
		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		try {
			FTPFile[] ftpFile = ftp.showListFile(path);
			logger.v("ftp show files!");
			sendMsg(MSG_REFRESH_UI, ftpFile);

		} catch (Exception e) {
			e.printStackTrace();
			sendMsg(MSG_SHOW_TOAST, "Ftp File List Error:" + e);
		}

	}

	private void shareFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			return;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the remote files!");
			logger.w("download fail,can't not find the remote file!");
			return;
		}

		ArrayList<ShareFileInfo> shareFiles = mShareFiles;
		mShareFiles.clear();

		for (FileInfo f : remote) {
			String remotePath = f.filePath + File.separator + f.fileName;
			String localPath = m_ftp.localDir + File.separator + f.fileName;

			result = ftp.download(localPath, remotePath);

			if (!result) {
				sendMsg(MSG_SHOW_TOAST, "download failure: " + remotePath);
				return;
			}

			ShareFileInfo localFile = new ShareFileInfo();
			localFile = Util.GetShareFileInfo(localPath);
			shareFiles.add(localFile);
		}

		mOnCallResponse.callResponse(shareFiles);
		// sendMsg(MSG_SHARE_FILE, shareFiles);

		sendMsg(MSG_SHOW_TOAST, "Ftp file share success!");
	}

	private void createFolder(String remoteFolder) throws IOException {
		boolean result = false;

		if (!isLogin) {
			return;
		}

		result = ftp.createFolder(remoteFolder);

		if (result) {
			sendMsg(MSG_SHOW_TOAST, "create folder success!");
		} else {
			sendMsg(MSG_SHOW_TOAST, "create folder fail: " + remoteFolder);
			logger.i("create folder fail: " + remoteFolder);
		}

	}

	private String getServerAddress(Context ctx) {
		WifiManager wifi_service = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();

		return Formatter.formatIpAddress(dhcpInfo.gateway);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			m_FtpCommandListener.ftpMsgHandler(msg);
		}
	};

}
