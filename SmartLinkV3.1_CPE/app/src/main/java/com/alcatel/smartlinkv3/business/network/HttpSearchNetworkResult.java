package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import java.util.List;

public class HttpSearchNetworkResult {
	public static class SearchNetworkResult extends BaseRequest{

		public SearchNetworkResult(IHttpFinishListener callback) {
			super("SearchNetworkResult", "4.3",  callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SearchNetworkResultResponse(m_finsishCallback);
		}
		
	}
	
	public static class SearchNetworkResultResponse extends BaseResponse{
		
		private NetworkItemList m_networkItemList;

		public SearchNetworkResultResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_networkItemList = gson.fromJson(strJsonResult, NetworkItemList.class);
//			try{
//				Log.v("NetworkSearchResult", Integer.toString(m_networkItemList.ListNetworkItem.size()));
//			}
//			catch(Exception e){
////				Log.v("NetworkSearchResult", "Exception");
//			}
			
		}

		@Override
		public NetworkItemList getModelResult() {
			// TODO Auto-generated method stub
			return m_networkItemList;
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
			// TODO Auto-generated method stub
			SearchState = 0;
			ListNetworkItem.clear();
		}
		
	}
}
