package com.alcatel.wifilink.rx.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.WpsHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2017/11/16 0016.
 */

public class PinPukPinFragment extends Fragment {

    @BindView(R.id.et_pin_rx)
    EditText etPinRx;
    @BindView(R.id.iv_pin_rx_deleted)
    ImageView ivPinRxDeleted;
    @BindView(R.id.tv_pin_rx_tip_num)
    TextView tvPinRxTipNum;
    @BindView(R.id.tv_pin_rx_tip_des)
    TextView tvPinRxTipDes;
    @BindView(R.id.iv_pin_rx_checkbox)
    ImageView ivPinRxCheckbox;
    @BindView(R.id.tv_pin_rx_checkbox)
    TextView tvPinRxCheckbox;
    @BindView(R.id.bt_pin_rx_unlock)
    Button btPinRxUnlock;
    Unbinder unbinder;

    private View inflate;
    private int red_color;
    private int gray_color;
    private Drawable check_pic;
    private Drawable uncheck_pic;
    private BoardSimHelper boardSimHelper;
    private PinPukIndexRxActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (PinPukIndexRxActivity) getActivity();
        inflate = View.inflate(getActivity(), R.layout.fragment_pinpukpin, null);
        unbinder = ButterKnife.bind(this, inflate);
        initRes();
        initUi();
        return inflate;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initUi();
        }
    }

    /**
     * 初始化资源
     */
    private void initRes() {
        red_color = getResources().getColor(R.color.red);
        gray_color = getResources().getColor(R.color.gray);
        check_pic = getResources().getDrawable(R.drawable.general_btn_remember_pre);
        uncheck_pic = getResources().getDrawable(R.drawable.general_btn_remember_nor);
    }

    /**
     * 初始化UI
     */
    private void initUi() {
        boolean isRememPin = SP.getInstance(getActivity()).getBoolean(Cons.PIN_REMEM_FLAG_RX, false);
        String pinCache = SP.getInstance(getActivity()).getString(Cons.PIN_REMEM_STR_RX, "");
        ivPinRxCheckbox.setImageDrawable(isRememPin ? check_pic : uncheck_pic);
        etPinRx.setText(isRememPin ? pinCache : "");
        etPinRx.setSelection(OtherUtils.getEdContent(etPinRx).length());
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取剩余次数
        getRemainTime();
    }

    /**
     * 获取PIN码剩余次数
     */
    private void getRemainTime() {
        boardSimHelper = new BoardSimHelper(getActivity());
        boardSimHelper.setOnNormalSimstatusListener(this::toRemain);
        boardSimHelper.boardNormal();
    }

    /**
     * 处理剩余次数
     *
     * @param simStatus
     */
    private void toRemain(SimStatus simStatus) {
        int pinTime = simStatus.getPinRemainingTimes();
        // pinTime = 0;//  测试Puk界面跳转,请将该代码注释
        tvPinRxTipNum.setText(String.valueOf(pinTime));
        if (pinTime < 3) {
            tvPinRxTipNum.setTextColor(red_color);
            tvPinRxTipDes.setTextColor(red_color);
            if (pinTime <= 0) {
                toPukfragment(simStatus);
            }
            return;
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_pin_rx_deleted, R.id.bt_pin_rx_unlock, R.id.iv_pin_rx_checkbox, R.id.tv_pin_rx_checkbox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pin_rx_deleted:
                etPinRx.setText("");
                break;
            case R.id.bt_pin_rx_unlock:
                unlockPin();
                break;
            case R.id.iv_pin_rx_checkbox:
            case R.id.tv_pin_rx_checkbox:
                boolean isCheck = ivPinRxCheckbox.getDrawable() == check_pic;
                ivPinRxCheckbox.setImageDrawable(isCheck ? uncheck_pic : check_pic);
                String pin = OtherUtils.getEdContent(etPinRx);
                SP.getInstance(getActivity()).putString(Cons.PIN_REMEM_STR_RX, ivPinRxCheckbox.getDrawable() == check_pic ? pin : "");
                SP.getInstance(getActivity()).putBoolean(Cons.PIN_REMEM_FLAG_RX, ivPinRxCheckbox.getDrawable() == check_pic);
                break;
        }
    }

    /**
     * 解PIN
     */
    private void unlockPin() {
        // 空值判断
        String pin = OtherUtils.getEdContent(etPinRx);
        if (TextUtils.isEmpty(pin)) {
            toast(R.string.pin_empty);
            return;
        }
        // 位数判断
        if (pin.length() < 4 | pin.length() > 8) {
            toast(R.string.the_pin_code_should_be_4_8_characters);
            return;
        }
        // 请求
        getBoardAndSimStauts();
    }

    private void getBoardAndSimStauts() {
        boardSimHelper = new BoardSimHelper(getActivity());
        boardSimHelper.setOnPinRequireListener(this::unlockPinRequest);
        boardSimHelper.setOnpukRequireListener(this::toPukfragment);
        boardSimHelper.setOnpukTimeoutListener(this::toPukfragment);
        boardSimHelper.setOnSimReadyListener(result -> toAc());
        boardSimHelper.boardNormal();
    }

    /**
     * 跳转
     */
    private void toAc() {
        // 是否进入过流量设置页
        if (SP.getInstance(getActivity()).getBoolean(Cons.DATAPLAN_RX, false)) {
            // 是否进入过wifi初始设置页
            if (SP.getInstance(getActivity()).getBoolean(Cons.WIFIINIT_RX, false)) {
                to(HomeRxActivity.class);
            } else {
                checkWps();// 检测是否开启了WPS模式
            }
        } else {
            to(DataPlanRxActivity.class);
        }
    }

    /**
     * 检测是否开启了WPS模式
     */
    private void checkWps() {
        WpsHelper wpsHelper = new WpsHelper();
        wpsHelper.setOnWpsListener(attr -> to(attr ? HomeRxActivity.class : WifiInitRxActivity.class));
        wpsHelper.setOnErrorListener(attr -> to(HomeRxActivity.class));
        wpsHelper.setOnResultErrorListener(attr -> to(HomeRxActivity.class));
        wpsHelper.getWpsStatus();
    }

    /**
     * 前往puk fragment
     *
     * @param result
     */
    private void toPukfragment(SimStatus result) {
        activity.fraHelper.transfer(activity.allClass[1]);
    }

    /**
     * 发送解PIN请求
     *
     * @param result
     */
    private void unlockPinRequest(SimStatus result) {
        String pin = OtherUtils.getEdContent(etPinRx);
        RX.getInstant().unlockPin(pin, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                // 是否勾选了记住PIN
                boolean isRememPin = ivPinRxCheckbox.getDrawable() == check_pic ? true : false;
                if (isRememPin) {
                    String pin = OtherUtils.getEdContent(etPinRx);
                    SP.getInstance(getActivity()).putString(Cons.PIN_REMEM_STR_RX, pin);
                    SP.getInstance(getActivity()).putBoolean(Cons.PIN_REMEM_FLAG_RX, isRememPin);
                }
                // 进入其他界面
                toAc();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                etPinRx.setText("");
                toast(R.string.pin_error_waring_title);
                getRemainTime();
            }

            @Override
            public void onError(Throwable e) {
                etPinRx.setText("");
                toast(R.string.sim_unlocked_failed);
                getRemainTime();
            }
        });
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */
    public void toast(int resId) {
        ToastUtil_m.show(getActivity(), resId);
    }

    public void to(Class ac) {
        CA.toActivity(getActivity(), ac, false, true, false, 0);
    }
}
