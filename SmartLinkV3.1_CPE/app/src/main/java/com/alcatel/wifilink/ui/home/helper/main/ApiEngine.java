package com.alcatel.wifilink.ui.home.helper.main;

import android.content.Context;

import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.battery.BatteryState;
import com.alcatel.wifilink.model.device.response.BlockList;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.utils.DataUtils;

import static com.alcatel.wifilink.R.string.device_set_success;

@Deprecated
public class ApiEngine {

    private static ApiEngine apiEngine;
    public static SimStatus home_simStatus;
    public static UsageSetting home_usageSetting;
    public static UsageRecord home_usageRecord;
    public static BatteryState home_batteryState;
    public static LoginState home_loginStatu;
    public static ConnectionStates home_connectionState;
    public static NetworkInfos home_networkInfos;
    public static ConnectedList home_connectDeviceList;
    public static BlockList home_blockDeviceList;
    public static BlockList blockDeviceList;
    public static ConnectedList connectDeviceList;

    /* interface */
    private static OnConnectDeviceList onConnectDeviceList;
    private static OnBlockDeviceList onBlockDeviceList;


    public static ApiEngine get() {
        if (apiEngine == null) {
            synchronized (ApiEngine.class) {
                if (apiEngine == null) {
                    apiEngine = new ApiEngine();
                }
            }
        }
        return apiEngine;
    }

    public static void getUsageRecord() {
        API.get().getUsageRecord(DataUtils.getCurrent(),new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord usageRecord) {
                home_usageRecord = usageRecord;
                System.out.println("getUsageRecord");
            }
        });
    }

    public static void getBatteryState() {
        API.get().getBatteryState(new MySubscriber<BatteryState>() {
            @Override
            protected void onSuccess(BatteryState batteryState) {
                ApiEngine.home_batteryState = batteryState;
            }
        });
    }

    public static void getUsageSetting() {
        API.get().getUsageSetting(new MySubscriber<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting usageSetting) {
                ApiEngine.home_usageSetting = usageSetting;
                System.out.println("getUsageSetting");
            }
        });
    }

    public static void getNetworkInfo() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos networkInfos) {
                ApiEngine.home_networkInfos = networkInfos;
                System.out.println("getNetworkInfo");
            }
        });
    }

    public static void getConnectStatus() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates connectionState) {
                ApiEngine.home_connectionState = connectionState;
                System.out.println("getConnectStatus");
            }
        });
    }

    public static void getSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus simStatus) {
                ApiEngine.home_simStatus = simStatus;
                System.out.println("getSimStatus");
            }
        });
    }

    public static void getUserLoginStatus() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState loginStatu) {
                ApiEngine.home_loginStatu = loginStatu;
                System.out.println("getUserLoginStatus");
            }
        });
    }

    public static void connectto() {
        API.get().connect(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    public static void disconnect() {
        API.get().disConnect(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    // TOAT: 
    public static void clearUsageReacord() {
        API.get().setUsageRecordClear(DataUtils.getCurrent(), new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }
        });
    }

    public static void getConnectedDeviceList() {
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList device) {
                ApiEngine.home_connectDeviceList = device;
                if (onConnectDeviceList != null) {
                    onConnectDeviceList.getConnectDevices(device);
                }
            }
        });
    }

    public static void getBlockDeviceList() {
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList blockList) {
                ApiEngine.home_blockDeviceList = blockList;
                if (onBlockDeviceList != null) {
                    onBlockDeviceList.getBlockDevices(blockList);
                }
            }
        });
    }

    public static void setConnectedDeviceBlock(Context context, String DeviceName, String MacAddress) {
        API.get().setConnectedDeviceBlock(DeviceName, MacAddress, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(context, context.getString(device_set_success));
            }
        });
    }

    public static void setDeviceUnblock(Context context, String DeviceName, String MacAddress) {
        API.get().setDeviceUnblock(DeviceName, MacAddress, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(context, context.getString(device_set_success));
            }
        });
    }

    public static void setDeviceName(Context context, String DeviceName, String MacAddress, int DeviceType) {
        API.get().setDeviceName(DeviceName, MacAddress, DeviceType, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(context, context.getString(device_set_success));
            }
        });
    }


    /* -------------------------------------------- interface -------------------------------------------- */
    public interface OnConnectDeviceList {
        void getConnectDevices(ConnectedList connectedDeviceList);
    }

    public static void setOnConnectDeviceList(OnConnectDeviceList onConnectDeviceList) {
        ApiEngine.onConnectDeviceList = onConnectDeviceList;
    }

    public interface OnBlockDeviceList {
        void getBlockDevices(BlockList blockList);
    }

    public static void setOnBlockDeviceList(OnBlockDeviceList onBlockDeviceList) {
        ApiEngine.onBlockDeviceList = onBlockDeviceList;
    }


}
