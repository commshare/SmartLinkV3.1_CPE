package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.ENUM.ServiceState;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileModel;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileModel.FileType;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask.ListFilesResult;
import com.alcatel.smartlinkv3.samba.SmbDownloadFilesTask;
import com.alcatel.smartlinkv3.samba.SmbNewFolderTask;
import com.alcatel.smartlinkv3.samba.SmbService;
import com.alcatel.smartlinkv3.samba.SmbUploadFilesTask;
import com.alcatel.smartlinkv3.samba.SmbUtils;
import com.alcatel.smartlinkv3.samba.SmbUtils.LIST_FILE_MODEL;
import com.alcatel.smartlinkv3.ui.activity.InquireDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1.OnBtnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LocalStorageActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private Button m_backBtn = null;
	private Button m_editBtn = null;
	private TextView m_noFileTV = null;
	public static File m_curDirectory = null;
	private ImageView m_rootDirImage = null;
	private ImageView m_arrowImage1 = null;
	private ImageView m_arrowImage2 = null;
	private TextView m_dirParentTv = null;
	private TextView m_dirTv = null;
	private TextView m_newFolder = null;
	private TextView m_upload = null;
	private TextView m_searchBtn = null;
	private TextView m_title = null;

	private ListView m_localStorageListView = null;
	private ArrayList<FileItem> m_storageListData = new ArrayList<FileItem>();
	public static String FLAG_CURRENT_LOCATION = "com.alcatel.cpe.ui.activity.flagcurrentlocation";// value:local
																									// samba
	public static String FLAG_LOCAL = "local";
	public static String FLAG_SAMBA = "samba";
	public static String CURRENT_DIRECTORY = "com.alcatel.cpe.ui.activity.currentdirectory";

	private String m_flag = FLAG_LOCAL;
	public static String m_curSambaDirectory;
	private String m_parentSmabaDirectory;

	private ProgressBar m_progressWaiting = null;
	private String m_writingFileName;
	
	private static SmbUploadFilesTask  	m_uploadTask; 
	private static SmbDownloadFilesTask m_downloadTask;
	
	private InputDialog1 m_newFolderDlg; 
	

	// samba list files
	private Handler m_smbListFilesTaskHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
			
				ListFilesResult res = (ListFilesResult) msg.obj;
				m_progressWaiting.setVisibility(View.GONE);
				m_parentSmabaDirectory = res.mParentPath;
			//	m_storageListData.clear();
				m_storageListData = (ArrayList<FileItem>) res.mFiles.clone();	
				m_localStorageListView.setEnabled(true);
				showSambaDirectoryLayout();
				updateListView();
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:
				m_localStorageListView.setEnabled(true);
				m_progressWaiting.setVisibility(View.GONE);
				
				String strErr =  (String) msg.obj;			
				Toast.makeText(LocalStorageActivity.this, strErr,
						Toast.LENGTH_LONG).show();
				
			/*	String strErr = LocalStorageActivity.this
						.getString(R.string.list_samba_files_failed);				
				Toast.makeText(LocalStorageActivity.this, strErr,
						Toast.LENGTH_LONG).show();
						*/
				break;

			}
		}
	};

	// samba create new folder
	private Handler m_smbNewFolderTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
				getSambaStorageListData();
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:
				String msgError = LocalStorageActivity.this
						.getString(R.string.new_folder_failed);
				Toast.makeText(LocalStorageActivity.this, msgError,
						Toast.LENGTH_SHORT).show();
				break;

			case SmbUtils.SMB_MSG_FILE_EXISTS:
				String msgExists = LocalStorageActivity.this
						.getString(R.string.directory_is_exist);
				Toast.makeText(LocalStorageActivity.this, msgExists,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private FilesReceiver m_fileReceiver = new FilesReceiver();

	private class FilesReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// samba upload
			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				if (intent.getAction().equals(
						SmbUtils.SMB_MSG_UPLOAD_FILES_UPDATE)) {

					String strPath = intent
							.getStringExtra(SmbUtils.SMB_OPT_FILES_PATH);
					int nPos = intent.getIntExtra(
							SmbUtils.SMB_OPT_FILES_PROGRESS, 0);
					addUploadingStatus(strPath, nPos);
					m_writingFileName = FileUtils.getFileName(strPath);
					updateListView();
				} else if (intent.getAction().equals(
						SmbUtils.SMB_MSG_UPLOAD_FILES_ERROR)
						|| intent.getAction().equals(
								SmbUtils.SMB_MSG_UPLOAD_FILES_FINISH)) {

					String strPath = intent
							.getStringExtra(SmbUtils.SMB_OPT_FILES_PATH);
					if (intent.getAction().equals(
							SmbUtils.SMB_MSG_UPLOAD_FILES_ERROR)) {
						if (strPath != null) {
							String err = LocalStorageActivity.this
									.getString(R.string.smb_upload_file_failed);
							err = String.format(err,
									FileUtils.getFileName(strPath));
							Toast.makeText(LocalStorageActivity.this, err,
									Toast.LENGTH_LONG).show();
						}
					}

					m_writingFileName = "";

					String curr = FileUtils
							.trimLastFileSeparator(m_curSambaDirectory);
					String add = FileUtils.trimLastFileSeparator(FileUtils
							.getParent(strPath));

					if (curr.equalsIgnoreCase(add)) {
						getSambaStorageListData();
					}

				}
				else if (intent.getAction().equals(
						SmbUtils.SMB_MSG_REFRESH_FILES)) {			
					getSambaStorageListData();
				}

			}
			// local download
			else {
				if (intent.getAction().equals(
						SmbUtils.SMB_MSG_DOWNLOAD_FILES_UPDATE)) {
					String strPath = intent
							.getStringExtra(SmbUtils.SMB_OPT_FILES_PATH);
					int nPos = intent.getIntExtra(
							SmbUtils.SMB_OPT_FILES_PROGRESS, 0);
					addDownloadingStatus(strPath, nPos);
					m_writingFileName = FileUtils.getFileName(strPath);
					updateListView();

				} else if (intent.getAction().equals(
						SmbUtils.SMB_MSG_DOWNLOAD_FILES_ERROR)
						|| intent.getAction().equals(
								SmbUtils.SMB_MSG_DOWNLOAD_FILES_FINISH)) {

					String strPath = intent
							.getStringExtra(SmbUtils.SMB_OPT_FILES_PATH);

					if (intent.getAction().equals(
							SmbUtils.SMB_MSG_DOWNLOAD_FILES_ERROR)) {
						if (strPath != null) {
							String err = LocalStorageActivity.this
									.getString(R.string.smb_download_file_failed);
							err = String.format(err,
									FileUtils.getFileName(strPath));
							Toast.makeText(LocalStorageActivity.this, err,
									Toast.LENGTH_LONG).show();
						}
					}

					m_writingFileName = "";
					String curr = FileUtils
							.trimLastFileSeparator(m_curDirectory
									.getAbsolutePath());
					String add = FileUtils.trimLastFileSeparator(FileUtils
							.getParent(strPath));
					if (curr.equalsIgnoreCase(add)) {
						getLocalStorageListData();
					}
				}

			}
		}
	}

	private void addDownloadingStatus(String strPath, int nPos) {

		boolean bFinded = false;

		for (FileItem item : m_storageListData) {
			if (item.path.equalsIgnoreCase(strPath)) {
				item.isDownloading = true;
				item.percent = nPos;
				bFinded = true;

			}

			if (item.isDir && strPath.indexOf(item.path) != -1) {
				item.isDownloading = true;
			}
		}

		if (!bFinded) {
			String curr = FileUtils.trimLastFileSeparator(m_curDirectory
					.getAbsolutePath());
			String add = FileUtils.trimLastFileSeparator(FileUtils
					.getParent(strPath));
			if (curr.equalsIgnoreCase(add)) {
				FileItem item = new FileItem();
				item.isDir = false;
				item.path = strPath;
				item.percent = nPos;
				item.name = FileUtils.getFileName(strPath);
				item.isDownloading = true;
				m_storageListData.add(item);
			}
		}

	}

	private void addUploadingStatus(String strPath, int nPos) {

		boolean bFinded = false;
		for (FileItem item : m_storageListData) {
			if (item.path.equalsIgnoreCase(strPath)) {
				item.isUploading = true;
				item.percent = nPos;
				bFinded = true;
			}

			if (item.isDir && strPath.indexOf(item.path) != -1) {
				item.isUploading = true;
			}
		}

		if (!bFinded) {
			String curr = FileUtils.trimLastFileSeparator(m_curSambaDirectory);
			String add = FileUtils.trimLastFileSeparator(FileUtils
					.getParent(strPath));
			if (curr.equalsIgnoreCase(add)) {
				FileItem item = new FileItem();
				item.isDir = false;
				item.path = strPath;
				item.percent = nPos;
				item.name = FileUtils.getFileName(strPath);
				item.isUploading = true;
				m_storageListData.add(item);
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m100_local_storage_view);
		m_bNeedBack = false;

		// get controls
		m_backBtn = (Button) this.findViewById(R.id.back_btn);
		m_backBtn.setOnClickListener(this);
		m_editBtn = (Button) this.findViewById(R.id.edit_btn);
		m_editBtn.setOnClickListener(this);
		m_title = (TextView) this.findViewById(R.id.title);
		m_noFileTV = (TextView) this.findViewById(R.id.no_file);
		m_rootDirImage = (ImageView) this.findViewById(R.id.single_directorys);
		m_rootDirImage.setOnClickListener(this);
		m_arrowImage1 = (ImageView) this.findViewById(R.id.directory_arrow1);
		m_arrowImage2 = (ImageView) this.findViewById(R.id.directory_arrow2);
		m_dirParentTv = (TextView) this.findViewById(R.id.directory_parent);
		m_dirParentTv.setOnClickListener(this);
		m_dirTv = (TextView) this.findViewById(R.id.directory);
		m_dirTv.setOnClickListener(this);
		m_newFolder = (TextView) this.findViewById(R.id.new_folder);
		m_newFolder.setOnClickListener(this);
		m_upload = (TextView) this.findViewById(R.id.upload);
		m_upload.setOnClickListener(this);
		m_searchBtn = (TextView) this.findViewById(R.id.search);
		m_searchBtn.setOnClickListener(this);

		m_localStorageListView = (ListView) this.findViewById(R.id.list_view);
		m_localStorageListView.setOnItemClickListener(this);
		LocalStorageListAdapter localStorageListAdapter = new LocalStorageListAdapter(
				this);
		m_localStorageListView.setAdapter(localStorageListAdapter);

		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.waiting_progress);
		m_progressWaiting.setVisibility(View.GONE);

		m_flag = this.getIntent().getStringExtra(
				LocalStorageActivity.FLAG_CURRENT_LOCATION);
		m_curSambaDirectory = this.getIntent().getStringExtra(
				LocalStorageActivity.CURRENT_DIRECTORY);
		
		initNewFolderDialog();

		initTitleBar();
		showUploadBtn();
		startSmbService();
		

		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {		
			getSambaStorageListData();
		}		
		
		registerFilesReceiver();
		
	}

	private void showUploadBtn() {
		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			m_upload.setVisibility(View.VISIBLE);
		} else {
			m_upload.setVisibility(View.GONE);
		}
	}

	private void initTitleBar() {
		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			String strTitle = "";
			int iamgeId = 0;
			if (FeatureVersionManager.getInstance().isSupportDevice(
					FeatureVersionManager.VERSION_DEVICE_M100) == true) {
				strTitle = this.getResources().getString(
						R.string.m100_storage_main_media_box);
				iamgeId = R.drawable.m100_media_box_white;
			} else {
				strTitle = this.getResources().getString(
						R.string.m100_storage_main_hard_disc);
				iamgeId = R.drawable.m100_hard_disc_white;
			}
			m_title.setText(strTitle);
			m_rootDirImage.setImageResource(iamgeId);
		}
	}

	private void startSmbService() {
		Intent intent = new Intent(this, SmbService.class);
		startService(intent);
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		super.onBroadcastReceive(context, intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			StorageMonitor.registerReceiver(this);			
		} else {
			getLocalStorageListData();
		}
	}

	@Override
	public void onPause() {
		super.onPause();	
		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			StorageMonitor.unregisterReceiver(this);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(m_fileReceiver);		
		Intent intent = new Intent(this, SmbService.class);
		stopService(intent);	
	
	}
	
	
	private void registerFilesReceiver() {

		// upload
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_UPLOAD_FILES_FINISH));
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_UPLOAD_FILES_ERROR));
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_UPLOAD_FILES_UPDATE));

		// download
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_DOWNLOAD_FILES_FINISH));
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_DOWNLOAD_FILES_ERROR));
		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_DOWNLOAD_FILES_UPDATE));

		// refresh

		this.registerReceiver(m_fileReceiver, new IntentFilter(
				SmbUtils.SMB_MSG_REFRESH_FILES));
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.single_directorys:
			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				m_curSambaDirectory = BusinessMannager.getInstance()
						.getSambaSettings().AccessPath;

				m_curSambaDirectory = FileUtils
						.addLastFileSeparator(m_curSambaDirectory);
				getSambaStorageListData();
			} else {
				m_curDirectory = Environment.getExternalStorageDirectory();
				getLocalStorageListData();
			}

			break;
		case R.id.directory_parent:

			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				m_curSambaDirectory = m_parentSmabaDirectory;
				getSambaStorageListData();
			} else {
				m_curDirectory = m_curDirectory.getParentFile();
				getLocalStorageListData();
			}
			break;

		case R.id.directory:

			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				getSambaStorageListData();
			} else {
				getLocalStorageListData();
			}
			break;
		case R.id.new_folder:
			newFolderClick();
			break;
		case R.id.search:
			searchBtnClick();
			break;
		case R.id.upload:
			uploadBtnClick();
			break;
		case R.id.edit_btn:
			editBtnClick();
			break;
		}
	}

	private void uploadBtnClick() {
		Intent intent = new Intent(this, StorageUploadActivity.class);
		intent.putExtra(StorageUploadActivity.UPLOAD_DIRECTORY,
				m_curSambaDirectory);
		this.startActivity(intent);
	}

	private void editBtnClick() {

		Intent intent = new Intent(this, StorageEditActivity.class);
		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			intent.putExtra(FLAG_CURRENT_LOCATION, FLAG_SAMBA);
			intent.putExtra(CURRENT_DIRECTORY, m_curSambaDirectory);
		} else {
			intent.putExtra(FLAG_CURRENT_LOCATION, FLAG_LOCAL);
			if (m_curDirectory != null)
				intent.putExtra(CURRENT_DIRECTORY, m_curDirectory.getPath());
		}

		this.startActivity(intent);
	}

	private void searchBtnClick() {
		Intent intent = new Intent(this, StorageSearchActivity.class);
		if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
			intent.putExtra(FLAG_CURRENT_LOCATION, FLAG_SAMBA);
			intent.putExtra(CURRENT_DIRECTORY, m_curSambaDirectory);
		} else {
			intent.putExtra(FLAG_CURRENT_LOCATION, FLAG_LOCAL);
			if (m_curDirectory != null)
				intent.putExtra(CURRENT_DIRECTORY, m_curDirectory.getPath());
		}
		intent.putExtra(StorageSearchActivity.ACTIVITY_FROM,
				StorageSearchActivity.ACTIVITY_MAIN);
		this.startActivity(intent);

	}
	
	private void initNewFolderDialog()
	{
		m_newFolderDlg = new InputDialog1(this);
		m_newFolderDlg.m_titleTextView.setText(R.string.new_folder_dialog_title);
		m_newFolderDlg.m_promptView.setText(R.string.new_folder_prompt);
		m_newFolderDlg.m_okBtn.setText(R.string.ok);
		m_newFolderDlg.m_okBtn.setEnabled(false);
		m_newFolderDlg.m_inputEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {				
			
				String editable = m_newFolderDlg.m_inputEdit.getText().toString();

				boolean isInvalide = FileUtils.isInvalidFileName(editable);
				if (editable != null && editable.length() != 0 && editable.getBytes().length <= FileUtils.FILENAME_MAX_LENGTH) {
					if (isInvalide) {
						String strErr = LocalStorageActivity.this
								.getString(R.string.file_name_error);
						Toast.makeText(LocalStorageActivity.this, strErr,
								Toast.LENGTH_SHORT).show();
						m_newFolderDlg.m_okBtn.setEnabled(false);
					} else {
						m_newFolderDlg.m_okBtn.setEnabled(true);
					}
				} else {
					m_newFolderDlg.m_okBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void newFolderClick() {
		m_newFolderDlg.m_inputEdit.setText("");
		m_newFolderDlg.showDialog(new OnBtnClick() {
			@Override
			public void onBtnClick() {

				if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
					createSambaFolder(m_newFolderDlg);
				} else {
					createLocalFolder(m_newFolderDlg);
				}
			}
		});
	}

	private void createLocalFolder(InputDialog1 dlg) {
		String editable = dlg.m_inputEdit.getText().toString();	
		
		File file = new File(m_curDirectory.getAbsoluteFile() + "/" + editable);
		if (file.exists()) {
			String msgRes = LocalStorageActivity.this
					.getString(R.string.directory_is_exist);
			Toast.makeText(LocalStorageActivity.this, msgRes,
					Toast.LENGTH_SHORT).show();
		} else {
			if (file.mkdirs() == true) {
				dlg.closeDialog();
				getLocalStorageListData();
			} else {
				String msgRes = LocalStorageActivity.this
						.getString(R.string.new_folder_failed);
				Toast.makeText(LocalStorageActivity.this, msgRes,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void createSambaFolder(InputDialog1 dlg) {
		String editable = dlg.m_inputEdit.getText().toString();
		String newFolder = FileUtils.combinePath(m_curSambaDirectory, editable);

		SmbNewFolderTask task = new SmbNewFolderTask(newFolder,
				m_smbNewFolderTaskHandler);
		task.start();
		dlg.closeDialog();
	}

	/*
	 * private class SortList implements Comparator { public int compare(Object
	 * o1, Object o2) { File c1 = (File) o1; File c2 = (File) o2;
	 * if(c1.isDirectory() == true && c2.isDirectory() == true) { return
	 * c1.getName().compareToIgnoreCase(c2.getName()); }
	 * 
	 * if(c1.isDirectory() == true && c2.isDirectory() == false) { return -1; }
	 * 
	 * if(c1.isDirectory() == false && c2.isDirectory() == true) { return 1; }
	 * 
	 * if(c1.isDirectory() == false && c2.isDirectory() == false) { return
	 * c1.getName().compareToIgnoreCase(c2.getName()); }
	 * 
	 * return -1; } }
	 */

	private void showDirectoryLayout() {
		String strRoot = Environment.getExternalStorageDirectory().getPath();
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if (m_curDirectory.getPath().equalsIgnoreCase(strRoot))
			bIsRoot = true;
		if (m_curDirectory.getParent().equalsIgnoreCase(strRoot))
			bIsParentRoot = true;
		
		if (bIsRoot == true) {
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.GONE);
			m_dirTv.setVisibility(View.GONE);		
		} else if (bIsParentRoot == true) {
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_dirParentTv.setVisibility(View.GONE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirTv.setText(m_curDirectory.getName());
		} else {
			m_rootDirImage.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirParentTv.setText(m_curDirectory.getParentFile().getName());
			m_dirTv.setText(m_curDirectory.getName());
		}
	}

	private void getLocalStorageListData() {
		//m_storageListData.clear();
		if (m_curDirectory == null || m_curDirectory.exists() == false) {
			m_curDirectory = Environment.getExternalStorageDirectory();
		}

		m_storageListData = FileUtils.listFiles(m_curDirectory);
		showDirectoryLayout();
		updateListView();
	}

	private void showSambaDirectoryLayout() {
		String strRoot = BusinessMannager.getInstance().getSambaSettings().AccessPath;
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curSambaDirectory = FileUtils.addLastFileSeparator(m_curSambaDirectory);
		m_parentSmabaDirectory = FileUtils.addLastFileSeparator(m_parentSmabaDirectory);
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if (m_curSambaDirectory.equalsIgnoreCase(strRoot))
			bIsRoot = true;

		if (m_parentSmabaDirectory.equalsIgnoreCase(strRoot))
			bIsParentRoot = true;
		
		enableToolBtns(true);

		if (bIsRoot == true) {
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.GONE);
			m_dirTv.setVisibility(View.GONE);
			if (!FeatureVersionManager.getInstance().isSupportDevice(
					FeatureVersionManager.VERSION_DEVICE_M100)) {
				enableToolBtns(false);
			}
		} else if (bIsParentRoot == true) {
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_dirParentTv.setVisibility(View.GONE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirTv.setText(FileUtils.getFileName(m_curSambaDirectory));
		} else {
			m_rootDirImage.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirParentTv
					.setText(FileUtils.getFileName(m_parentSmabaDirectory));
			m_dirTv.setText(FileUtils.getFileName(m_curSambaDirectory));
		}
	}	
	
	private void enableToolBtns(boolean bStatus)
	{
		m_newFolder.setEnabled(bStatus);
		m_editBtn.setEnabled(bStatus);		
		m_upload.setEnabled(bStatus);		
	}

	private void getSambaStorageListData() {	
		HttpAccessLog.getInstance().writeLogToFile("LocalStorageActivity getSambaStorageListData path: "+ m_curSambaDirectory);	
		String strRoot = BusinessMannager.getInstance().getSambaSettings().AccessPath;
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curSambaDirectory = FileUtils.addLastFileSeparator(m_curSambaDirectory);
		
		if(m_curSambaDirectory.indexOf(strRoot) >= 0)
		{
			
		}
		else
		{
			m_curSambaDirectory = strRoot;
		}
		
//		if(BusinessMannager.getInstance().getSambaServiceState() == ServiceState.Disabled && 
//			FeatureVersionManager.getInstance().isSupportDevice(FeatureVersionManager.VERSION_DEVICE_M100) == false)	
//		{
//			Intent it = new Intent(this, StorageMainActivity.class);
//			this.startActivity(it);
//		}
//		else{
//			
//			m_progressWaiting.setVisibility(View.VISIBLE);
//			SmbListFilesTask task = new SmbListFilesTask(m_curSambaDirectory,
//					m_storageListData, LIST_FILE_MODEL.ALL_FILE,
//					m_smbListFilesTaskHandler);
//			task.start();				
//			m_localStorageListView.setEnabled(false);	
//			
//		}
		
	}

	private void updateListView() {
		if (m_storageListData.size() > 0) {
			m_noFileTV.setVisibility(View.GONE);
			m_localStorageListView.setVisibility(View.VISIBLE);
		} else {
			m_noFileTV.setVisibility(View.VISIBLE);
			m_localStorageListView.setVisibility(View.GONE);
		}

		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_storageListData);

		((LocalStorageListAdapter) m_localStorageListView.getAdapter())
				.notifyDataSetChanged();
	}

	private class LocalStorageListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public LocalStorageListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return m_storageListData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public final class ViewHolder {
			public RelativeLayout itemNormal;
			public RelativeLayout itemFolderLoading;
			public RelativeLayout itemFileLoading;
			public TextView itemNormalName;
			public TextView itemFolderLoadingName;
			public TextView itemFileLoadingName;
			public TextView itemPercent;
			public TextView itemFolderLoadingPrompt;
			public ImageView itemNormalImage;
			public ImageView itemFolderLoadingImage;
			public ImageView itemFileLoadingImage;
			public ProgressBar itemLoadingProgress;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.m100_local_storage_list_adapter, null);
				holder.itemNormal = (RelativeLayout) convertView
						.findViewById(R.id.item_normal);
				holder.itemFolderLoading = (RelativeLayout) convertView
						.findViewById(R.id.item_folder_downloading);
				holder.itemFileLoading = (RelativeLayout) convertView
						.findViewById(R.id.item_file_downloading);
				holder.itemNormalName = (TextView) convertView
						.findViewById(R.id.normal_text);
				holder.itemFolderLoadingName = (TextView) convertView
						.findViewById(R.id.item_folder_downloading_text);
				holder.itemFolderLoadingPrompt = (TextView) convertView
						.findViewById(R.id.item_folder_downloading_tip);
				holder.itemFileLoadingName = (TextView) convertView
						.findViewById(R.id.item_file_downloading_text);
				holder.itemPercent = (TextView) convertView
						.findViewById(R.id.item_file_downloading_percent);
				holder.itemNormalImage = (ImageView) convertView
						.findViewById(R.id.normal_icon);
				holder.itemFolderLoadingImage = (ImageView) convertView
						.findViewById(R.id.item_folder_downloading_icon);
				holder.itemFileLoadingImage = (ImageView) convertView
						.findViewById(R.id.item_file_downloading_icon);
				holder.itemLoadingProgress = (ProgressBar) convertView
						.findViewById(R.id.item_file_downloading_progress);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FileItem item = m_storageListData.get(position);

			String strName = item.name;
			boolean bIsFolder = item.isDir;
			boolean bIsDownLoading = item.isDownloading;
			boolean bIsUploading = item.isUploading;

			if (bIsUploading) {
				if (bIsFolder) {
					holder.itemNormal.setVisibility(View.GONE);
					holder.itemFolderLoading.setVisibility(View.VISIBLE);
					holder.itemFileLoading.setVisibility(View.GONE);
					holder.itemFolderLoadingName.setText(strName);
					String strFileName = LocalStorageActivity.this
							.getResources().getString(
									R.string.m100_uploading_prompt);
					strFileName = String.format(strFileName, m_writingFileName);
					holder.itemFolderLoadingPrompt.setText(strFileName);
					holder.itemFolderLoadingImage
							.setBackgroundResource(R.drawable.m100_item_uploading_folder);
				} else {
					holder.itemNormal.setVisibility(View.GONE);
					holder.itemFolderLoading.setVisibility(View.GONE);
					holder.itemFileLoading.setVisibility(View.VISIBLE);
					holder.itemFileLoadingImage
							.setBackgroundResource(R.drawable.m100_item_uploading_file);
					holder.itemLoadingProgress.setProgress(item.percent);
					String strFileName = LocalStorageActivity.this
							.getResources().getString(
									R.string.m100_uploading_prompt);
					strFileName = String.format(strFileName, strName);
					holder.itemFileLoadingName.setText(strFileName);
					String strPecent = LocalStorageActivity.this.getResources()
							.getString(R.string.m100_loading_pecent);
					strPecent = String.format(strPecent, item.percent);
					holder.itemPercent.setText(strPecent);
				}
			} else if (bIsDownLoading) {
				if (bIsFolder) {
					holder.itemNormal.setVisibility(View.GONE);
					holder.itemFolderLoading.setVisibility(View.VISIBLE);
					holder.itemFileLoading.setVisibility(View.GONE);
					holder.itemFolderLoadingName.setText(strName);
					String strFileName = LocalStorageActivity.this
							.getResources().getString(
									R.string.m100_downloading_prompt);
					strFileName = String.format(strFileName, m_writingFileName);
					holder.itemFolderLoadingPrompt.setText(strFileName);
					holder.itemFolderLoadingImage
							.setBackgroundResource(R.drawable.m100_item_downloading_folder);
				} else {
					holder.itemNormal.setVisibility(View.GONE);
					holder.itemFolderLoading.setVisibility(View.GONE);
					holder.itemFileLoading.setVisibility(View.VISIBLE);
					holder.itemFileLoadingImage
							.setBackgroundResource(R.drawable.m100_item_downloading_file);
					holder.itemLoadingProgress.setProgress(item.percent);
					String strFileName = LocalStorageActivity.this
							.getResources().getString(
									R.string.m100_downloading_prompt);
					strFileName = String.format(strFileName, strName);
					holder.itemFileLoadingName.setText(strFileName);
					String strPecent = LocalStorageActivity.this.getResources()
							.getString(R.string.m100_loading_pecent);
					strPecent = String.format(strPecent, item.percent);
					holder.itemPercent.setText(strPecent);
				}
			} else {
				holder.itemNormal.setVisibility(View.VISIBLE);
				holder.itemFolderLoading.setVisibility(View.GONE);
				holder.itemFileLoading.setVisibility(View.GONE);
				holder.itemNormalName.setText(strName);
				if (bIsFolder == true) {
					holder.itemNormalImage
							.setBackgroundResource(R.drawable.m100_item_folder);
				} else {
					// to do
					FileType fileType = FileModel.getFileType(strName);
					if (fileType == FileType.Audio) {
						holder.itemNormalImage
								.setBackgroundResource(R.drawable.m100_item_music);
					} else if (fileType == FileType.Image) {
						holder.itemNormalImage
								.setBackgroundResource(R.drawable.m100_item_image);
					} else if (fileType == FileType.Text) {
						holder.itemNormalImage
								.setBackgroundResource(R.drawable.m100_item_doc);
					} else if (fileType == FileType.Video) {
						holder.itemNormalImage
								.setBackgroundResource(R.drawable.m100_item_video);
					} else {
						holder.itemNormalImage
								.setBackgroundResource(R.drawable.m100_item_unknown_file);
					}
				}
			}

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		FileItem item = m_storageListData.get(position);
		if (item.isDir == true) {

			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				m_curSambaDirectory = item.path;
				getSambaStorageListData();
			} else {
				String strPath = item.path;
				File file = new File(strPath);
				if (file.exists()) {
					m_curDirectory = file;
				}
				getLocalStorageListData();
			}
		}

		else {
			if (m_flag.equalsIgnoreCase(FLAG_SAMBA)) {
				if (!item.isUploading) {
					SmbUtils.openFile(this, item.path);
				}
				else
				{					
					cancelUploading(item);
				}
			} else {
				if (!item.isDownloading) {
					FileUtils.openFile(this, item.path);
				}
				else
				{
					cancelDownloading(item);
				}
			}
		}
	}
	
	public static void uploadFiles(ArrayList<FileItem> files, String smbDir)
	{
		m_uploadTask = new SmbUploadFilesTask(files, smbDir, SmartLinkV3App.getInstance().getApplicationContext());
		m_uploadTask.start();		
	}
	
	public static void downloadFiles(ArrayList<FileItem> files)
	{
		m_downloadTask = new SmbDownloadFilesTask(files, SmartLinkV3App.getInstance().getApplicationContext());
		m_downloadTask.start();	
	}
	
	
	private void cancelUploading(final FileItem item) {
		final InquireDialog inquireDlg = new InquireDialog(this);
		
		String content = this.getString(R.string.content_cancel_uploading);				
		content = String.format(content, item.name);		
		inquireDlg.m_titleTextView.setText(R.string.title_cancel_uploading);		
		inquireDlg.m_contentTextView.setText(content);
		inquireDlg.m_contentDescriptionTextView.setText("");		
		inquireDlg.m_confirmBtn.setBackgroundResource(R.drawable.selector_common_button);
		inquireDlg.m_confirmBtn.setText(R.string.btn_cancel_uploading);
		inquireDlg.showDialog(new OnInquireApply() {
			@Override
			public void onInquireApply() {	
				if(m_uploadTask != null && m_uploadTask.isAlive())
				{
					m_uploadTask.setCancel(true, item.path);
				}
				inquireDlg.closeDialog();
			}	
		});
	}
	
	private void cancelDownloading(final FileItem item) {
		final InquireDialog inquireDlg = new InquireDialog(this);
		
		String content = this.getString(R.string.content_cancel_downloading);				
		content = String.format(content, item.name);	
		inquireDlg.m_titleTextView.setText(R.string.title_cancel_downloading);		
		inquireDlg.m_contentTextView.setText(content);
		inquireDlg.m_contentDescriptionTextView.setText("");		
		inquireDlg.m_confirmBtn.setBackgroundResource(R.drawable.selector_common_button);
		inquireDlg.m_confirmBtn.setText(R.string.btn_cancel_downloading);
		inquireDlg.showDialog(new OnInquireApply() {
			@Override
			public void onInquireApply() {	
				if(m_downloadTask != null && m_downloadTask.isAlive())
				{
					m_downloadTask.setCancel(true, item.path);
				}	
				inquireDlg.closeDialog();
			}	
		});
	}
}
