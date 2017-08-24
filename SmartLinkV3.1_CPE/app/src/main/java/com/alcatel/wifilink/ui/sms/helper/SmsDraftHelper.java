package com.alcatel.wifilink.ui.sms.helper;

import android.app.Activity;

import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.sms.SMSContentParam;
import com.alcatel.wifilink.model.sms.SMSDeleteParam;
import com.alcatel.wifilink.model.sms.SMSSaveParam;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by qianli.ma on 2017/7/12.
 */

public class SmsDraftHelper {

    private Activity activity;
    private long contactId;
    private OnNoSimListener onNoSimListener;
    private OnGetDraftListener onGetDraftListener;
    private OnClearDraftListener onClearDraftListener;
    private OnSaveDraftListener onSaveDraftListener;

    public SmsDraftHelper(Activity activity, long contactId) {
        this.activity = activity;
        this.contactId = contactId;
    }

    public interface OnNoSimListener {
        void noSim();
    }

    public void setOnNoSimListener(OnNoSimListener onNoSimListener) {
        this.onNoSimListener = onNoSimListener;
    }

    public interface OnGetDraftListener {
        void getDraft(String draft);
    }

    public void setOnGetDraftListener(OnGetDraftListener onGetDraftListener) {
        this.onGetDraftListener = onGetDraftListener;
    }

    public interface OnClearDraftListener {
        void clear();
    }

    public void setOnClearDraftListener(OnClearDraftListener onClearDraftListener) {
        this.onClearDraftListener = onClearDraftListener;
    }

    public interface OnSaveDraftListener {
        void success();
    }

    public void setOnSaveDraftListener(OnSaveDraftListener onSaveDraftListener) {
        this.onSaveDraftListener = onSaveDraftListener;
    }

    /* **** getDraftSms: 获取草稿短信--> 只执行1次 **** */
    public void getDraftSms() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() != Cons.READY) {// no sim
                    if (onNoSimListener != null) {
                        onNoSimListener.noSim();
                    }
                } else {// normal
                    getDraftContent(activity, contactId);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
            }
        });
    }

    /* 获取草稿短信 */
    private void getDraftContent(Activity activity, long contactId) {
        SMSContentParam ssp = new SMSContentParam(0, contactId);
        API.get().getSMSContentList(ssp, new MySubscriber<SMSContentList>() {
            @Override
            protected void onSuccess(SMSContentList result) {

                List<SMSContentList.SMSContentBean> scbs = new ArrayList<>();
                for (SMSContentList.SMSContentBean scb : result.getSMSContentList()) {
                    if (scb.getSMSType() == Cons.DRAFT) {// add draft sms
                        scbs.add(scb);
                    }
                }
                activity.runOnUiThread(() -> {
                    String draft = "";
                    if (scbs.size() > 0) {
                        Collections.sort(scbs, new SmsContentSortHelper());
                        SMSContentList.SMSContentBean scb = scbs.get(0);
                        draft = scb.getSMSContent();
                    }
                    if (onGetDraftListener != null) {
                        onGetDraftListener.getDraft(draft);
                    }
                });

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
            }
        });
    }

    /* 清空草稿短信 */
    public void clearDraft() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() != Cons.READY) {
                    if (onNoSimListener != null) {
                        onNoSimListener.noSim();
                    }
                } else {
                    clearSms(contactId);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
            }
        });


    }

    /* 清空指定的草稿短信 */
    private void clearSms(long contactId) {
        // 先清空原先的草稿
        List<Long> draftList = new ArrayList<>();
        // 获取所有草稿短信
        SMSContentParam ssp = new SMSContentParam(0, contactId);
        API.get().getSMSContentList(ssp, new MySubscriber<SMSContentList>() {
            @Override
            protected void onSuccess(SMSContentList result) {
                for (SMSContentList.SMSContentBean scb : result.getSMSContentList()) {
                    if (scb.getSMSType() == Cons.DRAFT) {
                        draftList.add(scb.getSMSId());
                    }
                }

                // 删除全部草稿短信
                SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, draftList);
                API.get().deleteSMS(sdp, new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        if (onClearDraftListener != null) {
                            onClearDraftListener.clear();
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        if (onClearDraftListener != null) {
                            onClearDraftListener.clear();
                        }
                    }
                });

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
            }
        });
    }

    /* 保存草稿短信 */
    public void saveDraftSms(List<String> phoneNum, String content) {
        SMSSaveParam sssp = new SMSSaveParam(-1, content, DataUtils.getCurrent(), phoneNum);
        API.get().saveSMS(sssp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                if (onSaveDraftListener != null) {
                    onSaveDraftListener.success();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
            }
        });
    }

}
