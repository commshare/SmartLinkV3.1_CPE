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

package com.alcatel.wifilink.fileexplorer;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.fileexplorer.FtpFileViewInteractionHub.Mode;
import com.alcatel.wifilink.mediaplayer.util.ThumbnailLoader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FtpFileListItem {
	public static void setupFileListItemInfo(Context context, View view,
			FileInfo fileInfo, FileIconHelper fileIcon,
			FtpFileViewInteractionHub fileViewInteractionHub,
			ThumbnailLoader thumbnailLoader) {

		// if in moving mode, show selected file always
		if (fileViewInteractionHub.isMoveState()) {
			fileInfo.Selected = fileViewInteractionHub
					.isFileSelected(fileInfo.filePath);
		}

		ImageView checkbox = (ImageView) view.findViewById(R.id.file_checkbox);
		if (fileViewInteractionHub.getMode() == Mode.Pick) {
			checkbox.setVisibility(View.GONE);
		}
		if (fileViewInteractionHub.getMode() == Mode.Move) {
			checkbox.setVisibility(View.GONE);
		} else {
			checkbox.setVisibility(fileViewInteractionHub.canShowCheckBox() ? View.VISIBLE
					: View.GONE);
			checkbox.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on_holo_light
					: R.drawable.btn_check_off_holo_light);
			checkbox.setTag(fileInfo);
			view.setSelected(fileInfo.Selected);
		}

		Util.setText(view, R.id.file_name, fileInfo.fileName);
		Util.setText(view, R.id.file_count, fileInfo.IsDir ? "("
				+ fileInfo.Count + ")" : "");
		Util.setText(view, R.id.modified_time,
				Util.formatDateString(context, fileInfo.ModifiedDate));
		Util.setText(view, R.id.file_size,
				(fileInfo.IsDir ? "" : Util.convertStorage(fileInfo.fileSize)));

		ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
		ImageView lFileImageFrame = (ImageView) view
				.findViewById(R.id.file_image_frame);

		lFileImageFrame.setVisibility(View.GONE);
		if (fileInfo.IsDir) {
			lFileImage.setImageResource(R.drawable.microsd_item_folder);
		} else {
//			fileIcon.setIcon(fileInfo, lFileImage);
			fileIcon.setIcon(fileInfo, lFileImage, thumbnailLoader);
		}
	}

	public static class FileItemOnClickListener implements OnClickListener {
		private Context mContext;
		private FtpFileViewInteractionHub mFileViewInteractionHub;

		public FileItemOnClickListener(Context context,
				FtpFileViewInteractionHub fileViewInteractionHub) {
			mContext = context;
			mFileViewInteractionHub = fileViewInteractionHub;
		}

		@Override
		public void onClick(View v) {
			ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
			assert (img != null && img.getTag() != null);
			FileInfo tag = (FileInfo) img.getTag();
			tag.Selected = !tag.Selected;
			// ActionMode actionMode = ((FtpFileExplorerTabActivity) mContext)
			// .getActionMode();
			ActionMode actionMode = mFileViewInteractionHub.getActionMode();

			if (actionMode == null) {
				// actionMode = ((FtpFileExplorerTabActivity) mContext)
				// .startActionMode(new ModeCallback(mContext,
				// mFileViewInteractionHub));
				// ((FtpFileExplorerTabActivity) mContext)
				// .setActionMode(actionMode);
				actionMode = mFileViewInteractionHub
						.launchActionMode(new ModeCallback(mContext,
								mFileViewInteractionHub));
				mFileViewInteractionHub.setActionMode(actionMode);
			} else {
				actionMode.invalidate();
			}
			if (mFileViewInteractionHub.onCheckItem(tag, v)) {
				img.setImageResource(tag.Selected ? R.drawable.btn_check_on_holo_light
						: R.drawable.btn_check_off_holo_light);
			} else {
				tag.Selected = !tag.Selected;
			}
			Util.updateActionModeTitle(actionMode, mContext,
					mFileViewInteractionHub.getSelectedFileList().size());
		}
	}

	public static class ModeCallback implements ActionMode.Callback {
		private Menu mMenu;
		private Context mContext;
		private FtpFileViewInteractionHub mFileViewInteractionHub;

		private void initMenuItemSelectAllOrCancel() {
			boolean isSelectedAll = mFileViewInteractionHub.isSelectedAll();
			mMenu.findItem(R.id.action_cancel).setVisible(isSelectedAll);
			mMenu.findItem(R.id.action_select_all).setVisible(!isSelectedAll);
		}

		private void scrollToSDcardTab() {
			// ActionBar bar = ((FtpFileExplorerTabActivity) mContext)
			// .getActionBar();
			ActionBar bar = mFileViewInteractionHub.obtainActionBar();
			if (bar.getSelectedNavigationIndex() != Util.SDCARD_TAB_INDEX) {
				bar.setSelectedNavigationItem(Util.SDCARD_TAB_INDEX);
			}
		}

		public ModeCallback(Context context,
				FtpFileViewInteractionHub fileViewInteractionHub) {
			mContext = context;
			mFileViewInteractionHub = fileViewInteractionHub;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = ((Activity) mContext).getMenuInflater();
			mMenu = menu;
			// TODO : 修改操作条菜单
			// inflater.inflate(R.menu.ftp_operation_menu, mMenu);
			inflater.inflate(R.menu.ftp_operation_bar_menu, mMenu);
			// TODO : 屏蔽
			// initMenuItemSelectAllOrCancel();
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO :
			boolean isVisible = (mFileViewInteractionHub.getSelectedFileList()
					.size() == 1);
			mMenu.findItem(R.id.action_rename).setVisible(isVisible);
			mMenu.findItem(R.id.action_details).setVisible(isVisible);
			mMenu.findItem(R.id.action_move).setVisible(false);
			return true;
		}

		// TODO : 被上面方法替代
		@Deprecated
		public boolean onPrepareActionMode(ActionMode mode, Menu menu,
				boolean _OLD_MARK) {
			mMenu.findItem(R.id.action_copy_path).setVisible(
					mFileViewInteractionHub.getSelectedFileList().size() == 1);
			mMenu.findItem(R.id.action_cancel).setVisible(
					mFileViewInteractionHub.isSelected());
			mMenu.findItem(R.id.action_select_all).setVisible(
					!mFileViewInteractionHub.isSelectedAll());
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_download:
				System.out.println("ftp,click download button");
				mFileViewInteractionHub.onFtpDownload();
				mode.finish();
				break;
			case 1: // TODO
				System.out.println("ftp,click upload button");
				mFileViewInteractionHub.onFtpUpload();
				mode.finish();
				break;
			case R.id.action_delete:
				mFileViewInteractionHub.onOperationDelete();
				// mFileViewInteractionHub.onFtpDelete();
				mode.finish();
				break;
			case R.id.action_copy:
				/*
				 * ((FtpFileViewActivity) ((FileExplorerTabActivity) mContext)
				 * .getFragment(Util.SDCARD_TAB_INDEX))
				 * .copyFile(mFileViewInteractionHub.getSelectedFileList());
				 * mode.finish();
				 */
				scrollToSDcardTab();
				break;
			case R.id.action_move:
				/*
				 * ((FtpFileViewActivity) ((FileExplorerTabActivity) mContext)
				 * .getFragment(Util.SDCARD_TAB_INDEX))
				 * .moveToFile(mFileViewInteractionHub.getSelectedFileList());
				 * mode.finish();
				 */
				// scrollToSDcardTab();
				mFileViewInteractionHub.onOperationMove();
				break;
			case R.id.action_send:
				mFileViewInteractionHub.onOperationSend();
				mode.finish();
				break;
			case R.id.action_copy_path:
				mFileViewInteractionHub.onOperationCopyPath();
				mode.finish();
				break;
			case R.id.action_cancel:
				mFileViewInteractionHub.clearSelection();
				initMenuItemSelectAllOrCancel();
				mode.finish();
				break;
			case R.id.action_select_all:
				mFileViewInteractionHub.onOperationSelectAll();
				initMenuItemSelectAllOrCancel();
				break;

			// TODO : 添加的菜单项
			case R.id.action_rename:
				mFileViewInteractionHub.onOperationRename();
				// mFileViewInteractionHub.onRename();
				break;
			case R.id.action_details:
				mFileViewInteractionHub.onOperationInfo();
				break;

			case R.id.action_move_to:
				mFileViewInteractionHub.onOperationMoveTo();
				break;
				
			case R.id.action_cancel_edit :
			    mFileViewInteractionHub.switchEditCheckBox();
			    break;
			}
			Util.updateActionModeTitle(mode, mContext, mFileViewInteractionHub
					.getSelectedFileList().size());
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mFileViewInteractionHub.clearSelection();
			// ((FtpFileExplorerTabActivity) mContext).setActionMode(null);
			mFileViewInteractionHub.setActionMode(null);
		}
	}
}
