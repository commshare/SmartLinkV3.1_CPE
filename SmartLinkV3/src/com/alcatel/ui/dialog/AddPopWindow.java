package com.alcatel.ui.dialog;

import com.alcatel.R;
import com.alcatel.ui.activity.StorageMainActivity;
import com.alcatel.ui.activity.WpsMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
            intent.setClass(v.getContext(), StorageMainActivity.class);  
            v.getContext().startActivity(intent);
            this.dismiss();
			break;
		case R.id.wps_layout:
			intent.setClass(v.getContext(), WpsMainActivity.class);  
            v.getContext().startActivity(intent);
            this.dismiss();
			break;
		case R.id.logout_layout:
			this.dismiss();
			break;
		}
		
	}


	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
		} else {
			this.dismiss();
		}
	}
}
