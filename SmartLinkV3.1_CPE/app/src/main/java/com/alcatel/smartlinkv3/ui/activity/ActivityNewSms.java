package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.model.sms.SMSSaveParam;
import com.alcatel.smartlinkv3.model.sms.SMSSendParam;
import com.alcatel.smartlinkv3.model.sms.SendSMSResult;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;
import com.alcatel.smartlinkv3.utils.DataUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityNewSms extends BaseActivity implements OnClickListener {
    private TextView m_btnCancel = null;
    private LinearLayout m_btnBack = null;
    private Button m_btnSend = null;
    private EditText m_etNumber = null;
    private EditText m_etContent = null;
    private static final String NUMBER_REG_Ex = "^([+*#\\d;]){1}(\\d|[;*#]){0,}$";
    private String m_preMatchNumber = new String();

    private ProgressBar m_progressWaiting = null;

    private SendStatus m_sendStatus = SendStatus.None;
    private boolean m_bSendEnd = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_new_view);
        getWindow().setBackgroundDrawable(null);
        // get controls
        m_btnCancel = (TextView) findViewById(R.id.cancel);
        m_btnCancel.setOnClickListener(this);
        m_btnBack = (LinearLayout) findViewById(R.id.back_layout);
        m_btnBack.setOnClickListener(this);
        m_btnSend = (Button) findViewById(R.id.send_btn);
        m_btnSend.setEnabled(false);
        m_btnSend.setOnClickListener(this);
        m_etNumber = (EditText) findViewById(R.id.edit_number);
        m_etNumber.setText("");
        m_etContent = (EditText) findViewById(R.id.edit_content);
        m_etContent.setText("");
        setEditTextChangedListener();

        String text = ActivitySmsDetail.getOneSmsLenth(new String()) + "/1";

        m_progressWaiting = (ProgressBar) this.findViewById(R.id.sms_new_waiting_progress);
    }

    private void setEditTextChangedListener() {
        m_etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String editable = m_etNumber.getText().toString();
                String str = stringFilter(editable.toString());
                if (!editable.equals(str)) {
                    m_etNumber.setText(str);
                    //set cursor position
                    m_etNumber.setSelection(start);
                }

                String strNumber = str;
                String strContent = m_etContent.getText().toString();
                if (strNumber != null && strContent != null && strNumber.length() != 0 && strContent.length() != 0) {
                    m_btnSend.setEnabled(true);
                } else {
                    m_btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        m_etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String strNumber = m_etNumber.getText() == null ? null : m_etNumber.getText().toString();
                String strContent = s == null ? null : s.toString();
                if (strNumber != null && strContent != null && strNumber.length() != 0 && strContent.length() != 0) {
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
                    String newStr = strContent.substring(0, nMax);
                    m_etContent.setText(newStr);

                    Editable editable = m_etContent.getText();
                    int newLen = editable.length();

                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }

                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String stringFilter(String str) {
        Pattern p = Pattern.compile(NUMBER_REG_Ex);
        Matcher m = p.matcher(str);
        //return   m.replaceAll("").trim();
        if ((m.matches() || str == null || str.length() == 0) && checkAllNumber(str)) {
            if (str == null)
                str = new String();

            m_preMatchNumber = str;
        }

        return m_preMatchNumber;
    }

    private boolean checkAllNumber(String strNumber) {
        ArrayList<String> numberLst = getNumberFromString(strNumber);
        for (int i = 0; i < numberLst.size(); i++) {
            if (numberLst.get(i).length() > 20) {
                return false;
            }
        }

        return true;
    }
//
//    @Override
//    protected void onBroadcastReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
//        Boolean ok = response != null && response.isOk();
//        super.onBroadcastReceive(context, intent);
//
//        if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_SAVE_SMS_REQUSET)) {
//            String msgRes;
//            if (ok) {
//                msgRes = this.getString(R.string.sms_save_success);
//            } else if (response != null && response.getErrorCode().endsWith(ErrorCode.ERR_SAVE_SMS_SIM_IS_FULL)) {
//                msgRes = this.getString(R.string.sms_error_message_full_storage);
//            } else {
//                msgRes = this.getString(R.string.sms_save_error);
//            }
//            Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//            this.finish();
//        }
//
//        if (MessageUti.SMS_SEND_SMS_REQUSET.equals(action)) {
//            if (ok) {
//                //BusinessManager.getInstance().refreshSmsListAtOnce();
//            } else if (response != null && response.getErrorCode().endsWith(ErrorCode.ERR_SMS_SIM_IS_FULL)) {
//                String msgRes = this.getString(R.string.sms_error_message_full_storage);
//                Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//                resetUI();
//                this.finish();
//            } else {
//                String msgRes = this.getString(R.string.sms_error_send_error);
//                Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//                resetUI();
//            }
//        }
//
//        if (MessageUti.SMS_GET_SEND_STATUS_REQUSET.equals(action)) {
//            if (ok) {
//                int nSendReslt = intent.getIntExtra(Const.SMS_SNED_STATUS, 0);
//                SendStatus sendStatus = SendStatus.build(nSendReslt);
//                m_sendStatus = sendStatus;
//                boolean bEnd = false;
//                if (sendStatus == SendStatus.Fail) {
//                    String msgRes = this.getString(R.string.sms_error_send_error);
//                    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//                    bEnd = true;
//                }
//                if (sendStatus == SendStatus.Fail_Memory_Full) {
//                    String msgRes = this.getString(R.string.sms_error_message_full_storage);
//                    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//                    bEnd = true;
//                    this.finish();
//                }
//                if (sendStatus == SendStatus.Success) {
//                    String msgRes = this.getString(R.string.sms_send_success);
//                    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
//                    this.finish();
//                    bEnd = true;
//                }
//
//                if (bEnd) {
//                    m_bSendEnd = true;
//                    //BusinessManager.getInstance().refreshSmsListAtOnce();
//
//                    resetUI();
//                }
//            } else {
//                resetUI();
//            }
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
//        this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_SEND_SMS_REQUSET));
//        this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_GET_SEND_STATUS_REQUSET));
//        this.registerReceiver(this.m_msgReceiver, new IntentFilter(MessageUti.SMS_SAVE_SMS_REQUSET));
    }

    @Override
    public void onPause() {
        super.onPause();
        m_progressWaiting.setVisibility(View.GONE);
        if (m_etNumber.getText().toString() != null && m_etNumber.getText().toString().length() != 0 &&
                m_etContent.getText().toString() != null && m_etContent.getText().toString().length() != 0)
            m_btnSend.setEnabled(true);
        m_etNumber.setEnabled(true);
        m_etContent.setEnabled(true);
        //m_etNumber.setText("");
        //m_etContent.setText("");

        m_bSendEnd = false;
    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.cancel:
            case R.id.back_layout:
                OnBtnCancel();
                break;

            case R.id.send_btn:
                OnBtnSend();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        OnBtnCancel();
    }

    //
    private void OnBtnCancel() {
        String strContent = m_etContent.getText().toString();
        String strNumber = m_etNumber.getText().toString();
        if (strContent != null)
            strContent = strContent.trim();
        if (strContent != null && strContent.length() > 0 && strNumber != null && strNumber.length() > 0) {
            if (checkNumbers() == true) {
//                DataValue data = new DataValue();
//                data.addParam("SMSId", -1);
//                data.addParam("Content", strContent);
//                data.addParam("Number", strNumber);
//                BusinessManager.getInstance().sendRequestMessage(MessageUti.SMS_SAVE_SMS_REQUSET, data);
//                BusinessManager.getInstance().getContactMessagesAtOnceRequest();

                String num = m_etNumber.getText().toString();
                ArrayList<String> numList = new ArrayList<>();
                numList.add(num);
                API.get().saveSMS(new SMSSaveParam(-1, m_etContent.getText().toString(), DataUtils.getCurrent(), numList), new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        ToastUtil.showMessage(ActivityNewSms.this, R.string.sms_save_success);
                        finish();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        super.onResultError(error);
                        //060801: Save SMS failed. 060802: Fail with store space full.
                        if (error.getCode().equals("060801")) {
                            ToastUtil.showMessage(ActivityNewSms.this, R.string.sms_save_error);

                        } else if (error.getCode().equals("060802")) {
                            ToastUtil.showMessage(ActivityNewSms.this, R.string.sms_error_message_full_storage);
                        }
                        finish();
                    }

                    @Override
                    protected void onFailure() {
                        super.onFailure();
                        finish();
                    }
                });


            } else {
                String msgRes = this.getString(R.string.sms_number_invalid);
                Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
                m_etNumber.requestFocus();
            }
        } else {
            this.finish();
        }
        //this.finish();
    }

    //
    private void OnBtnSend() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (checkNumbers() == true) {
//            DataValue data = new DataValue();
//            data.addParam("content", m_etContent.getText().toString());
//            data.addParam("phone_number", m_etNumber.getText().toString());
//            BusinessManager.getInstance().sendRequestMessage(MessageUti.SMS_SEND_SMS_REQUSET, data);

            String num = m_etNumber.getText().toString();
            ArrayList<String> numList = new ArrayList<>();
            numList.add(num);
            API.get().sendSMS(new SMSSendParam(-1, m_etContent.getText().toString(), DataUtils.getCurrent(), numList), new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    getSendSMSResult();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    super.onResultError(error);
                    //060601: Send SMS failed. 060602: Fail still sending last message. 060603: Fail with store space full.
                    resetUI();
                    switch (error.getCode()) {
                        case "060601":
                            ToastUtil.showMessage(ActivityNewSms.this, R.string.send_sms_failed);
                            break;
                        case "060602":
                            ToastUtil.showMessage(ActivityNewSms.this, R.string.fail_still_sending_last_message);
                            break;
                        case "060603":
                            ToastUtil.showMessage(ActivityNewSms.this, R.string.fail_with_store_space_full);
                            break;
                        default:
                            break;

                    }
                }

                @Override
                protected void onFailure() {
                    super.onFailure();
                    resetUI();
                }

            });

            m_progressWaiting.setVisibility(View.VISIBLE);
            m_btnSend.setEnabled(false);
            m_etNumber.setEnabled(false);
            m_etContent.setEnabled(false);
        } else {

            String msgRes = this.getString(R.string.sms_number_invalid);
            Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
            m_etNumber.requestFocus();
        }
    }

    private void resetUI() {
        m_progressWaiting.setVisibility(View.GONE);
        m_btnSend.setEnabled(true);
        m_etNumber.setEnabled(true);
        m_etContent.setEnabled(true);
    }

    private void getSendSMSResult() {
        API.get().GetSendSMSResult(new MySubscriber<SendSMSResult>() {
            @Override
            protected void onSuccess(SendSMSResult result) {
                //0 : none 1 : sending 2 : success 3: fail still sending last message 4 : fail with Memory full 5: fail
                resetUI();
                int sendStatus = result.getSendStatus();
                if (sendStatus == 0) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil.showMessage(ActivityNewSms.this, R.string.none);
                } else if (sendStatus == 1) {
                    getSendSMSResult();
                } else if (sendStatus == 2) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil.showMessage(ActivityNewSms.this, R.string.succeed);
                } else if (sendStatus == 3) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil.showMessage(ActivityNewSms.this, R.string.fail_still_sending_last_message);
                } else if (sendStatus == 4) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil.showMessage(ActivityNewSms.this, R.string.fail_with_memory_full);
                } else if (sendStatus == 5) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil.showMessage(ActivityNewSms.this, R.string.fail);
                }

            }

            @Override
            protected void onFailure() {
                super.onFailure();
                resetUI();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                resetUI();
            }
        });
    }

    private boolean checkNumbers() {
        String strNumber = m_etNumber.getText().toString();
        ArrayList<String> numberLst = getNumberFromString(strNumber);
        if (numberLst.size() > 3) {
            return false;
        }
        return true;
    }

    private ArrayList<String> getNumberFromString(String number) {
        if (number == null)
            number = new String();
        String[] listNumbers = number.split(";");
        ArrayList<String> phoneNumberLst = new ArrayList<String>();
        for (int i = 0; i < listNumbers.length; i++) {
            if (null == listNumbers[i] || listNumbers[i].length() == 0)
                continue;
            phoneNumberLst.add(listNumbers[i]);
        }

        return phoneNumberLst;
    }

    private void showNewSmsCnt(CharSequence s) {
        int nSmsLength = ActivitySmsDetail.getOneSmsLenth(s.toString());
        int nRemain = nSmsLength - s.length() % nSmsLength;

        if (s.length() >= nSmsLength && nRemain == nSmsLength) {
            nRemain = 0;
        }

        int nCnt = 1;

        if (s.length() > nSmsLength) {
            nCnt = (s.length() - 1) / nSmsLength + 1;
        }


    }
}
