package com.alcatel.smartlinkv3.samba;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.NumberFormat;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.file.FileModel;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.common.file.FileModel.FileType;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SmbUtils {

	public static int port = 9011;
	public static NtlmPasswordAuthentication AUTH = null;

	public static final int SMB_MSG_TASK_FINISH = 0;
	public static final int SMB_MSG_TASK_ERROR = 1;
	public static final int SMB_MSG_FILE_EXISTS = 2;
	public static final int SMB_MSG_TASK_UPDATE = 3;	
	
	//options
	public static final String SMB_OPT_FILES_PATH = "com.alcatel.cpe.samba.smbutils.files.path";
	public static final String SMB_OPT_FILES_PROGRESS = "com.alcatel.cpe.samba.smbutils.files.progress";
	
	//msg
	public static final String SMB_MSG_UPLOAD_FILES_FINISH = "com.alcatel.cpe.samba.smbutils.uploadfiles.finish";
	public static final String SMB_MSG_UPLOAD_FILES_ERROR = "com.alcatel.cpe.samba.smbutils.uploadfiles.error";
	public static final String SMB_MSG_UPLOAD_FILES_UPDATE = "com.alcatel.cpe.samba.smbutils.uploadfiles.update";
	
	public static final String SMB_MSG_DOWNLOAD_FILES_FINISH = "com.alcatel.cpe.samba.smbutils.downloadfiles.finish";
	public static final String SMB_MSG_DOWNLOAD_FILES_ERROR = "com.alcatel.cpe.samba.smbutils.downloadfiles.error";
	public static final String SMB_MSG_DOWNLOAD_FILES_UPDATE = "com.alcatel.cpe.samba.smbutils.downloadfiles.update";
	
	public static final String SMB_MSG_REFRESH_FILES = "com.alcatel.cpe.samba.smbutils.refreshfiles";

	public static enum LIST_FILE_MODEL {
		ALL_FILE, DIR_ONLY, FILE_ONLY
	}
	
	
	public static final int BUFFER_SIZE = 8192;	
	public static final int UPDATE_BUFFER_SIZE = 8192*100;
	
	
	public static final String SMB_TEMP_DIR = ".smartrouter/";  
	public static final String LOCAL_TEMP_DIR = ".smartrouter/";  

	public static void openFile(Context context, String smbPath) {

		if (FileModel.getFileType(smbPath) == FileType.Unknown) {
			String strErr = context.getString(R.string.smb_error_open_unknown_file);
			Toast.makeText(context,  strErr, Toast.LENGTH_LONG).show();		
		}
		else
		{
			String path = smbPath;
			String httpReq = "http://" + CommonUtil.getIp(context) + ":" + port
					+ "/smb=";

			path = path.substring(6);
			try {
				path = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String url = httpReq + path;
			Log.e("", "url: " + url);
			Intent it = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(url);
			it.setDataAndType(uri, FileUtils.getMimeType(smbPath));

			try {
				context.startActivity(it);
			} catch (ActivityNotFoundException e) {
				String strErr = context.getString(R.string.smb_open_file_error_no_app);
				Toast.makeText(context,  strErr, Toast.LENGTH_LONG).show();			
			}	
		}
	}

	public static String trimSambaUrl(String path) {
		String strRes = path;
		String strRoot = BusinessMannager.getInstance().getSambaSettings().AccessPath;
		if (path.indexOf(strRoot) == 0) {
			strRes = path.substring(strRoot.length() - 1);
		}
		return strRes;
	}

	public static boolean createDir(String path) {
		boolean bRes = false;
		SmbFile smbFile;
		try {
			smbFile = new SmbFile(path, SmbUtils.AUTH);
			try {
				if (!smbFile.exists()) {
					smbFile.mkdirs();
				}
				bRes = true;
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: createDir: "+ e.getMessage());	
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: createDir: "+ e.getMessage());	
			e.printStackTrace();
		}
		return bRes;
	}
	
	public static boolean deleteFile(String file) {
		HttpAccessLog.getInstance().writeLogToFile("smaba  deleteFile: path = "+ file);	
		boolean bRes = false;
		try {
			SmbFile smbFile = new SmbFile(file, SmbUtils.AUTH);
			try {
				smbFile.delete();	
				bRes = true;
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: deleteFile: "+ e.getMessage());			
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {	
			HttpAccessLog.getInstance().writeLogToFile("Samba error: deleteFile: "+ e.getMessage());	
			e.printStackTrace();
		}
		
		return bRes;
	}	
	
	public static String getPercentString(long size, long totalSize)
	{
		NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(0);
        return format.format((double)size / (double) totalSize);       
	}
	
	public static int getPercentNumber(long size, long totalSize)
	{		
       return (int) (((double)size / (double) totalSize)*100);       
	}
	
	public static void showErrorMsg(int errCode)
	{
		Context context =  SmartLinkV3App.getInstance().getApplicationContext();
		int resId = SmbError.getErrorDescription(errCode);
		if(resId != SmbError.ERROR_RES){
			String strErr = context.getString(resId);		
			Toast.makeText(context, strErr, Toast.LENGTH_LONG).show();
		}		
	}
}
