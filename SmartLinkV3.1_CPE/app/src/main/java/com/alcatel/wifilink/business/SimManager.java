package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.business.sim.AutoEnterPinStateResult;
import com.alcatel.wifilink.business.sim.HttpAutoEnterPinState;
import com.alcatel.wifilink.business.sim.HttpChangePinAndState;
import com.alcatel.wifilink.business.sim.HttpGetSimStatus;
import com.alcatel.wifilink.business.sim.HttpUnlockPinPuk;
import com.alcatel.wifilink.business.sim.SIMStatusResult;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.AutoPinState;
import com.alcatel.wifilink.common.ENUM.SIMState;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class SimManager extends BaseManager {
    private SimStatusModel m_simStatus = new SimStatusModel();
    private AutoPinState m_autoPinState = AutoPinState.Disable;
    private Timer m_rollTimer = new Timer();
    private OnLockPinListener onLockPinListener;
    private OnUnlockPukListener onUnlockPukListener;

    @Override
    protected void clearData() {
        // TODO Auto-generated method stub
        m_simStatus.clear();
    }

    @Override
    protected void stopRollTimer() {
        m_rollTimer.cancel();
        m_rollTimer.purge();
        m_rollTimer = new Timer();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {
                startGetSimStatusTask();
            } else {

            }
        }
    }

    public SimManager(Context context) {
        super(context);
        //CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
    }

    public SimStatusModel getSimStatus() {
        return m_simStatus;
    }

    public AutoPinState getAutoPinState() {
        return m_autoPinState;
    }

    public void changePinState(DataValue data) {
        int nState = (Integer) data.getParamByKey("state");
        String strPin = (String) data.getParamByKey("pin");
        LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.ChangePinState(nState, strPin, response -> {
            //    			sendBroadcast(response, MessageUti.SIM_CHANGE_PIN_STATE_REQUEST);
        }));
    }

    //SetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void setAutoValidatePinState(DataValue data) {
        int nState = (Integer) data.getParamByKey("state");
        String strPin = (String) data.getParamByKey("pin");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.SetAutoValidatePinState(nState, strPin, response -> {
            // sendBroadcast(response, MessageUti.SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST);
        }));
    }

    //GetAutoEnterPinState  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void getAutoPinState(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpAutoEnterPinState.GetAutoValidatePinState(response -> {
            if (response.isOk()) {
                AutoEnterPinStateResult result = response.getModelResult();
                //                		m_autoPinState.build(result.State);
                //                		Log.v("PINCHECK", "RESULT " + result.State);
                if (result.State == 1) {
                    m_autoPinState = AutoPinState.Enable;
                } else {
                    m_autoPinState = AutoPinState.Disable;
                }
            }

            //    			sendBroadcast(response, MessageUti.SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST);
        }));
    }

    //change pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void changePin(DataValue data) {
        String strNewPin = (String) data.getParamByKey("new_pin");
        String strCurrentPin = (String) data.getParamByKey("current_pin");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpChangePinAndState.ChangePinCode(strNewPin, strCurrentPin, response -> {
            //    			sendBroadcast(response, MessageUti.SIM_CHANGE_PIN_REQUEST);
        }));
    }

    //unlock Puk  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void unlockPuk(DataValue data) {
        String strPuk = (String) data.getParamByKey("puk");
        String strPin = (String) data.getParamByKey("pin");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPuk(strPuk, strPin, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                // sendBroadcast(response, MessageUti.SIM_UNLOCK_PUK_REQUEST);
                if (onUnlockPukListener != null) {
                    onUnlockPukListener.isSuccess(response.isOk());
                }
            }
        }));
    }

    public interface OnUnlockPukListener {
        void isSuccess(boolean success);
    }

    public void setOnUnlockPukListener(OnUnlockPukListener onUnlockPukListener) {
        this.onUnlockPukListener = onUnlockPukListener;
    }

    //unlock Pin  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void unlockPin(DataValue data) {
        String strPin = (String) data.getParamByKey("pin");
        LegacyHttpClient.getInstance().sendPostRequest(new HttpUnlockPinPuk.UnlockPin(strPin, response -> {
            if (response.isOk()) {
                changeSimStatusGetInterval(true);
            }

            if (onLockPinListener != null) {
                onLockPinListener.isCorrect(response.isOk());
            }
            //  sendBroadcast(response, MessageUti.SIM_UNLOCK_PIN_REQUEST);
        }));
    }

    // 监听器: SIM请求
    public interface OnLockPinListener {
        void isCorrect(boolean correct);
    }

    public void setOnLockPinListener(OnLockPinListener onLockPinListener) {
        this.onLockPinListener = onLockPinListener;
    }

    //GetSimStatus ////////////////////////////////////////////////////////////////////////////////////////// 
    private GetSimStatusTask m_getSimStatusTask = null;

    private void startGetSimStatusTask() {
        m_getSimStatusTask = new GetSimStatusTask();
        changeSimStatusGetInterval(false);
    }

    private boolean m_bisFastSpeed = false;

    private void changeSimStatusGetInterval(boolean bFast) {
        if (m_getSimStatusTask == null)
            return;
        m_bisFastSpeed = bFast;
        int nInterval = 10 * 1000;
        if (bFast) {
            nInterval = 3 * 1000;
        }

        stopRollTimer();

        m_getSimStatusTask = new GetSimStatusTask();
        m_rollTimer.scheduleAtFixedRate(m_getSimStatusTask, 0, nInterval);

    }

    class GetSimStatusTask extends TimerTask {
        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpGetSimStatus.GetSimStatus(new IHttpFinishListener() {
                @Override
                public void onHttpRequestFinish(BaseResponse response) {
                    boolean isBroadcast = false;
                    boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
                    if (!bCPEWifiConnected) {
                        return;
                    }

                    if (response.isValid()) {
                        String strErrcode = response.getErrorCode();
                        if (strErrcode.length() == 0) {
                            SIMStatusResult simStatusResult = response.getModelResult();
                            SimStatusModel pre = new SimStatusModel();
                            pre.clone(m_simStatus);
                            m_simStatus.setValue(simStatusResult);
                            //                    		Log.v("PINCHECK", "PINSTATUS " + simStatusResult.PinState);


                            if (m_bisFastSpeed && m_simStatus.m_SIMState == SIMState.Accessable) {
                                changeSimStatusGetInterval(false);
                            }

                            if (!m_simStatus.equalTo(pre)) {
                                isBroadcast = true;
                                //        	        			Log.v("PINCHECK", "BROADCAST");
                            }
                        } else if (strErrcode.equalsIgnoreCase("1") || strErrcode.equalsIgnoreCase("6")) {
                            m_simStatus.clear();
                            m_simStatus.m_SIMState = SIMState.NoSim;
                            isBroadcast = true;
                        }
                    }

                    response.setBroadcast(isBroadcast);
                }
            }));
        }
    }
}
