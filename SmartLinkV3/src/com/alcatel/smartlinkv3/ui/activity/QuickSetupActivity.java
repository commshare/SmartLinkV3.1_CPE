package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.view.ClearEditText;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class QuickSetupActivity  extends Activity implements OnClickListener{
  private State mStep = State.UNKNOW;
  private TextView mNavigatorLeft;
  private TextView mNavigatorRight;
  private TextView mPromptText;
  private StateHandler mStateHandler;
  private boolean mPINCheckEnable;
  private ClearEditText mEnterText;
  
  protected TextView mSetupTitle;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*
     * DO NOT CLAIN 'android:theme="@android:style/Theme.Black.NoTitleBar"' 
     * in AndroidManifest.xml
     */
   // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.quick_setup);
   // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);
   // title = (TextView) findViewById(R.id.custom_title_text);
    mSetupTitle = (TextView)findViewById(R.id.qs_item_title);
    mPromptText = (TextView)findViewById(R.id.qs_item_prompt);
    mNavigatorRight = (TextView)findViewById(R.id.navigator_right);
    mNavigatorLeft = (TextView)findViewById(R.id.navigator_left);
    mEnterText = (ClearEditText)findViewById(R.id.password);
    
    //mPINCheckEnable = savedInstanceState.getBoolean("PINCheck", false);
    mPINCheckEnable = true;
    if (mPINCheckEnable) {
    mStateHandler = new PinCodeHandler();
    mStateHandler.addNextStateHandler(new WiFiSSIDHandler(true));
    } else {
      mStateHandler = new WiFiSSIDHandler(false);
    }
    
    mStateHandler.goTail().addNextStateHandler(new WiFiPasswdHandler()).
    addNextStateHandler(new SetupSummaryHandler());
    mStateHandler.setupViews();
    
    //mNavigatorLeft.setOnClickListener(this);
    mNavigatorRight.setOnClickListener(this);
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
    abstract public void storeSetting();
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
    }

    @Override
    public void storeSetting() {
      if(!mSkipSetup)
        return;
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
      mEnterText.setVisibility(View.VISIBLE);  
      mEnterText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
      mEnterText.getText().clear();
      mEnterText.addTextChangedListener(this);
    }

    @Override
    public void storeSetting() {
      if(!mSkipSetup)
        return;
      
      mEnterText.getText();
      
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
    }

    @Override
    public void storeSetting() {
    }
  }
}