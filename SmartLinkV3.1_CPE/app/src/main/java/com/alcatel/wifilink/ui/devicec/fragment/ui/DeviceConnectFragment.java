package com.alcatel.wifilink.ui.devicec.fragment.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.device.other.ConnectModel;
import com.alcatel.wifilink.model.device.response.BlockList;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.devicec.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.devicec.fragment.connect.ConnectAdapter;
import com.alcatel.wifilink.ui.devicec.helper.ModelHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
@SuppressLint("ValidFragment")
public class DeviceConnectFragment extends Fragment {

    @BindView(R.id.rcv_deviceconnect)
    RecyclerView rcv_deviceConnect;
    Unbinder unbinder;
    private View inflate;
    private ConnectAdapter rvAdapter;
    private List<ConnectModel> connectModelList = new ArrayList<>();
    private ActivityDeviceManager activity;
    public TimerHelper timerHelper;

    public DeviceConnectFragment(Activity activity) {
        this.activity = (ActivityDeviceManager) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_deviceconnect, null);
        unbinder = ButterKnife.bind(this, inflate);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcv_deviceConnect.setLayoutManager(lm);
        rvAdapter = new ConnectAdapter(activity, this, connectModelList);
        rcv_deviceConnect.setAdapter(rvAdapter);
        getDevicesStatus();// init
        // start timer
        startTime();
        return inflate;
    }

    /* **** startTime **** */
    private void startTime() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getDevicesStatus();
            }
        };
        timerHelper.start(3000);
    }

    /* **** getDevicesStatus **** */
    private void getDevicesStatus() {
        // get connect devices
        updateConnectedDeviceUI();
        // refresh blocked count
        updateBlockCount();
    }

    /* **** updateConnectedDeviceUI **** */
    private void updateConnectedDeviceUI() {
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList result) {
                connectModelList = ModelHelper.getConnectModel(result);
                if (connectModelList.size() > 0) {
                    rvAdapter.notifys(connectModelList);
                }
            }
        });
    }

    /* **** updateBlockCount **** */
    private void updateBlockCount() {
        API.get().getBlockDeviceList(new MySubscriber<BlockList>() {
            @Override
            protected void onSuccess(BlockList result) {
                int blockSize = result.getBlockList().size();
                activity.runOnUiThread(() -> {
                    activity.mblock.setText(activity.blockPre + blockSize + activity.blockFix);
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
