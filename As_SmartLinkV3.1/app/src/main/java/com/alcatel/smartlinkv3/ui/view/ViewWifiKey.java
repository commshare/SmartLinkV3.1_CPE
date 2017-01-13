package com.alcatel.smartlinkv3.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.ui.activity.FragmentWifiSettingTypeSelection;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;
import com.alcatel.smartlinkv3.ui.activity.UsageSettingActivity;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog;

import org.apache.http.HttpRequest;


public class ViewWifiKey extends BaseViewImpl {

	private LinearLayout mUnEditWifiNetworkLinear;
	private LinearLayout mEditWifiNetworkLinear;
	private ViewIfEditBroadcastReceiver viewIfEditBroadcastReceiver;


	public class ViewIfEditBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)){
				mUnEditWifiNetworkLinear.setVisibility(View.VISIBLE);
				mEditWifiNetworkLinear.setVisibility(View.GONE);
			} else if(intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)){
				mUnEditWifiNetworkLinear.setVisibility(View.GONE);
				mEditWifiNetworkLinear.setVisibility(View.VISIBLE);
			}
		}
	}

	public ViewWifiKey(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_wifikey,null);

		mUnEditWifiNetworkLinear = ((LinearLayout) m_view.findViewById(R.id.unEdit_wifi_network_linear));
		mEditWifiNetworkLinear = ((LinearLayout) m_view.findViewById(R.id.edit_wifi_network_linear));

	}

	@Override
	public void onResume() {
		viewIfEditBroadcastReceiver = new ViewIfEditBroadcastReceiver();
		m_context.registerReceiver(viewIfEditBroadcastReceiver, new IntentFilter(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST));
		m_context.registerReceiver(viewIfEditBroadcastReceiver, new IntentFilter(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST));

	}

	@Override
	public void onPause() {
	}

	@Override
	public void onDestroy() {
		try {
			m_context.unregisterReceiver(viewIfEditBroadcastReceiver);
		} catch (Exception e){

		}
	}

}

