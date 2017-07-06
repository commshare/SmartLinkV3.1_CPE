package com.alcatel.wifilink.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.model.connection.ConnectionState;
import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.model.profile.ProfileList;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;

public class SettingNetworkActivity extends BaseActivityWithBack implements OnClickListener ,AdapterView.OnItemSelectedListener {
    private static final String TAG = "SettingNetworkActivity";
    private LinearLayout mSetDataPlan;
    private LinearLayout mChangePin;
    private RelativeLayout mRoamingRl;
    private SwitchCompat mMobileDataSwitchCompat;
    private AppCompatSpinner mConnectionModeSpinner;
    private AppCompatSpinner mNetworkModeSpinner;
    private SwitchCompat mRoamingSwitchCompat;
    private SwitchCompat mSimPinCompat;
    private TextView mSimNumberTextView;
    private TextView mImsiTextView;
    private TextView mTvProfile;
    private boolean mOldMobileDataEnable;

    private Network mNetworkSettings;
    private ConnectionSettings mConnectionSettings;
    private UsageSetting mUsageSetting;
    //set data plan
    private TextView mMonthlyDataPlanText;
    private AppCompatSpinner mBillingDaySpinner;
    private AppCompatSpinner mUsageAlertSpinner;
    private SwitchCompat mDisconnectCompat;
    private SwitchCompat mTimeLimitCompat;
    private TextView mSetTimeLimitText;
    private SwitchCompat mLimitAutoDisaconectCompat;
    private boolean isCodeSelectBillingDay;

    //change sim pin
    private EditText mCurrentSimPin;
    private EditText mNewSimPin;
    private EditText mConfirmNewSimPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle(R.string.setting_mobile_network);
        setContentView(R.layout.activity_setting_network);
        mNetworkSettings = new Network();
        mConnectionSettings = new ConnectionSettings();
        mUsageSetting = new UsageSetting();
        //set data plan
        findViewById(R.id.rl_monthly_data_plan).setOnClickListener(this);
        findViewById(R.id.rl_set_time_limit).setOnClickListener(this);
        findViewById(R.id.relativelayout_network_profile).setOnClickListener(this);

        mSetDataPlan = (LinearLayout) findViewById(R.id.sett_data_plan);
        mChangePin = (LinearLayout) findViewById(R.id.linear_network_change_pin);
        mRoamingRl = (RelativeLayout) findViewById(R.id.relativelayout_network_roaming);
        mMobileDataSwitchCompat = (SwitchCompat) findViewById(R.id.network_mobile_data_switch);
        mMobileDataSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                Log.d(TAG, "mMobileDataSwitchCompat = " + enable);
                if (mOldMobileDataEnable != enable) {
                    if(enable) {
                        connect();
                    } else {
                        disConnect();
                    }
                }
            }
        });
        mConnectionModeSpinner = (AppCompatSpinner) findViewById(R.id.spinner_connection_mode);
        mConnectionModeSpinner.setOnItemSelectedListener(this);
        mNetworkModeSpinner = (AppCompatSpinner) findViewById(R.id.settings_network_mode);
        mNetworkModeSpinner.setOnItemSelectedListener(this);
        mRoamingSwitchCompat = (SwitchCompat) findViewById(R.id.network_roaming_switch);
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
        mBillingDaySpinner.setOnItemSelectedListener(this);
        mUsageAlertSpinner = (AppCompatSpinner) findViewById(R.id.setdataplan_usagealert);
        mDisconnectCompat = (SwitchCompat) findViewById(R.id.setdataplan_auto_disconnect);
        mDisconnectCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                Log.d(TAG, "mDisconnectCompat = " + enable);
                if(enable) {
                    mUsageSetting.setAutoDisconnFlag(1);
                } else {
                    mUsageSetting.setAutoDisconnFlag(0);
                }
                setUsageSetting(mUsageSetting);
            }
        });
        mTimeLimitCompat = (SwitchCompat) findViewById(R.id.setdataplan_timelimit);
        mTimeLimitCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                Log.d(TAG, "mTimeLimitCompat = " + enable);
                if(enable) {
                    mUsageSetting.setTimeLimitFlag(1);
                } else {
                    mUsageSetting.setTimeLimitFlag(0);
                }
                setUsageSetting(mUsageSetting);
            }
        });
        mSetTimeLimitText = (TextView) findViewById(R.id.textview_set_time_limit);
        mLimitAutoDisaconectCompat = (SwitchCompat) findViewById(R.id.setdataplan_limit_auto_disaconect);

        //change sim pin
        mCurrentSimPin = (EditText) findViewById(R.id.current_sim_pin);
        mNewSimPin = (EditText) findViewById(R.id.new_sim_pin);
        mConfirmNewSimPin = (EditText) findViewById(R.id.confirm_new_sim_pin);

    }

    private void connect() {
        API.get().connect(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, "Success", Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = true;
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "connect error");
                mOldMobileDataEnable = false;
                mMobileDataSwitchCompat.setChecked(false);
            }
        });
    }

    private void disConnect() {
        API.get().disConnect(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, "Success", Toast.LENGTH_SHORT).show();
                mOldMobileDataEnable = false;
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "disConnect error");
                mOldMobileDataEnable = true;
                mMobileDataSwitchCompat.setChecked(true);
            }
        });
    }

    private void getConnectionState() {
        API.get().getConnectionState(new MySubscriber<ConnectionState>() {
            @Override
            protected void onSuccess(ConnectionState result) {
                // 0: disconnected  1: connecting 2: connected 3: disconnecting
                Log.v(TAG, "getConnectionState" + result.getConnectionStatus());
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
                Log.d(TAG, "getConnectionState error");
            }
        });
    }

    private void setConnectionSettings(int connectMode) {
        if(connectMode == 0) {
            mConnectionSettings.setConnectMode(Constants.ConnectionSettings.CONNECTION_MODE_AUTO);
        } else if(connectMode == 1){
            mConnectionSettings.setConnectMode(Constants.ConnectionSettings.CONNECTION_MODE_MANUAL);
        }
        API.get().setConnectionSettings(mConnectionSettings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, "Success", Toast.LENGTH_SHORT).show();
                if(connectMode == 0){
                    mRoamingRl.setVisibility(View.VISIBLE);
                } else if(connectMode == 1){
                    mRoamingRl.setVisibility(View.GONE);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "setConnectionSettings error");
            }
        });
    }

    private void getConnectionSettings() {
        API.get().getConnectionSettings(new MySubscriber<ConnectionSettings> (){
            @Override
            protected void onSuccess(ConnectionSettings result) {
                //  0: manual connect 1: auto connect
                mConnectionSettings = result;
                Log.v(TAG, "getConnectionSettings"+result.getConnectMode());
                if(result.getConnectMode() == Constants.ConnectionSettings.CONNECTION_MODE_AUTO){
                    mConnectionModeSpinner.setSelection(0);
                    mRoamingRl.setVisibility(View.VISIBLE);
                } else if(result.getConnectMode() == Constants.ConnectionSettings.CONNECTION_MODE_MANUAL) {
                    mConnectionModeSpinner.setSelection(1);
                    mRoamingRl.setVisibility(View.GONE);
                }
                if(result.getRoamingConnect() == Constants.ConnectionSettings.ROAMING_DISABLE){
                    mRoamingSwitchCompat.setChecked(false);
                } else if(result.getRoamingConnect() == Constants.ConnectionSettings.ROAMING_ENABLE){
                    mRoamingSwitchCompat.setChecked(true);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getConnectionSettings error");
            }
        });
    }

    private void setNetworkSettings(int networkMode) {
        mNetworkSettings.setNetworkMode(networkMode);
        API.get().setNetworkSettings(mNetworkSettings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingNetworkActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "setNetworkSettings error");
            }
        });
    }

    private void getNetworkModeSettings() {
        API.get().getNetworkSettings(new MySubscriber<Network> (){
            @Override
            protected void onSuccess(Network result) {
                //  0: auto mode 1: 2G only 2: 3G only 3: LTE only

                Log.v(TAG, "getNetworkModeSettings"+result.getNetworkMode());
                mNetworkSettings = result;
                if(result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_AUTO){
                    mNetworkModeSpinner.setSelection(0);
                } else if(result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_4G) {
                    mNetworkModeSpinner.setSelection(1);
                } else if(result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_3G) {
                    mNetworkModeSpinner.setSelection(2);
                } else if(result.getNetworkMode() == Constants.SetNetWorkSeting.NET_WORK_MODE_2G) {
                    mNetworkModeSpinner.setSelection(3);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getNetworkModeSettings error");
            }
        });
    }

    private void getProfileList() {

        API.get().getProfileList(new MySubscriber<ProfileList>() {
            @Override
            protected void onSuccess(ProfileList result) {
                mTvProfile.setText(result.getProfileName());
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "getRoaming error");
            }
        });
    }

    private void changePinState(String pinCode, int enable) {
        API.get().changePinState(pinCode, enable, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "changePinState success");
                mSimPinCompat.setChecked(enable == 1);
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "changePinState error");
                mSimPinCompat.setChecked(enable == 1? false: true);
            }
        });
    }

    private void getSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus> (){
            @Override
            protected void onSuccess(SimStatus result) {
                // PinState: 0: unknown 1: enable but not verified 2: PIN enable verified 3: PIN disable 4: PUK required 5: PUK times used out;
                Log.v(TAG, "getSimStatus"+result.getPinState());
                if(result.getPinState() == 2){
                    mSimPinCompat.setChecked(true);
                } else if(result.getPinState() == 3) {
                    mSimPinCompat.setChecked(false);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getSimStatus error");
            }
        });
    }

    private void getSystemInfo() {
        API.get().getSystemInfo(new MySubscriber<SystemInfo> (){
            @Override
            protected void onSuccess(SystemInfo result) {
                Log.v(TAG, "IMSI="+result.getIMSI()+"---sim number"+result.getMSISDN());
                mSimNumberTextView.setText(result.getMSISDN());
                mImsiTextView.setText(result.getIMSI());
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getSystemInfo error");
            }
        });
    }

    private void setUsageSetting(UsageSetting usageSetting) {
        API.get().setUsageSetting(usageSetting, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "setUsageSetting success");
                getUsageSetting();
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "setUsageSetting error");
            }
        });
    }

    private void changePinCode(String newPin, String currentPin) {
        API.get().changePinCode(newPin, currentPin, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "changePinCode success");
                Toast.makeText(SettingNetworkActivity.this, "Success", Toast.LENGTH_SHORT).show();
                mCurrentSimPin.setText(null);
                mNewSimPin.setText(null);
                mConfirmNewSimPin.setText(null);
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "changePinCode error");
            }
        });
    }

    private void getUsageSetting() {
        API.get().getUsageSetting(new MySubscriber<UsageSetting> (){
            @Override
            protected void onSuccess(UsageSetting result) {
                mUsageSetting = result;
                String unit = "";
                if(result.getUnit() == 0) {
                    unit = "MB";
                } else if(result.getUnit() == 1) {
                    unit = "GB";
                } else if(result.getUnit() == 2) {
                    unit = "KB";
                }
                mMonthlyDataPlanText.setText(result.getMonthlyPlan() + " "+unit);
                mBillingDaySpinner.setSelection(result.getBillingDay());
                isCodeSelectBillingDay = true;
//                mUsageAlertSpinner
                if(result.getAutoDisconnFlag() == 0) {
                    mDisconnectCompat.setChecked(false);
                } else if(result.getAutoDisconnFlag() == 1) {
                    mDisconnectCompat.setChecked(true);
                }
                if(result.getTimeLimitFlag() == 0) {
                    mTimeLimitCompat.setChecked(false);
                } else if(result.getTimeLimitFlag() == 1) {
                    mTimeLimitCompat.setChecked(true);
                }
                mSetTimeLimitText.setText(result.getTimeLimitTimes()+"mins(s)");
                if(result.getAutoDisconnFlag() == 0) {
                    mLimitAutoDisaconectCompat.setChecked(false);
                } else if(result.getAutoDisconnFlag() == 1) {
                    mLimitAutoDisaconectCompat.setChecked(true);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getUsageSetting error");
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int nID = v.getId();
        switch (nID) {
            case R.id.network_set_data_plan:
                mSetDataPlan.setVisibility(View.VISIBLE);
                setTitle("Set data plan");
                getUsageSetting();
                break;
            case R.id.rl_monthly_data_plan:
                showSetmonthlyDataPlanDialog();
                break;
            case R.id.rl_set_time_limit:
                showSetTimeLimitDialog();
                break;
            case R.id.network_change_pin:
                mChangePin.setVisibility(View.VISIBLE);
                setTitle("Change Pin");
                invalidateOptionsMenu();
                break;
            case R.id.network_sim_pin_switch:
                mSimPinCompat.setChecked(mSimPinCompat.isChecked()?false:true);
                showSimPinEnableDialog();
                break;
            case R.id.relativelayout_network_profile:
                showProfileLinkDialog();
                break;
            default:
                break;
        }
    }

    private void showSetmonthlyDataPlanDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_monthly_data_plan, null);
        final EditText monthlyNumber = (EditText) v.findViewById(R.id.monthly_number);
        monthlyNumber.setText(mUsageSetting.getMonthlyPlan()+"");
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radiogroup_monthly_plan);
        RadioButton radioButtonGb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_gb);
        RadioButton radioButtonMb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_mb);
        RadioButton radioButtonKb = (RadioButton) v.findViewById(R.id.radio_monthly_plan_kb);
        if(mUsageSetting.getUnit() == 0) {
            radioButtonMb.setChecked(true);
        } else if(mUsageSetting.getUnit() == 1) {
            radioButtonGb.setChecked(true);
        } else if(mUsageSetting.getUnit() == 2) {
            radioButtonKb.setChecked(true);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUsageSetting.setMonthlyPlan(Integer.parseInt(monthlyNumber.getText().toString()));
                mUsageSetting.setUnit(radioGroup.getCheckedRadioButtonId());
                if(radioButtonMb.getId() == radioGroup.getCheckedRadioButtonId()){
                    mUsageSetting.setUnit(0);
                } else if(radioButtonGb.getId() == radioGroup.getCheckedRadioButtonId()){
                    mUsageSetting.setUnit(1);
                } else if(radioButtonKb.getId() == radioGroup.getCheckedRadioButtonId()){
                    mUsageSetting.setUnit(2);
                }
                setUsageSetting(mUsageSetting);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void showSetTimeLimitDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_set_time_limit, null);
        final EditText hrEt = (EditText) v.findViewById(R.id.dialog_time_limit_hr);
        final EditText minEt = (EditText) v.findViewById(R.id.dialog_time_limit_min);
        hrEt.setText(mUsageSetting.getTimeLimitTimes()/60+"");
        minEt.setText(mUsageSetting.getTimeLimitTimes()%60+"");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUsageSetting.setTimeLimitTimes(Integer.parseInt(hrEt.getText().toString())*60 + Integer.parseInt(minEt.getText().toString()));
                setUsageSetting(mUsageSetting);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void showSimPinEnableDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_set_sim_enable, null);
        final EditText nameEdit = (EditText) v.findViewById(R.id.et_sim_pin_code);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changePinState(nameEdit.getText().toString(), mSimPinCompat.isChecked()?0:1);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void showProfileLinkDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_profile_link, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void doneChangePinCode() {
        String currentPin = mCurrentSimPin.getText().toString();
        String newPin = mNewSimPin.getText().toString();
        String confirmPin = mConfirmNewSimPin.getText().toString();
        if (currentPin.length() == 0) {
            String strInfo = "Please input your current pin code";
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPin.length() == 0) {
            String strInfo = "Please input your new pin code!";
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPin.equals(confirmPin)) {
            String strInfo = "Inconsistent new pin code!";
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPin.length() != 4) {
            String strInfo = "The pin code should be 4 characters";
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }

        changePinCode(newPin, currentPin);
//
//        mCurrentPassword.setText(null);
//        mNewPassword.setText(null);
//        mConfirmPassword.setText(null);
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mCurrentPassword.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(mNewPassword.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(mConfirmPassword.getWindowToken(), 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_connection_mode) {
            setConnectionSettings(position);
        } else if (parent.getId() == R.id.settings_network_mode) {
            if(position == 0){
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_AUTO);
            } else if(position == 1) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_4G);
            } else if(position == 2) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_3G);
            } else if(position == 3) {
                setNetworkSettings(Constants.SetNetWorkSeting.NET_WORK_MODE_2G);
            }
        } else if (parent.getId() == R.id.setdataplan_billing_day) {
            if(isCodeSelectBillingDay){
                isCodeSelectBillingDay = false;
            } else {
                mUsageSetting.setBillingDay(position);
                setUsageSetting(mUsageSetting);
            }
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
        if (mSetDataPlan.getVisibility() == View.VISIBLE) {
            mSetDataPlan.setVisibility(View.GONE);
            setTitle(R.string.setting_mobile_network);
            return;
        } else if (mChangePin.getVisibility() == View.VISIBLE) {
            mChangePin.setVisibility(View.GONE);
            setTitle(R.string.setting_mobile_network);
            invalidateOptionsMenu();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
}
