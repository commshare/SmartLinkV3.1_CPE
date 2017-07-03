package com.alcatel.smartlinkv3.ui.setupwizard.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;
import com.alcatel.smartlinkv3.ui.setupwizard.allsetup.SetupWizardActivity;
import com.alcatel.smartlinkv3.ui.setupwizard.helper.EdittextWatcher;
import com.alcatel.smartlinkv3.ui.setupwizard.helper.FraHelper;
import com.alcatel.smartlinkv3.ui.setupwizard.helper.FragmentEnum;

@SuppressLint("ValidFragment")
public class PinCodeFragment extends Fragment {

    private SetupWizardActivity activity;
    private View inflate;
    private int pinRemainingTimes;// 剩余次数
    private BusinessManager mBusinessMgr;// business manager
    private boolean isRememberPassword = true;
    private boolean test = true;// 测试开关

    private EditText mEt_Pin_Password;// 密码框
    private RelativeLayout mRl_pin_del;// 删除按钮
    private TextView mTv_pin_times;// 次数
    private TextView mTv_pin_des;// attempt remainning
    private ImageView mIv_pin_check;// check
    private TextView mTv_pin_unlocked;// pin_unlocked
    private RelativeLayout mRl_setupWizard_wait;// waitting ui

    //    private QSBroadcastReceiver mReceiver;
    private static final String PIN_PASSWORD = "pinPassword";

    public PinCodeFragment(Activity activity) {
        this.activity = (SetupWizardActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // init topbanner
        activity.transferHead(SetupWizardActivity.HeadEnum.VISIBLE, getString(R.string.main_header_mobile_broadband));
        inflate = View.inflate(activity, R.layout.fragment_setupwizard_pincode, null);
        initView();
        initData();
        initEvent();
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));
        mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (mReceiver != null) {
//            getActivity().unregisterReceiver(mReceiver);
//        }
    }

    private void initView() {
        mRl_setupWizard_wait = activity.mRl_waitting;

        mEt_Pin_Password = (EditText) inflate.findViewById(R.id.handle_pin_password);
        mRl_pin_del = (RelativeLayout) inflate.findViewById(R.id.handle_pin_password_delete);

        mTv_pin_times = (TextView) inflate.findViewById(R.id.handle_pin_password_times);
        mTv_pin_des = (TextView) inflate.findViewById(R.id.handle_pin_password_times_des);

        mIv_pin_check = (ImageView) inflate.findViewById(R.id.handle_pin_remember_pin_select);
        mTv_pin_unlocked = (TextView) inflate.findViewById(R.id.handle_pin_connect_btn);
    }

    private void initData() {
        mBusinessMgr = BusinessManager.getInstance();
//        mReceiver = new QSBroadcastReceiver();
        remaingTimes();// 显示剩余次数
        rememberCheck();// 显示check选中状态
    }

    private void initEvent() {
        // Check click listener
        mIv_pin_check.setOnClickListener(v -> {
            changeCheck();
        });

        // edittext input watcher
        mEt_Pin_Password.addTextChangedListener(new EdittextWatcher() {
            @Override
            public void textChange() {
                changeDelIv();
            }
        });

        // pin del click
        mRl_pin_del.setOnClickListener(v -> {
            mEt_Pin_Password.setText("");
        });

        // pin unlocked
        mTv_pin_unlocked.setOnClickListener(v -> {
            pinUnlocked();
        });

//        // PIN解锁成功(broadcast形式):预留
//        mReceiver.setOnPinSuccessListener(() -> {
//            // 1.show success ui
//            activity.mRl_Success.setVisibility(View.VISIBLE);
//            // 2. save the pin password to sp
//            SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, isRememberPassword ? mEt_Pin_Password.getText().toString() : "");
//            // 3. auto trans to wifi setting ui
//            ToastUtil_m.show(getActivity(), "Switching...");
//            ChangeActivity.toActivity(getActivity(), SettingWifiActivity.class, true, true, false, 0);
//        });
//
//        // PIN解锁失败(broadcast形式):预留
//        mReceiver.setOnPinFailedListener(() -> {
//            activity.mRl_Failed.setVisibility(View.VISIBLE);
//        });

    }

    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * H1.获取并显示剩余次数
     */
    private void remaingTimes() {
        mBusinessMgr = BusinessManager.getInstance();
        pinRemainingTimes = mBusinessMgr.getSimStatus().m_nPinRemainingTimes;
        mTv_pin_times.setText(pinRemainingTimes + "");
        mTv_pin_times.setTextColor(pinRemainingTimes < 3 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.black_text));
        mTv_pin_des.setTextColor(pinRemainingTimes < 3 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.black_text));
    }

    /**
     * H2.显示check选中状态
     */
    private void rememberCheck() {
        if (isRememberPassword) {
            mIv_pin_check.setImageResource(R.drawable.general_btn_remember_pre);
        } else {
            mIv_pin_check.setImageResource(R.drawable.general_btn_remember_nor);
        }
    }

    /**
     * H3.修改选中 | 选中
     */
    private void changeCheck() {
        isRememberPassword = !isRememberPassword;
        if (isRememberPassword) {
            mIv_pin_check.setImageResource(R.drawable.general_btn_remember_pre);
        } else {
            mIv_pin_check.setImageResource(R.drawable.general_btn_remember_nor);
        }
    }

    /**
     * H4.显示 | 隐藏 删除按钮
     */
    private void changeDelIv() {
        mRl_pin_del.setVisibility(!TextUtils.isEmpty(mEt_Pin_Password.getText().toString()) ? View.VISIBLE : View.GONE);
    }


    /**
     * H5.解锁PIN码
     */
    private void pinUnlocked() {
        if (!test) {
            // TOAT: 测试阶段该段代码暂时无效 START
            if (!currentRemain()) {
                ToastUtil_m.show(getActivity(), "Pin had locked");
                return;
            }
            // TOAT: 测试阶段该段代码暂时无效 END
        }

        mRl_setupWizard_wait.setVisibility(View.VISIBLE);
        // 请求前把旧数据清空
        SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, "");
        // 非空判断
        String pinPassword = mEt_Pin_Password.getText().toString().trim();
        if (TextUtils.isEmpty(pinPassword)) {
            ToastUtil_m.show(getActivity(), "Not permit PIN empty");
            mRl_setupWizard_wait.setVisibility(View.GONE);
            return;
        }

        API.get().unlockPin(pinPassword, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, pinPassword);
                activity.mRl_Success.setVisibility(View.VISIBLE);
                // to --> wifi setting
                ChangeActivity.toActivity(getActivity(), SettingWifiActivity.class, true, true, false, 2000);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                if (!currentRemain()) {
                    // 跳转到PUK设置界面k
                    // to puk fragment
                    //ChangeActivity.toActivity(getActivity(), SettingPukActivity.class, true, true, false, 0);
                    FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.PUK_FRA);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                if (!currentRemain()) {
                    // 跳转到PUK设置界面k
                    // to puk fragment
                    //ChangeActivity.toActivity(getActivity(), SettingPukActivity.class, true, true, false, 0);
                    FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.PUK_FRA);
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                mRl_setupWizard_wait.setVisibility(View.GONE);
            }
        });
//        // 请求
//        new SimPinHelper(getActivity()) {
//            @Override
//            public void isPinCorrect(boolean correct) {
//                // wait ui is gone
//                mRl_setupWizard_wait.setVisibility(View.GONE);
//                if (correct) {// 如果PIN码正确, 则保存PIN码到本地
//                    SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, pinPassword);
//                    activity.mRl_Success.setVisibility(View.VISIBLE);
//                    // to --> wifi setting
//                    ChangeActivity.toActivity(getActivity(), SettingWifiActivity.class, true, true, false, 2000);
//                } else {
//                    // 输入错误后--> 并当前剩余次数 < 0
//                    if (!currentRemain()) {
//                        // 跳转到PUK设置界面k
//                        // to puk fragment
//                        //ChangeActivity.toActivity(getActivity(), SettingPukActivity.class, true, true, false, 0);
//                        FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.PUK_FRA);
//                    }
//                }
//
//            }
//        }.connect(mEt_Pin_Password.getText().toString().trim());
    }

    /**
     * H6.根据当前剩余次数进行限定操作
     *
     * @return
     */
    private boolean currentRemain() {
        // 1.获取最近一次剩余次数
        int mCurrentRemainTime = mBusinessMgr.getSimStatus().m_nPinRemainingTimes;
        // 2.设置UI
        mTv_pin_times.setText(mCurrentRemainTime + "");
        mTv_pin_des.setTextColor(Color.RED);
        mTv_pin_des.setText("attempts remaining");
        // 3.剩余次数如果小等于0--> 则屏蔽输入控件
        if (mCurrentRemainTime <= 0) {
            mEt_Pin_Password.setEnabled(false);
            mEt_Pin_Password.setFocusable(false);
            mEt_Pin_Password.setClickable(false);
            mEt_Pin_Password.setTextColor(Color.GRAY);
            return false;
        }
        return true;
    }
}
