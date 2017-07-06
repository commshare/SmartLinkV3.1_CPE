package com.alcatel.wifilink.ftp.client;

public interface FtpTransferIRetrieveListener {
	public void onStart(String filePath);

	public void onTrack(String filePath,long nowOffset);

	public void onError(Object obj, int type);

	public void onCancel(Object obj);

	public void onDone();
}
