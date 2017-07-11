package com.alcatel.wifilink.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alcatel.wifilink.Constants;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.view.CirclePageIndicator;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    private static final String TAG = "LoadingActivity";
    private final int SPLASH_DELAY = 1000;

    private Handler mHandler = new Handler();

    private SharedPreferences mSharedPrefs;
    private boolean mIsFirstRun = true;

    private ImageView mLogoImg;

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private List<View> mViews;

    private CirclePageIndicator mPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();

        mSharedPrefs = getSharedPreferences(LoadingActivity.class.getSimpleName(), MODE_PRIVATE);
        mHandler.postDelayed(this::startApp, SPLASH_DELAY);
    }

    private void initViews() {
        mLogoImg = (ImageView) findViewById(R.id.iv_logo);
        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        mPageIndicator = (CirclePageIndicator) findViewById(R.id.page_indicator);

        LayoutInflater inflater = LayoutInflater.from(this);
        mViews = new ArrayList<>();
        mViews.add(inflater.inflate(R.layout.what_new_one, null));
        mViews.add(inflater.inflate(R.layout.what_new_two, null));
        View view = inflater.inflate(R.layout.what_new_three, null);
        view.findViewById(R.id.btn_start).setOnClickListener(v -> {
            launchNextActivity();
        });
        mViews.add(view);

        mPagerAdapter = new GuidePagerAdapter(mViews);
        mViewPager.setAdapter(mPagerAdapter);
        mPageIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }

    private void startApp() {
        mIsFirstRun = mSharedPrefs.getBoolean(Constants.KEY_FIRST_RUN, true);
        Log.d(TAG, "startApp, mIsFirstRun:" + mIsFirstRun);
        if (mIsFirstRun) {
            showGuidePager();
        } else {
            launchNextActivity();
        }
    }

    private void launchNextActivity() {

        if (!isWifiConnected()) {
            launchRefreshWifiActivity();
            return;
        }

        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                Log.d(TAG, "login state:" + result.getState());
                if (result.getState() == ENUM.UserLoginStatus.LOGIN.ordinal()) {
                    launchHomeActivity();
                } else {
                    launchLoginActivity(result.getLoginRemainingTimes());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onResultError " + e);
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    launchRefreshWifiActivity();
                }
            }
        });
    }

    private void showGuidePager() {
        mLogoImg.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mPageIndicator.setVisibility(View.VISIBLE);
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    private void launchRefreshWifiActivity() {
        Intent intent = new Intent(this, RefreshWifiActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchLoginActivity(int remainTimes) {
        // Intent intent = new Intent(this, LoginActivity.class);
        // intent.putExtra("remain_times", remainTimes);
        // startActivity(intent);
        // finish();
        ChangeActivity.toActivity(this, LoginActivity.class, true, true, false, 0);
    }


    private class GuidePagerAdapter extends PagerAdapter {
        List<View> mList;

        public GuidePagerAdapter(List<View> guideViews) {
            mList = guideViews;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mList.get(position);
            container.addView(view);
            return view;
        }
    }
}
