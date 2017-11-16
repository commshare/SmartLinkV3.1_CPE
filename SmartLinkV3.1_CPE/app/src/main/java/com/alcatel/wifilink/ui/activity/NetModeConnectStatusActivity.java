package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.business.wanguide.StatusBean;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Deprecated
public class NetModeConnectStatusActivity extends BaseActivity {

    private final String TAG = "NetModeConnectStatus";
    private final int SUCCESS = 1;
    private final int FAILED = 0;

    @BindView(R.id.mIv_connectStatus_logo_success)
    ImageView mIvConnectStatusLogoSuccess;
    @BindView(R.id.mTv_connectStatus_des1_success)
    TextView mTvConnectStatusDes1Success;
    @BindView(R.id.mTv_connectStatus_des2_success)
    TextView mTvConnectStatusDes2Success;
    @BindView(R.id.mRl_connectStatus_success)
    RelativeLayout mRlConnectStatusSuccess;

    @BindView(R.id.mIv_connectStatus_logo_failed)
    ImageView mIvConnectStatusLogoFailed;
    @BindView(R.id.mTv_connectStatus_des1_failed)
    TextView mTvConnectStatusDes1Failed;
    @BindView(R.id.tv_disconnect_des2)
    TextView mTvConnectStatusDes2Failed;
    @BindView(R.id.mRl_connectStatus_failed)
    RelativeLayout mRlConnectStatusFailed;

    @BindView(R.id.mRp_connectStatus_tryagain)
    RippleView mRpConnectStatusTryagain;
    @BindView(R.id.mTv_connectStatus_home)
    TextView mTvConnectStatusHome;


    private StatusBean statusBean;// 状态对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_netmode_status);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        int status = statusBean.getStatus();
        //
        //status = 0;
        // 根据连接状态切换界面
        if (status == SUCCESS) {
            // 1.显示成功页
            switchStatus(View.VISIBLE, View.GONE);
            // 1.1.设置成功略过选择页--> 进入快速启动
            CPEConfig.getInstance().setQuickSetupFlag();
            // 2.延迟2秒跳转到setting页
            CA.toActivity(this, SettingWifiActivity.class, false, true, false, 2000);
        } else if (status == FAILED) {
            // 显示失败页
            switchStatus(View.GONE, View.VISIBLE);
        }
    }
    
    /* EVENTBUS观察者--> StatusBean */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getConnectStatus(StatusBean statusBean) {
        this.statusBean = statusBean;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.mRp_connectStatus_tryagain, R.id.mTv_connectStatus_home})
    public void onViewClicked(View view) {

        // 根据当前连接的类型进行重试
        switch (view.getId()) {
            /*Try again*/
            case R.id.mRp_connectStatus_tryagain:
                // 方案一:直接跳转会类型选择界面(当前)
                CA.toActivity(this, ConnectTypeSelectActivity.class, false, true, false, 0);

                // 方案二:当前界面直接进行连接
                /*// 显示正在连接中...
                choiceUi(R.string.NETMODE_FAILED_CONNECTING, R.string.NETMODE_FAILED_CONNECTING_DES);
                // 以当前的模式再次进行连接
                new NetModeCommitHelper(this) {
                    @Override
                    public void getStatusBean(StatusBean statusBean) {
                        if (statusBean.getStatus() == 1) {
                            switchStatus(View.VISIBLE, View.GONE);// 显示成功
                            CA.toActivity(NetModeConnectStatusActivity.this, SettingWifiActivity.class, true, true, false, 2000);// 跳转
                        } else {
                            choiceUi(R.string.NETMODE_STATUS_FAILED_DES1, R.string.NETMODE_STATUS_FAILED_DES2);// 显示失败
                        }
                    }
                }.commit(statusBean);*/
                break;

            /*home--> 跳转至主页*/
            case R.id.mTv_connectStatus_home:
                CPEConfig.getInstance().setQuickSetupFlag();/* 跳转到主页先初始化跳转标记, 否则--> loadingActivity */
                CA.toActivity(this, HomeActivity.class, false, true, false, 0);
                break;
        }
    }



    /* ************************************************ HELPER *********************************************** */

    /**
     * H1:切换成功 | 失败的状态
     *
     * @param rl_success 成功页
     * @param rl_failed  失败页
     */
    private void switchStatus(int rl_success, int rl_failed) {
        mRlConnectStatusSuccess.setVisibility(rl_success);
        mRlConnectStatusFailed.setVisibility(rl_failed);
    }

    /**
     * H2:切换UI
     *
     * @param title
     * @param des
     */
    private void choiceUi(int title, int des) {
        mTvConnectStatusDes1Failed.setText(getResources().getString(title));
        mTvConnectStatusDes2Failed.setText(getResources().getString(des));
    }
}
