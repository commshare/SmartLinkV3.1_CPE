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

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.rcv.SmsRcvAdapter;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class SmsFragments extends Fragment {

    @BindView(R.id.rcv_sms)
    RecyclerView rcvSms;
    Unbinder unbinder;
    private HomeActivity activity;
    private View inflate;
    private SmsRcvAdapter smsRcvAdapter;
    private TimerHelper timerHelper;
    private SMSContactList smsContactList;

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
        return inflate;
    }

    /* **** initView **** */
    private void initView() {
        rcvSms.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        smsRcvAdapter = new SmsRcvAdapter(getActivity(), smsContactList);
        rcvSms.setAdapter(smsRcvAdapter);
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

    /* **** getSmsContactList **** */
    private void getSmsContactList() {
        // get sms list
        API.get().getSMSContactList(0, new MySubscriber<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {
                smsContactList = result;
                smsRcvAdapter.notifys(smsContactList);
                SmsCountHelper.setSmsCount(getActivity(), HomeActivity.mTvHomeMessageCount);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
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
