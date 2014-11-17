package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.file.FileFinder;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileModel;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.common.file.FileModel.FileType;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.samba.SmbSearchFilesTask;
import com.alcatel.smartlinkv3.samba.SmbUtils;
import com.alcatel.smartlinkv3.ui.view.SearchEditText;
import com.alcatel.smartlinkv3.ui.view.SearchEditText.OnSearch;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StorageSearchActivity extends BaseActivity implements
		OnClickListener, OnSearch, OnItemClickListener {
	private String m_strCurLocation = "";
	private String m_curDirectory = "";
	private String m_fromActivty = "";

	public static final int MSG_LOGCL_STOREAGE_SEARCH_END = 0;
	public static final int MSG_LOGCL_STOREAGE_SEARCH_RESULT = 1;

	public static String ACTIVITY_FROM = "com.alcatel.smartlink.ui.activity.searchfrom";
	public static String ACTIVITY_MOVE_TO = "com.alcatel.smartlink.ui.activity.searchfrommoveto";
	public static String ACTIVITY_MAIN = "com.alcatel.smartlink.ui.activity.searchfrommain";

	private SearchEditText m_searchEditText;
	private TextView m_cancelBtn;
	private TextView m_searchNumber;
	private ListView m_searchStorageListView = null;
	private ArrayList<FileItem> m_searchStorageListData = new ArrayList<FileItem>();
	private FileFinder m_finder = null;
	SearchStorageListAdapter m_adapter;
	
	SmbSearchFilesTask m_task;
	
	private Handler m_smbSearchFilesTaskHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SmbUtils.SMB_MSG_TASK_FINISH:
				m_searchStorageListData = (ArrayList<FileItem>) msg.obj;
				showSambaSearchStorageListData();
				m_searchEditText.endSearch();
				break;

			case SmbUtils.SMB_MSG_TASK_ERROR:
				String strErr = StorageSearchActivity.this
						.getString(R.string.search_samba_files_failed);
				Toast.makeText(StorageSearchActivity.this, strErr,
						Toast.LENGTH_LONG).show();
				break;	
				
			case SmbUtils.SMB_MSG_TASK_UPDATE:
				m_searchStorageListData = (ArrayList<FileItem>) ((ArrayList<FileItem>)msg.obj).clone();					
			//	if(m_searchStorageListData.size() % 5 == 0)
				{					
					m_adapter.notifyDataSetChanged();
					m_searchStorageListView.setSelection(m_adapter.getCount()-1);
				}				 
				break;
			}	
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_samba_search);
		m_bNeedBack = false;

		m_strCurLocation = this.getIntent().getStringExtra(
				LocalStorageActivity.FLAG_CURRENT_LOCATION);
		m_curDirectory = this.getIntent().getStringExtra(
				LocalStorageActivity.CURRENT_DIRECTORY);
		m_fromActivty = this.getIntent().getStringExtra(ACTIVITY_FROM);
		// get controls
		m_searchEditText = (SearchEditText) this
				.findViewById(R.id.search_edit_text);
		m_searchEditText.setSearchCallBack(this);
		m_cancelBtn = (TextView) this.findViewById(R.id.cancel_btn);
		m_cancelBtn.setOnClickListener(this);
		m_searchNumber = (TextView) this.findViewById(R.id.search_number);
		m_searchStorageListView = (ListView) this.findViewById(R.id.list_view);
		m_searchStorageListView.setOnItemClickListener(this);
		m_adapter = new SearchStorageListAdapter(this);
		m_searchStorageListView.setAdapter(m_adapter);
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
		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {
			StorageMonitor.registerReceiver(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		endSearch();
		if (LocalStorageActivity.FLAG_SAMBA.equalsIgnoreCase(m_strCurLocation)) {
			StorageMonitor.unregisterReceiver(this);
		}
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel_btn:
			this.finish();
			break;
		}
	}

	private void endSearch() {

		if (m_strCurLocation.equalsIgnoreCase(LocalStorageActivity.FLAG_LOCAL)) {
			if (m_finder != null)
				m_finder.setCancel(true);
		} else {			
			if (m_task != null && m_task.isAlive()) {
				m_task.setCancel(true);
			}
		}
	}

	private void showLocalSearchStorageListData(ArrayList<File> serchResult) {
		m_searchStorageListData.clear();
		int nNumber = 0;
		if (serchResult != null)
			nNumber = serchResult.size();
		String strPrompt = this.getString(R.string.search_number);
		String strSearchResult = String.format(strPrompt, nNumber,
				m_searchEditText.getSearchText());
		m_searchNumber.setText(strSearchResult);
		m_searchNumber.setVisibility(View.VISIBLE);

		for (int i = 0; i < nNumber; i++) {
			File file = serchResult.get(i);
			FileItem item = new FileItem();
			item.isDir = file.isDirectory();
			item.name = file.getName();
			item.parentDir = file.getParent();
			item.path = file.getPath();
			m_searchStorageListData.add(item);
		}

		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_searchStorageListData);
		m_adapter.notifyDataSetChanged();
	}

	private void showSambaSearchStorageListData() {

		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_searchStorageListData);

		String strPrompt = this.getString(R.string.search_number);
		String strSearchResult = String.format(strPrompt,
				m_searchStorageListData.size(),
				m_searchEditText.getSearchText());
		m_searchNumber.setText(strSearchResult);
		m_searchNumber.setVisibility(View.VISIBLE);
		m_adapter.notifyDataSetChanged();
	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGCL_STOREAGE_SEARCH_END:
				showLocalSearchStorageListData((ArrayList<File>) msg.obj);
				m_searchEditText.endSearch();
				break;
			case MSG_LOGCL_STOREAGE_SEARCH_RESULT:
				showLocalSearchStorageListData((ArrayList<File>) msg.obj);
				break;
			}
		}
	};

	private void getLocalSearchResult(final String strSearch) {
		if (m_curDirectory != null) {
			Thread searchingThread = new Thread() {
				@Override
				public void run() {
					m_finder = new FileFinder();
					int nSearchType = 0;// 0:all 1:files; 2:directory
					if (ACTIVITY_MOVE_TO.equalsIgnoreCase(m_fromActivty) == true)
						nSearchType = 2;
					ArrayList<File> serchLocalResult = (ArrayList<File>) m_finder
							.findFiles(m_curDirectory, strSearch, nSearchType,mHandler);
					m_finder = null;
					mHandler.obtainMessage(MSG_LOGCL_STOREAGE_SEARCH_END,
							serchLocalResult).sendToTarget();
				}
			};
			searchingThread.start();
		}
	}

	private void getSambaSearchResult(String strSearch) {
		if (m_curDirectory != null) {		

			if (ACTIVITY_MOVE_TO.equalsIgnoreCase(m_fromActivty) == true) {
				m_task = new SmbSearchFilesTask(m_curDirectory,
						strSearch, SmbUtils.LIST_FILE_MODEL.DIR_ONLY,
						m_smbSearchFilesTaskHandler);
			} else {
				m_task = new SmbSearchFilesTask(m_curDirectory,
						strSearch, SmbUtils.LIST_FILE_MODEL.ALL_FILE,
						m_smbSearchFilesTaskHandler);
			}
			m_task.start();
		}
	}

	@Override
	public void onSearch(String strSearch) {
		m_searchNumber.setVisibility(View.GONE);
		m_searchStorageListData.clear();
		m_adapter.notifyDataSetChanged();

		if (m_strCurLocation.equalsIgnoreCase(LocalStorageActivity.FLAG_LOCAL)) {
			getLocalSearchResult(strSearch);
		} else {
			getSambaSearchResult(strSearch);
		}
	}

	private class SearchStorageListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchStorageListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return m_searchStorageListData.size();
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
			public TextView itemPath;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.storage_search_list_adapter, null);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.normal_icon);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.name);
				holder.itemPath = (TextView) convertView
						.findViewById(R.id.directory);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FileItem item = m_searchStorageListData.get(position);

			String strName = item.name;
			String strDir = item.parentDir;
			boolean bIsFolder = item.isDir;

			if (bIsFolder == true) {
				holder.itemImage
						.setBackgroundResource(R.drawable.item_folder);
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
					holder.itemImage
							.setBackgroundResource(R.drawable.item_doc);
				} else if (fileType == FileType.Video) {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_video);
				} else {
					holder.itemImage
							.setBackgroundResource(R.drawable.item_unknown_file);
				}
			}
			holder.itemName.setText(strName);
			holder.itemPath.setText(strDir);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		endSearch();
		
		FileItem item = m_searchStorageListData.get(position);
		if (m_strCurLocation.equalsIgnoreCase(LocalStorageActivity.FLAG_LOCAL)) {
			if (ACTIVITY_MOVE_TO.equalsIgnoreCase(m_fromActivty) == true) {
				Intent intent = new Intent();
				intent.putExtra(StorageMoveToActivity.SEARCH_RESULT, item.path);
				this.setResult(RESULT_OK, intent);
				this.finish();
			}
			if (ACTIVITY_MAIN.equalsIgnoreCase(m_fromActivty) == true) {
				if (item.isDir) {
					LocalStorageActivity.m_curDirectory = new File(item.path);
					this.finish();
				} else {
					FileUtils.openFile(this, item.path);
				}
			}
		} else {
			if (ACTIVITY_MOVE_TO.equalsIgnoreCase(m_fromActivty) == true) {
				Intent intent = new Intent();
				intent.putExtra(StorageMoveToActivity.SEARCH_RESULT, item.path);
				this.setResult(RESULT_OK, intent);
				this.finish();
			}

			if (ACTIVITY_MAIN.equalsIgnoreCase(m_fromActivty) == true) {
				if (item.isDir) {
					LocalStorageActivity.m_curSambaDirectory = item.path;
					this.finish();
					Intent intent = new Intent(SmbUtils.SMB_MSG_REFRESH_FILES);			
					this.sendBroadcast(intent);		
				} else {
					SmbUtils.openFile(this, item.path);
				}
			}
		}
	}

	@Override
	public void onEndSearch() {
		// TODO Auto-generated method stub
		endSearch();
	}
}
