package com.alcatel.smartlinkv3.ftp.client;

public interface FtpTransferIRetrieveListener {
	public void onStart();
	public void onTrack(long nowOffset);
	public void onError(Object obj, int type);
	public void onCancel(Object obj);
	public void onDone();
}