package com.alcatel.wifilink.ui.wizard.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.sim.helper.SimPukEmptyHelper;
import com.alcatel.wifilink.business.sim.helper.SimPukHelper;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.ui.activity.NetModeConnectStatusActivity;
import com.alcatel.wifilink.ui.wizard.allsetup.SetupWizardActivity;
@SuppressLint("ValidFragment")
public class PukCodeFragment extends Fragment implements View.OnClickListener {


    private final SetupWizardActivity activity;

    // puk code
    EditText mEtPukPukCode;
    ImageView mIvPukDel;
    RelativeLayout mRlPukPukCode;
    // 剩余次数
    TextView mTvPukRemain;
    // new pin
    EditText mEtPukNewPin;
    RelativeLayout mRlPukNewPin;
    // confirm pin
    EditText mEtPukConfirmPin;
    RelativeLayout mRlPukConfirmPin;
    // not match    
    TextView mTvPukAlarm;
    // 记录
    ImageView mIvPukCheck;
    TextView mTvPukCheck;
    RelativeLayout mRlPukCheck;
    // connect button
    RippleView mRpPukConnect;

    // puk waitting ui
    RelativeLayout mRlPukWaitting;

    private View inflate;
    private BusinessManager mBusinessMana;
    private boolean isCheck = true;
    private static final String PIN_PASSWORD = "pinPassword";
    private String DES = " attempts remainning";

    public PukCodeFragment(Activity activity) {
        this.activity = ((SetupWizardActivity) activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.mIv_back.setVisibility(View.GONE);
        activity.mTv_title.setText(getString(R.string.main_header_mobile_broadband));
        inflate = View.inflate(getActivity(), R.layout.fragment_setupwizard_pukcode, null);
        initView();
        initData();
        initEvent();
        return inflate;
    }

    private void initView() {
        mEtPukPukCode = (EditText) inflate.findViewById(R.id.mEt_puk_pukCode);
        mIvPukDel = (ImageView) inflate.findViewById(R.id.mIv_puk_del);
        mRlPukPukCode = (RelativeLayout) inflate.findViewById(R.id.mRl_puk_pukCode);
        mTvPukRemain = (TextView) inflate.findViewById(R.id.mTv_puk_remain);
        mEtPukNewPin = (EditText) inflate.findViewById(R.id.mEt_puk_newPin);
        mRlPukNewPin = (RelativeLayout) inflate.findViewById(R.id.mRl_puk_newPin);

        mEtPukConfirmPin = (EditText) inflate.findViewById(R.id.mEt_puk_confirmPin);
        mRlPukConfirmPin = (RelativeLayout) inflate.findViewById(R.id.mRl_puk_confirmPin);
        mTvPukAlarm = (TextView) inflate.findViewById(R.id.mTv_puk_alarm);

        mIvPukCheck = (ImageView) inflate.findViewById(R.id.mIv_puk_check);
        mTvPukCheck = (TextView) inflate.findViewById(R.id.mTv_puk_check);
        mRlPukCheck = (RelativeLayout) inflate.findViewById(R.id.mRl_puk_check);

        mRpPukConnect = (RippleView) inflate.findViewById(R.id.mRp_puk_connect);
        mRlPukWaitting = (RelativeLayout) inflate.findViewById(R.id.mRl_puk_waitting);


    }

    private void initData() {
        // 获取PUK剩余次数
        mBusinessMana = BusinessManager.getInstance();
        // TOAT: 测试阶段把该句注释
        setRemainUi(getPukRemainTimes());
    }


    private void initEvent() {
        mIvPukDel.setOnClickListener(this);
        mIvPukCheck.setOnClickListener(this);
        mRpPukConnect.setOnClickListener(this);
        
        setCheckable(false);// 1.设置选项默认为未选中
        setClickable(mRpPukConnect, false, R.drawable.puk_normal);// 2.设置connect初始化不可点击
        // 3.根据ED的empty激活connect button
        new SimPukEmptyHelper(getActivity(), mEtPukPukCode, mEtPukNewPin, mEtPukConfirmPin) {
            @Override
            public void getEmpty(boolean isEmpty) {
                if (isEmpty) {
                    setClickable(mRpPukConnect, false, R.drawable.puk_normal);
                } else {
                    setClickable(mRpPukConnect, true, R.drawable.puk_pressed);
                }

            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mIv_puk_del:// clear puk code edittext
                mEtPukPukCode.setText("");
                break;
            case R.id.mIv_puk_check:// change remember sim pin check 
                isCheck = !isCheck;
                setCheckable(isCheck);
                break;
            case R.id.mRp_puk_connect:/* connect button */
                // 1.检验PIN是否一致--> 根据情况显示警告UI
                boolean samePin = isSamePin(mEtPukNewPin, mEtPukConfirmPin);
                // 1.1.警告文本是否显示
                mTvPukAlarm.setVisibility(samePin ? View.GONE : View.VISIBLE);
                // 2.提交
                if (samePin) {
                    mRlPukWaitting.setVisibility(View.VISIBLE);// waitting ui
                    new SimPukHelper(getActivity()) {// commit to remote
                        @Override
                        public void isSuccesss(boolean isSuccess) {
                            mRlPukWaitting.setVisibility(View.GONE);
                            if (isSuccess) {/* 成功后--> 保存PIN到文件--> 跳转到状态页 */
                                SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, getEtContent(mEtPukConfirmPin));
                                ChangeActivity.toActivity(getActivity(), NetModeConnectStatusActivity.class, false, true, false, 0);
                            } else {/* PUK码错误--> 获取剩余次数 */
                                getActivity().runOnUiThread(() -> {
                                    // 不同次数的UI显示
                                    setRemainUi(getPukRemainTimes());
                                    // setRemainUi(1);
                                });
                            }
                        }
                    }.commit(mEtPukNewPin, mEtPukConfirmPin);
                }

                break;
        }
    }

    /* ------------------------------------------------- helper -------------------------------------------------*/

    /**
     * H1.设置控件点击状态 + 背景颜色
     *
     * @param view
     * @param clickable
     */
    private void setClickable(View view, boolean clickable, int resId) {
        view.setClickable(clickable);
        view.setBackgroundResource(resId);
    }

    /**
     * H2.修改选中与未选中状态
     *
     * @param isCheck
     */
    private void setCheckable(boolean isCheck) {
        mIvPukCheck.setImageResource(isCheck ? R.drawable.general_btn_remember_pre : R.drawable.general_btn_remember_nor);
    }

    /**
     * H3.PIN输入框内容是否一致
     *
     * @param ed1
     * @param ed2
     * @return
     */
    private boolean isSamePin(EditText ed1, EditText ed2) {
        String c1 = ed1.getText().toString().trim().replace(" ", "");
        String c2 = ed2.getText().toString().trim().replace(" ", "");
        return c1.equals(c2);
    }

    /**
     * H4.获取编辑框内容
     *
     * @param ed
     * @return
     */
    private String getEtContent(EditText ed) {
        return ed.getText().toString().trim().replace(" ", "");
    }

    /**
     * H5.获取PUK剩余输入次数
     *
     * @return
     */
    private int getPukRemainTimes() {
        return mBusinessMana.getSimStatus().m_nPukRemainingTimes;
    }

    /**
     * H6.设置编辑域不可编辑
     *
     * @param eds
     */
    private void setEtUnable(EditText... eds) {
        SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, "");
        for (EditText ed : eds) {
            ed.setText("");
            ed.setBackgroundColor(Color.parseColor("#dddddd"));
            ed.setClickable(false);
            ed.setFocusable(false);
        }
    }

    /**
     * H7.根据剩余次数显示不同的UI
     */
    public void setRemainUi(int pukRemainTimes) {
        if (pukRemainTimes == 1) {// 1.剩余 1 次时提示用户
            mTvPukRemain.setTextColor(Color.RED);
            mTvPukRemain.setText(pukRemainTimes + DES);
        }

        if (pukRemainTimes == 0) {// 2.剩余 0 次时提示用户
            mTvPukRemain.setTextColor(Color.RED);
            mTvPukRemain.setTextSize(Dimension.SP, 12);
            mTvPukRemain.setText(getResources().getString(R.string.puk_alarm_des1));
            setEtUnable(mEtPukPukCode, mEtPukNewPin, mEtPukConfirmPin);
        }
    }


}
