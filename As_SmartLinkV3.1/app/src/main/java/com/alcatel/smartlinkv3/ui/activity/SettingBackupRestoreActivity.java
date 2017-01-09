package com.alcatel.smartlinkv3.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.EnumRestoreErrorStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cybergarage.util.FileUtil;
import org.cybergarage.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingBackupRestoreActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private ProgressBar m_pbWaitingBar=null;
	private boolean  m_bRestore = false;
    private TextView mBackupTv;
    private TextView mRestoreTv;
    private Dialog   mUpgradeDialog;
    private EditText mBackupNameEt;
    private TextView mBackupConfirm;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_backup);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_3);
		prepareTitlebar();
		createControls();
		ShowWaiting(false);
		
		
	}

	private void prepareTitlebar(){
		m_tv_titleTextView = (TextView)findViewById(R.id.tv_title_title);
		m_tv_titleTextView.setText(R.string.setting_backup);
		//back button and text
				m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
				m_tv_back = (TextView)findViewById(R.id.tv_title_back);
				m_ib_back.setOnClickListener(this);
				m_tv_back.setOnClickListener(this);
	}
	
	private void createControls(){
        mBackupTv = (TextView) findViewById(R.id.device_backup_backup);
        mRestoreTv = (TextView) findViewById(R.id.device_backup_restore);
        mBackupTv.setOnClickListener(this);
        mRestoreTv.setOnClickListener(this);
		m_pbWaitingBar=(ProgressBar)findViewById(R.id.pb_backup_waiting_progress);
	}

	private void ShowWaiting(boolean blShow){
		if (blShow) {
			m_pbWaitingBar.setVisibility(View.VISIBLE);
		}else {
			m_pbWaitingBar.setVisibility(View.GONE);
		}
        mBackupTv.setClickable(!blShow);
		if(m_bRestore)
		{
            mRestoreTv.setClickable(false);
		}
		else
		{
            mRestoreTv.setClickable(!blShow);
		}
		
		m_ib_back.setEnabled(!blShow);
		m_tv_back.setEnabled(!blShow);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			SettingBackupRestoreActivity.this.finish();
			break;

		case R.id.device_backup_backup:
            showBackupDialog();
			break;
			
		case R.id.device_backup_restore:
			onBtnRestore();
			ShowWaiting(true);
			break;
		default:
			break;
		}
	}

    private void showBackupDialog() {
        mUpgradeDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mUpgradeDialog.setCanceledOnTouchOutside(false);
        mUpgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout deleteDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_backup, null);

        ImageView cancel = (ImageView) deleteDialogLLayout.findViewById(R.id.backup_cancel);
        mBackupNameEt = (EditText) deleteDialogLLayout.findViewById(R.id.backup_name);

        //处理EditText
        mBackupNameEt.setText("");
        StringUtil.setEditTextInputFilter(mBackupNameEt);

        mBackupConfirm = (TextView) deleteDialogLLayout.findViewById(R.id.backup_confirm);

        mUpgradeDialog.setContentView(deleteDialogLLayout);

        //确认备份
        mBackupConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mBackupNameEt.getText())){
                    Toast.makeText(SettingBackupRestoreActivity.this,
                            getString(R.string.setting_backup_waring), Toast.LENGTH_SHORT).show();
                }else{
                    dismissUpgradeDialog();
                    onBtnBackup();
                    ShowWaiting(true);
                }
            }
        });

        //取消按钮
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissUpgradeDialog();
            }
        });
        mUpgradeDialog.show();
    }

    private void dismissUpgradeDialog() {
        if (mUpgradeDialog != null && mUpgradeDialog.isShowing()) {
            mUpgradeDialog.dismiss();
        }
    }

    private void onBtnBackup(){
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_APP_BACKUP, null);
	}
	
	private void onBtnRestore(){
        mRestoreTv.setClickable(false);
		m_bRestore = true;
		BusinessMannager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP, null);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_APP_BACKUP));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP));
		registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		
		SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
		if (SIMState.Accessable != sim.m_SIMState) {
            mBackupTv.setClickable(false);
            mRestoreTv.setClickable(false);
            Toast.makeText(getApplicationContext(), getString(R.string.Home_no_sim), Toast.LENGTH_SHORT).show();
		}else {
            mBackupTv.setClickable(true);
            mRestoreTv.setClickable(true);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onBroadcastReceive(context, intent);
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_APP_BACKUP)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
//			String strTost = getString(R.string.setting_backup_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
                //获取配置文件
                String strPath = Environment.getExternalStorageDirectory().getPath()
                        + getString(R.string.setting_backup_path);
                //生成文件目录
                FileUtil.makeRootDirectory(strPath);
                //生成文件
                final String localFileName = strPath + mBackupNameEt.getText() + ".bin";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (downLoadConfig(localFileName)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SettingBackupRestoreActivity.this, getString(R.string.setting_backup_success), Toast.LENGTH_SHORT).show();
                                    ShowWaiting(false);
                                }
                            });
                        }else{
                            //删除错误的文件
                            FileUtil.deleteFile(localFileName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SettingBackupRestoreActivity.this, getString(R.string.setting_backup_failed), Toast.LENGTH_SHORT).show();
                                    ShowWaiting(false);
                                }
                            });
                        }
                    }
                }).start();
			}
			
		}
		
		if(intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP)){
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			String strTost = getString(R.string.setting_restore_failed);
			if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
				RestoreError info = BusinessMannager.getInstance().getRestoreError();
				int nErrorStatus = info.getRestoreError();
				EnumRestoreErrorStatus status = EnumRestoreErrorStatus.build(nErrorStatus);
				if (status == 
						EnumRestoreErrorStatus.RESTORE_ERROR_NO_BACKUP_FILE) {
					strTost = getString(R.string.setting_restore_no_backup_file);
                    mRestoreTv.setClickable(true);
				}else if (EnumRestoreErrorStatus.RESTORE_ERROR_SUCCESSFUL == status) {
					strTost = getString(R.string.setting_restore_success);
				}
			}
			else
			{
				m_bRestore = false;
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
                    mRestoreTv.setClickable(true);
				}
			}
			
			Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
			ShowWaiting(false);
		}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
                    mBackupTv.setClickable(true);
					if(m_bRestore)
					{
                        mRestoreTv.setClickable(false);
					}
					else
					{
                        mRestoreTv.setClickable(true);
					}
				}else{
                    mBackupTv.setClickable(false);
                    mRestoreTv.setClickable(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.Home_no_sim), Toast.LENGTH_SHORT).show();
				}
			}
    	}
	}

    //下载配置文件
    public boolean downLoadConfig(String localFileName) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        OutputStream out = null;
        InputStream in = null;

        try {
            HttpGet httpGet = new HttpGet(ConstValue.HTTP_GET_CONFIG_ADDRESS);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();

            long length = entity.getContentLength();
            if (length <= 0) {
                return false;
            }

//            System.out.println("The response value of token:" + httpResponse.getFirstHeader("token"));

            File file = new File(localFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
