package com.alcatel.smartlinkv3.common.file;

public class FileItem {
	
	public String name;	
	public String path;	
	public String parentDir;
	public boolean isDir;
	public boolean isDownloading;	
	public boolean isSelected;
	public boolean isUploading;
	public int  percent;

	public FileItem() {	
		name = "";	
		path = "";
		parentDir = "";
		isDir = false;
		isDownloading = false;
		isSelected = false;
		percent = 0;
	}
}
