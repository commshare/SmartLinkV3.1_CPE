package com.alcatel.smartlinkv3.fileexplorer;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewActivity.FtpCommandProc;
import com.alcatel.smartlinkv3.ftp.client.FtpClientModel;
import com.alcatel.smartlinkv3.ftp.client.FtpManager;
import com.alcatel.smartlinkv3.ftp.client.FtpManagerIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.FtpTransferIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.pubLog;

//TODO: CallBack
public class FtpFileCommandProc {
	// ftp command
	private static final int CONNECT = 1;
	private static final int SHOWFILE = 3;
	private static final int DOWNLOAD = 4;
	private static final int UPLOAD = 5;

	private static final int GETFILE = 7;
	private static final int DELETE = 8;
	private static final int PAUSE_DOWNLOAD = 9;
	private static final int CLOSE = -2;
	private static final int MOVE = 10;
	private static final int COPY = 11;

	// message type
	private static final int MSG_SHOW_TOAST = 1;
	private static final int MSG_REFRESH_UI = 10;

	private FtpCommandProc ftpTask = new FtpCommandProc();
	private FtpManager ftp = null;
	private Thread thread = null;
	private Context mContext;
	private pubLog logger = null;
	private FtpClientModel m_ftp = null;
	

	
	
	public interface FtpCommandListener {
		// runnable thread
		void ftp_connect();

		void ftp_showfiles();

		void ftp_download();

		void ftp_stop_download();

		void ftp_upload();

		void ftp_delete();

		void ftp_close();

		// ui thread
		void ftp_msg_proc(int msgType);

	}

	public boolean init(Context mContext) {
		ftp = new FtpManager();

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

		//ftp.setTransferFtpListener(TransferListener);
		//ftp.setFtpManagerListener(FtpManagerListener);
		
		m_ftp = new FtpClientModel();
		ftp.setConfig(mContext, m_ftp);

		thread = new Thread(ftpTask);
		ftpTask.CMD = CONNECT;
		thread.start();

		return true;
	}

	public void start() {

	}

	public void stop() {

	}

	// TODO : cmd,argc,args
	public void ftpCmd(int cmd, int argc, String[] args) {

		switch (cmd) {
			case CONNECT:
				break;
			case SHOWFILE:
				break;
			default:
				break;

		}

	}

	public void ftpCommandHandler() {

	}

	public void ftpMsgHandler() {

	}

	class FtpCommandProc implements Runnable {
		public int CMD = -1;
		public String p1 = "/";
		private ArrayList<FileInfo> p2 = new ArrayList<FileInfo>();
		private ArrayList<FileInfo> p3 = new ArrayList<FileInfo>();
		public boolean running = false;

		public void setRemotePath(String p1) {
			this.p1 = p1;
		}

		public void setRemoteFiles(ArrayList<FileInfo> p2) {
			this.p2 = p2;
		}

		public void setLocalFiles(ArrayList<FileInfo> p3) {
			this.p3 = p3;
		}

		@Override
		public void run() {
			running = true;
			logger.v("enter runnable...");
			while (running) {
				switch (CMD) {
				case CONNECT:
					logger.v("ftp is connecting!....");
					// ftpConnect();
					CMD = -1;
					break;
				case 2:
					// ftpChangeDir();
					CMD = -1;
					break;
				case SHOWFILE:
					// showFiles(p1);
					CMD = -1;
					break;
				case DOWNLOAD:
					if (p2.size() == 0) {
						logger.w("download fail,file size is 0 on task");
						CMD = -1;
						break;
					}

					logger.i("download file size: " + p2.size());

					for (FileInfo f : p2) {
						logger.i("download file lists: " + f.fileName);
					}

					logger.i("on downloanning...");
					// downloadFile(p2);
					CMD = -1;
					break;
				case UPLOAD:
					// TODO
					try {
						ftp.upload("/mnt/sdcard/ftpconf/Burning.mp3",
								"/Burning.mp3");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					CMD = -1;
					break;

				case DELETE:
					if (p2.size() == 0) {
						logger.w("delete fail,file size is 0 on task");
						CMD = -1;
						break;
					}

					logger.i("delete file size: " + p2.size());

					for (FileInfo f : p2) {
						logger.i("delete file lists: " + f.fileName);
					}
					// deleteFiles(p2);
					CMD = -1;
					break;

				case MOVE:
					CMD = -1;
					break;

				case COPY:
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
				case GETFILE:
					CMD = -1;
				default:
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
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

}
