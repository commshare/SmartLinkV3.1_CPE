package com.alcatel.wifilink.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.model.WanConnectStatusModel;
import com.alcatel.wifilink.common.CommonUtil;
import com.alcatel.wifilink.common.ENUM;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SettingAboutActivity extends BaseActivity implements OnClickListener{

    private TextView m_tv_title = null;
    private ImageButton    m_ib_title_back;
    private TextView       m_tv_title_back;
    private TextView       mAppVersionTv;
    private RelativeLayout mWebsiteRl;
    private RelativeLayout mUpgradeRl;
    private RelativeLayout mAppContainerRl;
    private TextView       mUpgradeFlagTv;
    private Dialog mUpgradeDialog;
    private boolean m_blHasNewApp = false;//是否为更新版本的事件
    private RelativeLayout mWaittingContainer;
    private RelativeLayout mUpgradeContainer;
    private TextView mVersion;
    private final int MSG_GET_NEW_VERSION = 10000000;
    private TextView mUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting_about);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
        //control title bar
        controlTitlebar();
        //create controls
        createControls();
        //app version
        String strVersionString = BusinessManager.getInstance().getAppVersion();
        String strTemp = getString(R.string.setting_item_link_app_version) + " " + strVersionString;
        mAppVersionTv.setText(strTemp);

    }

    private void controlTitlebar(){
        m_tv_title = (TextView)findViewById(R.id.tv_title_title);
        m_tv_title.setText(R.string.setting_about);
        m_ib_title_back = (ImageButton)findViewById(R.id.ib_title_back);
        m_tv_title_back = (TextView)findViewById(R.id.tv_title_back);
        m_ib_title_back.setOnClickListener(this);
        m_tv_title_back.setOnClickListener(this);
    }

    private void createControls(){
        mAppVersionTv = (TextView) findViewById(R.id.setting_about_link_app_desc);
        mAppContainerRl = (RelativeLayout) findViewById(R.id.setting_about_link_app_container);

        mWebsiteRl = (RelativeLayout) findViewById(R.id.setting_about_link_website_container);

        mUpgradeFlagTv = (TextView) findViewById(R.id.flagUpgrade);
        Intent intent = getIntent();
        int upgradeStatus = intent.getIntExtra("upgradeStatus", 0);
        if (ENUM.EnumDeviceCheckingStatus.build(upgradeStatus) == ENUM.EnumDeviceCheckingStatus.DEVICE_NEW_VERSION){
            mUpgradeFlagTv.setVisibility(View.VISIBLE);
        }else{
            mUpgradeFlagTv.setVisibility(View.GONE);
        }
        mUpgradeRl = (RelativeLayout) findViewById(R.id.setting_about_link_upgrade_container);

        //set click event
        mAppContainerRl.setOnClickListener(this);
        mWebsiteRl.setOnClickListener(this);
        mUpgradeRl.setOnClickListener(this);
    }

    @Override
    public void
    onClick(View v) {
        int nId = v.getId();
        switch (nId) {
            case R.id.ib_title_back:
            case R.id.tv_title_back:
                SettingAboutActivity.this.finish();
                break;
            case R.id.setting_about_link_app_container:
                String strTemp = "http://" + BusinessManager.getInstance().getServerAddress();
                CommonUtil.openWebPage(this, strTemp);
            case R.id.setting_about_link_website_container:
                CommonUtil.openWebPage(this, "http://www.alcatelonetouch.com");
                break;
            case R.id.setting_about_link_upgrade_container:
                showUpgradeDialog();
                break;
            default:
                break;
        }
    }

    private void showUpgradeDialog() {
        mUpgradeDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mUpgradeDialog.setCanceledOnTouchOutside(false);
        mUpgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout deleteDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_upgrade, null);

        mWaittingContainer = (RelativeLayout) deleteDialogLLayout.findViewById(R.id.upgrade_waiting);
        mWaittingContainer.setVisibility(View.VISIBLE);
        mUpgradeContainer = (RelativeLayout) deleteDialogLLayout.findViewById(R.id.upgrade_container);
        mUpgradeContainer.setVisibility(View.GONE);

        ImageView cancel = (ImageView) deleteDialogLLayout.findViewById(R.id.upgrade_cancel);
        mVersion = (TextView) deleteDialogLLayout.findViewById(R.id.upgrade_version);
        mUpgrade = (TextView) deleteDialogLLayout.findViewById(R.id.upgrade_confirm);

        mUpgradeDialog.setContentView(deleteDialogLLayout);
        mUpgrade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewVersion();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissUpgradeDialog();
            }
        });
        mUpgradeDialog.show();

        //获取最新版本信息
        getNewVersion();
        //        Tools.setDialogWidthAndHeight(deleteDialog, 0.4, mActivity);
    }

    private void getNewVersion() {
        WanConnectStatusModel status = BusinessManager.getInstance().getWanConnectStatus();
        ENUM.ConnectionStatus result = status.m_connectionStatus;
        if (result != ENUM.ConnectionStatus.Connected) {
            showCheckAppWaiting(false);
            mVersion.setText(R.string.setting_upgrade_no_connection);
            mUpgrade.setVisibility(View.GONE);
        }else {
            mUpgrade.setVisibility(View.VISIBLE);
            onBtnAppCheck();
        }
    }

    private void dismissUpgradeDialog() {
        if (mUpgradeDialog != null && mUpgradeDialog.isShowing()) {
            mUpgradeDialog.dismiss();
        }
    }

    private void onBtnAppCheck(){
        if (m_blHasNewApp) {
            if (hasGooglePlayAccount()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.alcatel.smartlinkv3"));
                startActivity(intent);
            }else {
                Toast.makeText(this, R.string.setting_upgrade_config_google_play, Toast.LENGTH_LONG).show();
            }
        }else {
            checkNewVersion();
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            showCheckAppWaiting(false);
            switch (msg.what) {
                case MSG_GET_NEW_VERSION:
                    String strNewVersion = msg.getData().getString("version");
                    String currVer = BusinessManager.getInstance().getAppVersion();
                    mVersion.setText(getString(R.string.setting_upgrade_new_app_version)+
                            msg.getData().getString("version"));
                    if(currVer.compareToIgnoreCase(strNewVersion) < 0)
                    {
                        mUpgrade.setText(R.string.setting_upgrade);
                        m_blHasNewApp = true;
                    }else {
                        mVersion.setText(R.string.setting_upgrade_no_new_version);
                        mUpgrade.setText(R.string.setting_upgrade_btn_check);
                        m_blHasNewApp = false;
                    }
                    break;

                default:
                    mVersion.setText(R.string.setting_upgrade_check_failed);
                    mUpgrade.setText(R.string.setting_upgrade_btn_check);
                    m_blHasNewApp = false;
                    break;
            }
        }
    };

    private void showCheckAppWaiting(boolean isWait) {
        if (isWait){
            mWaittingContainer.setVisibility(View.VISIBLE);
            mUpgradeContainer.setVisibility(View.GONE);
        }else {
            mWaittingContainer.setVisibility(View.GONE);
            mUpgradeContainer.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasGooglePlayAccount(){
        boolean blHas = false;
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccounts();
        for (Account account:accounts) {
            Log.d("Account", "account.name="+account.name);
            Log.d("Account", "account.type="+account.type);
            if (account.type.equalsIgnoreCase("com.google")) {
                blHas = true;
                break;
            }
        }
        return blHas;
    }

    private void checkNewVersion(){

        showCheckAppWaiting(true);
        //when checking new version, clear last checking result.
        mVersion.setText("");

        new Thread() {
            public void run() {
                HttpParams myParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(myParams, 5000);
                HttpConnectionParams.setSoTimeout(myParams, 5000);
                HttpClient httpclient = new DefaultHttpClient(myParams);

                HttpPost post = new HttpPost("https://play.google.com/store/apps/details?id=com.alcatel.smartlinkv3");

                HttpResponse response;
                int nMsgId = 0;
                String strNewVersionString = "";
                try {
                    response = httpclient.execute(post);
                    int nStatusCode = response.getStatusLine().getStatusCode();
                    if (nStatusCode == HttpStatus.SC_OK) {
                        String strRes = EntityUtils.toString(response.getEntity(), "utf-8");
                        int nPos = strRes.indexOf("softwareVersion");
                        if (nPos != -1) {
                            Log.e("@@@", strRes);
                            strRes = strRes.substring(nPos);
                            int start = strRes.indexOf(">");
                            int end = strRes.indexOf("<");
                            strRes = strRes.substring(start+1, end);
                            strRes = strRes.trim();
                            Log.e("@@@", strRes);
                            nMsgId = MSG_GET_NEW_VERSION;
                            strNewVersionString = strRes;
                        }

                    }
                }  catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = nMsgId;
                Bundle data = new Bundle();
                data.putString("version", strNewVersionString);
                msg.setData(data);
                handler.sendMessage(msg);

            }
        }.start();
    }

    @Override
    protected void onResume() {
        m_bNeedBack = false;
        super.onResume();
    }
}
