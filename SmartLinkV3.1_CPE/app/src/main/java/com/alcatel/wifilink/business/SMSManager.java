package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.alcatel.wifilink.business.model.SMSContactItemModel;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.business.model.SmsContactMessagesModel;
import com.alcatel.wifilink.business.model.SmsContentMessagesModel;
import com.alcatel.wifilink.business.sms.HttpSms;
import com.alcatel.wifilink.business.sms.SendStatusResult;
import com.alcatel.wifilink.business.sms.SmsContactListResult;
import com.alcatel.wifilink.business.sms.SmsContentListResult;
import com.alcatel.wifilink.business.sms.SmsInitResult;
import com.alcatel.wifilink.common.Const;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ENUM.EnumSMSDelFlag;
import com.alcatel.wifilink.common.ENUM.SMSInit;
import com.alcatel.wifilink.common.ENUM.SendStatus;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SMSManager extends BaseManager {
    private SMSInit m_smsInit = SMSInit.Initing;
    private SmsContactMessagesModel m_contactMessages = new SmsContactMessagesModel();

    private Timer m_getSmsRollTimer = new Timer();
    GetSMSInitTask m_getSmsInitTask = null;
    GetContactMessagesTask m_getContactMessagesTask = null;

    @Override
    protected void clearData() {
        // TODO Auto-generated method stub
        m_smsInit = SMSInit.Initing;
        m_contactMessages.clear();
    }

    @Override
    protected void stopRollTimer() {
        stopGetSmsInitTask();
        stopGetContactMessagesTask();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();

        if (MessageUti.CPE_WIFI_CONNECT_CHANGE.equals(action)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {

            }
        }

        if (MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET.equals(action)) {
            if (ok) {
                if (m_smsInit == SMSInit.Complete) {
                    stopGetSmsInitTask();
                    startGetContactMessagesTask();
                }
            }
        }

        if (MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
            if (ok) {
                SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
                if (simStatus.m_SIMState == ENUM.SIMState.Accessable) {
                    startGetSmsInitTask();
                } else {
                    sentSMSInitChangedMessage(SMSInit.Initing);
                    stopRollTimer();
                    m_contactMessages.clear();
                }
            }
        }
    }

    public SMSManager(Context context) {
        super(context);
        //CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
        m_context.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET));

        m_context.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }


    public SMSInit getSMSInit() {
        return m_smsInit;
    }

    public SmsContactMessagesModel getContactMessages() {
        return m_contactMessages;
    }

    public int GetUnreadSmsNumber() {
        int nUnreadCount = 0;
        ArrayList<SMSContactItemModel> lst = m_contactMessages.SMSContactList;
        if (null != lst) {
            for (int nIndex = 0; nIndex < lst.size(); nIndex++) {
                SMSContactItemModel sms = lst.get(nIndex);
                nUnreadCount += sms.UnreadCount;
            }
        }

        return nUnreadCount;
    }

    //GetSMSInitStatus ////////////////////////////////////////////////////////////////////////////////////////// 
    private void startGetSmsInitTask() {
        SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
        if (simStatus.m_SIMState != ENUM.SIMState.Accessable)
            return;

        if (m_getSmsInitTask == null) {
            m_getSmsInitTask = new GetSMSInitTask();
            m_getSmsRollTimer.scheduleAtFixedRate(m_getSmsInitTask, 0, 10 * 1000);
        }
    }

    private void stopGetSmsInitTask() {
        if (m_getSmsInitTask != null) {
            m_getSmsInitTask.cancel();
            m_getSmsInitTask = null;
        }
    }

    class GetSMSInitTask extends TimerTask {
        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSInitStatus(response -> {
                if (response.isOk()) {
                    SmsInitResult result = response.getModelResult();
                    m_smsInit = SMSInit.build(result.Status);
                }

                //        			sendBroadcast(response, MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET);

            }));
        }
    }

    private void sentSMSInitChangedMessage(SMSInit cur) {
        if (m_smsInit != cur) {
            m_smsInit = cur;
            //			sendBroadcast(BaseResponse.SUCCESS, MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET);
        }
    }

    //GetSMSContactList ////////////////////////////////////////////////////////////////////////////////////////// 
    public void startGetContactMessagesTask() {
        if (m_smsInit == SMSInit.Initing)
            return;

        if (m_getContactMessagesTask == null) {
            m_getContactMessagesTask = new GetContactMessagesTask();
            m_getSmsRollTimer.scheduleAtFixedRate(m_getContactMessagesTask, 0, 30 * 1000);
        }
    }

    public void stopGetContactMessagesTask() {
        if (m_getContactMessagesTask != null) {
            m_getContactMessagesTask.cancel();
            m_getContactMessagesTask = null;
        }
    }

    public void getContactMessagesAtOnceRequest() {
        if (m_smsInit == SMSInit.Complete) {
            GetContactMessagesTask task = new GetContactMessagesTask();
            m_getSmsRollTimer.schedule(task, 0);
        }
    }

    class GetContactMessagesTask extends TimerTask {
        @Override
        public void run() {
            LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSContactList(0, response -> {
                if (response.isOk()) {
                    SmsContactListResult result = response.getModelResult();
                    m_contactMessages.bulidFromResult(result);
                }

                //        			sendBroadcast(response, MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET);

            }));
        }
    }

    //GetSMSContentList ////////////////////////////////////////////////////////////////////////////////////////// 
    public static String SMS_CONTENT_LIST_EXTRA = "com.alcatel.smartlinkv3.business.smscontentlistextra";

    public void getSMSContentListRequest(DataValue data) {
        int nContactId = (Integer) data.getParamByKey("ContactId");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSContentList(0, nContactId, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                SmsContentMessagesModel model = new SmsContentMessagesModel();
                if (response.isOk()) {
                    SmsContentListResult result = response.getModelResult();
                    model.buildFromResult(result);
                }

                Intent megIntent = response.getIntent(null);
                megIntent.putExtra(SMS_CONTENT_LIST_EXTRA, model);
                //    			m_context.sendBroadcast(megIntent);
            }
        }));
    }

    //DeleteSMS ////////////////////////////////////////////////////////////////////////////////////////// 
    public void deleteSms(DataValue data) {
        EnumSMSDelFlag delFlag = (EnumSMSDelFlag) data.getParamByKey("DelFlag");
        Integer temp = (Integer) data.getParamByKey("ContactId");
        int nContactId = 0;
        if (temp != null)
            nContactId = temp;

        temp = (Integer) data.getParamByKey("SMSId");
        int nSMSId = 0;
        if (temp != null)
            nSMSId = temp;

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.DeleteSMS(EnumSMSDelFlag.antiBuild(delFlag), nContactId, nSMSId, response -> {
            //    			sendBroadcast(response, MessageUti.SMS_DELETE_SMS_REQUSET);
        }));
    }

    private ArrayList<String> getNumberFromString(String number) {
        String[] listNumbers = number.split(";");
        ArrayList<String> phoneNumberLst = new ArrayList<String>();
        for (int i = 0; i < listNumbers.length; i++) {
            if (null == listNumbers[i] || listNumbers[i].length() == 0)
                continue;
            phoneNumberLst.add(listNumbers[i]);
        }

        return phoneNumberLst;
    }

    //SendSMS ////////////////////////////////////////////////////////////////////////////////////////// 
    public void sendSms(DataValue data) {
        String strContent = (String) data.getParamByKey("content");
        String strNumbers = (String) data.getParamByKey("phone_number");
        ArrayList<String> phoneNumberLst = getNumberFromString(strNumbers);

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.SendSMS(-1, strContent, phoneNumberLst, response -> {
            if (response.isOk()) {
                // SendSmsResult sendSmsResult = response.getModelResult();
                // nSendId = sendSmsResult.SmsSendId;
                //                		
                // //getInstant send status
                //                		DataValue dataValue = new DataValue();
                //						dataValue.addParam("sms_send_id", nSendId);
                getSmsSendResult(null);
            }

            //     			sendBroadcast(response, MessageUti.SMS_SEND_SMS_REQUSET);
        }));
    }

    //GetSendSMSResult ////////////////////////////////////////////////////////////////////////////////////////// 
    public void getSmsSendResult(DataValue data) {
        if (m_smsInit == SMSInit.Initing)
            return;

        //final int nSmsSendId = (Integer) data.getParamByKey("sms_send_id");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSendSMSResult(response -> {
            int nSendStatus = 0;
            if (response.isOk()) {
                SendStatusResult sendStatusResult = response.getModelResult();
                nSendStatus = sendStatusResult.SendStatus;
                SendStatus sendStatus = SendStatus.build(nSendStatus);
                if (sendStatus == SendStatus.None) {
                    nSendStatus = 1;
                    sendStatus = SendStatus.build(nSendStatus);
                }
                if (sendStatus != SendStatus.Fail && sendStatus != SendStatus.Success && sendStatus != SendStatus.Fail_Memory_Full) {
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            DataValue dataValue = new DataValue();
                            getSmsSendResult(dataValue);
                        }
                    }, 1000);
                }
            }

            Intent megIntent = response.getIntent(null);
            megIntent.putExtra(Const.SMS_SNED_STATUS, nSendStatus);
            //    			m_context.sendBroadcast(megIntent);
        }));
    }

    //SaveSMS ////////////////////////////////////////////////////////////////////////////////////////// 
    public void SaveSMS(DataValue data) {
        int nSmsId = (Integer) data.getParamByKey("SMSId");//the index of SMS, if savenew SMS is -1, else other.
        String content = (String) data.getParamByKey("Content");
        String number = (String) data.getParamByKey("Number");
        ArrayList<String> phoneNumberLst = getNumberFromString(number);

        LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.SaveSMS(nSmsId, content, phoneNumberLst, response -> {
            //    			sendBroadcast(response, MessageUti.SMS_SAVE_SMS_REQUSET);
        }));
    }
}
