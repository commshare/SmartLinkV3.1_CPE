package com.alcatel.smartlinkv3.mediaplayer.util;

import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;



public class ThumbnailFileManager {

	public static String getSaveThumbnailRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "sharing/thumbnails/";
		} else {
			return CommonUtil.getRootFilePath() + "smartlinkv3/sharing/thumbnails/";
		}
	}
	
	public static String getSaveThumbnailsFullPath(String uri) {
		return getSaveThumbnailRootDir() + String.valueOf(uri.hashCode());
	}
	
}
