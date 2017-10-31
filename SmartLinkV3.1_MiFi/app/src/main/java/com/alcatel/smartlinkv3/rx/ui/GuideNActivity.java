package com.alcatel.smartlinkv3.rx.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.rx.adapter.GuideAdapter;
import com.zhy.android.percent.support.PercentLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideNActivity extends BaseRxActivity {

    @BindView(R.id.vp_guiderx)
    ViewPager vpGuiderx;
    @BindView(R.id.ll_guiderx_point)
    PercentLinearLayout llGuiderxPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_n);
        ButterKnife.bind(this);
        vpGuiderx.setAdapter(new GuideAdapter(this, vpGuiderx, llGuiderxPoint, null));
    }
}
