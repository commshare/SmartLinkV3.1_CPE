package com.alcatel.smartlinkv3.ui.sms.fragment.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.model.device.other.BlockModel;
import com.alcatel.smartlinkv3.ui.sms.helper.ModelHelper;
import com.alcatel.smartlinkv3.model.device.response.BlockList;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.ui.sms.allsetup.ActivityDeviceManager;
import com.alcatel.smartlinkv3.ui.sms.fragment.block.BlockAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeviceBlockFragment extends Fragment {

    @BindView(R.id.rcv_deviceBlock)
    RecyclerView rcv_block;
    Unbinder unbinder;
    private View inflate;
    private ActivityDeviceManager activity;
    private BlockAdapter blockAdapter;
    private List<BlockModel> blockList = new ArrayList<>();
    private List<BlockModel> blockModelList;
    public TimerHelper timerHelper;

    public DeviceBlockFragment(Activity activity) {
        this.activity = (ActivityDeviceManager) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_deviceblock, null);
        unbinder = ButterKnife.bind(this, inflate);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcv_block.setLayoutManager(lm);
        blockAdapter = new BlockAdapter(activity, blockList);
        rcv_block.setAdapter(blockAdapter);
        updateBlockDeviceUI();// init ui
        // start timer
        startTimer();
        return inflate;
    }

    private void startTimer() {
        timerHelper = new TimerHelper(activity) {
            @Override
            public void doSomething() {
                // refresh all
                updateBlockDeviceUI();
            }
        };
        timerHelper.start(3000);
    }

    /* **** updateBlockDeviceUI **** */
    private void updateBlockDeviceUI() {
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList result) {
                blockModelList = ModelHelper.getBlockModel(result);
                if (blockModelList.size() > 0) {
                    blockAdapter.notifys(blockModelList);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
