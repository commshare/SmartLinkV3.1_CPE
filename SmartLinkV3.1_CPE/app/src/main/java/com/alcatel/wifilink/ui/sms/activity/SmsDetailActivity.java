package com.alcatel.wifilink.ui.sms.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.helper.base.SmsWatcher;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SMSContentList;
import com.alcatel.wifilink.model.sms.SMSContentParam;
import com.alcatel.wifilink.model.sms.SMSDeleteParam;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.ui.HomeRxActivity;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.sms.adapter.SmsDetatilAdapter;
import com.alcatel.wifilink.ui.sms.helper.SmsDeletePop;
import com.alcatel.wifilink.ui.sms.helper.SmsDraftHelper;
import com.alcatel.wifilink.ui.sms.helper.SmsSendHelper;
import com.alcatel.wifilink.ui.sms.helper.SmsContentSortHelper;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmsDetailActivity extends BaseActivityWithBack implements View.OnClickListener, SmsDetatilAdapter.OnSmsSelectedListener, SmsDetatilAdapter.OnSmsLongClickListener, SmsDetatilAdapter.OnSendSuccessListener {

    @BindView(R.id.tv_smsdetail_date)
    TextView tvSmsdetailDate;// 路由器日期
    @BindView(R.id.rcv_smsdetail)
    RecyclerView rcvSmsdetail;// 消息列表
    @BindView(R.id.rv_smsdetail_send)
    RippleView rvSmsdetailSend;// 发送按钮
    @BindView(R.id.et_smsdetail_send)
    EditText etSmsdetailSend;// 内容编辑区

    public RippleView tv_delete_cancel;
    public RippleView tv_delete_confirm;

    // action bar
    private ActionBar actionbar;
    private ImageButton ib_back;
    private TextView tv_title;
    private ImageView iv_delete;

    private TimerHelper timerHelper;
    private SmsDetatilAdapter adapter;
    private SMSContentList smsContentList;
    private SMSContactList.SMSContact smsContact;
    private List<Long> smsIds = new ArrayList<>();
    private boolean isLongClick;// 处于长按状态
    private String draftSms = "";// 草稿短信
    private long draftSmsId;// 草稿短信ID
    private SmsDeletePop deletePop;
    private LinearLayoutManager linearLayoutManager;
    private boolean toastFlag = true;
    private String dateTimebanner = "";
    private String ActivityName = "SmsDetailActivity";
    private int current_sms_num = 0;// 最近一次获取到的短信条数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.t("ma_sms_oncreate_ac").vv("ma_sms_oncreate_ac: " + getClass().getSimpleName());
        ActivityName = getClass().getSimpleName();
        setContentView(R.layout.activity_sms_detail);
        ButterKnife.bind(this);
        actionbar = getSupportActionBar();
        EventBus.getDefault().register(this);
        initEvent();
        initView();
        initData();
    }

    private void initEvent() {
        //  添加短信字数限制监听
        new SmsWatcher(this, etSmsdetailSend);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getSmsContactInfo(SMSContactList.SMSContact smsContact) {
        this.smsContact = smsContact;
    }

    private void initView() {
        // set action bar
        setActionbar();
        // set recycle view
        setRecycleView();
        // set recycle touch listener
        setRecycleListener();
    }

    private void initData() {
        // 读取草稿短信
        getDraftSms();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSmsContents(true);
        startTimer();
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_smsdetail_back:// back to sms fragment
                clickBack();
                break;
            case R.id.iv_smsdetail_delete:// show the delete pop
                deleteSmsPop();
                break;
            case R.id.tv_smsdetail_detele_cancel:// click cancel
                resetLongClickFlag();// 1. reset the status ui
                getSmsContents(false);// 2. getInstant data again
                break;
            case R.id.tv_smsdetail_detele_confirm:// click confirm
                deleteSms();
                break;
        }
    }

    /* 发送按钮 */
    @OnClick(R.id.rv_smsdetail_send)
    public void onViewClicked() {
        sendSms();
    }

    /* 短信被选中 */
    @Override
    public void selected(List<Long> smsIds) {
        // 被选中的短信ID集合
        SmsDetailActivity.this.smsIds = smsIds;
    }

    /* 短信被长按 */
    @Override
    public void smsLongClick() {
        // 短信被长按
        isLongClick = true;
        timerHelper.stop();
        iv_delete.setVisibility(View.VISIBLE);
        etSmsdetailSend.setEnabled(!isLongClick);
        rvSmsdetailSend.setClickable(!isLongClick);
    }

    /* 重新发送短信 */
    @Override
    public void sendAgainSuccess() {
        // 重新获取短信
        getSmsContents(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        current_sms_num = 0;
        timerHelper.stop();
    }

    /* **** setActionbar **** */
    private void setActionbar() {
        new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                // when click--> save draft
                ib_back = (ImageButton) view.findViewById(R.id.ib_smsdetail_back);
                ib_back.setOnClickListener(SmsDetailActivity.this);
                // set phonenum
                tv_title = (TextView) view.findViewById(R.id.tv_smsdetail_title);
                tv_title.setText(OtherUtils.stitchPhone(SmsDetailActivity.this, smsContact.getPhoneNumber()));
                // set delete show or hide
                iv_delete = (ImageView) view.findViewById(R.id.iv_smsdetail_delete);
                iv_delete.setOnClickListener(SmsDetailActivity.this);
                iv_delete.setVisibility(View.GONE);
            }
        }.settingActionbarAttr(this, actionbar, R.layout.actionbar_smsdetail);
    }

    /* **** setRecycleView **** */
    private void setRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvSmsdetail.setLayoutManager(linearLayoutManager);
        adapter = new SmsDetatilAdapter(this, linearLayoutManager, smsContentList, smsContact.getPhoneNumber());
        adapter.setOnSmsSelectedListener(this);// 监听item被选中
        adapter.setOnSmsLongClickListener(this);// 短信被长按
        adapter.setOnSendSuccessListener(this);
        rcvSmsdetail.setAdapter(adapter);
    }

    /* **** setRecycleListener: 滑动监听并改变router time ui **** */
    private void setRecycleListener() {
        rcvSmsdetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 1. according the coodinary of top to set the alpha
                    if (!rcvSmsdetail.canScrollVertically(-1)) {// 已经到顶
                        tvSmsdetailDate.setAlpha(0.2f);
                    } else {
                        tvSmsdetailDate.setAlpha(0.8f);
                    }
                } else {
                    tvSmsdetailDate.setAlpha(0.3f);
                }
                // 2. set time according the first item who can see
                setPositionTextTime();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /* **** setPositionTextTime: 根据当前第一个可视的SMS显示对应的短信时间 **** */
    private void setPositionTextTime() {
        runOnUiThread(() -> {
            List<SMSContentList.SMSContentBean> list = filterDraft(smsContentList);
            int pos = linearLayoutManager.findFirstVisibleItemPosition();
            dateTimebanner = list.get(pos).getSMSTime();
            // dateTimebanner = dateTimebanner.replace("-", "/");
            tvSmsdetailDate.setText(dateTimebanner);
        });
    }

    /* 提出草稿短信后按日期排序 */
    public List<SMSContentList.SMSContentBean> filterDraft(SMSContentList smsContentList) {
        List<SMSContentList.SMSContentBean> list = new ArrayList();
        for (SMSContentList.SMSContentBean scb : smsContentList.getSMSContentList()) {
            if (scb.getSMSType() == Cons.DRAFT) {
                continue;
            }
            list.add(scb);
        }
        Collections.sort(list, new SmsContentSortHelper());
        return list;
    }

    /* **** setRecyclePositionToLast: 定位到最后 **** */
    private void setRecyclePositionToLast(int position) {
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    /* **** startTimer **** */
    private void startTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                getSmsContents(false);
            }
        };
        timerHelper.start(Cons.PERIOD);
    }

    /* **** timertask: getSmsContents **** */
    private void getSmsContents(boolean isSetRcvToLast) {
        // 检测sim卡是否有插入
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.READY) {// no sim
                    getContent(isSetRcvToLast);
                } else {// normal
                    // ToastUtil_m.show(SmsDetailActivity.this, getString(R.string.home_no_sim));
                    finish();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });

    }

    /**
     * @param isSetRcvToLast 是否需要定位rcv到底部
     */
    /* **** getContent **** */
    private void getContent(boolean isSetRcvToLast) {

        RX.getInstant().getSmsInitState(new ResponseObject<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                if (result.getState() == Cons.SMS_COMPLETE) {
                    // 重新获取当前号码是否有未读消息
                    RX.getInstant().getSMSContactList(0, new ResponseObject<SMSContactList>() {
                        @Override
                        protected void onSuccess(SMSContactList result) {

                            // 1.1.判断最新获取的contact id集合是否包含传进来的id--> 是:回退
                            List<Long> contactIds = new ArrayList<>();
                            for (SMSContactList.SMSContact scc : result.getSMSContactList()) {
                                contactIds.add(scc.getContactId());
                            }
                            if (!contactIds.contains(smsContact.getContactId())) {
                                clickBack();
                                return;
                            }

                            // 1.2.否:获取最新的短信列表
                            for (SMSContactList.SMSContact scc : result.getSMSContactList()) {
                                long contactId = scc.getContactId();
                                // 1.是当前的contact id
                                if (contactId == smsContact.getContactId()) {
                                    // 2.获取当前号码的未读消息数
                                    int unreadCount = scc.getUnreadCount();
                                    if (unreadCount > 0) {/* 如果有未读消息 */
                                        // 3.用户是否停留在短信详情页
                                        if (HomeRxActivity.CURRENT_ACTIVITY.contains(ActivityName)) {
                                            // 4.向接口发起请求
                                            realToGetContent();
                                        } else {
                                            // 3.用户离开
                                            // 4.把未读消息数量保存到MAP中
                                            HomeRxActivity.smsUnreadMap.put(contactId, unreadCount);
                                        }
                                    } else {/* 没有未读消息, 直接获取内容--> 正常显示已读的消息 */
                                        realToGetContent();// 向接口发起请求
                                    }
                                }
                            }
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {

                        }
                    });
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }

            /**
             * 向接口发起请求
             */
            private void realToGetContent() {
                long contactId = smsContact.getContactId();
                SMSContentParam ssp = new SMSContentParam(0, contactId);
                RX.getInstant().getSMSContentList(ssp, new ResponseObject<SMSContentList>() {
                    @Override
                    protected void onSuccess(SMSContentList result) {
                        boolean isLast = false;
                        // 1. refresh the list
                        smsContentList = result;
                        // 1.1.获取最新信息数
                        int newSize = smsContentList.getSMSContentList().size();
                        if (newSize > current_sms_num) {
                            isLast = true;// 如果最新获取到的信息数比临时缓存的大, 则修改[滚动到最后一条]标记
                        }
                        // 更新临时最新消息数
                        current_sms_num = newSize;
                        // 刷新适配器
                        if (adapter != null) {
                            adapter.notifys(smsContentList, isLast);
                        }
                        // 2. refresh the router time
                        setPositionTextTime();
                        // 3. force to  set rcv position to last
                        if (isLast) {
                            setRecyclePositionToLast(adapter.getItemCount() - 1);
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        // when the current number have no sms, then close it
                        finish();
                    }
                });
            }
        });
    }

    /* **** 获取草稿短信(只执行1次)  **** */
    private void getDraftSms() {

        SmsDraftHelper sdfp = new SmsDraftHelper(this, smsContact.getContactId());
        sdfp.setOnNoSimListener(() -> {
            ToastUtil_m.show(SmsDetailActivity.this, getString(R.string.home_no_sim));
            finish();
        });
        sdfp.setOnGetDraftListener(draft -> {
            etSmsdetailSend.setText(draft);
            etSmsdetailSend.setSelection(draft.length());
        });
        sdfp.getDraftSms();
    }

    /* **** clickBack **** */
    private void clickBack() {
        if (isLongClick) {// if long click status
            resetLongClickFlag();
            startTimer();
        } else {
            // 查看编辑域是否有内容--> 保存为草稿
            draftSms();
            finish();
        }
    }

    /* **** sendSms **** */
    private void sendSms() {
        // 1. getInstant send sms content from et
        String content = etSmsdetailSend.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil_m.show(this, getString(R.string.sms_empty));
            return;
        }
        // 2. send sms
        new SmsSendHelper(this, smsContact.getPhoneNumber(), content) {
            @Override
            public void sendFinish(int status) {/* 发送完成 */
                if (status == Cons.SUCCESS) {
                    // 注意此处, 一调用即为标记为已读
                    getSmsContents(true);
                } else {
                    finish();
                }
            }
        };
        // 3. clear the et
        etSmsdetailSend.setText("");
    }


    /* **** resetLongClickFlag **** */
    private void resetLongClickFlag() {
        // the ori long click flag maybe true--> then set false
        isLongClick = false;// 1. reset this flag to set the selected logo gone
        adapter.isLongClick = isLongClick;// 2. and deliver this to the adapter flag 
        iv_delete.setVisibility(View.GONE);// 3. set deleted button gone
        etSmsdetailSend.setEnabled(!isLongClick);// 4. set et enable
        rvSmsdetailSend.setClickable(!isLongClick);// 5. set send button enable
        if (deletePop != null) {// 6.pop dismiss if had
            deletePop.getPop().dismiss();
        }
    }

    /* **** draftSms **** */
    private void draftSms() {
        // et have no text
        if (TextUtils.isEmpty(etSmsdetailSend.getText().toString())) {
            clearDraft(false);
        } else {
            clearDraft(true);
        }
    }

    /* **** clearDraft **** */
    public void clearDraft(boolean isSaveDraft) {
        // 1.first deleted the draft sms in router
        SmsDraftHelper sdfp = new SmsDraftHelper(this, smsContact.getContactId());
        sdfp.setOnNoSimListener(() -> {
            ToastUtil_m.show(this, getString(R.string.home_no_sim));
            finish();
        });
        sdfp.setOnClearDraftListener(() -> {
            if (isSaveDraft) {
                // 2.then save sms to router
                saveDraft();
            }
        });
        sdfp.clearDraft();
    }

    /* **** 保存草稿 **** */
    private void saveDraft() {
        String content = etSmsdetailSend.getText().toString();
        List<String> phoneNumber = smsContact.getPhoneNumber();
        SmsDraftHelper sdfp = new SmsDraftHelper(this, smsContact.getContactId());
        sdfp.setOnSaveDraftListener(() -> {
            ToastUtil_m.show(SmsDetailActivity.this, getString(R.string.sms_save_success));
            finish();
        });
        sdfp.saveDraftSms(phoneNumber, content);
    }

    /* 弹出删除窗口 */
    private void deleteSmsPop() {
        // show the delete pop
        deletePop = new SmsDeletePop(this) {
            @Override
            public void getView(View inflate) {
                tv_delete_cancel = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_cancel);
                tv_delete_confirm = (RippleView) inflate.findViewById(R.id.tv_smsdetail_detele_confirm);
                tv_delete_cancel.setOnClickListener(SmsDetailActivity.this);
                tv_delete_confirm.setOnClickListener(SmsDetailActivity.this);
            }
        };
    }

    /* 真正删除短信 */
    private void deleteSms() {
        // reset the long status
        resetLongClickFlag();
        SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_MORE_SMS, smsIds);
        RX.getInstant().deleteSMS(sdp, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                if (isLongClick) {
                    ToastUtil_m.show(SmsDetailActivity.this, getString(R.string.sms_delete_success));
                }
                resetLongClickFlag();
                getSmsContents(false);/* 删除短信后无需跳到最后一条 */
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(SmsDetailActivity.this, getString(R.string.sms_delete_error));
            }
        });
    }
}
