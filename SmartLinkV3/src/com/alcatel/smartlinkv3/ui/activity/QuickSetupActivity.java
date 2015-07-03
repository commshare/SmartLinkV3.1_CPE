package com.alcatel.smartlinkv3.ui.activity;


import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
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
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog.OnClickConfirmBotton;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog.OnClickBottonConfirm;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog.OnClickBtnCancel;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.CancelLoginListener;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.view.ClearEditText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class QuickSetupActivity  extends Activity implements OnClickListener{
  private static final String TAG = "QuickSetupActivity";
  private final static String BUNDLE_HANDLER_STATE = "State";
  private final static String BUNDLE_EDIT_TEXT = "EDIT-TEXT";
  private final static String BUNDLE_WIFI_SSID = "WIFI-SSID";
  private final static String BUNDLE_WIFI_PASSWD = "WIFI-PASSWD";
  private final static String BUNDLE_LOGINERROR_TITLE = "LoginExceptTitle";
  private final static String BUNDLE_LOGINERROR_MESSAGE = "LoginExceptMessage";
  protected BroadcastReceiver mReceiver;
  private TextView mNavigatorLeft;
  private TextView mNavigatorRight;
  private TextView mPromptText;
  private TextView mWiFiSSIDTextView;
  private TextView mWiFiPasswdTextView;
  private StateHandler mStateHandler;
  private ClearEditText mEnterText;  
  private LinearLayout pukLinearLayout1;
  private LinearLayout pukLinearLayout2;
  private LinearLayout pukLinearLayout3;
  private ClearEditText pukCodeText;
  private ClearEditText newPinCodeText;
  private ClearEditText confirmPinCodeText;
  protected TextView mSetupTitle;
  private String mWiFiSSID;
  private String mWiFiPasswd;
  private SecurityMode mSecurityMode;
  private ErrorDialog mPINErrorDialog = null;
  private ErrorDialog mPUKErrorDialog = null;
  private AutoForceLoginProgressDialog m_ForceloginDlg = null;
  private LoginDialog mLoginDialog = null;
  private AutoLoginProgressDialog mAutoLoginDialog = null;
  private CommonErrorInfoDialog mConfirmDialog = null;
  private ForceLoginSelectDialog forceLoginSelectDialog = null;
  private Context mContext;
  private BusinessMannager mBusinessMgr;
  private boolean pukValState,newPinValState,confirmPinState;
  private ProgressDialog m_progress_dialog = null;
  //private int restoreLoginState = 0;//0:password输入框无操作。1：password输入框有操作。2.密码输入错误，弹出retry dialog。3

  
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
    pukCodeText = (ClearEditText) findViewById(R.id.puk_code);
    newPinCodeText = (ClearEditText) findViewById(R.id.new_pin_code);
    confirmPinCodeText = (ClearEditText) findViewById(R.id.confirm_pin_code);
    pukLinearLayout1 = (LinearLayout) findViewById(R.id.qs_puk_linear1);
    pukLinearLayout2 = (LinearLayout) findViewById(R.id.qs_puk_linear2);
    pukLinearLayout3 = (LinearLayout) findViewById(R.id.qs_puk_linear3);
    setViewsVisibility(false, false, false, false, false);
    
    mReceiver = new QSBroadcastReceiver();
    
//    if (restoreInstanceState(savedInstanceState))
//      return;
    
    UserLoginStatus status = mBusinessMgr.getLoginStatus(); 
    /*When use login and back to launcher, we do not let user enter
     * login password
     */
    if (LoginDialog.isLoginSwitchOff() || status == UserLoginStatus.login) {
      buildStateHandlerChain(false);
      return;
    }
    
    if (status == UserLoginStatus.LoginTimeOut) {
      handleLoginError(R.string.other_login_warning_title, 
          R.string.login_login_time_used_out_msg, true, true);
    } else {
      doLogin();
    }
  }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

  /*
   * if BUNDLE_HANDLER_STATE saved, it means that the screen orientation is 
   * change, activity should restore previous instance state.
   */
//  private boolean restoreInstanceState(Bundle savedInstanceState) {
//    if (savedInstanceState == null)
//      return false;
//    String stateName = savedInstanceState.getString(BUNDLE_HANDLER_STATE);
//    //Log.d(TAG, "stateName:"+stateName);
//    if (stateName == null)
//      return false;
//    State s = null;
//    try {
//      s = Enum.valueOf(State.class, stateName);
//    } catch (IllegalArgumentException ex) {
//      return false;
//    }
//    if (s == null){
//      return false;
//    } else if (s == State.LOGIN_ERROR) {
//      String title = savedInstanceState.getString(BUNDLE_LOGINERROR_TITLE);
//      String message = savedInstanceState.getString(BUNDLE_LOGINERROR_MESSAGE);
//      mStateHandler = new LoginExceptionHandler(title, message);
//      mStateHandler.setupViews();
//    } else {
//      StateHandler handler = null;
//      //onRestoreInstanceState is invoke after onCrate, so get these values here.
//      mWiFiSSID = savedInstanceState.getString(BUNDLE_WIFI_SSID);
//      mWiFiPasswd = savedInstanceState.getString(BUNDLE_WIFI_PASSWD);
//      buildStateHandlerChain(false);
//      if (mStateHandler != null)
//        handler = mStateHandler.getStateHandler(s);
//      if (handler != null) {
//        /*
//         * If the previous is the last summary, left navigator is invisible.
//         * And the left navigator is set in the second step. So do 
//         * handler.setupViews() is not enough. Let handlers between mStateHandler 
//         * and handler do setupViews().
//         */
//        //mStateHandler.quickPlay(handler);
//        handler.setupViews();
//        mStateHandler = handler;
//        if (mEnterText.getVisibility() == View.VISIBLE) {
//          String editText = savedInstanceState.getString(BUNDLE_EDIT_TEXT);
//          mEnterText.setText(editText);
//        }
//      } else {
//        Log.e(TAG, "can not get state " + s.name() + " handler");
//      }
//    }
//    return true;
//  }
  
//  @Override
//  protected void onRestoreInstanceState(Bundle savedInstanceState) {
//    super.onRestoreInstanceState(savedInstanceState);
//    
//  }

//  @Override
//  protected void onSaveInstanceState(Bundle outState) {
//    super.onSaveInstanceState(outState);
//    if (mStateHandler != null) {
//      State s = mStateHandler.getState();
//      outState.putString(BUNDLE_HANDLER_STATE, s.name());
//      if (s == State.LOGIN_ERROR) {
//        outState.putString(BUNDLE_LOGINERROR_TITLE, mStateHandler.getTitleText());
//        outState.putString(BUNDLE_LOGINERROR_MESSAGE, mStateHandler.getMessageText());
//      }
//    }
//    
//    if (mWiFiSSID != null)
//      outState.putString(BUNDLE_WIFI_SSID, mWiFiSSID);
//    if (mWiFiPasswd != null)
//      outState.putString(BUNDLE_WIFI_PASSWD, mWiFiPasswd);
//    
//    if (mEnterText.getVisibility() == View.VISIBLE)
//      outState.putString(BUNDLE_EDIT_TEXT, mEnterText.getText().toString());
//  }
  
  private void handleLoginError(int titleId, int messageId, boolean showDialog, final boolean retryLogin) {
    if (!retryLogin && !showDialog) {
      mStateHandler = new LoginExceptionHandler(getString(titleId), getString(messageId)); 
      mStateHandler.setupViews();
      return;
    }
    
    if (mConfirmDialog != null) {
      mConfirmDialog.destroyDialog();
    }
    mConfirmDialog = CommonErrorInfoDialog.getInstance(mContext);
    mConfirmDialog.setCancelCallback(new OnClickConfirmBotton() {

      @Override
      public void onConfirm() {
        if(retryLogin) {
          //If timeout, let user re-login
          //showLoginDialog();
        	doLogin();
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
  	m_ForceloginDlg = new AutoForceLoginProgressDialog(this); 
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
        	//Log.d(TAG, "ForceLogin.status:"+FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"));
        	if(FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") == true){
          forceLoginSelectDialog = ForceLoginSelectDialog.getInstance(mContext);
          forceLoginSelectDialog.showDialogAndCancel(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg), 
            		new OnClickBottonConfirm()  
            { 
                public void onConfirm()  
                { 
                    	m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() { 
                        public void onLoginSuccess()                  
                        { 
                            buildStateHandlerChain(false); 
                        } 
 
                        public void onLoginFailed(String error_code) 
                        { 
                            if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) 
                            { 
                                ErrorDialog.getInstance(mContext).showDialog(getString(R.string.login_psd_error_msg), 
                                        new OnClickBtnRetry()  
                                { 
                                    @Override 
                                    public void onRetry()  
                                    { 
                                        //showLoginDialog(); 
                                    		doLogin();
                                    } 
                                }); 
                            }else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) 
                            { 
                                //m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),    m_loginDlg.getLoginTimeUsedOutString()); 
                                handleLoginError(R.string.other_login_warning_title,  
                                          R.string.login_login_time_used_out_msg, true, true); 
                            } 
                        } 
                    }); 
                } 
                
                
            },new OnClickBtnCancel(){

							@Override
							public void onCancle() {
								 handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
							}}); 
         
        	}else{
        	  handleLoginError(R.string.other_login_warning_title,  R.string.login_other_user_logined_error_msg, false, false); 
        	}
        } else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) { 
          handleLoginError(R.string.other_login_warning_title,  
              R.string.login_login_time_used_out_msg, true, true); 
        } else { 
          ErrorDialog.getInstance(mContext).showDialog( 
              getString(R.string.login_psd_error_msg), 
              new OnClickBtnRetry() { 
                  @Override 
                  public void onRetry() { 
                    //showLoginDialog(); 
                  	doLogin();
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
        handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
      }
      
    });
    mLoginDialog.showDialogDoLogin();  
//    mLoginDialog.showDialog(new OnLoginFinishedListener() {
//      @Override
//      public void onLoginFinished() {
//       // buildStateHandlerChain(true);
//      }
//    });  
  }
  
  private void setViewsVisibility(boolean prompt_title_right_show, boolean leftShow,boolean pukShow,boolean mTextShow,boolean summShow) {
    int visibility = prompt_title_right_show ? View.VISIBLE : View.INVISIBLE;
    mSetupTitle.setVisibility(visibility);
    mPromptText.setVisibility(visibility);
    mNavigatorRight.setVisibility(visibility);
//    mEnterText.setEnabled(true);
//    mEnterText.setClearIconVisible(true);
    if (mTextShow) {
    	mEnterText.setVisibility(View.VISIBLE);
		}else {
			mEnterText.setVisibility(View.GONE);
		}
    if (pukShow) {
      pukLinearLayout1.setVisibility(View.VISIBLE);
      pukLinearLayout2.setVisibility(View.VISIBLE);
      pukLinearLayout3.setVisibility(View.VISIBLE);
		}else {
			pukLinearLayout1.setVisibility(View.GONE);
	    pukLinearLayout2.setVisibility(View.GONE);
	    pukLinearLayout3.setVisibility(View.GONE);
		}
   
    if (leftShow) {
      mNavigatorLeft.setVisibility(View.VISIBLE);
    }else {
    	mNavigatorLeft.setVisibility(View.INVISIBLE);
		}
    if (summShow) {
    	 mWiFiSSIDTextView.setVisibility(View.VISIBLE);
       mWiFiPasswdTextView.setVisibility(View.VISIBLE); 
		}else {
			mWiFiSSIDTextView.setVisibility(View.GONE);
      mWiFiPasswdTextView.setVisibility(View.GONE); 
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
   
		if(m_progress_dialog != null && m_progress_dialog.isShowing() && sim.m_SIMState != SIMState.SimCardIsIniting){
  		m_progress_dialog.dismiss();
  	}
    if (clear && mStateHandler != null) {
    	mStateHandler.clearOtherTextListen(mStateHandler);
      StateHandler head = mStateHandler.goHead();
      StateHandler next;
      for(;head != null;){
        head.mPreviousHandler = null;
        next = head.mNextHandler;
        head.mNextHandler = null;
        head = next;
      }
    }
    
   
    /*
     * refer to MainActivity.simRollRequest() to handle SIM/PIN state
     */
    //if(sim.m_SIMState == SIMState.NoSim ||
    //    sim.m_SIMState != SIMState.PinRequired && sim.m_PinState == PinState.PinEnableVerified){
    if (sim.m_SIMState == SIMState.PinRequired && sim.m_PinState != PinState.RequirePUK) {
      //if SIMState.PinRequired, that means sim.m_nPinRemainingTimes > 0
      mStateHandler = new PinCodeHandler(sim.m_nPinRemainingTimes);
      mStateHandler.addNextStateHandler(new WiFiSSIDHandler(false));
    }else if ((sim.m_SIMState == SIMState.PukRequired || sim.m_PinState == PinState.RequirePUK) && sim.m_SIMState != SIMState.PukTimesUsedOut ) {
    	if (mStateHandler != null) {
				mStateHandler = null;
			}
			mStateHandler = new PukCodeHandler(sim.m_nPukRemainingTimes);
			mStateHandler.addNextStateHandler(new WiFiSSIDHandler(false));
		} else {
      mStateHandler = new WiFiSSIDHandler(true);
   }        
      
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
    //Log.d(TAG, "click Tag:"+v.getTag());
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
  private void removePINCodePUKCodeSetting() {
  	
    StateHandler handler = mStateHandler;
    
    if (handler == null || handler.mIsHead == false || (handler.mState != State.PIN_CODE && handler.mState != State.PUK_CODE)) {
      Log.e(TAG, "Current state is not PIN code or it is not the head sate.");
      return;
    }
    mStateHandler.clearOtherTextListen(mStateHandler);
    handler.mPreviousHandler = null;
    mStateHandler = handler.mNextHandler;
    handler.mNextHandler = null;

    if (mStateHandler != null) {
      mStateHandler.mPreviousHandler = null;
      mStateHandler.mIsHead = true;
      mStateHandler.setupViews();
      //Log.d(TAG, "removePINCodePUKCodeSetting:"+ mStateHandler.mIsHead);
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    registerReceiver(mReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PUK_REQUEST));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));  
    registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
    registerReceiver(mReceiver, new IntentFilter(MessageUti.USER_HEARTBEAT_REQUEST));
  	registerReceiver(mReceiver, new IntentFilter(MessageUti.USER_COMMON_ERROR_32604_REQUEST));
      
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
    if (mPUKErrorDialog != null) 
    	mPUKErrorDialog.destroyDialog();
    if (mLoginDialog != null)
      mLoginDialog.destroyDialog();
    if (mConfirmDialog != null)
      mConfirmDialog.destroyDialog();
    if (mAutoLoginDialog != null)
      mAutoLoginDialog.destroyDialog();
    if (forceLoginSelectDialog != null) {
			forceLoginSelectDialog.destroyDialog();
		}
    mBusinessMgr = null;
  }
  
  class QSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      int result = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
                                     BaseResponse.RESPONSE_OK);
      String error = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
      if (error == null) {
				error = "Error";
			}
      Log.d(TAG, "Quick Setup receive broadcase " + action);
      boolean ok = BaseResponse.RESPONSE_OK == result && 0 == error.length();
      if (action.equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
        // If WiFi disconnect router, go to Refresh WiFi activity.
        if (!DataConnectManager.getInstance().getCPEWifiConnected()) {
          context.startActivity(new Intent(context, RefreshWifiActivity.class));
          // finish();
        }
      } else if (action.equals(MessageUti.USER_HEARTBEAT_REQUEST) || action.equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)) {
      	if (result == BaseResponse.RESPONSE_OK && error.equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
      		kickoffLogout();
				}
			}else if (action.equals(MessageUti.USER_LOGOUT_REQUEST)) {
  			if (BaseResponse.RESPONSE_OK == result && error.length() == 0){
  				MainActivity.m_blLogout = false;
  				MainActivity.m_blkickoff_Logout = false;
  			}
  			handleLoginError(R.string.qs_title, R.string.login_kickoff_logout_successful, true, false);
			}else if (action.equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
        if (BaseResponse.RESPONSE_OK == result && 0 == error.length()) {
          /*
           * Unfortunately, when orientation changes, activity will receive this message again.
           * If user change the wifi ssid or password, they will override , especially in
           * summary page, orientation changes may occur wifi ssid & password.
           */
          if (mWiFiSSID != null && mWiFiPasswd != null) {
            return;
          }
          mWiFiSSID = mBusinessMgr.getSsid();
          mWiFiPasswd = mBusinessMgr.getWifiPwd();        
          mSecurityMode = mBusinessMgr.getSecurityMode();
          
          if(mStateHandler == null || mEnterText.getText().length() > 0 ||
              mBusinessMgr.getLoginStatus() != UserLoginStatus.login) {
            return;
          }  
          if (mStateHandler.getState() == State.WIFI_SSID && mWiFiSSID != null) {
            //mEnterText.setHint(mWiFiSSID);
            mEnterText.setText(mWiFiSSID);
          } else if (mStateHandler.getState() == State.WIFI_PASSWD && mWiFiPasswd != null) {
            //mEnterText.setHint(mWiFiPasswd);
//          	if (mSecurityMode == SecurityMode.Disable) {
//          		//mWiFiPasswd = "";
//          		mEnterText.setEnabled(false);
//						}else {
//							mEnterText.setEnabled(true);
//						}
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
          removePINCodePUKCodeSetting();
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
                //removePINCodePUKCodeSetting();
                //finishQuickSetup(true);                
              	buildStateHandlerChain(true);
              }
            
            });
            mConfirmDialog.showDialog(getString(R.string.qs_item_pin_code), 
                    getString(R.string.pin_error_waring_title));
          }
        }
      }else if (action.equals(MessageUti.SIM_UNLOCK_PUK_REQUEST)) {
      	 if(mStateHandler == null) {
           return;
         } 
         if (BaseResponse.RESPONSE_OK == result && error.length() == 0) {
           removePINCodePUKCodeSetting();
         } else {
           //PUK unlock
           if (mStateHandler.retryTimes() > 0) {
             mPUKErrorDialog = ErrorDialog.getInstance(mContext);

             mPUKErrorDialog.showDialog(getString(R.string.puk_error_waring_title),
                 new OnClickBtnRetry() {

                   @Override
                   public void onRetry() {
                     mStateHandler.retryInput();
                   }
                 });
           } else {
             // PUK LOCK
             mConfirmDialog = CommonErrorInfoDialog.getInstance(mContext);
             mConfirmDialog.setCancelCallback(new OnClickConfirmBotton() {
               @Override
               public void onConfirm() {
                 removePINCodePUKCodeSetting();
                 //finishQuickSetup(true);                
               }
             });
             mConfirmDialog.showDialog(getString(R.string.qs_puk_code_title), 
                     getString(R.string.puk_error_waring_title));
           }
         }
				
			}
    }
  }  
  
  public void kickoffLogout() {
		UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
		if (m_loginStatus != null && m_loginStatus == UserLoginStatus.Logout) {
			MainActivity.setKickoffLogoutFlag(true);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.USER_LOGOUT_REQUEST, null);
		}
	}
  
  private void setWiFiConfigure(String ssid, String passwd){
    DataValue data = new DataValue();
    SecurityMode mode = mBusinessMgr.getSecurityMode();
    int encrypt;
    if (SecurityMode.Disable == mode) {
    	// wpa/wpa2 : 4,	encrypt auto:2
      encrypt = 2;
      mode = SecurityMode.WPA_WPA2;
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
    LOGIN_ERROR, PIN_CODE, PUK_CODE, WIFI_SSID, WIFI_PASSWD, SUMMARY, FINISH;    
  }
  
  abstract class StateHandler implements TextWatcher{
    private State mState;
    protected StateHandler mPreviousHandler;
    protected StateHandler mNextHandler;
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
    public void clearOtherTextListen(StateHandler handler) {
    	if (handler == null) {
				return;
			}
    	mEnterText.removeTextChangedListener(this);
    	StateHandler head = handler;
      StateHandler prev = head.mPreviousHandler;
      while (prev != null) {
      	mEnterText.removeTextChangedListener(prev);
      	head = prev;
        prev = head.mPreviousHandler;
      }
      head = handler;
      StateHandler next = head.mNextHandler;
      while (next != null) {
      	mEnterText.removeTextChangedListener(next);
      	head = next;
      	next = head.mNextHandler;
      }
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
      StateHandler next = tail.mNextHandler;
      while (next != null) {
        tail = next;
        next = tail.mNextHandler;
      }
      return tail;
    }
    
    public State getState() {
      return mState;
    }
    
    /*
     * get StateHandler to handle state
     */
    public StateHandler getStateHandler(State state){
      StateHandler head = goHead();
      while (head != null) {
        if (head.mState == state)
          return head;
        head = head.mNextHandler;
      }
      return null;
    }
    
    public void quickPlay(StateHandler handler) {
      if (handler == null)
        return;
      /*
       * check handler is after us in handler chain.
       */
      boolean after = false;
      StateHandler cursor = this;
      while (cursor != null) {
        if (cursor == handler) {
          after = true;
          break;
        }
        cursor = cursor.mNextHandler;
      }
      
      if (after == false)
        return;
      
      cursor = this;
      while (cursor != null) {
        cursor.setupViews();
        if (cursor == handler)
          return;
        cursor = cursor.mNextHandler;
      }
    }
    
    @Override   
    public void onTextChanged(CharSequence s, int start, int count, int after) {}   
   
    @Override   
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {} 

    @Override   
    public void afterTextChanged(Editable s) {
    	if(this instanceof WiFiSSIDHandler || this instanceof PinCodeHandler){
    		Log.d(TAG, "afterTextChanged:"+s.toString());
    		boolean isNext = textChangeAction(s, this.mInputMax, this.mInputMin, mEnterText);
    		if (isNext) {
    			mSkipSetup = false;
          mNavigatorRight.setText(R.string.next);
				}else{
					mSkipSetup = true;
	        mNavigatorRight.setText(R.string.skip);
				}
    	}else if (this instanceof PukCodeHandler || this instanceof PinCodeNew || this instanceof PinCodeConfirm) {
    		if (this instanceof PukCodeHandler) {
    			pukValState = textChangeAction(s, this.mInputMax, this.mInputMin, pukCodeText);
    			mSkipSetup = pukValState ? false : true;
				}else if (this instanceof PinCodeNew) {
					newPinValState = textChangeAction(s, this.mInputMax, this.mInputMin, newPinCodeText);
					mSkipSetup = newPinValState ? false : true;
				}else if ( this instanceof PinCodeConfirm) {
					confirmPinState = textChangeAction(s, this.mInputMax, this.mInputMin, confirmPinCodeText);
					mSkipSetup = confirmPinState ? false : true;
				}
    		
    		Log.d(TAG, "pukTextValSate:"+confirmPinState + newPinValState + pukValState);
    		if (confirmPinState && newPinValState && pukValState) {
          mNavigatorRight.setText(R.string.next);
				}else {
	        mNavigatorRight.setText(R.string.skip);
				}
    		Log.d(TAG, "this.mSkipSetup:"+mSkipSetup);
			}else if(this instanceof WiFiPasswdHandler){
					boolean isNext = textChangeAction(s, this.mInputMax, this.mInputMin, mEnterText);
					boolean isVal = checkPassword(s.toString());
				//	Log.d(TAG, "------------------s:"+s.toString());
	    		if (isNext && isVal) {
	    			mSkipSetup = false;
	          mNavigatorRight.setText(R.string.next);
					}else{
						mSkipSetup = true;
		        mNavigatorRight.setText(R.string.skip);
					}
				}
    }
    
  	protected boolean checkPassword(String strPsw) {
  		int nLength = strPsw.length();
  		boolean bCorrect = true;
  		if(mSecurityMode == SecurityMode.WEP) {
  			if(strPsw == null || !(nLength == 5 || nLength == 13 || nLength == 10 || nLength == 26)) {
  				bCorrect = false;
  			}else{
  				if(nLength == 5 || nLength == 13) {
  					for(int i = 0;i < nLength;i++) {
  						char c = strPsw.charAt(i);
  						if(!(c > 32 && c < 127 && c != 34 &&  c != 38 &&  c != 58 &&  c != 59 &&  c != 92)) {
  							bCorrect = false;
  							break;
  						}
  					}
  				}

  				if(nLength == 10 || nLength == 26) {
  					for(int i = 0;i < nLength;i++) {
  						char c = strPsw.charAt(i);
  						if(!(c >= '0' && c <= '9' || c >= 'a' &&  c <= 'z' ||  c >= 'A' &&  c <= 'Z')) {
  							bCorrect = false;
  							break;
  						}
  					}
  				}
  			}
  			if(bCorrect == true){
  				return true;
  			}else{
  				//m_strErrorInfo = getString(R.string.setting_wep_password_error_prompt);
  				return false;
  			}
  		}else{
  			if(strPsw == null || !(nLength > 7 && nLength < 64)) {
  				bCorrect = false;
  			}else{
  				for(int i = 0;i < nLength;i++) {
  					char c = strPsw.charAt(i);
  					if(!(c > 32 && c < 127 && c != 34 &&  c != 38 &&  c != 58 &&  c != 59 &&  c != 92)) {
  						bCorrect = false;
  						break;
  					}
  				}
  			}

  			if(bCorrect == true){
  				return true;
  			}else{
  				//m_strErrorInfo = getString(R.string.setting_wpa_password_error_prompt);
  				return false;
  			}
  		}
  	}
    
    protected boolean textChangeAction(Editable s,int iptMax,int iptMin,ClearEditText editText){
    	Log.d(TAG, "getState():"+this.getState());
      int len = s.length();
      if (len > iptMax) {
        int nSelStart = editText.getSelectionStart();
        int nSelEnd = editText.getSelectionEnd();
        if ((len-1) <= iptMax && nSelStart > 0) {
        	s.delete(nSelStart - 1, nSelStart);
				}else {
					s.delete(iptMax, len);
				}
       
        // 保持光标原先的位置，而
        // mEditText.setText(s)会让光标跑到最前面，
        // 就算是再加mEditText.setSelection(nSelStart)
        // 也不起作用
        len = s.length();
        if (nSelStart == 0 && len > 0) {
        	editText.setSelection(len);
				}else {
					editText.setTextKeepState(s);
				}
        Log.d(TAG, " mEnterText.getSelectionStart():"+ editText.getSelectionStart());
        																
        return true;
      } else if ( (len >= iptMin && iptMax > iptMin) || (iptMax == iptMin && iptMin == len)){
      	return true;
      } else {
        return false;
      }
    }
    abstract public void setupViews();
    public boolean storeSetting() {return true;}

    public int retryTimes(){return 0;}
    public boolean retryInput(){return true;}
    public String getTitleText() {return null;}
    public String getMessageText() { return null;}
  }
 
  /*
   * when other user login or login timeout
   */
  class LoginExceptionHandler extends StateHandler {
    private final String mTitle;
    private final String mMessage;
    LoginExceptionHandler(String title, String message) {
      super(State.LOGIN_ERROR, 0, 0);
      mTitle = title;
      mMessage = message;
      mIsHead = true;
    }

    @Override
    public void setupViews() {
    	setViewsVisibility(true, false, false, false, false);
      mSetupTitle.setText(mTitle);
      mPromptText.setText(mMessage);

      mNavigatorRight.setText(R.string.skip);
      mNavigatorRight.setOnClickListener(new OnClickListener(){

        @Override
        public void onClick(View v) { 
          finishQuickSetup(true);       
        }
        
      });
    }    

    public String getTitleText() {return mTitle;}
    public String getMessageText() { return mMessage;}
  }
  
  /*
   * When SIM PIN check is enabled, User can unlock SIM PIN in Quick Setup 
   * step, or skip. If user skip this step, he/she should unlock PIN in some
   * step that need check PIN later. 
   */
  class PinCodeHandler extends StateHandler {
    private int mPINTryTimes;
    PinCodeHandler(int tryTimes) {
      super(State.PIN_CODE, 4, 8);
      mIsHead = true;
      mPINTryTimes = tryTimes;
    }

    @Override
    public void setupViews() {
    	setViewsVisibility(true, false, false, true, false);
      mSetupTitle.setText(getString(R.string.qs_item_pin_code));
      mPromptText.setText(getString(R.string.qs_pin_code_prompt, mPINTryTimes));
      mNavigatorLeft.setClickable(false);
      mNavigatorRight.setText(R.string.skip); 
      mEnterText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      mEnterText.getText().clear();
      clearOtherTextListen(this);
      mEnterText.addTextChangedListener(this);
    }
    
    public int retryTimes(){return mPINTryTimes;}
    
    public boolean retryInput(){
	//will add a timeout later
    	SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
      mPINTryTimes = sim.m_nPinRemainingTimes;
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
      String strTitle = mContext.getString(R.string.IDS_PIN_LOCKED);
  		String strMsg = mContext.getString(R.string.IDS_PIN_CHECK_PIN_PROGRESS);
  		strMsg += "...";
	    m_progress_dialog = ProgressDialog.show(mContext, strTitle, strMsg, true, false);  
      return false;
    }

  }
  
  class PukCodeHandler extends StateHandler {
    private int mPUKTryTimes;
    PukCodeHandler(int tryTimes) {
      super(State.PUK_CODE, 8,8);
      mIsHead = true;
      mPUKTryTimes = tryTimes;
    }

    @Override
    public void setupViews() {
    	setViewsVisibility(true, false, true,false, false);
      mSetupTitle.setText(getString(R.string.qs_puk_code_title));
      mPromptText.setText(getString(R.string.qs_puk_code_prompt, mPUKTryTimes));
      mNavigatorLeft.setClickable(false);
      mNavigatorRight.setText(R.string.skip); 
      pukCodeText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      newPinCodeText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      confirmPinCodeText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      pukCodeText.getText().clear();
      newPinCodeText.getText().clear();
      confirmPinCodeText.getText().clear();
      clearOtherTextListen(this);
      pukCodeText.addTextChangedListener(this);
      newPinCodeText.addTextChangedListener(new PinCodeNew(State.PUK_CODE, 4, 8));
      confirmPinCodeText.addTextChangedListener(new PinCodeConfirm(State.PUK_CODE, 4, 8));
    }
    
    public int retryTimes(){return mPUKTryTimes;}
    
    public boolean retryInput(){
    	SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
    	mPUKTryTimes = sim.m_nPukRemainingTimes;
      mEnterText.getText().clear();
      mPromptText.setText(getString(R.string.qs_pin_code_prompt, mPUKTryTimes));
      mNavigatorRight.setText(R.string.skip);
      mSkipSetup = true;
      return true;
    }

    @Override
    public boolean storeSetting() {
    	 Log.d(TAG, "mSkipSetup:"+mSkipSetup);
      if(mSkipSetup)
        return true;

      DataValue data = new DataValue();
      data.addParam("puk", pukCodeText.getText().toString());
      data.addParam("pin", newPinCodeText.getText().toString());
      data.addParam("confirmPin", confirmPinCodeText.getText().toString());
      Log.d(TAG, "pin == confirmPIN:"+data.getParamByKey("pin").equals(data.getParamByKey("confirmPin")));
      if (data.getParamByKey("pin").equals(data.getParamByKey("confirmPin"))) {
      	mBusinessMgr.sendRequestMessage(MessageUti.SIM_UNLOCK_PUK_REQUEST, data);
				String strTitle = mContext.getString(R.string.IDS_PUK_LOCKED);
		    String strMsg = mContext.getString(R.string.IDS_PIN_CHECK_PUK_PROGRESS);
		    strMsg += "...";
		    m_progress_dialog = ProgressDialog.show(mContext, strTitle, strMsg, true, false);  
			}else {
				mConfirmDialog = CommonErrorInfoDialog.getInstance(mContext);
        mConfirmDialog.setCancelCallback(new OnClickConfirmBotton() {
        @Override
        public void onConfirm() {
           //removePINCodePUKCodeSetting();
           //finishQuickSetup(true);                
         }
       });
       mConfirmDialog.showDialog(getString(R.string.qs_puk_code_title), 
               getString(R.string.puk_prompt_str));
			}
      
      return false;
    }
  }
  class PinCodeNew extends StateHandler{

  	PinCodeNew(State state, int inputMin, int inputMax) {
			super(state, inputMin, inputMax);
		}

		@Override
		public void setupViews() {
		}
  }
  class PinCodeConfirm extends StateHandler{

		PinCodeConfirm(State state, int inputMin, int inputMax) {
			super(state, inputMin, inputMax);
		}

		@Override
		public void setupViews() {}
  }
  
  class WiFiSSIDHandler extends StateHandler {
    WiFiSSIDHandler(boolean isHead){
      super(State.WIFI_SSID, 1, 32);
      mIsHead = isHead;
    }

    @Override
    public void setupViews() {
    	setViewsVisibility(true, false, false, true, false);
      mSetupTitle.setText(getString(R.string.qs_item_wifi_ssid));
      mPromptText.setText(getString(R.string.qs_wifi_ssid_prompt));
      if (mIsHead) {
        mNavigatorLeft.setVisibility(View.INVISIBLE);
        mNavigatorLeft.setClickable(false);
      } else {
        mNavigatorLeft.setVisibility(View.VISIBLE);
        mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      }
      mEnterText.setInputType(InputType.TYPE_CLASS_TEXT);
      mEnterText.getText().clear();
      clearOtherTextListen(this);
      mEnterText.addTextChangedListener(this);      
      //mEnterText.setHint(mWiFiSSID);
      if (mWiFiSSID != null) {
        mEnterText.setText(mWiFiSSID);
        if (mEnterText.getSelectionStart() == 0 && mWiFiSSID.length() > 0) {
        	mEnterText.setSelection(mWiFiSSID.length());
				}else {
					mEnterText.setTextKeepState(mWiFiSSID);
				}
        //Log.d(TAG, "mEnterText.getSelectionStart()-wifi:"+mEnterText.getSelectionStart());
      }
      mNavigatorRight.setText(R.string.skip);
    }

    @Override
    public boolean storeSetting() {
      if(mSkipSetup == false) {
      //setWiFiConfigure(mEnterText.getText().toString(), mWiFiPasswd);
        String enter = mEnterText.getText().toString();
        if (!enter.equals(mWiFiSSID)) {
          mWiFiSSID = enter;
        }
        //mWiFiSSID = null;
      } else {
        /*if user enter new ssid, goto summary, and back to ssid page,
         * clear input, and skip. In this situtation, fetch the origin 
         * SSID.
         */
        mWiFiSSID = mBusinessMgr.getSsid();
      }

      return true;
    }
    
    @Override   
    public void onTextChanged(CharSequence s, int start, int count, int after) {
    }
  }
  
  class WiFiPasswdHandler extends StateHandler {
  	//wpa 
    WiFiPasswdHandler(){
      super(State.WIFI_PASSWD, 8, 63);   
    }

    @Override
    public void setupViews() {
    	setViewsVisibility(true,true,false,true,false);
    //	Log.d(TAG, "mSecurityMode:---"+mSecurityMode);
    	 if(mSecurityMode == SecurityMode.WEP){
    		 mPromptText.setText(getString(R.string.qs_wifi_wep_passwd_prompt));
    		 mInputMax = 26;
    		 mInputMin = 5;
    	 }else {
    		 mPromptText.setText(getString(R.string.qs_wifi_passwd_prompt));
			}
      mSetupTitle.setText(getString(R.string.qs_item_wifi_passwd));
      mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      //replace  TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
      mEnterText.setInputType(InputType.TYPE_CLASS_TEXT);
      mEnterText.getText().clear();
      clearOtherTextListen(this);
      mEnterText.addTextChangedListener(this);
      //mEnterText.setHint(mWiFiPasswd);
      if(mWiFiPasswd != null){
        mEnterText.setText(mWiFiPasswd);
  //      mEnterText.setHint(mWiFiPasswd);
        if (mEnterText.getSelectionStart() == 0 && mWiFiPasswd.length() > 0) {
        	mEnterText.setSelection(mWiFiPasswd.length());
  			}else {
  				mEnterText.setTextKeepState(mWiFiPasswd);
  			}
      }
    	//Log.d(TAG, "mEnterText.getSelectionStart()-wifi:"+mEnterText.getSelectionStart());
//      if(mSecurityMode == SecurityMode.Disable){
//      	mEnterText.setEnabled(false);
//      	mEnterText.setClearIconVisible(false);
//      }else {
//				mEnterText.setEnabled(true);
//				mEnterText.setClearIconVisible(true);
//			}
      mNavigatorRight.setText(R.string.skip); 
    }

    @Override
    public boolean storeSetting() {
      if(mSkipSetup == false) {
        //setWiFiConfigure(mWiFiSSID, mEnterText.getText().toString());
        String enter = mEnterText.getText().toString();
        if (!enter.equals(mWiFiPasswd)) {
          mWiFiPasswd = enter;
        }
        //mWiFiPasswd = null;
      } else {
        /*if user enter new ssid, goto summary, and back to ssid page,
         * clear input, and skip. In this situtation, fetch the origin 
         * SSID.
         */
        mWiFiPasswd = mBusinessMgr.getWifiPwd(); 
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
    	setViewsVisibility(true,true,false,false,true);
      mNavigatorRight.setText(R.string.finish);
      mNavigatorLeft.setOnClickListener(QuickSetupActivity.this);
      mWiFiSSIDTextView.setText(getString(R.string.qs_wifi_ssid, mWiFiSSID));
      mWiFiPasswdTextView.setText(getString(R.string.qs_wifi_passwd, mWiFiPasswd)); 
      clearOtherTextListen(this);
    }

    @Override
    public boolean storeSetting() {
      CPEConfig.getInstance().setQuickSetupFlag();

      boolean mSendRequest = false;//If user not change settings, do not send request.
      
      if (mWiFiSSID != null && mWiFiPasswd != null && 
          (!mWiFiSSID.equals(mBusinessMgr.getSsid()) || 
              !mWiFiPasswd.equals(mBusinessMgr.getWifiPwd()))) {
        mSendRequest = true;
      }
      
      if (mSendRequest) {
        setWiFiConfigure(mWiFiSSID, mWiFiPasswd);
      } else {
        finishQuickSetup(false);
      }
      return true;
    }
  }
}
