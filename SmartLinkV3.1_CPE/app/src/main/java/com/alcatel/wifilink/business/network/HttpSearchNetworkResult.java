package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.List;

public class HttpSearchNetworkResult {
	public static class SearchNetworkResult extends BaseRequest{

		public SearchNetworkResult(IHttpFinishListener callback) {
			super("Network","SearchNetworkResult", "4.3",  callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new SearchNetworkResultResponse(m_finsishCallback);
			return new DataResponse<>(NetworkItemList.class, MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET, m_finsishCallback);
		}
	}

	public class NetworkItem {
		public int State;
		public int Rat;
		public String Numberic;
		public int NetworkID;
		public String FullName;
		public String ShortName;
		public String mcc;
		public String mnc;
	}
	
	public class NetworkItemList extends BaseResult{
		public int SearchState = 0;
		public List<NetworkItem> ListNetworkItem;
		@Override
		protected void clear() {
			SearchState = 0;
			ListNetworkItem.clear();
		}
	}
}
