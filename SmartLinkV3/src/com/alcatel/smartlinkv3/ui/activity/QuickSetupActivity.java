package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class QuickSetupActivity  extends Activity implements OnClickListener{
  protected TextView title;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*
     * DO NOT CLAIN 'android:theme="@android:style/Theme.Black.NoTitleBar"' 
     * in AndroidManifest.xml
     */
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.quick_setup);
   // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);
   // title = (TextView) findViewById(R.id.custom_title_text);
  }
  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    
  }

}
