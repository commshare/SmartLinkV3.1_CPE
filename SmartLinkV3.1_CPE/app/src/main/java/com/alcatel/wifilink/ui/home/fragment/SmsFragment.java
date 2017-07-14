package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.ActivitySmsDetail;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.sms.SmsAdapter;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.wifilink.ui.home.helper.sms.SmsNumHelper;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressLint("ValidFragment")
public class SmsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private View m_view;
    private TextView m_noSmsTv = null;
    private ListView mLv_smsContactList = null;

    public List<SMSContactList.SMSContact> smsContactList = new ArrayList<>();
    private SmsAdapter smsAdapter;
    private TimerHelper timerHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        m_view = View.inflate(getActivity(), R.layout.fragment_home_message, null);
        mLv_smsContactList = (ListView) m_view.findViewById(R.id.sms_list_view);
        m_noSmsTv = (TextView) m_view.findViewById(R.id.no_sms);

        smsAdapter = new SmsAdapter(getActivity(), smsContactList);
        mLv_smsContactList.setAdapter(smsAdapter);

        mLv_smsContactList.setOnItemClickListener(this);
        mLv_smsContactList.setOnItemLongClickListener(this);
        return m_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // start timer
        startTimer();
    }

    /* -------------------------------------------- 1.START TIMER -------------------------------------------- */
    /* **** 1.start time to get sms info **** */
    private void startTimer() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                // get all status
                getSmsAllStatus();
            }
        };
        timerHelper.start(5000);
    }

    /* **** 2.getSmsAllStatus **** */
    private void getSmsAllStatus() {
        /* check the sms status is completed */
        API.get().getSmsInitState(new MySubscriber<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                int state = result.getState();
                /* sms init completed */
                if (state == Cons.SMS_COMPLETE) {
                    // get sms unread count 
                    RefreshNewSmsNumber();
                    // get sms list
                    getSmsList();

                } else if (state == Cons.SMS_INITING) {
                    // show default ui
                    showDefaultUi();
                }
            }

        });
    }
    
    /* -------------------------------------------- 1.START TIMER -------------------------------------------- */


    /* -------------------------------------------- 2.GET ALL STATUS -------------------------------------------- */
    /* **** RefreshNewSmsNumber **** */
    private void RefreshNewSmsNumber() {
        // show the sms count in homeactivity group
        SmsCountHelper.setSmsCount(getActivity(), HomeActivity.mTvHomeMessageCount);
    }

    /* **** getSmsContactList **** */
    private void getSmsList() {
        API.get().getSMSContactList(0, new MySubscriber<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {/* set sms list ui visible */
                // 1. get sms lists
                smsContactList = result.getSMSContactList();
                // 2. show effect ui
                showSmsEffectUi(smsContactList);
                // 3. refresh adapter
                smsAdapter.notifity(smsContactList);
            }
        });
    }

    /* **** showSmsEffectUi **** */
    private void showSmsEffectUi(List<SMSContactList.SMSContact> smsContactList) {
        if (smsContactList.size() == 0) {
            m_noSmsTv.setText(R.string.sms_empty);
            Drawable d = getActivity().getResources().getDrawable(R.drawable.sms_empty);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            m_noSmsTv.setCompoundDrawables(null, d, null, null);
            m_noSmsTv.setVisibility(View.VISIBLE);
            mLv_smsContactList.setVisibility(View.GONE);
        } else {
            m_noSmsTv.setVisibility(View.GONE);
            mLv_smsContactList.setVisibility(View.VISIBLE);
        }
    }

    /* **** showDefaultUi **** */
    private void showDefaultUi() {
        m_noSmsTv.setText(R.string.sms_init);
        Drawable d = getActivity().getResources().getDrawable(R.drawable.sms_init);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_noSmsTv.setCompoundDrawables(null, d, null, null);
        m_noSmsTv.setVisibility(View.VISIBLE);
        mLv_smsContactList.setVisibility(View.GONE);
    }
    /* -------------------------------------------- 2.GET ALL STATUS -------------------------------------------- */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent();
        intent.setClass(getActivity(), ActivitySmsDetail.class);// ActivitySmsDetail

        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        String phonenums = SmsNumHelper.getNew(smsContact.getPhoneNumber());// phone nums
        long contactId = smsContact.getContactId();// contactId

        intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_SMS_NUMBER, phonenums);
        intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_CONTACT_ID, contactId);

        getActivity().startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        timerHelper.stop();
    }

}
