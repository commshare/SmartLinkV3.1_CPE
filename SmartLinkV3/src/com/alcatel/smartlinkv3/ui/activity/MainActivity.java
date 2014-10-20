package com.alcatel.smartlinkv3.ui.activity;


import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;
import com.alcatel.smartlinkv3.ui.dialog.AddPopWindow;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;
import com.alcatel.smartlinkv3.ui.view.ViewHome;
import com.alcatel.smartlinkv3.ui.view.ViewIndex;
import com.alcatel.smartlinkv3.ui.view.ViewSetting;
import com.alcatel.smartlinkv3.ui.view.ViewSms;
import com.alcatel.smartlinkv3.ui.view.ViewUsage;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.MotionEvent;

public class MainActivity extends BaseActivity implements OnClickListener,OnGestureListener{
	private int m_preButton = 0;

	private ViewFlipper m_viewFlipper;
	
	private TextView m_homeBtn;
	private TextView m_usageBtn;
	private TextView m_smsBtn;
	private TextView m_settingBtn;


	private TextView m_titleTextView = null;
	private Button m_Btnbar = null;

	private ViewHome m_homeView = null;
	private ViewUsage m_usageView = null;
	private ViewSms m_smsView = null;
	private ViewSetting m_settingView = null;

	public static DisplayMetrics m_displayMetrics = new DisplayMetrics();
	
	private GestureDetector gestureDetector;
	private int pageIndex = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.getWindowManager().getDefaultDisplay()
				.getMetrics(m_displayMetrics);

		m_homeBtn = (TextView) this.findViewById(R.id.main_home);
		m_homeBtn.setOnClickListener(this);
		m_usageBtn = (TextView) this.findViewById(R.id.main_usage);
		m_usageBtn.setOnClickListener(this);
		m_smsBtn = (TextView) this.findViewById(R.id.main_sms);
		m_smsBtn.setOnClickListener(this);
		m_settingBtn = (TextView) this.findViewById(R.id.main_setting);
		m_settingBtn.setOnClickListener(this);

		m_viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		gestureDetector = new GestureDetector(this);


		m_titleTextView = (TextView) this.findViewById(R.id.main_title);
		
		m_Btnbar = (Button) this.findViewById(R.id.btnbar);
		m_Btnbar.setOnClickListener(this);;

		addView();
		setMainBtnStatus(R.id.main_home);
		showView(ViewIndex.VIEW_HOME);
		updateTitleUI(ViewIndex.VIEW_HOME);
		pageIndex = ViewIndex.VIEW_HOME;
	}

	@Override
	public void onResume() {
		super.onResume();

		m_homeView.onResume();
		m_usageView.onResume();
		m_smsView.onResume();
		m_settingView.onResume();
		
		//toPageHomeWhenPinSimNoOk();
	}

	@Override
	public void onPause() {
		super.onPause();
		m_homeView.onPause();
		m_usageView.onPause();
		m_smsView.onPause();
		m_settingView.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		m_homeView.onDestroy();
		m_usageView.onDestroy();
		m_smsView.onDestroy();
		m_settingView.onDestroy();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_home:
			homeBtnClick();
			break;
		case R.id.main_usage:
			usageBtnClick();
			break;
		case R.id.main_sms:
			smsBtnClick();
			break;
		case R.id.main_setting:
			settingBtnClick();
			break;
		case R.id.btnbar:
			if(this.pageIndex == ViewIndex.VIEW_HOME)
			{
				addBtnClick();
			}else if(this.pageIndex == ViewIndex.VIEW_USAGE){
				moreBtnClick();
			}else if(this.pageIndex == ViewIndex.VIEW_SMS){
				editBtnClick();
			}		
			break;
		}
	}

	private void homeBtnClick() {
		if (m_preButton == R.id.main_home) {
			return;
		}
		setMainBtnStatus(R.id.main_home);
		showView(ViewIndex.VIEW_HOME);
		updateTitleUI(ViewIndex.VIEW_HOME);
		pageIndex = ViewIndex.VIEW_HOME;
	}

	private void usageBtnClick() {
		if (m_preButton == R.id.main_usage) {
			return;
		}
		go2UsageView();
	}
	
	private void go2UsageView() {
		setMainBtnStatus(R.id.main_usage);
		showView(ViewIndex.VIEW_USAGE);
		updateTitleUI(ViewIndex.VIEW_USAGE);
		pageIndex = ViewIndex.VIEW_USAGE;
	}
	
	private void smsBtnClick() {
		if (m_preButton == R.id.main_sms) {
			return;
		}		
		go2SmsView();
	}

	private void go2SmsView() {
		setMainBtnStatus(R.id.main_sms);
		showView(ViewIndex.VIEW_SMS);
		updateTitleUI(ViewIndex.VIEW_SMS);
		pageIndex = ViewIndex.VIEW_SMS;
	}

	private void settingBtnClick() {
		if (m_preButton == R.id.main_setting) {
			return;
		}
		go2SettingView();
	}

	private void go2SettingView() {
		setMainBtnStatus(R.id.main_setting);
		showView(ViewIndex.VIEW_SETTINGE);
		updateTitleUI(ViewIndex.VIEW_SETTINGE);
		pageIndex = ViewIndex.VIEW_SETTINGE;
	}

	private void addView() {
		m_homeView = new ViewHome(this);	
		m_viewFlipper.addView(m_homeView.getView(), ViewIndex.VIEW_HOME,
				m_viewFlipper.getLayoutParams());

		m_usageView = new ViewUsage(this);
		m_viewFlipper.addView(m_usageView.getView(),
				ViewIndex.VIEW_USAGE, m_viewFlipper.getLayoutParams());
		
		m_smsView = new ViewSms(this);
		m_viewFlipper.addView(m_smsView.getView(), ViewIndex.VIEW_SMS,
				m_viewFlipper.getLayoutParams());

		m_settingView = new ViewSetting(this);
		m_viewFlipper.addView(m_settingView.getView(),
				ViewIndex.VIEW_SETTINGE, m_viewFlipper.getLayoutParams());
	}

	public void showView(int viewIndex) {
		m_viewFlipper.setDisplayedChild(viewIndex);
	}

	public void updateTitleUI(int viewIndex) {
		if (viewIndex == ViewIndex.VIEW_HOME) {
			m_titleTextView.setText(R.string.main_home);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_plus_icon);
			setMainBtnStatus(R.id.main_home);
		}
		if (viewIndex == ViewIndex.VIEW_USAGE) {
			m_titleTextView.setText(R.string.main_usage);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_more_icon);
			setMainBtnStatus(R.id.main_usage);
		}
		if (viewIndex == ViewIndex.VIEW_SMS) {
			m_titleTextView.setText(R.string.main_sms);
			m_Btnbar.setVisibility(View.VISIBLE);
			m_Btnbar.setBackgroundResource(R.drawable.actionbar_edit_icon);
			setMainBtnStatus(R.id.main_sms);
		}
		if (viewIndex == ViewIndex.VIEW_SETTINGE) {
			m_titleTextView.setText(R.string.main_setting);
			m_Btnbar.setVisibility(View.GONE);
			setMainBtnStatus(R.id.main_setting);
		}
	}

	private void setMainBtnStatus(int nActiveBtnId) {
		m_preButton = nActiveBtnId;
		int nDrawable = nActiveBtnId == R.id.main_home ? R.drawable.main_home_active
				: R.drawable.main_home_grey;
		Drawable d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_homeBtn.setCompoundDrawables(null, d, null, null);
		int nTextColor = nDrawable = nActiveBtnId == R.id.main_home ? R.color.color_blue
				: R.color.color_grey;
		m_homeBtn.setTextColor(this.getResources().getColor(nTextColor));

		//updateNewSmsUI(m_nNewCount);
		nDrawable = nActiveBtnId == R.id.main_usage ? R.drawable.main_usage_active
				: R.drawable.main_usage_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_usageBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_usage ? R.color.color_blue
				: R.color.color_grey;
		m_usageBtn.setTextColor(this.getResources().getColor(nTextColor));

		nDrawable = nActiveBtnId == R.id.main_sms ? R.drawable.main_sms_active
				: R.drawable.main_sms_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_smsBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_sms ? R.color.color_blue
				: R.color.color_grey;
		m_smsBtn.setTextColor(this.getResources().getColor(nTextColor));

		nDrawable = nActiveBtnId == R.id.main_setting ? R.drawable.main_setting_active
				: R.drawable.main_setting_grey;
		d = getResources().getDrawable(nDrawable);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		m_settingBtn.setCompoundDrawables(null, d, null, null);
		nTextColor = nDrawable = nActiveBtnId == R.id.main_setting ? R.color.color_blue
				: R.color.color_grey;
		m_settingBtn.setTextColor(this.getResources().getColor(nTextColor));
	}

	
	private void addBtnClick(){
		AddPopWindow addPopWindow = new AddPopWindow(MainActivity.this);
		addPopWindow.showPopupWindow(m_Btnbar);
	}
	
	private void moreBtnClick(){
		MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
		morePopWindow.showPopupWindow(m_Btnbar);
	}
	
	private void editBtnClick(){
		Intent intent = new Intent();
		intent.setClass(this, ActivityNewSms.class);	
		this.startActivity(intent);
	}
	


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > 120) {
			if (pageIndex < 3) {
				pageIndex++;
				m_viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.animation_right_in));
				m_viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.animation_left_out));
				m_viewFlipper.showNext();
				updateTitleUI(pageIndex);
			}
			return true;
		} 
		else if (e1.getX() - e2.getX() < -120) {
			if (pageIndex > 0) {
				pageIndex--;
				m_viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.animation_left_in));
				m_viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.animation_right_out));
				m_viewFlipper.showPrevious();
				updateTitleUI(pageIndex);
			}
			return true;
		}
		return false;
	}

/*
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {  
		   
        public boolean onTouch(View v, MotionEvent event) {  
            // TODO Auto-generated method stub  
            return gestureDetector.onTouchEvent(event);  
        }  
	};
	*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	} 
}
