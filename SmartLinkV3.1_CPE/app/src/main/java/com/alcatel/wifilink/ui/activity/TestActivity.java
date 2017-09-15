package com.alcatel.wifilink.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.utils.Logs;

public class TestActivity extends AppCompatActivity {

    private TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        heart();
    }

    private void heart() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().heartBeat(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        
                    }
                });
            }
        };
        timerHelper.start(3000);
    }

    public void connect(View v) {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {// get sim state
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                Logs.v("ma_test", "simState: " + simState);
                if (simState == Cons.PIN_REQUIRED) {
                    API.get().unlockPin("1234", new MySubscriber() {// unlock pin
                        @Override
                        protected void onSuccess(Object result) {
                            API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {// get connection
                                @Override
                                protected void onSuccess(ConnectionStates result) {
                                    int connectionStatus = result.getConnectionStatus();
                                    Logs.v("ma_test", "ConnectionStates-before: " + connectionStatus);
                                    API.get().connect(new MySubscriber() {// connect
                                        @Override
                                        protected void onSuccess(Object result) {
                                            API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {// get connection
                                                @Override
                                                protected void onSuccess(ConnectionStates result) {
                                                    int connectionStatus = result.getConnectionStatus();
                                                    Logs.v("ma_test", "ConnectionStates-after: " + connectionStatus);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                if (simState == Cons.READY) {
                    API.get().connect(new MySubscriber() {// connect
                        @Override
                        protected void onSuccess(Object result) {
                            API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {// get connection
                                @Override
                                protected void onSuccess(ConnectionStates result) {
                                    int connectionStatus = result.getConnectionStatus();
                                    Logs.v("ma_test", "ConnectionStates-after: " + connectionStatus);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

}
