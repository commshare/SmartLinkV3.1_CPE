package com.alcatel.smartlinkv3.ui.activity;


import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.ENUM.OVER_TIME_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



public class UsageSettingActivity extends BaseActivity implements OnClickListener{

	private UsageSettingReceiver m_usettingreceiver = new UsageSettingReceiver();
	
	private final static long MONTHLY_MAX_VALUE = 102400; // megabyte
	private final static long BILLING_MAX_VALUE = 31; // megabyte
	private final static int MAX_DISCONNECT_TIME_VALUE = 24 * 60 * 31;
	
	private ImageButton bnBack;
	private EditText m_billingValue;
	private EditText m_monthlyValue;
	private long m_monthlyVal = 0;
	private long m_billingVal = 0;
	private TextView m_consumptionValue;
	private boolean m_bIsMonthlyValueEdit = false;
	private boolean m_bIsBillingValueEdit = false;
	
	private Button m_timeLimitDisconnectBtn;
	private EditText m_timeLimit;
	private boolean m_bIsTimeLimitEdit = false;
	private boolean m_bIsTimeLimitStatusEdit = false;
	
	
	private Button m_roamingDisconnectBtn;
	private Button m_usageAutoDisconnectBtn;
	private boolean m_bIsAutoDisconnectedEdit = false;
	

	private class UsageSettingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			} else if (intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {

				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);

				if (nResult == 0 && strErrorCode.length() == 0) {
				}else{
				}
			}else if (intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				if (nResult == 0) {
					updateUI();
				}
			}else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
				
			} else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}

			else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_USED_DATA_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}

			else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}
			
			else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}
			
			else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_USED_TIMES_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}
			
			else if (intent.getAction().equals(
					MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, 0);
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (nResult == 0 && strErrorCode.length() == 0) {
					updateUI();
				}
			}
		}
	}

	private void registerReceiver() {
		// advanced
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_DATA_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_TIMES_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usage_setting_view);
		
		ImageButton bnBack = (ImageButton) this.findViewById(R.id.btn_back);
		bnBack.setOnClickListener(this);
		
		initMonthlyEdit();
		initBillingEdit();
		m_consumptionValue = (TextView) this.findViewById(R.id.consumption_value_tag);
		
		m_timeLimitDisconnectBtn = (Button) this.findViewById(R.id.enable_time_limit_btn);
		m_timeLimitDisconnectBtn.setOnClickListener(this);
		intTimeLimitEdit();
		
		m_roamingDisconnectBtn = (Button) this.findViewById(R.id.enable_roaming_btn);
		m_roamingDisconnectBtn.setOnClickListener(this);
		
		m_usageAutoDisconnectBtn = (Button) this.findViewById(R.id.enable_auto_disconnected_btn);
		m_usageAutoDisconnectBtn.setOnClickListener(this);
		
		
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
		updateUI();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
    		this.unregisterReceiver(m_usettingreceiver);
    	}catch(Exception e) {
    		
    	}
		
		m_bIsMonthlyValueEdit = false;
		m_bIsBillingValueEdit = false;
		m_bIsTimeLimitEdit = false;
		m_bIsTimeLimitStatusEdit = false;
		m_bIsAutoDisconnectedEdit = false;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
			
		case R.id.enable_auto_disconnected_btn:
			onBtnUsageAutoDisconnectClick();
			break;
			
		case R.id.enable_time_limit_btn:
			onBtnTimeLimitDisconnectClick();
			break;

		}
		
	}
	
	private void updateUI() {
		showSettingBilling();
		showSettingMonthly();
		showTimeLimitInfo();
		showUsageAutoDisconnectBtn();
	}
	
	private void initBillingEdit() {
		m_billingValue = (EditText) this.findViewById(R.id.billing_plan_value);
		m_billingValue.setText("");
		m_billingValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 1
						&& s.toString().equalsIgnoreCase("0")) {
					m_billingValue.setText("");
				}
			}

		});

		m_billingValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				// EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					setSettingBilling();
					// temporary behavior :just cancel edit focus

					InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_billingValue.getWindowToken(), 0);
				}

				return false;
			}

		});
	}
	
	private void showSettingBilling() {
		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			UsageSettingsResult statistic = BusinessMannager.getInstance()
					.getUsageSettings();

			m_billingValue.setEnabled(true);
			m_billingVal = statistic.HBillingDay;
			if (m_billingVal > 0) {
				if (!(m_billingValue.isFocused() == true || m_bIsBillingValueEdit == true))
					m_billingValue.setText("" + byte2megabyte(m_billingVal));
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
		long usage = 0;

		if (m_billingValue.getText().toString().length() > 0) {
			usage = Long.parseLong(m_billingValue.getText().toString());
		}
		if (usage != m_billingVal) {
			if (usage > BILLING_MAX_VALUE) {
				usage = BILLING_MAX_VALUE;
			}
			UsageSettingsResult staticSetting = BusinessMannager.getInstance().getUsageSettings();
			if(usage == staticSetting.HBillingDay)
				return;
			
			m_bIsBillingValueEdit = true;
			DataValue usageData = new DataValue();
			usageData.addParam("billing_day", megabyte2byte(usage));
			m_billingVal = megabyte2byte(usage);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET, usageData);
		} else {
			m_billingValue.setText("" + byte2megabyte(m_billingVal));
		}

	}
	
	private void initMonthlyEdit() {
		m_monthlyValue = (EditText) this.findViewById(R.id.monthly_plan_value);
		m_monthlyValue.setText("");
		m_monthlyValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 1
						&& s.toString().equalsIgnoreCase("0")) {
					m_monthlyValue.setText("");
				}
			}

		});

		m_monthlyValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				// EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					setSettingMonthly();
					// temporary behavior :just cancel edit focus

					InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_monthlyValue.getWindowToken(), 0);
				}

				return false;
			}

		});
	}
	
	private void showSettingMonthly() {
		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			UsageSettingsResult statistic = BusinessMannager.getInstance()
					.getUsageSettings();

			m_monthlyValue.setEnabled(true);
			m_monthlyVal = statistic.HMonthlyPlan;
			if (m_monthlyVal > 0) {
				if (!(m_monthlyValue.isFocused() == true || m_bIsMonthlyValueEdit == true))
					m_monthlyValue.setText("" + byte2megabyte(m_monthlyVal));
			} else {
				if (!(m_monthlyValue.isFocused() == true || m_bIsMonthlyValueEdit == true))
					m_monthlyValue.setText("");
			}

		} else {
			m_monthlyValue.clearFocus();
			m_monthlyValue.setEnabled(false);
		}

		showUsageAutoDisconnectBtn();
	}
	
	private void setSettingMonthly() {
		long usage = 0;

		if (m_monthlyValue.getText().toString().length() > 0) {
			usage = Long.parseLong(m_monthlyValue.getText().toString());
		}
		if (usage != this.byte2megabyte(m_monthlyVal)) {
			if (usage > MONTHLY_MAX_VALUE) {
				usage = MONTHLY_MAX_VALUE;
			}
			UsageSettingsResult staticSetting = BusinessMannager.getInstance().getUsageSettings();
			if(usage == staticSetting.HMonthlyPlan)
				return;
			
			m_bIsMonthlyValueEdit = true;
			DataValue usageData = new DataValue();
			usageData.addParam("monthly_plan", megabyte2byte(usage));
			m_monthlyVal = megabyte2byte(usage);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET, usageData);
		} else {
			m_monthlyValue.setText("" + byte2megabyte(m_monthlyVal));
		}

	}
	
	private long megabyte2byte(long megabyte) {
		return megabyte * 1024 * 1024;
	}

	private long byte2megabyte(long byteV) {
		return byteV / (1024 * 1024);
	}
	
	
	private void showTimeLimitInfo() {
		UsageSettingsResult setting = BusinessMannager.getInstance()
				.getUsageSettings();
		if (!(m_timeLimit.isInputMethodTarget() == true || this.m_bIsTimeLimitEdit == true)) {
			if (setting.HTimeLimitTimes > 0) {
				m_timeLimit.setText(String.valueOf(setting.HTimeLimitTimes));
			} else {
				m_timeLimit.setText("");
			}
		}

		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			m_timeLimit.setEnabled(true);
			if(m_bIsTimeLimitStatusEdit == false) {
				if (setting.HTimeLimitTimes > 0) {
					m_timeLimitDisconnectBtn.setEnabled(true);
					if (setting.HTimeLimitFlag == 1) {
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
		} else {
			m_timeLimit.setEnabled(false);
			m_timeLimitDisconnectBtn.setEnabled(false);
			m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
		}

		showUsageAutoDisconnectBtn();
	}

	private void intTimeLimitEdit() {
		m_timeLimit = (EditText) this.findViewById(R.id.set_time_limit_value);
		m_timeLimit.setText("");
		m_timeLimit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 1
						&& s.toString().equalsIgnoreCase("0")) {
					m_timeLimit.setText("");
				}
			}
		});

		m_timeLimit
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {

						// EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
						if (actionId == EditorInfo.IME_ACTION_DONE
								|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
							setSettingTimeLimit();
							// temporary behavior :just cancel edit focus
							InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									m_timeLimit.getWindowToken(), 0);
						}

						return false;
					}

				});
	}

	private void setSettingTimeLimit() {
		int lTime = 0;
		if (m_timeLimit.getText().toString().length() > 0) {
			lTime = Integer.parseInt(m_timeLimit.getText().toString());
		}

		if (lTime > MAX_DISCONNECT_TIME_VALUE) {
			lTime = MAX_DISCONNECT_TIME_VALUE;
		}
		
		UsageSettingsResult staticSetting = BusinessMannager.getInstance().getUsageSettings();
		if(lTime == staticSetting.HTimeLimitTimes)
			return;

		m_bIsTimeLimitEdit = true;
		DataValue data = new DataValue();
		data.addParam("over_time", lTime);
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET, data);
	}
	
	private void showUsageAutoDisconnectBtn() {

		SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			UsageSettingsResult usageSetting = BusinessMannager.getInstance().getUsageSettings();
			if(m_bIsAutoDisconnectedEdit == false) {
				if (usageSetting.HMonthlyPlan > 0) {
					m_usageAutoDisconnectBtn.setEnabled(true);
					if (usageSetting.HAutoDisconnFlag == 1) {
						// off
						m_usageAutoDisconnectBtn
								.setBackgroundResource(R.drawable.switch_off);
					} else {
						// on
						m_usageAutoDisconnectBtn
								.setBackgroundResource(R.drawable.switch_on);
					}
				} else {
					m_usageAutoDisconnectBtn.setEnabled(false);
					m_usageAutoDisconnectBtn
							.setBackgroundResource(R.drawable.switch_off);
				}
			}
		} else {
			m_usageAutoDisconnectBtn.setEnabled(false);
			// set disable pic
			m_usageAutoDisconnectBtn
					.setBackgroundResource(R.drawable.switch_off);
		}
	}
	
	private void onBtnUsageAutoDisconnectClick() {
		m_bIsAutoDisconnectedEdit = true;
		UsageSettingsResult usageSetting = BusinessMannager.getInstance()
				.getUsageSettings();
		DataValue data = new DataValue();
		if (usageSetting.HAutoDisconnFlag == 1) {
			m_usageAutoDisconnectBtn
					.setBackgroundResource(R.drawable.switch_on);
			data.addParam("auto_disconn_flag", 0);
		} else {
			m_usageAutoDisconnectBtn
					.setBackgroundResource(R.drawable.switch_off);
			data.addParam("auto_disconn_flag", 1);
		}
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET,
				data);

	}
	
	private void onBtnTimeLimitDisconnectClick() {
		m_bIsTimeLimitStatusEdit = true;
		UsageSettingsResult usageSetting = BusinessMannager.getInstance()
				.getUsageSettings();
		DataValue data = new DataValue();
		if (usageSetting.HTimeLimitFlag == 1) {
			m_timeLimitDisconnectBtn
					.setBackgroundResource(R.drawable.switch_on);
			data.addParam("over_time_status", OVER_TIME_STATE.Enable);
		} else {
			// on
			m_timeLimitDisconnectBtn
					.setBackgroundResource(R.drawable.switch_off);
			data.addParam("over_time_status", OVER_TIME_STATE.Disable);
		}
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET,
				data);
	}

}