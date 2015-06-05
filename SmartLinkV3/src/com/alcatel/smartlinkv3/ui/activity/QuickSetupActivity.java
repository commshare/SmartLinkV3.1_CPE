package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.ENUM.PinState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.SsidHiddenEnum;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog.OnClickConfirmBotton;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.DialogAutoDismiss;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.CancelLoginListener;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.view.ClearEditText;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class QuickSetupActivity  extends Activity implements OnClickListener{
  private static final String TAG = "QuickSetupActivity";
  protected BroadcastReceiver mReceiver;
  private TextView mNavigatorLeft;
  private TextView mNavigatorRight;
  private TextView mPromptText;
  private TextView mWiFiSSIDTextView;
  private TextView mWiFiPasswdTextView;
  private StateHandler mStateHandler;
  private ClearEditText mEnterText;  
  protected TextView mSetupTitle;
  private String mWiFiSSID;
  private String mWiFiPasswd;
  private boolean mSendRequest = false;//If user not change settings, do not send request.
  private ErrorDialog mPINErrorDialog = null;
  private LoginDialog mLoginDialog = null;
  private AutoLoginProgressDialog mAutoLoginDialog = null;
  private CommonErrorInfoDialog mConfirmDialog = null;
  private Context mContext;
  private BusinessMannager mBusinessMgr;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*
     * DO NOT CLAIN 'android:theme="@android:style/Theme.Black.NoTitleBar"' 
     * in AndroidManifest.xml
     */
    mContext = this;
    mBusinessMgr = BusinessMannager.getInstance();
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.quick_setup);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_1);

    TextView title = (TextView) findViewById(R.id.tv_title_title);
    if (title != null)
      title.setText(R.string.qs_title);
    findViewById(R.id.ib_title_back).setVisibility(View.INVISIBLE);
    findViewById(R.id.tv_title_back).setVisibility(View.INVISIBLE);
    
    mSetupTitle = (TextView)findViewById(R.id.qs_item_title);
    mPromptText = (TextView)findViewById(R.id.qs_item_prompt);
    mNavigatorRight = (TextView)findViewById(R.id.navigator_right);
    mNavigatorLeft = (TextView)findViewById(R.id.navigator_left);
    mEnterText = (ClearEditText)findViewById(R.id.password);
    mWiFiSSIDTextView = (TextView)findViewById(R.id.qs_detail_wifissid);
    mWiFiPasswdTextView = (TextView)findViewById(R.id.qs_detail_wifipasswd);   
    
    mReceiver = new QSBroadcastReceiver();
    
    UserLoginStatus status = mBusinessMgr.getLoginStatus(); 
    /*When use login and back to launcher, we do not let user enter
     * login password
     */
    if (LoginDialog.isLoginSwitchOff() || status == UserLoginStatus.login) {
      buildStateHandlerChain(false);
      return;
    }
    
    setViewsVisibility(false, true);
    if (status == UserLoginStatus.LoginTimeOut) {
      handleLoginError(R.string.other_login_warning_title, 
          R.string.login_login_time_used_out_msg, false);
    } else {
      doLogin();
    }
  }
  
  private void handleLoginError(int titleId, int messageId, final boolean login) {
    if (mConfirmDialog != null) {
      mConfirmDialog.destroyDialog();
    }
    mConfirmDialog = CommonErrorInfoDialog.getInstance(mContext);
    mConfirmDialog.setCancelCallback(new OnClickConfirmBotton() {

      @Override
      public void onConfirm() {
        if(login) {
          //If timeout, let user re-login
          showLoginDialog();
        } else {
          //If other user enter, do not need setup
          finishQuickSetup(true);
        }
      }
    
    });
    mConfirmDialog.showDialog(
        getString(titleId),
        getString(messageId));
  }
  
  private void doLogin() {
    mAutoLoginDialog = new AutoLoginProgressDialog(this);
    mAutoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener() {
      /*
       * Auto login successfully.
       * Scenario: user enter correct password, then exit activity by press home key,
       * later launch smartlink again. 
       */
      public void onLoginSuccess()  {
        buildStateHandlerChain(false);
      }

      public void onLoginFailed(String error_code) {
        if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)){
          handleLoginError(R.string.other_login_warning_title,
              R.string.login_other_user_logined_error_msg, false);
        } else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
          handleLoginError(R.string.other_login_warning_title, 
              R.string.login_login_time_used_out_msg, true);
        } else {
          ErrorDialog.getInstance(mContext).showDialog(
              getString(R.string.login_psd_error_msg),
              new OnClickBtnRetry() {
                  @Override
                  public void onRetry() {
                    showLoginDialog();
            }
          });
        }       
      }

      @Override
      public void onFirstLogin(){
        showLoginDialog();
      }         
    });  
  }
  /*
   * A dialog that let use enter password.
   */
  private void showLoginDialog() {
    mLoginDialog = new LoginDialog(this);
    mLoginDialog.setCancelCallback(new CancelLoginListener() {

      @Override
      public void onCancelLogin() {
        //finishQuickSetup(true);
        handleLoginError(R.string.qs_title, R.string.qs_exit_query, false);
      }
      
    });
    mLoginDialog.showDialog(new OnLoginFinishedListener() {
      @Override
      public void onLoginFinished() {
        buildStateHandlerChain(false);
      }
    });  
  }
  
  private void setViewsVisibility(boolean visible, boolean all) {
    int visibility = visible ? View.VISIBLE : View.INVISIBLE;
    mSetupTitle.setVisibility(visibility);
    mPromptText.setVisibility(visibility);
    mEnterText.setVisibility(visibility);
    mNavigatorRight.setVisibility(visibility);
    if (all) {
      mNavigatorLeft.setVisibility(visibility);
    }
  }
  
  /*
   * put various Settings into a chain. This chain is a 
   * double link queue, and mStateHandler is the cursor.
   * The head and tail of queue are not linked. 
   */
  private void buildStateHandlerChain(boolean clear) {
    //SimManager poll SIM_GET_SIM_STATUS_ROLL_REQUSET in task, but it do not always 
    //broadcaset SIM request.
    SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
    
    if (clear && mStateHandler != null) {
      StateHandler head = mStateHandler.goHead();
      StateHandler next;
      for(;head != null;){
        head.mPreviousHandler = null;
        next = head.mNextHandler;
        head.mNextHandler = null;
        head = next;
      }
    }
    
    setViewsVisibility(true, false);
    /*
     * refer to MainActivity.simRollRequest() to handle SIM/PIN state
     */
    //if(sim.m_SIMState == SIMState.NoSim ||
    //    sim.m_SIMState != SIMState.PinRequired && sim.m_PinState == PinState.PinEnableVerified){
    if (sim.m_SIMState == SIMState.PinRequired) {
      //if SIMState.PinRequired, that means sim.m_nPinRemainingTimes > 0
      mStateHandler = new PinCodeHandler(sim.m_nPinRemainingTimes);
      mStateHandler.addNextStateHandler(new WiFiSSIDHandler(false));
    } else {
      mStateHandler = new WiFiSSIDHandler(true);
   }        
//TODO:: NEED TEST
      
    mStateHandler.goTail().addNextStateHandler(new WiFiPasswdHandler()).
    addNextStateHandler(new SetupSummaryHandler());
    mStateHandler.setupViews();    

    //mNavigatorLeft.setOnClickListener(this);
    mNavigatorRight.setOnClickListener(QuickSetupActivity.this);
    //mNavigatorRight.setTextColor(Color.BLACK);
  }
  
  @Override
  public void onClick(View v) {
    int visibility = v.getVisibility();
    if ( visibility == View.GONE || visibility == View.INVISIBLE)
      return;
    
    if (v == mNavigatorRight) {
      nextSetting(true);
    } else if (v == mNavigatorLeft) {
      backSetting();
    }
  }
  
  /*
   * param checkStore whether let state handler do settings
   */
  private void nextSetting(boolean checkStore) {
    if(checkStore && mStateHandler.storeSetting() == false)
      return;
    
    StateHandler handler = mStateHandler.goNext();
    if (handler != null){
      mStateHandler = handler;
      handler.setupViews();
    }
  }
  
  private void backSetting() {
    StateHandler handler = mStateHandler.goBack(); 
    if (handler != null){
      mStateHandler = handler;
      handler.setupViews();
    }
  }
  
  /*
   * When user enter correct PIN, or he/she enter 3 error codes, 
   * disable PIN code setting. 
   */
  private void removePINCodeSetting() {
    StateHandler handler = mStateHandler;
    if (handler == null || handler.mIsHead == false || handler.mState != State.PIN_CODE) {
      Log.e(TAG, "Current state is not PIN code or it is not the head sate.");
      return;
    }

    handler.mPreviousHandler = null;
    mStateHandler = handler.mNextHandler;
    handler.mNextHandler = null;

    if (mStateHandler != null) {
      mStateHandler.mPreviousHandler = null;
      mStateHandler.mIsHead = true;
      mStateHandler.setupViews();
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    registerReceiver(mReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));  
    registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
      
    mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);   
  }
  
  @Override
  protected void onPause() {
    super.onDestroy();
    try {
        this.unregisterReceiver(mReceiver); 
      }catch(Exception e) {
        e.printStackTrace();
      }
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mPINErrorDialog != null)
      mPINErrorDialog.destroyDialog();
    if (mLoginDialog != null)
      mLoginDialog.destroyDialog();
    if (mConfirmDialog != null)
      mConfirmDialog.destroyDialog();
    if (mAutoLoginDialog != null)
      mAutoLoginDialog.destroyDialog();
    mBusinessMgr = null;
  }
  
  class QSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      int result = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
                                     BaseResponse.RESPONSE_OK);
      String error = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);

      Log.d(TAG, "Quick Setup receive broadcase " + action);
      boolean ok = BaseResponse.RESPONSE_OK == result && 0 == error.length();
      if (action.equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
        // If WiFi disconnect router, go to Refresh WiFi activity.
        if (!DataConnectManager.getInstance().getCPEWifiConnected()) {
          context.startActivity(new Intent(context, RefreshWifiActivity.class));
          // finish();
        }
      } else if (action.equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
        if (BaseResponse.RESPONSE_OK == result && 0 == error.length()) {
          mWiFiSSID = mBusinessMgr.getSsid();
          mWiFiPasswd = mBusinessMgr.getWifiPwd();           
          
          if(mStateHandler == null || mEnterText.getText().length() > 0 ||
              mBusinessMgr.getLoginStatus() != UserLoginStatus.login) {
            return;
          }  
          if (mStateHandler.getState() == State.WIFI_SSID && mWiFiSSID != null) {
            //mEnterText.setHint(mWiFiSSID);
            mEnterText.setText(mWiFiSSID);
          } else if (mStateHandler.getState() == State.WIFI_PASSWD && mWiFiPasswd != null) {
            //mEnterText.setHint(mWiFiPasswd);
            mEnterText.setText(mWiFiPasswd);
          }
        }
      } else if (action.equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
        if (BaseResponse.RESPONSE_OK == result && 0 == error.length()) {
          //mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);
          finishQuickSetup(false);
        } else {
          if (mStateHandler == null)
            return;
          if (mStateHandler.getState() == State.WIFI_SSID) {
            Log.v(TAG, "set wifi ssid failed, return " + error);
          } else if (mStateHandler.getState() == State.WIFI_PASSWD) {
            Log.v(TAG, "set wifi ssid failed, return " + error);
          }
        }
      } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
        if (ok) {
          buildStateHandlerChain(true);
        }
      } else if (action.equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
        if(mStateHandler == null) {
          return;
        } 
        if (BaseResponse.RESPONSE_OK == result && error.length() == 0) {
          //actually, if user enter correct PIN, it never need go back PIN enter interface.
          removePINCodeSetting();
          //nextSetting(false);
        } else {
          //PIN unlock
          if (mStateHandler.retryTimes() > 0) {
            mPINErrorDialog = ErrorDialog.getInstance(mContext);

            mPINErrorDialog.showDialog(getString(R.string.pin_error_waring_title),
                new OnClickBtnRetry() {

                  @Override
                  public void onRetry() {
                    mStateHandler.retryInput();
                  }
                });
          } else {
            // PIN LOCK
            mConfirmDialog = CommonErrorInfoDialog.getInstance(mContext);
            mConfirmDialog.setCancelCallback(new OnClickConfirmBotton() {

              @Override
              public void onConfirm() {
                removePINCodeSetting();
                //finishQuickSetup(true);                
              }
            
            });
            mConfirmDialog.showDialog(getString(R.string.qs_item_pin_code), 
                    getString(R.string.pin_error_waring_title));
          }
        }
      }
    }
  }  
  
  private void setWiFiConfigure(String ssid, String passwd){
    DataValue data = new DataValue();
    SecurityMode mode = mBusinessMgr.getSecurityMode();
    int encrypt;
    if (SecurityMode.Disable == mode) {
      encrypt = -1;
    } else if (SecurityMode.WEP == mode) {
      encrypt = WEPEncryption.antiBuild(
          BusinessMannager.getInstance().getWEPEncryption());
    }else {
      encrypt = WPAEncryption.antiBuild(
          BusinessMannager.getInstance().getWPAEncryption());
    }
    data.addParam("WlanAPMode", WlanFrequency.antiBuild(mBusinessMgr.getWlanFrequency()));
    data.addParam("Ssid", ssid);
    data.addParam("Password", passwd);
    data.addParam("Security", SecurityMode.antiBuild(mode));
    data.addParam("Encryption", encrypt);
    data.addParam("SsidStatus", SsidHiddenEnum.antiBuild(mBusinessMgr.getSsidStatus()));
    mBusinessMgr.sendRequestMessage(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET, data);
  }
  
  private void finishQuickSetup(boolean setFlag){
    if(setFlag) {
      CPEConfig.getInstance().setQuickSetupFlag();
    }
    Intent it = new Intent(mContext, MainActivity.class);
    startActivity(it);
    finish();
  }
  
  enum State {
    UNKNOW, PIN_CODE, WIFI_SSID, WIFI_PASSWD, SUMMARY, FINISH;    
  }
  
  abstract class StateHandler implements TextWatcher{
    private State mState;
    private StateHandler mPreviousHandler;
    private StateHandler mNextHandler;
    protected boolean mSkipSetup;
    protected boolean mIsHead;
    protected int mInputMax;
    protected int mInputMin;
    StateHandler(State state, int inputMin, int inputMax) {
      mState = state;
      mSkipSetup = true;
      mIsHead = false;
      mPreviousHandler = null;
      mNextHandler = null;
      mInputMax = inputMax;
      mInputMin = inputMin;
    }
    public StateHandler addNextStateHandler(StateHandler handler) {      
      mNextHandler = handler;
      if (handler != null){
        handler.setPreviousStateHandler(this);
      }
      return handler;
    }
    
    private void setPreviousStateHandler(StateHandler handler) {
      mPreviousHandler = handler;
    }
    
    public StateHandler goBack(){
      return mPreviousHandler;
    }
    
    public StateHandler goNext() {
      storeSetting();
      return mNextHandler;
    }
    
    public StateHandler skip() {
      return mNextHandler;
    }
    
    public StateHandler goHead() {
      StateHandler head = this;
      StateHandler prev = head.mPreviousHandler;
      while (prev != null) {
        head = prev;
        prev = head.mPreviousHandler;
      }
      return head;
    }
    
    public StateHandler goTail() {
      StateHandler tail = this;
      StateHandler prev = tail.mNextHandler;
      while (prev != null) {
        tail = prev;
        prev = tail.mNextHandler;
      }
      return tail;
    }
    
    public State getState() {
      return mState;
    }
        
    @Override   
    public void onTextChanged(CharSequence s, int start, int count, int after) {

    }   
   
    @Override   
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {   
           
    } 

    @Override   
    public void afterTextChanged(Editable s) {
      int len = s.length();
      if (len > mInputMax) {
        int nSelStart = mEnterText.getSelectionStart();
        int nSelEnd = mEnterText.getSelectionEnd();
        s.delete(nSelStart - 1, nSelEnd);

        mEnterText.setTextKeepState(s);// 保持光标原先的位置，而
                                       // mEditText.setText(s)会让光标跑到最前面，
                                       // 就算是再加mEditText.setSelection(nSelStart)
                                       // 也不起作用
      } else if ( (mInputMax == mInputMin && mInputMin == len) ||
          (mInputMax > mInputMin && len >= mInputMin)){
        mSkipSetup = false;
        mNavigatorRight.setText(R.string.next);
      } else {
        mSkipSetup = true;
        mNavigatorRight.setText(R.string.skip);
      }
    }
    abstract public void setupViews();
    public boolean storeSetting() {return true;}

    public int retryTimes(){return 0;}
    public boolean retryInput(){return true;}
  }
  
  /*
   * When SIM PIN check is enabled, User can unlock SIM PIN in Quick Setup 
   * step, or skip. If user skip this step, he/she should unlock PIN in some
   * step that need check PIN later. 
   */
  class PinCodeHandler extends StateHandler {
    private int mPINTryTimes;
    PinCodeHandler(int tryTimes) {
      super(State.PIN_CODE, 8, 8);
      mIsHead = true;
      mPINTryTimes = tryTimes;
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_item_pin_code));
      mPromptText.setText(getString(R.string.qs_pin_code_prompt, mPINTryTimes));
      mNavigatorLeft.setVisibility(View.INVISIBLE);
      mNavigatorLeft.setClickable(false);
      mNavigatorRight.setText(R.string.skip); 
      mEnterText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);
    }
    
    public int retryTimes(){return mPINTryTimes;}
    
    public boolean retryInput(){
      mPINTryTimes--;
      mEnterText.getText().clear();
      mPromptText.setText(getString(R.string.qs_pin_code_prompt, mPINTryTimes));
      mNavigatorRight.setText(R.string.skip);
      mSkipSetup = true;
      return true;
    }

    @Override
    public boolean storeSetting() {
      if(mSkipSetup)
        return true;

      DataValue data = new DataValue();
      data.addParam("pin", mEnterText.getText().toString());
      mBusinessMgr.sendRequestMessage(MessageUti.SIM_UNLOCK_PIN_REQUEST, data);
      return false;
    }
  }
  
  class WiFiSSIDHandler extends StateHandler {
    WiFiSSIDHandler(boolean isHead){
      super(State.WIFI_SSID, 1, 32);
      mIsHead = isHead;
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_item_wifi_ssid));
      mPromptText.setText(getString(R.string.qs_wifi_ssid_prompt));
      mNavigatorRight.setText(R.string.skip);
      mNavigatorLeft.setVisibility(mIsHead ? View.VISIBLE : View.GONE);
      if (mIsHead) {
        mNavigatorLeft.setVisibility(View.INVISIBLE);
        mNavigatorLeft.setClickable(false);
      } else {
        mNavigatorLeft.setVisibility(View.VISIBLE);
        mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      }
      mEnterText.setInputType(InputType.TYPE_CLASS_TEXT);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);      
      //mEnterText.setHint(mWiFiSSID);
      if (mWiFiSSID != null) {
        mEnterText.setText(mWiFiSSID);
      }
    }

    @Override
    public boolean storeSetting() {
      if(mSkipSetup == false) {
      //setWiFiConfigure(mEnterText.getText().toString(), mWiFiPasswd);
        String enter = mEnterText.getText().toString();
        if (!enter.equals(mWiFiSSID)) {
          mWiFiSSID = enter;
          mSendRequest = true;
        }
        //mWiFiSSID = null;
      }

      return true;
    }
    
    @Override   
    public void onTextChanged(CharSequence s, int start, int count, int after) {
    } 
  }
  
  class WiFiPasswdHandler extends StateHandler {
    WiFiPasswdHandler(){
      super(State.WIFI_PASSWD, 8, 63);      
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_item_wifi_passwd));
      mPromptText.setText(getString(R.string.qs_wifi_passwd_prompt));
      mNavigatorRight.setText(R.string.skip); 
      mNavigatorLeft.setVisibility(View.VISIBLE);
      mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      mEnterText.setVisibility(View.VISIBLE);
      //replace  TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
      mEnterText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);
      mWiFiSSIDTextView.setVisibility(View.GONE);
      mWiFiPasswdTextView.setVisibility(View.GONE);      
      //mEnterText.setHint(mWiFiPasswd);
      if(mWiFiPasswd != null){
        mEnterText.setText(mWiFiPasswd);
      }
    }

    @Override
    public boolean storeSetting() {
      if(mSkipSetup == false) {
        //setWiFiConfigure(mWiFiSSID, mEnterText.getText().toString());
        String enter = mEnterText.getText().toString();
        if (!enter.equals(mWiFiPasswd)) {
          mWiFiPasswd = enter;
          mSendRequest = true;
        }
        //mWiFiPasswd = null;
      }

      return true;
    }    
  }
  
  class SetupSummaryHandler extends StateHandler {
    SetupSummaryHandler(){
      super(State.SUMMARY, 0, 0);      
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_completed));
      mPromptText.setText(getString(R.string.qs_summary));
      mNavigatorRight.setText(R.string.finish);
      mEnterText.setVisibility(View.GONE);
      mWiFiSSIDTextView.setVisibility(View.VISIBLE);
      mWiFiPasswdTextView.setVisibility(View.VISIBLE); 
      mWiFiSSIDTextView.setText(getString(R.string.qs_wifi_ssid, mWiFiSSID));
      mWiFiPasswdTextView.setText(getString(R.string.qs_wifi_passwd, mWiFiPasswd)); 
    }

    @Override
    public boolean storeSetting() {
      CPEConfig.getInstance().setQuickSetupFlag();
      if (mSendRequest) {
        setWiFiConfigure(mWiFiSSID, mWiFiPasswd);
      } else {
        finishQuickSetup(false);
      }
      return true;
    }
  }
}
