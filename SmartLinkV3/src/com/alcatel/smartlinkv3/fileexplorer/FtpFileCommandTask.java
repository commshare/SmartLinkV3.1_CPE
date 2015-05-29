package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

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

	public boolean init(Context mContext) {
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

		ftp.setTransferFtpListener(TransferListener);
		ftp.setFtpManagerListener(FtpManagerListener);

		m_ftp = new FtpClientModel();

		if (true) {
			m_ftp.host = getServerAddress(mContext);
			m_ftp.port = 21;
		}

		m_ftp.localDir = Environment.getExternalStorageDirectory().getPath()
				+ "/LinkApp";

		logger.v("Local directory: " + m_ftp.localDir);
		logger.v("Server ip is: " + m_ftp.host);

		ftp.setConfig(mContext, m_ftp);
		thread = new Thread(ftpTask);

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
		ftpTask.setRemoteRootPath(remotePath);
		ftpTask.awakenCMD(UPLOAD);
	}

	public void ftp_delete(ArrayList<FileInfo> remoteFiles) {
		ftpTask.setRemoteFiles(remoteFiles);
		ftpTask.awakenCMD(DELETE);
	}

	public void ftp_move(ArrayList<FileInfo> remoteFiles, String remotePath) {
		ftpTask.setRemoteRootPath(remotePath);
		ftpTask.setRemoteFiles(remoteFiles);
		ftpTask.awakenCMD(MOVE);
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
		ftpTask.setRemoteFiles(remoteFiles);
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

		// TODO
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

		public void setLocalFiles(ArrayList<File> localFiles) {
			this.localFiles = localFiles;
		}

		public void setRename(String fromFile, String toFile) {
			this.fromFile = fromFile;
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
				CMD = -1;
				break;
			case COPY:
				CMD = -1;
				break;
			case RENAME:
				if ((this.fromFile != null) && (this.toFile != null)) {
					renameFile(this.fromFile, this.toFile);
				}

				CMD = -1;
				break;
			case SHARE:
				if (this.remoteFiles != null) {
					shareFiles(this.remoteFiles);
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
			/*
			 * long per = now / every; changeProgressText((int) per);
			 */
			// changeProgressText((int) now);
		}

		@Override
		public void onStart() {
			Log.d("", "onStart.............");
			// getEVE();
		}

		@Override
		public void onError(Object obj, int type) {
			// sendMsg(obj + "");
		}

		public void onDone() {
			// changeProgressText(100);
		}

		@Override
		public void onCancel(Object obj) {
			Log.d("", "onCancel...............");
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
			// TODO :test
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
			logger.w("obj file [" + fromFile + "] can't not be null!");
			return;
		}

		boolean result = ftp.rename(fromFile, toFile);

		if (result) {
			sendMsg(MSG_SHOW_TOAST, "rename file [" + fromFile + "] success!");
		} else {
			sendMsg(MSG_SHOW_TOAST, "rename file [" + fromFile + "] fail!");
		}

	}

	private void uploadFiles(ArrayList<File> local, String remote)
			throws IOException {
		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((local == null) || (local.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the local files!");
			logger.w("download fail,can't not find the local files!");
			return;
		}
		// TODO
		for (File f : local) {
			String localPath = f.getPath();
			ftp.upload(localPath, remote);

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
			
			if(!result){
				sendMsg(MSG_SHOW_TOAST, "download failure: " + remotePath);
				return false;
			}

		}
		sendMsg(MSG_SHOW_TOAST, "download success!");
		result = true;
		return result;
	}

	private void showFiles(String path) {
		if (isLogin) {
			try {
				FTPFile[] ftpFile = ftp.showListFile(path);
				logger.v("ftp show files!");
				sendMsg(MSG_REFRESH_UI, ftpFile);

			} catch (Exception e) {
				e.printStackTrace();
				sendMsg(MSG_SHOW_TOAST, "Ftp File List Error:" + e);
			}
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
		//sendMsg(MSG_SHARE_FILE, shareFiles);
		
		sendMsg(MSG_SHOW_TOAST, "Ftp file share");
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
