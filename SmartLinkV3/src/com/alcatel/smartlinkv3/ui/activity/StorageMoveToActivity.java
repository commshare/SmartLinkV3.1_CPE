package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.ENUM.ServiceState;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask;
import com.alcatel.smartlinkv3.samba.SmbMoveFilesTask;
import com.alcatel.smartlinkv3.samba.SmbNewFolderTask;
import com.alcatel.smartlinkv3.samba.SmbUtils;
import com.alcatel.smartlinkv3.samba.SmbListFilesTask.ListFilesResult;
import com.alcatel.smartlinkv3.samba.SmbUtils.LIST_FILE_MODEL;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1;
import com.alcatel.smartlinkv3.ui.dialog.InputDialog1.OnBtnClick;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StorageMoveToActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private String m_strCurLocation = "";
	private String m_curDirectory = null;

	private Button m_cancelBtn = null;
	private ImageView m_rootDirImage = null;
	private ImageView m_arrowImage1 = null;
	private ImageView m_arrowImage2 = null;
	private TextView m_dirParentTv = null;
	private TextView m_dirTv = null;

	private TextView m_newFolder = null;
	private TextView m_ToHere = null;
	private TextView m_search = null;

	private ProgressBar m_progressWaiting = null;

	private ArrayList<String> m_selectedFile = new ArrayList<String>();

	private ListView m_moveToStorageListView = null;
	private ArrayList<FileItem> m_moveTostorageListData = new ArrayList<FileItem>();

	private static final int REQUESTCODE = 100;
	public static final String SEARCH_RESULT = "com.alcatel.cpe.ui.activity.returnsearch";

	private static final int MSG_LOGCL_STOREAGE_TO_HERE = 0;

	private String m_parentSmabaDirectory;
	
	// samba list files
	
    private Handler m_smbListFilesTaskHandler = new Handler(){
    	@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
    		switch(msg.what)
    		{
    		case SmbUtils.SMB_MSG_TASK_FINISH:    			
    			ListFilesResult res = (ListFilesResult) msg.obj;
				m_progressWaiting.setVisibility(View.GONE);
				m_parentSmabaDirectory = res.mParentPath;
				m_moveTostorageListData.clear();
				m_moveTostorageListData = (ArrayList<FileItem>) res.mFiles.clone();
				showSambaDirectoryLayout();			
				updateListView();  
    			break;
    			
    		case SmbUtils.SMB_MSG_TASK_ERROR:
    			m_progressWaiting.setVisibility(View.GONE);
    			String strErr = StorageMoveToActivity.this
    					.getString(R.string.list_samba_files_failed);
    			Toast.makeText(StorageMoveToActivity.this, strErr,
    					Toast.LENGTH_LONG).show();
    			break;     	
    		}  		
    	}    	
    };	
	
	// samba move files	
	private Handler m_smbMoveFilesTaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch(msg.what)
    		{
    		case SmbUtils.SMB_MSG_TASK_FINISH:    			
    			m_newFolder.setEnabled(true);
    			m_ToHere.setEnabled(true);
    			m_search.setEnabled(true);
    			m_progressWaiting.setVisibility(View.GONE);				
    			LocalStorageActivity.m_curSambaDirectory =m_curDirectory;
    			StorageMoveToActivity.this.finish();	
    			Intent intent = new Intent(SmbUtils.SMB_MSG_REFRESH_FILES);			
    			StorageMoveToActivity.this.sendBroadcast(intent);		
    			break;
    			
    		case SmbUtils.SMB_MSG_TASK_ERROR:    		
    			String strErr = StorageMoveToActivity.this
    					.getString(R.string.smb_move_files_failed);    			
    			Toast.makeText(StorageMoveToActivity.this, strErr,
    					Toast.LENGTH_LONG).show();    			
    			break;     		
    		}  
		}
	}; 

    //samba create new folder 
    private Handler m_smbNewFolderTaskHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch(msg.what)
    		{
    		case SmbUtils.SMB_MSG_TASK_FINISH:
    			getSambaStorageListData();
    			break;
    			
    		case SmbUtils.SMB_MSG_TASK_ERROR:
    			String msgError = StorageMoveToActivity.this.getString(R.string.new_folder_failed);
    			Toast.makeText(StorageMoveToActivity.this, msgError, Toast.LENGTH_SHORT).show();
    			break;
    			
    		case SmbUtils.SMB_MSG_FILE_EXISTS:
    			String msgExists = StorageMoveToActivity.this.getString(R.string.directory_is_exist);
    			Toast.makeText(StorageMoveToActivity.this, msgExists, Toast.LENGTH_SHORT).show();
    			break;
    		}  		
    	}    	
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_move_to_view);
		m_bNeedBack = false;

		m_strCurLocation = this.getIntent().getStringExtra(
				LocalStorageActivity.FLAG_CURRENT_LOCATION);

		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			m_curDirectory = Environment.getExternalStorageDirectory()
					.getPath();
		} else {
			m_curDirectory = BusinessMannager.getInstance().getSambaSettings().AccessPath;
			m_curDirectory = FileUtils.addLastFileSeparator(m_curDirectory);
		}
		m_selectedFile = this.getIntent().getStringArrayListExtra(
				StorageEditActivity.EDIT_SELECT_FILES);
		// get controls
		m_cancelBtn = (Button) this.findViewById(R.id.cancel_btn);
		m_cancelBtn.setOnClickListener(this);

		m_rootDirImage = (ImageView) this.findViewById(R.id.root_directory);
		m_rootDirImage.setOnClickListener(this);
		m_arrowImage1 = (ImageView) this.findViewById(R.id.directory_arrow1);
		m_arrowImage2 = (ImageView) this.findViewById(R.id.directory_arrow2);
		m_dirParentTv = (TextView) this.findViewById(R.id.directory_parent);
		m_dirParentTv.setOnClickListener(this);
		m_dirTv = (TextView) this.findViewById(R.id.directory);
		m_dirTv.setOnClickListener(this);

		m_newFolder = (TextView) this.findViewById(R.id.new_folder);
		m_newFolder.setOnClickListener(this);
		m_ToHere = (TextView) this.findViewById(R.id.to_here);
		m_ToHere.setOnClickListener(this);
		m_search = (TextView) this.findViewById(R.id.search);
		m_search.setOnClickListener(this);

		m_progressWaiting = (ProgressBar) this
				.findViewById(R.id.waiting_progress);
		m_progressWaiting.setVisibility(View.GONE);

		m_moveToStorageListView = (ListView) this.findViewById(R.id.list_view);
		m_moveToStorageListView.setOnItemClickListener(this);
		MoveToStorageListAdapter moveToStorageListAdapter = new MoveToStorageListAdapter(
				this);
		m_moveToStorageListView.setAdapter(moveToStorageListAdapter);

		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			getLocalStorageListData();
		} else {
			getSambaStorageListData();
		}
		initTitleBar();
	}
	
	private void initTitleBar() {
		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {
			int iamgeId = 0;
			if (FeatureVersionManager.getInstance().isSupportDevice(FeatureVersionManager.VERSION_DEVICE_M100) == true) {
				iamgeId = R.drawable.m100_media_box_white;
			} else {
				iamgeId = R.drawable.m100_hard_disc_white;
			}
			m_rootDirImage.setImageResource(iamgeId);
		}
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
		if(LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation))
		{
			StorageMonitor.registerReceiver(this);
		}	
	}

	@Override
	public void onPause() {
		super.onPause();
		if(LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation))
		{
			StorageMonitor.unregisterReceiver(this);
		}	
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel_btn:
			this.finish();
			break;
		case R.id.root_directory:
			if (LocalStorageActivity.FLAG_LOCAL
					.equalsIgnoreCase(m_strCurLocation)) {
				m_curDirectory = Environment.getExternalStorageDirectory()
						.getPath();
				getLocalStorageListData();
			}
			else
			{			
				m_curDirectory = BusinessMannager.getInstance()
							.getSambaSettings().AccessPath;
				m_curDirectory = FileUtils
							.addLastFileSeparator(m_curDirectory);
					getSambaStorageListData();
			}

			break;
		case R.id.directory_parent:
			if (LocalStorageActivity.FLAG_LOCAL
					.equalsIgnoreCase(m_strCurLocation)) {
				File file = new File(m_curDirectory);
				if (file.exists()) {
					m_curDirectory = file.getParentFile().getPath();
				} else {
					m_curDirectory = Environment.getExternalStorageDirectory()
							.getPath();
				}

				getLocalStorageListData();
			}
			else
			{
				m_curDirectory = m_parentSmabaDirectory;
				getSambaStorageListData();
			}
			break;

		case R.id.directory:

			if (LocalStorageActivity.FLAG_LOCAL
					.equalsIgnoreCase(m_strCurLocation)) {
				getLocalStorageListData();
			}
			else
			{
				getSambaStorageListData();
			}
			break;
		case R.id.new_folder:
			newFolderClick();
			break;
		case R.id.search:
			searchBtnClick();
			break;
		case R.id.to_here:
			toHereClick();
			break;
		}
	}

	private void toHereClick() {
		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			for (int i = 0; i < m_selectedFile.size(); i++) {
				String strDec = m_curDirectory.toLowerCase();
				String strSrc = m_selectedFile.get(i).toLowerCase();
				File file = new File(strSrc);
				if (strDec.indexOf(strSrc) == 0 && file.isDirectory()) {
					String msgRes = this
							.getString(R.string.move_to_is_subfolder_fail);
					String mes = String.format(msgRes, m_curDirectory,
							m_selectedFile.get(i));
					Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}

		m_newFolder.setEnabled(false);
		m_ToHere.setEnabled(false);
		m_search.setEnabled(false);
		m_progressWaiting.setVisibility(View.VISIBLE);

		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			toHereLocal();
		} else {
			SmbMoveFilesTask task = new SmbMoveFilesTask(m_selectedFile, m_curDirectory, m_smbMoveFilesTaskHandler);
			task.start();		
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGCL_STOREAGE_TO_HERE:
				m_newFolder.setEnabled(true);
				m_ToHere.setEnabled(true);
				m_search.setEnabled(true);
				m_progressWaiting.setVisibility(View.GONE);
				TohereResult result = (TohereResult) msg.obj;
				if (result.m_bResult == false) {
					Toast.makeText(StorageMoveToActivity.this,
							result.m_strMess, Toast.LENGTH_SHORT).show();
				}else{
					LocalStorageActivity.m_curDirectory = new File(m_curDirectory);
					StorageMoveToActivity.this.finish();
					return;
				}
				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					getLocalStorageListData();
				}
				break;
			}
		}
	};

	private class TohereResult {
		public boolean m_bResult = false;
		public String m_strMess = new String();
	}

	private void toHereLocal() {
		Thread searchingThread = new Thread() {
			@Override
			public void run() {
				TohereResult result = new TohereResult();
				File desFile = new File(m_curDirectory);
				if (desFile.exists() == true) {
					for (int i = 0; i < m_selectedFile.size(); i++) {
						File file = new File(m_selectedFile.get(i));
						if (file.exists() == true) {
							String newFileStr = m_curDirectory + "/"
									+ file.getName();
							File newFile = new File(newFileStr);
							if (newFile.exists() == false) {
								boolean bResult = file.renameTo(newFile);
								if (bResult == false) {
									result.m_bResult = false;
									String msgRes = StorageMoveToActivity.this
											.getString(R.string.move_to_file_move_fail);
									String mes = String.format(msgRes,
											newFileStr);
									result.m_strMess = mes;
									mHandler.obtainMessage(
											MSG_LOGCL_STOREAGE_TO_HERE, result)
											.sendToTarget();
									return;
								}
							} else {
								result.m_bResult = false;
								String msgRes = StorageMoveToActivity.this
										.getString(R.string.move_to_file_already_exist_fail);
								String mes = String.format(msgRes, newFileStr);
								result.m_strMess = mes;
								mHandler.obtainMessage(
										MSG_LOGCL_STOREAGE_TO_HERE, result)
										.sendToTarget();
								return;
							}
						}
					}
				}

				result.m_bResult = true;
				mHandler.obtainMessage(MSG_LOGCL_STOREAGE_TO_HERE, result)
						.sendToTarget();
			}
		};
		searchingThread.start();
	}

	private void searchBtnClick() {
		Intent intent = new Intent(this, StorageSearchActivity.class);
		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			intent.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
					LocalStorageActivity.FLAG_LOCAL);
			if (m_curDirectory != null)
				intent.putExtra(LocalStorageActivity.CURRENT_DIRECTORY,
						m_curDirectory);
		}
		else {
			intent.putExtra(LocalStorageActivity.FLAG_CURRENT_LOCATION,
					LocalStorageActivity.FLAG_SAMBA);
			intent.putExtra(LocalStorageActivity.CURRENT_DIRECTORY,
					m_curDirectory);
		}		 

		intent.putExtra(StorageSearchActivity.ACTIVITY_FROM,
				StorageSearchActivity.ACTIVITY_MOVE_TO);

		this.startActivityForResult(intent, REQUESTCODE);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUESTCODE) {
			switch (resultCode) {
			case RESULT_OK:
				String dateString = data.getExtras().getString(SEARCH_RESULT);
				if (dateString != null) {
					m_curDirectory = dateString;
					if (LocalStorageActivity.FLAG_LOCAL
							.equalsIgnoreCase(m_strCurLocation)) {
						getLocalStorageListData();
					}
					else
					{
						getSambaStorageListData();
					}
				}
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void newFolderClick() {
		final InputDialog1 newFolderDlg = new InputDialog1(this);
		newFolderDlg.m_titleTextView.setText(R.string.new_folder_dialog_title);
		newFolderDlg.m_promptView.setText(R.string.new_folder_prompt);
		newFolderDlg.m_okBtn.setText(R.string.ok);
		newFolderDlg.m_okBtn.setEnabled(false);
		newFolderDlg.m_inputEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String editable = newFolderDlg.m_inputEdit.getText().toString();
				boolean isInvalide = FileUtils.isInvalidFileName(editable);
				if (editable != null && editable.length() != 0 && editable.getBytes().length <= FileUtils.FILENAME_MAX_LENGTH) {
					if (isInvalide) {
						String strErr = StorageMoveToActivity.this
								.getString(R.string.file_name_error);
						Toast.makeText(StorageMoveToActivity.this, strErr,
								Toast.LENGTH_SHORT).show();
						newFolderDlg.m_okBtn.setEnabled(false);
					} else {
						newFolderDlg.m_okBtn.setEnabled(true);
					}
				} else {
					newFolderDlg.m_okBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		newFolderDlg.showDialog(new OnBtnClick() {
			@Override
			public void onBtnClick() {

				if (LocalStorageActivity.FLAG_LOCAL
						.equalsIgnoreCase(m_strCurLocation)) {
					createLocalFolder(newFolderDlg);
				} else {
					createSambaFolder(newFolderDlg);
				}
			}
		});
	}

	private void createLocalFolder(InputDialog1 dlg) {
		String editable = dlg.m_inputEdit.getText().toString();
		File file = new File(m_curDirectory + "/" + editable);
		if (file.exists()) {
			String msgRes = StorageMoveToActivity.this
					.getString(R.string.directory_is_exist);
			Toast.makeText(StorageMoveToActivity.this, msgRes,
					Toast.LENGTH_SHORT).show();
		} else {
			if (file.mkdirs() == true) {
				dlg.closeDialog();
				getLocalStorageListData();
			} else {
				String msgRes = StorageMoveToActivity.this
						.getString(R.string.new_folder_failed);
				Toast.makeText(StorageMoveToActivity.this, msgRes,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	private void createSambaFolder(InputDialog1 dlg) {
		String editable = dlg.m_inputEdit.getText().toString();
		String newFolder = FileUtils.combinePath(m_curDirectory, editable);
		SmbNewFolderTask task = new SmbNewFolderTask(newFolder,
				m_smbNewFolderTaskHandler);
		task.start();
		dlg.closeDialog();
	}

	private void showDirectoryLayout() {
		String strRoot = Environment.getExternalStorageDirectory().getPath();		
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		File curFile = new File(m_curDirectory);
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

	private void getLocalStorageListData() {
		m_moveTostorageListData.clear();

		File curFile = new File(m_curDirectory);
		if (curFile == null || curFile.exists() == false) {
			curFile = Environment.getExternalStorageDirectory();
			m_curDirectory = curFile.getPath();
		}

		m_moveTostorageListData = FileUtils.listDirectoris(curFile);
		showDirectoryLayout();
		updateListView();
	}

	private void updateListView() {
		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_moveTostorageListData);

		((MoveToStorageListAdapter) m_moveToStorageListView.getAdapter())
				.notifyDataSetChanged();
	}

	private void getSambaStorageListData() {
		
		HttpAccessLog.getInstance().writeLogToFile("StorageMoveToActivity getSambaStorageListData path: "+ m_curDirectory);	
		String strRoot = BusinessMannager.getInstance().getSambaSettings().AccessPath;
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curDirectory = FileUtils.addLastFileSeparator(m_curDirectory);
		
		if(m_curDirectory.indexOf(strRoot) >= 0)
		{
			
		}
		else
		{
			m_curDirectory = strRoot;
		}		
		
//		if(BusinessMannager.getInstance().getSambaServiceState() == ServiceState.Disabled && 
//			FeatureVersionManager.getInstance().isSupportDevice(FeatureVersionManager.VERSION_DEVICE_M100) == false)	
//		{
//			Intent it = new Intent(this, SdSharingActivity.class);
//			this.startActivjlvity(it);
//		}
//		else{
//			
//			m_moveTostorageListData.clear();
//			m_progressWaiting.setVisibility(View.VISIBLE);
//			SmbListFilesTask task = new SmbListFilesTask(m_curDirectory,
//					m_moveTostorageListData,  LIST_FILE_MODEL.DIR_ONLY, m_smbListFilesTaskHandler);
//			task.start();	
//		}
	
	}

	private void showSambaDirectoryLayout() {
		String strRoot = BusinessMannager.getInstance().getSambaSettings().AccessPath;
		strRoot = FileUtils.addLastFileSeparator(strRoot);
		m_curDirectory = FileUtils.addLastFileSeparator(m_curDirectory);
		m_parentSmabaDirectory = FileUtils.addLastFileSeparator(m_parentSmabaDirectory);
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if (m_curDirectory.equalsIgnoreCase(strRoot))
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
			enableToolBtns(false);
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

	private class MoveToStorageListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MoveToStorageListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return m_moveTostorageListData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public final class ViewHolder {
			public TextView itemName;
			public ImageView itemImage;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater
						.inflate(R.layout.storage_move_to_list_adapter, null);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.name);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.normal_icon);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FileItem item = m_moveTostorageListData.get(position);
			String strName = item.name;
			holder.itemImage.setBackgroundResource(R.drawable.m100_item_folder);
			holder.itemName.setText(strName);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		FileItem item = m_moveTostorageListData.get(position);

		if (LocalStorageActivity.FLAG_LOCAL.equalsIgnoreCase(m_strCurLocation)) {
			String strPath = item.path;
			File file = new File(strPath);
			if (file.exists()) {
				m_curDirectory = file.getPath();
			}
			getLocalStorageListData();
		}
		else
		{
			m_curDirectory = item.path;
			getSambaStorageListData();		
		}	
	}
	
	
	private void enableToolBtns(boolean bStatus)
	{
		m_newFolder.setEnabled(bStatus);
		m_ToHere.setEnabled(bStatus);	
	}

}
