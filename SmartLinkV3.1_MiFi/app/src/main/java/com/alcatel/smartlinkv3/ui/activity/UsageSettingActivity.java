package com.alcatel.smartlinkv3.ui.activity;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.ConnectionSettingsModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.OVER_DISCONNECT_STATE;
import com.alcatel.smartlinkv3.common.ENUM.OVER_ROAMING_STATE;
import com.alcatel.smartlinkv3.common.ENUM.OVER_TIME_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.rx.impl.usage.UsageSetting;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;


public class UsageSettingActivity extends BaseActivity implements OnClickListener {
    private String TAG = "UsageSettingActivity";

    private UsageSettingReceiver m_usettingreceiver = new UsageSettingReceiver();

    private final static int MONTHLY_MAX_VALUE = 1048576; // megabyte
    private final static int BILLING_MAX_VALUE = 31; // megabyte
    private final static int MAX_DISCONNECT_TIME_VALUE = 43200;

    private ImageButton bnBack;
    private TextView tvback;

    private EditText m_billingValue;
    private EditText m_monthlyValue;
    private long m_monthlyVal = 0;
    private int m_billingVal = 0;
    private TextView m_consumptionValue;
    private boolean m_bIsMonthlyValueEdit = false;
    private boolean m_bIsBillingValueEdit = false;

    private Button m_timeLimitDisconnectBtn;
    private EditText m_timeLimit;
    private boolean m_bIsTimeLimitEdit = false;
    private boolean m_bIsTimeLimitStatusEdit = false;


    private Button m_roamingDisconnectBtn;
    private boolean m_bIsRoamingDisconnectedEdit = false;
    private Button m_usageAutoDisconnectBtn;
    private boolean m_bIsAutoDisconnectedEdit = false;

    private boolean m_isFirstSelection = true;

    private Spinner m_unit_selector;
    private TextView tvDone;
    private ProgressDialog pgd;
    private int spinnerPosition;
    private RelativeLayout rlError;
    private UsageSetting usageResult;


    private class UsageSettingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    // updateUI();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                String msgRes = null;
                if (nResult == 0 && strErrorCode.length() == 0) {
                    msgRes = UsageSettingActivity.this.getString(R.string.usage_clear_history_success);
                    Toast.makeText(UsageSettingActivity.this, msgRes, Toast.LENGTH_SHORT).show();
                } else {
                    msgRes = UsageSettingActivity.this.getString(R.string.usage_clear_history_fail);
                    Toast.makeText(UsageSettingActivity.this, msgRes, Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET) || intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET) || intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                if (nResult == 0) {
                    // updateUI();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    m_bIsBillingValueEdit = false;
                    // showSettingBilling();
                }

            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET)) {
                m_bIsMonthlyValueEdit = false;
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    // showSettingMonthly();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_USED_DATA_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    //updateUI();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET)) {
                m_bIsTimeLimitStatusEdit = false;
                showTimeLimitInfo();
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {

                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    m_bIsTimeLimitEdit = false;
                    showTimeLimitInfo();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_USED_TIMES_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    //updateUI();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET)) {
                m_bIsAutoDisconnectedEdit = false;
                showUsageAutoDisconnectBtn();
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {

                }
            } else if (intent.getAction().equals(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET)) {
                m_bIsRoamingDisconnectedEdit = false;
                showRoamingAutoDisconnectBtn();
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {

                }
            } else if (intent.getAction().equals(MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    showRoamingAutoDisconnectBtn();
                }
            } else if (intent.getAction().equals(MessageUti.STATISTICS_SET_UNIT_REQUSET)) {
                int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
                String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
                if (nResult == 0 && strErrorCode.length() == 0) {
                    // setSettingMonthly();
                }
            }
        }
    }

    private void registerReceiver() {
        // advanced
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET));

        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.WAN_CONNECT_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.WAN_DISCONNECT_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_USED_DATA_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_USED_TIMES_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET));
        this.registerReceiver(m_usettingreceiver, new IntentFilter(MessageUti.STATISTICS_SET_UNIT_REQUSET));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usage_setting_view);
        getWindow().setBackgroundDrawable(null);
        initView();
        initClick();
    }


    private void initClick() {
        m_timeLimitDisconnectBtn.setOnClickListener(this);
        m_roamingDisconnectBtn.setOnClickListener(this);
        m_usageAutoDisconnectBtn.setOnClickListener(this);
        tvDone.setOnClickListener(this);
        bnBack.setOnClickListener(this);
        tvback.setOnClickListener(this);
    }

    private void initView() {
        bnBack = (ImageButton) this.findViewById(R.id.btn_back);
        tvback = (TextView) this.findViewById(R.id.Back);
        initMonthlyEdit();
        initBillingEdit();
        m_consumptionValue = (TextView) this.findViewById(R.id.consumption_value_tag);
        m_timeLimitDisconnectBtn = (Button) this.findViewById(R.id.enable_time_limit_btn);
        intTimeLimitEdit();
        m_roamingDisconnectBtn = (Button) this.findViewById(R.id.enable_roaming_btn);
        m_usageAutoDisconnectBtn = (Button) this.findViewById(R.id.enable_auto_disconnected_btn);
        tvDone = (TextView) findViewById(R.id.tv_done);
        rlError = (RelativeLayout) findViewById(R.id.rl_error);
    }


    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
        showSettingBilling();
        showSettingMonthly();
        showTimeLimitInfo();
        showUsageAutoDisconnectBtn();
        showRoamingAutoDisconnectBtn();

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(m_usettingreceiver);
        } catch (Exception e) {

        }

        m_bIsMonthlyValueEdit = false;
        m_bIsBillingValueEdit = false;
        m_bIsTimeLimitEdit = false;
        m_bIsTimeLimitStatusEdit = false;
        m_bIsAutoDisconnectedEdit = false;
        m_bIsRoamingDisconnectedEdit = false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.Back:
                this.finish();
                break;

            case R.id.enable_roaming_btn:
                onBtnRoamingAutoDisconnectClick();
                break;

            case R.id.enable_auto_disconnected_btn:
                onBtnUsageAutoDisconnectClick();
                break;

            case R.id.enable_time_limit_btn:
                onBtnTimeLimitDisconnectClick();
                break;

            case R.id.tv_done:
                onDoneClick();
                break;

            default:
                break;
        }

    }

    private void onDoneClick() {
        // 先获取最新的usage状态
        pgd = OtherUtils.showProgressPop(this);
        API.get().getUsageSetting(new MySubscriber<UsageSetting>() {

            @Override
            protected void onSuccess(UsageSetting usageResult) {
                UsageSettingActivity.this.usageResult = usageResult;
                rlError.setVisibility(View.GONE);
                tvDone.setVisibility(View.VISIBLE);
                // 设置billing day & monthly data plan & time limit的值
                setAllData();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                pgd = OtherUtils.showProgressPop(UsageSettingActivity.this);
                rlError.setVisibility(View.VISIBLE);
                tvDone.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                pgd = OtherUtils.showProgressPop(UsageSettingActivity.this);
                rlError.setVisibility(View.VISIBLE);
                tvDone.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 检查天数以及流量是否合理
     *
     * @return
     */
    private void setAllData() {

        // billday
        int billDay;
        String billValue = OtherUtils.getEdittext(m_billingValue);
        billDay = TextUtils.isEmpty(billValue) ? 1 : Integer.valueOf(billValue);// 参数
        usageResult.setBillingDay(billDay);
        m_billingValue.setText(String.valueOf(billDay));

        // monthly plan
        long monthlyTraffic = 0;
        String monthlyStr = OtherUtils.getEdittext(m_monthlyValue);
        monthlyTraffic = TextUtils.isEmpty(monthlyStr) ? 0 : Long.valueOf(monthlyStr);
        long monthlyPlan = (spinnerPosition == Conn.MB) ? // 参数
                                   (1024 * 1024 * monthlyTraffic) : (1024 * 1024 * 1024 * monthlyTraffic);
        m_monthlyValue.setText(String.valueOf(monthlyTraffic));
        usageResult.setMonthlyPlan(monthlyPlan);

        // set time limit
        int timeLimit = 1;
        String timelimitStr = OtherUtils.getEdittext(m_timeLimit);
        timeLimit = TextUtils.isEmpty(timelimitStr) ? 1 : Integer.valueOf(timelimitStr);
        m_timeLimit.setText(String.valueOf(timeLimit));
        usageResult.setTimeLimitTimes(timeLimit);

        // System.out.println("ma_usage: billingday--> " + usageResult.getBillingDay()
        //                            + ";monthly plan--> " + usageResult.getMonthlyPlan()
        //                            + ";timelimit--> " + m_timeLimit);

        // 2. 提交
        API.get().setUsageSetting(usageResult, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                // 由于viewSetting采用了定时机制, 数据在提交成功后立即返回viewSetting并不能及时刷新
                // 此处为了提高用户体验, 采用延迟3秒返回
                m_timeLimit.postDelayed(() -> {
                    OtherUtils.hideProgressPop(pgd);
                    ToastUtil_m.show(UsageSettingActivity.this, getString(R.string.setting_success));
                }, 3500);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(UsageSettingActivity.this, getString(R.string.setting_upgrade_no_connection));
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(UsageSettingActivity.this, getString(R.string.setting_upgrade_no_connection));
            }
        });
    }

    private void updateUI() {
        showSettingBilling();
        showSettingMonthly();
        showTimeLimitInfo();
        showUsageAutoDisconnectBtn();
        showRoamingAutoDisconnectBtn();
    }

    private void initBillingEdit() {
        m_billingValue = (EditText) this.findViewById(R.id.billing_plan_value);
        m_billingValue.setSelection(OtherUtils.getEdittext(m_billingValue).length());
        m_billingValue.setText("");
        m_billingValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(String.valueOf(s))) {
                    int billDay = Integer.valueOf(String.valueOf(s));
                    // TODO: 2017/11/6 0006 日期是否符合规则
                    if (billDay < 1) {
                        m_billingValue.setText("1");
                    } else if (billDay > 31) {
                        m_billingValue.setText("31");
                    }
                    m_billingValue.setSelection(m_billingValue.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().equalsIgnoreCase("0")) {
                    m_billingValue.setText("");
                }
            }

        });

        // m_billingValue.setOnEditorActionListener((v, actionId, event) -> {
        //     // EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
        //     if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
        //         commitBillingSetting();
        //     }
        //     return false;
        // });
    }

    /**
     * 提交billing结账日
     */
    private void commitBillingSetting() {
        setSettingBilling();
        // temporary behavior :just cancel edit focus
        m_timeLimitDisconnectBtn.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_billingValue.getWindowToken(), 0);
    }

    private void showSettingBilling() {
        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        if (simState.m_SIMState == SIMState.Accessable) {
            UsageSettingModel statistic = BusinessMannager.getInstance().getUsageSettings();

            m_billingValue.setEnabled(true);
            m_billingVal = statistic.HBillingDay;
            if (m_billingVal > 0) {
                if (!(m_billingValue.isFocused() == true || m_bIsBillingValueEdit == true))
                    m_billingValue.setText("" + m_billingVal);
            } else {
                if (!(m_billingValue.isFocused() == true || m_bIsBillingValueEdit == true))
                    m_billingValue.setText("");
            }

        } else {
            m_billingValue.clearFocus();
            m_billingValue.setEnabled(false);
        }

    }

    private void setSettingBilling() {
        int usage = 0;

        if (m_billingValue.getText().toString().length() > 0) {
            usage = (int) Long.parseLong(m_billingValue.getText().toString());
        }
        if (usage != m_billingVal) {
            if (usage > BILLING_MAX_VALUE) {
                usage = BILLING_MAX_VALUE;
            }
            UsageSettingModel staticSetting = BusinessMannager.getInstance().getUsageSettings();
            if (usage == staticSetting.HBillingDay)
                return;

            m_bIsBillingValueEdit = true;
            DataValue usageData = new DataValue();
            usageData.addParam("billing_day", usage);
            BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET, usageData);
        } else {
            m_billingValue.setText("" + m_billingVal);
        }

    }

    private void initMonthlyEdit() {
        m_monthlyValue = (EditText) this.findViewById(R.id.monthly_plan_value);
        m_monthlyValue.setSelection(OtherUtils.getEdittext(m_monthlyValue).length());
        m_monthlyValue.setText("");
        m_monthlyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 流量控制是否符合规则
                if (!TextUtils.isEmpty(String.valueOf(s))) {
                    int monthlyPlan = Integer.valueOf(String.valueOf(s));
                    if (monthlyPlan < 0) {
                        m_monthlyValue.setText("0");
                    } else if (monthlyPlan > 1024) {
                        m_monthlyValue.setText("1024");
                    }
                    m_monthlyValue.setSelection(m_monthlyValue.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().equalsIgnoreCase("0")) {
                    m_monthlyValue.setText("");
                }

            }

        });

        // m_monthlyValue.setOnEditorActionListener((v, actionId, event) -> {
        //     // EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
        //     if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
        //         commitMontyPlan();
        //     }
        //     return false;
        // });

        m_unit_selector = (Spinner) this.findViewById(R.id.unit_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.usage_unit, R.layout.unit_display_item);
        adapter.setDropDownViewResource(R.layout.unit_selection_item);
        m_unit_selector.setAdapter(adapter);
        m_unit_selector.setSelection(BusinessMannager.getInstance().getUsageSettings().HUnit);
        m_unit_selector.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;
                if (m_isFirstSelection) {
                    m_isFirstSelection = false;
                } else {
                    Log.v("CHECKUNIT", "selected: " + position);
                    DataValue data = new DataValue();
                    data.addParam("unit", position);
                    BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_UNIT_REQUSET, data);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    /**
     * 提交月流量使用计划
     */
    private void commitMontyPlan() {
        setSettingMonthly();
        // temporary behavior :just cancel edit focus
        m_timeLimitDisconnectBtn.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_monthlyValue.getWindowToken(), 0);
    }

    private void showSettingMonthly() {
        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        if (simState.m_SIMState == SIMState.Accessable) {
            UsageSettingModel statistic = BusinessMannager.getInstance().getUsageSettings();

            //change the interface by zhanghao 20170626

            UsageRecordResult statisticTem = BusinessMannager.getInstance().getUsageRecord();
            m_consumptionValue.setText(CommonUtil.ConvertTrafficToStringFromMB(this, (long) statisticTem.HUseData));

            //end to change;

            m_monthlyValue.setEnabled(true);
            m_monthlyVal = statistic.HMonthlyPlan;
            if (m_monthlyVal > 0) {
                if (!(m_monthlyValue.isFocused() == true || m_bIsMonthlyValueEdit == true)) {
                    if (statistic.HUnit == 0) {
                        m_monthlyValue.setText("" + byte2megabyte(m_monthlyVal));
                    } else {
                        m_monthlyValue.setText("" + new DecimalFormat("0.###").format(byte2gegabyte(m_monthlyVal)));
                    }
                }

            } else {
                if (!(m_monthlyValue.isFocused() == true || m_bIsMonthlyValueEdit == true))
                    m_monthlyValue.setText("");
            }

        } else {
            m_monthlyValue.clearFocus();
            m_monthlyValue.setEnabled(false);
        }
        if (pgd != null) {
            OtherUtils.hideProgressPop(pgd);
        }
        //showUsageAutoDisconnectBtn();
    }

    private void setSettingMonthly() {
        double usage = 0;

        if (m_monthlyValue.getText().toString().length() > 0) {
            usage = Double.parseDouble(m_monthlyValue.getText().toString());
        }
        UsageSettingModel staticSetting = BusinessMannager.getInstance().getUsageSettings();
        if ((usage != this.byte2megabyte(m_monthlyVal) && staticSetting.HUnit == 0) || (usage != this.byte2gegabyte(m_monthlyVal) && staticSetting.HUnit == 1)) {
            if ((long) usage == staticSetting.HMonthlyPlan)
                return;

            if (staticSetting.HUnit == 0) {
                if (usage > MONTHLY_MAX_VALUE) {
                    usage = MONTHLY_MAX_VALUE;
                    String strInfo = getString(R.string.usage_maximum_monthly_plan_notice) + "1048576 MB";
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (usage > MONTHLY_MAX_VALUE / 1024) {
                    usage = MONTHLY_MAX_VALUE / 1024;
                    String strInfo = getString(R.string.usage_maximum_monthly_plan_notice) + "1024 GB";
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
            }

            m_bIsMonthlyValueEdit = true;
            DataValue usageData = new DataValue();
            // TOAT: 2017/12/26 0026 这里把判断取消掉
            //if (usage <= 0) {
                usageData.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Disable);
                m_usageAutoDisconnectBtn.setEnabled(false);
                m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            //}
            Log.i(TAG, "Name=======" + BusinessMannager.getInstance().getFeatures().getDeviceName());
            if (staticSetting.HUnit == 0) {


                usageData.addParam("monthly_plan", megabyte2byte((long) usage));
                m_monthlyVal = megabyte2byte((long) usage);

            } else {

                usageData.addParam("monthly_plan", gegabyte2byte(usage));
                m_monthlyVal = gegabyte2byte(usage);

            }


            BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET, usageData);
        } else {
            System.out.println("ma_monthly: " + byte2megabyte(m_monthlyVal));
            // m_monthlyValue.setText(String.valueOf(byte2megabyte(m_monthlyVal)));
        }

    }

    private long megabyte2byte(long megabyte) {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y901"))
            return megabyte;
        else
            return megabyte * 1024 * 1024;
    }

    private long gegabyte2byte(double gegabyte) {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y901"))
            return (long) gegabyte;
        else
            return (long) (gegabyte * 1024 * 1024 * 1024);
    }

    private long byte2megabyte(long byteV) {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y901"))
            return byteV;
        else
            return byteV / (1024 * 1024);
    }

    private float byte2gegabyte(long byteV) {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y901"))
            return byteV;
        else
            return (float) byte2megabyte(byteV) / (float) 1024;
    }


    private void showTimeLimitInfo() {
        UsageSettingModel setting = BusinessMannager.getInstance().getUsageSettings();
        if (!(m_timeLimit.isInputMethodTarget() == true || this.m_bIsTimeLimitEdit == true)) {
            if (setting.HTimeLimitTimes > 0) {
                m_timeLimit.setText(String.valueOf(setting.HTimeLimitTimes));
            } else {
                m_timeLimit.setText("");
            }
        }

        if (m_bIsTimeLimitStatusEdit == false) {
            if (setting.HTimeLimitTimes > 0) {
                m_timeLimitDisconnectBtn.setEnabled(true);
                if (setting.HTimeLimitFlag == OVER_TIME_STATE.Disable) {
                    // off
                    m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
                } else {
                    // on
                    m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
                }
            } else {
                m_timeLimitDisconnectBtn.setEnabled(false);
                m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            }
        }

    }

    private void intTimeLimitEdit() {
        m_timeLimit = (EditText) this.findViewById(R.id.set_time_limit_value);
        m_timeLimit.setText("");
        m_timeLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(String.valueOf(s))) {
                    int time = Integer.valueOf(String.valueOf(s));
                    if (time < 0) {
                        m_timeLimit.setText("1");
                        m_timeLimit.setSelection(m_timeLimit.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().equalsIgnoreCase("0")) {
                    m_timeLimit.setText("");
                }
            }
        });

        // m_timeLimit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        //
        //     public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //
        //         // EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
        //         if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
        //             setSettingTimeLimit();
        //             // temporary behavior :just cancel edit focus
        //             m_timeLimitDisconnectBtn.requestFocusFromTouch();
        //             InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //             imm.hideSoftInputFromWindow(m_timeLimit.getWindowToken(), 0);
        //         }
        //
        //         return false;
        //     }
        //
        // });
    }

    private void setSettingTimeLimit() {
        int lTime = 0;
        if (m_timeLimit.getText().toString().length() > 0) {
            lTime = Integer.parseInt(m_timeLimit.getText().toString());
        }

        if (lTime > MAX_DISCONNECT_TIME_VALUE) {
            lTime = MAX_DISCONNECT_TIME_VALUE;
        }

        UsageSettingModel staticSetting = BusinessMannager.getInstance().getUsageSettings();
        if (lTime == staticSetting.HTimeLimitTimes)
            return;

        m_bIsTimeLimitEdit = true;
        DataValue data = new DataValue();
        data.addParam("time_limit_times", lTime);
        BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET, data);
    }

    private void showUsageAutoDisconnectBtn() {

        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        //if (simState.m_SIMState == SIMState.Accessable) {
        UsageSettingModel usageSetting = BusinessMannager.getInstance().getUsageSettings();
        if (m_bIsAutoDisconnectedEdit == false) {

            m_usageAutoDisconnectBtn.setEnabled(true);
            if (usageSetting.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Disable) {
                // off
                m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            } else {
                // on
                m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
            }
            
            // TOAT: 这段为原代码--> 加入了月流量判断,但后期不做月流量判断
            // if (usageSetting.HMonthlyPlan > 0) {
            //     m_usageAutoDisconnectBtn.setEnabled(true);
            //     if (usageSetting.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Disable) {
            //         // off
            //         m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            //     } else {
            //         // on
            //         m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
            //     }
            // } else {
            //     m_usageAutoDisconnectBtn.setEnabled(false);
            //     m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            // }
        }
    }

    private void onBtnUsageAutoDisconnectClick() {
        Logger.t("ma_usage").v("click disconnect");
        UsageSettingModel usageSetting = BusinessMannager.getInstance().getUsageSettings();
        // TOAT: 把月流量判断取消
        //if (usageSetting.HMonthlyPlan > 0) {
            m_bIsAutoDisconnectedEdit = true;
            DataValue data = new DataValue();

            if (usageSetting.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Disable) {
                Logger.t("ma_usage").v("click disconnect Disable");
                m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
                data.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Enable);
            } else {
                Logger.t("ma_usage").v("click disconnect enable");
                m_usageAutoDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
                data.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Disable);
            }
            BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET, data);
        //}

    }

    private void onBtnTimeLimitDisconnectClick() {
        m_bIsTimeLimitStatusEdit = true;
        UsageSettingModel usageSetting = BusinessMannager.getInstance().getUsageSettings();
        DataValue data = new DataValue();
        if (usageSetting.HTimeLimitFlag == OVER_TIME_STATE.Disable) {
            m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
            m_timeLimit.setEnabled(true);
            // 测试是否可编辑edittext 
            m_timeLimit.setKeyListener(DigitsKeyListener.getInstance(true, true));
            data.addParam("time_limit_flag", OVER_TIME_STATE.Enable);
        } else {
            // on
            m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            m_timeLimit.setEnabled(false);
            m_timeLimit.setKeyListener(null);
            data.addParam("time_limit_flag", OVER_TIME_STATE.Disable);
        }
        BusinessMannager.getInstance().sendRequestMessage(MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET, data);
    }

    private void showRoamingAutoDisconnectBtn() {

        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        //if (simState.m_SIMState == SIMState.Accessable) {
        UsageSettingModel usageSetting = BusinessMannager.getInstance().getUsageSettings();
        ConnectionSettingsModel connectionSetting = BusinessMannager.getInstance().getConnectSettings();
        if (m_bIsRoamingDisconnectedEdit == false) {
            //				if (usageSetting.HMonthlyPlan > 0) {
            m_roamingDisconnectBtn.setEnabled(true);
            if (connectionSetting.HRoamingConnect == OVER_ROAMING_STATE.Disable) {
                // off
                m_roamingDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            } else {
                // on
                m_roamingDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
            }
        }
    }

    private void onBtnRoamingAutoDisconnectClick() {
        m_bIsRoamingDisconnectedEdit = true;
        ConnectionSettingsModel connectionSetting = BusinessMannager.getInstance().getConnectSettings();
        DataValue data = new DataValue();
        if (connectionSetting.HRoamingConnect == OVER_ROAMING_STATE.Disable) {
            m_roamingDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
            data.addParam("roaming_connect_flag", OVER_ROAMING_STATE.Enable);
        } else {
            m_roamingDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
            data.addParam("roaming_connect_flag", OVER_ROAMING_STATE.Disable);
        }
        BusinessMannager.getInstance().sendRequestMessage(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET, data);

    }
}
