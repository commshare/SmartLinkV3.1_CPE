package com.alcatel.smartlinkv3.httpservice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zen on 17-3-22.
 */

public class RestHttpClient {
    private final static String TAG = "RestHttpClient";

    private static final String AUTHORIZATION = "Authorization";
    private static final String ACCESS_TOKEN = "token=";
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";
    private static final String CONTENT_TYPE_MULT_FORM = "multipart/form-data";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static String currentAccessTokenValue = "";
    private final static Retrofit retrofit;

    static {
        OkHttpClient client = new OkHttpClient.Builder().
                readTimeout(60, TimeUnit.SECONDS).
                connectTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).
//                addInterceptor(mTokenInterceptor).
                build();

        retrofit = new Retrofit.Builder()
                .baseUrl("192.168.1.1") //// TODO: 17-3-22 use the gateway ip is better.
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    static Interceptor mTokenInterceptor = new Interceptor() {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
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

    public static void setAccessToken(String token) {
        currentAccessTokenValue = token;
    }

    public static String createHeadsValue() {
        return "";
    }
}
