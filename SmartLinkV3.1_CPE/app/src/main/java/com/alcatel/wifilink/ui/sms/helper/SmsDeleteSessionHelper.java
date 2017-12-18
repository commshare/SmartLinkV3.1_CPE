package com.alcatel.wifilink.ui.sms.helper;

import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.sms.SMSContentParam;
import com.alcatel.wifilink.model.sms.SMSDeleteParam;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmsDeleteSessionHelper {

    private List<Long> smsIdsAll;
    private int index = 0;


    public SmsDeleteSessionHelper() {
        index = 0;
        smsIdsAll = new ArrayList<>();
    }

    /**
     * 删除一个会话
     */
    public void deleteOneSessionSms(long contactId) {
        List<Long> smsIds = new ArrayList<>();// 抽取所有需要删除的短信ID
        SMSContentParam scp = new SMSContentParam(0, contactId);
        RX.getInstant().getSMSContentList(scp, new ResponseObject<SMSContentList>() {
            @Override
            protected void onSuccess(SMSContentList result) {
                // getInstant all smsids
                for (SMSContentList.SMSContentBean scb : result.getSMSContentList()) {
                    smsIds.add(scb.getSMSId());
                }
                // deleted it
                SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, smsIds);
                RX.getInstant().deleteSMS(sdp, new ResponseObject() {
                    @Override
                    protected void onSuccess(Object result) {
                        // 删除一个会话成功
                        deletedOneSessionNext(result);
                    }
                });
            }
        });
    }

    /* -------------------------------------------- method2 -------------------------------------------- */

    /**
     * 删除一个或者多个会话短信
     *
     * @param contactIds
     */
    public void deletedOneOrMoreSessionSms(List<Long> contactIds) {
        index = 0;
        // 获取所有的smsIds
        getAllSmsIds(contactIds);
    }

    /**
     * 获取所有的smsIds
     *
     * @param contactIds
     */
    private void getAllSmsIds(List<Long> contactIds) {
        Logger.t("ma_delsms").v("index: " + index);
        if (index < contactIds.size()) {/* 未完成收集前 */
            Logger.t("ma_delsms").v("begin collect: " + contactIds.toString());
            SMSContentParam scp = new SMSContentParam(0, contactIds.get(index));
            RX.getInstant().getSMSContentList(scp, new ResponseObject<SMSContentList>() {
                @Override
                protected void onSuccess(SMSContentList result) {
                    Logger.t("ma_delsms").v("success collect");
                    smsIdsAll.addAll(OtherUtils.getAllSmsIdByOneSession(result));
                    index++;
                    getAllSmsIds(contactIds);
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    Logger.t("ma_delsms").v("success onResultError");
                    resultErrorNext(error);
                    smsIdsAll.clear();
                    index = 0;
                }

                @Override
                public void onError(Throwable e) {
                    Logger.t("ma_delsms").v("success onError");
                    errorNext(e);
                    smsIdsAll.clear();
                    index = 0;
                }
            });
        } else {/* 已完成收集 */
            Logger.t("ma_contactIds").v("contactIds: " + contactIds.toString());
            Logger.t("ma_contactIds").v("smsIds: " + smsIdsAll.toString());
            getSmsIdsNext(smsIdsAll);
            index = 0;// 恢复导引
            doDelSms(smsIdsAll);// 执行删除
        }
    }

    /**
     * 执行删除
     *
     * @param smsIdsAll
     */
    private void doDelSms(List<Long> smsIdsAll) {
        SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, smsIdsAll);
        RX.getInstant().deleteSMS(sdp, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                Logger.t("ma_delsms").v("deleted success");
                DelMoreSessionSuccessNext(result);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Logger.t("ma_delsms").v("deleted onResultError:" + error.getCode() + ";" + error.getMessage());
                resultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                Logger.t("ma_delsms").v("deleted error:" + e.getMessage());
                errorNext(e);
            }
        });
    }

    private OnSMSIdsListener onSMSIdsListener;

    // 接口OnSMSIdsListener
    public interface OnSMSIdsListener {
        void getSmsIds(List<Long> attr);
    }

    // 对外方式setOnSMSIdsListener
    public void setOnSMSIdsListener(OnSMSIdsListener onSMSIdsListener) {
        this.onSMSIdsListener = onSMSIdsListener;
    }

    // 封装方法getSmsIdsNext
    private void getSmsIdsNext(List<Long> attr) {
        if (onSMSIdsListener != null) {
            onSMSIdsListener.getSmsIds(attr);
        }
    }

    /* -------------------------------------------- method1 -------------------------------------------- */

    /**
     * 删除多个会话(暂时不可用,推荐使用:deletedOneOrMoreSessionSms())
     *
     * @param contactIds 联系人ID
     */
    @Deprecated
    public void deleteMoreSessionSms(List<Long> contactIds) {
        // 1.检测初始化状态
        RX.getInstant().getSmsInitState(new ResponseObject<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                if (result.getState() == Cons.COMPLETE) {
                    // 2.完成状态下进行删除操作
                    SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, contactIds);
                    RX.getInstant().deleteSMS(sdp, new ResponseObject() {
                        @Override
                        protected void onSuccess(Object result) {
                            // 删除多个会话成功
                            Logger.t("ma_delsms_error").v("del success");
                            DelMoreSessionSuccessNext(result);
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            Logger.t("ma_delsms_error").v(error.getMessage() + ";" + error.getCode());
                            resultErrorNext(error);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.t("ma_delsms_error").v(e.getMessage());
                            errorNext(e);
                        }
                    });
                } else {
                    deleteMoreSessionSms(contactIds);
                }

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Logger.t("ma_delsms_error").v(error.getMessage() + ";" + error.getCode());
                resultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                Logger.t("ma_delsms_error").v(e.getMessage());
                errorNext(e);
            }
        });
    }

    private OnResultErrorListener onResultErrorListener;

    // 接口OnResultErrorListener
    public interface OnResultErrorListener {
        void resultError(ResponseBody.Error attr);
    }

    // 对外方式setOnResultErrorListener
    public void setOnResultErrorListener(OnResultErrorListener onResultErrorListener) {
        this.onResultErrorListener = onResultErrorListener;
    }

    // 封装方法resultErrorNext
    private void resultErrorNext(ResponseBody.Error attr) {
        if (onResultErrorListener != null) {
            onResultErrorListener.resultError(attr);
        }
    }

    private OnErrorListener onErrorListener;

    // 接口OnErrorListener
    public interface OnErrorListener {
        void error(Throwable attr);
    }

    // 对外方式setOnErrorListener
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    // 封装方法errorNext
    private void errorNext(Throwable attr) {
        if (onErrorListener != null) {
            onErrorListener.error(attr);
        }
    }

    private OnDeteledMoreSessionSuccessListener onDeteledMoreSessionSuccessListener;

    // 接口OnDeteledMoreSessionSuccessListener
    public interface OnDeteledMoreSessionSuccessListener {
        void DelMoreSessionSuccess(Object attr);
    }

    // 对外方式setOnDeteledMoreSessionSuccessListener
    public void setOnDeteledMoreSessionSuccessListener(OnDeteledMoreSessionSuccessListener onDeteledMoreSessionSuccessListener) {
        this.onDeteledMoreSessionSuccessListener = onDeteledMoreSessionSuccessListener;
    }

    // 封装方法DelMoreSessionSuccessNext
    private void DelMoreSessionSuccessNext(Object attr) {
        if (onDeteledMoreSessionSuccessListener != null) {
            onDeteledMoreSessionSuccessListener.DelMoreSessionSuccess(attr);
        }
    }

    private OnDeletedOneSessionListener onDeletedOneSessionListener;

    // 接口OnDeletedOneSessionListener
    public interface OnDeletedOneSessionListener {
        void deletedOneSession(Object attr);
    }

    // 对外方式setOnDeletedOneSessionListener
    public void setOnDeletedOneSessionListener(OnDeletedOneSessionListener onDeletedOneSessionListener) {
        this.onDeletedOneSessionListener = onDeletedOneSessionListener;
    }

    // 封装方法deletedOneSessionNext
    private void deletedOneSessionNext(Object attr) {
        if (onDeletedOneSessionListener != null) {
            onDeletedOneSessionListener.deletedOneSession(attr);
        }
    }


}
