package com.alcatel.wifilink.mediaplayer.picture;

import com.alcatel.wifilink.mediaplayer.util.CommonUtil;



public class FileManager {

	public static String getSaveRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "dlnaicons/";
		} else {
			return CommonUtil.getRootFilePath() + "smartlinkv3/mediaplayer/dlnaicons/";
		}
	}
	
	public static String getSaveFullPath(String uri) {
		return getSaveRootDir() + getFormatUri(uri);
	}
	

	public static String getFormatUri(String uri)
	{
		uri  = uri.replace("/", "_");
		uri  = uri.replace(":", "");	
		uri  = uri.replace("?", "_");
		uri  = uri.replace("%", "_");	
		
		int length = uri.length();
		if (length > 150)
		{
			uri = uri.substring(length - 150);
		}
		
		
		return uri;
	}
	
}
