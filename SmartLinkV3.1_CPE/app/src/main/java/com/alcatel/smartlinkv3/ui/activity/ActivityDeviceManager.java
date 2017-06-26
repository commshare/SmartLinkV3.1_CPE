package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.model.device.other.BlockModel;
import com.alcatel.smartlinkv3.model.device.other.ConnectModel;
import com.alcatel.smartlinkv3.model.device.other.ModelHelper;
import com.alcatel.smartlinkv3.model.device.response.BlockList;
import com.alcatel.smartlinkv3.model.device.response.ConnectedList;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.home.helper.cons.Cons;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.utils.ActionbarSetting;

import java.util.ArrayList;
import java.util.List;

public class ActivityDeviceManager extends BaseActivityWithBack implements OnClickListener {
    private ListView mLv_connectDevice = null;
    private ListView mLv_blockedDevice = null;
    private LinearLayout m_back = null;
    private TextView m_txConnectedCnt;
    private TextView m_txBlockCnt;
    private ImageView m_refresh;
    private ProgressBar m_waiting = null;

    private boolean m_bIsWorking = false;
    private boolean m_bEnableRefresh = false;

    List<ConnectModel> connectModelList = new ArrayList<>();
    List<BlockModel> blockModelList = new ArrayList<>();

    private String m_strLocalMac = new String();
    private ImageButton mbackBtn;
    private TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_manage_view);
        getWindow().setBackgroundDrawable(null);

        // connect device
        mLv_connectDevice = (ListView) this.findViewById(R.id.connected_devices);
        ConnectedDevAdapter connectedDevAdapter = new ConnectedDevAdapter(this);
        mLv_connectDevice.setAdapter(connectedDevAdapter);

        // block device
        mLv_blockedDevice = (ListView) this.findViewById(R.id.block_devices);
        BlockedDevAdapter blockedDevAdapter = new BlockedDevAdapter(this);
        mLv_blockedDevice.setAdapter(blockedDevAdapter);

        m_txConnectedCnt = (TextView) this.findViewById(R.id.tx_connected_cnt);
        m_txBlockCnt = (TextView) this.findViewById(R.id.tx_block_cnt);
        String strConnectedCnt = this.getResources().getString(R.string.device_manage_connected);
        strConnectedCnt = String.format(strConnectedCnt, 0);
        m_txConnectedCnt.setText(strConnectedCnt);

        String strBlockdCnt = this.getResources().getString(R.string.device_manage_block);
        strBlockdCnt = String.format(strBlockdCnt, 0);
        m_txBlockCnt.setText(strBlockdCnt);

        m_refresh = (ImageView) findViewById(R.id.refresh);
        m_refresh.setOnClickListener(this);

        m_waiting = (ProgressBar) this.findViewById(R.id.waiting_progress);

        // init action bar
        initActionbar();
        // init Devices
        getDevicesStatus();
        // init timer
        initTimer();

    }

    /* **** initActionbar **** */
    private void initActionbar() {
        new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                mbackBtn = (ImageButton) view.findViewById(R.id.device_back);
                mbackBtn.setOnClickListener(ActivityDeviceManager.this);
            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbardevices);
    }

    /* **** initTimer **** */
    private void initTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                getDevicesStatus();
            }
        };
        timerHelper.start(5000);
    }

    /* **** getDevicesStatus **** */
    private void getDevicesStatus() {
        if (!m_bEnableRefresh) {
            // get macaddress
            getLocalMacAddress();
        }
        // get connect devices
        updateConnectedDeviceUI();
        // get block devices
        updateBlockDeviceUI();
    }

    /* **** updateConnectedDeviceUI **** */
    private void updateConnectedDeviceUI() {
<<<<<<< Updated upstream
=======
        // System.out.println("当前方法名: " + "updateConnectedDeviceUI");
>>>>>>> Stashed changes
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList result) {
                connectModelList = ModelHelper.getConnectModel(result);
                if (connectModelList.size() > 0) {
                    ((ConnectedDevAdapter) mLv_connectDevice.getAdapter()).notifyDataSetChanged();
                    String strConnectedCnt = ActivityDeviceManager.this.getResources().getString(R.string.device_manage_connected);
                    strConnectedCnt = String.format(strConnectedCnt, connectModelList.size());
                    m_txConnectedCnt.setText(strConnectedCnt);
                }
            }
        });
    }

    /* **** updateBlockDeviceUI **** */
    private void updateBlockDeviceUI() {
<<<<<<< Updated upstream
=======
        // System.out.println("当前方法名: " + "updateBlockDeviceUI");
>>>>>>> Stashed changes
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList result) {
                blockModelList = ModelHelper.getBlockModel(result);
                if (blockModelList.size() > 0) {
                    ((BlockedDevAdapter) mLv_blockedDevice.getAdapter()).notifyDataSetChanged();
                    String strBlockdCnt = ActivityDeviceManager.this.getResources().getString(R.string.device_manage_block);
                    strBlockdCnt = String.format(strBlockdCnt, blockModelList.size());
                    m_txBlockCnt.setText(strBlockdCnt);
                }
            }
        });
    }

    /* **** getLocalMacAddress **** */
    private void getLocalMacAddress() {
<<<<<<< Updated upstream
=======
        // System.out.println("当前方法名: " + "getLocalMacAddress");
>>>>>>> Stashed changes
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        m_strLocalMac = info.getMacAddress();
    }


    @Override
    public void onResume() {
        super.onResume();
        getDevicesStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        m_bIsWorking = false;
        m_bEnableRefresh = false;
        m_waiting.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHelper.stop();
        System.out.println("当前方法名: " + "onDestroy");
    }

    /* setConnectedDeviceBlock */
    private void setConnectedDeviceBlock(String strDeviceName, String strMac) {
        API.get().setConnectedDeviceBlock(strDeviceName, strMac, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
            }
        });
    }

    /* setDeviceUnlock */
    private void setDeviceUnlock(String strDeviceName, String strMac) {
        API.get().setDeviceUnblock(strDeviceName, strMac, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
            }
        });
    }

    /* setDeviceName */
    private void setDeviceName(String strDeviceName, String strMac, int nDeviceType) {
        API.get().setDeviceName(strDeviceName, strMac, nDeviceType, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh:// button who refresh 
                m_bEnableRefresh = true;
                m_waiting.setVisibility(View.VISIBLE);
                getDevicesStatus();
                ToastUtil_m.show(this, getString(R.string.Refresh_successful));
                m_waiting.setVisibility(View.GONE);
                break;
            case R.id.device_back:// button who back to home 
                finish();
                break;
        }
    }

    public class ConnectedDevAdapter extends BaseAdapter {

        public ConnectedDevAdapter(Context context) {

        }

        public int getCount() {
            return connectModelList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public final class ViewHolder {
            public Button blockBtn;
            public ImageView icon;
            public TextView deviceNameTextView;
            public EditText deviceNameEditView;
            public RelativeLayout deviceNameLayout;
            public ImageView modifyDeviceName;
            public TextView ip;
            public TextView mac;
        }

        ViewHolder holder = null;
        private String m_strEditString = new String();

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ActivityDeviceManager.this).inflate(R.layout.device_manage_connected_item, null);
                holder.blockBtn = (Button) convertView.findViewById(R.id.block_button);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.deviceNameTextView = (TextView) convertView.findViewById(R.id.device_description_textview);
                holder.deviceNameEditView = (EditText) convertView.findViewById(R.id.device_description_editview);
                holder.deviceNameLayout = (RelativeLayout) convertView.findViewById(R.id.device_name_layout);
                holder.modifyDeviceName = (ImageView) convertView.findViewById(R.id.edit_image);
                holder.ip = (TextView) convertView.findViewById(R.id.ip);
                holder.mac = (TextView) convertView.findViewById(R.id.mac);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (m_bIsWorking == true)
                holder.blockBtn.setEnabled(false);
            else
                holder.blockBtn.setEnabled(true);

            ConnectModel connectModel = connectModelList.get(position);
            ConnectedList.Device device = connectModel.device;

            final String displayName = device.DeviceName;
            holder.deviceNameTextView.setText(displayName);

            if (device.ConnectMode == Cons.USB_CONNECT)
                holder.blockBtn.setEnabled(false);
            else
                holder.blockBtn.setEnabled(true);

            holder.ip.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_ip), device.IPAddress));
            final String mac = device.MacAddress;
            holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));
            final int type = device.DeviceType;

            if (mac.equalsIgnoreCase(m_strLocalMac)) {
                holder.blockBtn.setVisibility(View.GONE);
                holder.deviceNameEditView.setVisibility(View.GONE);
                holder.deviceNameTextView.setVisibility(View.VISIBLE);
                holder.icon.setBackgroundResource(R.drawable.connected_profile);
                holder.modifyDeviceName.setVisibility(View.GONE);
            } else {
                holder.blockBtn.setVisibility(View.VISIBLE);
                holder.icon.setBackgroundResource(R.drawable.connected_device);
                holder.modifyDeviceName.setVisibility(View.VISIBLE);
                if (connectModel.isEdit == true) {
                    holder.deviceNameEditView.setVisibility(View.VISIBLE);
                    holder.deviceNameEditView.setText(m_strEditString);
                    holder.deviceNameEditView.requestFocus();
                    int nSelection = m_strEditString.length() == 0 ? 0 : m_strEditString.length();
                    holder.deviceNameEditView.setSelection(nSelection);
                    holder.deviceNameTextView.setVisibility(View.GONE);
                    holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_finished);
                } else {
                    holder.deviceNameEditView.setVisibility(View.GONE);
                    holder.deviceNameTextView.setVisibility(View.VISIBLE);
                    holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
                }
            }

            holder.deviceNameEditView.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable arg0) {
                    String strText = arg0.toString();
                    String strNewText = strText.replaceAll("[,\":;&]", "");
                    if (strNewText.equals(arg0.toString()) == false) {
                        arg0 = arg0.replace(0, arg0.length(), strNewText);
                    }
                    m_strEditString = strNewText;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

            });

            holder.modifyDeviceName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (connectModelList.get(position).isEdit == true) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        connectModelList.get(position).isEdit = false;
                        String strName = m_strEditString.trim();
                        if (strName.length() == 0 || strName.length() > 31) {
                            String msgRes = ActivityDeviceManager.this.getString(R.string.device_manage_name_empty);
                            Toast.makeText(ActivityDeviceManager.this, msgRes, Toast.LENGTH_SHORT).show();
                        }
                        if (strName.length() != 0 && !strName.equals(displayName)) {
                            setDeviceName(strName, mac, type);
                            connectModelList.get(position).device.DeviceName = strName;
                        }
                    } else {
                        m_strEditString = connectModelList.get(position).device.DeviceName;
                        connectModelList.get(position).isEdit = true;
                    }

                    ((ConnectedDevAdapter) mLv_connectDevice.getAdapter()).notifyDataSetChanged();
                }

            });

            holder.blockBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_bIsWorking = true;
                    m_bEnableRefresh = true;
                    m_waiting.setVisibility(View.VISIBLE);
                    holder.blockBtn.setEnabled(false);
                    setConnectedDeviceBlock(displayName, mac);
                    connectModelList.remove(position);
                    ((ConnectedDevAdapter) mLv_connectDevice.getAdapter()).notifyDataSetChanged();
                    ((BlockedDevAdapter) mLv_blockedDevice.getAdapter()).notifyDataSetChanged();
                }

            });


            holder.deviceNameEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        String strName = v.getText().toString();

                        if (!strName.equals(displayName)) {
                            setDeviceName(strName, mac, type);
                        }
                        connectModelList.get(position).isEdit = false;
                        holder.modifyDeviceName.setBackgroundResource(R.drawable.connected_edit);
                    }

                    return false;
                }

            });

            return convertView;
        }

    }

    public class BlockedDevAdapter extends BaseAdapter {

        public BlockedDevAdapter(Context context) {

        }

        public int getCount() {
            return blockModelList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public final class ViewHolder {
            public Button unblockBtn;
            public ImageView icon;
            public TextView deviceName;
            public TextView mac;
        }

        ViewHolder holder = null;

        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ActivityDeviceManager.this).inflate(R.layout.device_manage_block_item, null);
                holder.unblockBtn = (Button) convertView.findViewById(R.id.unblock_button);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.deviceName = (TextView) convertView.findViewById(R.id.device_description);
                holder.mac = (TextView) convertView.findViewById(R.id.mac);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (m_bIsWorking == true)
                holder.unblockBtn.setEnabled(false);
            else
                holder.unblockBtn.setEnabled(true);

            BlockModel blockModel = blockModelList.get(position);
            BlockList.BlockDevice block = blockModel.block;

            final String displayName = block.DeviceName;
            holder.deviceName.setText(displayName);
            final String mac = block.MacAddress;
            holder.mac.setText(String.format(ActivityDeviceManager.this.getString(R.string.device_manage_mac), mac));

            holder.unblockBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_bIsWorking = true;
                    m_waiting.setVisibility(View.VISIBLE);
                    m_bEnableRefresh = true;
                    holder.unblockBtn.setEnabled(false);
                    setDeviceUnlock(displayName, mac);
                    blockModelList.remove(position);
                    ((ConnectedDevAdapter) mLv_connectDevice.getAdapter()).notifyDataSetChanged();
                    ((BlockedDevAdapter) mLv_blockedDevice.getAdapter()).notifyDataSetChanged();
                }
            });

            return convertView;
        }

    }

}
