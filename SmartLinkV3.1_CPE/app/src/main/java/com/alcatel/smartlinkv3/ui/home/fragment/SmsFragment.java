package com.alcatel.smartlinkv3.ui.home.fragment;

import android.content.Intent;
import android.content.IntentFilter;
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

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.model.SMSContactItemModel;
import com.alcatel.smartlinkv3.business.model.SmsContactMessagesModel;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.ui.activity.ActivitySmsDetail;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SMSSummaryItem;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsAdapter;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsBroadcastReceiver;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsCountHelper;

import java.util.ArrayList;


public class SmsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SmsBroadcastReceiver.OnSmsRequestListener {

    private ListView m_smsContactMessagesList = null;
    //	private Button m_btnNewSms = null;
    private TextView m_noSmsTv = null;

    private SmsBroadcastReceiver m_receiver;
    private ArrayList<SMSSummaryItem> m_smsContactMessagesLstData = new ArrayList<SMSSummaryItem>();

    public final static int SMS_MATCH_NUM = 7;
    private View m_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_message, null);
        m_smsContactMessagesList = (ListView) m_view.findViewById(R.id.sms_list_view);
        SmsAdapter smsAdapter = new SmsAdapter(getActivity(), m_smsContactMessagesLstData);
        m_smsContactMessagesList.setAdapter(smsAdapter);
        m_noSmsTv = (TextView) m_view.findViewById(R.id.no_sms);

        m_receiver = new SmsBroadcastReceiver();
        m_receiver.setOnSmsRequestListener(this);
        m_smsContactMessagesList.setOnItemClickListener(this);
        m_smsContactMessagesList.setOnItemLongClickListener(this);
        return m_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BusinessManager.getInstance().getSMSInit() == SMSInit.Complete)
            BusinessManager.getInstance().getContactMessagesAtOnceRequest();

        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET));

        RefreshNewSmsNumber();
        getListSmsSummaryData();
        displayUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(m_receiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ActivitySmsDetail.class);
        intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_SMS_NUMBER, m_smsContactMessagesLstData.get(position).strNumber);
        intent.putExtra(ActivitySmsDetail.INTENT_EXTRA_CONTACT_ID, m_smsContactMessagesLstData.get(position).nContactid);
        getActivity().startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void displayUIs() {
        displayUI();
    }

    @Override
    public void RefreshNewSmsNumbers() {
        RefreshNewSmsNumber();
    }

    @Override
    public void getListSmsSummaryDatas() {
        getListSmsSummaryData();
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */
    private void RefreshNewSmsNumber() {
        // show the sms count in homeactivity group
        SmsCountHelper.setSmsCount(HomeActivity.mTvHomeMessageCount);
    }

    private void getListSmsSummaryData() {

        m_smsContactMessagesLstData.clear();
        SmsContactMessagesModel messages = BusinessManager.getInstance().getContactMessages();
        for (int i = 0; i < messages.SMSContactList.size(); i++) {
            SMSContactItemModel sms = messages.SMSContactList.get(i);
            SMSSummaryItem item = new SMSSummaryItem();
            for (int j = 0; j < sms.PhoneNumber.size(); j++) {
                if (j == sms.PhoneNumber.size() - 1)
                    item.strNumber += sms.PhoneNumber.get(j);
                else
                    item.strNumber = item.strNumber + sms.PhoneNumber.get(j) + ";";
            }
            item.nUnreadNumber = sms.UnreadCount;
            item.nCount = sms.TSMSCount;
            item.enumSmsType = sms.SMSType;
            item.strSummaryContent = sms.SMSContent;
            item.strSummaryTime = sms.SMSTime;
            item.nContactid = sms.ContactId;
            m_smsContactMessagesLstData.add(item);
        }

    }

    private void displayUI() {
        if (BusinessManager.getInstance().getSMSInit() == SMSInit.Initing) {
            m_noSmsTv.setText(R.string.sms_init);
            Drawable d = getActivity().getResources().getDrawable(R.drawable.sms_init);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            m_noSmsTv.setCompoundDrawables(null, d, null, null);
            m_noSmsTv.setVisibility(View.VISIBLE);
            m_smsContactMessagesList.setVisibility(View.GONE);
        } else {
            SmsContactMessagesModel messages = BusinessManager.getInstance().getContactMessages();
            if (messages.SMSContactList.size() == 0) {
                m_noSmsTv.setText(R.string.sms_empty);
                Drawable d = getActivity().getResources().getDrawable(R.drawable.sms_empty);
                d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
                m_noSmsTv.setCompoundDrawables(null, d, null, null);
                m_noSmsTv.setVisibility(View.VISIBLE);
                m_smsContactMessagesList.setVisibility(View.GONE);
            } else {
                m_noSmsTv.setVisibility(View.GONE);
                m_smsContactMessagesList.setVisibility(View.VISIBLE);
            }
        }
    }

}
