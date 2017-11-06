package com.alcatel.smartlinkv3.rx.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.appwidget.PopupWindows;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.adapter.SettingWifiRxAdapter;
import com.alcatel.smartlinkv3.rx.bean.PsdBean;
import com.alcatel.smartlinkv3.rx.bean.PsdRuleBean;
import com.alcatel.smartlinkv3.rx.impl.other.EdittextWatch;
import com.alcatel.smartlinkv3.rx.impl.wlan.WlanResult;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.ScreenSize;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2017/11/2 0002.
 */
@SuppressLint("ValidFragment")
public class SettingwifiRx2p4Fragment extends BaseSettingwifiRxFragment {

    @BindView(R.id.et_ssid)
    EditText etSsid;
    @BindView(R.id.rl_ssid)
    PercentRelativeLayout rlSsid;
    @BindView(R.id.iv_ssid_broadcast)
    ImageView ivSsidBroadcast;
    @BindView(R.id.rl_ssid_broadcast)
    PercentRelativeLayout rlSsidBroadcast;
    @BindView(R.id.iv_password_socket)
    ImageView ivPasswordSocket;
    @BindView(R.id.rl_password_socket)
    PercentRelativeLayout rlPasswordSocket;
    @BindView(R.id.iv_wifipsd_eye)
    ImageView ivWifipsdEye;
    @BindView(R.id.et_wifipsd_eye)
    EditText etWifipsdEye;
    @BindView(R.id.rl_wifipsd_eye)
    PercentRelativeLayout rlWifipsdEye;
    @BindView(R.id.iv_security_more)
    ImageView ivSecurityMore;
    @BindView(R.id.tv_security)
    TextView tvSecurity;
    @BindView(R.id.rl_security)
    PercentRelativeLayout rlSecurity;
    @BindView(R.id.iv_encrytion_more)
    ImageView ivEncrytionMore;
    @BindView(R.id.tv_encrytion)
    TextView tvEncrytion;
    @BindView(R.id.rl_encrytion)
    PercentRelativeLayout rlEncrytion;
    Unbinder unbinder;
    private View inflate;

    private WlanResult.APListBean ap = SettingwifiRxActivity.apbean_2P4;// 从静态读取AP
    private PsdBean psdBean = new PsdBean();// 缓存WEP|WPA的初始化值
    private boolean isPsdVisible = false;// 密码是否可见(默认不可见false)
    private String wifipassword;
    private int securitMode;
    private int encrytionMode;
    private PopupWindows pop_security;
    private PopupWindows pop_encrytion;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_wifirx, null);
        unbinder = ButterKnife.bind(this, inflate);
        initPsd();
        initView();
        return inflate;
    }

    /**
     * show or hide的时候会调用该方法
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {// 显示的时候判断密码是否匹配规则
            PsdRuleBean prb = OtherUtils.checkPsdRule(ap).get(0);
            if (prb.getSecurityMode() == Conn.disable) {
                return;
            }
            // WEP模式 | WPA模式
            boolean wepNotMatch = prb.getSecurityMode() == Conn.WEP & !prb.isMatchWep();
            boolean wpaNotMatch = prb.getSecurityMode() != Conn.WEP & !prb.isMatchWpa();
            etWifipsdEye.setTextColor(wepNotMatch || wpaNotMatch ? colorError : colorCheck);
        }
    }

    /**
     * 缓存WEP|WPA的初始化值
     */
    private void initPsd() {
        psdBean.setWepKey(ap.getWepKey());
        psdBean.setWepType(ap.getWepType());
        psdBean.setWpaKey(ap.getWpaKey());
        psdBean.setWpaType(ap.getWpaType());
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // wifi广播相关信息
        etSsid.setText(ap.getSsid());
        etSsid.setSelection(OtherUtils.getEdittext(etSsid).length());
        ivSsidBroadcast.setImageDrawable(ap.getSsidHidden() == Conn.disable ? ivSwitcherOn : ivSwitcherOff);
        ivPasswordSocket.setImageDrawable(ap.getSecurityMode() == Conn.disable ? ivSwitcherOff : ivSwitcherOn);
        // 密码信息相关的布局是否消隐
        rlWifipsdEye.setVisibility(ap.getSecurityMode() == Conn.disable ? View.GONE : View.VISIBLE);
        rlSecurity.setVisibility(ap.getSecurityMode() == Conn.disable ? View.GONE : View.VISIBLE);
        rlEncrytion.setVisibility(ap.getSecurityMode() == Conn.disable ? View.GONE : View.VISIBLE);
        // 密码相关信息
        etWifipsdEye.setText(ap.getSecurityMode() == Conn.WEP ? ap.getWepKey() : ap.getWpaKey());
        setEdVisible(etWifipsdEye, isPsdVisible);
        etWifipsdEye.addTextChangedListener(new EdittextWatch() {
            @Override
            public void textchange(String s) {
                etWifipsdEye.setTextColor(colorCheck);// 恢复正常颜色(在初始检测时候有可能被修改)
                if (ap.getSecurityMode() != Conn.disable) {
                    wifipassword = s;
                    if (ap.getSecurityMode() == Conn.WEP) {
                        ap.setWepKey(wifipassword);
                    } else {
                        ap.setWpaKey(wifipassword);
                    }
                }
            }
        });
        ivWifipsdEye.setImageDrawable(isPsdVisible ? ivPsdVisible : ivPsdInVisible);
        tvSecurity.setText(securityArr.get(ap.getSecurityMode()));
        if (ap.getSecurityMode() != Conn.disable) {
            tvEncrytion.setText(ap.getSecurityMode() == Conn.WEP ? wepArr.get(ap.getWepType()) : wpaArr.get(ap.getWpaType()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_ssid_broadcast,           // ssid broadcast   
                     R.id.iv_password_socket,   // password     
                     R.id.iv_wifipsd_eye,       // wifi password eye
                     R.id.iv_security_more,     // security more    
                     R.id.tv_security,          // security text    
                     R.id.iv_encrytion_more,    // encrytion more
                     R.id.tv_encrytion})        // encrytion text   
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_ssid_broadcast:
                setSsidSocket();
                break;
            case R.id.iv_password_socket:
                setPasswordSocket();
                break;
            case R.id.iv_wifipsd_eye:
                setPsdVisibleSocket();
                break;
            case R.id.iv_security_more:
            case R.id.tv_security:
                setSecurityMode();
                break;
            case R.id.iv_encrytion_more:
            case R.id.tv_encrytion:
                setEncrytion();
                break;
        }

    }

    /**
     * 设置加密类型
     */
    private void setEncrytion() {
        // 获取安全策略模式
        boolean isSecurityWEP = ap.getSecurityMode() == Conn.WEP;
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int w = (int) (size.width * 0.8f);
        int h = (int) (size.height * 0.40f); 
        if (isSecurityWEP) {
            h = (int) (size.height * 0.18f);
        } else {
            h = (int) (size.height * 0.27f);
        }
        View inflate = View.inflate(getActivity(), R.layout.pop_settingwifirx, null);
        RecyclerView rcv_pop = (RecyclerView) inflate.findViewById(R.id.rcv_settingwifirx_pop);
        rcv_pop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        List<String> encrytionArr = ap.getSecurityMode() == Conn.WEP ? wepArr : wpaArr;// 加密可选数组
        int encrytionType = ap.getSecurityMode() == Conn.WEP ? ap.getWepType() : ap.getWpaType();
        rcv_pop.setAdapter(new SettingWifiRxAdapter(getActivity(), encrytionArr, encrytionType) {
            @Override
            public void getCheckPositon(int hadCheckPosition) {
                pop_encrytion.dismiss();
                if (isSecurityWEP) {
                    ap.setWepType(hadCheckPosition);
                } else {
                    ap.setWpaType(hadCheckPosition);
                }
                tvEncrytion.setText(isSecurityWEP ? wepArr.get(ap.getWepType()) : wpaArr.get(ap.getWpaType()));
            }
        });
        pop_encrytion = new PopupWindows(getActivity(), inflate, w, h, true);
    }

    /**
     * 点击设置安全策略(WEP WPA)
     */
    private void setSecurityMode() {
        // 获取不带[disable]的策略数组
        List<String> securityWithoutDisable = OtherUtils.getSecurityArrWithoutDisable(getActivity());
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int w = (int) (size.width * 0.8f);
        int h = (int) (size.height * 0.36f);
        View inflate = View.inflate(getActivity(), R.layout.pop_settingwifirx, null);
        RecyclerView rcv_pop = (RecyclerView) inflate.findViewById(R.id.rcv_settingwifirx_pop);
        rcv_pop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcv_pop.setAdapter(new SettingWifiRxAdapter(getActivity(), securityWithoutDisable, ap.getSecurityMode() - 1) {
            @Override
            public void getCheckPositon(int hadCheckPosition) {
                pop_security.dismiss();
                ap.setSecurityMode(hadCheckPosition + 1);// 从数组中返回的角标必须+1
                tvSecurity.setText(securityWithoutDisable.get(hadCheckPosition));// 设置安全策略文本
                tvEncrytion.setText(ap.getSecurityMode() == Conn.WEP ?// 设置加密类型
                                            wepArr.get(ap.getWepType()) : wpaArr.get(ap.getWpaType()));
                // 恢复密码操作(若选择WEP则恢复WPA密码,若选择WPA则恢复WEP密码)
                // 此处在SettingWifiRxActivit@method:uploadSetting()方法中继续了上传前规避恢复操作
                reBackKey(ap.getSecurityMode());
                // 恢复显示默认初始值
                etWifipsdEye.setText(ap.getSecurityMode() == Conn.WEP ? psdBean.getWepKey() : psdBean.getWpaKey());
            }
        });
        pop_security = new PopupWindows(getActivity(), inflate, w, h, true);
    }

    /**
     * 密码可视开关
     */
    private void setPsdVisibleSocket() {
        isPsdVisible = !isPsdVisible;
        setEdVisible(etWifipsdEye, isPsdVisible);
        etWifipsdEye.setSelection(OtherUtils.getEdittext(etWifipsdEye).length());
        ivWifipsdEye.setImageDrawable(isPsdVisible ? ivPsdVisible : ivPsdInVisible);
    }

    /**
     * 设置密码开关
     */
    private void setPasswordSocket() {

        if (ap.getSecurityMode() != Conn.disable) {/* 初始的安全策略为有效状态 */
            // 存放值到临时变量
            wifipassword = !TextUtils.isEmpty(wifipassword) ? wifipassword :// 非空--> 保存当前值
                                   ap.getSecurityMode() == Conn.WEP ?// 为空
                                           ap.getWepKey() : ap.getWpaKey();// AP自带过来的值
            securitMode = ap.getSecurityMode();
            encrytionMode = ap.getSecurityMode() == Conn.WEP ? ap.getWepType() : ap.getWpaType();
            ap.setSecurityMode(Conn.disable);// 对AP赋值
            // 此处应为恢复密码默认值---> 根据CTS的要求,此处恢复密码时
            // (此处已经由SettingWifiRxActivity@method:uploadWlanSetting()进行上传时规避处理)
            // reBackKey(ap.getSecurityMode());// 该方法不在此调用

        } else {/* 初始的安全策略为无效状态 */
            etWifipsdEye.setText(TextUtils.isEmpty(wifipassword) ? ap.getWpaKey() : wifipassword);
            tvSecurity.setText(TextUtils.isEmpty(wifipassword) ? securityArr.get(Conn.WPA_WPA2) : securityArr.get(securitMode));
            tvEncrytion.setText(TextUtils.isEmpty(wifipassword) ?// 是否初始状态为空
                                        wpaArr.get(Conn.ENCRYTION_AUTO) :// 默认为AUTO
                                        ap.getSecurityMode() == Conn.WEP ?// 不为空--> 是否为WEP模式
                                                wepArr.get(encrytionMode) :// WEP类型
                                                wpaArr.get(encrytionMode));// WPA类型
            ap.setSecurityMode(TextUtils.isEmpty(wifipassword) ? Conn.WPA_WPA2 : securitMode);// 对AP赋值
        }
        etWifipsdEye.setSelection(OtherUtils.getEdittext(etWifipsdEye).length());
        boolean securityClose = ap.getSecurityMode() == Conn.disable;
        rlWifipsdEye.setVisibility(securityClose ? View.GONE : View.VISIBLE);
        rlSecurity.setVisibility(securityClose ? View.GONE : View.VISIBLE);
        rlEncrytion.setVisibility(securityClose ? View.GONE : View.VISIBLE);
        ivPasswordSocket.setImageDrawable(ivPasswordSocket.getDrawable() == ivSwitcherOff ? ivSwitcherOn : ivSwitcherOff);
    }

    /**
     * 设置ssid broadcast开关
     */
    private void setSsidSocket() {
        // ssidHidden--> enable:隐藏 disable:显示
        ap.setSsidHidden(ap.getSsidHidden() == Conn.disable ? Conn.ENABLE : Conn.disable);
        // 修改ui
        ivSsidBroadcast.setImageDrawable(ap.getSsidHidden() == Conn.ENABLE ? ivSwitcherOff : ivSwitcherOn);
    }

    /**
     * 显示 | 隐藏 密码
     *
     * @param etWifipsdInput 密码编辑域
     * @param isVisible
     */
    public void setEdVisible(EditText etWifipsdInput, boolean isVisible) {
        if (isVisible) {
            etWifipsdInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            etWifipsdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    /**
     * 恢复原始密码类型操作
     *
     * @param type
     */
    public void reBackKey(int type) {
        if (type == Conn.disable) {
            // 全部还原
            ap.setWepKey(psdBean.getWepKey());
            ap.setWepType(psdBean.getWepType());
            ap.setWpaKey(psdBean.getWpaKey());
            ap.setWpaType(psdBean.getWpaType());
        } else if (type == Conn.WEP) {
            // 还原WPA
            ap.setWpaKey(psdBean.getWpaKey());
            ap.setWpaType(psdBean.getWpaType());
        } else {
            // 还原WEP
            ap.setWepKey(psdBean.getWepKey());
            ap.setWepType(psdBean.getWepType());
        }

    }
}
