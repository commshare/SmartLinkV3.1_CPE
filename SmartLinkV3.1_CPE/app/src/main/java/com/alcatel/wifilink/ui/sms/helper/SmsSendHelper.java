package com.alcatel.wifilink.ui.sms.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ToastUtil;
import com.alcatel.wifilink.model.sms.SMSSendParam;
import com.alcatel.wifilink.model.sms.SendSMSResult;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.DataUtils;
import com.alcatel.wifilink.utils.ProgressUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;

import java.util.List;

public abstract class SmsSendHelper {

    private Context context;
    private List<String> phoneNums;
    private String content;
    private ProgressDialog pop;
    private Handler handler;
    private int count = 0;

    public SmsSendHelper(Context context, List<String> phoneNums, String content) {
        this.context = context;
        this.phoneNums = phoneNums;
        this.content = content;
        handler = new Handler();
        send();
    }

    /**
     * include the success or failed
     */
    public abstract void sendFinish(int status);

    public void send() {
        popDismiss();
        pop = new ProgressUtils(context).getProgressPop(context.getString(R.string.sms_sending));
        SMSSendParam ssp = new SMSSendParam(-1, content, DataUtils.getCurrent(), phoneNums);
        RX.getInstant().sendSMS(ssp, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                handler.postDelayed(() -> getSendStatus(),3000);/* 发送完毕获取短信状态(延迟3秒) */
                // getSendStatus();/* 发送完毕获取短信状态 */
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                popDismiss();
            }

            @Override
            public void onError(Throwable e) {
                popDismiss();
            }
        });
    }

    /**
     * 发送完毕获取短信状态
     */
    private void getSendStatus() {
        RX.getInstant().GetSendSMSResult(new ResponseObject<SendSMSResult>() {
            @Override
            protected void onSuccess(SendSMSResult result) {
                int sendStatus = result.getSendStatus();
                if (sendStatus == Cons.NONE) {
                    ToastUtil.showMessage(context, context.getString(R.string.none));
                } else if (sendStatus == Cons.SENDING) {
                    noCostCheck();// 间隔5秒,获取5次,如仍是sending则认为欠费
                } else if (sendStatus == Cons.SUCCESS) {
                    ToastUtil.showMessage(context, R.string.succeed);
                } else if (sendStatus == Cons.FAIL_STILL_SENDING_LAST_MSG) {
                    noCostCheck();// 间隔5秒,获取5次,如仍是sending则认为欠费
                } else if (sendStatus == Cons.FAIL_WITH_MEMORY_FULL) {
                    ToastUtil.showMessage(context, R.string.fail_with_memory_full);
                } else if (sendStatus == Cons.FAIL) {
                    ToastUtil.showMessage(context, R.string.fail);
                }
                sendFinish(result.getSendStatus());
                // 临时计数清零
                if (sendStatus != Cons.SENDING & sendStatus != Cons.FAIL_STILL_SENDING_LAST_MSG) {
                    popDismiss();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                popDismiss();
            }

            @Override
            public void onError(Throwable e) {
                popDismiss();
            }
        });
    }

    /**
     * 间隔5秒,获取5次,如仍是sending则认为欠费
     */
    private void noCostCheck() {
        // 5秒后重新获取-获取5次失败认为欠费
        if (count <= 7) {
            handler.postDelayed(() -> {
                count++;
                getSendStatus();
            }, 5000);
        } else {
            ToastUtil_m.show(context, context.getString(R.string.check_sim_normal_or_no_cost));
            popDismiss();
        }
    }

    /* -------------------------------------------- helper -------------------------------------------- */
    public void popDismiss() {
        // 计数器清零
        count = 0;
        if (pop != null) {
            pop.dismiss();
            pop = null;
        }
    }

}
