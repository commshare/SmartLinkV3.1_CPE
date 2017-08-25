package com.alcatel.wifilink.provider;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.alcatel.wifilink.provider.MediaProvider.MediaTable.*;

import com.alcatel.wifilink.fileexplorer.FileInfo;
import com.alcatel.wifilink.fileexplorer.MediaFile;
import com.alcatel.wifilink.fileexplorer.MediaFile.MediaFileType;
import com.alcatel.wifilink.provider.MediaProvider.MediaTable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class MediaProviderUtils {    
    private static final String TAG = "MediaProviderUtils";
        
    public static Uri insert(ContentResolver resolver, String path, String name,
    		long modified_time, long size, String sender, boolean isNew) {
		ContentValues values = new ContentValues();
		Bitmap bm = null;
		values.put(MediaTable.COLUMN_NAME_PATH, path);		
		values.put(MediaTable.COLUMN_NAME_DATE, modified_time);		
		values.put(MediaTable.COLUMN_NAME_SIZE, size);		
		values.put(MediaTable.COLUMN_NAME_NAME, name);
		values.put(MediaTable.COLUMN_NAME_SENDER, sender);
		values.put(MediaTable.COLUMN_NAME_NEW, isNew);
		MediaFileType type = MediaFile.getFileType(path);
		
//		switch(type.fileType)
//		{		
//		case Music:
//			bm = FileThumbUtils.getMusicThumbnail(path);
//			break;
//		case Photo:
//			bm = FileThumbUtils.getPhotoThumb(path);
//			break;
//		case Video:
//			bm = FileThumbUtils.getVideoThumbnail(path);
//			break;
//			
//		default:			
//			break;			
//		}
		if (bm != null) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      bm.compress(CompressFormat.PNG, 100, os);

      byte[] array = os.toByteArray();			
			//ByteBuffer dst = ByteBuffer.allocate(bm);
			// bm.copyPixelsToBuffer(dst);
			values.put(COLUMN_NAME_THUMBNAIL, array);
		}
		
		values.put(MediaTable.COLUMN_NAME_MIME, type.mimeType);
		values.put(MediaTable.COLUMN_NAME_MIME, MediaFile.getFileType(path).mimeType);
		return resolver.insert(MediaTable.CONTENT_URI, values);		
    }
    
    public static Uri insert(ContentResolver resolver, FileInfo item,
    		 String sender,  boolean isNew) {
		return insert(resolver, item.filePath, item.fileName, item.ModifiedDate,
				item.fileSize, sender, isNew);
    }
    
    public static Uri insert(ContentResolver resolver, File file, 
    		 String sender, boolean isNew) {
		return insert(resolver, file.getAbsolutePath(), file.getName(),
				file.lastModified(), file.length(), sender, isNew);
    }
    
    /*
     * remove new attribute from the file. 
     * FileProviderUtils.consumeFile(m_resolver, "/mnt/sdcard/tcloudboa/Video/starwar.rm");
     */
	public static int consumeFile(ContentResolver resolver, String path) {
		Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
		builder.appendEncodedPath(MediaTable.COLUMN_NAME_PATH);
		ContentUris.appendId(builder, MediaProvider.FILE_PATH);
		Uri uri = builder.build();

		ContentValues values = new ContentValues();
		values.put(MediaTable.COLUMN_NAME_NEW, false);

		return resolver.update(uri, values, MediaTable.COLUMN_NAME_PATH + "='"
				+ path + "'", null);
    }
	
	public static int removeFile(ContentResolver resolver, String path) {
		Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
		builder.appendEncodedPath(MediaTable.COLUMN_NAME_PATH);
		ContentUris.appendId(builder, MediaProvider.FILE_PATH);
		Uri uri = builder.build();

		int removed = resolver.delete(uri, MediaTable.COLUMN_NAME_PATH + "='"
				+ path + "'", null);
		//TODO::physical delete:
		return removed;
    }	

	public static Bitmap getFileThumbnail(ContentResolver resolver, String path) {
		Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
		Uri uri = builder.build();

		Cursor cursor = resolver.query(uri,
				new String[] {COLUMN_NAME_THUMBNAIL},
				MediaTable.COLUMN_NAME_PATH + "='" + path + "'",
				(String[]) null, (String) null);
		if (!cursor.moveToFirst()) {
			return null;
		}
		
		if (cursor.getCount() > 1) {
		}

		int column = cursor.getColumnIndex(MediaTable.COLUMN_NAME_THUMBNAIL);
		byte[] thumbnail = cursor.getBlob(column);
		cursor.close();
		return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
    }
	

	public static List<FileInfo> getFilesByPath(ContentResolver resolver, String path) {
    	StringBuilder selection = new StringBuilder(MediaTable.COLUMN_NAME_PATH);
        Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
        Uri uri = builder.build();        
        selection.append(" LIKE '");
        selection.append(path);
        selection.append("%' ");        
        return getFiles(resolver, uri, selection.toString());		
	}

	/*
	public static List<FileInfo> getFilesByType(ContentResolver resolver, FileType type) {
    	StringBuilder selection = new StringBuilder(MediaTable.COLUMN_NAME_PATH);
        Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
        //ContentUris.appendId(builder, FileProvider.SENDER_LIST);
        //builder.appendEncodedPath(Contacts.Data.CONTENT_DIRECTORY);
        Uri uri = builder.build();
        
        //we now use path column, may be MIME column is better!
        if (type == null){
        	selection.append(" LIKE '%/tcloudboa/%' ");
        } else if (type == Video) {
        	selection.append(" LIKE '%/tcloudboa/Video/%' ");
        } else if (type == Music) {
        	selection.append(" LIKE '%/tcloudboa/Music/%' "); 
        } else if (type == Document) {
        	selection.append(" LIKE '%/tcloudboa/Document/%' ");        	
        } else if (type == Photo) {
        	selection.append(" LIKE '%/tcloudboa/Photo/%' ");        	
        }
        return getFiles(resolver, uri, selection.toString(), type);
	}
	*/
	public static List<FileInfo> getFiles(ContentResolver resolver,Uri uri, String selection){
    	List<FileInfo> result = new ArrayList<FileInfo>();        
		Cursor cursor = resolver.query(uri, new String[]{COLUMN_NAME_NAME,
						COLUMN_NAME_PATH, COLUMN_NAME_SIZE, COLUMN_NAME_DATE,
						COLUMN_NAME_SENDER, COLUMN_NAME_NEW},	
						selection, (String[])null, (String)null);        
				 //FileTable.COLUMN_NAME_PATH + " LIKE '%?%' ", new String[] {"tcloudboa"},
        if(!cursor.moveToFirst()) {
        	return null;
        }
        
        int nameColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_NAME);
        int pathColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_PATH);
        int sizeColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_SIZE);
        int dateColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_DATE);
        //int senderColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_SENDER);
        //int newColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_NEW);
       
        do{
        	FileInfo file = new FileInfo();        		
        	file.filePath = cursor.getString(pathColumn);        	
        	file.fileName = cursor.getString(nameColumn);        	
        	file.fileSize = cursor.getLong(sizeColumn);        	
        	file.ModifiedDate = cursor.getLong(dateColumn);
        	//TODO::	
        	//file.author = cursor.getString(senderColumn);        	
        	//file.isNew = (cursor.getShort(newColumn) != 0);
        	
        	//type = MediaFile.getFileType(file.filePath); // we can query MIME column
        	//file.fileType = type;
        	result.add(file);
        }  while(cursor.moveToNext());
        cursor.close();
        return result;
    }
	  
    public static String[] getSenders(ContentResolver resolver) {
    	List<String> senders = new ArrayList<String>();        
        Uri.Builder builder = MediaTable.CONTENT_URI.buildUpon();
        builder.appendEncodedPath(SENDER_LIST_PATH);
        //builder = builder.fragment(COLUMN_NAME_SENDER);
        //builder = builder.query(COLUMN_NAME_SENDER);
        //ContentUris.appendId(builder, FileProvider.SENDER_LIST);
        Uri sendersUri = builder.build();
        
		Cursor cursor = resolver.query(sendersUri,
				 new String[]{MediaTable.COLUMN_NAME_SENDER},
				 null, (String[])null, (String)null);
		
        if(!cursor.moveToFirst()) {
        	return null;
        }
        int senderColumn = cursor.getColumnIndex(MediaTable.COLUMN_NAME_SENDER);
       do{
        	senders.add(cursor.getString(senderColumn));
        } while (cursor.moveToNext()); 
        cursor.close();
        return senders.toArray(new String[senders.size()]);
    }
}
