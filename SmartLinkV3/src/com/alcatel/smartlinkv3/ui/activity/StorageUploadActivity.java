package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileModel;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.common.file.FileModel.FileType;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbUtils;
import com.alcatel.smartlinkv3.ui.dialog.DialogAutoDismiss;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireCancle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StorageUploadActivity extends BaseActivity implements OnClickListener {
	private String m_curDirectory = null;

	private TextView m_cancelBtn = null;
	private ImageView m_rootDirImage = null;
	private ImageView m_arrowImage1 = null;
	private ImageView m_arrowImage2 = null;
	private TextView m_dirParentTv = null;
	private TextView m_dirTv = null;
	
	private TextView m_uploadBtn = null;
	private CheckBox m_selectAllCb = null;
	
	private ListView m_listView = null;
	private ArrayList<FileItem> m_listData = new ArrayList<FileItem>();
	
	private ProgressBar m_progressWaiting = null;
	private String m_uploadDir = "";
	
	public static String UPLOAD_DIRECTORY = "com.alcatel.smartlink.ui.activity.uploaddirectory";
	
	boolean IsDialog = false;
	int DialogNum = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_upload_view);
		m_bNeedBack = false;		
		m_uploadDir = this.getIntent().getStringExtra(UPLOAD_DIRECTORY);
		// get controls
		m_cancelBtn = (TextView)this.findViewById(R.id.cancel_btn);
		m_cancelBtn.setOnClickListener(this);
		
		m_rootDirImage = (ImageView)this.findViewById(R.id.root_directory);
		m_arrowImage1 = (ImageView)this.findViewById(R.id.directory_arrow1);
		m_arrowImage2 = (ImageView)this.findViewById(R.id.directory_arrow2);
		m_dirParentTv = (TextView)this.findViewById(R.id.directory_parent);
		m_dirTv = (TextView)this.findViewById(R.id.directory);
		m_rootDirImage.setOnClickListener(this);
		m_dirParentTv.setOnClickListener(this);
		m_dirTv.setOnClickListener(this);
		
		m_uploadBtn = (TextView)this.findViewById(R.id.upload_btn);
		m_selectAllCb = (CheckBox)this.findViewById(R.id.select_all);
		m_selectAllCb.setOnClickListener(this);
		m_uploadBtn.setOnClickListener(this);
		
		m_listView = (ListView) this.findViewById(R.id.list_view);
		ListAdapter listAdapter = new ListAdapter(this);
		m_listView.setAdapter(listAdapter);
		
		m_progressWaiting = (ProgressBar) this.findViewById(R.id.waiting_progress);	
		m_progressWaiting.setVisibility(View.GONE);		

		getListData();
		showToolbarBtn();
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
		StorageMonitor.registerReceiver(this);		
	}

	@Override
	public void onPause() {
		super.onPause();	
		StorageMonitor.unregisterReceiver(this);		
	}

	public void onClick(View arg0) {
		switch(arg0.getId()) {
		case R.id.cancel_btn:
			this.finish();
			break;
		case R.id.select_all:
			onSeleckAllClick();
			break;
		case R.id.root_directory:
			showCheckOffPrompt();
			m_curDirectory = Environment.getExternalStorageDirectory().getPath();
			getListData();
			break;
		case R.id.directory_parent:
			showCheckOffPrompt();
			File file = new File(m_curDirectory);
			if (file.exists()) {
				m_curDirectory = file.getParentFile().getPath();
			} else {
				m_curDirectory = Environment.getExternalStorageDirectory()
						.getPath();
			}

			getListData();
			showToolbarBtn();
			break;
		case R.id.directory:
			showCheckOffPrompt();
			getListData();
			showToolbarBtn();
			break;
		case R.id.upload_btn:
			onUpLoadClick();
			showToolbarBtn();
			break;	
		}
	}
	
	private void showCheckOffPrompt() {
		boolean bHaveCheck = false;
		for(int i = 0;i < m_listData.size();i++) {
			if(m_listData.get(i).isSelected == true) {
				bHaveCheck = true;
				break;
			}
		}
		
		if(bHaveCheck) {
			DialogAutoDismiss dialog = new DialogAutoDismiss(this);
			dialog.m_contentTextView.setText(R.string.check_off_prompt);
			dialog.showDialog(5000);
		}
	}
	
	
	private void onUpLoadClick()
	{	
		DialogNum = 0;
		final ArrayList<FileItem> uploadFilesTwo = new  ArrayList<FileItem>();
		ArrayList<FileItem> uploadFiles = new  ArrayList<FileItem>();		 
		for (int i = 0; i < m_listData.size(); i++) {

			boolean bChecked = m_listData.get(i).isSelected;
			if (bChecked) {
				uploadFiles.add(m_listData.get(i));				
			}
		}
		
		for (int j = 0; j < uploadFiles.size(); j++)
		{
			File file = new File(uploadFiles.get(j).path);
			String smbDirTmp = FileUtils
					.combinePath(m_uploadDir, file.getName());
			if(FileIsExists(smbDirTmp))
			{
				IsDialog = true;
				uploadFilesTwo.add(uploadFiles.get(j));
				final InquireReplaceDialog inquireDlg = new InquireReplaceDialog(this);
				DialogNum++;			
				inquireDlg.m_titleTextView.setText(R.string.confirm_folder_replace);
				inquireDlg.m_contentTextView.setText(R.string.confirm_folder_content);
				inquireDlg.m_confirmBtn.setText(R.string.yes);
				inquireDlg.showDialog(new OnInquireApply() {
					@Override
					public void onInquireApply() {
						// TODO Auto-generated method stub
						inquireDlg.closeDialog();
						DialogNum--;
						if(DialogNum < 1)
						{
							LocalStorageActivity.uploadFiles(uploadFilesTwo, m_uploadDir);
							finishActivity();
						}
					}

						
				},new OnInquireCancle() {
					@Override
					public void onInquireCancel() {
						// TODO Auto-generated method stub
						uploadFilesTwo.remove(0);
						inquireDlg.closeDialog();
						DialogNum--;
						if(DialogNum < 1)
						{
							finishActivity();
						}
					}
				});
			}
		}

		for(int z = 0;z < uploadFilesTwo.size();z++)
		{
			int t=uploadFiles.size();
			while(t > 0)
			{
				if(uploadFilesTwo.get(z).name.equals(uploadFiles.get(--t).name))
				{
					uploadFiles.remove(t);
				}
			}
		}
		
		LocalStorageActivity.uploadFiles(uploadFiles, m_uploadDir);
		Log.v("pchong", "pccccc  uploadFiles.size"+uploadFiles.size());
	/*	SmbUploadFilesTask task = new SmbUploadFilesTask(uploadFiles, m_uploadDir, this);
		task.start();*/		
		if(IsDialog != true)
		{
			this.finish();
		}
		
	}
	private void finishActivity() {
		// TODO Auto-generated method stub
		this.finish();
	}
	
	public static boolean FileIsExists(String path) {
		boolean bRes = false;
		SmbFile smbFile;
		try {
			smbFile = new SmbFile(path, SmbUtils.AUTH);
			try {
				if (smbFile.exists()) {
					bRes = true;
				}else {
					bRes = false;
				}
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: FileIsExists: "+ e.getMessage());	
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: FileIsExists: "+ e.getMessage());	
			e.printStackTrace();
		}
		return bRes;
	}
	
	private void onSeleckAllClick() {
		boolean bChecked = m_selectAllCb.isChecked();
		for(int i = 0;i < m_listData.size();i++) {
			m_listData.get(i).isSelected = bChecked;
		}
		((ListAdapter)m_listView.getAdapter()).notifyDataSetChanged();
		showToolbarBtn();
	}
	
	private void showToolbarBtn() {
		if(m_listData.size() == 0) {
			m_selectAllCb.setEnabled(false);
		}else {
			m_selectAllCb.setEnabled(true);
		}	
		int nCheckCount = 0;
		for(int i = 0;i < m_listData.size();i++) {
			boolean bChecked = m_listData.get(i).isSelected;
			if(bChecked)
				nCheckCount++;
		}
		if(nCheckCount == m_listData.size()) {
			m_selectAllCb.setChecked(true);
		}else{
			m_selectAllCb.setChecked(false);
		}
		
		if(nCheckCount == 0) {
			m_uploadBtn.setEnabled(false);
		}else{
			m_uploadBtn.setEnabled(true);
		}
	}
	
	private void showDirectoryLayout() {
		String strRoot = Environment.getExternalStorageDirectory().getPath();
		File curFile = new File(m_curDirectory);
		boolean bIsRoot = false;
		boolean bIsParentRoot = false;
		if(curFile.getPath().equalsIgnoreCase(strRoot))
			bIsRoot = true;
		if(curFile.getParent().equalsIgnoreCase(strRoot))
			bIsParentRoot = true;
		
		if(bIsRoot == true) {
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.GONE);
			m_dirTv.setVisibility(View.GONE);
		}else if(bIsParentRoot == true){
			m_rootDirImage.setVisibility(View.VISIBLE);
			m_dirParentTv.setVisibility(View.GONE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirTv.setText(curFile.getName());
		}else{
			m_rootDirImage.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirParentTv.setText(curFile.getParentFile().getName());
			m_dirTv.setText(curFile.getName());
		}
	}
	
	
	private void getListData() {
		m_listData.clear();
		
		if(m_curDirectory == null || m_curDirectory.length() == 0)
			m_curDirectory = Environment.getExternalStorageDirectory().getPath();
		File curFile = new File(m_curDirectory);
		if(curFile == null || curFile.exists() == false) {
			curFile = Environment.getExternalStorageDirectory();
			m_curDirectory = curFile.getPath();
		}
		
		m_listData = FileUtils.listFiles(curFile);
		
		showDirectoryLayout();
		updateListView();
	}
	
	private void updateListView()
	{
		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_listData);			
		((ListAdapter)m_listView.getAdapter()).notifyDataSetChanged();
	}
	
	private class ListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
			
		public ListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_listData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public ImageView itemImage;
			public TextView itemName;
			public RelativeLayout itemContentLayout;
			public ImageView itemLine;
			public CheckBox itemCheckBox;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.storage_upload_list_adapter,null);	
				holder.itemImage = (ImageView)convertView.findViewById(R.id.normal_icon);
				holder.itemLine = (ImageView)convertView.findViewById(R.id.line);
				holder.itemName = (TextView)convertView.findViewById(R.id.name);
				holder.itemContentLayout = (RelativeLayout)convertView.findViewById(R.id.layout_content);
				holder.itemCheckBox = (CheckBox)convertView.findViewById(R.id.checkbox);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			final FileItem item = m_listData.get(position);
			String strName = item.name;
			boolean bIsFolder = item.isDir;
			if(bIsFolder == true) {
				holder.itemImage.setBackgroundResource(R.drawable.item_folder);
			}else{
				//to do
				FileType fileType = FileModel.getFileType(strName);
				if(fileType == FileType.Audio) {
					holder.itemImage.setBackgroundResource(R.drawable.item_music);
				}else if(fileType == FileType.Image) {
					holder.itemImage.setBackgroundResource(R.drawable.item_image);
				}else if(fileType == FileType.Text) {
					holder.itemImage.setBackgroundResource(R.drawable.item_doc);
				}else if(fileType == FileType.Video){
					holder.itemImage.setBackgroundResource(R.drawable.item_video);
				}else {
					holder.itemImage.setBackgroundResource(R.drawable.item_unknown_file);
				}
			}
			holder.itemName.setText(strName);
			boolean bChecked = (Boolean)m_listData.get(position).isSelected;
			holder.itemCheckBox.setChecked(bChecked);
			final CheckBox checkbox = holder.itemCheckBox;
			holder.itemCheckBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean bChecked = checkbox.isChecked();
					m_listData.get(position).isSelected = bChecked;
					showToolbarBtn();
				}	
			});
			
			if(item.isDir == true) {
				boolean bHaveSubdir = haveFiles(item.path);
				if(bHaveSubdir == true) {
					holder.itemLine.setVisibility(View.VISIBLE);
					holder.itemContentLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {						
							m_curDirectory = item.path;
							showCheckOffPrompt();
							getListData();
						}
					});
				}else{
					holder.itemContentLayout.setOnClickListener(null);
					holder.itemLine.setVisibility(View.GONE);
				}
			}else{
				holder.itemContentLayout.setOnClickListener(null);
				holder.itemLine.setVisibility(View.GONE);
			}
			
			
			return convertView;
		}
		
		private boolean haveFiles(String strDir) {
			boolean bRes = false;
			File file = new File(strDir);
			File[] files = file.listFiles();
			if(files != null)
			{
				for(int i = 0;i < files.length;i++) {
					if(!files[i].isHidden() )
					{
						if(files[i].isDirectory() || files[i].isFile())
						{
							bRes =  true;
							break;
						}						
					}				
				}
			}			
			return bRes;
		}
	}
}
