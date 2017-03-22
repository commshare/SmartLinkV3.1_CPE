package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpSearchNetwork {
	public static class SearchNetwork extends BaseRequest{
		public SearchNetwork(IHttpFinishListener callback) {
			super("SearchNetwork", "4.2", callback);
		}
	}
}
