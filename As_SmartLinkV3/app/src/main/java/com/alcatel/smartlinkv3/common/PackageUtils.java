package com.alcatel.smartlinkv3.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.alcatel.smartlinkv3.common.file.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

public class PackageUtils {	
	private Context m_context;
	private static final String APK_NAME = "TCloudClientMobile.apk";
	private static final String PKG_NAME = "com.tcl.tcloudclientmobile";
	
	public PackageUtils(Context context) {		
		m_context = context;
	} 
	
	public void checkPrivateCloudApp() {		
		if (!isPrivateCloudAppExist()) {
			InputStream inputStream;
			String strStorage = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			try {
				inputStream = m_context.getAssets().open(APK_NAME);
				FileUtils.WriteFileFromInput(strStorage, APK_NAME,
						inputStream);				
				autoInstall(FileUtils.combinePath(strStorage, APK_NAME));
			} catch (IOException e) {			
				e.printStackTrace();
			}
		} 
	}

	private boolean isPrivateCloudAppExist() {
		List<PackageInfo> list = m_context.getPackageManager().getInstalledPackages(
				0);
		for (PackageInfo packageInfo : list) {
			if (packageInfo.packageName.equals(PKG_NAME)) {				
				return true;
			}
		}
		return false;
	}

	private void autoInstall(String filePath) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(filePath)),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		m_context.startActivity(intent);
	}

	public String getArchiveApkVersion(String filePath) {	
		PackageManager pm = m_context.getPackageManager();		
		PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);			
		return info.versionName;
	}

}
