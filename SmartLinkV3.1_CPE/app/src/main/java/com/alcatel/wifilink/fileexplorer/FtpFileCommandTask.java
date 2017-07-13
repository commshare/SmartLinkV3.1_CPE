package com.alcatel.wifilink.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

import com.alcatel.wifilink.ftp.client.FtpClientModel;
import com.alcatel.wifilink.ftp.client.FtpManager;
import com.alcatel.wifilink.ftp.client.FtpManagerIRetrieveListener;
import com.alcatel.wifilink.ftp.client.FtpTransferIRetrieveListener;
import com.alcatel.wifilink.ftp.client.ListQueue;
import com.alcatel.wifilink.ftp.client.ThreadPoolTask;
import com.alcatel.wifilink.ftp.client.ThreadPoolTask.TaskPoolOnCallResponse;
import com.alcatel.wifilink.ftp.client.ThreadPoolTaskManager;
import com.alcatel.wifilink.ftp.client.pubLog;

//TODO: CallBack
public class FtpFileCommandTask {
	private static final int SUCCESS = 0;
	private static final int FAIL = -1;

	private ListQueue mCmdQueue;
	private FtpCommandHandler ftpCommandHandler;

	private FtpManager ftp;
	private Thread threadHandler;
	private volatile boolean isRunning;

	private Context mContext;
	private pubLog logger;
	private FtpClientModel m_ftp;

	private FtpCommandListener m_FtpCommandListener;
	private volatile boolean isLogin = false;
	private volatile boolean isInit = false;

	private ArrayList<ShareFileInfo> mShareFiles;

	private OnCallResponse mOnCallResponse;

	private ThreadPoolTaskManager downloadThreadPool;

	public FtpFileCommandTask() {
		mCmdQueue = new ListQueue();
		ftpCommandHandler = new FtpCommandHandler();
		mShareFiles = new ArrayList<ShareFileInfo>();
	}

	// ftp cmd
	public class FTP_CMD {
		private static final int LOGIN = 1;
		private static final int SHOWFILES = 3;
		private static final int DOWNLOAD = 4;
		private static final int UPLOAD = 5;
		private static final int LIST_FILES = 6;

		private static final int DELETE = 8;
		private static final int PAUSE_DOWNLOAD = 9;
		private static final int MOVE = 10;
		private static final int COPY = 11;
		private static final int RENAME = 12;
		private static final int SHARE = 13;
		private static final int CREATE_FOLDER = 14;
		private static final int CLOSE = -2;
	}

	// message type
	public class MSG_TYPE {
		public static final int MSG_SHOW_TOAST = 1;
		public static final int MSG_SHARE_FILE = 2;
		public static final int MSG_REFRESH_UI_CMD = 9;
		public static final int MSG_REFRESH_UI = 10;

		public static final int MSG_START_DOWNLOAD = 11;
		public static final int MSG_ON_DOWNLOAD = 12;
		public static final int MSG_END_DOWNLOAD = 13;
		public static final int MSG_PAUSE_DOWNLOAD = 14;
		public static final int MSG_ERROR_DOWNLOAD = 15;
		public static final int MSG_CREATE_FOLDER = 16;
	}

	private enum FTP_STATE {
		Initialized, UnInitiailized, Connecting, Connected, DisConnected, Logining, Logined, UnLogined, Operating, Exit,
	}

	private volatile FTP_STATE mState = FTP_STATE.UnInitiailized;

	class FtpCommandFormat {
		int cmd;
		Object arg1;
		Object arg2;
	}

	public class TransferTracker {
		String filePath;
		long process;
		boolean isSuccess;
	}

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

	public void setFtpTransferListener(FtpTransferIRetrieveListener listener) {
		ftp.setTransferFtpListener(listener);
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
		logger.v("ftp init sendAgainSuccess!");

		// if download or upload,this is provide transfer status
		ftp.setTransferFtpListener(TransferListener);

		// ftp operatetion status
		ftp.setFtpManagerListener(FtpManagerListener);

		m_ftp = new FtpClientModel();

		if (true) {
			m_ftp.host = getServerAddress(mContext);
			m_ftp.port = 21;
		} else {
			m_ftp.host = "192.168.1.103";
			m_ftp.port = 2221;
			m_ftp.username = "admin";
			m_ftp.password = "admin";
		}

		m_ftp.localDir = Environment.getExternalStorageDirectory().getPath()
				+ "/LinkApp";

		logger.i("Local directory: " + m_ftp.localDir);
		logger.i("Server ip: " + m_ftp.host);

		ftp.setConfig(mContext, m_ftp);
		threadHandler = new Thread(ftpCommandHandler);

		// init thread pool
		downloadThreadPool = new ThreadPoolTaskManager();
		downloadThreadPool.start();

		/*
		 * ThreadPoolTaskManager.getInstance(); ThreadPool
		 * downloadTaskManagerThread = new ThreadPool(); Thread threadPool = new
		 * Thread(downloadTaskManagerThread); threadPool.start();
		 */

		this.isInit = true;
		mState = FTP_STATE.Initialized;
		return true;
	}

	public void start() {
		isRunning = true;

		threadHandler.start();

		int time = 0;
		while (!threadHandler.isAlive()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (time >= 10) {
				threadHandler.start();
			}
			time++;
		}

		int n = 0;
		while (!isLogin) {
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (n >= 500) {
				logger.i("ftp can not login!");
				break;
			}
			n++;
		}

	}

	public void setConfig(FtpClientModel m_ftp) {
		this.m_ftp = m_ftp;
	}

	public FtpClientModel getConfig() {
		return this.m_ftp;
	}

	public void stop() {
		ftp_close();
	}

	public void ftp_connect() {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.LOGIN;
		cmdTask.arg1 = null;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_showfiles(String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.SHOWFILES;
		cmdTask.arg1 = remotePath;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_download(ArrayList<FileInfo> remoteFiles) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.DOWNLOAD;
		cmdTask.arg1 = remoteFiles;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_pause_download() {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.PAUSE_DOWNLOAD;
		cmdTask.arg1 = null;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_upload(ArrayList<File> localFiles, String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.UPLOAD;
		cmdTask.arg1 = localFiles;
		cmdTask.arg2 = remotePath;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_delete(ArrayList<FileInfo> remoteFiles) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.DELETE;
		cmdTask.arg1 = remoteFiles;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_move(ArrayList<FileInfo> remoteFiles, String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.MOVE;
		cmdTask.arg1 = remoteFiles;
		cmdTask.arg2 = remotePath;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_create_folder(String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.CREATE_FOLDER;
		cmdTask.arg1 = remotePath;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_copy(ArrayList<FileInfo> remoteFiles, String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.COPY;
		cmdTask.arg1 = remoteFiles;
		cmdTask.arg2 = remotePath;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_rename(String fromFile, String toFile) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.RENAME;
		cmdTask.arg1 = fromFile;
		cmdTask.arg2 = toFile;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_share(ArrayList<FileInfo> remoteFiles) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.SHARE;
		cmdTask.arg1 = remoteFiles;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_list_files(String remotePath) {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.LIST_FILES;
		cmdTask.arg1 = remotePath;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public void ftp_close() {
		FtpCommandFormat cmdTask = new FtpCommandFormat();
		cmdTask.cmd = FTP_CMD.CLOSE;
		cmdTask.arg1 = null;
		cmdTask.arg2 = null;
		addCommandTaskToLists(cmdTask);
	}

	public synchronized boolean addCommandTaskToLists(Object obj) {
		synchronized (mCmdQueue) {
			if (!isNetworkConnected(mContext)) {
				mState = FTP_STATE.Exit;
				logger.i("can not connect the network!");
				return false;
			}

			try {
				boolean isConn = false;
				isConn = ftp.isFtpConnected();
				if (!isConn) {
					mState = FTP_STATE.DisConnected;
					logger.i("ftp is not connected!");
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (obj != null) {
				mCmdQueue.addQueue((Object) obj);
				return true;
			}
		}
		return false;
	}

	public boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	class FtpCommandHandler implements Runnable {
		public int CMD = -1;
		boolean iRet = false;
		private int connTimes = 0;
		
		@Override
		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				switch (mState) {
				case Initialized:
					mState = FTP_STATE.Connecting;
					break;
				case UnInitiailized:
					break;
				case Connecting:
					/*if (connTimes >= 10) {
						mState = FTP_STATE.Exit;
						break;
					}*/
					
					if (isNetworkConnected(mContext)) {
						iRet = ftpConnect();

						if (iRet) {
							mState = FTP_STATE.Connected;
						} else {
							mState = FTP_STATE.Exit;
							//connTimes++;
						}
					} else {
						mState = FTP_STATE.Exit;
					}

					break;
				case Connected:
					mState = FTP_STATE.Logining;
					break;
				case DisConnected:
					if (isNetworkConnected(mContext)) {
						mState = FTP_STATE.Connecting;
					} else {
						mState = FTP_STATE.Exit;
					}

					break;
				case Logining:
					if (!isNetworkConnected(mContext)) {
						mState = FTP_STATE.Exit;
					}

					try {
						iRet = ftp.isFtpConnected();
						
						if (iRet) {
							if (ftpLogin()) {
								mState = FTP_STATE.Logined;
								isLogin = true;
							} else {
								mState = FTP_STATE.UnLogined;
								isLogin = false;
							}
						} else {
							mState = FTP_STATE.DisConnected;
						}
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					break;
				case Logined:
					if (isInit && isLogin) {
						mState = FTP_STATE.Operating;
					}

					break;
				case UnLogined:
					try {
						iRet = ftp.isFtpConnected();
						if (iRet) {
							mState = FTP_STATE.Connecting;
						} else {
							ftp_close();
							isLogin = false;
							isRunning = false;
							logger.i("can not connect the network!");
							sendMsg(MSG_TYPE.MSG_SHOW_TOAST,
									"can not connect the network!");
						}
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					break;
				case Operating:
					while (mState == FTP_STATE.Operating) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (mCmdQueue == null) {
							continue;
						}

						if (mCmdQueue.QueueEmpty()) {
							continue;
						}

						int times = 0;
						while (!isLogin) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (times >= 10000) {
								mState = FTP_STATE.DisConnected;
								logger.i("relogin again!");
								break;
							}
							times++;
						}

						if (isNetworkConnected(mContext)) {
							try {
								boolean isConn = ftp.isFtpConnected();
								if (isConn) {
									try {
										runCmd();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										mState = FTP_STATE.Exit;
										e.printStackTrace();
									}
								} else {
									mState = FTP_STATE.Exit;
								}
							} catch (IOException e1) {
								mState = FTP_STATE.Exit;
								e1.printStackTrace();
							}
						} else {
							mState = FTP_STATE.Exit;
						}
					}

					break;
				case Exit:
					if (!isNetworkConnected(mContext)) {
						logger.i("can not connect the network!");
						sendMsg(MSG_TYPE.MSG_SHOW_TOAST,
								"can not connect the network!");
					}
					ftp_close();
					isRunning = false;
					isLogin = false;
					mState = FTP_STATE.UnInitiailized;
					
					break;
				default:
					mState = FTP_STATE.UnInitiailized;
					break;
				}

			}
		}

		private synchronized void runCmd() throws Exception {
			FtpCommandFormat ftpCommand = (FtpCommandFormat) mCmdQueue
					.getQueue();
			CMD = ftpCommand.cmd;

			if (!isNetworkConnected(mContext)) {
				mState = FTP_STATE.DisConnected;
				CMD = -1;
				return;
			}

			if (!ftp.isFtpConnected()) {
				mState = FTP_STATE.DisConnected;
				CMD = -1;
				return;
			}

			if (!isLogin) {
				mState = FTP_STATE.UnLogined;
				CMD = -1;
				return;
			}

			switch (CMD) {
			case FTP_CMD.LOGIN:
				// logger.v("ftp is connecting!....");
				ftpConnectLogin();
				CMD = -1;
				break;
			case FTP_CMD.SHOWFILES:
				String showFiles = (String) ftpCommand.arg1;
				if (showFiles != null) {
					showFiles(showFiles);
				}

				CMD = -1;
				break;
			case FTP_CMD.LIST_FILES:
				String listFiles = (String) ftpCommand.arg1;
				if (listFiles != null) {
					listFiles((String) ftpCommand.arg1);
				}

				CMD = -1;
				break;
			case FTP_CMD.DOWNLOAD:
				final ArrayList<FileInfo> downloadFiles = (ArrayList<FileInfo>) ftpCommand.arg1;

				if (downloadFiles.size() == 0) {
					logger.w("download fail,file size is 0 on task");
					CMD = -1;
					break;
				}

				TaskPoolOnCallResponse onCallResponse = new TaskPoolOnCallResponse() {
					@Override
					public void taskCallResponse(Object obj) {
						// TODO Auto-generated method stub
						downloadFiles(downloadFiles);
					}
				};

				boolean result = downloadThreadPool.addTask(new ThreadPoolTask(
						downloadFiles.get(0).fileName, onCallResponse));

				if (result) {
					logger.i("add download thread pool sendAgainSuccess!");
				} else {
					logger.i("add download thread pool fail!");
				}

				CMD = -1;
				break;
			case FTP_CMD.PAUSE_DOWNLOAD:
				ftp.setFtpStopDownload();
				CMD = -1;
				break;
			case FTP_CMD.UPLOAD:
				ArrayList<File> uploadFiles = (ArrayList<File>) ftpCommand.arg1;
				String uploadPath = (String) ftpCommand.arg2;

				// TODO: need a thread pool
				if (uploadFiles.size() == 0) {
					logger.w("upload fail,file size is 0 on task");
					CMD = -1;
					break;
				}

				uploadFiles(uploadFiles, uploadPath);

				CMD = -1;
				break;
			case FTP_CMD.DELETE:
				ArrayList<FileInfo> deleteFiles = (ArrayList<FileInfo>) ftpCommand.arg1;
				if (deleteFiles.size() == 0) {
					logger.w("delete fail,file size is 0 on task");
					CMD = -1;
					break;
				}

				logger.i("delete file size: " + deleteFiles.size());

				for (FileInfo f : deleteFiles) {
					logger.i("delete file lists: " + f.fileName);
				}
				deleteFiles(deleteFiles);
				CMD = -1;
				break;

			case FTP_CMD.MOVE:
				ArrayList<FileInfo> moveFiles = (ArrayList<FileInfo>) ftpCommand.arg1;
				String movePath = (String) ftpCommand.arg2;

				if ((moveFiles != null) && (movePath != null)) {
					moveFiles(moveFiles, movePath);
				}

				CMD = -1;
				break;
			case FTP_CMD.COPY:
				CMD = -1;
				break;
			case FTP_CMD.CREATE_FOLDER:
				String createFolder = (String) ftpCommand.arg1;
				logger.i("FTP_CMD.CREATE_FOLDER");
				if (createFolder != null) {
					createFolder(createFolder);
				}
				logger.i("FTP_CMD.CREATE_FOLDER = " + createFolder);
				CMD = -1;
				break;
			case FTP_CMD.RENAME:
				String fromRename = (String) ftpCommand.arg1;
				String toRename = (String) ftpCommand.arg2;

				if ((fromRename != null) && (toRename != null)) {
					renameFile(fromRename, toRename);
				}

				CMD = -1;
				break;
			case FTP_CMD.SHARE:
				ArrayList<FileInfo> shareFiles = (ArrayList<FileInfo>) ftpCommand.arg1;

				if (shareFiles != null) {
					shareFiles(shareFiles);
				}

				CMD = -1;
				break;
			case FTP_CMD.CLOSE:
				try {
					if (ftp.isFtpConnected()) {
						ftp.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				isInit = false;
				isLogin = false;
				isRunning = false;
				CMD = -1;
				break;
			default:
				CMD = -1;
				break;
			}
		}

	}

	FtpTransferIRetrieveListener TransferListener = new FtpTransferIRetrieveListener() {
		@Override
		public void onTrack(String filePath, long nowOffset) {
			// changeProgressText((int) now);
			// logger.i("[" + filePath + "] transfer progress: " + now);

			TransferTracker transfer = new TransferTracker();
			transfer.filePath = filePath;
			transfer.process = nowOffset;

			sendMsg(MSG_TYPE.MSG_ON_DOWNLOAD, transfer);
		}

		@Override
		public void onError(Object obj, int type) {
			logger.i("transfer error: " + obj);
			sendMsg(MSG_TYPE.MSG_ERROR_DOWNLOAD, obj);
			// sendMsg(obj + "");
		}

		public void onDone() {
			logger.i("transfer done......");
			sendMsg(MSG_TYPE.MSG_END_DOWNLOAD, "down load end!");
			// changeProgressText(100);
		}

		@Override
		public void onCancel(Object obj) {
			sendMsg(MSG_TYPE.MSG_PAUSE_DOWNLOAD, "down load cancel!");
			logger.i("transfer cancel.....");
		}

		@Override
		public void onStart(String filePath) {
			// TODO Auto-generated method stub
			sendMsg(MSG_TYPE.MSG_START_DOWNLOAD, filePath);
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

	boolean ftpConnect() {
		boolean iRet = false;
		
		if (mState == FTP_STATE.Connected) {
			return true;
		}
		
		if (!isNetworkConnected(mContext)) {
			logger.i("can not connect the network!");
			mState = FTP_STATE.DisConnected;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect network!");
			// ftp_close();
			return false;
		}

		iRet = ftp.ftpConnect();

		if (iRet) {
			mState = FTP_STATE.Connected;
			return true;
		} else {
			mState = FTP_STATE.DisConnected;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp connect fail!");
			return false;
		}

	}

	boolean ftpLogin() {
		boolean iRet = false;

		if (mState == FTP_STATE.Logined) {
			return true;
		}
		
		if (!isNetworkConnected(mContext)) {
			logger.i("can not connect the network!");
			mState = FTP_STATE.DisConnected;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect network!");
			// ftp_close();
			return false;
		}

		try {
			boolean isConn = false;
			isConn = ftp.isFtpConnected();
			if (!isConn) {
				mState = FTP_STATE.DisConnected;
				logger.i("ftp is not connected!....");
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect ftp server!");
				// ftp_close();
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		iRet = ftp.ftpLogin();

		if (iRet) {
			mState = FTP_STATE.Logined;
			isLogin = true;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp login Success!");
			return true;
		} else {
			mState = FTP_STATE.UnLogined;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp login fail!");
			return false;
		}
	}

	private void ftpConnectLogin() {
		boolean iRet = false;

		if (mState == FTP_STATE.Logined) {
			return;
		}

		if (!isNetworkConnected(mContext)) {
			logger.i("can not connect the network!");
			mState = FTP_STATE.DisConnected;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect network!");
			return;
		}

		iRet = ftp.ftpConnect();

		if (iRet) {
			//mState = FTP_STATE.Connected;
		} else {
			//mState = FTP_STATE.DisConnected;
			//sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp connect fail!");
			return;
		}

		try {
			boolean isConn = false;
			isConn = ftp.isFtpConnected();
			if (!isConn) {
				mState = FTP_STATE.DisConnected;
				logger.i("ftp is not connected!....");
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect ftp server!");
				// ftp_close();
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		iRet = ftp.ftpLogin();

		if (iRet) {
			mState = FTP_STATE.Logined;
			isLogin = true;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp login Success!");
		} else {
			mState = FTP_STATE.UnLogined;
			isLogin = false;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "ftp login fail!");
		}

	}

	private void deleteFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not find the remote files!");
			logger.w("delete fail,can't not find the remote file!");
			return;
		}

		for (FileInfo f : remote) {
			logger.i("delete file lists: " + f.fileName);
			String remotePath = f.filePath + File.separator + f.fileName;
			result = ftp.deleteFiles(remotePath);

			if (result) {
				sendMsg(MSG_TYPE.MSG_REFRESH_UI_CMD, "delete sendAgainSuccess");
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "delete file [" + remotePath
						+ "] sendAgainSuccess!");
			} else {
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "delete file [" + remotePath
						+ "] fail!");
			}
		}

	}

	private void renameFile(String fromFile, String toFile) throws IOException {
		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
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
			sendMsg(MSG_TYPE.MSG_REFRESH_UI_CMD, "rename sendAgainSuccess");
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "rename file [" + fromFile
					+ "] sendAgainSuccess!");
		} else {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "rename file [" + fromFile
					+ "] fail!");
		}

	}

	private void moveFiles(ArrayList<FileInfo> fromFiles, String remotePath)
			throws IOException {
		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
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
				sendMsg(MSG_TYPE.MSG_REFRESH_UI_CMD, "move sendAgainSuccess");
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "move file [" + fromFile
						+ "] sendAgainSuccess!");
			} else {
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "move file [" + fromFile
						+ "] fail!");
				logger.w("move file fail fromFile = " + fromFile + ",toFile = "
						+ toFile);
				reply_code();
			}
		}

	}

	private void uploadFiles(ArrayList<File> local, String remotePath)
			throws Exception {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((local == null) || (local.size() == 0)) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not find the local files!");
			logger.w("download fail,can't not find the local files!");
			return;
		}

		for (File f : local) {
			String localPath = f.getPath();
			result = ftp.upload(localPath,
					remotePath + File.separator + f.getName());
			if (!result) {
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "upload fail: localPath = "
						+ localPath + ",remotePath = " + remotePath
						+ File.separator + f.getName());
				logger.w("upload fail: localPath = " + localPath
						+ ",remotePath = " + remotePath + File.separator
						+ f.getName());
				return;
			}
		}
		sendMsg(MSG_TYPE.MSG_REFRESH_UI_CMD, "upload sendAgainSuccess");
		sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "upload sendAgainSuccess!");
	}

	private boolean downloadFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
			return false;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not find the remote files!");
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
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "download failure: "
						+ remotePath);
				return false;
			}

		}
		sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "download sendAgainSuccess!");
		result = true;
		return result;
	}

	private void showFiles(String path) {
		if (!isNetworkConnected(mContext)) {
			logger.i("can not connect the network!");
			mState = FTP_STATE.DisConnected;
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect network!");
			return;
		}

		try {
			boolean isConn = false;
			isConn = ftp.isFtpConnected();
			if (!isConn) {
				mState = FTP_STATE.DisConnected;
				logger.i("ftp is not connected!");
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not connect ftp server!");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		try {
			List<FTPFile> ftpFile = ftp.showListFile(path);

			if (false) {
				for (FTPFile file : ftpFile) {
					logger.i("filename = " + file.getName());
				}
			}

			sendMsg(MSG_TYPE.MSG_REFRESH_UI, ftpFile);

		} catch (Exception e) {
			mState = FTP_STATE.DisConnected;
			e.printStackTrace();
		}

	}

	private void listFiles(String path) {
		if (!isLogin) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "no login yet!");
			logger.i("listFiles fail,have not login!");
			return;
		}

		try {
			List<FTPFile> ftpFile = ftp.showListFile(path);

			ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();

			for (FTPFile child : ftpFile) {
				FileInfo lFileInfo = Util.GetFtpFileInfo(child, path, null,
						false);
				if (lFileInfo != null) {
					fileList.add(lFileInfo);
				}
			}

			if (mOnCallResponse != null) {
				mOnCallResponse.callResponse(fileList);
				mOnCallResponse = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reply_code() {
		if (!isNetworkConnected(mContext)) {
			logger.i("can not connect the network!");
			mState = FTP_STATE.DisConnected;
			// ftp_close();
			return;
		}

		try {
			boolean isConn = false;
			isConn = ftp.isFtpConnected();
			if (!isConn) {
				mState = FTP_STATE.DisConnected;
				logger.i("ftp is not connected!....");
				// ftp_close();
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.w("ftp reply code = " + ftp.getReply());
	}

	private void shareFiles(ArrayList<FileInfo> remote) {
		boolean result = false;

		if (!isLogin) {
			return;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "can not find the remote files!");
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
				sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "download failure: "
						+ remotePath);
				return;
			}

			ShareFileInfo localFile = new ShareFileInfo();
			localFile = Util.GetShareFileInfo(localPath);
			shareFiles.add(localFile);
		}

		mOnCallResponse.callResponse(shareFiles);
		// sendMsg(MSG_SHARE_FILE, shareFiles);

		sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "Ftp file share sendAgainSuccess!");
	}

	private void createFolder(String remoteFolder) throws IOException {
		boolean result = false;

		if (!isLogin) {
			return;
		}

		result = ftp.createFolder(remoteFolder);

		if (result) {
			logger.i("create folder suceess: " + remoteFolder);
			sendMsg(MSG_TYPE.MSG_REFRESH_UI_CMD, "create folder sendAgainSuccess");
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "create folder sendAgainSuccess!");
			sendMsg(MSG_TYPE.MSG_CREATE_FOLDER, SUCCESS);
		} else {
			logger.i("create folder fail: " + remoteFolder);
			sendMsg(MSG_TYPE.MSG_SHOW_TOAST, "create folder fail: "
					+ remoteFolder);
			sendMsg(MSG_TYPE.MSG_CREATE_FOLDER, FAIL);
			logger.i("create folder fail: " + remoteFolder);
			reply_code();
		}

	}

	public String getServerAddress(Context ctx) {
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
