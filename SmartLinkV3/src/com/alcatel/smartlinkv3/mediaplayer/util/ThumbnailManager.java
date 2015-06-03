package com.alcatel.smartlinkv3.mediaplayer.util;

import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;



public class ThumbnailManager {

	public static String getSavePictureRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "sharing/thumbnails/picture/";
		} else {
			return CommonUtil.getRootFilePath() + "smartlinkv3/sharing/thumbnails/picture/";
		}
	}
	
	public static String getSavePictureFullPath(String uri) {
		return getSavePictureRootDir() + getFormatUri(uri);
	}
	
	public static String getSaveMusicRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "sharing/music/thumbnails/";
		} else {
			return CommonUtil.getRootFilePath() + "smartlinkv3/sharing/music/thumbnails/";
		}
	}
	
	public static String getSaveMusicFullPath(String uri) {
		return getSaveMusicRootDir() + getFormatUri(uri);
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
