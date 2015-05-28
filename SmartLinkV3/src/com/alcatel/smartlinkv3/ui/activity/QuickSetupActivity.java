package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.PinState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.SsidHiddenEnum;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.view.ClearEditText;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuickSetupActivity  extends Activity implements OnClickListener{
  //private State mStep = State.UNKNOW;

  private static final String TAG = "QuickSetupActivity";
  protected BroadcastReceiver mReceiver;
  private TextView mNavigatorLeft;
  private TextView mNavigatorRight;
  private TextView mPromptText;
  private TextView mWiFiSSIDTextView;
  private TextView mWiFiPasswdTextView;
  private StateHandler mStateHandler;
  private boolean mPINCheckEnable;
  private ClearEditText mEnterText;  
  protected TextView mSetupTitle;
  private String mWiFiSSID;
  private String mWiFiPasswd;
  private Context mContext;
  private BusinessMannager mBusinessMgr;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*
     * DO NOT CLAIN 'android:theme="@android:style/Theme.Black.NoTitleBar"' 
     * in AndroidManifest.xml
     */
    mContext = getBaseContext();
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
    
    //mPINCheckEnable = savedInstanceState.getBoolean("PINCheck", false);
    buildStateHandlerChain(false);

    mReceiver = new QSBroadcastReceiver();
  }
  
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
    
    if(sim.m_SIMState == SIMState.NoSim ||
        sim.m_SIMState != SIMState.PinRequired && sim.m_PinState == PinState.PinEnableVerified){
      mStateHandler = new WiFiSSIDHandler(false);
    } else {
      mStateHandler = new PinCodeHandler();
      mStateHandler.addNextStateHandler(new WiFiSSIDHandler(true));          
    }        
    
    mStateHandler.goTail().addNextStateHandler(new WiFiPasswdHandler()).
    addNextStateHandler(new SetupSummaryHandler());
    mStateHandler.setupViews();
    
    //mNavigatorLeft.setOnClickListener(this);
    mNavigatorRight.setOnClickListener(QuickSetupActivity.this);
    //mNavigatorRight.setTextColor(Color.BLACK);
    //mNavigatorLeft.setTextColor(Color.BLACK);
  }
  
  @Override
  public void onClick(View v) {
    int visibility = v.getVisibility();
    if ( visibility == View.GONE || visibility == View.INVISIBLE)
      return;
    
    if (v == mNavigatorRight) {
      nextSetting();
    } else if (v == mNavigatorLeft) {
      backSetting();
    }
  }
  
  private void nextSetting() {
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
  private boolean checkQuicksetupStatus() {
    SharedPreferences preferences = getSharedPreferences(
        "QUICKSETUP", Context.MODE_PRIVATE);
    Editor editor = preferences.edit();
    boolean qs_stat = preferences.getBoolean("status", true);
    return qs_stat;
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
      } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
        if (ok) {
          buildStateHandlerChain(true);
        }
      } else if (action.equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
        if (BaseResponse.RESPONSE_OK == result && 0 == error.length()) {
          mWiFiSSID = mBusinessMgr.getSsid();
          mWiFiPasswd = mBusinessMgr.getWifiPwd();

          if(mEnterText.getText().length() > 0) {
            return;
          }  
          if (mStateHandler.getState() == State.WIFI_SSID) {
            mEnterText.setHint(mWiFiSSID);          
          } else if (mStateHandler.getState() == State.WIFI_PASSWD) {
            mEnterText.setHint(mWiFiPasswd);
          }
          //mEnterText.invalidate();
        }
      } else if (action.equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
        if (BaseResponse.RESPONSE_OK == result && 0 == error.length()) {
          mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);

        } else {
          if (mStateHandler == null)
            return;
          if (mStateHandler.getState() == State.WIFI_SSID) {
            Log.v(TAG, "set wifi ssid failed, return " + error);
          } else if (mStateHandler.getState() == State.WIFI_PASSWD) {
            Log.v(TAG, "set wifi ssid failed, return " + error);
          }
        }
      } else if (action.equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
        if (BaseResponse.RESPONSE_OK == result && error.length() == 0) {
          // m_dlgPin.onEnterPinResponse(true);
        } else {
          // m_dlgPin.onEnterPinResponse(false);
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
  
  enum State {
    UNKNOW, PIN_CODE, WIFI_SSID, WIFI_PASSWD, SUMMARY, FINISH;    
  }
  
  abstract class StateHandler implements TextWatcher{
    private State mState;
    private StateHandler mPreviousHandler;
    private StateHandler mNextHandler;
    protected boolean mSkipSetup;
    protected int mInputMax;
    protected int mInputMin;
    StateHandler(State state, int inputMin, int inputMax) {
      mState = state;
      mSkipSetup = true;
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
    public void storeSetting() {}
  }

  class UnknowHandler extends StateHandler {
    UnknowHandler(){
      super(State.UNKNOW, 0, 0);      
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_completed));
      mPromptText.setText(getString(R.string.qs_summary));
      mNavigatorRight.setText(R.string.finish);
      mEnterText.setVisibility(View.GONE);
      mWiFiSSIDTextView.setVisibility(View.VISIBLE);
      mWiFiPasswdTextView.setVisibility(View.VISIBLE);  
    }
  }
  
  class PinCodeHandler extends StateHandler {
    private int mPINTryTimes = 3;
    PinCodeHandler() {
      super(State.PIN_CODE, 8, 8);
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

    @Override
    public void storeSetting() {
      if(!mSkipSetup)
        return;      
    }
  }
  
  class WiFiSSIDHandler extends StateHandler {
    private boolean mHaveBackSetting;
    WiFiSSIDHandler(boolean back){
      super(State.WIFI_SSID, 1, 32);
      mHaveBackSetting = back;
    }

    @Override
    public void setupViews() {
      mSetupTitle.setText(getString(R.string.qs_item_wifi_ssid));
      mPromptText.setText(getString(R.string.qs_wifi_ssid_prompt));
      mNavigatorRight.setText(R.string.skip);
      mNavigatorLeft.setVisibility(mHaveBackSetting ? View.VISIBLE : View.GONE);
      if (mHaveBackSetting) {
        mNavigatorLeft.setVisibility(View.VISIBLE );
        mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      } else {
        mNavigatorLeft.setVisibility(View.INVISIBLE);
        mNavigatorLeft.setClickable(false);
      }
      mEnterText.setInputType(InputType.TYPE_CLASS_TEXT);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);      
      mEnterText.setHint(mWiFiSSID);
        //mEnterText.setText(mWiFiSSID);      
    }

    @Override
    public void storeSetting() {
      if(mSkipSetup) {
        //mWiFiSSID = null;
        return;
      }
      //setWiFiConfigure(mEnterText.getText().toString(), mWiFiPasswd);


        mWiFiSSID = mEnterText.getText().toString();
      
     
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
      mEnterText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);
      mWiFiSSIDTextView.setVisibility(View.GONE);
      mWiFiPasswdTextView.setVisibility(View.GONE);      
      mEnterText.setHint(mWiFiPasswd);      
    }

    @Override
    public void storeSetting() {
      if(mSkipSetup) {
        //mWiFiPasswd = null;
        return;
      }
      //setWiFiConfigure(mWiFiSSID, mEnterText.getText().toString());
      mWiFiPasswd = mEnterText.getText().toString();
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
    public void storeSetting() {
      setWiFiConfigure(mWiFiSSID, mWiFiPasswd);
      Intent it = new Intent(mContext, MainActivity.class);
      startActivity(it);
      finish();
    }
  }
}