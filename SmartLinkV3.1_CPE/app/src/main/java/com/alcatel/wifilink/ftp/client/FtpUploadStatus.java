package com.alcatel.wifilink.ftp.client;

public enum FtpUploadStatus {
	Upload_New_File_Success, 
	Upload_New_File_Failed, 
	Upload_From_Break_Success, 
	Upload_From_Break_Failed,
	Parameter_Error,
	Create_Directory_Success, 
	Create_Directory_Fail, 
	File_Exits, 
	File_Non_Exits,
	Remote_Bigger_Local, 
	Delete_Remote_Faild
}
