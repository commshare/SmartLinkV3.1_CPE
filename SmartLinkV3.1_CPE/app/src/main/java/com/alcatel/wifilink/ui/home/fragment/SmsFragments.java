package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatel.wifilink.R;
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
import com.alcatel.wifilink.ui.sms.helper.SmsDeletePop;
import com.alcatel.wifilink.ui.sms.helper.SmsDeleteSessionHelper;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class SmsFragments extends Fragment implements View.OnClickListener {

    @BindView(R.id.rcv_sms)
    RecyclerView rcvSms;
    Unbinder unbinder;
    @BindView(R.id.no_sms)
    TextView noSms;
    @BindView(R.id.tv_sms_batchDeteled)
    TextView tvSmsBatchDeteled;

    private HomeRxActivity activity;
    private View inflate;
    private SmsRcvAdapter smsRcvAdapter;
    private TimerHelper timerHelper;
    private SMSContactList smsContactList;// 不带有长按标记的联系人集合
    private List<SMSContactSelf> smsContactSelfList = new ArrayList<>();// 带有长按标记的联系人集合
    private SmsDeletePop deletePop;
    private RippleView tv_delete_cancel;
    private RippleView tv_delete_confirm;
    private long contactId_longClick;// 被长按的短信列表条目contactId

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
        resetUi();
        initView();
        initEvent();
        return inflate;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            resetUi();
        }
    }

    private void resetUi() {
        if (activity != null) {
            activity.tabFlag = Cons.TAB_SMS;
            activity.llNavigation.setVisibility(View.VISIBLE);
            activity.rlBanner.setVisibility(View.VISIBLE);
        } else {
            ((HomeRxActivity)getActivity()).tabFlag = Cons.TAB_SMS;
            ((HomeRxActivity)getActivity()).llNavigation.setVisibility(View.VISIBLE);
            ((HomeRxActivity)getActivity()).rlBanner.setVisibility(View.VISIBLE);   
        }
        
    }

    /* **** initView **** */
    private void initView() {
        rcvSms.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        smsRcvAdapter = new SmsRcvAdapter(getActivity(), smsContactSelfList);
        rcvSms.setAdapter(smsRcvAdapter);
    }

    /* **** initEvent **** */
    private void initEvent() {
        // 长按短信列表item
        smsRcvAdapter.setOnRcvLongClickListener(position -> {
            // 1. get contactId which long click
            SMSContactList.SMSContact smsContact = smsContactList.getSMSContactList().get(position);
            contactId_longClick = smsContact.getContactId();
            // 2. show pop
            deletePop = new SmsDeletePop(getActivity()) {
                @Override
                public void getView(View inflate) {
                    tv_delete_cancel = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_cancel);
                    tv_delete_confirm = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_confirm);
                    tv_delete_cancel.setOnClickListener(SmsFragments.this);
                    tv_delete_confirm.setOnClickListener(SmsFragments.this);
                }
            };
        });
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
    public void onPause() {
        super.onPause();
        timerHelper.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sms_batchDeteled:
                // TODO: 2017/12/17 0017
                break;
            case R.id.tv_smsdetail_detele_cancel:
                if (deletePop != null) {
                    deletePop.getPop().dismiss();
                }
                break;
            case R.id.tv_smsdetail_detele_confirm:
                deleteSessionSms(contactId_longClick);
                break;
        }
    }
    
    /* -------------------------------------------- HELPER -------------------------------------------- */

    /* **** getSmsContactList **** */
    private void getSmsContactList() {
        // get sms list
        RX.getInstant().getSMSContactList(0, new ResponseObject<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {
                smsContactList = result;
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

    /* **** deleteSessionSms **** */
    private void deleteSessionSms(long contactId) {
        new SmsDeleteSessionHelper(contactId) {
            @Override
            public void deletSuccess() {
                deletePop.getPop().dismiss();
                ToastUtil_m.show(getActivity(), getString(R.string.sms_delete_success));
                getSmsContactList();
            }
        };
    }
}
