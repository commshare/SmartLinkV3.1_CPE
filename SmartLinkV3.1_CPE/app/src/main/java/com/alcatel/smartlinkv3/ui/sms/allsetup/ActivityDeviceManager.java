package com.alcatel.smartlinkv3.ui.sms.allsetup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.model.device.response.BlockList;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.BaseActivityWithBack;
import com.alcatel.smartlinkv3.ui.sms.helper.FraDeviceHelper;
import com.alcatel.smartlinkv3.ui.sms.helper.FragmentDeviceEnum;
import com.alcatel.smartlinkv3.utils.ActionbarSetting;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityDeviceManager extends BaseActivityWithBack implements OnClickListener {

    public String blockPre = "Blocked (";
    public String blockFix = ")";
    public int blockSize;

    @BindView(R.id.fl_device)
    FrameLayout flDevice;

    public int resid = R.id.fl_device;

    public ImageButton mbackBtn;
    public TextView mblock;
    private TextView mTitle;
    public FragmentManager fm;

    public FragmentDeviceEnum tempEn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_manage_view);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        // init action bar
        initActionbar();
        // init fragment 
        toFragment(FragmentDeviceEnum.CONNECT);
        // get block count
        getblockCount();
    }

    /* **** getblockCount **** */
    private void getblockCount() {
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList result) {
                blockSize = result.getBlockList().size();
                runOnUiThread(() -> {
                    mblock.setText(blockPre + blockSize + blockFix);
                });

            }
        });
    }

    /* **** initActionbar **** */
    private void initActionbar() {
        new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                // back button
                mbackBtn = (ImageButton) view.findViewById(R.id.device_back);
                mbackBtn.setOnClickListener(ActivityDeviceManager.this);
                // block
                mblock = (TextView) view.findViewById(R.id.device_block);
                mblock.setOnClickListener(ActivityDeviceManager.this);
                // title
                mTitle = (TextView) view.findViewById(R.id.device_title);
                mTitle.setText(getString(R.string.device));

            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbardevices);
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            
            /* back button */
            case R.id.device_back:
                if (tempEn == FragmentDeviceEnum.CONNECT) {
                    finish();
                } else {
                    toFragment(FragmentDeviceEnum.CONNECT);
                }
                break;

            /* block button */
            case R.id.device_block:
                checkBlockList();
                break;
        }
    }

    private void checkBlockList() {
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList result) {
                if (result.getBlockList().size() > 0) {
                    toFragment(FragmentDeviceEnum.BLOCK);
                }
            }
        });
    }

    // helper --> to which fragment
    public void toFragment(FragmentDeviceEnum en) {
        runOnUiThread(() -> {
            if (en == FragmentDeviceEnum.CONNECT) {
                mblock.setVisibility(View.VISIBLE);
                mTitle.setText(getString(R.string.device));
                tempEn = FragmentDeviceEnum.CONNECT;
                FraDeviceHelper.commit(this, fm, resid, tempEn);
            } else {
                mblock.setVisibility(View.GONE);
                mTitle.setText(getString(R.string.device_manage_block_btn));
                tempEn = FragmentDeviceEnum.BLOCK;
                FraDeviceHelper.commit(this, fm, resid, tempEn);
            }
        });
    }
}
