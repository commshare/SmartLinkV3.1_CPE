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
// TODO
package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

import com.alcatel.smartlinkv3.fileexplorer.FtpFileViewInteractionHub.uiCommandListener;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FtpFileOperationHelper {
	private static final String LOG_TAG = "FileOperation";

	private ArrayList<FileInfo> mCurFileNameList = new ArrayList<FileInfo>();

	private boolean mMoving;

	private IOperationProgressListener mOperationListener;

	private FilenameFilter mFilter;

	public interface IOperationProgressListener {
		void onFinish();

		void onFileChanged(String path);
	}

	public FtpFileOperationHelper(IOperationProgressListener l) {
		mOperationListener = l;
	}

	public void setFilenameFilter(FilenameFilter f) {
		mFilter = f;
	}

	private uiCommandListener mUICmdListener;

	public void setUiCommandListener(uiCommandListener UICmdListener) {
		this.mUICmdListener = UICmdListener;
	}

	public boolean FtpShowFileLists(String path) {
		if (mCurFileNameList.size() == 0)
			return false;

		final String _path = path;
		asnycExecute(new Runnable() {
			@Override
			public void run() {

			}
		});

		return true;
	}

	public boolean CreateFolder(String path, String name) {
		Log.v(LOG_TAG, "CreateFolder >>> " + path + "," + name);

		File f = new File(Util.makePath(path, name));
		if (f.exists())
			return false;

		return f.mkdir();
	}

	public void Copy(ArrayList<FileInfo> files) {
		copyFileList(files);
	}

	public boolean Paste(String path) {
		if (mCurFileNameList.size() == 0)
			return false;

		final String _path = path;
		asnycExecute(new Runnable() {
			@Override
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					CopyFile(f, _path);
				}

				mOperationListener.onFileChanged(Environment
						.getExternalStorageDirectory().getAbsolutePath());

				clear();
			}
		});

		return true;
	}

	public boolean canPaste() {
		return mCurFileNameList.size() != 0;
	}

	public void StartMove(ArrayList<FileInfo> files) {
		if (mMoving)
			return;

		mMoving = true;
		copyFileList(files);
	}

	public boolean isMoveState() {
		return mMoving;
	}

	public boolean canMove(String path) {
		for (FileInfo f : mCurFileNameList) {
			if (!f.IsDir)
				continue;

			String filePath = Util.makePath(f.filePath, f.fileName);
			if (Util.containsPath(filePath, path))
				return false;
		}

		return true;
	}

	public void clear() {
		synchronized (mCurFileNameList) {
			mCurFileNameList.clear();
		}
	}

	// TODO : 废除，被替代
    @Deprecated
	public boolean EndMove(String path, boolean OLD_MARK) {
		if (!mMoving)
			return false;
		mMoving = false;

		if (TextUtils.isEmpty(path))
			return false;

		final String _path = path;
		asnycExecute(new Runnable() {
			@Override
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					MoveFile(f, _path);
				}

				mOperationListener.onFileChanged(Environment
						.getExternalStorageDirectory().getAbsolutePath());

				clear();
			}
		});

		return true;
	}
    public void EndMove(String path) {
        final String _path = path;
        
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                for (FileInfo f : mCurFileNameList) {
                    MoveFile(f, _path);
                }

                mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory().getAbsolutePath());

                clear();
            }
        });
    }
    public boolean checkMove(String path) {
        if (!mMoving)
            return false;
        mMoving = false;
        
        if (TextUtils.isEmpty(path))
            return false;
        
        return true;
    }

	public ArrayList<FileInfo> getFileList() {
		return mCurFileNameList;
	}

	private void asnycExecute(Runnable r) {
		final Runnable _r = r;
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				synchronized (mCurFileNameList) {
					_r.run();
				}
				if (mOperationListener != null) {
					mOperationListener.onFinish();
				}

				return null;
			}
		}.execute();
	}

	public boolean isFileSelected(String path) {
		synchronized (mCurFileNameList) {
			for (FileInfo f : mCurFileNameList) {
				if (f.filePath.equalsIgnoreCase(path))
					return true;
			}
		}
		return false;
	}

	public boolean Rename(FileInfo f, String newName) {
		if (f == null || newName == null) {
			Log.e(LOG_TAG, "Rename: null parameter");
			return false;
		}

		// File file = new File(f.filePath);
		String newPath = Util.makePath(Util.getPathFromFilepath(f.filePath),
				newName);
		String fromFile = Util.makePath(Util.getPathFromFilepath(f.filePath),
				f.fileName);
		// final boolean needScan = file.isFile();
		try {
			// boolean ret = file.renameTo(new File(newPath));
			// TODO : rename file api
			Log.e(LOG_TAG, "Rename: " + f.fileName + " to " + newPath);
			Log.e(LOG_TAG, "fromFile:"+fromFile);

			mUICmdListener.rename(fromFile, newName);
			return true;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Fail to rename file," + e.toString());
		}
		return false;
	}

	// TODO : 废除，被替代
	@Deprecated
	public boolean Rename(FileInfo f, String newName, boolean _OLD_MARK) {
		if (f == null || newName == null) {
			Log.e(LOG_TAG, "Rename: null parameter");
			return false;
		}

		File file = new File(f.filePath);
		String newPath = Util.makePath(Util.getPathFromFilepath(f.filePath),
				newName);
		final boolean needScan = file.isFile();
		try {
			boolean ret = file.renameTo(new File(newPath));
			if (ret) {
				if (needScan) {
					mOperationListener.onFileChanged(f.filePath);
				}
				mOperationListener.onFileChanged(newPath);
			}
			return ret;
		} catch (SecurityException e) {
			Log.e(LOG_TAG, "Fail to rename file," + e.toString());
		}
		return false;
	}

	// TODO : 废除，被替代
	@Deprecated
	public boolean Delete(ArrayList<FileInfo> files, boolean _OLD_MARK) {
		copyFileList(files);
		asnycExecute(new Runnable() {
			@Override
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					DeleteFile(f);
				}

				mOperationListener.onFileChanged(Environment
						.getExternalStorageDirectory().getAbsolutePath());

				clear();
			}
		});
		return true;
	}

	public boolean Delete(ArrayList<FileInfo> files) {
		copyFileList(files);
		mUICmdListener.delete(files);
		clear();
		return true;
	}

	// TODO : 废除，被替代
	@Deprecated
	protected void DeleteFile(FileInfo f, boolean _OLD_MARK) {
		if (f == null) {
			Log.e(LOG_TAG, "DeleteFile: null parameter");
			return;
		}

		File file = new File(f.filePath);
		boolean directory = file.isDirectory();
		if (directory) {
			for (File child : file.listFiles(mFilter)) {
				if (Util.isNormalFile(child.getAbsolutePath())) {
					DeleteFile(Util.GetFileInfo(child, mFilter, true));
				}
			}
		}

		file.delete();

		Log.v(LOG_TAG, "DeleteFile >>> " + f.filePath);
	}

	protected void DeleteFile(FileInfo f) {
		if (f == null) {
			Log.e(LOG_TAG, "DeleteFile: null parameter");
			return;
		}

		ArrayList<FileInfo> list = new ArrayList<FileInfo>();
		list.add(f);
		mUICmdListener.delete(list);

		Log.v(LOG_TAG, "DeleteFile >>> " + f.filePath);
	}

	// TODO : 废除，被替代
	@Deprecated
	private void CopyFile(FileInfo f, String dest, boolean _OLD_MARK) {
		if (f == null || dest == null) {
			Log.e(LOG_TAG, "CopyFile: null parameter");
			return;
		}

		File file = new File(f.filePath);
		if (file.isDirectory()) {

			// directory exists in destination, rename it
			String destPath = Util.makePath(dest, f.fileName);
			File destFile = new File(destPath);
			int i = 1;
			while (destFile.exists()) {
				destPath = Util.makePath(dest, f.fileName + " " + i++);
				destFile = new File(destPath);
			}

			for (File child : file.listFiles(mFilter)) {
				if (!child.isHidden()
						&& Util.isNormalFile(child.getAbsolutePath())) {
					CopyFile(Util.GetFileInfo(child, mFilter, Settings
							.instance().getShowDotAndHiddenFiles()), destPath);
				}
			}
		} else {
			String destFile = Util.copyFile(f.filePath, dest);
		}
		Log.v(LOG_TAG, "CopyFile >>> " + f.filePath + "," + dest);
	}

	private void CopyFile(FileInfo f, String dest) {
		if (f == null || dest == null) {
			Log.e(LOG_TAG, "CopyFile: null parameter");
			return;
		}

		ArrayList<FileInfo> list = new ArrayList<FileInfo>();
		list.add(f);
		mUICmdListener.copy(list, dest);
		Log.v(LOG_TAG, "CopyFile >>> " + f.filePath + "," + dest);
	}

	// TODO : 废除，被替代
	@Deprecated
	private boolean MoveFile(FileInfo f, String dest, boolean _OLD_MARK) {
		Log.v(LOG_TAG, "MoveFile >>> " + f.filePath + "," + dest);

		if (f == null || dest == null) {
			Log.e(LOG_TAG, "CopyFile: null parameter");
			return false;
		}

		File file = new File(f.filePath);
		String newPath = Util.makePath(dest, f.fileName);
		try {
			return file.renameTo(new File(newPath));
		} catch (SecurityException e) {
			Log.e(LOG_TAG, "Fail to move file," + e.toString());
		}
		return false;
	}

	private boolean MoveFile(FileInfo f, String dest) {
		Log.v(LOG_TAG, "Move File " + f.filePath + "/" + f.fileName + " to " + dest);

		if (f == null || dest == null) {
			Log.e(LOG_TAG, "CopyFile: null parameter");
			return false;
		}

		// File file = new File(f.filePath);
		// String newPath = Util.makePath(dest, f.fileName);
		ArrayList<FileInfo> remote1 = new ArrayList<FileInfo>();
		remote1.add(f);
		mUICmdListener.move(remote1, dest);
		return true;
	}

	private void copyFileList(ArrayList<FileInfo> files) {
		synchronized (mCurFileNameList) {
			mCurFileNameList.clear();
			for (FileInfo f : files) {
				mCurFileNameList.add(f);
			}
		}
	}

}
