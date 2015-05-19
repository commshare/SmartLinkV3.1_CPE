package com.alcatel.smartlinkv3.business.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.util.Log;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpSearchNetworkResult {
	public static class SearchNetworkResult extends BaseRequest{

		public SearchNetworkResult(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SearchNetworkResult");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
