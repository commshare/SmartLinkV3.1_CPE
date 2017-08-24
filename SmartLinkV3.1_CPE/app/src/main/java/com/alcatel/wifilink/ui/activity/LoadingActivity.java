package com.alcatel.wifilink.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alcatel.wifilink.Constants;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WizardActivity;
import com.alcatel.wifilink.ui.view.CirclePageIndicator;
import com.alcatel.wifilink.utils.OtherUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static com.alcatel.wifilink.R.style.dialog;

public class LoadingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoadingActivity";
    private final int SPLASH_DELAY = 1000;
    private Handler mHandler = new Handler();

    private boolean isFirstRun = true;
    private PagerAdapter mPagerAdapter;
    private List<View> mViews = new ArrayList<>();
    ;
    private String firstRun = Constants.KEY_FIRST_RUN;

    @BindView(R.id.iv_logo)
    ImageView mLogoImg;// 启屏图片
    @BindView(R.id.vp_guide)
    ViewPager mViewPager;// 切换器
    @BindView(R.id.page_indicator)
    CirclePageIndicator mPageIndicator;// 指示点
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtherUtils.stopAutoTimer();
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        initViews();
        mHandler.postDelayed(this::startApp, SPLASH_DELAY);// 延迟启动
    }

    private void initViews() {
        // get page view
        mViews.addAll(OtherUtils.getGuidePages(this));
        View pageLast = mViews.get(mViews.size() - 1);// 获取最后一页
        Button bt_start = (Button) pageLast.findViewById(R.id.btn_start);
        bt_start.setOnClickListener(this);
        // set adapter
        mPagerAdapter = new GuidePagerAdapter(mViews);
        mViewPager.setAdapter(mPagerAdapter);
        mPageIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {// 点击start button后
        pd = OtherUtils.showProgressPop(this);
        SharedPrefsUtil.getInstance(this).putBoolean(firstRun, false);
        toNextOperation();
    }

    /* 启动APP */
    private void startApp() {
        //  判断是否为第一次使用
        isFirstRun = SharedPrefsUtil.getInstance(this).getBoolean(firstRun, true);
        if (isFirstRun) {
            showGuidePager();// 显示引导页
        } else {
            toNextOperation();// 不显示引导页
        }
    }

    /* 显示向导视图 */
    private void showGuidePager() {
        mLogoImg.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mPageIndicator.setVisibility(View.VISIBLE);
    }

    /* 点击start之后的跳转状态 */
    private void toNextOperation() {

        // 1.if no wifi or wifi ssid is not correct
        if (!OtherUtils.isWiFiActive(this)) {
            // to RefreshWifiActivity
            ChangeActivity.toActivity(this, RefreshWifiActivity.class, false, true, false, 0);
            return;
        }

        // 2.login
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                OtherUtils.hideProgressPop(pd);
                if (result.getState() == Cons.LOGIN) {
                    // to HomeActivity
                    ChangeActivity.toActivity(LoadingActivity.this, WizardActivity.class, false, true, false, 0);
                } else {
                    // to LoginActivity
                    ChangeActivity.toActivity(LoadingActivity.this, LoginActivity.class, false, true, false, 0);
                }
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pd);
                Log.d("ma_load", "load_err: " + e.getMessage().toString());
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {/* 连接超时 */
                    // to RefreshWifiActivity
                    ChangeActivity.toActivity(LoadingActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                } else {
                    ChangeActivity.toActivity(LoadingActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                }
            }

            @Override
            protected void onFailure() {
                OtherUtils.hideProgressPop(pd);
                ChangeActivity.toActivity(LoadingActivity.this, RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                if (Cons.GET_LOGIN_STATE_FAILED.equalsIgnoreCase(error.getCode())) {/* 错误的的登陆码 */
                    ToastUtil_m.show(LoadingActivity.this, getString(R.string.login_failed));
                } else {
                    /* 未知的错误--> 手机没有连接上对应的路由器 */
                    ChangeActivity.toActivity(LoadingActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                }
            }
        });
    }

    /* -------------------------------------------- Adapter -------------------------------------------- */
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
