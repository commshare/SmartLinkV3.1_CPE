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


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.mediaplayer.proxy.GetMetaDataProxy;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.upnp.UpnpUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;

public class FileIconHelper {

    private static final String LOG_TAG = "FileIconHelper";
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

    static {
        addItem(new String[] {
            "mp3"
        }, R.drawable.microsd_item_music);
        addItem(new String[] {
            "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[] {
            "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[] {
            "mid"
        }, R.drawable.file_icon_mid);
        addItem(new String[] {
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf"
        }, R.drawable.microsd_item_videos);
        addItem(new String[] {
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.microsd_item_pictures);
        addItem(new String[] {
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_icon_txt);
        addItem(new String[] {
                "doc", "ppt", "docx", "pptx", "xsl", "xslx",
        }, R.drawable.file_icon_office);
        addItem(new String[] {
            "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[] {
            "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[] {
            "mtz"
        }, R.drawable.file_icon_theme);
        addItem(new String[] {
            "rar"
        }, R.drawable.file_icon_rar);
    }

    public FileIconHelper(Context context) {
    }

    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    public static int getFileIcon(String ext) {
        Integer i = fileExtToIcons.get(ext.toLowerCase());
        if (i != null) {
            return i.intValue();
        } else {
            return R.drawable.file_icon_default;
        }
    }

    public void setIcon(FileInfo fileInfo, ImageView fileImage) {
        String filename = fileInfo.fileName;
        String extFromFilename = Util.getExtFromFilename(filename);
        int id = getFileIcon(extFromFilename);
        fileImage.setImageResource(id);
    }
    
    public void setIcon(FileInfo fileInfo, ImageView fileImage, ImageView fileImageFrame,
            ThumbnailLoader thumbnailLoader) {
        String filename = fileInfo.fileName;
        String extFromFilename = Util.getExtFromFilename(filename);
        fileImageFrame.setVisibility(View.GONE);
        int id = getFileIcon(extFromFilename);
        fileImage.setImageResource(id);
        
        if (R.drawable.microsd_item_pictures != id) {
            return;
        }
        
        Uri uri = Util.uriFromFtpFile("192.168.1.1",fileInfo);
        Log.d("Icon", "ftp uri is " + uri.toString());
        final ImageView icon = fileImage;
        final ThumbnailLoader _loader = thumbnailLoader;
        GetMetaDataProxy.syncGetMetaData(fileImage.getContext(), uri.toString(), 
                new GetMetaDataProxy.GetMetaDataRequestCallback() {
            @Override
            public void onGetItemMetaData(MediaItem item) {
                if(item != null) {
                    Log.d("Icon", "item res is" + item.getRes());
                    if (UpnpUtil.isPictureItem(item)){
                        final String requestUrl = getRequestUrl(item);
                        Activity activit = (Activity) icon.getContext();
                        activit.runOnUiThread (new Runnable() {
                            public void run() {
                                _loader.DisplayImage(requestUrl, icon);
                            }
                        });
                    }
                } else
                    Log.d("Icon", "GetMetaData failed!");
            }
        });
    }
    
    private String getRequestUrl(MediaItem mi) {
        String IThumbnailWH = "?width=80,height=80";
        String requestUrl = mi.getRes();
        requestUrl = requestUrl.replace("MediaItems", "Resized");
        requestUrl = requestUrl+IThumbnailWH;
        return requestUrl;
    }

}
