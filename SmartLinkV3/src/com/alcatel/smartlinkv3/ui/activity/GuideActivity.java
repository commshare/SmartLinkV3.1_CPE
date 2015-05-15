package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnPageChangeListener {
	private ViewPager mVP;
	private ViewPagerAdapter mVPAdapter;
	private List<View> mViews;
	private ImageView[] mDots;
	private int mCurrentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		initViews();
		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mViews = new ArrayList<View>();
		mViews.add(inflater.inflate(R.layout.what_new_one, null));
		mViews.add(inflater.inflate(R.layout.what_new_two, null));
		mViews.add(inflater.inflate(R.layout.what_new_three, null));
		mViews.add(inflater.inflate(R.layout.what_new_four, null));

		mVPAdapter = new ViewPagerAdapter(mViews, this);
		mVP = (ViewPager) findViewById(R.id.viewpager);
		mVP.setAdapter(mVPAdapter);
		mVP.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		mDots = new ImageView[mViews.size()];
		for (int i = 0; i < mViews.size(); i++) {
			mDots[i] = (ImageView) ll.getChildAt(i);
			mDots[i].setSelected(false);
		}
		mCurrentIndex = 0;
		mDots[mCurrentIndex].setSelected(true);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > mViews.size() - 1
				|| position == mCurrentIndex) {
			return;
		}
		mDots[position].setSelected(true);
		mDots[mCurrentIndex].setSelected(false);
		mCurrentIndex = position;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}
}