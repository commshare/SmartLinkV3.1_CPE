package com.alcatel.wifilink.ui.home.helper.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;

public class ViewConnectBroadcastReceiver extends BroadcastReceiver {

    private OnWifiConnectListener wifiConnectListener;
    private OnNetworkRollRequestListener onNetworkRollRequestListener;
    private OnSimRollRequestListener onSimRollRequestListener;
    private OnWanRollRequestListener onWanRollRequestListener;
    private OnWanConnectListener wanConnectListener;
    private OnDeviceConnectListener deviceConnectListener;
    private OnBatteryListener onBatteryListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();

        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            if (wifiConnectListener != null) {
                wifiConnectListener.wificonnect();
            }
            // resetConnectBtnFlag();
            // m_connectLayout.setVisibility(View.VISIBLE);
            // m_connectedLayout.setVisibility(View.GONE);
            // showConnectBtnView();
        } else if (intent.getAction().equals(MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET)) {
            if (ok) {
                if (onNetworkRollRequestListener != null) {
                    onNetworkRollRequestListener.networkRollRequest();
                }
                // showSignalAndNetworkType();
                // showNetworkState();
                // showConnectBtnView();
            }
        } else if (intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
            if (ok) {
                if (onSimRollRequestListener != null) {
                    onSimRollRequestListener.simRollRequest();
                }
                // showSignalAndNetworkType();
                // resetConnectBtnFlag();
                // showNetworkState();
                // showConnectBtnView();
            }
        } else if (intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)) {
            if (ok) {
                if (onWanRollRequestListener != null) {
                    onWanRollRequestListener.wanRollRequest();
                }
                // resetConnectBtnFlag();
                // showNetworkState();
                // showConnectBtnView();
            }
        } else if (intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET) || intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)) {
            if (wanConnectListener != null) {
                wanConnectListener.wanConnect(ok);
            }
            // if (ok) {
            //     m_bConnectReturn = true;
            // } else {
            //     //operation fail
            //     m_bConnectPressd = false;
            //     showNetworkState();
            //     showConnectBtnView();
            // }
        } else if (intent.getAction().equals(MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST)) {
            if (ok) {
                if (deviceConnectListener != null) {
                    deviceConnectListener.deviceConnect();
                }
                // showAccessDeviceState();
            }
        } else if (intent.getAction().equals(MessageUti.POWER_GET_BATTERY_STATE)) {
            if (ok) {
                if (onBatteryListener != null) {
                    onBatteryListener.batteryStatus();
                }
                // showBatteryState();
            }
        }
    }

    // WIFI CONNECT
    public interface OnWifiConnectListener {
        void wificonnect();
    }

    public void setOnWifiConnectListener(OnWifiConnectListener wifiConnectListener) {
        this.wifiConnectListener = wifiConnectListener;
    }

    // NETWORK REQUEST
    public interface OnNetworkRollRequestListener {
        void networkRollRequest();
    }

    public void setOnNetworkRollRequestListener(OnNetworkRollRequestListener OnNetworkRequestListener) {
        onNetworkRollRequestListener = OnNetworkRequestListener;
    }

    // SIM ROLL REQUEST
    public interface OnSimRollRequestListener {
        void simRollRequest();
    }

    public void setOnSimRollRequestListener(OnSimRollRequestListener OnSimRequestListener) {
        onSimRollRequestListener = OnSimRequestListener;
    }

    // WAN ROLL CONNECT
    public interface OnWanRollRequestListener {
        void wanRollRequest();
    }

    public void setOnWanRollRequestListener(OnWanRollRequestListener onWanRollRequestListener) {
        this.onWanRollRequestListener = onWanRollRequestListener;
    }

    // WAN ROLL CONNECT
    public interface OnWanConnectListener {
        void wanConnect(boolean isOk);
    }

    public void setOnWanConnectListener(OnWanConnectListener wanConnectListener) {
        this.wanConnectListener = wanConnectListener;
    }

    // DEVICE CONNECT
    public interface OnDeviceConnectListener {
        void deviceConnect();
    }

    public void setOnDeviceConnectListener(OnDeviceConnectListener deviceConnectListener) {
        this.deviceConnectListener = deviceConnectListener;
    }

    // DEVICE CONNECT
    public interface OnBatteryListener {
        void batteryStatus();
    }

    public void setOnBatteryListener(OnBatteryListener onBatteryListener) {
        this.onBatteryListener = onBatteryListener;
    }
}
