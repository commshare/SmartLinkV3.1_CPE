package com.alcatel.smartlinkv3.mediaplayer.player;

import android.content.Context;

public class SingleSecondTimer extends AbstractTimer{

	public SingleSecondTimer(Context context) {
		super(context);
		setTimeInterval(1000);
	}

}