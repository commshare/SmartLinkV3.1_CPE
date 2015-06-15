package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.fileexplorer.TextInputDialog.OnFinishListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class FileDialog extends Activity {

	private static final String TAG = "Moveto";
	private static final String PATH_SD_CARD = "/sdcard";

	protected List<Map<String, Object>> mData;
	protected String mDir = PATH_SD_CARD;
	protected ListView mListView;
	private MyAdapter mAdapter;

	private ResultCode.Builder mResultCodeBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		String title = bl.getString("explorer_title");
		Uri uri = intent.getData();
		mDir = uri.getPath();
		setTitle(title);
		mData = new ArrayList<Map<String, Object>>();

		setContentView(R.layout.list_file);
		mListView = (ListView) findViewById(R.id.list_file);
		mListView.setOnItemClickListener(listener);

		TextView textNavigationDir = (TextView) findViewById(R.id.text_navigation_directory);
		textNavigationDir.setOnClickListener(mClickListener);
		showTextNavigationDir();

		findViewById(R.id.button_cancel).setOnClickListener(mClickListener);
		findViewById(R.id.button_choose).setOnClickListener(mClickListener);
		findViewById(R.id.image_new_foler).setOnClickListener(mClickListener);

		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);

		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
        int minv = min(d.getHeight(),d.getWidth());
        p.height = (int)(minv * 0.84);
        p.width = (int) (minv * 0.76);
		getWindow().setAttributes(p);

		setResultCodeBuilder();

	}
	
	private int min(int v1, int v2) { 
	    return (v1 < v2) ? v1 : v2;
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "on Resume notify data changed. dir is " + mDir);
		notifyDataChanged();
	}

	private String filePath2Name(String path) {
		int index = path.lastIndexOf(File.separatorChar);
		String name = (index >= 0) ? path.substring(index + 1) : path;
		return name;
	}

	private String fileParent(String path) {
		int length = path.length(), firstInPath = 0;
		if (File.separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
			firstInPath = 2;
		}
		int index = path.lastIndexOf(File.separatorChar);
		if (index == -1 && firstInPath > 0) {
			index = 2;
		}
		if (index == -1 || path.charAt(length - 1) == File.separatorChar) {
			return null;
		}
		if (path.indexOf(File.separatorChar) == index
				&& path.charAt(firstInPath) == File.separatorChar) {
			return path.substring(0, index + 1);
		}
		return path.substring(0, index);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_navigation_directory:
				if (!mDir.equals(PATH_SD_CARD)) {
					mDir = fileParent(mDir);
					notifyDataChanged();
				}
				break;
			case R.id.button_cancel:
				finish();
				break;
			case R.id.button_choose:
				finishWithResult(mDir);
				break;
			case R.id.image_new_foler:
				showNewFolerDia(FileDialog.this);
				break;
			}
		}
	};

	protected List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles();

		files = filter(files);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())
					map.put("img", R.drawable.ex_folder);
				else
					map.put("img", R.drawable.ex_doc);
				list.add(map);
			}
		}
		return list;
	}

	private boolean checkFileName(String fileName) {

		if (fileName == null)
			return false;

		for (Map<String, Object> map : mData) {
			if (fileName.equals(map.get("title")))
				return true;
		}

		return false;
	}

	private File[] filter(File[] files) {
		ArrayList<File> list = new ArrayList<File>();
		for (File f : files) {
			if (f.isDirectory())
				list.add(f);
		}
		return list.toArray(new File[list.size()]);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("MyListView4-click", (String) mData.get(position).get("info"));
		if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder) {
			mDir = (String) mData.get(position).get("info");
			notifyDataChanged();
		} else {
			finishWithResult((String) mData.get(position).get("info"));
		}
	}

	protected void notifyDataChanged() {
		mData = getData();
		BaseAdapter adapter = (BaseAdapter) mListView.getAdapter();
		adapter.notifyDataSetChanged();

		showTextNavigationDir();
	}

	protected void showTextNavigationDir() {
		final int DIR_LIMIT = 2;

		TextView textNavigationDir = (TextView) findViewById(R.id.text_navigation_directory);
		String textContent = null;
		if (mDir.equals(PATH_SD_CARD)) {
			textContent = this.getResources().getString(R.string.sdcard);
		} else {
			textContent = mDir;
			String dirs[] = textContent.substring(1).split(File.separator);
			if (dirs.length > DIR_LIMIT) {
				textContent = "";
				for (int i = 0; i < DIR_LIMIT; i++) {
					textContent = File.separator + dirs[dirs.length - 1 - i]
							+ textContent;
				}
				textContent = "..." + textContent;
			}
		}
		textNavigationDir.setText(textContent);
	}

	private OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			onListItemClick(mListView, view, position, id);
		}
	};

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listview, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get(
					"img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
	}

	private void finishWithResult(String path) {
		Bundle conData = new Bundle();
		conData.putString("results", "Thanks Thanks");
		Intent intent = new Intent();
		intent.putExtras(conData);
		Uri startDir = Uri.fromFile(new File(path));
		intent.setDataAndType(startDir,
				"vnd.android.cursor.dir/lysesoft.andexplorer.file");
		setResult(RESULT_OK, intent);
		finish();
	}

	private void setResultCodeBuilder() {
		this.mResultCodeBuilder = ResultCode
				.getBuilder()
				.setFail(getString(R.string.fail))
				.setFailCause(getResources().getStringArray(R.array.fail_cause))
				.setSucceed(getString(R.string.succeed))
				.setSucceedDesc(
						getResources().getStringArray(R.array.succeed_desc))
				.setmListener(new ResultCode.OnResultListener() {

					@Override
					public void onResult(ResultCode resultCode) {
						if (!resultCode.isSucceed())
							showMessage(resultCode.getStringByResult(),
									resultCode.getStringByCause());
					}
				});
	}

	private ResultCode getResultCode(int Code) {
		return mResultCodeBuilder.builder(Code);
	}

	protected boolean doOperationNewFoler(String folerPath) {

		int result = ResultCode.SUCCEED_CREATE_FOLDER;

		do {
			File f = new File(folerPath);
			if (f.exists()) {
				result = ResultCode.FAIL_FOLDER_EXISTS;
				break;
			}
			if (!f.mkdirs()) {
				result = ResultCode.FAIL_FOLDER_CREATE;
				break;
			}
		} while (false);

		return getResultCode(result).doResult();
	}

	protected void onDialogClick(int id, int code, Object... args) {
		switch (id) {
		case R.layout.new_foler_dialog:
			if (code == 0)
				if (args.length > 0) {
					try {
						String folerPath = (String) args[0];
						if (checkFileName(filePath2Name(folerPath)))
							getResultCode(ResultCode.FAIL_FOLDER_EXISTS)
									.doResult();
						else if (doOperationNewFoler(folerPath))
							notifyDataChanged();
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
				}
			break;
		}

	}

	private void showNewFolerDia(final Context context) {
		TextInputDialog dialog = new TextInputDialog(context,
				context.getString(R.string.operation_create_folder),
				context.getString(R.string.operation_create_folder_message),
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						String folerName = text;
						String folerPath = mDir + File.separator + folerName;

						onDialogClick(R.layout.new_foler_dialog, 0, folerPath);
						return true;
					}
				});

		dialog.show();

		dialog.setCanceledOnTouchOutside(false);
		Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveBtn.setTextColor(0xff0070c5);
		Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		negativeBtn.setTextColor(0xff0070c5);
	}

	void showMessage(String tag, String message) {
		Toast toast = Toast.makeText(this, tag + " : " + message,
				Toast.LENGTH_SHORT);
		toast.setGravity(android.view.Gravity.BOTTOM
				| android.view.Gravity.FILL_HORIZONTAL, 0, 0);
		toast.show();
	}
}
