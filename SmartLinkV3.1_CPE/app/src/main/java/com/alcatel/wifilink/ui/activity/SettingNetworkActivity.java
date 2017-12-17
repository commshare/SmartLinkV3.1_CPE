package com.alcatel.wifilink.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.model.connection.ConnectionState;
import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.model.profile.ProfileList;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.WifiUtils;

public class SettingNetworkActivity extends BaseActivityWithBack implements OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "SettingNetworkActivity";
    private LinearLayout mMobileNetwork;
    private LinearLayout mSetDataPlan;
    private LinearLayout mChangePin;
    private RelativeLayout mRoamingRl;
    private SwitchCompat mMobileDataSwitchCompat;
    private AppCompatSpinner mConnectionModeSpinner;
    private AppCompatSpinner mNetworkModeSpinner;
    private ImageView mRoamingSwitchCompat;
    private SwitchCompat mSimPinCompat;
    private TextView mSimNumberTextView;
    private TextView mImsiTextView;
    private TextView mTvProfile;
    private boolean mOldMobileDataEnable;

    private Network mNetworkSettings;
    private ConnectionSettings mConnectionSettings;
    private UsageSetting mUsageSetting;
    private SimStatus mSimStatus;
    //set data plan
    private TextView mMonthlyDataPlanText;
    private AppCompatSpinner mBillingDaySpinner;
    private AppCompatSpinner mUsageAlertSpinner;
    private SwitchCompat mDisconnectCompat;
    private LinearLayout mTimeLimitLl;
    private SwitchCompat mTimeLimitCompat;
    private TextView mSetTimeLimitText;
    private SwitchCompat mLimitAutoDisaconectCompat;

    //change sim pin
    private EditText mCurrentSimPin;
    private EditText mNewSimPin;
    private EditText mConfirmNewSimPin;

    private boolean mFirstSetConnectionMode;
    private boolean mFirstSetNetworkMode;
    private boolean mFirstSetBillingDay;
    private Drawable switchOn;
    private Drawable switchOff;
    private ProgressDialog pgd;
    private boolean isRoaming;
    private PopupWindows popPin;
    private EditText et_settingPin;
    private TextView ok_settingPin;
    private TextView cancel_settingPin;
    private View iv_settingPin;
    private View rl_settingPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRes();
        setTitle(R.string.setting_mobile_network);
        setContentView(R.layout.activity_setting_network);
        mFirstSetConnectionMode = true;
        mFirstSetNetworkMode = true;
        mFirstSetBillingDay = true;
        mNetworkSettings = new Network();
        mConnectionSettings = new ConnectionSettings();
        mUsageSetting = new UsageSetting();
        mSimStatus = new SimStatus();
        //set data plan
        findViewById(R.id.rl_monthly_data_plan).setOnClickListener(this);
        findViewById(R.id.rl_set_time_limit).setOnClickListener(this);
        findViewById(R.id.relativelayout_network_profile).setOnClickListener(this);

        mMobileNetwork = (LinearLayout) findViewById(R.id.ll_mobile_network);
        mSetDataPlan = (LinearLayout) findViewById(R.id.sett_data_plan);
        mChangePin = (LinearLayout) findViewById(R.id.linear_network_change_pin);
        mRoamingRl = (RelativeLayout) findViewById(R.id.relativelayout_network_roaming);
        mMobileDataSwitchCompat = (SwitchCompat) findViewById(R.id.network_mobile_data_switch);
        mMobileDataSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                if (!mMobileDataSwitchCompat.isPressed()) {
                    return;
                }

                if (mOldMobileDataEnable != enable) {
                    if (enable) {
                        connect();
                    } else {
                        disConnect();
                    }
                }
            }
        });
        mConnectionModeSpinner = (AppCompatSpinner) findViewById(R.id.spinner_connection_mode);
        //        mConnectionModeSpinner.setSelection(0, true);
        mConnectionModeSpinner.setOnItemSelectedListener(this);
        mNetworkModeSpinner = (AppCompatSpinner) findViewById(R.id.settings_network_mode);
        //        mNetworkModeSpinner.setSelection(0, true);
        mNetworkModeSpinner.setOnItemSelectedListener(this);
        mRoamingSwitchCompat = (ImageView) findViewById(R.id.network_roaming_switch);
        mRoamingSwitchCompat.setOnClickListener(this);
        mSimPinCompat = (SwitchCompat) findViewById(R.id.network_sim_pin_switch);
        mSimPinCompat.setOnClickListener(this);
        mSimNumberTextView = (TextView) findViewById(R.id.textview_sim_number);
        mImsiTextView = (TextView) findViewById(R.id.textview_network_imsi);
        mTvProfile = (TextView) findViewById(R.id.textview_network_profile);
        findViewById(R.id.network_set_data_plan).setOnClickListener(this);
        findViewById(R.id.network_change_pin).setOnClickListener(this);
        getConnectionState();
        getConnectionSettings();
        getNetworkModeSettings();
        getProfileList();
        getSimStatus();
        getSystemInfo();

        //set data plan
        mMonthlyDataPlanText = (TextView) findViewById(R.id.textview_monthly_data_plan);
        mBillingDaySpinner = (AppCompatSpinner) findViewById(R.id.setdataplan_billing_day);
        //        mBillingDaySpinner.setSelection(0, true);
        mBillingDaySpinner.setOnItemSelectedListener(this);
        mUsageAlertSpinner = (AppCompatSpinner) findViewById(R.id.setdataplan_usagealert);
        int usageLimit_default = SP.getInstance(SettingNetworkActivity.this).getInt(Cons.USAGE_LIMIT, 90);
        setDefaultLimit(mUsageAlertSpinner, usageLimit_default);
        mUsageAlertSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] usageAlertStringArr = getResources().getStringArray(R.array.settings_data_plan_usage_alert);
                String usageAlertPercent = usageAlertStringArr[position];
                int usageLimit = Integer.valueOf(usageAlertPercent.replace("%", ""));
                SP.getInstance(SettingNetworkActivity.this).putInt(Cons.USAGE_LIMIT, usageLimit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mDisconnectCompat = (SwitchCompat) findViewById(R.id.setdataplan_auto_disconnect);
        mDisconnectCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                if (!compoundButton.isPressed())
                    return;
                if (enable) {
                    mUsageSetting.setAutoDisconnFlag(1);
                } else {
                    mUsageSetting.setAutoDisconnFlag(0);
                }
                setUsageSetting(mUsageSetting);
            }
        });
        mTimeLimitLl = (LinearLayout) findViewById(R.id.ll_time_limit);
        mTimeLimitCompat = (SwitchCompat) findViewById(R.id.setdataplan_timelimit);
        mTimeLimitCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                if (enable) {
                    //                    mUsageSetting.setTimeLimitFlag(1);
                    mTimeLimitLl.setVisibility(View.VISIBLE);
                } else {
                    mTimeLimitLl.setVisibility(View.GONE);
                    if (mUsageSetting.getTimeLimitFlag() == 1) {
                        mUsageSetting.setTimeLimitFlag(0);
                        setUsageSetting(mUsageSetting);
                    }
                }
            }
        });
        mSetTimeLimitText = (TextView) findViewById(R.id.textview_set_time_limit);
        mLimitAutoDisaconectCompat = (SwitchCompat) findViewById(R.id.setdataplan_limit_auto_disaconect);
        mLimitAutoDisaconectCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                if (enable) {
                    mUsageSetting.setTimeLimitFlag(1);
                } else {
                    mUsageSetting.setTimeLimitFlag(0);
                }
                setUsageSetting(mUsageSetting);
            }
        });
        //change sim pin
        mCurrentSimPin = (EditText) findViewById(R.id.current_sim_pin);
        mNewSimPin = (EditText) findViewById(R.id.new_sim_pin);
        mConfirmNewSimPin = (EditText) findViewById(R.id.confirm_new_sim_pin);

        rl_settingPin = findViewById(R.id.rl_pop_setttingPin);
        iv_settingPin = findViewById(R.id.iv_pop_setting_bg);
        et_settingPin = (EditText) findViewById(R.id.et_pop_setttingpin);
        ok_settingPin = (TextView) findViewById(R.id.tv_pop_settingpin_ok);
        cancel_settingPin = (TextView) findViewById(R.id.tv_pop_settingpin_cancel);
    }

    private void setDefaultLimit(AppCompatSpinner mUsageAlertSpinner, int usageLimit_default) {
        switch (usageLimit_default) {
            case 90:
                mUsageAlertSpinner.setSelection(0, true);
                break;
            case 80:
                mUsageAlertSpinner.setSelection(1, true);
                break;
            case 70:
                mUsageAlertSpinner.setSelection(2, true);
                break;
            case 60:
                mUsageAlertSpinner.setSelection(3, true);
                break;
        }
    }

    private void initRes() {
        switchOn = getResources().getDrawable(R.drawable.pwd_switcher_on);
        switchOff = getResources().getDrawable(R.drawable.pwd_switcher_off);
    }

    private void connect() {
        RX.getInstant().connect(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = true;
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.restart_device_tip), Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = false;
                mMobileDataSwitchCompat.setChecked(false);
            }

            @Override
            protected void onFailure() {
                mOldMobileDataEnable = false;
                mMobileDataSwitchCompat.setChecked(false);
            }
        });
    }

    private void disConnect() {
        RX.getInstant().disConnect(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = false;
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.setting_failed), Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = true;
                mMobileDataSwitchCompat.setChecked(true);
            }

            @Override
            protected void onFailure() {
                mOldMobileDataEnable = true;
                mMobileDataSwitchCompat.setChecked(true);
            }
        });
    }

    private void getConnectionState() {
        RX.getInstant().getConnectionState(new ResponseObject<ConnectionState>() {
            @Override
            protected void onSuccess(ConnectionState result) {
                // 0: disconnected  1: connecting 2: connected 3: disconnecting
                if (result.getConnectionStatus() == Constants.ConnectionStatus.DISCONNECTED) {
                    mMobileDataSwitchCompat.setChecked(false);
                    mOldMobileDataEnable = false;
                } else if (result.getConnectionStatus() == Constants.ConnectionStatus.CONNECTED) {
                    mMobileDataSwitchCompat.setChecked(true);
                    mOldMobileDataEnable = true;
                }
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void setConnectionSettings(int connectMode) {
        if (connectMode == 0) {
            mConnectionSettings.setConnectMode(Constants.ConnectionSettings.CONNECTION_MODE_AUTO);
        } else if (connectMode == 1) {
            mConnectionSettings.setConnectMode(Constants.ConnectionSettings.CONNECTION_MODE_MANUAL);
        }
        RX.getInstant().setConnectionSettings(mConnectionSettings, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                if (connectMode == 0) {
                    mRoamingRl.setVisibility(View.VISIBLE);
                } else if (connectMode == 1) {
                    mRoamingRl.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void getConnectionSettings() {
        RX.getInstant().getConnectionSettings(new ResponseObject<ConnectionSettings>() {
            @Override
            protected void onSuccess(ConnectionSettings result) {
                //  0: manual connect 1: auto connect
                mConnectionSettings = result;
                if (result.getConnectMode() == Constants.ConnectionSettings.CONNECTION_MODE_AUTO) {
                    mConnectionModeSpinner.setSelection(0);
                    mRoamingRl.setVisibility(View.VISIBLE);
                } else if (result.getConnectMode() == Constants.ConnectionSettings.CONNECTION_MODE_MANUAL) {
                    mConnectionModeSpinner.setSelection(1);
                    mRoamingRl.setVisibility(View.GONE);
                }
                if (result.getRoamingConnect() == Constants.ConnectionSettings.ROAMING_DISABLE) {
                    mRoamingSwitchCompat.setImageResource(R.drawable.pwd_switcher_off);
                    isRoaming = false;
                } else if (result.getRoamingConnect() == Constants.ConnectionSettings.ROAMING_ENABLE) {
                    isRoaming = true;
                    mRoamingSwitchCompat.setImageResource(R.drawable.pwd_switcher_on);
                }
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void setNetworkSettings(int networkMode) {
        mNetworkSettings.setNetworkMode(networkMode);
        RX.getInstant().setNetworkSettings(mNetworkSettings, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();

            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void getNetworkModeSettings() {
        RX.getInstant().getNetworkSettings(new ResponseObject<Network>() {
            @Override
            protected void onSuccess(Network result) {
                //  0: auto mode 1: 2G only 2: 3G only 3: LTE only

                mNetworkSettings = result;
                if (result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_AUTO) {
                    mNetworkModeSpinner.setSelection(0);
                } else if (result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_4G) {
                    mNetworkModeSpinner.setSelection(1);
                } else if (result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_3G) {
                    mNetworkModeSpinner.setSelection(2);
                } else if (result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_2G) {
                    mNetworkModeSpinner.setSelection(3);
                }
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void getProfileList() {

        RX.getInstant().getProfileList(new ResponseObject<ProfileList>() {
            @Override
            protected void onSuccess(ProfileList result) {
                mTvProfile.setText(result.getProfileName());
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void changePinState(String pinCode, int enable) {
        RX.getInstant().changePinState(pinCode, enable, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                rl_settingPin.setVisibility(View.GONE);
                mSimPinCompat.setChecked(enable == 1);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                et_settingPin.setText("");
                OtherUtils.hideKeyBoard(SettingNetworkActivity.this);
                int remainTimes = mSimStatus.getPinRemainingTimes() - 1;
                String content = getString(R.string.pin_error_waring_title) + "\n" + getString(R.string.can_also_enter_times, remainTimes + "");
                Toast.makeText(SettingNetworkActivity.this, content, Toast.LENGTH_SHORT).show();
                getSimStatus();
            }

            @Override
            protected void onFailure() {
                mSimPinCompat.setChecked(enable == 1 ? false : true);
            }
        });
    }

    private void getSimStatus() {
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                // PinState: 0: unknown
                // 1: enable but not verified
                // 2: PIN enable verified
                // 3: PIN disable
                // 4: PUK required
                // 5: PUK times used out;
                mSimStatus = result;
                if (result.getPinState() == 2) {
                    mSimPinCompat.setChecked(true);
                } else if (result.getPinState() == 3) {
                    mSimPinCompat.setChecked(false);
                }
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void getSystemInfo() {
        RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                mSimNumberTextView.setText(result.getMSISDN());
                mImsiTextView.setText(result.getIMSI());
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void setUsageSetting(UsageSetting usageSetting) {
        RX.getInstant().setUsageSetting(usageSetting, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                getUsageSetting();
            }

            @Override
            protected void onFailure() {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    private void changePinCode(String newPin, String currentPin) {
        RX.getInstant().changePinCode(newPin, currentPin, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                mCurrentSimPin.setText(null);
                mNewSimPin.setText(null);
                mConfirmNewSimPin.setText(null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                int remainTimes = mSimStatus.getPinRemainingTimes() - 1;
                Toast.makeText(SettingNetworkActivity.this, error.getMessage() + " " + getString(R.string.can_also_enter_times, remainTimes + ""), Toast.LENGTH_SHORT).show();
                getSimStatus();
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    private void getUsageSetting() {
        RX.getInstant().getUsageSetting(new ResponseObject<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting result) {
                mUsageSetting = result;
                String unit = "";
                if (result.getUnit() == Constants.UsageSetting.UNIT_MB) {
                    unit = "MB";
                } else if (result.getUnit() == Constants.UsageSetting.UNIT_GB) {
                    unit = "GB";
                } else if (result.getUnit() == Constants.UsageSetting.UNIT_KB) {
                    unit = "KB";
                }
                int dataPlanByte = getDataPlanByte(result.getUnit());
                long monthPlan = result.getMonthlyPlan() / dataPlanByte;
                mMonthlyDataPlanText.setText(monthPlan + " " + unit);
                mBillingDaySpinner.setSelection(result.getBillingDay());
                // mUsageAlertSpinner
                if (result.getAutoDisconnFlag() == 0) {
                    mDisconnectCompat.setChecked(false);
                } else if (result.getAutoDisconnFlag() == 1) {
                    mDisconnectCompat.setChecked(true);
                }
                if (result.getTimeLimitFlag() == 0) {
                    mLimitAutoDisaconectCompat.setChecked(false);
                } else if (result.getTimeLimitFlag() == 1) {
                    mLimitAutoDisaconectCompat.setChecked(true);
                }
                mSetTimeLimitText.setText(result.getTimeLimitTimes() + getString(R.string.min_s));
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Toast.makeText(SettingNetworkActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onFailure() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int nID = v.getId();
        switch (nID) {

            case R.id.network_roaming_switch:
                modifyRoam();
                break;

            case R.id.network_set_data_plan:
                mSetDataPlan.setVisibility(View.VISIBLE);
                mMobileNetwork.setVisibility(View.GONE);
                setTitle(getString(R.string.setting_set_data_plan));
                getUsageSetting();
                break;
            case R.id.rl_monthly_data_plan:
                showSetmonthlyDataPlanDialog();
                break;
            case R.id.rl_set_time_limit:
                showSetTimeLimitDialog();
                break;
            case R.id.network_change_pin:
                if (mSimPinCompat.isChecked()) {
                    mChangePin.setVisibility(View.VISIBLE);
                    mMobileNetwork.setVisibility(View.GONE);
                    setTitle(getString(R.string.change_pin));
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(SettingNetworkActivity.this, R.string.please_enable_sim_pin, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.network_sim_pin_switch:
                changePinStatus();
                break;
            case R.id.relativelayout_network_profile:
                showProfileLinkDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 修改PIN状态
     */
    private void changePinStatus() {
        BoardSimHelper boardSimHelper = new BoardSimHelper(this);
        boardSimHelper.setOnNownListener(simStatus -> toast(R.string.home_no_sim));
        boardSimHelper.setOnPinRequireListener(result -> {
            showSimPinEnableDialog();
        });
        boardSimHelper.setOnpukRequireListener(result -> {
            
        });
        boardSimHelper.setOnpukTimeoutListener(result -> toast(R.string.Home_PukTimes_UsedOut));
    }

    private void modifyRoam() {
        pgd = OtherUtils.showProgressPop(this);
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.READY) {
                    // 切换标记位
                    isRoaming = !isRoaming;
                    // mRoamingSwitchCompat.setImageResource(roamCheck ? R.drawable.pwd_switcher_on : R.drawable.pwd_switcher_off);
                    int RoamingConnect = isRoaming ? 1 : 0;// 原来是选中状态--> 显示为未选中
                    // 先获取一次漫游状态
                    RX.getInstant().getConnectionSettings(new ResponseObject<ConnectionSettings>() {
                        @Override
                        protected void onSuccess(ConnectionSettings result) {
                            result.setRoamingConnect(RoamingConnect);
                            // 提交
                            RX.getInstant().setConnectionSettings(result, new ResponseObject() {
                                @Override
                                protected void onSuccess(Object result) {
                                    RX.getInstant().getConnectionSettings(new ResponseObject<ConnectionSettings>() {
                                        @Override
                                        protected void onSuccess(ConnectionSettings result) {
                                            OtherUtils.hideProgressPop(pgd);
                                            int roamingConnect = result.getRoamingConnect();
                                            mRoamingSwitchCompat.setImageResource(roamingConnect == 1 ? R.drawable.pwd_switcher_on : R.drawable.pwd_switcher_off);
                                        }

                                        @Override
                                        protected void onResultError(ResponseBody.Error error) {
                                            roamError();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            roamError();
                                        }
                                    });

                                }

                                @Override
                                protected void onResultError(ResponseBody.Error error) {
                                    roamError();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    roamError();
                                }
                            });
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            ToastUtil_m.show(SettingNetworkActivity.this, R.string.connect_failed);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil_m.show(SettingNetworkActivity.this, R.string.connect_failed);
                        }
                    });
                } else {
                    OtherUtils.hideProgressPop(pgd);
                    ToastUtil_m.show(SettingNetworkActivity.this, R.string.insert_sim_or_wan);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(SettingNetworkActivity.this, R.string.insert_sim_or_wan);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(SettingNetworkActivity.this, R.string.insert_sim_or_wan);
            }
        });

    }

    private void roamError() {
        isRoaming = !isRoaming;
        OtherUtils.hideProgressPop(pgd);
        ToastUtil_m.show(SettingNetworkActivity.this, R.string.connect_failed);
    }

    private void showSetmonthlyDataPlanDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_monthly_data_plan, null);
        final EditText monthlyNumber = (EditText) v.findViewById(R.id.monthly_number);
        int dataPlanByte = getDataPlanByte(mUsageSetting.getUnit());
        long monthPlan = mUsageSetting.getMonthlyPlan() / dataPlanByte;
        monthlyNumber.setText(monthPlan + "");
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radiogroup_monthly_plan);
        RadioButton radioButtonGb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_gb);
        RadioButton radioButtonMb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_mb);
        // RadioButton radioButtonKb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_kb);
        if (mUsageSetting.getUnit() == Constants.UsageSetting.UNIT_MB) {
            radioButtonMb.setChecked(true);
        } else if (mUsageSetting.getUnit() == Constants.UsageSetting.UNIT_GB) {
            radioButtonGb.setChecked(true);
        } else if (mUsageSetting.getUnit() == Constants.UsageSetting.UNIT_KB) {
            // radioButtonKb.setChecked(true);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mothlyplan = monthlyNumber.getText().toString();
                mothlyplan = TextUtils.isEmpty(mothlyplan) ? "0" : mothlyplan;
                if (Integer.parseInt(mothlyplan) >= 0 && Integer.parseInt(mothlyplan) <= 1024) {
                    if (radioButtonMb.getId() == radioGroup.getCheckedRadioButtonId()) {
                        mUsageSetting.setUnit(Constants.UsageSetting.UNIT_MB);
                    } else if (radioButtonGb.getId() == radioGroup.getCheckedRadioButtonId()) {
                        mUsageSetting.setUnit(Constants.UsageSetting.UNIT_GB);
                    }
                    int dataPlanByte = getDataPlanByte(mUsageSetting.getUnit());
                    mUsageSetting.setMonthlyPlan((Long.parseLong(mothlyplan) * dataPlanByte));
                    setUsageSetting(mUsageSetting);
                } else {
                    Toast.makeText(SettingNetworkActivity.this, R.string.input_a_data_value_between_0_1024, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private int getDataPlanByte(int unit) {
        int dataPlanByte = 1;
        if (unit == Constants.UsageSetting.UNIT_MB) {
            dataPlanByte = dataPlanByte * 1024 * 1024;
        } else if (unit == Constants.UsageSetting.UNIT_GB) {
            dataPlanByte = dataPlanByte * 1024 * 1024 * 1024;
        } else if (unit == Constants.UsageSetting.UNIT_KB) {
            dataPlanByte = dataPlanByte * 1024;
        }
        return dataPlanByte;
    }

    private void showSetTimeLimitDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_set_time_limit, null);
        final EditText hrEt = (EditText) v.findViewById(R.id.dialog_time_limit_hr);
        final EditText minEt = (EditText) v.findViewById(R.id.dialog_time_limit_min);
        hrEt.setText(mUsageSetting.getTimeLimitTimes() / 60 + "");
        minEt.setText(mUsageSetting.getTimeLimitTimes() % 60 + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUsageSetting.setTimeLimitTimes(Integer.parseInt(hrEt.getText().toString()) * 60 + Integer.parseInt(minEt.getText().toString()));
                setUsageSetting(mUsageSetting);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void showSimPinEnableDialog() {
        rl_settingPin.setVisibility(View.VISIBLE);
        et_settingPin.setText("");
        iv_settingPin.setOnClickListener(v -> {
            OtherUtils.hideKeyBoard(this);
            rl_settingPin.setVisibility(View.GONE);
        });
        ok_settingPin.setOnClickListener(v -> {
            String edContent = OtherUtils.getEdContent(et_settingPin);
            if (TextUtils.isEmpty(edContent)) {
                ToastUtil_m.show(SettingNetworkActivity.this, getString(R.string.not_empty));
                return;
            } else if (edContent.length() < 4 || edContent.length() > 8) {
                ToastUtil_m.show(SettingNetworkActivity.this, getString(R.string.the_pin_code_should_be_4_8_characters));
                return;
            } else {
                OtherUtils.hideKeyBoard(this);
                changePinState(edContent, mSimPinCompat.isChecked() ? 0 : 1);
            }
        });
        cancel_settingPin.setOnClickListener(v -> {
            OtherUtils.hideKeyBoard(this);
            rl_settingPin.setVisibility(View.GONE);
        });
    }

    private void showProfileLinkDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_profile_link, null);
        v.findViewById(R.id.tv_web_edit_profile).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                String wifiGateWay = WifiUtils.getWifiGateWay(SmartLinkV3App.getInstance());
                Uri content_url = Uri.parse("http://" + wifiGateWay);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void doneChangePinCode() {
        if (mSimStatus.getPinRemainingTimes() == 0) {
            CA.toActivity(this, PukUnlockActivity.class, false, true, false, 0);
            return;
        }
        String currentPin = mCurrentSimPin.getText().toString();
        String newPin = mNewSimPin.getText().toString();
        String confirmPin = mConfirmNewSimPin.getText().toString();
        if (currentPin.length() == 0) {
            String strInfo = getString(R.string.please_input_your_current_pin_code);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPin.length() == 0) {
            String strInfo = getString(R.string.please_input_your_new_pin_code);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPin.equals(confirmPin)) {
            String strInfo = getString(R.string.inconsistent_new_pin_code);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPin.length() < 4 || confirmPin.length() > 8) {
            String strInfo = getString(R.string.the_pin_code_should_be_4_8_characters);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }

        changePinCode(newPin, currentPin);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_connection_mode) {
            if (mFirstSetConnectionMode) {
                mFirstSetConnectionMode = false;
                return;
            }
            setConnectionSettings(position);
        } else if (parent.getId() == R.id.settings_network_mode) {
            if (mFirstSetNetworkMode) {
                mFirstSetNetworkMode = false;
                return;
            }
            if (position == 0) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_AUTO);
            } else if (position == 1) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_4G);
            } else if (position == 2) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_3G);
            } else if (position == 3) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_2G);
            }
        } else if (parent.getId() == R.id.setdataplan_billing_day) {
            if (mFirstSetBillingDay) {
                mFirstSetBillingDay = false;
                return;
            }
            mUsageSetting.setBillingDay(position);
            setUsageSetting(mUsageSetting);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mChangePin.getVisibility() == View.VISIBLE) {
            getMenuInflater().inflate(R.menu.save, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            doneChangePinCode();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {

        if (rl_settingPin.getVisibility() == View.VISIBLE) {
            OtherUtils.hideKeyBoard(this);
            rl_settingPin.setVisibility(View.GONE);
            return;
        }

        if (mSetDataPlan.getVisibility() == View.VISIBLE) {
            mSetDataPlan.setVisibility(View.GONE);
            mMobileNetwork.setVisibility(View.VISIBLE);
            setTitle(R.string.setting_mobile_network);
            return;
        } else if (mChangePin.getVisibility() == View.VISIBLE) {
            mChangePin.setVisibility(View.GONE);
            mMobileNetwork.setVisibility(View.VISIBLE);
            setTitle(R.string.setting_mobile_network);
            invalidateOptionsMenu();
            return;
        }
        super.onBackPressed();
    }

    public void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(this, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(this, ac, false, isFinish, false, 0);
    }
}
