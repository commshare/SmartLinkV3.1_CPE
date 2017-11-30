package com.alcatel.wifilink.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.sim.helper.SimPukEmptyHelper;
import com.alcatel.wifilink.business.sim.helper.SimPukHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.SP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Deprecated
public class SettingPukActivity extends BaseActivity {

    // 头部
    @BindView(R.id.mTv_puk_title)
    TextView mTvPukTitle;
    @BindView(R.id.mTv_pub_skip)
    TextView mTvPubSkip;
    @BindView(R.id.mRl_puk_head)
    RelativeLayout mRlPukHead;
    // puk code
    @BindView(R.id.mEt_puk_pukCode)
    EditText mEtPukPukCode;
    @BindView(R.id.mIv_puk_del)
    ImageView mIvPukDel;
    @BindView(R.id.mRl_puk_pukCode)
    RelativeLayout mRlPukPukCode;
    // 剩余次数
    @BindView(R.id.mTv_puk_remain)
    TextView mTvPukRemain;
    // new pin
    @BindView(R.id.mEt_puk_newPin)
    EditText mEtPukNewPin;
    @BindView(R.id.mRl_puk_newPin)
    RelativeLayout mRlPukNewPin;
    // confirm pin
    @BindView(R.id.mEt_puk_confirmPin)
    EditText mEtPukConfirmPin;
    @BindView(R.id.mRl_puk_confirmPin)
    RelativeLayout mRlPukConfirmPin;
    // not match    
    @BindView(R.id.mTv_puk_alarm)
    TextView mTvPukAlarm;
    // 记录
    @BindView(R.id.mIv_puk_check)
    ImageView mIvPukCheck;
    @BindView(R.id.mTv_puk_check)
    TextView mTvPukCheck;
    @BindView(R.id.mRl_puk_check)
    RelativeLayout mRlPukCheck;
    // connect button
    @BindView(R.id.mRp_puk_connect)
    RippleView mRpPukConnect;

    // waitting ui
    @BindView(R.id.mRl_puk_waitting)
    RelativeLayout mRlPukWaitting;

    private boolean isCheck;
    private final String TAG = "SettingPukActivity";
    private static final String PIN_PASSWORD = "pinPassword";
    private BusinessManager mBusinessMana;
    private int pukRemainTimes;
    private String DES = " attempts remainning";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting_puk);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    private void initData() {
        // 获取PUK剩余次数
        mBusinessMana = BusinessManager.getInstance();
        // TOAT: 测试阶段把该句注释
        setRemainUi(getPukRemainTimes());
    }


    private void initEvent() {
        setCheckable(false);// 1.设置选项默认为未选中
        setClickable(mRpPukConnect, false, R.drawable.puk_normal);// 2.设置connect初始化不可点击
        // 3.根据ED的empty激活connect button
        new SimPukEmptyHelper(this, mEtPukPukCode, mEtPukNewPin, mEtPukConfirmPin) {
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

    @OnClick({R.id.mIv_puk_del, R.id.mRp_puk_connect, R.id.mTv_pub_skip, R.id.mIv_puk_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mTv_pub_skip:// skip
                CA.toActivity(this, SettingWifiActivity.class, false, true, false, 0);
                break;
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
                    new SimPukHelper(this) {// commit to remote
                        @Override
                        public void isSuccesss(boolean isSuccess) {
                            mRlPukWaitting.setVisibility(View.GONE);
                            if (isSuccess) {/* 成功后--> 保存PIN到文件--> 跳转到状态页 */
                                SP.getInstance(SettingPukActivity.this).putString(PIN_PASSWORD, getEtContent(mEtPukConfirmPin));
                                CA.toActivity(SettingPukActivity.this, NetModeConnectStatusActivity.class, false, true,
                                        false, 0);
                            } else {/* PUK码错误--> 获取剩余次数 */
                                runOnUiThread(() -> {
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
        SP.getInstance(SettingPukActivity.this).putString(PIN_PASSWORD, "");
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
