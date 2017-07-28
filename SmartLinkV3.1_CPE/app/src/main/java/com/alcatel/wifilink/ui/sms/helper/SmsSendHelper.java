package com.alcatel.wifilink.ui.sms.helper;

import android.content.Context;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ToastUtil;
import com.alcatel.wifilink.model.sms.SMSSendParam;
import com.alcatel.wifilink.model.sms.SendSMSResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.ActivityNewSms;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.DataUtils;

import java.util.List;

public abstract class SmsSendHelper {

    private Context context;
    private List<String> phoneNums;
    private String content;

    public SmsSendHelper(Context context, List<String> phoneNums, String content) {
        this.context = context;
        this.phoneNums = phoneNums;
        this.content = content;
        send();
    }

    /**
     * include the success or failed
     */
    public abstract void sendFinish(int status);

    public void send() {
        SMSSendParam ssp = new SMSSendParam(-1, content, DataUtils.getCurrent(), phoneNums);
        API.get().sendSMS(ssp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                getSendStatus();/* 发送完毕获取短信状态 */
            }
        });
    }

    /**
     * 发送完毕获取短信状态
     */
    private void getSendStatus() {
        API.get().GetSendSMSResult(new MySubscriber<SendSMSResult>() {
            @Override
            protected void onSuccess(SendSMSResult result) {
                int sendStatus = result.getSendStatus();
                if (sendStatus == Cons.NONE) {
                    ToastUtil.showMessage(context, context.getString(R.string.none));
                } else if (sendStatus == Cons.SENDING) {
                    getSendStatus();
                } else if (sendStatus == Cons.SUCCESS) {
                    ToastUtil.showMessage(context, R.string.succeed);
                } else if (sendStatus == Cons.FAIL_STILL_SENDING_LAST_MSG) {
                    ToastUtil.showMessage(context, R.string.fail_still_sending_last_message);
                } else if (sendStatus == Cons.FAIL_WITH_MEMORY_FULL) {
                    ToastUtil.showMessage(context, R.string.fail_with_memory_full);
                } else if (sendStatus == Cons.FAIL) {
                    ToastUtil.showMessage(context, R.string.fail);
                }
                sendFinish(result.getSendStatus());
            }
        });
    }

}
