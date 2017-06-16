package com.alcatel.smartlinkv3.network;

import com.alcatel.smartlinkv3.model.connection.ConnectionState;
import com.alcatel.smartlinkv3.model.sim.AutoValidatePinState;
import com.alcatel.smartlinkv3.model.sim.ChangePinParams;
import com.alcatel.smartlinkv3.model.user.LoginState;
import com.alcatel.smartlinkv3.model.user.NewPasswdParams;
import com.alcatel.smartlinkv3.model.sim.PinParams;
import com.alcatel.smartlinkv3.model.sim.PinStateParams;
import com.alcatel.smartlinkv3.model.sim.PukParams;
import com.alcatel.smartlinkv3.model.sim.SetAutoValidatePinStateParams;
import com.alcatel.smartlinkv3.model.sim.SimStatus;
import com.alcatel.smartlinkv3.model.user.LoginParams;
import com.alcatel.smartlinkv3.model.sim.UnlockSimlockParams;
import com.alcatel.smartlinkv3.model.wlan.WlanSettings;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tao.j on 2017/6/14.
 */

public class API {

    private SmartLinkApi smartLinkApi;

    private static API api;

    private API() {
        if (smartLinkApi == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl("http://192.168.1.1")
                    .client(buildOkHttpClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            smartLinkApi = retrofit.create(SmartLinkApi.class);
        }
    }

    private OkHttpClient buildOkHttpClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.addInterceptor(httpLoggingInterceptor);
        return builder.build();
    }

    public static API get() {
        if (api == null) {
            synchronized (API.class) {
                if (api == null) {
                    api = new API();
                }
            }
        }
        return api;
    }

    private void subscribe(MySubscriber subscriber, Observable observable) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * login
     *
     * @param userName   user name
     * @param passwd     password
     * @param subscriber callback
     */
    public void login(String userName, String passwd, MySubscriber subscriber) {
        Observable observable = smartLinkApi.request(new RequestBody(Methods.LOGIN, new LoginParams(userName, passwd)));
        subscribe(subscriber, observable);
    }

    /**
     * logout
     *
     * @param subscriber callback
     */
    public void logout(MySubscriber subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.LOGOUT)));
    }

    /**
     * get login state
     *
     * @param subscriber callback
     */
    public void getLoginState(MySubscriber subscriber) {
        subscribe(subscriber, smartLinkApi.getLoginState(new RequestBody(Methods.GET_LOGIN_STATE)));
    }

    /**
     * change password
     *
     * @param userName   user name
     * @param currPasswd current password
     * @param newPasswd  new password
     * @param subscriber callback
     */
    public void changePasswd(String userName, String currPasswd, String newPasswd, MySubscriber subscriber) {
        NewPasswdParams passwdParams = new NewPasswdParams(userName, currPasswd, newPasswd);
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PASSWORD, passwdParams)));
    }

    /**
     * heart beat
     *
     * @param subscriber callback
     */
    public void heartBeat(MySubscriber subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.HEART_BEAT)));
    }

    /**
     * get sim status
     * @param subscriber callback
     */
    public void getSimStatus(MySubscriber<SimStatus> subscriber){
        subscribe(subscriber, smartLinkApi.getSimStatus(new RequestBody(Methods.GET_SIM_STATUS)));
    }

    /**
     * unlock pin
     * @param pin pin code
     * @param subscriber call back
     */
    public void unlockPin(String pin, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_PIN, new PinParams(pin))));
    }

    public void unlockPuk(String puk, String newPin, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_PUK, new PukParams(puk, newPin))));
    }

    public void changePinCode(String currPin, String newPin, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PIN_CODE, new ChangePinParams(currPin, newPin))));
    }

    public void changePinState(String pin, int state, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PIN_STATE, new PinStateParams(pin, state))));
    }

    public void getAutoValidatePinState(MySubscriber<AutoValidatePinState> subscriber){
        subscribe(subscriber, smartLinkApi.getAutoValidatePinState(new RequestBody(Methods.GET_AUTO_VALIDATE_PIN_STATE)));
    }

    public void setAutoValidatePinState(String pin, int state, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_AUTO_VALIDATE_PIN_STATE, new SetAutoValidatePinStateParams(pin, state))));
    }

    public void unlockSimlock(int simlockState, String simlockCode, MySubscriber subscriber){
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_SIMLOCK, new UnlockSimlockParams(simlockState, simlockCode))));
    }

    public void getConnectionState(MySubscriber<ConnectionState> subscriber){
        subscribe(subscriber, smartLinkApi.getConnectionState(new RequestBody(Methods.GET_CONNECTION_STATE)));
    }
    /**
     * get all wlan settings
     * @param subscriber call back
     */
    public void getWlanSettings(MySubscriber<WlanSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanSettings(new RequestBody(Methods.GET_WLAN_SETTINGS)));
    }

    interface SmartLinkApi {

        @POST("/jrd/webapi")
        Observable<ResponseBody> request(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<LoginState>> getLoginState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SimStatus>> getSimStatus(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanSettings>> getWlanSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<AutoValidatePinState>> getAutoValidatePinState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ConnectionState>> getConnectionState(@Body RequestBody requestBody);
    }
}
