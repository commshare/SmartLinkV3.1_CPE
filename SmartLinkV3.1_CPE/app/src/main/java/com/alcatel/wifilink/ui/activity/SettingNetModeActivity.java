package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.WanManager;
import com.alcatel.wifilink.business.wanguide.NetModeCommitHelper;
import com.alcatel.wifilink.business.wanguide.NetModeUiHelper;
import com.alcatel.wifilink.business.wanguide.StatusBean;
import com.alcatel.wifilink.business.wanguide.WanInfo;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Deprecated
public class SettingNetModeActivity extends BaseActivity {
    
    /*
    *  Ax:带此序号声明为业务类方法
    *  Hx:带此序号声明为辅助类方法
    */

    public final static int MODE_DHCP = 0;
    public final static int MODE_PPPOE = 1;
    public final static int MODE_STATIC_IP = 2;

    @BindView(R.id.main_header_back_iv)
    ImageView mainHeaderBackIv; // 回退按钮
    @BindView(R.id.main_header_right_text)
    TextView mainHeaderRightText;// skip
    @BindView(R.id.main_header_right_container)
    FrameLayout mainHeaderRightContainer;
    @BindView(R.id.tv_title_title)
    TextView tvTitleTitle;

    @BindView(R.id.mRb_netmode_dhcp)
    RadioButton mRbNetmodeDhcp;// MODE_DHCP
    @BindView(R.id.mRb_netmode_pppoe)
    RadioButton mRbNetmodePPPoE;// MODE_PPPOE
    @BindView(R.id.mRb_netmode_staticIp)
    RadioButton mRbNetmodeStaticIp;// Static IP
    @BindView(R.id.mRg_netmode)
    RadioGroup mRgNetmode;// 选项组

    @BindView(R.id.mll_netmode)
    LinearLayout mllNetmode;// 待变容器
    @BindView(R.id.mBt_netmode_connect)
    RippleView mBtNetmodeConnect;// 连接按钮

    // 等待检测UI
    @BindView(R.id.mRl_netmode_detecting)
    RelativeLayout mRlNetmodeDetecting;

    private final static String TAG = "SettingNetModeActivity";

    public final static String NETMODE_INDEX = "netmode_index";// 索引标签名

    public int netmodeIndex = 0;// 默认索引
    private EditText[] itemEd;// 对应的主选项卡中的待变容器组元素
    private WanInfo wanInfo;// WAN口当前信息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting_netmode);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvTitleTitle.setText(R.string.connect_internet);
    }

    private void initData() {
        /*自动检测当前的上网模式*/

        mRlNetmodeDetecting.setVisibility(View.VISIBLE);

        mRlNetmodeDetecting.postDelayed(() -> {
            WanManager wanManager = BusinessManager.getInstance().getWanManager();// 获取WAN口Manager
            wanManager.setOnWanInfoListener(wanInfo -> {
                runOnUiThread(() -> {
                    mRlNetmodeDetecting.setVisibility(View.GONE);
                });
                // 1.获取WanInfo
                SettingNetModeActivity.this.wanInfo = wanInfo;
                
                // 2.根据类型进行判断
                // TOAT: 此处可以测试不同类型的效果
                //SettingNetModeActivity.this.wanInfo.setConnectType(0);
                ChoiceUiType(SettingNetModeActivity.this.wanInfo);

            });
            wanManager.getWanInfo();/* request */
        }, 1500);


    }

    /* 回退按钮 */
    @OnClick(R.id.main_header_back_iv)
    public void back() {
        CA.toActivity(this, ConnectTypeSelectActivity.class, false, true, false, 0);
        overridePendingTransition(0, 0);
    }

    /* Connect按钮 */
    @OnClick(R.id.mBt_netmode_connect)
    public void onConnect() {
        netmodeIndex = wanInfo.getConnectType();
        // 1.根据类型封装WanInfo请求体
        switch (netmodeIndex) {
            case MODE_DHCP:
                commitType("");
                break;
            case MODE_PPPOE:
                String item_Account = getEdittextContent(itemEd[0]);
                String item_Password = getEdittextContent(itemEd[1]);
                String item_MTU = getEdittextContent(itemEd[2]);
                if (!EmptyHandle(item_Account, item_Password, item_MTU)) {
                    // 拨号模式提交
                    commitType(item_Account, item_Password);
                }
                break;
            case MODE_STATIC_IP:
                String item_IPAddress = getEdittextContent(itemEd[0]);
                String item_SubMask = getEdittextContent(itemEd[1]);
                if (!EmptyHandle(item_IPAddress, item_SubMask)) {
                    // 静态IP下提交
                    commitType(item_IPAddress, item_SubMask);
                }
                break;
        }
    }

    /* SKIP按钮 */
    @OnClick(R.id.main_header_right_text)
    public void skip() {
        // 跳转--> wifi setting界面
        CA.toActivity(this, SettingWifiActivity.class, false, true, false, 0);
    }

    /* --------------------------------------------------- HELPER ------------------------------------------------*/

    /**
     * A1.请求路由器端口获取端口连接类型进行UI切换
     */
    private void ChoiceUiType(WanInfo wanInfo) {
        // 0.创建模式选择辅助
        NetModeUiHelper netModeHelper = new NetModeUiHelper(this);
        RadioButton[] rbs = {mRbNetmodeDhcp, mRbNetmodePPPoE, mRbNetmodeStaticIp};
        // 1.根据类型初始化类型
        // 2.修改主选项组UI
        rbs = netModeHelper.setNetModeUi(wanInfo, rbs);
        // 3.设置主选项组中 [待变容器] 选项卡监听接口
        netModeHelper.setOnNetModeItemListener(itemEd -> {
            SettingNetModeActivity.this.itemEd = itemEd;
        });
        // 4.设置主选项组点击切换 + 初始化
        netModeHelper.setOnClickAndInit(rbs, mllNetmode, wanInfo);
    }

    /**
     * A2.请求路由器
     *
     * @param content 提交的内容
     */
    private void commitType(String... content) {
        // 1.create statusbean
        StatusBean statusBean = new StatusBean();
        // 2.set value from diff type
        switch (wanInfo.getConnectType()) {
            case MODE_DHCP:
                break;
            case MODE_PPPOE:
                wanInfo.setAccount(content[0]);
                wanInfo.setPassword(content[1]);
                break;
            case MODE_STATIC_IP:
                wanInfo.setIpAddress(content[0]);
                wanInfo.setSubNetMask(content[1]);
                break;
        }
        // 3. set Waninfo in StatusBean
        statusBean.setWanInfo(wanInfo);
        // 4. commit it
        new NetModeCommitHelper(this) {
            @Override
            public void getStatusBean(StatusBean statusBean) {
                EventBus.getDefault().postSticky(statusBean);
                CA.toActivity(SettingNetModeActivity.this, NetModeConnectStatusActivity.class, false, true, false, 0);
            }
        }.commit(statusBean);

    }

    /**
     * H2.获取编辑域内容
     *
     * @param editText
     * @return
     */
    public String getEdittextContent(EditText editText) {
        return editText.getText().toString().trim().replace(" ", "");
    }

    /**
     * H3.非空判断
     *
     * @param contents 判断的内容
     * @return true: 存在空值; false: 不存在空值
     */
    public boolean EmptyHandle(String... contents) {
        StringBuffer sf = new StringBuffer();
        boolean isEmpty = false;// 默认为false
        for (String content : contents) {
            sf.append(content).append(":");
            if (TextUtils.isEmpty(content)) {
                isEmpty = true;// 如有空值, 修改为true
            }
        }
        if (isEmpty) {
            ToastUtil_m.show(this, "Not permit empty");
        }
        return isEmpty;
    }

}
