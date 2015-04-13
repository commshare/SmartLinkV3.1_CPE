package com.alcatel.smartlinkv3.mediaplayer;

import java.util.HashMap;
import java.util.Map;

public interface ItatisticsEvent {

	public void onEvent(String eventID);
	public void onEvent(String eventID, HashMap<String, String> map);
}
