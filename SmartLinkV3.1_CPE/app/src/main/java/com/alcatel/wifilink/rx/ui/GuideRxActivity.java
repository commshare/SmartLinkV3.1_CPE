package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.art.zok.autoview.AutoViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideRxActivity extends BaseActivityWithBack {

    @BindView(R.id.autoVp)
    AutoViewPager autoVp;
    private View view_guide1;
    private View view_guide2;
    private View view_guide3;
    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_rx);
        ButterKnife.bind(this);
        initRes();
        initAdapter();
    }


    private void initRes() {
        // 提交标记
        SP.getInstance(this).putBoolean(Cons.GUIDE_RX, true);
        // 初始化资源
        views = new ArrayList<>();
        view_guide1 = View.inflate(this, R.layout.guiderx1, null);
        view_guide2 = View.inflate(this, R.layout.guiderx2, null);
        view_guide3 = View.inflate(this, R.layout.guiderx3, null);
        views.add(view_guide1);
        views.add(view_guide2);
        views.add(view_guide3);
    }

    private void initAdapter() {
        autoVp.setPagerAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = null;
                switch (position) {
                    case 0:
                        view = getLayoutInflater().inflate(R.layout.guiderx1, container, false);
                        break;
                    case 1:
                        view = getLayoutInflater().inflate(R.layout.guiderx2, container, false);
                        break;
                    case 2:
                        view = getLayoutInflater().inflate(R.layout.guiderx3, container, false);
                        view.findViewById(R.id.bt_guide_rx_start).setOnClickListener(v -> {
                            CA.toActivity(GuideRxActivity.this, LoginRxActivity.class, false, true, false, 0);
                        });
                        break;
                }
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }
}
