package com.alcatel.smartlinkv3.ftp.client;

public interface FtpManagerIRetrieveListener {
	public void onStart();
	public void onTrack(long nowOffset);
	public void onStatus(Object obj, int type);
	public void onCancel(Object obj);
	public void onDone();
}