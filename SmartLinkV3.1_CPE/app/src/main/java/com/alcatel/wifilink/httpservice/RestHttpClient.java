package com.alcatel.wifilink.httpservice;

import android.content.Context;
import android.util.Log;

import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.common.ConnectivityUtils;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Subscriber;

/**
 * Created by zen on 17-3-22.
 */

public class RestHttpClient {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String TAG = "RestHttpClient";
    private static final String AUTHORIZATION = "Authorization";
    private static final String ACCESS_TOKEN = "token=";
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";
    private static final String CONTENT_TYPE_MULT_FORM = "multipart/form-data";
    private static RestHttpClient m_instance = null;
    private boolean mSniffHttpServer = false;
    private String currentAccessTokenValue = "";
    Interceptor mTokenInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (currentAccessTokenValue == null || currentAccessTokenValue.isEmpty()
                    /*|| alreadyHasAuthorizationHeader(originalRequest)*/) {
                return chain.proceed(originalRequest);
            }
            Request authorised = originalRequest.newBuilder()
                    .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
                    .addHeader(AUTHORIZATION, createHeadsValue())
                    .build();


            return chain.proceed(authorised);
        }
    };
    private Retrofit retrofit;
    private OkHttpClient mHttpClient;
    private String mServerUrl;

    private RestHttpClient() {
        mHttpClient = new OkHttpClient.Builder().
                readTimeout(60, TimeUnit.SECONDS).
                connectTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).
//                addInterceptor(mTokenInterceptor).
        build();

        //// TODO: 17-3-22 use the gateway ip is better.
        setServerAddress(ConnectivityUtils.getServerAddress(SmartLinkV3App.getInstance()));//"192.168.1.1"
    }

    public static RestHttpClient getInstance() {
        if (m_instance == null) {
            Log.d(TAG, "Create Http Client instance");
            m_instance = new RestHttpClient();
        }
        return m_instance;
    }

    public static String createHeadsValue() {
        return "";
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    public void setAccessToken(String token) {
        currentAccessTokenValue = token;
    }

    public void setServerAddress(String strIp) {
        String originServer = mServerUrl;
        mServerUrl = String.format(ConstValue.HTTP_SERVER_ADDRESS, strIp);
        // mServerUrl = "http://172.24.222.48/cgi-bin/luci/jrd/webapi";

//        if (retrofit != null && mServerUrl.equals(originServer)) {
//            return;
//        }
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(mServerUrl) //baseUrl must end in "/"
//                .client(mHttpClient)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();

        if (mServerUrl.equals(originServer)) {
            return;
        }

        setSniffHttpServer(false);

        Log.d(TAG, mServerUrl);
        HttpAccessLog.getInstance().writeLogToFile("Server address:" + mServerUrl);
    }

    public boolean isSniffHttpServer() {
        return mSniffHttpServer;
    }

    public void setSniffHttpServer(boolean mSniffHttpServer) {
        this.mSniffHttpServer = mSniffHttpServer;
    }

    public BaseResponse sendPostRequest(BaseRequest baseRequest) {
        baseRequest.buildRequestParamJson();
        BaseResponse baseResponse = baseRequest.createResponseObject();

        if(!FeatureVersionManager.getInstance().isSupportApi(baseRequest.getModule(), baseRequest.getMethod())){
            baseResponse.setResult(BaseResponse.RESPONSE_UNSUPPORTED_API);
            return baseResponse;
        }

//        final String authorizationValue = this.createHeadsValue();
        String responseBody = "";
        final Request request = new Request.Builder()
                .url(mServerUrl)
//                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
//                .addHeader(AUTHORIZATION, authorizationValue)
                .post(RequestBody.create(MEDIA_TYPE_JSON, baseRequest.getRequestParamJson().toString()))
                .build();

        try {
            Response httpResponse = mHttpClient.newCall(request).execute();
            responseBody = httpResponse.body().string();

            if (httpResponse.isSuccessful()) {
                JSONObject responseJson = new JSONObject(responseBody);
                HttpAccessLog.getInstance().writeLogToFile("Response:" + responseBody);
                baseResponse.parseResult(SmartLinkV3App.getInstance().getApplicationContext(), responseJson);
            } else {
                baseResponse.setResult(BaseResponse.RESPONSE_STATUS_ERROR);
                baseResponse.setErrorCode(Integer.toString(httpResponse.code()));
                baseResponse.setErrorMessage(httpResponse.message());
            }
            setSniffHttpServer(true);
        } catch (IOException e) {
            e.printStackTrace();
            baseResponse.setResult(BaseResponse.RESPONSE_CONNECTION_ERROR);
        } catch (JSONException e) {
            e.printStackTrace();
            baseResponse.setResult(BaseResponse.RESPONSE_MALFORMED);
        } finally {
            if (null == responseBody || responseBody.trim().length() <= 0) {
                responseBody = "";
            }
        }

        baseResponse.invokeFinishCallback();
        return baseResponse;
    }

    @RxLogObservable
    public <T> BaseResponse sendPostRequest(BaseRequest baseRequest, Subscriber<T> f) {
        baseRequest.buildRequestParamJson();
        BaseResponse baseResponse = baseRequest.createResponseObject();

        if(!FeatureVersionManager.getInstance().isSupportApi(baseRequest.getModule(), baseRequest.getMethod())){
            baseResponse.setResult(BaseResponse.RESPONSE_UNSUPPORTED_API);
            return baseResponse;
        }

        String responseBody = "";
        final Request request = new Request.Builder()
                .url(mServerUrl)
//                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
//                .addHeader(AUTHORIZATION, authorizationValue)
                .post(RequestBody.create(MEDIA_TYPE_JSON, baseRequest.getRequestParamJson().toString()))
                .build();

        try {
            Response httpResponse = mHttpClient.newCall(request).execute();
            Context context = SmartLinkV3App.getInstance();//SmartLinkV3App.getInstance().getApplicationContext();
            responseBody = httpResponse.body().string();
            if (httpResponse.isSuccessful()) {
                JSONObject responseJson = new JSONObject(responseBody);
                HttpAccessLog.getInstance().writeLogToFile("Response:" + responseBody);
                baseResponse.parseResult(context, responseJson);
                f.onNext(baseResponse.getModelResult());
                f.onCompleted();
                baseResponse.sendBroadcast(context);
            } else {
                f.onError(new HttpException(httpResponse));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            f.onError(e);
        } catch (IOException e) {
            e.printStackTrace();
            f.onError(e);
        } finally {
            if (null == responseBody || responseBody.trim().length() <= 0) {
                responseBody = "";
            }
        }

        return baseResponse;
    }
}
