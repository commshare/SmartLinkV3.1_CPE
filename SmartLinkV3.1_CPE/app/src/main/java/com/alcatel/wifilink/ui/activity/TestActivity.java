package com.alcatel.wifilink.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
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


    int count = 0;

    public void connectClick(View v) {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {// get sim status
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                Logs.v("ma_test", "get simState success: " + simState);
                
                /* 有PIN码的情况下 */
                if (simState == Cons.PIN_REQUIRED) {
                    Logs.v("ma_test", "have pin statement");
                    API.get().unlockPin("1234", new MySubscriber() {// unlock pin
                        @Override
                        protected void onSuccess(Object result) {
                            Logs.v("ma_test", "unlockPin success");
                            API.get().connect(new MySubscriber() {// connect
                                @Override
                                protected void onSuccess(Object result) {
                                    Logs.v("ma_test", "connect success by pin statement");
                                }

                                @Override
                                protected void onResultError(ResponseBody.Error error) {
                                    Logs.v("ma_test", error.getMessage());
                                }
                            });
                        }
                    });
                } else {
                     /* 没有PIN码的情况下 */
                    Logs.v("ma_test", "no pin statement");
                    API.get().connect(new MySubscriber() {// connect
                        @Override
                        protected void onSuccess(Object result) {
                            Logs.v("ma_test", "connect success by no pin statement");
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            Logs.v("ma_test", error.getMessage());
                        }
                    });
                }


            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        timerHelper.stop();
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }
}
