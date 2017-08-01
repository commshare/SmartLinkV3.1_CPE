package com.alcatel.wifilink.network;

/**
 * Created by tao.j on 2017/6/15.
 */

public interface Methods {
    //LoginParams
    String LOGIN = "Login";
    String LOGOUT = "Logout";
    String GET_LOGIN_STATE = "GetLoginState";
    String CHANGE_PASSWORD = "ChangePassword";
    String HEART_BEAT = "HeartBeat";
    String SET_PASSWORD_CHANGE_FLAG = "SetPasswordChangeFlag";
    String GET_PASSWORD_CHANGE_FLAG = "GetPasswordChangeFlag";

    //SIM card
    String GET_SIM_STATUS = "GetSimStatus";
    String UNLOCK_PIN = "UnlockPin";
    String UNLOCK_PUK = "UnlockPuk";
    String CHANGE_PIN_CODE = "ChangePinCode";
    String CHANGE_PIN_STATE = "ChangePinState";
    String GET_AUTO_VALIDATE_PIN_STATE = "GetAutoValidatePinState";
    String SET_AUTO_VALIDATE_PIN_STATE = "SetAutoValidatePinState";
    String UNLOCK_SIMLOCK = "UnlockSimlock";

    //Connect
    String GET_CONNECTION_STATE = "GetConnectionState";
    String CONNECT = "Connect";
    String DISCONNECT = "DisConnect";
    String GET_CONNECTION_SETTINGS = "GetConnectionSettings";
    String SET_CONNECTION_SETTINGS = "SetConnectionSettings";
    String SET_CONNECTION_MODE = "SetConnectionMode";

    //Network
    String GET_NETWORK_SETTINGS = "GetNetworkSettings";
    String SET_NETWORK_SETTINGS = "SetNetworkSettings";
    String GET_NETWORK_INFO = "GetNetworkInfo";

    //Wlan setting
    String GET_WLAN_STATE = "GetWlanState";
    String SET_WLAN_STATE = "SetWlanState";
    String SET_WLAN_ON = "SetWlanOn";
    String GET_WLAN_SETTINGS = "GetWlanSettings";
    String SET_WLAN_SETTINGS = "SetWlanSettings";
    String SET_WPS_PIN = "SetWPSPin";
    String SET_WPS_PBC = "SetWPSPbc";
    String GET_WLAN_SUPPORT_MODE = "GetWlanSupportMode";
    String GET_WLAN_STATISTICS = "GetWlanStatistics";

    //System
    String GET_SYSTEM_STATUS = "GetSystemStatus";
    String GET_SYSTEM_INFO = "GetSystemInfo";
    String SET_DEVICE_REBOOT = "SetDeviceReboot";
    String SET_DEVICE_RESET = "SetDeviceReset";
    String SET_DEVICE_BACKUP = "SetDeviceBackup";

    //Wan
    String GET_WAN_SETTINGS = "GetWanSettings";
    String SET_WAN_SETTINGS = "SetWanSettings";
    String GET_LAN_SETTINGS = "GetLanSettings";
    //update
    String GET_DEVICE_NEW_VERSION = "GetDeviceNewVersion";
    String SET_CHECK_NEW_VERSION = "SetCheckNewVersion";
    String SET_DEVICE_START_UPDATE = "SetDeviceStartUpdate";
    String GET_DEVICE_UPGRADE_STATE = "GetDeviceUpgradeState";
    String SET_FOTA_START_DOWNLOAD = "SetFOTAStartDownload";
    String GET_FTP_SETTINGS = "GetFtpSettings";
    String GET_SAMBA_SETTINGS = "GetSambaSettings";
    String GET_DLNA_SETTINGS = "GetDLNASettings";
    String SET_FTP_SETTINGS = "SetFtpSettings";
    String SET_SAMBA_SETTINGS = "SetSambaSettings";
    String SET_DLNA_SETTINGS = "SetDLNASettings";

    String GET_BATTERYSTATE = "GetBatteryState";
    String GET_NETWORKINFO = "GetNetworkInfo";

    String SET_USAGE_SETTING = "SetUsageSettings";
    String GET_USAGESETTING = "GetUsageSettings";
    String GET_USAGERECORD = "GetUsageRecord";
    String SET_USAGERECORDCLEAR = "SetUsageRecordClear";

    String GET_CONNECTEDDEVICELIST = "GetConnectedDeviceList";
    String GET_BLOCKDEVICELIST = "GetBlockDeviceList";
    String SET_CONNECTEDDEVICEBLOCK = "SetConnectedDeviceBlock";
    String SET_DEVICEUNBLOCK = "SetDeviceUnblock";
    String SET_DEVICENAME = "SetDeviceName";

    //profile
    String GET_PROFILE_LIST = "GetProfileList";

    String GET_SMSCONTACTLIST = "GetSMSContactList";
    String GET_SMSINITSTATE = "getSmsInitState";

    String GET_SMSCONTENTLIST = "GetSMSContentList";
    String SAVESMS = "SaveSMS";
    String DELETESMS = "DeleteSMS";
    String SENDSMS = "SendSMS";
    String GET_SEND_SMS_RESULT = "GetSendSMSResult";
    String GET_SINGLE_SMS = "GetSingleSMS";


}
