/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPFile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileExplorerTabActivity.OnBackPressedListener;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewInteractionHub.Mode;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewInteractionHub.UICmd;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewInteractionHub.uiCommandListener;
import com.alcatel.smartlinkv3.ftp.client.FtpClientModel;
import com.alcatel.smartlinkv3.ftp.client.FtpManager;
import com.alcatel.smartlinkv3.ftp.client.FtpManagerIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.FtpTransferIRetrieveListener;
import com.alcatel.smartlinkv3.ftp.client.pubLog;

public class FtpFileViewFragment extends Fragment implements
		IFileInteractionListener, OnBackPressedListener {

	public static final String EXT_FILTER_KEY = "ext_filter";

	private static final String LOG_TAG = "FtpFileViewActivity";

	public static final String EXT_FILE_FIRST_KEY = "ext_file_first";

	public static final String ROOT_DIRECTORY = "root_directory";

	public static final String PICK_FOLDER = "pick_folder";

	private ListView mFileListView;

	// private TextView mCurrentPathTextView;
	private ArrayAdapter<FileInfo> mAdapter;

	private FtpFileViewInteractionHub mFileViewInteractionHub;

	private FileCategoryHelper mFileCagetoryHelper;

	private FileIconHelper mFileIconHelper;

	private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

	private Activity mActivity;

	private View mRootView;

	private static final String sdDir = Util.getSdDirectory();

	private FtpManager ftp = null;
	private Thread thread = null;
	private Context mContext = null;
	private pubLog logger = null;
	private FtpClientModel m_ftp = null;

	// memorize the scroll positions of previous paths
	private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<PathScrollPositionItem>();
	private String mPreviousPath;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.v(LOG_TAG, "received broadcast:" + intent.toString());
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}
	};

	uiCommandListener mUiCmdListener = new uiCommandListener() {
		@Override
		public void showfiles(ArrayList<FileInfo> remote) {
			// TODO Auto-generated method stub
			ArrayList<FileInfo> files = new ArrayList<FileInfo>();

			for (FileInfo f : remote) {
				files.add(f);
			}

			ftpTask.CMD = SHOWFILE;
			ftpTask.setRemoteFiles(files);
		}

		@Override
		public void upload(ArrayList<FileInfo> local, String remote) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ui_command(UICmd cmd, String args) {
			// TODO Auto-generated method stub
			return;
		}

		@Override
		public void move(ArrayList<FileInfo> remote1, String remote2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void download(ArrayList<FileInfo> remote, String local) {
			// TODO Auto-generated method stub
			ArrayList<FileInfo> files = new ArrayList<FileInfo>();

			for (FileInfo f : remote) {
				files.add(f);
			}
			ftpTask.CMD = DOWNLOAD;
			ftpTask.setRemoteFiles(files);
		}

		@Override
		public void delete(ArrayList<FileInfo> remote) {
			// TODO Auto-generated method stub
			ArrayList<FileInfo> files = new ArrayList<FileInfo>();

			for (FileInfo f : remote) {
				files.add(f);
			}
			ftpTask.CMD = DELETE;
			ftpTask.setRemoteFiles(files);
		}

		@Override
		public void copy(ArrayList<FileInfo> remote1, String remote2) {
			// TODO Auto-generated method stub

		}

	};

	volatile long every = 0;

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

	// ftp command type
	private static final int CONNECT = 1;
	private static final int SHOWFILE = 3;
	private static final int DOWNLOAD = 4;
	private static final int UPLOAD = 5;


	private static final int CLOSE = -2;
	private static final int GETFILE = 7;
	private static final int DELETE = 8;
	private static final int PAUSE_DOWNLOAD = 9;
	private static final int MOVE = 10;
	private static final int COPY = 11;

	// message type
	private static final int MSG_SHOW_TOAST = 1;
	private static final int MSG_REFRESH_UI = 10;

	// ftp task
	private FtpCommandProc ftpTask = new FtpCommandProc();
	private boolean isLogin = false;

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
			isLogin = true;
			handler.sendEmptyMessage(2);
		} else {
			sendMsg(MSG_SHOW_TOAST, "ftp login fail!");
		}
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
					ftpConnect();
					CMD = -1;
					break;
				case 2:
					// ftpChangeDir();
					CMD = -1;
					break;
				case SHOWFILE:
					showFiles(p1);
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
					downloadFile(p2);
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
					deleteFiles(p2);
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

	private void deleteFiles(ArrayList<FileInfo> remote) {

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
			ftp.deleteFiles(remotePath);
		}

		sendMsg(MSG_SHOW_TOAST, "delete success!");

	}

	private void downloadFile(ArrayList<FileInfo> remote) {

		if (!isLogin) {
			sendMsg(MSG_SHOW_TOAST, "no login yet!");
			return;
		}

		if ((remote == null) || (remote.size() == 0)) {
			sendMsg(MSG_SHOW_TOAST, "can not find the remote files!");
			logger.w("download fail,can't not find the remote file!");
			return;
		}

		for (FileInfo f : remote) {
			logger.i("download file lists: " + f.fileName);

			String remotePath = f.filePath + File.separator + f.fileName;
			String localPath = m_ftp.localDir + File.separator
					+ f.fileName;
			ftp.download(localPath, remotePath);

		}
		sendMsg(MSG_SHOW_TOAST, "download success!");

	}

	private void showFiles(String path) {
		if (isLogin) {
			try {
				String fstr = "";
				FTPFile[] ftpFile = ftp.showListFile(path);
				sendMsg(MSG_REFRESH_UI, ftpFile);

			} catch (Exception e) {
				e.printStackTrace();
				sendMsg(MSG_SHOW_TOAST, "Ftp File List Error:" + e);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case -1:
				break;
			case MSG_SHOW_TOAST:
				Toast.makeText(mContext, msg.obj + "", Toast.LENGTH_SHORT)
						.show();
				mFileViewInteractionHub.refreshFileList();
				break;
			case 2:
				// enableBtn(!isOK);
				break;
			case 5:
				// progressText.setText(msg.obj + "%");
				// progressBar.setProgress(Integer.parseInt(msg.obj + ""));
				// if (("" + msg.obj).equals("100")) {
				// fileListDialog.findViewById(R.id.getfileBtn).setEnabled(
				// true);
				// }
				break;
			case PAUSE_DOWNLOAD:
				ftp.setFtpStopDownload();
				break;
			case MSG_REFRESH_UI:
				FTPFile[] listFiles = (FTPFile[]) msg.obj;

				if (listFiles == null) {
					break;
				}

				String path = mFileViewInteractionHub.getCurrentPath();

				FileSortHelper sort = mFileViewInteractionHub.getCurrentSort();

				final int pos = computeScrollPosition(path);
				ArrayList<FileInfo> fileList = mFileNameList;
				fileList.clear();

				for (FTPFile child : listFiles) {

					FileInfo lFileInfo = Util.GetFtpFileInfo(child, path,
							mFileCagetoryHelper.getFilter(), Settings
									.instance().getShowDotAndHiddenFiles());

					if (lFileInfo != null) {
						fileList.add(lFileInfo);
					}

				}

				sortCurrentList(sort);
				showEmptyView(fileList.size() == 0);
				mFileListView.post(new Runnable() {
					@Override
					public void run() {
						mFileListView.setSelection(pos);
					}
				});

				break;

			default:
				break;
			}
		}
	};

	private boolean ftp_init() {
		ftp = new FtpManager();

		mContext = getActivity().getApplicationContext();
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
		ftpTask.CMD = CONNECT;
		thread.start();

		return true;
	}
	
	
	private String getServerAddress(Context ctx){  
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo(); 
        return Formatter.formatIpAddress(dhcpInfo.gateway);  
    }
	
	private boolean mBackspaceExit;
	
	interface IConnectedActionMode {
		public void setActionMode(ActionMode actionMode);
	    public ActionMode getActionMode();
	    public ActionMode launchActionMode(ActionMode.Callback callback);
	    public ActionBar obtainActionBar();
	}
	private IConnectedActionMode mCommectedActionMode;
	
	@Override  
	public void onAttach(Activity activity){  
	      super.onAttach(activity);	      
	      try{  
	          mCommectedActionMode =(IConnectedActionMode)activity;
	      }catch(ClassCastException e){
	          throw new ClassCastException( 
	        		  activity.toString() + "must implement IConnectedActionMode");
	      }
	}
	
	@Override
	public Activity obtainActivity() {
		return this.getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		boolean iRet = ftp_init();

		if (!iRet) {
			logger.w("ftp init fail!");
		}
				
		mActivity = getActivity();
			
		// getWindow().setFormat(android.graphics.PixelFormat.RGBA_8888);
		mRootView = inflater.inflate(R.layout.ftp_file_explorer_list,
				container, false);
		ActivitiesManager.getInstance().registerActivity(
				ActivitiesManager.ACTIVITY_FILE_VIEW, mActivity);

		mFileCagetoryHelper = new FileCategoryHelper(mActivity);
		mFileViewInteractionHub = new FtpFileViewInteractionHub(this);
		mFileViewInteractionHub.setConnectdActionMode(mCommectedActionMode);
		mFileViewInteractionHub.setUiCommandListener(mUiCmdListener);// test

		Intent intent = mActivity.getIntent();
		String action = intent.getAction();

		if (!TextUtils.isEmpty(action)
				&& (action.equals(Intent.ACTION_PICK) || action
						.equals(Intent.ACTION_GET_CONTENT))) {
			mFileViewInteractionHub.setMode(Mode.Pick);

			boolean pickFolder = intent.getBooleanExtra(PICK_FOLDER, false);
			if (!pickFolder) {
				String[] exts = intent.getStringArrayExtra(EXT_FILTER_KEY);
				if (exts != null) {
					mFileCagetoryHelper.setCustomCategory(exts);
				}
			} else {
				mFileCagetoryHelper.setCustomCategory(new String[] {});
				mRootView.findViewById(R.id.pick_operation_bar).setVisibility(
						View.VISIBLE);

				mRootView.findViewById(R.id.button_pick_confirm)
						.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								try {
									Intent intent = Intent.parseUri(
											mFileViewInteractionHub
													.getCurrentPath(), 0);
									mActivity.setResult(Activity.RESULT_OK,
											intent);
									mActivity.finish();
								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
							}
						});

				mRootView.findViewById(R.id.button_pick_cancel)
						.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								mActivity.finish();
							}
						});
			}
		} else {
			mFileViewInteractionHub.setMode(Mode.View);
		}

		mFileListView = (ListView) mRootView.findViewById(R.id.file_path_list);
		mFileIconHelper = new FileIconHelper(mActivity);
		mAdapter = new FtpFileListAdapter(mActivity,
				R.layout.ftp_file_browser_item, mFileNameList,
				mFileViewInteractionHub, mFileIconHelper);

		boolean baseSd = intent.getBooleanExtra(GlobalConsts.KEY_BASE_SD,
				!FileExplorerPreferenceActivity.isReadRoot(mActivity));
		Log.i(LOG_TAG, "baseSd = " + baseSd);
		// TODO
		String rootDir = intent.getStringExtra(ROOT_DIRECTORY);

		/*
		 * if (!TextUtils.isEmpty(rootDir)) { if (baseSd &&
		 * this.sdDir.startsWith(rootDir)) { rootDir = this.sdDir; } } else {
		 * rootDir = baseSd ? this.sdDir : GlobalConsts.ROOT_PATH; }
		 */
		rootDir = "/";
		// TODO
		mFileViewInteractionHub.setRootPath(rootDir);

		// TODO
		String currentDir = FileExplorerPreferenceActivity
				.getPrimaryFolder(mActivity);

		// TODO
		Uri uri = intent.getData();
		if (uri != null) {
			// if (baseSd && this.sdDir.startsWith(uri.getPath())) {
			// currentDir = this.sdDir;
			// } else {
			currentDir = uri.getPath();
			// }
		}
		// TODO
		currentDir = "/";
		mFileViewInteractionHub.setCurrentPath(currentDir);
		Log.i(LOG_TAG, "CurrentDir = " + currentDir);

		mFileViewInteractionHub.setHostTag(m_ftp.host + m_ftp.port + "/");

		mBackspaceExit = (uri != null)
				&& (TextUtils.isEmpty(action) || (!action
						.equals(Intent.ACTION_PICK) && !action
						.equals(Intent.ACTION_GET_CONTENT)));

		mFileListView.setAdapter(mAdapter);
		mFileViewInteractionHub.refreshFileList();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		mActivity.registerReceiver(mReceiver, intentFilter);

		updateUI();
		setHasOptionsMenu(true);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mActivity.unregisterReceiver(mReceiver);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		mFileViewInteractionHub.onPrepareOptionsMenu(menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mFileViewInteractionHub.onCreateOptionsMenu(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onBack() {
		if (mBackspaceExit || !Util.isSDCardReady()
				|| mFileViewInteractionHub == null) {
			return false;
		}
		return mFileViewInteractionHub.onBackPressed();
	}

	private class PathScrollPositionItem {
		String path;
		int pos;

		PathScrollPositionItem(String s, int p) {
			path = s;
			pos = p;
		}
	}

	// execute before change, return the memorized scroll position
	private int computeScrollPosition(String path) {
		int pos = 0;
		if (mPreviousPath != null) {
			if (path.startsWith(mPreviousPath)) {
				int firstVisiblePosition = mFileListView
						.getFirstVisiblePosition();
				if (mScrollPositionList.size() != 0
						&& mPreviousPath.equals(mScrollPositionList
								.get(mScrollPositionList.size() - 1).path)) {
					mScrollPositionList.get(mScrollPositionList.size() - 1).pos = firstVisiblePosition;
					Log.i(LOG_TAG, "computeScrollPosition: update item: "
							+ mPreviousPath + " " + firstVisiblePosition
							+ " stack count:" + mScrollPositionList.size());
					pos = firstVisiblePosition;
				} else {
					mScrollPositionList.add(new PathScrollPositionItem(
							mPreviousPath, firstVisiblePosition));
					Log.i(LOG_TAG, "computeScrollPosition: add item: "
							+ mPreviousPath + " " + firstVisiblePosition
							+ " stack count:" + mScrollPositionList.size());
				}
			} else {
				int i;
				boolean isLast = false;
				for (i = 0; i < mScrollPositionList.size(); i++) {
					if (!path.startsWith(mScrollPositionList.get(i).path)) {
						break;
					}
				}
				// navigate to a totally new branch, not in current stack
				if (i > 0) {
					pos = mScrollPositionList.get(i - 1).pos;
				}

				for (int j = mScrollPositionList.size() - 1; j >= i - 1
						&& j >= 0; j--) {
					mScrollPositionList.remove(j);
				}
			}
		}

		Log.i(LOG_TAG, "computeScrollPosition: result pos: " + path + " " + pos
				+ " stack count:" + mScrollPositionList.size());
		mPreviousPath = path;
		return pos;
	}

	public boolean onRefreshFileList(String path, FileSortHelper sort) {
		ftpTask.CMD = SHOWFILE;
		ftpTask.setRemotePath(path);
		return true;
	}

	private void updateUI() {
		boolean sdCardReady = Util.isSDCardReady();
		View noSdView = mRootView.findViewById(R.id.sd_not_available_page);
		noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

		View navigationBar = mRootView.findViewById(R.id.navigation_bar);
		navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
		mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

		if (sdCardReady) {
			mFileViewInteractionHub.refreshFileList();
		}
	}

	private void showEmptyView(boolean show) {
		View emptyView = mRootView.findViewById(R.id.empty_view);
		if (emptyView != null)
			emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View getViewById(int id) {
		return mRootView.findViewById(id);
	}

	@Override
	public void onDataChanged() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}

		});
	}

	@Override
	public void onPick(FileInfo f) {
		try {
			logger.v("onPick(),current ftp path = " + f.filePath);

			Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath))
					.toString(), 0);
			mActivity.setResult(Activity.RESULT_OK, intent);
			mActivity.finish();
			return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldShowOperationPane() {
		return true;
	}

	@Override
	public boolean onOperation(int id) {
		return false;
	}

	@Override
	public String getDisplayPath(String path) {
		if (path.startsWith(this.sdDir)
				&& !FileExplorerPreferenceActivity.showRealPath(mActivity)) {
			return getString(R.string.sd_folder)
					+ path.substring(this.sdDir.length());
		} else {
			return path;
		}
	}

	@Override
	public String getRealPath(String displayPath) {
		final String perfixName = getString(R.string.sd_folder);
		if (displayPath.startsWith(perfixName)) {
			return this.sdDir + displayPath.substring(perfixName.length());
		} else {
			return displayPath;
		}
	}

	@Override
	public boolean onNavigation(String path) {
		return false;
	}

	@Override
	public boolean shouldHideMenu(int menu) {
		return false;
	}

	public void copyFile(ArrayList<FileInfo> files) {
		mFileViewInteractionHub.onOperationCopy(files);
	}

	public void refresh() {
		if (mFileViewInteractionHub != null) {
			mFileViewInteractionHub.refreshFileList();
		}
	}

	public void moveToFile(ArrayList<FileInfo> files) {
		mFileViewInteractionHub.moveFileFrom(files);
	}

	public interface SelectFilesCallback {
		// files equals null indicates canceled
		void selected(ArrayList<FileInfo> files);
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mFileViewInteractionHub.startSelectFiles(callback);
	}

	@Override
	public FileIconHelper getFileIconHelper() {
		return mFileIconHelper;
	}

	public boolean setPath(String location) {
		if (!location.startsWith(mFileViewInteractionHub.getRootPath())) {
			return false;
		}
		mFileViewInteractionHub.setCurrentPath(location);
		mFileViewInteractionHub.refreshFileList();
		return true;
	}

	@Override
	public FileInfo getItem(int pos) {
		if (pos < 0 || pos > mFileNameList.size() - 1)
			return null;

		return mFileNameList.get(pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sortCurrentList(FileSortHelper sort) {
		Collections.sort(mFileNameList, sort.getComparator());
		onDataChanged();
	}

	@Override
	public ArrayList<FileInfo> getAllFiles() {
		return mFileNameList;
	}

	@Override
	public void addSingleFile(FileInfo file) {
		mFileNameList.add(file);
		onDataChanged();
	}
	
	// TODO 添加接口
	@Override
	public void notifyDataChanged() {
		onDataChanged();
	}

	@Override
	public int getItemCount() {
		return mFileNameList.size();
	}

	@Override
	public void runOnUiThread(Runnable r) {
		mActivity.runOnUiThread(r);
	}
}
