package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.DensityUtils;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSDelFlag;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSType;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.model.sms.SMSContentList;
import com.alcatel.smartlinkv3.model.sms.SMSContentNew;
import com.alcatel.smartlinkv3.model.sms.SMSContentParam;
import com.alcatel.smartlinkv3.model.sms.SMSDeleteParam;
import com.alcatel.smartlinkv3.model.sms.SMSSaveParam;
import com.alcatel.smartlinkv3.model.sms.SMSSendParam;
import com.alcatel.smartlinkv3.model.sms.SmsInitState;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.InquireDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.home.helper.cons.Cons;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.utils.ActionbarSetting;
import com.alcatel.smartlinkv3.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.RelativeLayout.ALIGN_LEFT;
import static android.widget.RelativeLayout.CENTER_VERTICAL;
import static android.widget.RelativeLayout.LEFT_OF;
import static com.alcatel.smartlinkv3.R.id.sms_sent_fail_left_iv;
import static com.alcatel.smartlinkv3.common.ENUM.EnumSMSType.Draft;
import static com.alcatel.smartlinkv3.common.ENUM.EnumSMSType.Read;
import static com.alcatel.smartlinkv3.common.ENUM.EnumSMSType.Report;
import static com.alcatel.smartlinkv3.common.ENUM.EnumSMSType.Unread;

public class ActivitySmsDetail extends BaseActivityWithBack implements OnClickListener, OnScrollListener, TextWatcher {
    // public static final int ONE_SMS_LENGTH = 160;
    public static final String INTENT_EXTRA_SMS_NUMBER = "sms_number";
    public static final String INTENT_EXTRA_CONTACT_ID = "contact_id";

    private Timer m_timer = new Timer();
    // private GetSMSContentTask m_getSMSContentTask = null;

    // private TextView m_tvTitle = null;
    private Button m_btnSend = null;
    // private Button m_btnDelete = null;
    // private LinearLayout m_btnBack = null;
    private EditText m_etContent = null;

    private ListView m_smsListView = null;
    // private ArrayList<SMSDetailItem> mSmsListData = new ArrayList<SMSDetailItem>();
    private List<SMSContentNew> mSmsListData = new ArrayList<>();
    private String m_smsNumber = new String();
    private int mContactId = 0;

    private ProgressBar m_progressWaiting = null;

    private boolean m_bDeleteContact = true;
    private boolean m_bIsLastOneMessage = false;

    private boolean m_bDeleteSingleEnable = true;
    private boolean m_bDeleteEnd = false;
    // private boolean m_bNeedFinish = false;
    private boolean m_bSendEnd = false;
    private SendStatus m_sendStatus = SendStatus.None;

    private boolean m_bFristGet = true;
    private TimerHelper timerHelper;
    
    private ImageButton ib_back;
    private TextView tv_title;
    private ImageView iv_delete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_details_view);
        getWindow().setBackgroundDrawable(null);
        // Edit Text
        m_etContent = (EditText) this.findViewById(R.id.ID_SMS_DETAIL_EDIT_CONTENT);
        m_etContent.addTextChangedListener(this);

        String text = getOneSmsLenth(new String()) + "/1";
        // Text View
        //m_tvTitle = (TextView) this.findViewById(R.id.ID_SMS_DETAIL_TITLE);
        // Button
        m_btnSend = (Button) this.findViewById(R.id.ID_SMS_DETAIL_BUTTON_SEND);
        m_btnSend.setOnClickListener(this);
        //m_btnBack = (LinearLayout) this.findViewById(R.id.back_layout);
        //m_btnBack.setOnClickListener(this);
        //m_btnDelete = (Button) findViewById(R.id.delete_button);
        //m_btnDelete.setOnClickListener(this);
        //
        m_progressWaiting = (ProgressBar) this.findViewById(R.id.sms_waiting_progress);

        m_smsNumber = this.getIntent().getStringExtra(INTENT_EXTRA_SMS_NUMBER);
        mContactId = this.getIntent().getIntExtra(INTENT_EXTRA_CONTACT_ID, 0);
        // m_tvTitle.setText(m_smsNumber);
        m_smsListView = (ListView) this.findViewById(R.id.sms_detail_list_view);
        SmsDetailListAdapter smsListAdapter = new SmsDetailListAdapter(this);
        m_smsListView.setAdapter(smsListAdapter);
        m_smsListView.setOnScrollListener(this);
        
        initActionbar();

    }

    /* **** initActionbar **** */
    private void initActionbar() {
        new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                ib_back = (ImageButton) view.findViewById(R.id.ib_smsdetail_back);
                ib_back.setOnClickListener(ActivitySmsDetail.this);
                
                tv_title = (TextView) view.findViewById(R.id.tv_smsdetail_title);
                tv_title.setText(m_smsNumber);
                
                iv_delete = (ImageView) view.findViewById(R.id.iv_smsdetail_delete);
                iv_delete.setOnClickListener(ActivitySmsDetail.this);
            }
        }.settingActionbarAttr(this,getSupportActionBar(),R.layout.actionbar_smsdetail);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();

        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                getSmsContents();
            }
        };
        timerHelper.start(5000);
    }


    
    /* -------------------------------------------- SMS API -------------------------------------------- */

    /* **** getSmsContents **** */
    private void getSmsContents() {
        API.get().getSmsInitState(new MySubscriber<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                if (result.getState() == Cons.SMS_COMPLETE) {
                    // get content list
                    SMSContentParam scp = new SMSContentParam(0, mContactId);
                    API.get().getSMSContentList(scp, new MySubscriber<SMSContentList>() {
                        @Override
                        protected void onSuccess(SMSContentList result) {
                            // transfer the sms content list
                            getSmsListData(result);
                            // m_btnDelete.setEnabled(true);
                            iv_delete.setEnabled(true);
                            if (m_bFristGet == true) {
                                m_bFristGet = false;
                                m_progressWaiting.setVisibility(View.GONE);
                            }
                            if (m_bDeleteEnd == true) {
                                m_bDeleteEnd = false;
                                m_progressWaiting.setVisibility(View.GONE);
                                m_etContent.setEnabled(true);
                                if (m_etContent.getText().toString() != null && m_etContent.getText().toString().length() > 0)
                                    m_btnSend.setEnabled(true);
                                else
                                    m_btnSend.setEnabled(false);
                                // m_btnDelete.setEnabled(true);
                                iv_delete.setEnabled(true);
                                m_bDeleteSingleEnable = true;
                            } else if (m_bSendEnd == true) {
                                m_bSendEnd = false;
                                m_progressWaiting.setVisibility(View.GONE);
                                m_etContent.setEnabled(true);
                                // m_btnDelete.setEnabled(true);
                                iv_delete.setEnabled(true);
                                m_bDeleteSingleEnable = true;
                                if (m_sendStatus == SendStatus.Success) {
                                    m_etContent.setText("");
                                    m_btnSend.setEnabled(false);
                                } else if (m_sendStatus == SendStatus.Fail || m_sendStatus == SendStatus.Fail_Memory_Full) {
                                    m_btnSend.setEnabled(true);
                                }
                            }

                        }
                    });
                }
            }
        });
    }
    
    
    /* -------------------------------------------- SMS API -------------------------------------------- */


    @Override
    public void onPause() {
        super.onPause();
        m_progressWaiting.setVisibility(View.GONE);

        m_etContent.setEnabled(true);
        // m_btnSend.setEnabled(true);
        m_bDeleteSingleEnable = true;
        // m_etContent.setText("");

        // m_bNeedFinish = false;
        m_bSendEnd = false;
        m_bDeleteEnd = false;

        // stopGetSmsContentTask();
    }

    //    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        //super.onBroadcastReceive(context, intent);

        // TOGO 2017/6/28 SAVE SMS
        if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_SAVE_SMS_REQUSET)) {
            // String msgRes = null;
            // if (ok) {
            //     msgRes = this.getString(R.string.sms_save_success);
            // } else if (response.getErrorCode().endsWith(ErrorCode.ERR_SAVE_SMS_SIM_IS_FULL)) {
            //     msgRes = this.getString(R.string.sms_error_message_full_storage);
            // } else {
            //     msgRes = this.getString(R.string.sms_save_error);
            // }
            // Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
            // this.finish();

            // TOGO 2017/6/28 SMS INIT
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)) {
            // if (ok) {
            //     startGetSmsContentTask();
            // }

            // TOGO 2017/6/28 SMS GET CONTENT
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET)) {
            // if (ok) {
            //     SmsContentMessagesModel smsContent = intent.getParcelableExtra(SMSManager.SMS_CONTENT_LIST_EXTRA);
            //     getSmsListData(smsContent);
            //     m_btnDelete.setEnabled(true);
            //     if (m_bFristGet == true) {
            //         m_bFristGet = false;
            //         m_progressWaiting.setVisibility(View.GONE);
            //     }
            // }
            // TOGO 2017/6/29 
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET)) {
            if (ok) {
                if (m_bDeleteEnd == true) {
                    m_bDeleteEnd = false;
                    m_progressWaiting.setVisibility(View.GONE);
                    m_etContent.setEnabled(true);
                    if (m_etContent.getText().toString() != null && m_etContent.getText().toString().length() > 0)
                        m_btnSend.setEnabled(true);
                    else
                        m_btnSend.setEnabled(false);
                    // m_btnDelete.setEnabled(true);
                    iv_delete.setEnabled(true);
                    m_bDeleteSingleEnable = true;
                } else if (m_bSendEnd == true) {
                    m_bSendEnd = false;
                    m_progressWaiting.setVisibility(View.GONE);
                    m_etContent.setEnabled(true);
                    // m_btnDelete.setEnabled(true);
                    iv_delete.setEnabled(true);
                    m_bDeleteSingleEnable = true;
                    if (m_sendStatus == SendStatus.Success) {
                        m_etContent.setText("");
                        m_btnSend.setEnabled(false);
                    } else if (m_sendStatus == SendStatus.Fail || m_sendStatus == SendStatus.Fail_Memory_Full) {
                        m_btnSend.setEnabled(true);
                    }
                }
            }
        }

        // TOGO 2017/6/28 SMS_DELETE_SMS_REQUSET
        if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_DELETE_SMS_REQUSET)) {
            m_bDeleteEnd = true;
            boolean bDeleteSeccuss = false;
            if (ok) {
                bDeleteSeccuss = true;
            }

            if (m_bDeleteContact) {
                if (bDeleteSeccuss) {
                    BusinessManager.getInstance().getContactMessagesAtOnceRequest();
                    String msgRes = this.getString(R.string.sms_delete_multi_success);
                    //Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                    this.finish();
                } else {
                    String strMsg = getString(R.string.sms_delete_multi_error);
                    Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
                    m_progressWaiting.setVisibility(View.GONE);
                    BusinessManager.getInstance().getContactMessagesAtOnceRequest();
                    // getSmsContentAtOnceRequest();
                }
            } else {
                m_progressWaiting.setVisibility(View.GONE);
                if (bDeleteSeccuss) {
                    Toast.makeText(this, getString(R.string.sms_delete_success), Toast.LENGTH_SHORT).show();
                    BusinessManager.getInstance().getContactMessagesAtOnceRequest();
                    if (m_bIsLastOneMessage) {
                        this.finish();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.sms_delete_error), Toast.LENGTH_SHORT).show();
                }
                // getSmsContentAtOnceRequest();
            }

            // TOAT: SMS_SEND_SMS_REQUSET
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_SEND_SMS_REQUSET)) {
            if (ok) {
                BusinessManager.getInstance().getContactMessagesAtOnceRequest();
                // getSmsContentAtOnceRequest();
            } else if (response.getErrorCode().endsWith(ErrorCode.ERR_SMS_SIM_IS_FULL)) {
                String msgRes = this.getString(R.string.sms_error_message_full_storage);
                // Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                ToastUtil.showMessage(this, msgRes, Toast.LENGTH_SHORT);
                m_progressWaiting.setVisibility(View.GONE);
                // if (m_progress_dialog != null &&
                // m_progress_dialog.isShowing()) {
                // m_progress_dialog.dismiss();
                // }
                m_etContent.setEnabled(true);
                m_btnSend.setEnabled(true);
                // m_btnDelete.setEnabled(true);
                iv_delete.setEnabled(true);
                m_bDeleteSingleEnable = true;
                m_bSendEnd = true;
                m_sendStatus = SendStatus.Fail_Memory_Full;
                // getSmsContentAtOnceRequest();
            } else {
                String msgRes = this.getString(R.string.sms_error_send_error);
                Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                m_progressWaiting.setVisibility(View.GONE);
                // if (m_progress_dialog != null &&
                // m_progress_dialog.isShowing()) {
                // m_progress_dialog.dismiss();
                // }
                m_etContent.setEnabled(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(m_etContent.getWindowToken(), 0);
                m_btnSend.setEnabled(true);
                // m_btnDelete.setEnabled(true);
                iv_delete.setEnabled(true);
                m_bDeleteSingleEnable = true;
                m_bSendEnd = true;
                m_sendStatus = SendStatus.Fail;
                // getSmsContentAtOnceRequest();
            }
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SEND_STATUS_REQUSET)) {
            if (ok) {
                int nSendReslt = intent.getIntExtra(Const.SMS_SNED_STATUS, 0);
                SendStatus sendStatus = SendStatus.build(nSendReslt);
                m_sendStatus = sendStatus;
                boolean bEnd = false;
                if (sendStatus == SendStatus.Fail) {
                    String msgRes = this.getString(R.string.sms_error_send_error);
                    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                    bEnd = true;
                }
                if (sendStatus == SendStatus.Fail_Memory_Full) {
                    String msgRes = this.getString(R.string.sms_error_message_full_storage);
                    // Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                    ToastUtil.showMessage(this, msgRes, Toast.LENGTH_SHORT);
                    bEnd = true;
                }
                if (sendStatus == SendStatus.Success) {
                    String msgRes = this.getString(R.string.sms_send_success);
                    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                    bEnd = true;
                }

                if (bEnd == true) {
                    m_bSendEnd = true;
                    // getSmsContentAtOnceRequest();
                }
            } else {
                m_progressWaiting.setVisibility(View.GONE);
                // if (m_progress_dialog != null &&
                // m_progress_dialog.isShowing()) {
                // m_progress_dialog.dismiss();
                // }
                m_etContent.setEnabled(true);
                m_btnSend.setEnabled(true);
                // m_btnDelete.setEnabled(true);
                iv_delete.setEnabled(true);
                m_bDeleteSingleEnable = true;
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                onBackPressed();
                break;

            case R.id.ID_SMS_DETAIL_BUTTON_SEND:
                OnBtnSend();
                break;

            case R.id.delete_button:
                OnBtnDelete();
                break;
            
            case R.id.ib_smsdetail_back:
                onBackPressed();
                break;
            
            case R.id.iv_smsdetail_delete:
                OnBtnDelete();
                break;
            
            
            default:
                break;
        }
    }

    // TOGO 2017/6/28 
    @Override
    public void onBackPressed() {
        OnBtnBack();
        super.onBackPressed();
    }

    // TOGO 2017/6/28 
    private void OnBtnBack() {
        // BusinessManager.getInstance().getContactMessagesAtOnceRequest();
        String et_content = m_etContent.getText().toString();
        String strNumber = m_smsNumber;
        if (et_content != null)
            et_content = et_content.trim();
        // **** if edittext have content input ****
        if (et_content != null && et_content.length() > 0 && strNumber != null && strNumber.length() > 0) {
            SMSSaveParam ssp = new SMSSaveParam();
            ssp.setSMSContent(et_content);
            List<String> phonenums = new ArrayList<>();
            phonenums.add(strNumber);
            ssp.setPhoneNumber(phonenums);

            SMSContentNew smsContentNew = mSmsListData.get(mSmsListData.size() - 1);
            if (null != smsContentNew && smsContentNew.getScsb().getSMSType() == Cons.DRAFT) {
                ssp.setSMSId(smsContentNew.getScsb().getSMSId());
            }
            // TOAT: SAVE SMS
            API.get().saveSMS(ssp, new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_save_success));
                }

                @Override
                protected void onFailure() {
                    super.onFailure();
                    ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_save_error));
                }
            });
            // **** if no content ****
        } else {
            this.finish();
            if (mSmsListData.size() == 0)
                return;
            SMSContentNew smsContentNew = mSmsListData.get(mSmsListData.size() - 1);
            if (null != smsContentNew && smsContentNew.getScsb().getSMSType() == Cons.DRAFT) {

                List<Integer> smsIds = new ArrayList<>();
                smsIds.add(smsContentNew.getScsb().getSMSId());
                SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_SESSION_SMS, smsIds);
                deleteSms(sdp);// deleted sms
            }
        }
    }

    /* **** deleted sms **** */
    private void deleteSms(SMSDeleteParam sdp) {
        API.get().deleteSMS(sdp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                // TOAT: 2017/6/28 DELETE 
                m_bDeleteEnd = true;
                m_progressWaiting.setVisibility(View.GONE);
                if (m_bDeleteContact) {
                    ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_delete_multi_success));
                    ActivitySmsDetail.this.finish();
                } else {
                    ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_delete_success));
                    if (m_bIsLastOneMessage) {
                        ActivitySmsDetail.this.finish();
                    }
                }
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                m_progressWaiting.setVisibility(View.GONE);
                ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_delete_multi_error));
            }
        });
    }

    // TOGO 2017/6/28 
    private void OnBtnDelete() {
        final InquireDialog inquireDlg = new InquireDialog(ActivitySmsDetail.this);
        inquireDlg.m_titleTextView.setText(R.string.dialog_delete_title);
        inquireDlg.m_contentTextView.setText(R.string.dialog_delete_content);
        inquireDlg.m_contentDescriptionTextView.setText(R.string.dialog_delete_content_description);
        inquireDlg.m_confirmBtn.setText(R.string.delete);
        inquireDlg.showDialog(new OnInquireApply() {
            @Override
            public void onInquireApply() {

                m_bDeleteContact = true;
                m_progressWaiting.setVisibility(View.VISIBLE);
                m_etContent.setEnabled(false);
                m_btnSend.setEnabled(false);
                // m_btnDelete.setEnabled(false);
                iv_delete.setEnabled(true);
                m_bDeleteSingleEnable = false;

                boolean bHaveSms = false;
                for (int i = 0; i < mSmsListData.size(); i++) {
                    // SMSDetailItem item = mSmsListData.get(i);
                    SMSContentNew item = mSmsListData.get(i);
                    if (item.isDateItem == false) {
                        bHaveSms = true;
                        break;
                    }
                }

                if (bHaveSms == true) {

                    List<Integer> smsIds = new ArrayList<>();
                    for (SMSContentNew smsContentNew : mSmsListData) {
                        smsIds.add(smsContentNew.getScsb().getSMSId());
                    }
                    SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_ONE_SMS, smsIds);
                    deleteSms(sdp);// deleted sms

                    // DataValue data = new DataValue();
                    // data.addParam("DelFlag", EnumSMSDelFlag.Delete_contact_messages);
                    // data.addParam("ContactId", mContactId);
                    // BusinessManager.getInstance().sendRequestMessage(MessageUti.SMS_DELETE_SMS_REQUSET, data);
                }
                inquireDlg.closeDialog();
            }
        });
    }

    // TOGO 2017/6/28 
    private int getDeleteMessagesNumber() {
        int nNumber = 0;
        for (int i = 0; i < mSmsListData.size(); i++) {
            // SMSDetailItem item = mSmsListData.get(i);
            SMSContentNew smsContentNew = mSmsListData.get(i);
            if (smsContentNew.isDateItem == false)
                nNumber++;
        }
        return nNumber;
    }

    // TOAT: 
    private void OnBtnSend() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_etContent.getWindowToken(), 0);

        // DataValue data = new DataValue();
        // data.addParam("content", m_etContent.getText().toString());
        // data.addParam("phone_number", m_tvTitle.getText().toString());
        // BusinessManager.getInstance().sendRequestMessage(MessageUti.SMS_SEND_SMS_REQUSET, data);

        // String phonenum = m_tvTitle.getText().toString();
        String phonenum = tv_title.getText().toString();
        List<String> phonenums = new ArrayList<>();
        phonenums.add(phonenum);

        int smsid = -1;// TODO: 2017/6/28 发送短信时候是否为-1 
        String smsTime = DataUtils.getCurrent();
        String content = m_etContent.getText().toString();

        SMSSendParam sssp = new SMSSendParam(smsid, content, smsTime, phonenums);
        API.get().sendSMS(sssp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            protected void onFailure() {
                super.onFailure();
                ToastUtil_m.show(ActivitySmsDetail.this, getString(R.string.sms_error_send_error));
                m_progressWaiting.setVisibility(View.GONE);
                // set some flag
                m_etContent.setEnabled(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(m_etContent.getWindowToken(), 0);
                m_btnSend.setEnabled(true);
                // m_btnDelete.setEnabled(true);
                iv_delete.setEnabled(true);
                m_bDeleteSingleEnable = true;
                m_bSendEnd = true;
                // TOGO 2017/6/28 
                m_sendStatus = SendStatus.Fail;
            }
        });

        m_progressWaiting.setVisibility(View.VISIBLE);

        m_btnSend.setEnabled(false);
        m_etContent.setEnabled(false);
        // m_btnDelete.setEnabled(false);
        iv_delete.setEnabled(true);
        m_bDeleteSingleEnable = false;
    }

    private void showNewSmsCnt(CharSequence s) {

    }

    public static int getOneSmsLenth(String strSmsContent) {
        if (null == strSmsContent)
            return 160;

        if (strSmsContent.length() < strSmsContent.getBytes().length) {
            return 67;
        } else {
            return 160;
        }

    }

    // TOGO 2017/6/28 
    public void ShowErrorMsg(String strTitle, String strMsg) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String strInfo = s == null ? null : s.toString();
        if (s != null && strInfo.length() > 0) {
            m_btnSend.setEnabled(true);

        } else {
            m_btnSend.setEnabled(false);
        }

        int sms[] = SmsMessage.calculateLength(s, false);
        int msgCount = sms[0];
        int codeUnitCount = sms[1];
        int codeUnitsRemaining = sms[2];
        int codeUnitSize = sms[3];

        if (msgCount > 10) {
            int nMax = 670;
            if (codeUnitSize == 1) {
                nMax = 1530;
            }
            int selEndIndex = Selection.getSelectionEnd(s);
            String newStr = strInfo.substring(0, nMax);
            m_etContent.setText(newStr);

            Editable editable = m_etContent.getText();
            int newLen = editable.length();

            if (selEndIndex > newLen) {
                selEndIndex = editable.length();
            }

            Selection.setSelection(editable, selEndIndex);
        }

    }

    // TOGO 2017/6/28 
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    // TOGO 2017/6/28 
    @Override
    public void afterTextChanged(Editable s) {
    }

    // TOGO 2017/6/28 
    private void getSmsListData(SMSContentList result) {
        // 排序
        List<SMSContentList.SMSContentBean> smsContentList = result.getSMSContentList();
        Collections.sort(smsContentList, new SortSmsListByTime());
        mSmsListData.clear();


        for (int i = 0; i < smsContentList.size(); i++) {
            SMSContentList.SMSContentBean smsContentBean = smsContentList.get(i);
            // 处理日期格式
            Date smsDate = DataUti.formatDateFromString(smsContentBean.getSMSTime());

            if (i == 0) {
                // SMSDetailItem item = new SMSDetailItem();
                SMSContentNew smsContentNew = new SMSContentNew();
                smsContentNew.scsb = smsContentBean;
                smsContentNew.isDateItem = true;
                
                if (smsDate != null) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    smsContentNew.scsb.setSMSTime(format.format(smsDate));
                }

                mSmsListData.add(smsContentNew);
            } else {
                Date preSmsDate = DataUti.formatDateFromString(smsContentList.get(i - 1).getSMSTime());
                if (!(smsDate.getYear() == preSmsDate.getYear() && smsDate.getMonth() == preSmsDate.getMonth() && smsDate.getDate() == preSmsDate.getDate())) {
                    // SMSDetailItem item = new SMSDetailItem();
                    SMSContentNew smsContentNew = new SMSContentNew();
                    smsContentNew.isDateItem = true;
                    if (smsDate != null) {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        smsContentNew.getScsb().setSMSTime(format.format(smsDate));
                    }

                    mSmsListData.add(smsContentNew);
                }
            }

            // SMSDetailItem item = new SMSDetailItem();
            SMSContentNew smsContentNew = new SMSContentNew();
            smsContentNew.setDateItem(false);
            smsContentNew.setContactId(mContactId);
            smsContentNew.setScsb(smsContentBean);
            mSmsListData.add(smsContentNew);
        }

        // 草稿UI
        DarftSMSDisplay();
        // TODO: 2017/6/28 更新适配器 
        ((SmsDetailListAdapter) m_smsListView.getAdapter()).notifyDataSetChanged();
    }

    // TOGO 2017/6/28 根据短信时间排序
    private class SortSmsListByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            SMSContentList.SMSContentBean c1 = (SMSContentList.SMSContentBean) o1;
            SMSContentList.SMSContentBean c2 = (SMSContentList.SMSContentBean) o2;
            Date d1 = DataUti.formatDateFromString(c1.getSMSTime());
            Date d2 = DataUti.formatDateFromString(c2.getSMSTime());
            if (d1.after(d2) == true)
                return 1;
            if (d1.equals(d2) == true)
                return 0;
            return -1;
        }
    }

    // TOGO 2017/6/28 
    private void DarftSMSDisplay() {
        if (mSmsListData.size() == 0)
            return;
        // SMSDetailItem item = mSmsListData.get(mSmsListData.size() - 1);
        SMSContentNew smsContentNew = mSmsListData.get(mSmsListData.size() - 1);
        if (smsContentNew.getScsb().getSMSType() == Cons.DRAFT && !m_etContent.isFocused()) {
            m_etContent.setEnabled(true);
            m_etContent.setText(smsContentNew.getScsb().getSMSContent());
        }
    }

    /* -------------------------------------------- Adapter -------------------------------------------- */
    private class SmsDetailListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public SmsDetailListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mSmsListData.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public final class ViewHolder {
            public FrameLayout dateLayout;
            public TextView date;
            public RelativeLayout smsContentLayout;
            public TextView smsContent;
            public TextView smsDate;
            public ImageView sentFail;
            public ImageView sentFailLeft;
            public TextView sendFailText;
            public View placeHolder;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ActivitySmsDetail.this).inflate(R.layout.sms_detail_item, null);
                holder.dateLayout = (FrameLayout) convertView.findViewById(R.id.sms_detail_date_layout);
                holder.date = (TextView) convertView.findViewById(R.id.sms_detail_date_textview);
                holder.smsContentLayout = (RelativeLayout) convertView.findViewById(R.id.sms_detail_content_layout);
                holder.smsDate = (TextView) convertView.findViewById(R.id.sms_detail_date);
                holder.smsContent = (TextView) convertView.findViewById(R.id.sms_detail_content);
                holder.sentFail = (ImageView) convertView.findViewById(R.id.sms_sent_fail_image);
                holder.sentFailLeft = (ImageView) convertView.findViewById(sms_sent_fail_left_iv);
                holder.sendFailText = (TextView) convertView.findViewById(R.id.sms_sent_fail_text);
                holder.placeHolder = (View) convertView.findViewById(R.id.place_holder);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == mSmsListData.size() - 1) {
                holder.placeHolder.setVisibility(View.VISIBLE);
            } else {
                holder.placeHolder.setVisibility(View.GONE);
            }

            SMSContentNew smsContentNew = mSmsListData.get(position);

            if (smsContentNew.isDateItem == true) {
                holder.date.setText(smsContentNew.getScsb().getSMSTime());
                holder.dateLayout.setVisibility(View.VISIBLE);
                holder.smsContentLayout.setVisibility(View.GONE);

                holder.sentFailLeft.setVisibility(View.GONE);
            } else {
                holder.dateLayout.setVisibility(View.GONE);
                holder.smsContentLayout.setVisibility(View.VISIBLE);
                holder.smsDate.setText(smsContentNew.getScsb().getSMSTime());

                holder.smsContent.setText(mSmsListData.get(position).getScsb().getSMSContent());
                holder.smsContentLayout.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (m_bDeleteSingleEnable == false)
                            return true;
                        final InquireDialog inquireDlg = new InquireDialog(ActivitySmsDetail.this);
                        inquireDlg.m_titleTextView.setText(R.string.dialog_delete_title);
                        inquireDlg.m_contentTextView.setText(R.string.dialog_delete_single_sms_content);
                        inquireDlg.m_contentDescriptionTextView.setText("");
                        inquireDlg.m_confirmBtn.setText(R.string.delete);
                        inquireDlg.showDialog(new OnInquireApply() {
                            @Override
                            public void onInquireApply() {
                                m_bDeleteContact = false;
                                m_progressWaiting.setVisibility(View.VISIBLE);
                                m_etContent.setEnabled(false);
                                m_btnSend.setEnabled(false);
                                // m_btnDelete.setEnabled(false);
                                iv_delete.setEnabled(true);
                                m_bDeleteSingleEnable = false;
                                if (getDeleteMessagesNumber() == 1) {
                                    m_bIsLastOneMessage = true;
                                } else {
                                    m_bIsLastOneMessage = false;
                                }


                                List<Integer> smsIds = new ArrayList<>();
                                smsIds.add(mSmsListData.get(position).getScsb().getSMSId());
                                SMSDeleteParam sdp = new SMSDeleteParam(Cons.DELETE_SESSION_SMS, smsIds);
                                deleteSms(sdp);// deleted sms
                                inquireDlg.closeDialog();
                            }
                        });
                        return true;
                    }

                });

                LayoutParams contentLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                final int type = mSmsListData.get(position).getScsb().getSMSType();
                int nContentLayoutBg = R.drawable.selector_sms_detail_receive;
                holder.smsContentLayout.setVisibility(View.VISIBLE);
                switch (type) {
                    case Cons.REPORT:
                        holder.smsContent.setText(R.string.sms_report_message);
                        holder.smsContentLayout.setLayoutParams(contentLayout);
                        holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);
                    case Cons.READ:
                    case Cons.UNREAD:
                        contentLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.sms_layout);
                        nContentLayoutBg = R.drawable.selector_sms_detail_receive;
                        contentLayout.setMargins(30, 10, 80, 10);
                        holder.smsContent.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_black));

                        holder.smsDate.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_grey));
                        holder.sendFailText.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_grey));
                        holder.sentFail.setImageResource(R.drawable.warning_blue_bg);
                        holder.smsContentLayout.setLayoutParams(contentLayout);
                        holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);
                        holder.smsContentLayout.setPadding(70, 0, 0, 20);
                        break;
                    case Cons.DRAFT:
                        if (position == mSmsListData.size() - 1) {
                            holder.smsContentLayout.setVisibility(View.GONE);
                        }
                        holder.smsContentLayout.setLayoutParams(contentLayout);
                        holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);
                    default:
                        contentLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.sms_layout);
                        nContentLayoutBg = R.drawable.selector_sms_detail_out;
                        contentLayout.setMargins(20, 10, 30, 10);
                        holder.smsContent.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_white));
                        holder.smsDate.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_sms_detail_send_grey));
                        holder.sendFailText.setTextColor(ActivitySmsDetail.this.getResources().getColor(R.color.color_sms_detail_send_grey));
                        holder.sentFail.setImageResource(R.drawable.sms_failed_white_bg);

                        int leftFailWidthParams = DensityUtils.dip2px(ActivitySmsDetail.this, 25f);
                        LayoutParams sentFailLeftParams = new LayoutParams(leftFailWidthParams, leftFailWidthParams);
                        if (holder.smsContent.getLineCount() > 1 || holder.smsContent.getText().toString().length() >= 12) {//POP 4+型号时15是极限
                            //多行或者当行过长时
                            int leftFailMarginParams = DensityUtils.dip2px(ActivitySmsDetail.this, 10f);
                            sentFailLeftParams.setMargins(leftFailMarginParams, 0, 0, 0);
                            sentFailLeftParams.addRule(ALIGN_LEFT);
                            contentLayout.addRule(RelativeLayout.RIGHT_OF, R.id.sms_sent_fail_left_iv);
                        } else {
                            //单行够短时
                            sentFailLeftParams.addRule(LEFT_OF, R.id.sms_detail_content_layout);
                        }
                        sentFailLeftParams.addRule(CENTER_VERTICAL);
                        holder.sentFailLeft.setLayoutParams(sentFailLeftParams);

                        holder.smsContentLayout.setLayoutParams(contentLayout);
                        holder.smsContentLayout.setBackgroundResource(nContentLayoutBg);
                        holder.smsContentLayout.setPadding(20, 0, 70, 20);
                        break;
                }

                if (type == Cons.SENT_FAILED) {
                    holder.sentFail.setVisibility(View.GONE);
                    holder.sendFailText.setVisibility(View.GONE);

                    holder.sentFailLeft.setVisibility(View.VISIBLE);
                } else {
                    holder.sentFail.setVisibility(View.GONE);
                    holder.sendFailText.setVisibility(View.GONE);

                    holder.sentFailLeft.setVisibility(View.GONE);
                }

                holder.smsContentLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == Cons.SENT_FAILED) {
                            FailedSMSClick(mSmsListData.get(position));
                        }
                    }
                });
            }

            return convertView;
        }
    }

    // TOGO 2017/6/28 
    private void FailedSMSClick(SMSContentNew item) {
        m_etContent.setEnabled(true);
        m_etContent.setText(item.getScsb().getSMSContent());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (visibleItemCount <= totalItemCount) {
            m_smsListView.setStackFromBottom(true);
        } else {
            m_smsListView.setStackFromBottom(false);
        }
    }

}
