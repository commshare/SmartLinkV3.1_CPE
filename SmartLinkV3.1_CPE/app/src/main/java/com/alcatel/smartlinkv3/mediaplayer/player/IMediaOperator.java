package com.alcatel.smartlinkv3.mediaplayer.player;


public interface IMediaOperator {
	public void exit();
	public void replay();
	public void pause();
	public void stop();
	public void prev();
	public boolean next();
	public void skipTo(int time);
}
