package com.alcatel.wifilink.ui.activity;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.business.model.UsageSettingModel;
import com.alcatel.wifilink.common.CommonUtil;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.OVER_DISCONNECT_STATE;
import com.alcatel.wifilink.common.ENUM.OVER_TIME_STATE;
import com.alcatel.wifilink.common.ENUM.SIMState;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.httpservice.BaseResponse;

import java.text.DecimalFormat;



public class UsageSettingActivity extends BaseActivity implements OnClickListener{

	private Context context;

	private UsageSettingReceiver m_usettingreceiver = new UsageSettingReceiver();
	
	private final static int MONTHLY_MAX_VALUE = 1048576; // megabyte
	private final static int BILLING_MAX_VALUE = 31; // megabyte
	public static final int USAGE_ALERT_VALUE = 100;
	private final static int MAX_DISCONNECT_TIME_VALUE = 9999;

	private TextView tvback;
	
	private EditText m_billingValue;
	private EditText m_monthlyValue;
	private long m_monthlyVal = 0;
	private int m_billingVal = 0;
	private int m_alertVal = 0;
	private TextView m_consumptionValue;
	private boolean m_bIsMonthlyValueEdit = false;
	private boolean m_bIsBillingValueEdit = false;
	private boolean m_bIsAlertValueEdit = false;
	
	private Button m_timeLimitDisconnectBtn;
	private EditText m_timeLimit;
	private boolean m_bIsTimeLimitEdit = false;
	private boolean m_bIsTimeLimitStatusEdit = false;

	private Button m_usageAutoDisconnectBtn;
	private boolean m_bIsAutoDisconnectedEdit = false;
	
	private Button m_unit_selector;
	private Button mbBtn;
	private Button gbBtn;
	private Button confirmBtn;
	private TextView monthlyPlanUnit;

	private boolean m_isMB = true;
	public static final String SETTING_USAGE_ALERT_VALUE = "SettingUsageAlertValue";
	public static final String SETTING_MONTHLY_DATA_UNIT = "SettingMonthlyDataUnit";
	private EditText usage_alert_value_edt;

	private class UsageSettingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();
			if(ok && intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
					updateUI();
			} else if (intent.getAction().equals(MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET)) {
				String msgRes = null;
				if (ok) {
					msgRes = UsageSettingActivity.this.getString(R.string.usage_clear_history_success);
					Toast.makeText(UsageSettingActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}else{
					msgRes = UsageSettingActivity.this.getString(R.string.usage_clear_history_fail);
					Toast.makeText(UsageSettingActivity.this, msgRes,Toast.LENGTH_SHORT).show();
				}
			}else if (intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)
					|| intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)) {

				if (ok) {
					updateUI();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET)) {
				if (ok) {
					m_bIsBillingValueEdit= false;
					showSettingBilling();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET)) {
				if (ok) {
					m_bIsAlertValueEdit= false;
//					showSettingAlert();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET)) {
				m_bIsMonthlyValueEdit = false;
				if (ok) {
					showSettingMonthly();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_USED_DATA_REQUSET)) {
				if (ok) {
					//updateUI();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET)) {
				m_bIsTimeLimitStatusEdit = false;
				showTimeLimitInfo();
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET)) {
				if (ok) {
					m_bIsTimeLimitEdit = false;
					showTimeLimitInfo();
				}
			} else if (intent.getAction().equals(MessageUti.STATISTICS_SET_USED_TIMES_REQUSET)) {
				if (ok) {
					//updateUI();
				}
			} else if (intent.getAction().equals(MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET)) {
				m_bIsAutoDisconnectedEdit = false;
				showUsageAutoDisconnectBtn();
			}else if (intent.getAction().equals(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET)) {
//				showRoamingAutoDisconnectBtn();
			}else if (intent.getAction().equals(MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET)) {
				if (ok) {
//					showRoamingAutoDisconnectBtn();
				}
			}else if (intent.getAction().equals(MessageUti.STATISTICS_SET_UNIT_REQUSET)) {
				if (ok) {
					setSettingMonthly();
				}
			}
		}
	}

	private void registerReceiver() {
		// advanced
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET));
		
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.WAN_CONNECT_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.WAN_DISCONNECT_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_DATA_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_USED_TIMES_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET));
		this.registerReceiver(m_usettingreceiver, new IntentFilter(
				MessageUti.STATISTICS_SET_UNIT_REQUSET));

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usage_setting_view);
		context = this;
		getWindow().setBackgroundDrawable(null);
		ImageButton bnBack = (ImageButton) this.findViewById(R.id.btn_back);
		bnBack.setOnClickListener(this);
		tvback = (TextView) this.findViewById(R.id.Back);
		tvback.setOnClickListener(this);
		initMonthlyEdit();
		initBillingEdit();
		m_consumptionValue = (TextView) this.findViewById(R.id.consumption_value_tag);
		
		m_timeLimitDisconnectBtn = (Button) this.findViewById(R.id.enable_time_limit_btn);
		m_timeLimitDisconnectBtn.setOnClickListener(this);
		intTimeLimitEdit();

		m_usageAutoDisconnectBtn = (Button) this.findViewById(R.id.enable_auto_disconnected_btn);
		m_usageAutoDisconnectBtn.setOnClickListener(this);

		monthlyPlanUnit = ((TextView) findViewById(R.id.monthly_plan_unit));
		monthlyPlanUnit.setText(SP.getInstance(this).getString(SETTING_MONTHLY_DATA_UNIT, "MB"));

		initUsageAlertEdit();
		showSettingAlert();
	}

	private void showSettingAlert() {
		int alertV = SP.getInstance(this).getInt(SETTING_USAGE_ALERT_VALUE, 0);
		if (alertV != 0){
			if (alertV <= 100){
				usage_alert_value_edt.setText(alertV + "");
			} else {
				usage_alert_value_edt.setText("100");
			}
		}
	}

	private void initUsageAlertEdit() {
		usage_alert_value_edt = ((EditText) findViewById(R.id.usage_alert_value));
		usage_alert_value_edt.setText("");
		usage_alert_value_edt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 1
						&& s.toString().equalsIgnoreCase("0")) {
					usage_alert_value_edt.setText("");
				}
			}
		});

		usage_alert_value_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				// EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					setUsageAlertValue();
					// temporary behavior :just cancel edit focus
					m_timeLimitDisconnectBtn.requestFocusFromTouch();
					InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(usage_alert_value_edt.getWindowToken(), 0);
				}

				return false;
			}
		});
	}

	private void setUsageAlertValue() {
		int usage = 0;

		if (usage_alert_value_edt.getText().toString().length() > 0) {
			usage = (int) Long.parseLong(usage_alert_value_edt.getText().toString());
			alertValue = usage;
		}
		if (usage != m_alertVal) {
			if (usage > USAGE_ALERT_VALUE) {
				usage = USAGE_ALERT_VALUE;
				usage_alert_value_edt.setText("" + usage);
			}
			UsageSettingModel staticSetting = BusinessManager.getInstance().getUsageSettings();
			if(usage == staticSetting.HUsageAlertValue)
				return;

			m_bIsAlertValueEdit = true;
			DataValue usageData = new DataValue();
			usageData.addParam("alert_value", usage);
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_ALERT_VALUE_REQUSET, usageData);
		} else {
			usage_alert_value_edt.setText("" + m_alertVal);
		}

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
//		showSettingAlert();
		showTimeLimitInfo();
		showUsageAutoDisconnectBtn();
		
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
		m_bIsAlertValueEdit = false;
		m_bIsTimeLimitEdit = false;
		m_bIsTimeLimitStatusEdit = false;
		m_bIsAutoDisconnectedEdit = false;

		if (!usage_alert_value_edt.getText().equals("0")){
			SP.getInstance(this).putInt(SETTING_USAGE_ALERT_VALUE, alertValue);
		}

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
			case R.id.Back:
				this.finish();
				break;
			case R.id.unit_selector:
				onBtnUsageSelectedUint();
				break;
			case R.id.enable_auto_disconnected_btn:
				onBtnUsageAutoDisconnectClick();
				break;
			case R.id.enable_time_limit_btn:
				onBtnTimeLimitDisconnectClick();
				break;
			default:
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
					m_timeLimitDisconnectBtn.requestFocusFromTouch();
					InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_billingValue.getWindowToken(), 0);
				}

				return false;
			}

		});
	}
	
	private void showSettingBilling() {
		SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			UsageSettingModel statistic = BusinessManager.getInstance()
					.getUsageSettings();

			m_billingValue.setEnabled(true);
			m_billingVal = statistic.HBillingDay;
			if (m_billingVal > 0) {
				if (!(m_billingValue.isFocused() || m_bIsBillingValueEdit))
					m_billingValue.setText("" + m_billingVal);
			} else {
				if (!(m_billingValue.isFocused() || m_bIsBillingValueEdit))
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
			UsageSettingModel staticSetting = BusinessManager.getInstance().getUsageSettings();
			if(usage == staticSetting.HBillingDay)
				return;
			
			m_bIsBillingValueEdit = true;
			DataValue usageData = new DataValue();
			usageData.addParam("billing_day", usage);
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_BILLING_DAY_REQUSET, usageData);
		} else {
			m_billingValue.setText("" + m_billingVal);
		}

	}
	
	private void initMonthlyEdit() {
		m_monthlyValue = (EditText) findViewById(R.id.monthly_plan_value);
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
					m_timeLimitDisconnectBtn.requestFocusFromTouch();
					InputMethodManager imm = (InputMethodManager) UsageSettingActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_monthlyValue.getWindowToken(), 0);
				}

				return false;
			}

		});
		
		m_unit_selector = (Button) this.findViewById(R.id.unit_selector);
		m_unit_selector.setOnClickListener(this);
	}

	private void onBtnUsageSelectedUint() {
		final Dialog dialog = new Dialog(this, R.style.Theme_Light_Dialog);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_monthly_selector_unit,null);
		//获得dialog的window窗口
		Window window = dialog.getWindow();
		//设置dialog在屏幕底部
		window.setGravity(Gravity.BOTTOM);
		//设置dialog弹出时的动画效果，从屏幕底部向上弹出
		window.setWindowAnimations(R.style.dialogStyle);
		window.getDecorView().setPadding(0, 0, 0, 0);
		//获得window窗口的属性
		android.view.WindowManager.LayoutParams lp = window.getAttributes();
		//设置窗口宽度为充满全屏
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		//设置窗口高度为包裹内容
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//将设置好的属性set回去
		window.setAttributes(lp);
		//将自定义布局加载到dialog上
		dialog.setContentView(dialogView);
		mbBtn = ((Button) dialogView.findViewById(R.id.mb_btn));
		gbBtn = ((Button) dialogView.findViewById(R.id.gb_btn));
		confirmBtn = ((Button) dialogView.findViewById(R.id.confirm_btn));
		dialog.show();
		mbBtn.setPressed(m_isMB);
		gbBtn.setPressed(!m_isMB);
		mbBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mbBtn.setTextColor(getResources().getColor(R.color.mg_blue));
				gbBtn.setTextColor(getResources().getColor(R.color.black_text));
				m_isMB = true;
			}
		});
		gbBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				gbBtn.setTextColor(getResources().getColor(R.color.mg_blue));
				mbBtn.setTextColor(getResources().getColor(R.color.black_text));
				gbBtn.setSelected(true);
				m_isMB = false;
			}
		});
		confirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				if (m_isMB){
					monthlyPlanUnit.setText(R.string.mb_text);
					SP.getInstance(context).putString(SETTING_MONTHLY_DATA_UNIT, "MB");

					DataValue data = new DataValue();
					data.addParam("unit", 0);
					BusinessManager.getInstance().sendRequestMessage(
							MessageUti.STATISTICS_SET_UNIT_REQUSET, data);
				} else {
					monthlyPlanUnit.setText(R.string.gb_text);
					SP.getInstance(context).putString(SETTING_MONTHLY_DATA_UNIT, "GB");

					DataValue data = new DataValue();
					data.addParam("unit", 1);
					BusinessManager.getInstance().sendRequestMessage(
							MessageUti.STATISTICS_SET_UNIT_REQUSET, data);
				}
			}
		});
	}


	private void showSettingMonthly() {
		SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
		if (simState.m_SIMState == SIMState.Accessable) {
			UsageSettingModel statistic = BusinessManager.getInstance()
					.getUsageSettings();
			
			m_consumptionValue.setText(CommonUtil.ConvertTrafficMB(this, statistic
								             .HUsedData));

			m_monthlyValue.setEnabled(true);
			m_monthlyVal = statistic.HMonthlyPlan;
			if (m_monthlyVal > 0) {
				if (!(m_monthlyValue.isFocused() || m_bIsMonthlyValueEdit)){
					if(statistic.HUnit == 0){
						m_monthlyValue.setText("" + byte2megabyte(m_monthlyVal));
					}
					else{
						m_monthlyValue.setText("" + new DecimalFormat("0.###").format(byte2gegabyte(m_monthlyVal)));
					}
				}
					
			} else {
				if (!(m_monthlyValue.isFocused() || m_bIsMonthlyValueEdit))
					m_monthlyValue.setText("");
			}

		} else {
			m_monthlyValue.clearFocus();
			m_monthlyValue.setEnabled(false);
		}
	}
	
	private void setSettingMonthly() {
		double usage = 0;

		if (m_monthlyValue.getText().toString().length() > 0) {
			usage =  Double.parseDouble(m_monthlyValue.getText().toString());
		}
		UsageSettingModel staticSetting = BusinessManager.getInstance().getUsageSettings();
		if ((usage != this.byte2megabyte(m_monthlyVal)&&staticSetting.HUnit == 0) || 
			 (usage != this.byte2gegabyte(m_monthlyVal)&&staticSetting.HUnit == 1)) {
			if((long)usage == staticSetting.HMonthlyPlan)
				return;
			
			if(staticSetting.HUnit == 0){
				if (usage > MONTHLY_MAX_VALUE) {
					usage = MONTHLY_MAX_VALUE;
					String strInfo = getString(R.string.usage_maximum_monthly_plan_notice) + "1048576 MB";
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			else{
				if (usage > MONTHLY_MAX_VALUE/1024) {
					usage = MONTHLY_MAX_VALUE/1024;
					String strInfo = getString(R.string.usage_maximum_monthly_plan_notice) + "1024 GB";
					Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			
			m_bIsMonthlyValueEdit = true;
			DataValue usageData = new DataValue();
			if(usage <= 0)
			{
				usageData.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Disable);
				m_usageAutoDisconnectBtn.setEnabled(false);
				m_usageAutoDisconnectBtn
						.setBackgroundResource(R.drawable.switch_off);
			}
			if(staticSetting.HUnit == 0){
				usageData.addParam("monthly_plan", megabyte2byte((long) usage));
				m_monthlyVal = megabyte2byte((long) usage);
			}
			else{
				usageData.addParam("monthly_plan", gegabyte2byte(usage));
				m_monthlyVal = gegabyte2byte(usage);
			}
			
			
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_MONTHLY_PLAN_REQUSET, usageData);
		} else {
			m_monthlyValue.setText("" + byte2megabyte(m_monthlyVal));
		}

	}
	
	private long megabyte2byte(long megabyte) {
		return megabyte * 1024 * 1024;
	}
	
	private long gegabyte2byte(double gegabyte) {
		return (long) (gegabyte * 1024 * 1024 * 1024);
	}

	private long byte2megabyte(long byteV) {
		return byteV / (1024 * 1024);
	}
	
	private float byte2gegabyte(long byteV) {
		return (float)byte2megabyte(byteV) / (float)1024;
	}
	
	
	private void showTimeLimitInfo() {
		m_timeLimit.setEnabled(false);

		UsageSettingModel setting = BusinessManager.getInstance()
				.getUsageSettings();

		if (!(m_timeLimit.isInputMethodTarget() || this.m_bIsTimeLimitEdit)) {
			if (setting.HTimeLimitTimes > 0) {
				m_timeLimit.setText(String.valueOf(setting.HTimeLimitTimes));
			} else {
				m_timeLimit.setText("");
			}
		}

		if(!m_bIsTimeLimitStatusEdit) {
			if (setting.HTimeLimitTimes > 0) {
				m_timeLimitDisconnectBtn.setEnabled(true);
				if (setting.HTimeLimitFlag == OVER_TIME_STATE.Disable) {
					// off
					m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_off);
				} else {
					// on
					m_timeLimitDisconnectBtn.setBackgroundResource(R.drawable.switch_on);
					m_timeLimit.setEnabled(true);
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
							m_timeLimitDisconnectBtn.requestFocusFromTouch();
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
		
		UsageSettingModel staticSetting = BusinessManager.getInstance().getUsageSettings();
		if(lTime == staticSetting.HTimeLimitTimes)
			return;

		m_bIsTimeLimitEdit = true;
		DataValue data = new DataValue();
		data.addParam("time_limit_times", lTime);
		BusinessManager.getInstance().sendRequestMessage(
				MessageUti.STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET, data);
	}
	
	private void showUsageAutoDisconnectBtn() {
		UsageSettingModel usageSetting = BusinessManager.getInstance().getUsageSettings();
		if(!m_bIsAutoDisconnectedEdit) {
			if (usageSetting.HMonthlyPlan > 0) {
				m_usageAutoDisconnectBtn.setEnabled(true);
				if (usageSetting.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Disable) {
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
	}
	
	private void onBtnUsageAutoDisconnectClick() {
		UsageSettingModel usageSetting = BusinessManager.getInstance()
				.getUsageSettings();
		if(usageSetting.HMonthlyPlan > 0)
		{
			m_bIsAutoDisconnectedEdit = true;
			DataValue data = new DataValue();
			
			if (usageSetting.HAutoDisconnFlag == OVER_DISCONNECT_STATE.Disable) {
				m_usageAutoDisconnectBtn
						.setBackgroundResource(R.drawable.switch_on);
				data.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Enable);
			} else {
				m_usageAutoDisconnectBtn
						.setBackgroundResource(R.drawable.switch_off);
				data.addParam("auto_disconn_flag", OVER_DISCONNECT_STATE.Disable);
			}
			BusinessManager.getInstance().sendRequestMessage(
					MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET,
					data);
		}
	}
	
	private void onBtnTimeLimitDisconnectClick() {
		m_bIsTimeLimitStatusEdit = true;
		UsageSettingModel usageSetting = BusinessManager.getInstance()
				.getUsageSettings();
		DataValue data = new DataValue();
		if (usageSetting.HTimeLimitFlag == OVER_TIME_STATE.Disable) {
			m_timeLimitDisconnectBtn
					.setBackgroundResource(R.drawable.switch_on);
			m_timeLimit.setEnabled(true);
			data.addParam("time_limit_flag", OVER_TIME_STATE.Enable);
		} else {
			// on
			m_timeLimitDisconnectBtn
					.setBackgroundResource(R.drawable.switch_off);
			m_timeLimit.setEnabled(false);
			data.addParam("time_limit_flag", OVER_TIME_STATE.Disable);
		}
		BusinessManager.getInstance().sendRequestMessage(
				MessageUti.STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET,
				data);
	}
}
