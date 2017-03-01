package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LauncherActivity extends Activity {
	private final int ANIMATION_INTERNAL = 1000;
	private final int LOGO_INTERNAL = 1000;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startActivity(new Intent(LauncherActivity.this,
					LoadingActivity.class));
			overridePendingTransition(0, 0);
			LauncherActivity.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frameLayout = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		frameLayout.setLayoutParams(params);

		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		imageView.setImageResource(R.drawable.loading);
		imageView.setScaleType(ScaleType.CENTER);
		frameLayout.addView(imageView);
		setContentView(frameLayout);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(ANIMATION_INTERNAL);
		imageView.startAnimation(scaleAnimation);
		scaleAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mHandler.sendEmptyMessageDelayed(0, LOGO_INTERNAL);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}
}