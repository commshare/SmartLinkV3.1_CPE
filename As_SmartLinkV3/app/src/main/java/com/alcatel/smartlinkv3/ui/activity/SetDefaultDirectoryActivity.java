package com.alcatel.smartlinkv3.ui.activity;

import java.io.File;
import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileSortUtils;
import com.alcatel.smartlinkv3.common.file.FileSortUtils.SortMethod;
import com.alcatel.smartlinkv3.common.file.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;

import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetDefaultDirectoryActivity extends BaseActivity implements
		OnClickListener {
	private Button m_cancelBtn = null;
	private Button m_selectBtn = null;
	private String m_curDirectory = null;
	private ImageView m_arrowImage1 = null;
	private ImageView m_arrowImage2 = null;
	private TextView m_dirParentTv = null;
	private TextView m_dirTv = null;

	private ListView m_listView = null;
	private ArrayList<FileItem> m_listData = new ArrayList<FileItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_default_storage_view);
		m_bNeedBack = false;

		// get controls
		m_cancelBtn = (Button) this.findViewById(R.id.cancel_btn);
		m_cancelBtn.setOnClickListener(this);
		m_selectBtn = (Button) this.findViewById(R.id.select_btn);
		m_selectBtn.setOnClickListener(this);
		m_arrowImage1 = (ImageView) this.findViewById(R.id.directory_arrow1);
		m_arrowImage2 = (ImageView) this.findViewById(R.id.directory_arrow2);
		m_dirParentTv = (TextView) this.findViewById(R.id.directory_parent);
		m_dirParentTv.setOnClickListener(this);
		m_dirTv = (TextView) this.findViewById(R.id.directory);
		m_dirTv.setOnClickListener(this);

		m_listView = (ListView) this.findViewById(R.id.list_view);
		ListAdapter listAdapter = new ListAdapter(this);
		m_listView.setAdapter(listAdapter);

		initCurDirectory();
	}

	private void initCurDirectory() {
		String strRoot = Environment.getExternalStorageDirectory().getPath();
		String strCurDir = CPEConfig.getInstance().getDefaultDir();
		if (strRoot.equalsIgnoreCase(strCurDir)) {
			m_curDirectory = strCurDir;
		} else {
			File file = new File(strCurDir);
			m_curDirectory = file.getParentFile().getPath();
		}
		getListData();
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
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel_btn:
			this.finish();
			break;
		case R.id.directory_parent:
			String strRoot = Environment.getExternalStorageDirectory()
					.getPath();
			File curFile = new File(m_curDirectory);
			if (curFile.getPath().equalsIgnoreCase(strRoot)) {
				m_curDirectory = Environment.getExternalStorageDirectory()
						.getPath();
			} else {
				m_curDirectory = curFile.getParent();
			}
			getListData();
			break;

		case R.id.directory:
			getListData();
			break;
		case R.id.select_btn:
			selectBtnClick();
			break;
		}
	}

	private void selectBtnClick() {
		for (int i = 0; i < m_listData.size(); i++) {
			FileItem item = m_listData.get(i);
			if (item.isSelected) {
				CPEConfig.getInstance().setDefaultDir(item.path);
				break;
			}
		}
		this.finish();
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
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.GONE);
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_dirParentTv.setText(curFile.getName());
			m_dirTv.setVisibility(View.GONE);
		} else if (bIsParentRoot == true) {
			m_dirParentTv.setVisibility(View.VISIBLE);
			m_dirParentTv.setText(curFile.getParentFile().getName());
			m_arrowImage1.setVisibility(View.VISIBLE);
			m_dirTv.setVisibility(View.VISIBLE);
			m_arrowImage2.setVisibility(View.VISIBLE);
			m_dirTv.setText(curFile.getName());
		} else {
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
		File file = new File(m_curDirectory);
		if (file == null || file.exists() == false) {
			file = Environment.getExternalStorageDirectory();
			m_curDirectory = file.getPath();
		}

		m_listData = FileUtils.listDirectoris(file);
		String strDefaultDir = CPEConfig.getInstance().getDefaultDir();
		for (int i = 0; i < m_listData.size(); i++) {
			FileItem item = m_listData.get(i);
			if (item.path.equalsIgnoreCase(strDefaultDir)) {
				item.isSelected = true;
				break;
			}
		}
		changeSelectBtnState();
		showDirectoryLayout();
		updateListView();
	}

	private void changeSelectBtnState() {
		boolean bHaveChecked = false;
		for (int i = 0; i < m_listData.size(); i++) {
			FileItem item = m_listData.get(i);
			if (item.isSelected) {
				bHaveChecked = true;
				break;
			}
		}
		m_selectBtn.setEnabled(bHaveChecked);
	}

	private void updateListView() {
		FileSortUtils sort = new FileSortUtils();
		sort.setSortMethod(SortMethod.name);
		sort.sort(m_listData);

		((ListAdapter) m_listView.getAdapter()).notifyDataSetChanged();
	}

	private class ListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public ListAdapter(Context context) {
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

		public final class ViewHolder {
			public TextView itemName;
			public RadioButton itemRadio;
			public RelativeLayout itemContent;
			public ImageView itemLine;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(
						SetDefaultDirectoryActivity.this).inflate(
						R.layout.set_default_directory_list_adapter, null);
				holder.itemContent = (RelativeLayout) convertView
						.findViewById(R.id.layout_content);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.name);
				holder.itemRadio = (RadioButton) convertView
						.findViewById(R.id.radio);
				holder.itemLine = (ImageView) convertView
						.findViewById(R.id.line);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final FileItem item = m_listData.get(position);
			String strName = item.name;
			holder.itemName.setText(strName);

			boolean bHaveSubdir = haveSubDirectory(item.path);
			if (bHaveSubdir == true) {
				holder.itemLine.setVisibility(View.VISIBLE);
				holder.itemContent.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						m_curDirectory = item.path;
						getListData();
					}
				});
			} else {
				holder.itemContent.setOnClickListener(null);
				holder.itemLine.setVisibility(View.GONE);
			}

			boolean bChecked = item.isSelected;
			holder.itemRadio.setChecked(bChecked);
			final RadioButton radioBox = holder.itemRadio;
			holder.itemRadio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// clean check flag
					for (int i = 0; i < m_listData.size(); i++) {
						m_listData.get(i).isSelected = false;
					}
					((ListAdapter) m_listView.getAdapter())
							.notifyDataSetChanged();
					boolean bChecked = radioBox.isChecked();
					m_listData.get(position).isSelected = bChecked;
					changeSelectBtnState();
				}
			});

			return convertView;
		}

		private boolean haveSubDirectory(String strDir) {
			File file = new File(strDir);
			File[] files = file.listFiles();
			if (files == null)
				return false;
			for (int i = 0; i < files.length; i++) {
				if (files[i].isHidden() || files[i].isFile())
					continue;
				if (files[i].isDirectory())
					return true;
			}
			return false;
		}
	}
}
