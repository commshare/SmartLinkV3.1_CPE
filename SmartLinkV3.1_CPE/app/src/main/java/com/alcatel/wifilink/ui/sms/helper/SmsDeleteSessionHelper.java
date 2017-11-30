package com.alcatel.wifilink.ui.sms.helper;

import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.sms.SMSContentParam;
import com.alcatel.wifilink.model.sms.SMSDeleteParam;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

import java.util.ArrayList;
import java.util.List;

public abstract class SmsDeleteSessionHelper {

    private long contactId;

    public SmsDeleteSessionHelper(long contactId) {
        this.contactId = contactId;
        deleteSessionSms();
    }

    /* **** deleteSessionSms **** */
    private void deleteSessionSms() {
        List<Long> smsIds = new ArrayList<>();// 抽取所有需要删除的短信ID
        API.get().getSmsInitState(new MySubscriber<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                getSessionSmss();// 获取一个会话里的所有短信
            }

            /* 获取一个会话中所有的短信ID */
            private void getSessionSmss() {
                SMSContentParam scp = new SMSContentParam(0, contactId);
                API.get().getSMSContentList(scp, new MySubscriber<SMSContentList>() {
                    @Override
                    protected void onSuccess(SMSContentList result) {
                        // get all smsids
                        for (SMSContentList.SMSContentBean scb : result.getSMSContentList()) {
                            smsIds.add(scb.getSMSId());
                        }
                        // delete it
                        deletSmsByIds();
                    }

                    /* 根据短信ID删除 */
                    private void deletSmsByIds() {
                        SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, smsIds);
                        API.get().deleteSMS(sdp, new MySubscriber() {
                            @Override
                            protected void onSuccess(Object result) {
                                deletSuccess();
                            }
                        });
                    }
                });
            }
        });
    }

    public abstract void deletSuccess();

}
