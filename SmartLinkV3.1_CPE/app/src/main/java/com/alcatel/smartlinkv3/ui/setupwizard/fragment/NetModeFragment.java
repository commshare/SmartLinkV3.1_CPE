package com.alcatel.smartlinkv3.ui.setupwizard.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.appwidget.RippleView;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.WanManager;
import com.alcatel.smartlinkv3.business.wanguide.NetModeCommitHelper;
import com.alcatel.smartlinkv3.business.wanguide.NetModeUiHelper;
import com.alcatel.smartlinkv3.business.wanguide.StatusBean;
import com.alcatel.smartlinkv3.business.wanguide.WanInfo;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.ui.setupwizard.allsetup.SetupWizardActivity;
import com.alcatel.smartlinkv3.ui.setupwizard.helper.FraHelper;
import com.alcatel.smartlinkv3.ui.setupwizard.helper.FragmentEnum;

import org.greenrobot.eventbus.EventBus;

import static android.content.ContentValues.TAG;

/**
 * Created by qianli.ma on 2017/6/14.
 */

public class NetModeFragment extends Fragment implements View.OnClickListener {

    public final static int MODE_DHCP = 0;
    public final static int MODE_PPPOE = 1;
    public final static int MODE_STATIC_IP = 2;

    private SetupWizardActivity activity;

    RadioButton mRbNetmodeDhcp;// MODE_DHCP
    RadioButton mRbNetmodePPPoE;// MODE_PPPOE
    RadioButton mRbNetmodeStaticIp;// Static IP
    RadioGroup mRgNetmode;// 选项组

    LinearLayout mllNetmode;// 待变容器
    RippleView mBtNetmodeConnect;// 连接按钮

    // 等待检测UI
    RelativeLayout mRlNetmodeDetecting;

    private View inflate;
    public int netmodeIndex = 0;// 默认索引
    private WanInfo wanInfo;// WAN口当前信息
    private EditText[] itemEd;// 对应的主选项卡中的待变容器组元素

    public NetModeFragment(Activity activity) {
        this.activity = (SetupWizardActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // back & skip button visible
        activity.transferHead(SetupWizardActivity.HeadEnum.VISIBLE, getString(R.string.connect_internet));
        inflate = View.inflate(getActivity(), R.layout.fragment_setupwizard_netmode, null);
        initView();
        initData();
        initEvent();
        return inflate;
    }


    private void initView() {
        mRbNetmodeDhcp = (RadioButton) inflate.findViewById(R.id.mRb_netmode_dhcp);
        mRbNetmodePPPoE = (RadioButton) inflate.findViewById(R.id.mRb_netmode_pppoe);
        mRbNetmodeStaticIp = (RadioButton) inflate.findViewById(R.id.mRb_netmode_staticIp);
        mRgNetmode = (RadioGroup) inflate.findViewById(R.id.mRg_netmode);
        mllNetmode = (LinearLayout) inflate.findViewById(R.id.mll_netmode);
        mBtNetmodeConnect = (RippleView) inflate.findViewById(R.id.mBt_netmode_connect);
        mRlNetmodeDetecting = (RelativeLayout) inflate.findViewById(R.id.mRl_netmode_detecting);
    }

    private void initData() {
        Log.d(TAG, "choice the mode");
        /*自动检测当前的上网模式*/
        mRlNetmodeDetecting.setVisibility(View.VISIBLE);

        mRlNetmodeDetecting.postDelayed(() -> {
            WanManager wanManager = BusinessManager.getInstance().getWanManager();// 获取WAN口Manager
            wanManager.setOnWanInfoListener(wanInfo -> {
                getActivity().runOnUiThread(() -> {
                    mRlNetmodeDetecting.setVisibility(View.GONE);
                });
                NetModeFragment.this.wanInfo = wanInfo;
                // 1.获取WanInfo
                Log.d(TAG, "getWanInfos: " + wanInfo.toString());
                // 2.根据类型进行判断

                // TOAT: 此处可以测试不同类型的效果
                //NetModeFragment.this.wanInfo.setConnectType(0);
                ChoiceUiType(NetModeFragment.this.wanInfo);

            });
            wanManager.getWanInfo();/* request */
        }, 1500);
    }

    private void initEvent() {
        mBtNetmodeConnect.setOnClickListener(this);
    }
    
     /* --------------------------------------------------- HELPER ------------------------------------------------*/

    /**
     * A1.请求路由器端口获取端口连接类型进行UI切换
     */
    private void ChoiceUiType(WanInfo wanInfo) {
        // 0.创建模式选择辅助
        NetModeUiHelper netModeHelper = new NetModeUiHelper(getActivity());
        RadioButton[] rbs = {mRbNetmodeDhcp, mRbNetmodePPPoE, mRbNetmodeStaticIp};
        // 1.根据类型初始化类型
        // 2.修改主选项组UI
        rbs = netModeHelper.setNetModeUi(wanInfo, rbs);
        // 3.设置主选项组中 [待变容器] 选项卡监听接口
        netModeHelper.setOnNetModeItemListener(itemEd -> {
            NetModeFragment.this.itemEd = itemEd;
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
        new NetModeCommitHelper(getActivity()) {
            @Override
            public void getStatusBean(StatusBean statusBean) {
                EventBus.getDefault().postSticky(statusBean);
                // TODO: 2017/6/14 to net mode status fragment 
                // ChangeActivity.toActivity(getActivity(), NetModeConnectStatusActivity.class, true, true, false, 0);
                FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.WAN_NETMODE_STATE_FRA);
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
            ToastUtil_m.show(getActivity(), "Not permit empty");
        }
        return isEmpty;
        //ToastUtil_m.show(this, sf.toString());
    }

    @Override
    public void onClick(View v) {
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
}
