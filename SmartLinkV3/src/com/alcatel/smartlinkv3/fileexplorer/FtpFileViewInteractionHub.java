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
import java.util.ArrayList;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.fileexplorer.FileSortHelper.SortMethod;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileCommandTask.OnCallResponse;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileListItem.ModeCallback;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileOperationHelper.IOperationProgressListener;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewFragment.IConnectedActionMode;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewFragment.SelectFilesCallback;
import com.alcatel.smartlinkv3.fileexplorer.TextInputDialog.OnFinishListener;
import com.alcatel.smartlinkv3.ui.view.PathIndicator;
import com.alcatel.smartlinkv3.ui.view.PathIndicator.OnPathChangeListener;

import android.R.drawable;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FtpFileViewInteractionHub implements IOperationProgressListener,
		IConnectedActionMode {
	private static final String LOG_TAG = "FtpFileViewInteractionHub";

	private IFileInteractionListener mFileViewListener;

	private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

	private FtpFileOperationHelper mFileOperationHelper;

	private FileSortHelper mFileSortHelper;

	private View mConfirmOperationBar;

	private ProgressDialog progressDialog;

	private Activity mActivity;

	private PathIndicator mPathIndicator;

	private IConnectedActionMode mCommectedActionMode;

	public void setConnectdActionMode(IConnectedActionMode icam) {
		this.mCommectedActionMode = icam;
	}

	@Override
	public void setActionMode(ActionMode actionMode) {
		mCommectedActionMode.setActionMode(actionMode);
	}

	@Override
	public ActionMode getActionMode() {
		return mCommectedActionMode.getActionMode();
	}

	@Override
	public ActionMode launchActionMode(ActionMode.Callback callback) {
		return mCommectedActionMode.launchActionMode(callback);
	}

	@Override
	public ActionBar obtainActionBar() {
		return mCommectedActionMode.obtainActionBar();
	}

	private uiCommandListener mUICmdListener;

	public enum Mode {
		View, Pick, Move
	};

	public enum UICmd {
		FTP_DOWNLOAD, FTP_UPLOAD, FTP_COPY, FTP_MOVE, FTP_DELETE
	}

	public void setUiCommandListener(uiCommandListener UICmdListener) {
		this.mUICmdListener = UICmdListener;
		mFileOperationHelper.setUiCommandListener(UICmdListener);
	}

	public interface uiCommandListener {
		void ui_command(UICmd cmd, String args);

		void showfiles(ArrayList<FileInfo> remote);

		void download(ArrayList<FileInfo> remote, String local);
		void pause_download();
		// TODO
		void upload(ArrayList<File> local, String remote);

		void copy(ArrayList<FileInfo> remote1, String remote2);

		void move(ArrayList<FileInfo> remote1, String remote2);

		void rename(String fromFile, String toFile);

		void delete(ArrayList<FileInfo> remote);
		
		void share(ArrayList<FileInfo> remote,OnCallResponse response);
		
		void create_folder(String remote,OnCallResponse response);
	}

	public FtpFileViewInteractionHub(IFileInteractionListener fileViewListener) {
		assert (fileViewListener != null);
		mFileViewListener = fileViewListener;

		mPathIndicator = (PathIndicator) mFileViewListener
				.getViewById(R.id.path_indicator);
		mPathIndicator.setOnPathChangeListener(new OnPathChangeListener() {
			/*
			 * User click PathIndicator to change folder.
			 */
			@Override
			public void onPathSelected(int targetFolderID,
					String targetFolderPath) {
				if (mFileViewListener.onNavigation(targetFolderPath))
					return;

				if (targetFolderPath.isEmpty()) {
					mCurrentPath = mRoot;
				} else {
					mCurrentPath = targetFolderPath;
				}
				refreshFileList();
			}

		});

		mFileListView = (ListView) mFileViewListener
				.getViewById(R.id.file_path_list);
		//mFileListView.setLongClickable(true);
		//mFileListView
		//		.setOnCreateContextMenuListener(mListViewContextMenuListener);
		mFileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});
		
		mFileListView.setOnItemLongClickListener(mOnItemLongClickListerner);

		mConfirmOperationBar = mFileViewListener
				.getViewById(R.id.moving_operation_bar);
		setupClick(mConfirmOperationBar, R.id.button_moving_confirm);
		setupClick(mConfirmOperationBar, R.id.button_moving_cancel);

		mFileOperationHelper = new FtpFileOperationHelper(this);
		mFileSortHelper = new FileSortHelper();
		mActivity = mFileViewListener.obtainActivity();
	}

	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(mActivity);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	public void sortCurrentList() {
		mFileViewListener.sortCurrentList(mFileSortHelper);
	}

	public FileSortHelper getCurrentSort() {
		return mFileSortHelper;
	}

	public boolean canShowCheckBox() {
		return mConfirmOperationBar.getVisibility() != View.VISIBLE;
	}

	private void showConfirmOperationBar(boolean show) {
		mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void addContextMenuSelectedItem() {
		if (mCheckedFileNameList.size() == 0) {
			int pos = mListViewContextMenuSelectedItem;
			if (pos != -1) {
				FileInfo fileInfo = mFileViewListener.getItem(pos);
				if (fileInfo != null) {
					mCheckedFileNameList.add(fileInfo);
				}
			}
		}
	}

	public ArrayList<FileInfo> getSelectedFileList() {
		return mCheckedFileNameList;
	}

	public boolean canPaste() {
		return mFileOperationHelper.canPaste();
	}

	// operation finish notification
	@Override
	public void onFinish() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		mFileViewListener.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showConfirmOperationBar(false);
				clearSelection();
				refreshFileList();
			}
		});
	}

	public FileInfo getItem(int pos) {
		return mFileViewListener.getItem(pos);
	}

	public boolean isInSelection() {
		return mCheckedFileNameList.size() > 0;
	}

	public boolean isMoveState() {
		return mFileOperationHelper.isMoveState()
				|| mFileOperationHelper.canPaste();
	}

	private void setupClick(View v, int id) {
		View button = (v != null ? v.findViewById(id) : mFileViewListener
				.getViewById(id));
		if (button != null)
			button.setOnClickListener(buttonClick);
	}

	private View.OnClickListener buttonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_operation_download:
				Log.v("ftp", "click download button");
				mUICmdListener.download(getSelectedFileList(), null);
				break;
			case R.id.button_operation_copy:
				Log.v("ftp", "click copy button");
				onOperationCopy();
				break;
			case R.id.button_operation_move:
				onOperationMove();
				break;
			case R.id.button_operation_send:
				onOperationSend();
				break;
			case R.id.button_operation_delete:
				Log.v("ftp", "click delete button");
				mUICmdListener.delete(getSelectedFileList());
				// onOperationDelete();
				break;
			case R.id.button_operation_cancel:
				onOperationSelectAllOrCancel();
				break;
			case R.id.button_moving_confirm:
				onOperationButtonConfirm();
				break;
			case R.id.button_moving_cancel:
				onOperationButtonCancel();
				break;
			}
		}

	};

	private void onOperationReferesh() {
		refreshFileList();
	}

	private void onOperationFavorite() {
		String path = mCurrentPath;

		if (mListViewContextMenuSelectedItem != -1) {
			path = mFileViewListener.getItem(mListViewContextMenuSelectedItem).filePath;
		}

		onOperationFavorite(path);
	}

//	private void onOperationSetting() {
//		Intent intent = new Intent(mActivity,
//				FileExplorerPreferenceActivity.class);
//		if (intent != null) {
//			try {
//				mActivity.startActivity(intent);
//			} catch (ActivityNotFoundException e) {
//				Log.e(LOG_TAG, "fail to start setting: " + e.toString());
//			}
//		}
//	}

	private void onOperationFavorite(String path) {
		FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
				.getInstance();
		if (databaseHelper != null) {
			int stringId = 0;
			if (databaseHelper.isFavorite(path)) {
				databaseHelper.delete(path);
				stringId = R.string.removed_favorite;
			} else {
				databaseHelper.insert(Util.getNameFromFilepath(path), path);
				stringId = R.string.added_favorite;
			}

			Toast.makeText(mActivity, stringId, Toast.LENGTH_SHORT).show();
		}
	}

	private void onOperationShowSysFiles() {
		Settings.instance().setShowDotAndHiddenFiles(
				!Settings.instance().getShowDotAndHiddenFiles());
		refreshFileList();
	}

	public void onOperationSelectAllOrCancel() {
		if (!isSelectedAll()) {
			onOperationSelectAll();
		} else {
			clearSelection();
		}
	}

	public void onOperationSelectAll() {
		mCheckedFileNameList.clear();
		for (FileInfo f : mFileViewListener.getAllFiles()) {
			f.Selected = true;
			mCheckedFileNameList.add(f);
		}

		ActionMode mode = getActionMode();
		if (mode == null) {
			mode = this.launchActionMode(new ModeCallback(mActivity, this));
			setActionMode(mode);
			Util.updateActionModeTitle(mode, mActivity, getSelectedFileList()
					.size());
		}
		mFileViewListener.onDataChanged();
	}

	private OnClickListener navigationClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String path = (String) v.getTag();
			assert (path != null);
			if (mFileViewListener.onNavigation(path))
				return;

			if (path.isEmpty()) {
				mCurrentPath = mRoot;
			} else {
				mCurrentPath = path;
			}
			refreshFileList();
		}

	};

	public boolean onOperationUpLevel() {

		if (mFileViewListener.onOperation(GlobalConsts.OPERATION_UP_LEVEL)) {
			return true;
		}

		if (!mRoot.equals(mCurrentPath)) {
			//mCurrentPath = new File(mCurrentPath).getParent();
			mCurrentPath = Util.getParent(mCurrentPath);
		    refreshFileList();
			return true;
		}

		return false;
	}

	public void onOperationCreateFolder() {
		TextInputDialog dialog = new TextInputDialog(mActivity,
				mActivity.getString(R.string.operation_create_folder),
				mActivity.getString(R.string.operation_create_folder_message),
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						return doCreateFolder(text);
					}
				});

		dialog.show();

		dialog.setCanceledOnTouchOutside(false);
		Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveBtn.setTextColor(0xff0070c5);
		Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		negativeBtn.setTextColor(0xff0070c5);
	}

	// TODO 修改，添加参数标记_OLD_MARK,为重用方法，被修改方法替代
	private boolean doCreateFolder(String text, boolean OLD_MRAK) {
		if (TextUtils.isEmpty(text))
			return false;

		if (mFileOperationHelper.CreateFolder(mCurrentPath, text)) {
			mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(
					mCurrentPath, text)));
			mFileListView.setSelection(mFileListView.getCount() - 1);
		} else {
			new AlertDialog.Builder(mActivity)
					.setMessage(
							mActivity.getString(R.string.fail_to_create_folder))
					.setPositiveButton(R.string.confirm, null).create().show();
			return false;
		}

		return true;
	}
	private boolean doCreateFolder(String text) {
        if (TextUtils.isEmpty(text))
            return false;
        
        String pathCreateFolder = Util.makePath(mCurrentPath, text);
        mUICmdListener.create_folder(pathCreateFolder, null);

        return true;
    }

	public void onOperationSearch() {

	}

	public void onSortChanged(SortMethod s) {
		if (mFileSortHelper.getSortMethod() != s) {
			mFileSortHelper.setSortMethog(s);
			sortCurrentList();
		}
	}

	public void onOperationCopy() {
		onOperationCopy(getSelectedFileList());
	}

	public void onOperationCopy(ArrayList<FileInfo> files) {
		mFileOperationHelper.Copy(files);
		clearSelection();

		showConfirmOperationBar(true);
		View confirmButton = mConfirmOperationBar
				.findViewById(R.id.button_moving_confirm);
		confirmButton.setEnabled(false);
		// refresh to hide selected files
		refreshFileList();
	}

	public void onOperationCopyPath() {
		if (getSelectedFileList().size() == 1) {
			copy(getSelectedFileList().get(0).filePath);
		}
		clearSelection();
	}

	private void copy(CharSequence text) {
		ClipboardManager cm = (ClipboardManager) mActivity
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(text);
	}

	private void onOperationPaste() {
		if (mFileOperationHelper.Paste(mCurrentPath)) {
			showProgress(mActivity.getString(R.string.operation_pasting));
		}
	}

	public void onOperationMove() {
		mFileOperationHelper.StartMove(getSelectedFileList());
		clearSelection();
		showConfirmOperationBar(true);
		View confirmButton = mConfirmOperationBar
				.findViewById(R.id.button_moving_confirm);
		confirmButton.setEnabled(false);
		// refresh to hide selected files
		refreshFileList();
	}
	
	public void onOperationMoveTo() {
	    if (getSelectedFileList().size() == 1) {
	        String title = getSelectedFileList().get(0).fileName;
	        String path = Util.makePath(getSelectedFileList().get(0).filePath, title);
	        String files[] = new String[] {path};
	        IntentBuilder.goExFileDialog(mActivity, title, files);
	    } else if (getSelectedFileList().size() > 1) {
	        String title = mActivity.getString(R.string.move_to_title);
	        ArrayList<String> listFiles = new ArrayList<String>();
	        for (FileInfo info : getSelectedFileList()) {
	            String path = Util.makePath(info.filePath, info.fileName);
	            listFiles.add(path);
	        }
	        String files[] = listFiles.toArray(new String[listFiles.size()]);
	        IntentBuilder.goExFileDialog(mActivity, title, files);
	    }
	}
	
	public void operationMoveTo(String path) {
        mUICmdListener.move(getSelectedFileList(), path);
        clearSelection();
	}

	public void refreshFileList() {
		clearSelection();
		// onRefreshFileList returns true indicates list has changed
		mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
		// update move operation button state
		updateConfirmButtons();
		mPathIndicator.setPath(mCurrentPath);
	}

	private void updateConfirmButtons() {
		if (mConfirmOperationBar.getVisibility() == View.GONE)
			return;

		Button confirmButton = (Button) mConfirmOperationBar
				.findViewById(R.id.button_moving_confirm);
		int text = R.string.operation_paste;
		if (isSelectingFiles()) {
			confirmButton.setEnabled(mCheckedFileNameList.size() != 0);
			text = R.string.operation_send;
		} else if (isMoveState()) {
			confirmButton
					.setEnabled(mFileOperationHelper.canMove(mCurrentPath));
		}

		confirmButton.setText(text);
	}

	// TODO 修改，添加参数标记_OLD_MARK,为重用方法，被修改方法替代
    @Deprecated
	public void onOperationSend(boolean _OLD_MARK) {
		ArrayList<FileInfo> selectedFileList = getSelectedFileList();
		for (FileInfo f : selectedFileList) {
			if (f.IsDir) {
				AlertDialog dialog = new AlertDialog.Builder(mActivity)
						.setMessage(R.string.error_info_cant_send_folder)
						.setPositiveButton(R.string.confirm, null).create();
				dialog.show();
				return;
			}
		}		

		Intent intent = IntentBuilder.buildSendFile(selectedFileList);
		if (intent != null) {
			try {
				mFileViewListener.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.e(LOG_TAG, "fail to view file: " + e.toString());
			}
		}
		clearSelection();
	}
    public void onOperationSend() {
        ArrayList<FileInfo> selectedFileList = getSelectedFileList();
        for (FileInfo f : selectedFileList) {
            if (f.IsDir) {
                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setMessage(R.string.error_info_cant_send_folder)
                        .setPositiveButton(R.string.confirm, null).create();
                dialog.show();
                return;
            }
        }
        Log.d("Send", selectedFileList.toString());
        Log.d("Send", String.valueOf(selectedFileList.size()));
        mUICmdListener.share(selectedFileList, new FtpFileCommandTask.OnCallResponse() {           
            @Override
            public void callResponse(Object obj) {
                Log.d("Send", "response share.");
                try {
                    @SuppressWarnings("unchecked")
                    final ArrayList<ShareFileInfo> list = (ArrayList<ShareFileInfo>) obj;
                    
                    mFileViewListener.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           Intent intent = IntentBuilder.buildShareFile(list);
                            if (intent != null) {
                                try {
                                    mFileViewListener.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Log.e(LOG_TAG, "fail to view file: " + e.toString());
                                }
                            }
                        }
                    });
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
                clearSelection();
            }
        });
    }

	public void onOperationRename() {
		// TODO : 去掉上下文菜单选择判断
		// int pos = mListViewContextMenuSelectedItem;
		// if (pos == -1)
		// return;

		if (getSelectedFileList().size() == 0)
			return;

		final FileInfo f = getSelectedFileList().get(0);
		clearSelection();

		TextInputDialog dialog = new TextInputDialog(mActivity,
				mActivity.getString(R.string.operation_rename), null,
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						return doRename(f, text);
					}
				});

		dialog.show();

		dialog.setCanceledOnTouchOutside(false);
		Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveBtn.setTextColor(0xff0070c5);
		Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		negativeBtn.setTextColor(0xff0070c5);
	}

	// TODO : 
	private boolean doRename(final FileInfo f, String text, boolean _OLD_MARK) {
		if (TextUtils.isEmpty(text))
			return false;

		if (mFileOperationHelper.Rename(f, text)) {
			f.fileName = text;
			mFileViewListener.onDataChanged();
		} else {
			new AlertDialog.Builder(mActivity)
					.setMessage(mActivity.getString(R.string.fail_to_rename))
					.setPositiveButton(R.string.confirm, null).create().show();
			return false;
		}

		return true;
	}
	private boolean doRename(final FileInfo f, String text) {
        if (TextUtils.isEmpty(text))
            return false;
        Log.d("fileexplorer", "rename : old file is " + f.filePath + " new file is " + text);
        String fromFile = Util.makePath(f.filePath, f.fileName);
        String toFile = Util.makePath(f.filePath, text);
        mUICmdListener.rename(fromFile, toFile);

        return true;
    }

	private void notifyFileSystemChanged(String path) {
		if (path == null)
			return;
		// TODO
		final File f = new File(path);

		final Intent intent;
		if (f.isDirectory()) {
			intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
			intent.setClassName("com.android.providers.media",
					"com.android.providers.media.MediaScannerReceiver");
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			Log.v(LOG_TAG,
					"directory changed, send broadcast:" + intent.toString());
		} else {
			intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(Uri.fromFile(new File(path)));
			Log.v(LOG_TAG, "file changed, send broadcast:" + intent.toString());
		}
		mActivity.sendBroadcast(intent);
	}

	public void onFtpDownload() {
		mUICmdListener.download(getSelectedFileList(), null);
	}

	public void onFtpUpload() {
		mUICmdListener.upload(null, null);
	}

	public void onRename() {
		mUICmdListener.rename(null, null);
	}

	public void onShare() {
		mUICmdListener.share(null, null);
	}
	
	public void onMove() {
		mUICmdListener.move(null, null);
	}
	
	public void onCreateFolder() {
		mUICmdListener.create_folder(null, null);
	}

	public void onFtpDelete(final ArrayList<FileInfo> list) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.custom_delete_dlg, null);
		builder.setView(view);

		builder.setPositiveButton(mActivity.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mUICmdListener.delete(list);
						Log.d(LOG_TAG, "selected file list size is " + String.valueOf(list.size()));
						dialog.dismiss();
						
					}
				});
		builder.setNegativeButton(mActivity.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = builder.show();

		alertDialog.setCanceledOnTouchOutside(false);
		Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveBtn.setTextColor(0xff0070c5);
		Button negativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		negativeBtn.setTextColor(0xff0070c5);
	}

	public void onOperationDelete() {
		//doOperationDelete(getSelectedFileList());
	    ArrayList<FileInfo> list = new ArrayList<FileInfo>(getSelectedFileList());
	    Log.d(LOG_TAG, "selected file list size is " + String.valueOf(list.size()));
	    onFtpDelete(list);
	    clearSelection();
	}

	public void onOperationDelete(int position) {
		FileInfo file = mFileViewListener.getItem(position);
		if (file == null)
			return;

		ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
		selectedFileList.add(file);
		doOperationDelete(selectedFileList);
	}

	private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
		final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(
				selectedFileList);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.custom_delete_dlg, null);
		builder.setView(view);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mUICmdListener.delete(selectedFiles);
				dialog.dismiss();
				clearSelection();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						clearSelection();
					}
				});
		AlertDialog alertDialog = builder.show();

		Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveBtn.setTextColor(0xff0070c5);
		Button negativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		negativeBtn.setTextColor(0xff0070c5);
	}

	public void onOperationInfo() {
		if (getSelectedFileList().size() == 0)
			return;

		FileInfo file = getSelectedFileList().get(0);
		if (file == null)
			return;

		InformationDialog dialog = new InformationDialog(mActivity, file,
				mFileViewListener.getFileIconHelper());
		dialog.show();

		dialog.setCanceledOnTouchOutside(false);
		Button neutralBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		neutralBtn.setTextColor(0xff0070c5);
		clearSelection();
	}
	
	public void onOperationAddFile() {
	    ExternalFileObtain.getInstance(mActivity).getFiles(
            new ExternalFileObtain.OnGetFilesListener() {
                @Override
                public void onGetFiles(ArrayList<File> list) {
                    // TODO Auto-generated method stub
                    Log.i("ADD_FILE", "upload file" + list.toString() + " to " + mCurrentPath);
                    mUICmdListener.upload(list, mCurrentPath);
                }
            });
	}

	public void onOperationButtonConfirm() {
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(mCheckedFileNameList);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			//if (mFileOperationHelper.EndMove(mCurrentPath)) {
			//	showProgress(mActivity.getString(R.string.operation_moving));
			//}
			if (mFileOperationHelper.checkMove(mCurrentPath)) {
			    showProgress(mActivity.getString(R.string.operation_moving));
			    mFileOperationHelper.EndMove(mCurrentPath);
			}
		} else {
			onOperationPaste();
		}
	}

	public void onOperationButtonCancel() {
		mFileOperationHelper.clear();
		showConfirmOperationBar(false);
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(null);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			// refresh to show previously selected hidden files
			mFileOperationHelper.EndMove(null);
			refreshFileList();
		} else {
			refreshFileList();
		}
	}

	// context menu
	private OnCreateContextMenuListener mListViewContextMenuListener = new OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			if (isInSelection() || isMoveState())
				return;
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

			FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
					.getInstance();
			FileInfo file = mFileViewListener.getItem(info.position);
			if (databaseHelper != null && file != null) {
				int stringId = databaseHelper.isFavorite(file.filePath) ? R.string.operation_unfavorite
						: R.string.operation_favorite;
				addMenuItem(menu, GlobalConsts.MENU_FAVORITE, 0, stringId);
			}

			addMenuItem(menu, GlobalConsts.MENU_COPY, 0,
					R.string.operation_copy);
			addMenuItem(menu, GlobalConsts.MENU_COPY_PATH, 0,
					R.string.operation_copy_path);
			// addMenuItem(menu, GlobalConsts.MENU_PASTE, 0,
			// R.string.operation_paste);
			addMenuItem(menu, GlobalConsts.MENU_MOVE, 0,
					R.string.operation_move);
			addMenuItem(menu, MENU_SEND, 0, R.string.operation_send);
			addMenuItem(menu, MENU_RENAME, 0, R.string.operation_rename);
			addMenuItem(menu, MENU_DELETE, 0, R.string.operation_delete);
			addMenuItem(menu, MENU_INFO, 0, R.string.operation_info);

			if (!canPaste()) {
				MenuItem menuItem = menu.findItem(GlobalConsts.MENU_PASTE);
				if (menuItem != null)
					menuItem.setEnabled(false);
			}
		}
	};

	// File List view setup
	private ListView mFileListView;
	private int mListViewContextMenuSelectedItem;

	Dialog fileListDialog;
	ProgressBar progressBar;
	TextView progressText;

	private void initProgressBar() {
		progressBar = (ProgressBar) fileListDialog
				.findViewById(R.id.progressBar);
		progressText = (TextView) fileListDialog
				.findViewById(R.id.progressText);
	}

	private void onShowFilesDialog() {
		fileListDialog = new Dialog(mActivity);
		fileListDialog.setTitle("File Upload");

		fileListDialog.setContentView(R.layout.ftp_dialog_files);

		// fileListDialog.findViewById(R.id.getfileBtn).setOnClickListener(this);

		initProgressBar();
		// if (listView == null) {
		// listView = (ListView) fileListDialog.findViewById(R.id.list);
		// listView.setAdapter(new MyAdapter());
		// }

		ListView mFileListView = (ListView) fileListDialog
				.findViewById(R.id.file_path_list);

		mFileListView.setLongClickable(true);
		mFileListView
				.setOnCreateContextMenuListener(mListViewContextMenuListener);
		mFileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});

		fileListDialog.show();

	}

	// menu
	private static final int MENU_UPLOAD = 0;

	private static final int MENU_SEARCH = 1;

	// private static final int MENU_NEW_FOLDER = 2;
	private static final int MENU_SORT = 3;

	private static final int MENU_SEND = 7;

	private static final int MENU_RENAME = 8;

	private static final int MENU_DELETE = 9;

	private static final int MENU_INFO = 10;

	private static final int MENU_SORT_NAME = 11;

	private static final int MENU_SORT_SIZE = 12;

	private static final int MENU_SORT_DATE = 13;

	private static final int MENU_SORT_TYPE = 14;

	private static final int MENU_REFRESH = 15;

	private static final int MENU_SELECTALL = 16;

	private static final int MENU_SETTING = 17;

	private static final int MENU_EXIT = 18;

	// TODO : 编辑可选框
	private boolean makrEditCheckBox = false;

	public boolean canEditCheckBox() {
		return this.makrEditCheckBox;
	}

	private void switchEditCheckBox() {
		this.makrEditCheckBox = !this.makrEditCheckBox;
		if (!canEditCheckBox()) {
		    clearSelection();
		}
		mFileViewListener.notifyDataChanged();
	}

	private OnMenuItemClickListener menuItemClick = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mListViewContextMenuSelectedItem = info != null ? info.position
					: -1;

			int itemId = item.getItemId();
			if (mFileViewListener.onOperation(itemId)) {
				return true;
			}

			addContextMenuSelectedItem();

			switch (itemId) {
			case MENU_UPLOAD:
				// show dialog
				// choose the files
				// put the files into fileinfo
				Toast.makeText(mActivity, "ftp upload", Toast.LENGTH_SHORT)
						.show();
				onShowFilesDialog();
				break;
			case MENU_SEARCH:
				onOperationSearch();
				break;
			case GlobalConsts.MENU_NEW_FOLDER:
				onOperationCreateFolder();
				break;
			case MENU_REFRESH:
				onOperationReferesh();
				break;
			case MENU_SELECTALL:
				onOperationSelectAllOrCancel();
				break;
			case GlobalConsts.MENU_SHOWHIDE:
				onOperationShowSysFiles();
				break;
			case GlobalConsts.MENU_FAVORITE:
				onOperationFavorite();
				break;
			case MENU_EXIT:
				mActivity.finish();
				break;
			// sort
			case MENU_SORT_NAME:
				item.setChecked(true);
				onSortChanged(SortMethod.name);
				break;
			case MENU_SORT_SIZE:
				item.setChecked(true);
				onSortChanged(SortMethod.size);
				break;
			case MENU_SORT_DATE:
				item.setChecked(true);
				onSortChanged(SortMethod.date);
				break;
			case MENU_SORT_TYPE:
				item.setChecked(true);
				onSortChanged(SortMethod.type);
				break;

			case GlobalConsts.MENU_COPY:
				onOperationCopy();
				break;
			case GlobalConsts.MENU_COPY_PATH:
				onOperationCopyPath();
				break;
			case GlobalConsts.MENU_PASTE:
				onOperationPaste();
				break;
			case GlobalConsts.MENU_MOVE:
				onOperationMove();
				break;
			case MENU_SEND:
				onOperationSend();
				break;
			case MENU_RENAME:
				onOperationRename();
				break;
			case MENU_DELETE:
				onOperationDelete();
				break;
			case MENU_INFO:
				onOperationInfo();
				break;
			// TODO 添加菜单选项操作
			case GlobalConsts.MENU_EDIT:
				switchEditCheckBox();
				// Toast.makeText(mActivity, "Edit File.",
				// Toast.LENGTH_SHORT).show();
				break;
			case GlobalConsts.MENU_ADD_FILE:
				onOperationAddFile();
				break;

			default:
				return false;
			}

			mListViewContextMenuSelectedItem = -1;
			return true;
		}

	};

	private com.alcatel.smartlinkv3.fileexplorer.FtpFileViewInteractionHub.Mode mCurrentMode;

	private String mhost;

	private String mCurrentPath;

	private String mRoot;

	private SelectFilesCallback mSelectFilesCallback;

	// TODO 修改，添加参数标记_OLD_MARK,为重用方法，被修改方法替代
	@Deprecated
	public boolean onCreateOptionsMenu(Menu menu, boolean _OLD_MARK) {
		clearSelection();

		addMenuItem(menu, MENU_UPLOAD, 0, R.string.operation_upload,
				R.drawable.ic_menu_select_all);

		addMenuItem(menu, MENU_SELECTALL, 1, R.string.operation_selectall,
				R.drawable.ic_menu_select_all);

		SubMenu sortMenu = menu.addSubMenu(0, MENU_SORT, 2,
				R.string.menu_item_sort).setIcon(R.drawable.ic_menu_sort);
		addMenuItem(sortMenu, MENU_SORT_NAME, 0, R.string.menu_item_sort_name);
		addMenuItem(sortMenu, MENU_SORT_SIZE, 1, R.string.menu_item_sort_size);
		addMenuItem(sortMenu, MENU_SORT_DATE, 2, R.string.menu_item_sort_date);
		addMenuItem(sortMenu, MENU_SORT_TYPE, 3, R.string.menu_item_sort_type);
		sortMenu.setGroupCheckable(0, true, true);
		sortMenu.getItem(0).setChecked(true);

		// addMenuItem(menu, GlobalConsts.MENU_PASTE, 2,
		// R.string.operation_paste);
		addMenuItem(menu, GlobalConsts.MENU_NEW_FOLDER, 3,
				R.string.operation_create_folder, R.drawable.ic_menu_new_folder);
		addMenuItem(menu, GlobalConsts.MENU_FAVORITE, 4,
				R.string.operation_favorite, R.drawable.ic_menu_delete_favorite);
		addMenuItem(menu, GlobalConsts.MENU_SHOWHIDE, 5,
				R.string.operation_show_sys, R.drawable.ic_menu_show_sys);
		addMenuItem(menu, MENU_REFRESH, 6, R.string.operation_refresh,
				R.drawable.ic_menu_refresh);
		addMenuItem(menu, MENU_SETTING, 7, R.string.menu_setting,
				drawable.ic_menu_preferences);
		addMenuItem(menu, MENU_EXIT, 8, R.string.menu_exit,
				drawable.ic_menu_close_clear_cancel);
		return true;
	}

	// TODO 添加，替代旧方法
	public boolean onCreateOptionsMenu(Menu menu) {
		clearSelection();
		addDefaultMenu(menu);
		return true;
	}

	// TODO 添加，修改菜单选项
	private void addDefaultMenu(Menu menu) {
		addMenuItem(menu, MENU_SORT_NAME, 0, R.string.menu_item_sort_by_name,
				R.drawable.btn_radio_on_normal);
		addMenuItem(menu, MENU_SORT_TYPE, 1, R.string.menu_item_sort_by_type,
				R.drawable.btn_radio_on_normal);
		// menu.getItem(MENU_SORT_NAME).setChecked(true);

		addMenuItem(menu, GlobalConsts.MENU_SHOWHIDE, 2,
				R.string.operation_show_sys, R.drawable.ic_menu_show_sys);

		addMenuItem(menu, GlobalConsts.MENU_EDIT, 3, R.string.operation_edit,
				R.drawable.storage_toolbar_rename_normal);

		addMenuItem(menu, GlobalConsts.MENU_NEW_FOLDER, 4,
				R.string.operation_create_folder, R.drawable.ic_menu_new_folder);
		addMenuItem(menu, GlobalConsts.MENU_ADD_FILE, 5,
				R.string.operation_add_file, R.drawable.item_uploading_file);

	}

	private void addMenuItem(Menu menu, int itemId, int order, int string) {
		addMenuItem(menu, itemId, order, string, -1);
	}

	private void addMenuItem(Menu menu, int itemId, int order, int string,
			int iconRes) {
		if (!mFileViewListener.shouldHideMenu(itemId)) {
			MenuItem item = menu.add(0, itemId, order, string)
					.setOnMenuItemClickListener(menuItemClick);
			if (iconRes > 0) {
				item.setIcon(iconRes);
			}
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		updateMenuItems(menu);
		return true;
	}

	// TODO 修改，添加参数标记，被替代
	@Deprecated
	private void updateMenuItems(Menu menu, boolean _OLD_MARK) {
		menu.findItem(MENU_SELECTALL).setTitle(
				isSelectedAll() ? R.string.operation_cancel_selectall
						: R.string.operation_selectall);
		menu.findItem(MENU_SELECTALL).setEnabled(mCurrentMode != Mode.Pick);

		MenuItem menuItem = menu.findItem(GlobalConsts.MENU_SHOWHIDE);
		if (menuItem != null) {
			menuItem.setTitle(Settings.instance().getShowDotAndHiddenFiles() ? R.string.operation_hide_sys
					: R.string.operation_show_sys);
		}

		FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
				.getInstance();
		if (databaseHelper != null) {
			MenuItem item = menu.findItem(GlobalConsts.MENU_FAVORITE);
			if (item != null) {
				item.setTitle(databaseHelper.isFavorite(mCurrentPath) ? R.string.operation_unfavorite
						: R.string.operation_favorite);
			}
		}

	}

	// TODO 添加，替代方法
	private void updateMenuItems(Menu menu) {

		menu.findItem(MENU_SORT_NAME).setChecked(
				mFileSortHelper.getSortMethod() == SortMethod.name);
		menu.findItem(MENU_SORT_TYPE).setChecked(
				mFileSortHelper.getSortMethod() == SortMethod.type);

		MenuItem menuItem = menu.findItem(GlobalConsts.MENU_SHOWHIDE);
		boolean isHidden = Settings.instance().getShowDotAndHiddenFiles();
		menuItem.setTitle(isHidden ? R.string.operation_hide_sys
				: R.string.operation_show_sys);

	}

	public boolean isFileSelected(String filePath) {
		return mFileOperationHelper.isFileSelected(filePath);
	}

	public void setMode(Mode m) {
		mCurrentMode = m;
	}

	public Mode getMode() {
		return mCurrentMode;
	}

	public void onListItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileInfo lFileInfo = mFileViewListener.getItem(position);

		if (lFileInfo == null) {
			Log.e(LOG_TAG, "file does not exist on position:" + position);
			return;
		}

		if (isInSelection()) {
			boolean selected = lFileInfo.Selected;
			ActionMode actionMode = getActionMode();
			ImageView checkBox = (ImageView) view
					.findViewById(R.id.file_checkbox);
			if (selected) {
				mCheckedFileNameList.remove(lFileInfo);
				checkBox.setImageResource(R.drawable.btn_check_off_holo_light);
			} else {
				mCheckedFileNameList.add(lFileInfo);
				checkBox.setImageResource(R.drawable.btn_check_on_holo_light);
			}
			if (actionMode != null) {
				if (mCheckedFileNameList.size() == 0)
					actionMode.finish();
				else
					actionMode.invalidate();
			}
			lFileInfo.Selected = !selected;

			Util.updateActionModeTitle(actionMode, mActivity,
					mCheckedFileNameList.size());
			return;
		}

		if (!lFileInfo.IsDir) {
			if (mCurrentMode == Mode.Pick) {
				mFileViewListener.onPick(lFileInfo);
			} else {
				// TODO : 
				//viewFile(lFileInfo);
				viewMediaFile(lFileInfo);
			}
			return;
		}

		mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
		ActionMode actionMode = this.getActionMode();
		if (actionMode != null) {
			actionMode.finish();
		}
		
		if (canEditCheckBox())
            switchEditCheckBox();
            
		refreshFileList();
	}
	
	private OnItemLongClickListener mOnItemLongClickListerner = new OnItemLongClickListener() {
	    @Override
	    public  boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	        FtpFileListItem.FileItemOnClickListener listener = 
	                new FtpFileListItem.FileItemOnClickListener(mActivity,FtpFileViewInteractionHub.this);
	        View viewFileCheckboxArea = view.findViewById(R.id.file_checkbox_area);
	        listener.onClick(viewFileCheckboxArea);
	        if (!canEditCheckBox()) {
	            switchEditCheckBox();
	        }
	        
	        return true;
	    }
	};

	public void setRootPath(String path) {
		mRoot = path;
		mCurrentPath = path;
	}

	public String getRootPath() {
		return mRoot;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setHostTag(String host) {
		mhost = host;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	private String getAbsoluteName(String path, String name) {
		return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path
				+ File.separator + name;
	}

	// check or uncheck
	public boolean onCheckItem(FileInfo f, View v) {
		if (isMoveState())
			return false;

		if (isSelectingFiles() && f.IsDir)
			return false;

		if (f.Selected) {
			mCheckedFileNameList.add(f);
		} else {
			mCheckedFileNameList.remove(f);
		}
		return true;
	}

	private boolean isSelectingFiles() {
		return mSelectFilesCallback != null;
	}

	public boolean isSelectedAll() {
		return mFileViewListener.getItemCount() != 0
				&& mCheckedFileNameList.size() == mFileViewListener
						.getItemCount();
	}

	public boolean isSelected() {
		return mCheckedFileNameList.size() != 0;
	}

	public void clearSelection() {
		if (mCheckedFileNameList.size() > 0) {
			for (FileInfo f : mCheckedFileNameList) {
				if (f == null) {
					continue;
				}
				f.Selected = false;
			}
			mCheckedFileNameList.clear();
			mFileViewListener.onDataChanged();
		}
	}

	private void viewFile(FileInfo lFileInfo) {
		try {
		    String filePath = Util.makePath(lFileInfo.filePath, lFileInfo.fileName);
			IntentBuilder.viewFile(mActivity, filePath);
		} catch (ActivityNotFoundException e) {
			Log.e(LOG_TAG, "fail to view file: " + e.toString());
		}
	}
	
	// TODO
	private void viewMediaFile(FileInfo info) {
	    
	    if (!Util.isMediaFile(info.fileName))
	        return;
	    
	    ArrayList<FileInfo> list = new ArrayList<FileInfo>();
	    list.add(info);
	    this.mUICmdListener.share(list, new FtpFileCommandTask.OnCallResponse() {
            
            @Override
            public void callResponse(Object obj) {
                try {
                    @SuppressWarnings("unchecked")
                    ArrayList<ShareFileInfo> list = (ArrayList<ShareFileInfo>) obj;
                    ShareFileInfo info = list.get(0);
                    if (info == null) return;                    
                    final String filePath = info.filePath;
                    Log.d("view", "view file path : " + filePath);
                    mFileViewListener.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                IntentBuilder.viewMediaFile(mActivity, filePath);
                            } catch (ActivityNotFoundException e) {
                                Log.e(LOG_TAG, "fail to view file: " + e.toString());
                            }
                        }
                    });
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });    
	}

	public boolean onBackPressed() {
	    if (isInSelection()) {
            clearSelection();
        } else if (canEditCheckBox()) {
		    switchEditCheckBox();
		}  else if (!onOperationUpLevel()) {
			return false;
		}
		return true;
	}

	public void copyFile(ArrayList<FileInfo> files) {
		mFileOperationHelper.Copy(files);
	}

	public void moveFileFrom(ArrayList<FileInfo> files) {
		mFileOperationHelper.StartMove(files);
		showConfirmOperationBar(true);
		updateConfirmButtons();
		// refresh to hide selected files
		refreshFileList();
	}

	@Override
	public void onFileChanged(String path) {
		// notifyFileSystemChanged(path);
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mSelectFilesCallback = callback;
		showConfirmOperationBar(true);
		updateConfirmButtons();
	}

}
