package com.alcatel.ui.view;

import com.alcatel.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


public class AddPopWindow extends PopupWindow {
	private View conentView;

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

		this.setAnimationStyle(R.style.AnimationPreview);
		LinearLayout addTaskLayout = (LinearLayout) conentView
				.findViewById(R.id.add_task_layout);
		LinearLayout teamMemberLayout = (LinearLayout) conentView
				.findViewById(R.id.team_member_layout);
		addTaskLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AddPopWindow.this.dismiss();
			}
		});

		teamMemberLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddPopWindow.this.dismiss();
			}
		});
	}


	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
		} else {
			this.dismiss();
		}
	}
}
