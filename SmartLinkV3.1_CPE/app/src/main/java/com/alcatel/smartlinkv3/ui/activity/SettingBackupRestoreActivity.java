package com.alcatel.smartlinkv3.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.common.CommonUtil;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.alcatel.smartlinkv3.httpservice.ConstValue.HTTP_UPLOAD_BACKUP_SETTINGS_ADDRESS;

public class SettingBackupRestoreActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_titleTextView = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private ProgressBar m_pbWaitingBar=null;
	private boolean  m_bRestore = false;
    private TextView          mBackupTv;
    private TextView          mRestoreTv;
    private Dialog            mBackupDialog;
    private Dialog            mRestoreDialog;
    private EditText          mBackupNameEt;
    private TextView          mBackupConfirm;
    private ListView          mRestoreNameListLv;
    private TextView          mRestoreConfirm;
    private List<RestoreBean> restoreConfigs;
    private RestoreAdapter mRestoreAdapter;
    private String selectPath;

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
            //先判断是否有备份文件
            restoreConfigs = getRestoreConfigs();
            if (restoreConfigs.size() > 0){
                showRestoreDialog();
//                onBtnRestore();
//                ShowWaiting(true);
            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.setting_restore_waring), Toast.LENGTH_SHORT).show();
            }
			break;
		default:
			break;
		}
	}

    private void showRestoreDialog() {
        mRestoreDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mRestoreDialog.setCanceledOnTouchOutside(false);
        mRestoreDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout restoreDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_restore, null);

        ImageView cancel = (ImageView) restoreDialogLLayout.findViewById(R.id.restore_cancel);
        mRestoreNameListLv = (ListView) restoreDialogLLayout.findViewById(R.id.restore_name_list);

        restoreConfigs.get(0).setCheck(true);//默认第一个被选中
        selectPath = restoreConfigs.get(0).getRestorePath();
        mRestoreAdapter = new RestoreAdapter(this, restoreConfigs);
        mRestoreNameListLv.setAdapter(mRestoreAdapter);
        mRestoreNameListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < restoreConfigs.size(); i++) {
                    if (i == position){
                        restoreConfigs.get(i).setCheck(true);
                        selectPath = restoreConfigs.get(i).getRestorePath();
                    }else{
                        restoreConfigs.get(i).setCheck(false);
                    }
                }
                mRestoreAdapter.notifyDataSetChanged();
            }
        });

        mRestoreConfirm = (TextView) restoreDialogLLayout.findViewById(R.id.restore_confirm);

        mRestoreDialog.setContentView(restoreDialogLLayout);

        //确认还原备份
        mRestoreConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), selectPath, Toast.LENGTH_SHORT).show();
                dismissRestoreDialog();
                ShowWaiting(true);
                onBtnRestore();
            }
        });

        //取消按钮
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissRestoreDialog();
            }
        });
        mRestoreDialog.show();
    }

    private List<RestoreBean> getRestoreConfigs() {

        List<RestoreBean> restoreBeenList = new ArrayList<RestoreBean>();
        String rootPath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.setting_backup_path);
        File f = new File(rootPath);
        File[] files = f.listFiles();// 列出所有文件
        // 将所有文件存入list中
        if(files != null){
            int count = files.length;// 文件个数
            for (int i = 0; i < count; i++) {
                File file = files[i];
                if (!file.isDirectory()){//是文件才添加
                    RestoreBean restoreBean = new RestoreBean();
                    restoreBean.setName(file.getName());
                    restoreBean.setRestorePath(file.getPath());
                    restoreBean.setCheck(false);
                    restoreBeenList.add(restoreBean);
                }
            }
        }
        return restoreBeenList;
    }

    public class RestoreAdapter extends BaseAdapter {

        private Context           context;
        private List<RestoreBean> restoreBeanList;

        public RestoreAdapter(Context context, List<RestoreBean> restoreBeanList) {
            this.context = context;
            this.restoreBeanList = restoreBeanList;
        }

        @Override
        public int getCount() {
            return restoreBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return restoreBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RestoreViewHolder restoreViewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_restore_list, null);
                restoreViewHolder = new RestoreViewHolder();

                restoreViewHolder.nameTv = (TextView) convertView.findViewById(R.id.item_restore_name);
                restoreViewHolder.isCheckIv = (ImageView) convertView.findViewById(R.id.item_restore_select);

                convertView.setTag(restoreViewHolder);
            }else{
                restoreViewHolder = (RestoreViewHolder) convertView.getTag();
            }

            //找数据
            RestoreBean carOrderBean = restoreBeanList.get(position);

            restoreViewHolder.nameTv.setText(carOrderBean.getName());
            if (carOrderBean.isCheck){
                restoreViewHolder.isCheckIv.setVisibility(View.VISIBLE);
            }else{
                restoreViewHolder.isCheckIv.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    //备份的viewHolder
    private class RestoreViewHolder{
        TextView nameTv;
        ImageView isCheckIv;
    }

    //备份的Bean
    public class RestoreBean{
        private String name;
        private Boolean isCheck;
        private String restorePath;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getCheck() {
            return isCheck;
        }

        public void setCheck(Boolean check) {
            isCheck = check;
        }

        public String getRestorePath() {
            return restorePath;
        }

        public void setRestorePath(String restorePath) {
            this.restorePath = restorePath;
        }
    }

    private void dismissRestoreDialog() {
        if (mRestoreDialog != null && mRestoreDialog.isShowing()) {
            mRestoreDialog.dismiss();
        }
    }

    private void showBackupDialog() {
        mBackupDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mBackupDialog.setCanceledOnTouchOutside(false);
        mBackupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout backupDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_backup, null);

        ImageView cancel = (ImageView) backupDialogLLayout.findViewById(R.id.backup_cancel);
        mBackupNameEt = (EditText) backupDialogLLayout.findViewById(R.id.backup_name);

        //处理EditText
        mBackupNameEt.setText("");
        CommonUtil.setEditTextInputFilter(mBackupNameEt);

        mBackupConfirm = (TextView) backupDialogLLayout.findViewById(R.id.backup_confirm);

        mBackupDialog.setContentView(backupDialogLLayout);

        //确认备份
        mBackupConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mBackupNameEt.getText())){
                    Toast.makeText(SettingBackupRestoreActivity.this,
                            getString(R.string.setting_backup_waring), Toast.LENGTH_SHORT).show();
                }else{
                    dismissBackupDialog();
                    onBtnBackup();
                    ShowWaiting(true);
                }
            }
        });

        //取消按钮
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissBackupDialog();
            }
        });
        mBackupDialog.show();
    }

    private void dismissBackupDialog() {
        if (mBackupDialog != null && mBackupDialog.isShowing()) {
            mBackupDialog.dismiss();
        }
    }

    private void onBtnBackup(){
		BusinessManager.getInstance().
		sendRequestMessage(MessageUti.SYSTEM_SET_APP_BACKUP, null);
	}
	
	private void onBtnRestore(){
        mRestoreTv.setClickable(false);
		m_bRestore = true;
//		BusinessManager.getInstance().
//		sendRequestMessage(MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP, null);

        // 调用文件上传方法，需要传入requestBody的key值，本地文件路径以及请求回调方法
        uploadConfig("file", selectPath, new Callback() {
            @Override
            public void onResponse(Call call, final okhttp3.Response response) {
                if (response.message().equals("OK")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowWaiting(false);
                            Toast.makeText(getApplicationContext(), getString(R.string.setting_restore_success), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowWaiting(false);
                            Toast.makeText(getApplicationContext(), getString(R.string.setting_restore_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call arg0, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowWaiting(false);
                        Toast.makeText(getApplicationContext(), getString(R.string.setting_restore_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";       // 指明要上传的文件格式
    public static void uploadConfig(String partName, String path, final Callback callback){
        File file = new File(path);             // 需要上传的文件
        RequestBody requestFile =               // 根据文件格式封装文件
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // 初始化请求体对象，设置Content-Type以及文件数据流
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)            // multipart/form-data
                .addFormDataPart(partName, file.getName(), requestFile)
                .build();

        // 封装OkHttp请求对象，初始化请求参数
        Request request = new Request.Builder()
                .url(HTTP_UPLOAD_BACKUP_SETTINGS_ADDRESS)                // 上传url地址
                .post(requestBody)              // post请求体
                .build();

        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient  = httpBuilder
                .connectTimeout(100, TimeUnit.SECONDS)          // 设置请求超时时间
                .writeTimeout(150, TimeUnit.SECONDS)
                .build();
        // 发起异步网络请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (callback != null){
                    callback.onResponse(call, response);
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }
        });
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
		
		SimStatusModel sim = BusinessManager.getInstance().getSimStatus();
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
                CommonUtil.makeRootDirectory(strPath);
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
                            CommonUtil.deleteFile(localFileName);
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
				RestoreError info = BusinessManager.getInstance().getRestoreError();
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
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
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
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
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
