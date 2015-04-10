package com.alcatel.smartlinkv3.common.file;

public class FileModel {

	public static enum FileType {
		Audio, Image, Video, Text, Unknown
	}	

	public static FileType getFileType(String path) {
		FileType type = FileType.Unknown;

		String strMineType = FileUtils.getMimeType(path);
		if(strMineType.startsWith("audio/"))
		{
			type = FileType.Audio;
		}
		else if(strMineType.startsWith("image/"))
		{
			type = FileType.Image;
		}
		else if(strMineType.startsWith("video/"))
		{
			type = FileType.Video;
		}else if(strMineType.startsWith("text/")) {
			type = FileType.Text;
		}else {
			type = FileType.Unknown;
		}
	
		return type;
	}

}
