package com.alcatel.smartlinkv3.fileexplorer;

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

	// message type
	private static final int MSG_SHOW_TOAST = 1;
	private static final int MSG_REFRESH_UI = 10;

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

	public void init() {

	}

	public void start() {

	}

	public void stop() {

	}

	public void ftpCommandHandler() {

	}

	public void ftpMsgHandler() {

	}
}
