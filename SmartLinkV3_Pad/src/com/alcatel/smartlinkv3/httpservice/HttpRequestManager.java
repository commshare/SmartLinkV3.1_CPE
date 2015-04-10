package com.alcatel.smartlinkv3.httpservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class HttpRequestManager {

	private static final String TAG = "HttpRequestManager";

	private ExecutorService m_threadPool;
	private LinkedList<BaseRequest> m_request_list;
	private LinkedList<BaseResponse> m_response_list;

	private int m_nDisconnectNum = 0;
	private int DISCONNECT_NUMBER = 5;
	// private Context m_context = null;

	private Boolean m_bStopBussiness = false;

	private String m_server_address = null;

	protected static final int FINISH_HTTP_REQUEST = 0x110;

	private static HttpRequestManager m_instance = null;

	public static HttpRequestManager GetInstance() {
		if (m_instance == null) {
			Log.d(TAG, "create requestManaget");
			m_instance = new HttpRequestManager();
		}
		return m_instance;
	}

	/*
	 * //fix bug:changed device public static void RecreateInstance() {
	 * Log.d(TAG, "Recreate requestManaget"); m_instance = new
	 * HttpRequestManager(); }
	 */

	private HttpRequestManager() {
		m_request_list = new LinkedList<BaseRequest>();
		m_response_list = new LinkedList<BaseResponse>();
		m_threadPool = Executors.newFixedThreadPool(getMaxThreadCount());
		setServerAddress("192.168.1.1");
	}

	public void setServerAddress(String strIp) {
		// test
		m_server_address = String.format(ConstValue.HTTP_SERVER_ADDRESS, strIp);
		// m_server_address = "http://172.24.222.48/cgi-bin/luci/jrd/webapi";
		// test
		Log.d(TAG, m_server_address);
		HttpAccessLog.getInstance().writeLogToFile(
				"Server address:" + m_server_address);
	}

	/*
	 * public void setContext(Context context) { m_context = context; }
	 */

	private int getMaxThreadCount() {
		int n = getCupCoreCount() * 2;
		if (n < 4)
			n = 4;
		return n;
	}

	private int getCupCoreCount() {
		try {
			int counter = 0;
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			while (true) {
				String text = br.readLine();
				if (text == null)
					break;

				if (text.toLowerCase().startsWith("processor"))
					counter++;
			}

			String strLog = String.format("Cpu Counter: %d", counter);
			Log.d(TAG, strLog);
			return counter;
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return 1;
	}

	public BaseRequest getRequest() {
		synchronized (m_request_list) {
			return m_request_list.poll();
		}
	}

	private void clearRequestList() {
		synchronized (m_request_list) {
			m_request_list.clear();
		}
	}

	private void clearResponseList() {
		synchronized (m_response_list) {
			m_response_list.clear();
		}
	}

	public BaseResponse getResponse() {
		synchronized (m_response_list) {
			return m_response_list.poll();
		}
	}

	public void appendRequest(BaseRequest request) {
	
		synchronized (m_request_list) {
			filterRequest(request);
			m_request_list.addLast(request);			
		}
		//Log.e("request_list", "request_list size : " + String.valueOf(m_request_list.size()) + "   append message:" + request.m_requestParamJson.toString());
		HttpAccessLog.getInstance().writeLogToFile("request_list size : " + String.valueOf(m_request_list.size()  + "   append message:" + request.m_requestParamJson.toString()));
	}
	
	
	private void filterRequest(BaseRequest request)
	{
		String method;
		try {
			method = (String) request.getRequsetParmJson().get(ConstValue.JSON_METHOD);
			final Iterator<BaseRequest> iter = m_request_list.iterator();
			while (iter.hasNext()) {
				BaseRequest reqInList = iter.next();
				String methodInList = (String) reqInList.getRequsetParmJson().get(ConstValue.JSON_METHOD);				
			    if (method.equalsIgnoreCase(methodInList) && MethodProp.isRemoveAble(method)) {			    
			    	iter.remove();			    	
			    }
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void appendResponse(BaseResponse response) {
		synchronized (m_response_list) {
			m_response_list.addLast(response);
		}
		Log.d("response_list", "response_list size : " + String.valueOf(m_response_list.size()));
		HttpAccessLog.getInstance().writeLogToFile("response_list size : " + String.valueOf(m_response_list.size()));
	}

	public void startBussiness() {
		synchronized (m_bStopBussiness) {
			m_bStopBussiness = false;
		}
	}

	public void stopBussiness() {
		synchronized (m_bStopBussiness) {
			m_bStopBussiness = true;
		}

		DataConnectManager.getInstance().setCPEWifiConnected(false);
		//DataConnectManager.getInstance().sendCPEWifiConnectChangeMsg();

		clearRequestList();
		clearResponseList();
	}

	public Boolean getStopBussiness() {
		synchronized (m_bStopBussiness) {
			return m_bStopBussiness;
		}
	}

	private Handler m_message_handler = new Handler(SmartLinkV3App.getInstance()
			.getApplicationContext().getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (getStopBussiness() == false) {
				switch (msg.what) {
				case HttpRequestManager.FINISH_HTTP_REQUEST:
					BaseResponse respose = getResponse();

					if (respose != null) {
						respose.invokeFinishCallback();

						if ((respose.getResultCode() == BaseResponse.RESPONSE_CONNECTION_ERROR)) {
							m_nDisconnectNum++;
						} else {
							m_nDisconnectNum = 0;
						}
						if (m_nDisconnectNum >= DISCONNECT_NUMBER) {
							m_nDisconnectNum = DISCONNECT_NUMBER;
							HttpAccessLog.getInstance().writeLogToFile("CPE WIFI error:m_nDisconnectNum >= DISCONNECT_NUMBER");
							DataConnectManager.getInstance().setCPEWifiConnected(false);
						} else {
							DataConnectManager.getInstance().setCPEWifiConnected(true);
						}

						// no feature api
						if (DataConnectManager.getInstance()
								.getCPEWifiConnected() == true) {
							if (BusinessMannager.getInstance().getFeatures()
									.getFeatures().size() <= 0) {
								HttpAccessLog.getInstance().writeLogToFile("CPE WIFI error:getFeatures().size() <= 0");
								DataConnectManager.getInstance().setCPEWifiConnected(false);
							}
						}

						//DataConnectManager.getInstance()
						//		.sendCPEWifiConnectChangeMsg();
					}
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public void sendPostRequest(BaseRequest request) {
		if (getStopBussiness() == true)
			return;
		request.setHttpUrl(m_server_address);

		request.buildRequestParamJson();
		appendRequest(request);
		m_threadPool.submit(new HttpThreadRunnable());
	}

	public interface IHttpFinishListener {
		public void onHttpRequestFinish(BaseResponse response);
	}

	class HttpThreadRunnable implements Runnable {
		public HttpThreadRunnable() {
		}

		@Override
		public void run() {
			if (getStopBussiness() == true)
				return;
			// Log.d(TAG, String.format("%d", Thread.currentThread().getId()));
			BaseRequest request = getRequest();
			BaseResponse response_obj = request.createResponseObject();
			String response = null;
			String body = request.getRequsetParmJson().toString();
			try {
				String httpUrl = request.getHttpUrl();
				Log.d("HttpRequestManager", httpUrl);
				Log.d("HttpRequestManager", body);
				HttpAccessLog.getInstance().writeLogToFile("Request:" + body + " httpUrl:" + httpUrl);
				// HttpPost connect object
				HttpPost httpRequest = new HttpPost(httpUrl);
				// body
				StringEntity se = new StringEntity(body.toString(), "utf-8");
				httpRequest.setEntity(se);

				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters,20 * 1000);
				HttpConnectionParams.setSoTimeout(httpParameters, 20 * 1000);

				// get default HttpClient
				HttpClient httpclient = new DefaultHttpClient(httpParameters);

				// get HttpResponse
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				// HttpStatus.SC_OK
				response = new String();
				int nStatusCode = httpResponse.getStatusLine().getStatusCode();
				if (nStatusCode == HttpStatus.SC_OK) {
					// get response string
					response = EntityUtils.toString(httpResponse.getEntity(),
							"utf-8");
					JSONObject responseJson = new JSONObject(response);
					Log.d("HttpRequestManger response: ", response);
					HttpAccessLog.getInstance().writeLogToFile(
							"Response:" + response);
					response_obj.parseResult(responseJson);
				} else {
					// request error
					Log.d("HttpRequestManager", "ERROR: body(" + body + ")"
							+ "response:" + response);
					HttpAccessLog.getInstance().writeLogToFile(
							"ERROR: body(" + body + ")"
									+ "httpResponse.getStatusLine().getStatusCode():" + String.valueOf(nStatusCode));
					response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
				}
			} catch (UnsupportedEncodingException e) {
				response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
				e.printStackTrace();
				Log.d("HttpRequestManager",
						"ERROR: body(" + body + ")"
								+ "UnsupportedEncodingException message:"
								+ e.toString());
				HttpAccessLog.getInstance().writeLogToFile(
						"ERROR: body(" + body + ")"
								+ "UnsupportedEncodingException message:"
								+ e.toString());
			} catch (ClientProtocolException e) {
				response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
				e.printStackTrace();
				Log.d("HttpRequestManager", "ERROR: body(" + body + ")"
						+ "ClientProtocolException message:" + e.toString());
				HttpAccessLog.getInstance().writeLogToFile(
						"ERROR: body(" + body + ")"
								+ "ClientProtocolException message:"
								+ e.toString());
			} catch (IOException e) {
				response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
				e.printStackTrace();
				Log.d("HttpRequestManager", "ERROR: body(" + body + ")"
						+ "IOException message:" + e.toString());
				HttpAccessLog.getInstance().writeLogToFile(
						"ERROR: body(" + body + ")" + "IOException message:"
								+ e.toString());
			} catch (Exception e) {
				response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
				e.printStackTrace();
				Log.d("HttpRequestManager", "ERROR: body(" + body + ")"
						+ "Exception message:" + e.toString());
				HttpAccessLog.getInstance().writeLogToFile(
						"ERROR: body(" + body + ")" + "Exception message:"
								+ e.toString());
			} finally {

			}

			appendResponse(response_obj);
			Message msg = new Message();
			msg.what = HttpRequestManager.FINISH_HTTP_REQUEST;
			HttpRequestManager.this.m_message_handler.sendMessage(msg);
		}
	}
}
