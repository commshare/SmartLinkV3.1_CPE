package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.bean.SMSContactSelf;
import com.alcatel.wifilink.rx.ui.HomeRxActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.rcv.SmsRcvAdapter;
import com.alcatel.wifilink.ui.sms.helper.SmsDeleteSessionHelper;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ScreenSize;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class SmsFragments extends Fragment implements FragmentBackHandler {

    @BindView(R.id.rcv_sms)
    RecyclerView rcvSms;
    Unbinder unbinder;
    @BindView(R.id.no_sms)
    TextView noSms;
    @BindView(R.id.tv_sms_batchDeteled)
    TextView tvSmsBatchDeteled;
    @BindView(R.id.ll_sms_batchDeteled)
    LinearLayout llSmsBatchDeteled;
    @BindView(R.id.tv_sms_batchSelectAll)
    TextView tvSmsBatchSelectAll;

    private View inflate;
    private HomeRxActivity activity;
    private TimerHelper timerHelper;
    private SmsRcvAdapter smsRcvAdapter;
    private SMSContactList smsContactList;// 不带有长按标记的联系人集合
    private List<SMSContactSelf> smsContactSelfList = new ArrayList<>();// 带有长按标记的联系人集合
    private List<Long> contactIdList;// 长按模式下存放被点击的短信条目的联系人ID
    private PopupWindows pop_deleted_sms;
    public static boolean isLongClick = false;
    private String select_all;
    private String deselect_all;

    public SmsFragments() {

    }

    public SmsFragments(Activity activity) {
        this.activity = (HomeRxActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_sms_update, null);
        unbinder = ButterKnife.bind(this, inflate);
        initRes();
        isLongClick = false;
        resetUi();
        initView();
        initEvent();
        return inflate;
    }

    private void initRes() {
        select_all = getString(R.string.operation_selectall);
        deselect_all = getString(R.string.operation_cancel_selectall);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    private void startTimer() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getSmsContactList();
            }
        };
        timerHelper.start(5000);
        OtherUtils.timerList.add(timerHelper);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            isLongClick = false;
            resetUi();
            startTimer();
        } else {
            stopTimer();
        }
        // 删除按钮的显示与否
        llSmsBatchDeteled.setVisibility(isLongClick ? View.VISIBLE : View.GONE);

    }

    /**
     * 重置banner & navigation
     */
    private void resetUi() {
        if (activity == null) {
            activity = (HomeRxActivity) getActivity();
        }
        activity.tabFlag = Cons.TAB_SMS;
        activity.llNavigation.setVisibility(View.VISIBLE);
        activity.rlBanner.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        rcvSms.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        smsRcvAdapter = new SmsRcvAdapter(getActivity(), smsContactSelfList);
        rcvSms.setAdapter(smsRcvAdapter);
    }

    /**
     * 初始化适配器事件
     */
    private void initEvent() {
        
        /* 长按短信列表item */
        smsRcvAdapter.setOnRcvLongClickListener(position -> {
            if (!isLongClick) {
                stopTimer();// 停止定时器
                isLongClick = true;// 进入允许删除状态
                tvSmsBatchSelectAll.setText(select_all);// 初始文本为selected all
                ((HomeRxActivity) getActivity()).ivSmsNew.setVisibility(View.GONE);// 短信新建暂时不可用
                smsContactSelfList = OtherUtils.modifySMSContactSelf(smsContactSelfList, true);
                smsRcvAdapter.notifys(smsContactSelfList);
                llSmsBatchDeteled.setVisibility(isLongClick ? View.VISIBLE : View.GONE);
            }
        });
        
        /* 长按模式下点击单条短信 */
        smsRcvAdapter.setOnSMSWhenLongClickAfterListener((attr, contactIdList) -> {
            SmsFragments.this.contactIdList = contactIdList;
            if (contactIdList.size() == 0) {
                tvSmsBatchSelectAll.setText(select_all);
            }
        });
        
        /* 全选|全不选 */
        smsRcvAdapter.setOnSelectAllOrNotListener(contactIdList -> {
            SmsFragments.this.contactIdList = contactIdList;
        });

    }

    @OnClick({R.id.tv_sms_batchSelectAll, R.id.tv_sms_batchDeteled})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sms_batchSelectAll:// 全选按钮
                selectedAll();
                break;
            case R.id.tv_sms_batchDeteled:// 删除按钮
                if (contactIdList != null && contactIdList.size() > 0) {
                    // 显示弹窗
                    showDelPop();
                }
                break;
        }
    }

    /**
     * 选中全部短信
     */
    private void selectedAll() {
        if (isLongClick) {
            // 修改全选或者全不选
            smsRcvAdapter.selectOrDeSelectAll(tvSmsBatchSelectAll.getText() == select_all ? true : false);
            tvSmsBatchSelectAll.setText(tvSmsBatchSelectAll.getText() == select_all ? deselect_all : select_all);
        }
    }

    /**
     * 显示删除弹窗
     */
    private void showDelPop() {
        Drawable pop_bg = getActivity().getResources().getDrawable(R.drawable.bg_pop_conner);
        View inflate = View.inflate(getActivity(), R.layout.pop_smsdetail_deleted, null);
        int width = (int) (ScreenSize.getSize(getActivity()).width * 0.75f);
        int height = (int) (ScreenSize.getSize(getActivity()).height * 0.22f);
        RippleView tv_delete_cancel = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_cancel);
        RippleView tv_delete_confirm = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_confirm);
        tv_delete_cancel.setOnClickListener(v -> pop_deleted_sms.dismiss());
        tv_delete_confirm.setOnClickListener(v -> {
            pop_deleted_sms.dismiss();
            Logger.t("ma_sms_contactId").v(contactIdList.toString());
            deleteSessionSms(contactIdList);
        });
        pop_deleted_sms = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }
    
    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * 获取会话列表
     */
    private void getSmsContactList() {
        // get sms list
        RX.getInstant().getSMSContactList(0, new ResponseObject<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {
                smsContactList = result;
                // Logger.t("ma_delsms_contactids").v(result.getSMSContactList().toString());
                smsContactSelfList = OtherUtils.getSMSSelfList(smsContactList);
                smsRcvAdapter.notifys(smsContactSelfList);
                //SmsCountHelper.setSmsCount(getActivity(), HomeActivity.mTvHomeMessageCount);
                noSms.setVisibility(smsContactList.getSMSContactList().size() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    /**
     * 删除会话短信
     *
     * @param contactIds
     */
    private void deleteSessionSms(List<Long> contactIds) {
        SmsDeleteSessionHelper sdsh = new SmsDeleteSessionHelper();
        sdsh.setOnResultErrorListener(attr -> delFailedReset());
        sdsh.setOnErrorListener(attr -> delSuccessReset());
        sdsh.setOnDeteledMoreSessionSuccessListener(attr -> delSuccessReset());
        sdsh.deletedOneOrMoreSessionSms(contactIds);
    }

    /**
     * 删除成功后恢复UI
     */
    private void delSuccessReset() {
        isLongClick = false;// 恢复标记位
        ((HomeRxActivity) getActivity()).ivSmsNew.setVisibility(View.VISIBLE);
        llSmsBatchDeteled.setVisibility(isLongClick ? View.VISIBLE : View.GONE);
        ToastUtil_m.show(getActivity(), getString(R.string.sms_delete_success));
        startTimer();// 重启定时器
    }

    /**
     * 删除失败后恢复UI
     */
    private void delFailedReset() {
        isLongClick = false;// 恢复标记位
        if (getActivity() != null) {
            ((HomeRxActivity) getActivity()).ivSmsNew.setVisibility(View.VISIBLE);
            if (llSmsBatchDeteled != null) {
                llSmsBatchDeteled.setVisibility(isLongClick ? View.VISIBLE : View.GONE);
            }
            ToastUtil_m.show(getActivity(), getString(R.string.sms_delete_error));
        }
        startTimer();// 重启定时器
    }

    @Override
    public boolean onBackPressed() {
        if (isLongClick) {// 如果处于长按状态下
            isLongClick = false;
            ((HomeRxActivity) getActivity()).ivSmsNew.setVisibility(View.VISIBLE);
            llSmsBatchDeteled.setVisibility(isLongClick ? View.VISIBLE : View.GONE);
            startTimer();// 重启定时器
            return isLongClick;
        } else {
            return true;
        }
    }

    private void stopTimer() {
        timerHelper.stop();
    }


    @Override
    public void onPause() {
        super.onPause();
        timerHelper.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
