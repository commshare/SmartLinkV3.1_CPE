package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements OnPageChangeListener {
    private ViewPager mVP;
    private ViewPagerAdapter mVPAdapter;
    private List<View> mViews;
    private ImageView[] mDots;
    private int mCurrentIndex;
    private LinearLayout mLL;

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

        mVPAdapter = new ViewPagerAdapter(mViews, this);
        mVP = (ViewPager) findViewById(R.id.viewpager);
        mVP.setAdapter(mVPAdapter);
        mVP.setOnPageChangeListener(this);

        mLL = (LinearLayout) findViewById(R.id.ll);
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
        if (position < 0 || position > mViews.size() - 1 || position == mCurrentIndex) {
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
        switch (arg0) {
            case 0:
            case 1:
                mLL.setVisibility(View.VISIBLE);
                break;

            case 2:
                mLL.setVisibility(View.GONE);

            default:
                break;
        }

        setCurrentDot(arg0);
    }

    class ViewPagerAdapter extends PagerAdapter {
        private List<View> mViews;
        private Activity mActivity;

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
                Button startBtn = (Button) mActivity.findViewById(R.id.btn_start);
                startBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CPEConfig.getInstance().setInitialLaunchedFlag();
                        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
                        Class<?> clazz = bCPEWifiConnected ? QuickSetupActivity.class : RefreshWifiActivity.class;
                        Intent intent = new Intent(mActivity, clazz);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                });
            }

            return mViews.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
