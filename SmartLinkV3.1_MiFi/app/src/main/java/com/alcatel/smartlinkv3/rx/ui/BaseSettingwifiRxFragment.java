package com.alcatel.smartlinkv3.rx.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.utils.OtherUtils;

import java.util.List;

/**
 * Created by qianli.ma on 2017/11/3 0003.
 */

public abstract class BaseSettingwifiRxFragment extends Fragment {

    public List<String> securityArr;
    public List<String> wepArr;
    public List<String> wpaArr;
    public int colorCheck;
    public int colorUnCheck;
    public int colorError;
    public Typeface textBold;
    public Typeface textNormal;
    public Drawable ivSwitcherOn;
    public Drawable ivSwitcherOff;
    public Drawable ivPsdVisible;
    public Drawable ivPsdInVisible;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRes();// 初始化资源
    }


    /**
     * 初始化资源
     */
    private void initRes() {
        securityArr = OtherUtils.getSecurityArr(getActivity());// WEP WPA WPA2 WPA/WPA2
        wepArr = OtherUtils.getWepArr(getActivity());// WEP:open share
        wpaArr = OtherUtils.getWpaArr(getActivity());// WPA:TKIP AES AUTO
        colorCheck = getResources().getColor(R.color.main_title_background);
        colorUnCheck = getResources().getColor(R.color.gray);
        colorError = Color.RED;
        textBold = Typeface.defaultFromStyle(Typeface.BOLD);
        textNormal = Typeface.defaultFromStyle(Typeface.NORMAL);
        ivSwitcherOn = getResources().getDrawable(R.drawable.pwd_switcher_on);
        ivSwitcherOff = getResources().getDrawable(R.drawable.pwd_switcher_off);
        ivPsdVisible = getResources().getDrawable(R.drawable.password_show);
        ivPsdInVisible = getResources().getDrawable(R.drawable.password_hide);
    }

}
