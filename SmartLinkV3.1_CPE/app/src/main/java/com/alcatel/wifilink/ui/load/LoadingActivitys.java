package com.alcatel.wifilink.ui.load;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.LoginActivity;
import com.alcatel.wifilink.ui.activity.RefreshWifiActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.view.CirclePageIndicator;
import com.alcatel.wifilink.ui.wizard.allsetup.WizardActivity;
import com.alcatel.wifilink.utils.OtherUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingActivitys extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.page_indicator)
    CirclePageIndicator pageIndicator;

    List<View> pagers;// pager集合
    View lastPager;// 最后一页
    Button bt_start;// start按钮
    boolean firstTag;// 是否为第一次启动
    GuidePagerAdapter adapter;// 滑动页适配器
    ProgressDialog pd;// 进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading_activitys);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        pagers = new ArrayList<>();
        pagers.addAll(OtherUtils.getGuidePages(this));// 获取所有的pagers
        lastPager = pagers.get(pagers.size() - 1);// 获取最后一页
        bt_start = (Button) lastPager.findViewById(R.id.btn_start);// start按钮
        bt_start.setOnClickListener(this);// 设置点击事件
        adapter = new GuidePagerAdapter(pagers);// 创建适配器
        vpGuide.setAdapter(adapter);// 设置适配器
        pageIndicator.setViewPager(vpGuide);// 关联轮播点
        ivLogo.postDelayed(this::toNext, 1000);// 延迟1秒--> 切屏
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                pd = OtherUtils.showProgressPop(this);
                SP.getInstance(this).putBoolean(Cons.FIRST_RUN, false);
                toNext();
                break;
        }
    }

    /**
     * 切换页面
     */
    private void toNext() {
        Log.d("ma_load", "toNext");
        // 1.是否之前连接过向导页
        firstTag = SP.getInstance(this).getBoolean(Cons.FIRST_RUN, true);
        if (firstTag) {
            showGuidePager();
        } else {
            obtainLoginState();
        }
    }

    /**
     * 获取登陆状态
     */
    private void obtainLoginState() {
        RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                OtherUtils.hideProgressPop(pd);
                int state = result.getState();
                if (state == Cons.LOGIN) {
                    CA.toActivity(LoadingActivitys.this, WizardActivity.class, false, true, false, 0);
                } else {
                    CA.toActivity(LoadingActivitys.this, LoginActivity.class, false, true, false, 0);
                }
            }

            @Override
            protected void onFailure() {
                OtherUtils.hideProgressPop(pd);
                CA.toActivity(LoadingActivitys.this, RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pd);
                if (e instanceof SocketTimeoutException) {/* 连接超时 */
                    // to RefreshWifiActivity
                } else if (e instanceof ConnectException) {
                    // to RefreshWifiActivity
                } else {
                }
                CA.toActivity(LoadingActivitys.this, RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pd);
                if (Cons.GET_LOGIN_STATE_FAILED.equalsIgnoreCase(error.getCode())) {/* 错误的的登陆码 */
                    ToastUtil_m.show(LoadingActivitys.this, getString(R.string.login_failed));
                } else {
                    /* 未知的错误--> 手机没有连接上对应的路由器 */
                    CA.toActivity(LoadingActivitys.this, RefreshWifiActivity.class, false, true, false, 0);
                }
            }
        });
    }

    /**
     * 显示向导视图
     */
    private void showGuidePager() {
        ivLogo.setVisibility(View.GONE);
        vpGuide.setVisibility(View.VISIBLE);
        pageIndicator.setVisibility(View.VISIBLE);
    }

}
