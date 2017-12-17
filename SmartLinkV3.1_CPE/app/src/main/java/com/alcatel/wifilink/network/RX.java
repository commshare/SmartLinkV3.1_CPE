package com.alcatel.wifilink.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alcatel.wifilink.model.Usage.UsageParams;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.Usage.UsageRecordParam;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.Usage.UsageSettings;
import com.alcatel.wifilink.model.battery.BatteryState;
import com.alcatel.wifilink.model.connection.ConnectionMode;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.model.connection.ConnectionState;
import com.alcatel.wifilink.model.device.param.ConnectedDeviceBlockParam;
import com.alcatel.wifilink.model.device.param.DeviceNameParam;
import com.alcatel.wifilink.model.device.param.DeviceUnblockParam;
import com.alcatel.wifilink.model.device.response.BlockList;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.language.LanguageResult;
import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.profile.ProfileList;
import com.alcatel.wifilink.model.sharing.DLNASettings;
import com.alcatel.wifilink.model.sharing.FTPSettings;
import com.alcatel.wifilink.model.sharing.SambaSettings;
import com.alcatel.wifilink.model.sim.AutoValidatePinState;
import com.alcatel.wifilink.model.sim.ChangePinParams;
import com.alcatel.wifilink.model.sim.PinParams;
import com.alcatel.wifilink.model.sim.PinStateParams;
import com.alcatel.wifilink.model.sim.PukParams;
import com.alcatel.wifilink.model.sim.SetAutoValidatePinStateParams;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.sim.UnlockSimlockParams;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SMSContactListParam;
import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.sms.SMSContentParam;
import com.alcatel.wifilink.model.sms.SMSDeleteParam;
import com.alcatel.wifilink.model.sms.SMSSaveParam;
import com.alcatel.wifilink.model.sms.SMSSendParam;
import com.alcatel.wifilink.model.sms.SMSStorageState;
import com.alcatel.wifilink.model.sms.SendSMSResult;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.model.sms.SmsSingle;
import com.alcatel.wifilink.model.system.SysStatus;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.system.WanSetting;
import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.model.update.DeviceUpgradeState;
import com.alcatel.wifilink.model.user.LoginParams;
import com.alcatel.wifilink.model.user.LoginResult;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.user.NewPasswdParams;
import com.alcatel.wifilink.model.wan.WanSettingsParams;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.model.wlan.LanSettings;
import com.alcatel.wifilink.model.wlan.WlanSetting;
import com.alcatel.wifilink.model.wlan.WlanSettings;
import com.alcatel.wifilink.model.wlan.WlanState;
import com.alcatel.wifilink.model.wlan.WlanStatus;
import com.alcatel.wifilink.model.wlan.WlanSupportAPMode;
import com.alcatel.wifilink.network.downloadfile.DownloadProgressInterceptor;
import com.alcatel.wifilink.network.downloadfile.DownloadProgressListener;
import com.alcatel.wifilink.rx.helper.base.HttpLoggerInterceptor;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.utils.Constants;
import com.alcatel.wifilink.utils.EncryptionUtil;
import com.alcatel.wifilink.utils.FileUtils;
import com.alcatel.wifilink.utils.HostnameUtils;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.WifiUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.alcatel.wifilink.utils.Constants.SP_KEY_TOKEN;


/**
 * Created by tao.j on 2017/6/14.
 */

public class RX {

    public static final String AUTHORIZATION_KEY = "KSDHSDFOGQ5WERYTUIQWERTYUISDFG1HJZXCVCXBN2GDSMNDHKVKFsVBNf";
    public static final String USER_KEY = "e5dl12XYVggihggafXWf0f2YSf2Xngd1";

    private SmartLinkApi smartLinkApi;

    private static RX api;

    private String token;
    private int TIMEOUT = 30;
    public static String gateWay;

    private RX() {
        if (smartLinkApi == null) {
            SharedPreferences sp = SmartLinkV3App.getInstance().getSharedPreferences(Constants.SP_GLOBAL_INFO, Context.MODE_PRIVATE);
            int token = sp.getInt(SP_KEY_TOKEN, 0);
            encryptToken(token);
            createSmartLinkApi();
        }
    }

    public static RX getInstant() {
        // 1.检测wifi是否有连接
        boolean wiFiActive = OtherUtils.isWiFiActive(SmartLinkV3App.getInstance());
        if (!wiFiActive) {
            OtherUtils.setWifiActive(SmartLinkV3App.getInstance(), true);
        }
        gateWay = WifiUtils.getWifiGateWay(SmartLinkV3App.getInstance());
        gateWay = "http://" + (TextUtils.isEmpty(gateWay) | !gateWay.startsWith("192.168") ? "192.168.1.1" : gateWay);
        if (api == null) {
            synchronized (RX.class) {
                if (api == null) {
                    api = new RX();
                }
            }
        }
        return api;
    }

    public void updateToken(int token) {
        cacheToken(token);
        encryptToken(token);
        createSmartLinkApi();
    }

    public void encryptToken(int token) {
        if (token == 0) {
            this.token = null;
        }

        String str = String.valueOf(token);

        this.token = EncryptionUtil.encrypt(str);
    }

    private void cacheToken(int token) {
        SharedPreferences sp = SmartLinkV3App.getInstance().getSharedPreferences(Constants.SP_GLOBAL_INFO, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_KEY_TOKEN, token).apply();
    }

    private void createSmartLinkApi() {
        try {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(gateWay).client(buildOkHttpClient()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            smartLinkApi = retrofit.create(SmartLinkApi.class);
        } catch (Exception e) {
            Logs.d("ma_api", "okhttpClient error");
        }
    }

    private OkHttpClient buildOkHttpClient() {
        // OKGO
        HttpLoggerInterceptor malogger = new HttpLoggerInterceptor("MALOGGER");
        malogger.setPrintLevel(HttpLoggerInterceptor.Level.BODY);
        // OKhttp
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);// 连接失败后重试
        builder.addInterceptor(chain -> {
            Request request = chain.request();
            Request.Builder reqBuilder = request.newBuilder();
            reqBuilder.addHeader("_TclRequestVerificationKey", AUTHORIZATION_KEY);
            if (token != null) {
                /* token */
                reqBuilder.addHeader("_TclRequestVerificationToken", token);
            }

            // 获取当前连接的IP
            Context context = SmartLinkV3App.getInstance().getApplicationContext();
            // 形式: http://网关如192.168.3.1/
            String wifiGateWay = WifiUtils.getWifiGateWay(context);
            // String wifiIp = WifiUtils.getWifiIp(context);
            Log.d("ma_load", wifiGateWay);
            String ip = Cons.IP_PRE + wifiGateWay + Cons.IP_SUFFIX;
            Log.d("ma_load", ip);
            /* referer */
            reqBuilder.addHeader("Referer", ip);
            request = reqBuilder.build();
            Response proceed = chain.proceed(request);
            return proceed;
        });
        
        /* google play request online ssl verify */
        builder.hostnameVerifier(HostnameUtils.getVerify());
        // builder.addInterceptor(httpLoggingInterceptor);
        builder.addInterceptor(malogger);
        return builder.build();
    }


    private OkHttpClient createDownloadHttpClient(DownloadProgressListener listener) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).retryOnConnectionFailure(true).connectTimeout(TIMEOUT, TimeUnit.SECONDS).build();
        return client;
    }


    private void subscribe(ResponseObject subscriber, Observable observable) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    private void subscribe(Subscriber subscriber, Observable observable) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    private void subscribeDownloadFile(Subscriber subscriber, Observable observable, File file) {
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).map(new Func1<okhttp3.ResponseBody, InputStream>() {
            @Override
            public InputStream call(okhttp3.ResponseBody responseBody) {
                return responseBody.byteStream();
            }
        }).observeOn(Schedulers.computation()).doOnNext(new Action1<InputStream>() {
            @Override
            public void call(InputStream inputStream) {
                try {
                    FileUtils.writeFile(inputStream, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public void setDeviceUpdateStop(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_DEVICE_UPDATE_STOP)));
    }

    public void getNetworkRegisterState(ResponseObject<NetworkRegisterState> subscriber) {
        subscribe(subscriber, smartLinkApi.getNetworkRegisterState(new RequestBody(Methods.GET_NETWORK_REGISTER_STATE)));
    }

    public void getCurrentLanguage(ResponseObject<LanguageResult> subscriber) {
        subscribe(subscriber, smartLinkApi.getCurrentLanguage(new RequestBody(Methods.GET_CURRENT_LANGUAGE)));
    }

    public void getSMSStorageState(ResponseObject<SMSStorageState> subscriber) {
        subscribe(subscriber, smartLinkApi.getSMSStorageState(new RequestBody(Methods.GET_SMS_STORAGE_STATE)));
    }

    /**
     * login
     *
     * @param userName   user name
     * @param passwd     password
     * @param subscriber callback
     */
    public void login(String userName, String passwd, ResponseObject<LoginResult> subscriber) {
        Observable observable = smartLinkApi.login(new RequestBody(Methods.LOGIN, new LoginParams(userName, passwd)));
        subscribe(subscriber, observable);
    }

    /**
     * logout
     *
     * @param subscriber callback
     */
    public void logout(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.LOGOUT)));
    }

    /**
     * getInstant login state
     *
     * @param subscriber callback
     */
    public void getLoginState(ResponseObject<LoginState> subscriber) {
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
    public void changePasswd(String userName, String currPasswd, String newPasswd, ResponseObject subscriber) {
        NewPasswdParams passwdParams = new NewPasswdParams(userName, currPasswd, newPasswd);
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PASSWORD, passwdParams)));
    }

    /**
     * heart beat
     *
     * @param subscriber callback
     */
    public void heartBeat(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.HEART_BEAT)));
    }

    /**
     * getInstant sim status
     *
     * @param subscriber callback
     */
    public void getSimStatus(ResponseObject<SimStatus> subscriber) {
        subscribe(subscriber, smartLinkApi.getSimStatus(new RequestBody(Methods.GET_SIM_STATUS)));
    }

    /**
     * unlock pin
     *
     * @param pin        pin code
     * @param subscriber call back
     */
    public void unlockPin(String pin, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_PIN, new PinParams(pin))));
    }

    public void unlockPuk(String puk, String newPin, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_PUK, new PukParams(puk, newPin))));
    }

    public void changePinCode(String newPin, String currPin, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PIN_CODE, new ChangePinParams(newPin, currPin))));
    }

    public void changePinState(String pin, int state, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PIN_STATE, new PinStateParams(pin, state))));
    }

    public void getAutoValidatePinState(ResponseObject<AutoValidatePinState> subscriber) {
        subscribe(subscriber, smartLinkApi.getAutoValidatePinState(new RequestBody(Methods.GET_AUTO_VALIDATE_PIN_STATE)));
    }

    public void setAutoValidatePinState(String pin, int state, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_AUTO_VALIDATE_PIN_STATE, new SetAutoValidatePinStateParams(pin, state))));
    }

    public void unlockSimlock(int simlockState, String simlockCode, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.UNLOCK_SIMLOCK, new UnlockSimlockParams(simlockState, simlockCode))));
    }

    public void getConnectionState(ResponseObject<ConnectionState> subscriber) {
        subscribe(subscriber, smartLinkApi.getConnectionState(new RequestBody(Methods.GET_CONNECTION_STATE)));
    }

    public void uploadFile(Subscriber subscriber, MultipartBody.Part body) {
        subscribe(subscriber, smartLinkApi.uploadFile(body));
    }

    /**
     * getInstant 2.4g and 5g status (on/off)
     *
     * @param subscriber call back
     */
    public void getWlanState(ResponseObject<WlanState> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanState(new RequestBody(Methods.GET_WLAN_STATE)));
    }

    /**
     * 获取wlan的模式(0:无效 1:有效)
     *
     * @param subscriber
     */
    public void getWlanStatus(ResponseObject<WlanStatus> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanStatus(new RequestBody(Methods.GET_WLAN_STATE)));
    }

    public void setWlanState(WlanState state, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_WLAN_STATE, state)));
    }

    @Deprecated
    public void getWlanSettings(ResponseObject<WlanSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanSettings(new RequestBody(Methods.GET_WLAN_SETTINGS)));
    }

    public void getWlanSetting(ResponseObject<WlanSetting> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanSetting(new RequestBody(Methods.GET_WLAN_SETTINGS)));
    }

    @Deprecated
    public void setWlanSettings(WlanSettings settings, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_WLAN_SETTINGS, settings)));
    }

    public void setWlanSetting(WlanSetting setting, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_WLAN_SETTINGS, setting)));
    }

    public void getWlanSupportMode(ResponseObject<WlanSupportAPMode> subscriber) {
        subscribe(subscriber, smartLinkApi.getWlanSupportMode(new RequestBody(Methods.GET_WLAN_SUPPORT_MODE)));
    }

    public void getSystemStatus(ResponseObject<SysStatus> subscriber) {
        subscribe(subscriber, smartLinkApi.getSystemStatus(new RequestBody(Methods.GET_SYSTEM_STATUS)));
    }

    public void rebootDevice(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.reboot(new RequestBody(Methods.SET_DEVICE_REBOOT)));
    }

    public void resetDevice(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.resetDevice(new RequestBody(Methods.SET_DEVICE_RESET)));
    }

    public void backupDevice(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.backupDevice(new RequestBody(Methods.SET_DEVICE_BACKUP)));
    }

    public void getSystemInfo(ResponseObject<SystemInfo> subscriber) {
        subscribe(subscriber, smartLinkApi.getSystemInfo(new RequestBody(Methods.GET_SYSTEM_INFO)));
    }

    public void getWanSeting(ResponseObject<WanSetting> subscriber) {
        subscribe(subscriber, smartLinkApi.getWanSeting(new RequestBody(Methods.GET_WAN_SETTINGS)));
    }

    public void getWanSettings(ResponseObject<WanSettingsResult> subscriber) {
        subscribe(subscriber, smartLinkApi.getWanSettings(new RequestBody(Methods.GET_WAN_SETTINGS)));
    }

    public void setWanSettings(WanSettingsParams wsp, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_WAN_SETTINGS, wsp)));
    }

    public void getDeviceNewVersion(ResponseObject<DeviceNewVersion> subscriber) {
        subscribe(subscriber, smartLinkApi.getDeviceNewVersion(new RequestBody(Methods.GET_DEVICE_NEW_VERSION)));
    }

    public void setCheckNewVersion(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_CHECK_NEW_VERSION)));
    }

    public void setDeviceStartUpdate(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_DEVICE_START_UPDATE)));
    }

    public void SetFOTAStartDownload(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_FOTA_START_DOWNLOAD)));
    }

    public void getDeviceUpgradeState(ResponseObject<DeviceUpgradeState> subscriber) {
        subscribe(subscriber, smartLinkApi.getDeviceUpgradeState(new RequestBody(Methods.GET_DEVICE_UPGRADE_STATE)));
    }

    public void downConfigureFile(Subscriber subscriber, String url, File file) {
        subscribeDownloadFile(subscriber, smartLinkApi.downloadFile(url), file);
    }

    public void getFTPSettings(ResponseObject<FTPSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getFTPSettings(new RequestBody(Methods.GET_FTP_SETTINGS)));
    }

    public void getSambaSettings(ResponseObject<SambaSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getSambaSettings(new RequestBody(Methods.GET_SAMBA_SETTINGS)));
    }

    public void getDLNASettings(ResponseObject<DLNASettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getDLNASettings(new RequestBody(Methods.GET_DLNA_SETTINGS)));
    }

    public void setFTPSettings(FTPSettings settings, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_FTP_SETTINGS, settings)));
    }

    public void setSambaSettings(SambaSettings settings, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_SAMBA_SETTINGS, settings)));
    }

    public void setDLNASettings(DLNASettings settings, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_DLNA_SETTINGS, settings)));
    }

    public void getConnectionStates(ResponseObject<ConnectionStates> subscriber) {
        subscribe(subscriber, smartLinkApi.getConnectionStates(new RequestBody(Methods.GET_CONNECTION_STATE)));
    }


    /**
     * change password
     *
     * @param userName   user name
     * @param currPasswd current password
     * @param newPasswd  new password
     * @param subscriber callback
     */
    public void changePassword(String userName, String currPasswd, String newPasswd, ResponseObject subscriber) {
        NewPasswdParams passwdParams = new NewPasswdParams(userName, currPasswd, newPasswd);
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CHANGE_PASSWORD, passwdParams)));
    }

    public void connect(ResponseObject subscriber) {
        Logs.v("ma_test", "gateWay: " + gateWay);
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.CONNECT)));
    }

    public void disConnect(ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.DISCONNECT)));
    }

    public void getConnectionSettings(ResponseObject<ConnectionSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getConnectionSettings(new RequestBody(Methods.GET_CONNECTION_SETTINGS)));
    }

    public void setConnectionSettings(ConnectionSettings connectionSettingsParams, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_CONNECTION_SETTINGS, connectionSettingsParams)));
    }

    public void getNetworkSettings(ResponseObject<Network> subscriber) {
        subscribe(subscriber, smartLinkApi.getNetworkSettings(new RequestBody(Methods.GET_NETWORK_SETTINGS)));
    }

    public void setNetworkSettings(Network network, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_NETWORK_SETTINGS, network)));
    }

    public void getUsageRecord(String current_time, ResponseObject<UsageRecord> subscriber) {
        subscribe(subscriber, smartLinkApi.getUsageRecord(new RequestBody(Methods.GET_USAGERECORD, new UsageRecordParam(current_time))));
    }

    public void getBatteryState(ResponseObject<BatteryState> subscriber) {
        subscribe(subscriber, smartLinkApi.getBatteryState(new RequestBody(Methods.GET_BATTERYSTATE)));
    }

    public void setUsageSetting(UsageSetting usageSettingParams, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_USAGE_SETTING, usageSettingParams)));
    }

    public void setUsageSettings(UsageSettings us, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_USAGE_SETTING, us)));
    }

    public void getUsageSetting(ResponseObject<UsageSetting> subscriber) {
        subscribe(subscriber, smartLinkApi.getUsageSetting(new RequestBody(Methods.GET_USAGESETTING)));
    }

    public void getUsageSettings(ResponseObject<UsageSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.getUsageSettings(new RequestBody(Methods.GET_USAGESETTING)));
    }

    public void getNetworkInfo(ResponseObject<NetworkInfos> subscriber) {
        subscribe(subscriber, smartLinkApi.getNetworkInfo(new RequestBody(Methods.GET_NETWORKINFO)));
    }

    public void setUsageRecordClear(String clearTime, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_USAGERECORDCLEAR, new UsageParams(clearTime))));
    }

    public void getConnectedDeviceList(ResponseObject<ConnectedList> subscriber) {
        subscribe(subscriber, smartLinkApi.getConnectedDeviceList(new RequestBody(Methods.GET_CONNECTEDDEVICELIST)));
    }

    public void getBlockDeviceList(ResponseObject<BlockList> subscriber) {
        subscribe(subscriber, smartLinkApi.getBlockDeviceList(new RequestBody(Methods.GET_BLOCKDEVICELIST)));
    }


    public void setConnectedDeviceBlock(String DeviceName, String MacAddress, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_CONNECTEDDEVICEBLOCK, new ConnectedDeviceBlockParam(DeviceName, MacAddress))));
    }

    public void setDeviceUnblock(String DeviceName, String MacAddress, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_DEVICEUNBLOCK, new DeviceUnblockParam(DeviceName, MacAddress))));
    }

    public void setDeviceName(String DeviceName, String MacAddress, int DeviceType, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_DEVICENAME, new DeviceNameParam(DeviceName, MacAddress, DeviceType))));
    }

    public void setConnectionMode(int connectMode, ResponseObject subscriber) {
        ConnectionMode connectionModeParams = new ConnectionMode(connectMode);
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SET_CONNECTION_MODE, connectionModeParams)));
    }

    public void getProfileList(ResponseObject<ProfileList> subscriber) {
        subscribe(subscriber, smartLinkApi.getProfileList(new RequestBody(Methods.GET_PROFILE_LIST)));
    }

    public void getSMSContactList(int Page, ResponseObject<SMSContactList> subscriber) {
        subscribe(subscriber, smartLinkApi.getSMSContactList(new RequestBody(Methods.GET_SMSCONTACTLIST, new SMSContactListParam(Page))));
    }

    public void getSmsInitState(ResponseObject<SmsInitState> subscriber) {
        subscribe(subscriber, smartLinkApi.getSmsInitState(new RequestBody(Methods.GET_SMSINITSTATE)));
    }

    public void getSMSContentList(SMSContentParam scp, ResponseObject<SMSContentList> subscriber) {
        subscribe(subscriber, smartLinkApi.getSMSContentList(new RequestBody(Methods.GET_SMSCONTENTLIST, scp)));
    }

    public void saveSMS(SMSSaveParam ssp, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SAVESMS, ssp)));
    }

    public void deleteSMS(SMSDeleteParam sp, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.DELETESMS, sp)));
    }

    public void sendSMS(SMSSendParam sssp, ResponseObject subscriber) {
        subscribe(subscriber, smartLinkApi.request(new RequestBody(Methods.SENDSMS, sssp)));
    }

    public void GetSendSMSResult(ResponseObject<SendSMSResult> subscriber) {
        subscribe(subscriber, smartLinkApi.GetSendSMSResult(new RequestBody(Methods.GET_SEND_SMS_RESULT)));
    }

    public void getSingleSMS(long SMSId, ResponseObject<SmsSingle> subscriber) {
        subscribe(subscriber, smartLinkApi.GetSingleSMS(new RequestBody(Methods.GET_SEND_SMS_RESULT, SMSId)));
    }

    public void getLanSettings(ResponseObject<LanSettings> subscriber) {
        subscribe(subscriber, smartLinkApi.GetLanSettings(new RequestBody(Methods.GET_LAN_SETTINGS)));
    }

    // TOAT: 接口
    interface SmartLinkApi {

        @POST("/jrd/webapi")
        Observable<ResponseBody> request(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<NetworkRegisterState>> getNetworkRegisterState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<LanguageResult>> getCurrentLanguage(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<LoginState>> getLoginState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<LoginResult>> login(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SimStatus>> getSimStatus(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<AutoValidatePinState>> getAutoValidatePinState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ConnectionState>> getConnectionState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanState>> getWlanState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanStatus>> getWlanStatus(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanSettings>> getWlanSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanSetting>> getWlanSetting(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WlanSupportAPMode>> getWlanSupportMode(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SysStatus>> getSystemStatus(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody> reboot(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody> resetDevice(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody> backupDevice(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SystemInfo>> getSystemInfo(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WanSetting>> getWanSeting(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<WanSettingsResult>> getWanSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<DeviceNewVersion>> getDeviceNewVersion(@Body RequestBody requestBody);


        @POST("/jrd/webapi")
        Observable<ResponseBody<DeviceUpgradeState>> getDeviceUpgradeState(@Body RequestBody requestBody);

        @Streaming
        @GET
        Observable<okhttp3.ResponseBody> downloadFile(@Url String url);

        @Multipart
        @POST("/goform/uploadBackupSettings")
        Observable<okhttp3.ResponseBody> uploadFile(@Part MultipartBody.Part file);

        @POST("/jrd/webapi")
        Observable<ResponseBody<FTPSettings>> getFTPSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SambaSettings>> getSambaSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<DLNASettings>> getDLNASettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ConnectionSettings>> getConnectionSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<Network>> getNetworkSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<BatteryState>> getBatteryState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<UsageRecord>> getUsageRecord(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<UsageSetting>> getUsageSetting(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<UsageSettings>> getUsageSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<NetworkInfos>> getNetworkInfo(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ConnectedList>> getConnectedDeviceList(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<BlockList>> getBlockDeviceList(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ConnectionStates>> getConnectionStates(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<ProfileList>> getProfileList(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SMSContactList>> getSMSContactList(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SmsInitState>> getSmsInitState(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SMSContentList>> getSMSContentList(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SendSMSResult>> GetSendSMSResult(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SmsSingle>> GetSingleSMS(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<LanSettings>> GetLanSettings(@Body RequestBody requestBody);

        @POST("/jrd/webapi")
        Observable<ResponseBody<SMSStorageState>> getSMSStorageState(@Body RequestBody requestBody);
    }
}
