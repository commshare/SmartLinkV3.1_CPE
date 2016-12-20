package com.alcatel.smartlinkv3.ui.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.OVER_TIME_STATE;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.SdSharingActivity;
import com.alcatel.smartlinkv3.ui.activity.UsageSettingActivity;
import com.alcatel.smartlinkv3.ui.activity.WpsMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MorePopWindow extends PopupWindow implements OnClickListener{
	private View conentView;
	LinearLayout m_usage_setting;
	LinearLayout m_clear_history;

	public MorePopWindow(final Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.more_popup_dialog, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		this.setContentView(conentView);
		this.setWidth(w / 2 + 50);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable dw = new ColorDrawable(0000000000);
		this.setBackgroundDrawable(dw);
		//this.setAnimationStyle(R.style.AnimationPreview);
		
		m_usage_setting = (LinearLayout) conentView
				.findViewById(R.id.usage_setting_layout);
		m_usage_setting.setOnClickListener(this);
		
		m_clear_history = (LinearLayout) conentView
				.findViewById(R.id.clear_history_layout);
		m_clear_history.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(); 
		String msgRes = null;
		switch (v.getId()) {
		case R.id.usage_setting_layout:
            intent.setClass(v.getContext(), UsageSettingActivity.class);  
            v.getContext().startActivity(intent);
            this.dismiss();
			break;
		case R.id.clear_history_layout:
			SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
			ConnectStatusModel connectStatus = BusinessMannager.getInstance().getConnectStatus();
			if (connectStatus.m_connectionStatus == ConnectionStatus.Disconnected) {
				DataValue data = new DataValue();
				SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = new Date();
				String strDate = sDate.format(now);
				data.addParam("clear_time", strDate);
				BusinessMannager.getInstance().sendRequestMessage(
						MessageUti.STATISTICS_CLEAR_ALL_RECORDS_REQUSET, data);
			}else {
				msgRes = v.getContext().getString(R.string.usage_comsumptionexplain_label);
				Toast.makeText(v.getContext(), msgRes,Toast.LENGTH_SHORT).show();
			}
			this.dismiss();
			break;	
		}
	}



	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -16);
		} else {
			this.dismiss();
		}
	}
}
