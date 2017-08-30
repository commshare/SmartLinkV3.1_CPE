package com.alcatel.wifilink.ui.wizard.allsetup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.ui.activity.BaseActivity;
import com.alcatel.wifilink.ui.activity.MainActivity;
import com.alcatel.wifilink.ui.activity.SettingWifiActivity;
import com.alcatel.wifilink.ui.wizard.helper.FraHelper;
import com.alcatel.wifilink.ui.wizard.helper.FragmentEnum;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@Deprecated
public class SetupWizardActivity extends BaseActivity {

    public RelativeLayout mainHeaderTopbanner;// 顶部标题容器
    @BindView(R.id.main_header_back_iv)
    public ImageView mIv_back;// 回退
    @BindView(R.id.tv_title_title)
    public TextView mTv_title;// 标题
    @BindView(R.id.main_header_right_text)
    public TextView mTv_skip;// 跳过


    @BindView(R.id.mRl_setupWizard_waitting)
    public RelativeLayout mRl_waitting;// 等待

    @BindView(R.id.mFl_Setupwizard_container)
    public FrameLayout mFlSetupwizard;/* 切换容器 */

    @BindView(R.id.rl_success)
    public RelativeLayout mRl_Success;// 成功UI

    @BindView(R.id.mRl_setupWizard_failed)
    public RelativeLayout mRl_Failed;// 失败UI
    @BindView(R.id.bt_tryagain)
    public Button mRp_Tryagain;// try again button
    @BindView(R.id.tv_toHome)
    public TextView mTv_Home;// home button


    public String TAG = "SetupWizardActivity";
    public int flid_setupWizard;// 切换容器ID
    public FragmentManager fm;
    private long mKeyTime;// 双击间隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup_wizard);
        ButterKnife.bind(this);
        mainHeaderTopbanner = (RelativeLayout) findViewById(R.id.mHead_setupWizard);
        fm = getSupportFragmentManager();
        flid_setupWizard = R.id.mFl_Setupwizard_container;
        initView();
        initData();
    }

    private void initView() {
        transferHead(HeadEnum.GONE, getString(R.string.main_header_linkhub));
    }

    private void initData() {
        FraHelper.commit(this, fm, flid_setupWizard, FragmentEnum.CONNECTTYPE_FRA);
    }

    /**
     * 切换顶部栏状态
     *
     * @param headEnum
     */
    public void transferHead(HeadEnum headEnum, String title) {
        if (headEnum.equals(HeadEnum.VISIBLE)) {
            mIv_back.setVisibility(View.VISIBLE);// back button gone
            mTv_skip.setVisibility(View.VISIBLE);// skip gone
        } else {
            mIv_back.setVisibility(View.GONE);// back button gone
            mTv_skip.setVisibility(View.GONE);// skip gone
        }

        if (!TextUtils.isEmpty(title)) {
            mTv_title.setText(title);
        }
    }

    @OnClick({R.id.main_header_back_iv, R.id.main_header_right_text, R.id.bt_tryagain, R.id.tv_toHome})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_header_back_iv:/* back button*/
                // 1.to connecttype fragment + refresh topbanner
                refreshUi();
                break;
            case R.id.main_header_right_text:/* skip button*/
                // 1.to connecttype fragment + refresh topbanner
                refreshUi();
                // 2.to wifi setting activity
                ChangeActivity.toActivity(this, SettingWifiActivity.class, false, true, false, 0);
                break;
            case R.id.bt_tryagain:/* try again button */
                // 1.to connecttype fragment + refresh topbanner
                refreshUi();
                break;
            case R.id.tv_toHome:/* home button */
                // to Main Activity
                CPEConfig.getInstance().setQuickSetupFlag();
                ChangeActivity.toActivity(this, MainActivity.class, false, true, false, 0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mKeyTime) > 2000) {
            mKeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    public enum HeadEnum {
        VISIBLE, GONE;
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * H1.更新视图
     * 1. 切换到 connecttype fragment
     * 2. 切换顶部栏
     */
    private void refreshUi() {
        // 0. change topbanner
        transferHead(HeadEnum.GONE, getString(R.string.main_header_linkhub));
        // 1. change to the connecttype activity
        FraHelper.commit(this, fm, flid_setupWizard, FragmentEnum.CONNECTTYPE_FRA);
        // 2. sendAgainSuccess | failed | waitting dismiss
        mRl_Success.setVisibility(View.GONE);
        mRl_Failed.setVisibility(View.GONE);
        mRl_waitting.setVisibility(View.GONE);
    }

}
