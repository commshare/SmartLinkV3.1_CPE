package com.alcatel.smartlinkv3.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.model.connection.ConnectionSettings;
import com.alcatel.smartlinkv3.model.connection.ConnectionState;
import com.alcatel.smartlinkv3.model.network.Network;
import com.alcatel.smartlinkv3.model.network.NetworkInfo;
import com.alcatel.smartlinkv3.model.sim.SimStatus;
import com.alcatel.smartlinkv3.model.system.SystemInfo;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;

public class SettingNetworkActivity extends BaseActivityWithBack implements OnClickListener ,AdapterView.OnItemSelectedListener {
    private static final String TAG = "SettingNetworkActivity";
    private LinearLayout mSetDataPlan;
    private LinearLayout mChangePin;
    private SwitchCompat mMobileDataSwitchCompat;
    private AppCompatSpinner mConnectionModeSpinner;
    private SwitchCompat mRoamingSwitchCompat;
    private SwitchCompat mSimPinCompat;
    private TextView mSimNumberTextView;
    private TextView mImsiTextView;

    //set data plan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle(R.string.setting_mobile_network);
        setContentView(R.layout.activity_setting_network);
        //set data plan
        findViewById(R.id.rl_monthly_data_plan).setOnClickListener(this);
        findViewById(R.id.rl_set_time_limit).setOnClickListener(this);

        mSetDataPlan = (LinearLayout) findViewById(R.id.sett_data_plan);
        mChangePin = (LinearLayout) findViewById(R.id.linear_network_change_pin);
        mMobileDataSwitchCompat = (SwitchCompat) findViewById(R.id.network_mobile_data_switch);
        mConnectionModeSpinner = (AppCompatSpinner) findViewById(R.id.spinner_connection_mode);
        mConnectionModeSpinner.setOnItemSelectedListener(this);
        mRoamingSwitchCompat = (SwitchCompat) findViewById(R.id.network_roaming_switch);
        mSimPinCompat = (SwitchCompat) findViewById(R.id.network_sim_pin_switch);
        mSimNumberTextView = (TextView) findViewById(R.id.textview_sim_number);
        mImsiTextView = (TextView) findViewById(R.id.textview_network_imsi);
        findViewById(R.id.network_set_data_plan).setOnClickListener(this);
        findViewById(R.id.network_change_pin).setOnClickListener(this);
        getConnectionState();
        getConnectionSettings();
        getNetworkModeSettings();
        getRoaming();
        getSimStatus();
        getSystemInfo();
    }

    private void getConnectionState() {
        API.get().getConnectionState(new MySubscriber<ConnectionState> (){
            @Override
            protected void onSuccess(ConnectionState result) {
               // 0: disconnected  1: connecting 2: connected 3: disconnecting
                Log.v(TAG, "getConnectionState"+result.getConnectionStatus());
                if(result.getConnectionStatus().equals("0")){
                    mMobileDataSwitchCompat.setChecked(false);
                } else if(result.getConnectionStatus().equals("2")) {
                    mMobileDataSwitchCompat.setChecked(true);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getConnectionState error");
            }
        });
    }

    private void getConnectionSettings() {
        API.get().getConnectionSettings(new MySubscriber<ConnectionSettings> (){
            @Override
            protected void onSuccess(ConnectionSettings result) {
                //  0: manual connect 1: auto connect

                Log.v(TAG, "getConnectionSettings"+result.getConnectMode());
                if(result.getConnectMode() == 0){
                    mConnectionModeSpinner.setSelection(0);
                } else if(result.getConnectMode() == 1) {
                    mConnectionModeSpinner.setSelection(1);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getConnectionSettings error");
            }
        });
    }

    private void getNetworkModeSettings() {
        API.get().getNetworkSettings(new MySubscriber<Network> (){
            @Override
            protected void onSuccess(Network result) {
                //  0: auto mode 1: 2G only 2: 3G only 3: LTE only

                Log.v(TAG, "getNetworkModeSettings"+result.getNetworkMode());
                if(result.getNetworkMode() == 0){
                    mConnectionModeSpinner.setSelection(0);
                } else if(result.getNetworkMode() == 3) {
                    mConnectionModeSpinner.setSelection(1);
                } else if(result.getNetworkMode() == 2) {
                    mConnectionModeSpinner.setSelection(2);
                } else if(result.getNetworkMode() == 1) {
                    mConnectionModeSpinner.setSelection(3);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getNetworkModeSettings error");
            }
        });
    }

    private void getRoaming() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfo> (){
            @Override
            protected void onSuccess(NetworkInfo result) {
                //  0: roaming 1: no roaming
                Log.v(TAG, "getRoaming"+result.getRoaming());
                if(result.getRoaming() == 0){
                    mRoamingSwitchCompat.setChecked(true);
                } else if(result.getRoaming() == 1) {
                    mRoamingSwitchCompat.setChecked(false);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getRoaming error");
            }
        });
    }

    private void getSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus> (){
            @Override
            protected void onSuccess(SimStatus result) {
                // PinState: 0: unknown 1: enable but not verified 2: PIN enable verified 3: PIN disable 4: PUK required 5: PUK times used out;
                Log.v(TAG, "getRoaming"+result.getPinState());
                if(result.getPinState() == 2){
                    mSimPinCompat.setChecked(true);
                } else if(result.getPinState() == 3) {
                    mSimPinCompat.setChecked(false);
                }
            }
            @Override
            protected void onFailure() {
                Log.d(TAG, "getRoaming error");
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
                Log.d(TAG, "getRoaming error");
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
                break;
            default:
                break;
        }
    }

    private void showSetmonthlyDataPlanDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_monthly_data_plan, null);
        final EditText nameEdit = (EditText) v.findViewById(R.id.monthly_number);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    private void showSetTimeLimitDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_set_time_limit, null);
        final EditText nameEdit = (EditText) v.findViewById(R.id.set_time_limit_number);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_connection_mode) {

        }
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
