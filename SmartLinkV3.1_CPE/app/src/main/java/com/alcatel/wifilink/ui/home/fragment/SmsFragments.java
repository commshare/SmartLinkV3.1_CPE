package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.rcv.SmsRcvAdapter;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.wifilink.ui.sms.helper.SmsDeletePop;
import com.alcatel.wifilink.ui.sms.helper.SmsDeleteSessionHelper;

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

    private HomeActivity activity;
    private View inflate;
    private SmsRcvAdapter smsRcvAdapter;
    private TimerHelper timerHelper;
    private SMSContactList smsContactList;
    private SmsDeletePop deletePop;
    private RippleView tv_delete_cancel;
    private RippleView tv_delete_confirm;
    private long contactId_longClick;// 被长按的短信列表条目contactId

    public SmsFragments() {

    }

    public SmsFragments(Activity activity) {
        this.activity = (HomeActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_sms_update, null);
        unbinder = ButterKnife.bind(this, inflate);
        initView();
        initEvent();
        return inflate;
    }

    /* **** initView **** */
    private void initView() {
        rcvSms.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        smsRcvAdapter = new SmsRcvAdapter(getActivity(), smsContactList);
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
        timerHelper.start(2000);
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
        API.get().getSMSContactList(0, new MySubscriber<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {
                smsContactList = result;
                smsRcvAdapter.notifys(smsContactList);
                //SmsCountHelper.setSmsCount(getActivity(), HomeActivity.mTvHomeMessageCount);
                noSms.setVisibility(smsContactList.getSMSContactList().size() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.d("ma_getSmsContactlist", "error: " + error.getMessage().toString());
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
