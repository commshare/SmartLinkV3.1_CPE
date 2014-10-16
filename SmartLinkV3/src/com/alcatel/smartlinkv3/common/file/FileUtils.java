package com.alcatel.smartlinkv3.common.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alcatel.smartlinkv3.R;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FileUtils {
	
	public static final String invalid_file_char = "[/:\\\\<>*?|\"]";	
	
	public static final int FILENAME_MAX_LENGTH = 255;

	public static String combinePath(String path1, String path2) {
		if (path1.endsWith(File.separator))
			return path1 + path2;

		return path1 + File.separator + path2;
	}
	
	public static boolean isHidenDir(String path) {
		File file = new File(path);
		if(file.isDirectory() && file.isHidden())
		{
			return true;
		}
		
		return false;		
	}
	
	
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }
    
	public static File WriteFileFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;

		if (!isFileExist(path)) {
			createDir(path);
		}
		String fullPath = combinePath(path, fileName);

		try {

			file = new File(fullPath);
			output = new FileOutputStream(file);

			byte buffer[] = new byte[4 * 1024];

			int res = input.read(buffer);
			while (res != -1) {
				output.write(buffer, 0, res);
				res = input.read(buffer);
				Thread.sleep(1);
			}
			output.flush();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static boolean createDir(String dirName) {
		boolean bRes = true;
		File dir = new File(dirName);
		if (!dir.exists()) {
			bRes = dir.mkdirs();
		}
		
		return bRes;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
    
	public static String getMimeType(String path) {
		
		String strExt = getExtFromFilename(path);
		if(strExt == null || strExt.isEmpty())
			return "*/*";			

		String mimeType = MimeUtils.guessMimeTypeFromExtension(strExt.toLowerCase());

		return mimeType != null ? mimeType : "*/*";		
	}
	
	public static String trimLastFileSeparator(String path) {
		String strRes = path;
		if (path.endsWith(File.separator))
		{
			int nIndex = path.lastIndexOf(File.separator);
			strRes = path.substring(0, nIndex);
		}
		
		return strRes;			
	}
	
	
	public static String addLastFileSeparator(String path)
	{
		String strRes = path;
		if (!path.endsWith(File.separator))
		{
			strRes = path+File.separator;
		}
		
		return strRes;	
	}

	public static String getFileName(String path) {
		String strRes = path;
		String temp = trimLastFileSeparator(path);
		int nIndex = temp.lastIndexOf(File.separator);
		strRes = temp.substring(nIndex+1);
		return strRes;			
	}
	
	public static String getParent(String path) {
		String strRes = path;
		String temp = trimLastFileSeparator(path);
		int nIndex = temp.lastIndexOf(File.separator);
		strRes = temp.substring(0, nIndex+1);
		return strRes;			
	}
	
	public static void openFile(Context context, String path)
	{
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(path);
		
		String strMineType = FileUtils.getMimeType(path);
		intent.setDataAndType(Uri.fromFile(file), strMineType);	
		
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			String strErr = context.getString(R.string.smb_open_file_error_no_app);
			Toast.makeText(context,  strErr, Toast.LENGTH_LONG).show();			
		}			
	}
	
	public static  ArrayList<FileItem> listFiles(File dir) {
		
		ArrayList<FileItem> items  = new ArrayList<FileItem>();		
		File[] files = dir.listFiles();	
		
		for(int i = 0;i < files.length;i++) {
			if(files[i].isHidden())
				continue;
			
			FileItem item = new FileItem();			
			item.isDir = files[i].isDirectory();
			item.name = files[i].getName();
			item.path =  files[i].getAbsolutePath();
			item.parentDir = files[i].getParent();	
			item.isDownloading = false;	
			item.isSelected = false;
			items.add(item);			
		}		
		return items;		
	}
	
	public static  ArrayList<FileItem> listDirectoris(File dir) {
		
		ArrayList<FileItem> items  = new ArrayList<FileItem>();		
		File[] files = dir.listFiles();	
		
		for(int i = 0;i < files.length;i++) {
			if(files[i].isHidden() || files[i].isFile())
				continue;
			
			FileItem item = new FileItem();			
			item.isDir = files[i].isDirectory();
			item.name = files[i].getName();
			item.path =  files[i].getAbsolutePath();
			item.parentDir = files[i].getParent();	
			item.isDownloading = false;	
			item.isSelected = false;
			items.add(item);			
		}		
		return items;		
	}
	
	public static boolean isInvalidFileName(String name)
	{
		Pattern pattern = Pattern.compile(invalid_file_char);
		Matcher matcher = pattern.matcher(name);
		return matcher.find();
	}
	
	public static void deleteFile(String path) {
		File file = new File(path);
		if (file != null) {		
			if (file.isDirectory()) {			
				File[] files = file.listFiles();

				for (File f : files) {
					deleteFile(f.getAbsolutePath());
				}
			}
			file.delete();
		}
	}
}
