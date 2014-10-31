package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.ui.activity.SdSharingActivity;
import com.alcatel.smartlinkv3.ui.activity.WpsMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


public class AddPopWindow extends PopupWindow implements OnClickListener{
	private View conentView;
	LinearLayout m_sd_sharing;
	LinearLayout m_wps;
	LinearLayout m_logout;

	public AddPopWindow(final Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.add_popup_dialog, null);
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
		m_sd_sharing = (LinearLayout) conentView
				.findViewById(R.id.sd_sharing_layout);
		m_sd_sharing.setOnClickListener(this);
		
		m_wps = (LinearLayout) conentView
				.findViewById(R.id.wps_layout);
		m_wps.setOnClickListener(this);
		
		m_logout = (LinearLayout) conentView
				.findViewById(R.id.logout_layout);
		m_logout.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(); 
		switch (v.getId()) {
		case R.id.sd_sharing_layout:
            intent.setClass(v.getContext(), SdSharingActivity.class);  
            v.getContext().startActivity(intent);
			break;
		case R.id.wps_layout:
			intent.setClass(v.getContext(), WpsMainActivity.class);  
            v.getContext().startActivity(intent);
			break;
		case R.id.logout_layout:
			userLogout();
			break;
		}
		this.dismiss();
		
	}


	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -16);
		} else {
			this.dismiss();
		}
	}
	
	public void userLogout() {
		UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
		if (m_loginStatus != null && m_loginStatus == UserLoginStatus.selfLogined) {
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.USER_LOGOUT_REQUEST, null);
		}
	}
}
