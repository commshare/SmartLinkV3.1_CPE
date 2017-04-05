package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpSearchNetwork {
	public static class SearchNetwork extends BaseRequest{
		public SearchNetwork(IHttpFinishListener callback) {
			super("Network", "SearchNetwork", "4.2", callback);
		}
	}
}
