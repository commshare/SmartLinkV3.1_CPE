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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.mediaplayer.util.ThumbnailLoader;



public class FtpFileListAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;

    private FtpFileViewInteractionHub mFileViewInteractionHub;

    private FileIconHelper mFileIcon;

    private Context mContext;
    
    private ThumbnailLoader thumbnailLoader;
    
    public FtpFileListAdapter(Context context, int resource,
            List<FileInfo> objects, FtpFileViewInteractionHub f,
            FileIconHelper fileIcon) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mContext = context;
        
        thumbnailLoader = new ThumbnailLoader(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.ftp_file_browser_item, parent, false);
        }

        FileInfo lFileInfo = mFileViewInteractionHub.getItem(position);
        FtpFileListItem.setupFileListItemInfo(mContext, view, lFileInfo,
                mFileIcon, mFileViewInteractionHub,
                thumbnailLoader);
        
     // TODO: 修改file checbox 可见度
        if (mFileViewInteractionHub.canEditCheckBox()) {
        	view.findViewById(R.id.file_checkbox_area).setVisibility(View.VISIBLE);
        	view.findViewById(R.id.file_checkbox_area).setOnClickListener(
        			new FtpFileListItem.FileItemOnClickListener(mContext, mFileViewInteractionHub));
    	} else {
    		view.findViewById(R.id.file_checkbox_area).setVisibility(View.GONE);
    	}	
        return view;
    }
}
