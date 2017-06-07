package com.alcatel.smartlinkv3.httpservice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LegacyHttpClient {
    private static final String TAG = "LegacyHttpClient";
    private static final int FINISH_HTTP_REQUEST = 0x110;
    private static final int DISCONNECT_NUMBER = 5;
    private static LegacyHttpClient m_instance = null;

    private ExecutorService m_threadPool;
    private LinkedList<BaseRequest> m_request_list;
    private LinkedList<BaseResponse> m_response_list;

    private int m_nDisconnectNum = 0;
    // private Context m_context = null;
    private Boolean m_bStopBusiness = false;
    private String m_server_address = null;

    public static LegacyHttpClient getInstance() {
        if (m_instance == null) {
            Log.d(TAG, "Create Http Client instance");
            m_instance = new LegacyHttpClient();
        }
        return m_instance;
    }

	/*
                 * //fix bug:changed device public static void RecreateInstance() {
	 * Log.d(TAG, "Recreate requestManaget"); m_instance = new
	 * LegacyHttpClient(); }
	 */

    private LegacyHttpClient() {
        m_request_list = new LinkedList<>();
        m_response_list = new LinkedList<>();
        m_threadPool = Executors.newFixedThreadPool(getMaxThreadCount());
        setServerAddress("192.168.1.1");
    }

    public void setServerAddress(String strIp) {
        // test
        m_server_address = String.format(ConstValue.HTTP_SERVER_ADDRESS, strIp);
        // m_server_address = "http://172.24.222.48/cgi-bin/luci/jrd/webapi";
        // test
        Log.d(TAG, m_server_address);
        HttpAccessLog.getInstance().writeLogToFile("Server address:" + m_server_address);
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

            Log.d(TAG, String.format(Locale.getDefault(), "Cpu Counter: %d", counter));
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
        HttpAccessLog.getInstance().writeLogToFile("request_list size : " + String.valueOf(m_request_list.size() + "   append message:" + request.m_requestParamJson.toString()));
    }


    private void filterRequest(BaseRequest request) {
        String method;
        try {
            method = (String) request.getRequestParamJson().get(ConstValue.JSON_METHOD);
            final Iterator<BaseRequest> iter = m_request_list.iterator();
            while (iter.hasNext()) {
                BaseRequest reqInList = iter.next();
                String methodInList = (String) reqInList.getRequestParamJson().get(ConstValue.JSON_METHOD);
                if (method.equalsIgnoreCase(methodInList) && MethodProp.isRemoveAble(method)) {
                    iter.remove();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendResponse(BaseResponse response) {
        synchronized (m_response_list) {
            m_response_list.addLast(response);
        }
        Log.d("response_list", "response_list size : " + String.valueOf(m_response_list.size()));
        HttpAccessLog.getInstance().writeLogToFile("response_list size : " + String.valueOf(m_response_list.size()));

        Message msg = new Message();
        msg.what = LegacyHttpClient.FINISH_HTTP_REQUEST;
        m_message_handler.sendMessage(msg);
    }

    public void start() {
        synchronized (m_bStopBusiness) {
            m_bStopBusiness = false;
        }
    }

    public void stop() {
        synchronized (m_bStopBusiness) {
            m_bStopBusiness = true;
        }

        DataConnectManager.getInstance().setCPEWifiConnected(false);
        //DataConnectManager.getInstance().sendCPEWifiConnectChangeMsg();

        clearRequestList();
        clearResponseList();
    }

    public Boolean isStop() {
        SmartLinkV3App.getInstance();
        synchronized (this) {
            return m_bStopBusiness;
        }
    }

    private Handler m_message_handler = new Handler(SmartLinkV3App.getInstance().getApplicationContext().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!isStop()) {
                switch (msg.what) {
                    case LegacyHttpClient.FINISH_HTTP_REQUEST:
                        BaseResponse response = getResponse();

                        if (response != null) {
                            response.invokeFinishCallback();

                            if ((response.getResultCode() == BaseResponse.RESPONSE_CONNECTION_ERROR)) {
                                m_nDisconnectNum++;
                            } else {
                                m_nDisconnectNum = 0;
                                response.sendBroadcast(SmartLinkV3App.getInstance().getApplicationContext());
                            }
                            if (m_nDisconnectNum >= DISCONNECT_NUMBER) {
                                m_nDisconnectNum = DISCONNECT_NUMBER;
                                HttpAccessLog.getInstance().writeLogToFile("CPE WIFI error:m_nDisconnectNum >= DISCONNECT_NUMBER");
                                DataConnectManager.getInstance().setCPEWifiConnected(false);
                            } else {
                                DataConnectManager.getInstance().setCPEWifiConnected(true);
                            }

                            // no feature api
                            if (DataConnectManager.getInstance().getCPEWifiConnected()) {
                                if (BusinessManager.getInstance().getFeatures().getFeatures().size() <= 0) {
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

        if (isStop())
            return;

        if (!FeatureVersionManager.getInstance().isSupportApi(request.getModule(), request.getMethod())) {
            BaseResponse response_obj = request.createResponseObject();
            response_obj.setResult(BaseResponse.RESPONSE_UNSUPPORTED_API);
            sendResponse(response_obj);
            return;
        }
        // request.setHttpUrl(m_server_address);
        Log.d(TAG, "sendpostrequest");
        request.buildRequestParamJson();
        appendRequest(request);
        m_threadPool.submit(new HttpThreadRunnable());
    }

    public interface IHttpFinishListener {
        void onHttpRequestFinish(BaseResponse response);
    }

    class HttpThreadRunnable implements Runnable {
        public HttpThreadRunnable() {
        }

        @Override
        public void run() {
            Log.d(TAG, "lega run");
            if (isStop())
                return;
            // Log.d(TAG, String.format("%d", Thread.currentThread().getId()));
            BaseRequest request = getRequest();
            BaseResponse response_obj = request.createResponseObject();

            String body = request.getRequestParamJson().toString();
            try {
                //				String httpUrl = request.getHttpUrl();
                Log.d(TAG, m_server_address);
                Log.d(TAG, body);
                HttpAccessLog.getInstance().writeLogToFile("Request:" + body + " httpUrl:" + m_server_address);
                // HttpPost connect object
                HttpPost httpRequest = new HttpPost(m_server_address);
                // body
                StringEntity se = new StringEntity(body.toString(), "utf-8");
                httpRequest.setEntity(se);

                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 20 * 1000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20 * 1000);

                // get default LegacyHttpClient
                org.apache.http.client.HttpClient httpclient = new DefaultHttpClient(httpParameters);

                // get HttpResponse--> 真正提交请求
                // TOIN 2017/6/7 真正提交请求
                HttpResponse httpResponse = httpclient.execute(httpRequest);
                // HttpStatus.SC_OK

                int nStatusCode = httpResponse.getStatusLine().getStatusCode();
                if (nStatusCode == HttpStatus.SC_OK) {
                    // get response string
                    String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                    JSONObject responseJson = new JSONObject(response);
                    HttpAccessLog.getInstance().writeLogToFile("Response:" + response);
                    response_obj.parseResult(SmartLinkV3App.getInstance().getApplicationContext(), responseJson);
                } else {
                    // request error
                    HttpAccessLog.getInstance().writeLogToFile("ERROR: body(" + body + ")" + "httpResponse.getStatusLine().getStatusCode():" + String.valueOf(nStatusCode));
                    response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
                }
            } catch (UnsupportedEncodingException e) {
                response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
                e.printStackTrace();
                HttpAccessLog.getInstance().writeLogToFile("ERROR: body(" + body + ")" + "UnsupportedEncodingException message:" + e.toString());
            } catch (ClientProtocolException e) {
                response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
                e.printStackTrace();
                HttpAccessLog.getInstance().writeLogToFile("ERROR: body(" + body + ")" + "ClientProtocolException message:" + e.toString());
            } catch (IOException e) {
                response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
                e.printStackTrace();
                HttpAccessLog.getInstance().writeLogToFile("ERROR: body(" + body + ")" + "IOException message:" + e.toString());
            } catch (Exception e) {
                response_obj.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
                e.printStackTrace();
                HttpAccessLog.getInstance().writeLogToFile("ERROR: body(" + body + ")" + "Exception message:" + e.toString());
            } finally {

            }

            sendResponse(response_obj);
        }
    }
}
