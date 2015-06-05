package com.alcatel.smartlinkv3.mediaplayer.util;

import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;



public class ThumbnailFileManager {

	public static String getSavePictureRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "sharing/thumbnails/";
		} else {
			return CommonUtil.getRootFilePath() + "smartlinkv3/sharing/thumbnails/";
		}
	}
	
	public static String getSaveThumbnailsFullPath(String uri) {
		return getSavePictureRootDir() + String.valueOf(uri.hashCode());
	}
	
}
