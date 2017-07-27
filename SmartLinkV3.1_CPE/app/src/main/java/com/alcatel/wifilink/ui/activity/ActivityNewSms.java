package com.alcatel.wifilink.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ENUM.SendStatus;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sms.SMSSaveParam;
import com.alcatel.wifilink.model.sms.SMSSendParam;
import com.alcatel.wifilink.model.sms.SendSMSResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.DataUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityNewSms extends BaseActivityWithBack implements OnClickListener {
    private Button m_btnSend = null;
    private EditText m_etNumber = null;
    private EditText m_etContent = null;
    private static final String NUMBER_REG_Ex = "^([+*#\\d;]){1}(\\d|[;*#]){0,}$";
    private String m_preMatchNumber = new String();

    private ProgressBar m_progressWaiting = null;

    private SendStatus m_sendStatus = SendStatus.None;
    private boolean m_bSendEnd = false;
    private ActionbarSetting actionbarSetting;
    private ActionBar actionBar;
    private ProgressDialog pd;
    private ImageButton ib_back;
    // private TextView tv_cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_new_view);
        getWindow().setBackgroundDrawable(null);

        // init actionbar
        initActionbar();

        // get controls
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

    private void initActionbar() {
        actionBar = getSupportActionBar();
        actionbarSetting = new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                ib_back = (ImageButton) view.findViewById(R.id.ib_newsms_back);
                ib_back.setOnClickListener(ActivityNewSms.this);
                //tv_cancel = (TextView) view.findViewById(R.id.tv_newsms_cancel);
                // tv_cancel.setOnClickListener(ActivityNewSms.this);
            }
        };
        actionbarSetting.settingActionbarAttr(this, actionBar, R.layout.actionbar_newsms);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        m_progressWaiting.setVisibility(View.GONE);
        if (m_etNumber.getText().toString() != null && m_etNumber.getText().toString().length() != 0 && m_etContent.getText().toString() != null && m_etContent.getText().toString().length() != 0)
            m_btnSend.setEnabled(true);
        m_etNumber.setEnabled(true);
        m_etContent.setEnabled(true);
        m_bSendEnd = false;
    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {

            case R.id.ib_newsms_back:
                finish();
                break;

            // case R.id.tv_newsms_cancel:
            //     finish();
            //     break;

            case R.id.send_btn:/* 发送短信 */
                showSendingDialog();
                OnBtnSend();// 发送
                m_etContent.setText("");// 清空编辑域
                break;
        }
    }

    /* **** showSendingDialog **** */
    private void showSendingDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.sms_sending));
        pd.show();
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

                String num = m_etNumber.getText().toString();
                ArrayList<String> numList = new ArrayList<>();
                numList.add(num);
                API.get().saveSMS(new SMSSaveParam(-1, m_etContent.getText().toString(), DataUtils.getCurrent(), numList), new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        ToastUtil_m.show(ActivityNewSms.this, getString(R.string.sms_save_success));
                        finish();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        super.onResultError(error);
                        //060801: Save SMS failed. 060802: Fail with store space full.
                        if (error.getCode().equals("060801")) {
                            ToastUtil_m.show(ActivityNewSms.this, getString(R.string.sms_save_error));

                        } else if (error.getCode().equals("060802")) {
                            ToastUtil_m.show(ActivityNewSms.this, getString(R.string.sms_error_message_full_storage));
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
                            ToastUtil_m.show(ActivityNewSms.this, getString(R.string.send_sms_failed));
                            break;
                        case "060602":
                            ToastUtil_m.show(ActivityNewSms.this, getString(R.string.fail_still_sending_last_message));
                            break;
                        case "060603":
                            ToastUtil_m.show(ActivityNewSms.this, getString(R.string.fail_with_store_space_full));
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
                //0 : none 1 : sending 2 : sendAgainSuccess 3: fail still sending last message 4 : fail with Memory full 5: fail
                resetUI();
                int sendStatus = result.getSendStatus();
                if (sendStatus == 0) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil_m.show(ActivityNewSms.this, getString(R.string.none));
                    finish();
                } else if (sendStatus == 1) {
                    getSendSMSResult();
                } else if (sendStatus == 2) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil_m.show(ActivityNewSms.this, getString(R.string.succeed));
                    finish();// 结束界面
                } else if (sendStatus == 3) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil_m.show(ActivityNewSms.this, getString(R.string.fail_still_sending_last_message));
                    finish();// 结束界面
                } else if (sendStatus == 4) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil_m.show(ActivityNewSms.this, getString(R.string.fail_with_memory_full));
                    finish();// 结束界面
                } else if (sendStatus == 5) {
                    m_progressWaiting.setVisibility(View.GONE);
                    ToastUtil_m.show(ActivityNewSms.this, getString(R.string.fail));
                    finish();// 结束界面
                }
                if (pd != null) {
                    pd.dismiss();
                }

            }

            @Override
            protected void onFailure() {
                super.onFailure();
                resetUI();
                if (pd != null) {
                    pd.dismiss();
                }
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
