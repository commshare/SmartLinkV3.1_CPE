package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileModel;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.common.file.FileModel.FileType;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbDeleteFilesTask;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask;
import com.alcatel.smartlinkv3.samba.SmbUtils;
import com.alcatel.smartlinkv3.samba.SmbDeleteFilesTask.DeleteResult;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask.ListFilesResult;
import com.alcatel.smartlinkv3.samba.SmbRenameFileTask;
import com.alcatel.smartlinkv3.samba.SmbUtils.LIST_FILE_MODEL;
import com.alcatel.smartlinkv3.ui.activity.InquireDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.dialog.DialogAutoDismiss;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1.OnBtnClick;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StorageEditActivity extends BaseActivity implements
		OnClickListener {
	private String m_strCurLocation = "";
	private String m_curDirectory = null;

	private TextView m_doneBtn = null;
	private TextView m_title = null;
	private ImageView m_rootDirImage = null;
	private ImageView m_arrowImage1 = null;
	private ImageView m_arrowImage2 = null;
	private TextView m_dirParentTv = null;
	private TextView m_dirTv = null;

	private TextView m_downloadBtn = null;
	private TextView m_moveBtn = null;
	private TextView m_renameBtn = null;
	private TextView m_deleteBtn = null;
	private CheckBox m_selectAllCb = null;

	private ListView m_editStorageListView = null;
	private ArrayList<FileItem> m_editStorageListData = new ArrayList<FileItem>();
	private String m_parentSmabaDirectory;

	private static final int MSG_DELETE_FILES_DONE = 100;

	public static String EDIT_SELECT_FILES = "com.alcatel.smartlink.ui.activity.selectedFiles";

	private ProgressBar m_progressWaiting = null;
	private ProgressDialog m_progress_dialog = null;

	private InputDialog1 m_renameFolderDlg;
	// samba list files
	private Handler m_smbListFilesTaskHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
				ListFilesResult res = (ListFilesResult) msg.obj;
				m_progressWaiting.setVisibility(View.GONE);
				m_parentSmabaDirectory = res.mParentPath;
				m_editStorageListData.clear();
				m_editStorageListData = (ArrayList<FileItem>) res.mFiles
						.clone();
				showSambaDirectoryLayout();
				updateListView();
				showToolbarBtn();
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:
				m_progressWaiting.setVisibility(View.GONE);
				String strErr = StorageEditActivity.this
						.getString(R.string.list_samba_files_failed);
				Toast.makeText(StorageEditActivity.this, strErr,
						Toast.LENGTH_LONG).show();
				break;

			}
		}
	};

	// samba rename file
	private Handler m_smbRenameFileTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
				getSambaEditStorageListData();
				showToolbarBtn();
				StorageEditActivity.this.finish();
				Intent intent = new Intent(SmbUtils.SMB_MSG_REFRESH_FILES);
				StorageEditActivity.this.sendBroadcast(intent);
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:
				String msgError = StorageEditActivity.this
						.getString(R.string.rename_failed);
				Toast.makeText(StorageEditActivity.this, msgError,
						Toast.LENGTH_SHORT).show();
				StorageEditActivity.this.finish();
				break;

			case SmbUtils.SMB_MSG_FILE_EXISTS:
				String msgExists = StorageEditActivity.this
						.getString(R.string.rename_is_exist);
				Toast.makeText(StorageEditActivity.this, msgExists,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	// smaba delete files
	private Handler m_smbDeleteFilesTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
				if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
					m_progress_dialog.dismiss();
				}
				StorageEditActivity.this.finish();
				DeleteResult res = (DeleteResult) msg.obj;
				if (res.failed != 0) {
					String msgRes = new String();
					if (res.succeeded + res.failed == 1) {
						msgRes = StorageEditActivity.this
								.getString(R.string.delete_file_fail1);
					} else {
						String str = StorageEditActivity.this
								.getString(R.string.delete_file_fail2);
						msgRes = String.format(str, res.succeeded, res.failed);
					}
					Toast.makeText(StorageEditActivity.this, msgRes,
							Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(SmbUtils.SMB_MSG_REFRESH_FILES);
				StorageEditActivity.this.sendBroadcast(intent);

				break;
			}
		}
	};

	private Handler m_localDeleteFilesTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DELETE_FILES_DONE:
				if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
					m_progress_dialog.dismiss();
				}

				LocalDeleteResult res = (LocalDeleteResult) msg.obj;

				if (res.failed != 0) {
					String msgRes = new String();
					if (res.succeeded + res.failed == 1) {
						msgRes = StorageEditActivity.this
								.getString(R.string.delete_file_fail1);
					} else {
						String str = StorageEditActivity.this
								.getString(R.string.delete_file_fail2);
						msgRes = String.format(str, res.succeeded, res.failed);
					}
					Toast.makeText(StorageEditActivity.this, msgRes,
							Toast.LENGTH_SHORT).show();
				}
				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					getLocalEditStorageListData();
				}
				showToolbarBtn();
				StorageEditActivity.this.finish();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_eidt_view);
		m_bNeedBack = false;

		m_strCurLocation = this.getIntent().getStringExtra(
				LocalStorageActivity.FLAG_CURRENT_LOCATION);
		m_curDirectory = this.getIntent().getStringExtra(
				LocalStorageActivity.CURRENT_DIRECTORY);

		// get controls
		m_doneBtn = (TextView) this.findViewById(R.id.done_btn);
		m_doneBtn.setOnClickListener(this);
		m_title = (TextView) this.findViewById(R.id.title);
		m_rootDirImage = (ImageView) this.findViewById(R.id.root_directory);
		m_arrowImage1 = (ImageView) this.findViewById(R.id.directory_arrow1);
		m_arrowImage2 = (ImageView) this.findViewById(R.id.directory_arrow2);
		m_dirParentTv = (TextView) this.findViewById(R.id.directory_parent);
		m_dirTv = (TextView) this.findViewById(R.id.directory);

		m_downloadBtn = (TextView) this.findViewById(R.id.download);
		m_moveBtn = (TextView) this.findViewById(R.id.move);
		m_renameBtn = (TextView) this.findViewById(R.id.rename);
		m_deleteBtn = (TextView) this.findViewById(R.id.delete);
		m_selectAllCb = (CheckBox) this.findViewById(R.id.select_all);
		m_selectAllCb.setOnClickListener(this);
		m_downloadBtn.setOnClickListener(this);
		m_moveBtn.setOnClickListener(this);
		m_renameBtn.setOnClickListener(this);
		m_deleteBtn.setOnClickListener(this);

		m_editStorageListView = (ListView) this.findViewById(R.id.list_view);
		EditStorageListAdapter editStorageListAdapter = new EditStorageListAdapter(
				this);
		m_editStorageListView.setAdapter(editStorageListAdapter);

		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.waiting_progress);
		m_progressWaiting.setVisibility(View.GONE);

		initTitleBar();

		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			getLocalEditStorageListData();
		} else {
			getSambaEditStorageListData();
		}

		showToolbarBtn();

		m_renameFolderDlg = new InputDialog1(this);
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
		showToolbarBtn();

		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {
			StorageMonitor.registerReceiver(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {
			StorageMonitor.unregisterReceiver(this);
		}

		if (m_renameFolderDlg != null) {
			m_renameFolderDlg.closeDialog();
		}
	}
	
	private void initTitleBar() {
		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {			
			String strTitle = this.getResources().getString(
						R.string.storage_main_hard_disc);
			int	iamgeId = R.drawable.hard_disc_grey;			
			m_title.setText(strTitle);
			m_rootDirImage.setImageResource(iamgeId);
		}
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {

		case R.id.done_btn:
			this.finish();
			break;
		case R.id.select_all:
			onSeleckAllClick();
			break;
		case R.id.rename:
			onRenameClick();
			break;
		case R.id.delete:
			onDeleteClick();
			break;

		case R.id.download:
			onDownLoadClick();
			break;
		case R.id.move:
			onMoveClick();
			break;
		}
	}

	private void onMoveClick() {
		Intent intent = new Intent(this, StorageMoveToActivity.class);
		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			intent.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
					LocalStorageActivity.FLAG_LOCAL);
		} else {
			intent.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
					LocalStorageActivity.FLAG_SAMBA);
		}

		ArrayList<String> selectFile = new ArrayList<String>();
		for (int i = 0; i < m_editStorageListData.size(); i++) {
			boolean bChecked = m_editStorageListData.get(i).isSelected;
			if (bChecked == true) {
				selectFile.add(m_editStorageListData.get(i).path);
			}
		}

		intent.putStringArrayListExtra(EDIT_SELECT_FILES, selectFile);

		this.startActivity(intent);
		this.finish();
	}

	private void onDownLoadClick() {
		DialogAutoDismiss dialog = new DialogAutoDismiss(
				this.getApplicationContext());
		dialog.m_contentTextView.setText(R.string.download_files_tip);
		dialog.showDialog(5000);

		ArrayList<FileItem> downloadFiles = new ArrayList<FileItem>();
		for (int i = 0; i < m_editStorageListData.size(); i++) {
			boolean bChecked = m_editStorageListData.get(i).isSelected;
			if (bChecked) {
				downloadFiles.add(m_editStorageListData.get(i));
			}
		}
		LocalStorageActivity.downloadFiles(downloadFiles);
		this.finish();
		LocalStorageActivity.m_curDirectory = new File(CPEConfig.getInstance()
				.getDefaultDir());
	}

	private void onDeleteClick() {
		final InquireDialog inquireDlg = new InquireDialog(this);
		inquireDlg.m_titleTextView.setText(R.string.delete_file_title);
		inquireDlg.m_contentTextView.setText(R.string.delete_file_content);
		inquireDlg.m_contentDescriptionTextView.setVisibility(View.GONE);
		inquireDlg.m_confirmBtn.setText(R.string.yes);
		inquireDlg.showDialog(new OnInquireApply() {
			@Override
			public void onInquireApply() {

				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					deleteLocalFiles(inquireDlg);
				} else {
					deleteSambaFiles(inquireDlg);
				}

			}
		});
	}

	public class LocalDeleteResult {

		public int succeeded;
		public int failed;

		public LocalDeleteResult() {
			succeeded = 0;
			failed = 0;
		}
	}

	public class LocalDeleteFilesTask extends Thread {

		private Handler mHandler;
		private LocalDeleteResult mRes;

		public LocalDeleteFilesTask(Handler handler) {
			mHandler = handler;
			mRes = new LocalDeleteResult();
		}

		@Override
		public void run() {
			for (int i = 0; i < m_editStorageListData.size(); i++) {
				boolean bChecked = m_editStorageListData.get(i).isSelected;
				if (bChecked == true) {
					String strFileName = m_editStorageListData.get(i).name;
					File srcFile = new File(m_curDirectory + "/" + strFileName);
					if (deleteFile(srcFile) == true)
						mRes.succeeded++;
					else
						mRes.failed++;
				}
			}
			mHandler.obtainMessage(MSG_DELETE_FILES_DONE, mRes).sendToTarget();
		}
	}

	private void deleteLocalFiles(InquireDialog dlg) {
		LocalDeleteFilesTask task = new LocalDeleteFilesTask(
				m_localDeleteFilesTaskHandler);
		task.start();
		dlg.closeDialog();
		m_progress_dialog = ProgressDialog.show(this, null,
				this.getString(R.string.deleting), true, false);
	}

	private void deleteSambaFiles(InquireDialog dlg) {
		SmbDeleteFilesTask task = new SmbDeleteFilesTask(m_editStorageListData,
				m_smbDeleteFilesTaskHandler);
		task.start();
		dlg.closeDialog();
		m_progress_dialog = ProgressDialog.show(this, null,
				this.getString(R.string.deleting), true, false);
	}

	private boolean deleteFile(File file) {
		if (file.isFile()) {
			return file.delete();
		} else {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return file.delete();
			} else {
				for (int i = 0; i < childFiles.length; i++) {
					deleteFile(childFiles[i]);
				}
				return file.delete();
			}
		}
	}

	private FileItem getFirstSelectedFileItem() {
		FileItem item = new FileItem();
		for (int i = 0; i < m_editStorageListData.size(); i++) {
			boolean bChecked = m_editStorageListData.get(i).isSelected;
			if (bChecked) {
				item = m_editStorageListData.get(i);
				break;
			}
		}
		return item;
	}

	private void onRenameClick() {
		FileItem selectedItem = getFirstSelectedFileItem();
		m_renameFolderDlg.m_titleTextView.setText(R.string.rename_dialog_title);
		m_renameFolderDlg.m_promptView.setText(R.string.rename_prompt);
		m_renameFolderDlg.m_okBtn.setText(R.string.ok);
		m_renameFolderDlg.m_okBtn.setEnabled(false);
		m_renameFolderDlg.m_inputEdit.setText(selectedItem.name);

		int nIndex = selectedItem.name.length();
		if (!selectedItem.isDir && selectedItem.name.lastIndexOf(".") != -1) {
			nIndex = selectedItem.name.lastIndexOf(".");
		}
		m_renameFolderDlg.m_inputEdit.setSelection(nIndex);
		m_renameFolderDlg.m_inputEdit.setFocusable(true);
		m_renameFolderDlg.m_inputEdit.setFocusableInTouchMode(true);
		m_renameFolderDlg.m_inputEdit.requestFocus();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(m_renameFolderDlg.m_inputEdit, 0);
			}
		}, 300);

		m_renameFolderDlg.m_inputEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String editable = m_renameFolderDlg.m_inputEdit.getText()
						.toString();
				boolean isInvalide = FileUtils.isInvalidFileName(editable);
				if (editable != null
						&& editable.length() != 0
						&& editable.getBytes().length <= FileUtils.FILENAME_MAX_LENGTH) {
					if (isInvalide) {
						String strErr = StorageEditActivity.this
								.getString(R.string.file_name_error);
						Toast.makeText(StorageEditActivity.this, strErr,
								Toast.LENGTH_SHORT).show();
						m_renameFolderDlg.m_okBtn.setEnabled(false);
					} else if (editable.indexOf(" ") == 0) {
						String strErr = StorageEditActivity.this
								.getString(R.string.file_name_start_with_space);
						Toast.makeText(StorageEditActivity.this, strErr,
								Toast.LENGTH_SHORT).show();
						m_renameFolderDlg.m_okBtn.setEnabled(false);
					} else {
						m_renameFolderDlg.m_okBtn.setEnabled(true);
					}
				} else {
					m_renameFolderDlg.m_okBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		m_renameFolderDlg.showDialog(new OnBtnClick() {
			@Override
			public void onBtnClick() {
				String strPath = getFirstSelectedFileItem().path;
				String dstName = m_renameFolderDlg.m_inputEdit.getText()
						.toString();

				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					renameLocalFile(strPath, dstName, m_renameFolderDlg);
				} else {
					renameSambaFile(strPath, dstName, m_renameFolderDlg);
				}
			}
		});
	}

	private void renameLocalFile(String srcPath, String dstName,
			InputDialog1 renameFolderDlg) {
		File srcFile = new File(srcPath);
		File file = new File(m_curDirectory + "/" + dstName);
		if (file.exists()) {
			String msgRes = StorageEditActivity.this
					.getString(R.string.rename_is_exist);
			Toast.makeText(StorageEditActivity.this, msgRes, Toast.LENGTH_SHORT)
					.show();
		} else {
			if (srcFile.renameTo(file) == true) {
				renameFolderDlg.closeDialog();
				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					getLocalEditStorageListData();
				}
				showToolbarBtn();
				this.finish();
			} else {
				String msgRes = StorageEditActivity.this
						.getString(R.string.rename_failed);
				Toast.makeText(StorageEditActivity.this, msgRes,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void renameSambaFile(String srcPath, String dstName,
			InputDialog1 dlg) {
		SmbRenameFileTask task = new SmbRenameFileTask(srcPath, dstName,
				m_smbRenameFileTaskHandler);
		task.start();
		dlg.closeDialog();
	}

	private void onSeleckAllClick() {
		boolean bChecked = m_selectAllCb.isChecked();
		for (int i = 0; i < m_editStorageListData.size(); i++) {
			m_editStorageListData.get(i).isSelected = bChecked;
		}
		((EditStorageListAdapter) m_editStorageListView.getAdapter())
				.notifyDataSetChanged();
		showToolbarBtn();
	}

	private void showToolbarBtn() {
		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			m_downloadBtn.setVisibility(View.GONE);
		} else {
			m_downloadBtn.setVisibility(View.VISIBLE);
		}

		if (m_editStorageListData.size() == 0) {
			m_selectAllCb.setEnabled(false);
		} else {
			m_selectAllCb.setEnabled(true);
		}
		int nCheckCount = 0;
		for (int i = 0; i < m_editStorageListData.size(); i++) {
			boolean bChecked = m_editStorageListData.get(i).isSelected;
			if (bChecked)
				nCheckCount++;
		}
		if (nCheckCount == m_editStorageListData.size()) {
			m_selectAllCb.setChecked(true);
		} else {
			m_selectAllCb.setChecked(false);
		}

		if (nCheckCount == 0) {
			m_downloadBtn.setEnabled(false);
			m_moveBtn.setEnabled(false);
			m_renameBtn.setEnabled(false);
			m_deleteBtn.setEnabled(false);
		} else if (nCheckCount == 1) {
			m_downloadBtn.setEnabled(true);
			m_moveBtn.setEnabled(true);
			m_renameBtn.setEnabled(true);
			m_deleteBtn.setEnabled(true);
		} else {
			m_downloadBtn.setEnabled(true);
			m_moveBtn.setEnabled(true);
			m_renameBtn.setEnabled(false);
			m_deleteBtn.setEnabled(true);
		}
	}

	private void showDirectoryLayout() {
		String strRoot = Environment.getExternalStorageDirectory().getPath();
		File curFile = new File(m_curDirectory);
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if (curFile.getPath().equalsIgnoreCase(strRoot))
			bIsRoot = true;
		if (curFile.getParent().equalsIgnoreCase(strRoot))
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
			m_dirTv.setText(curFile.getName());
		} else {
			m_rootDirImage.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirParentTv.setText(curFile.getParentFile().getName());
			m_dirTv.setText(curFile.getName());
		}
	}

	private void getLocalEditStorageListData() {
		m_editStorageListData.clear();

		File curFile = new File(m_curDirectory);
		if (curFile == null || curFile.exists() == false) {
			curFile = Environment.getExternalStorageDirectory();
			m_curDirectory = curFile.getPath();
		}

		m_editStorageListData = FileUtils.listFiles(curFile);

		showDirectoryLayout();
		updateListView();

	}

	private void showSambaDirectoryLayout() {
		String strRoot = BusinessMannager.getInstance().getSambaPath();
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curDirectory = FileUtils.addLastFileSeparator(m_curDirectory);
		m_parentSmabaDirectory = FileUtils
				.addLastFileSeparator(m_parentSmabaDirectory);
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if (m_curDirectory.equalsIgnoreCase(strRoot))
			bIsRoot = true;

		if (m_parentSmabaDirectory.equalsIgnoreCase(strRoot))
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
			m_dirTv.setText(FileUtils.getFileName(m_curDirectory));
		} else {
			m_rootDirImage.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirParentTv
					.setText(FileUtils.getFileName(m_parentSmabaDirectory));
			m_dirTv.setText(FileUtils.getFileName(m_curDirectory));
		}
	}

	private void getSambaEditStorageListData() {

		HttpAccessLog.getInstance().writeLogToFile(
				"StorageEditActivity getSambaStorageListData path: "
						+ m_curDirectory);
		String strRoot = BusinessMannager.getInstance().getSambaPath();
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curDirectory = FileUtils.addLastFileSeparator(m_curDirectory);

		if (m_curDirectory.indexOf(strRoot) >= 0) {

		} else {
			m_curDirectory = strRoot;
		}

		m_editStorageListData.clear();
		m_progressWaiting.setVisibility(View.VISIBLE);
		SmbListFilesTask task = new SmbListFilesTask(m_curDirectory,
				m_editStorageListData, LIST_FILE_MODEL.ALL_FILE,
				m_smbListFilesTaskHandler);
		task.start();

	}

	private void updateListView() {
		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_editStorageListData);
		((EditStorageListAdapter) m_editStorageListView.getAdapter())
				.notifyDataSetChanged();
	}

	private class EditStorageListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public EditStorageListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return m_editStorageListData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public final class ViewHolder {
			public ImageView itemImage;
			public TextView itemName;
			public CheckBox itemCheckBox;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.storage_edit_list_adapter, null);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.normal_icon);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.name);
				holder.itemCheckBox = (CheckBox) convertView
						.findViewById(R.id.select);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FileItem item = m_editStorageListData.get(position);
			String strName = item.name;
			boolean bIsFolder = item.isDir;
			if (bIsFolder == true) {
				holder.itemImage.setBackgroundResource(R.drawable.item_folder);
			} else {
				// to do
				FileType fileType = FileModel.getFileType(strName);
				if (fileType == FileType.Audio) {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_music);
				} else if (fileType == FileType.Image) {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_image);
				} else if (fileType == FileType.Text) {
					holder.itemImage.setBackgroundResource(R.drawable.item_doc);
				} else if (fileType == FileType.Video) {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_video);
				} else {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_unknown_file);
				}
			}
			holder.itemName.setText(strName);
			boolean bChecked = (Boolean) m_editStorageListData.get(position).isSelected;
			holder.itemCheckBox.setChecked(bChecked);
			final CheckBox checkbox = holder.itemCheckBox;
			holder.itemCheckBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean bChecked = checkbox.isChecked();
					m_editStorageListData.get(position).isSelected = bChecked;
					showToolbarBtn();
				}
			});

			return convertView;
		}
	}
}
