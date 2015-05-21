package com.alcatel.smartlinkv3.ui.activity;

import java.util.List;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewPagerAdapter extends PagerAdapter {
	private List<View> mViews;
	private Activity mActivity;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.mViews = views;
		this.mActivity = activity;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViews.get(position));
	}

	@Override
	public int getCount() {
		if (mViews != null) {
			return mViews.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViews.get(position), 0);
		if (position == (mViews.size() - 1)) {
			Button startBtn = (Button) container.findViewById(R.id.btn_start);
			startBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setGuided();
					goHome();
				}
			});
		}
		return mViews.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	private void setGuided() {
		SharedPreferences preferences = mActivity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

	private void goHome() {
		Intent intent = new Intent(mActivity, LoadingActivity.class);
		mActivity.startActivity(intent);
		mActivity.finish();
	}
}