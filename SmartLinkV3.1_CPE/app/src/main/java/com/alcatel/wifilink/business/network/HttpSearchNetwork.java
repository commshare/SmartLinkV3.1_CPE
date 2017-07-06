package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpSearchNetwork {
	public static class SearchNetwork extends BaseRequest{
		public SearchNetwork(IHttpFinishListener callback) {
			super("Network", "SearchNetwork", "4.2", callback);
			setBroadcastAction(MessageUti.NETWORK_SEARCH_NETWORK_REQUSET);
		}
	}
}
