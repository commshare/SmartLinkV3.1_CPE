package com.alcatel.wifilink.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.ui.home.fragment.SettingFragment;
import com.alcatel.wifilink.utils.PreferenceUtil;

import java.util.Locale;

public class SettingLanguageActivity extends BaseActivityWithBack {
    private static final String TAG = "SettingLanguageActivity";
    public static final String IS_SWITCH_LANGUAGE = "is_switch_language";
    private String[] mLanguageStrings = {Constants.Language.ENGLISH, Constants.Language.ARABIC, Constants.Language.ESPANYOL,
            Constants.Language.GERMENIC, Constants.Language.ITALIAN, Constants.Language.FRENCH};
    private ListView mLanguageListView;
    private LanguageAdapter mLanguageAdapter;
    private String mCurrentLanguage;
    private String mChangeLanguage;

    private boolean mIsSwitchLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_language);
        setTitle(R.string.language);
        mIsSwitchLanguage = getIntent().getBooleanExtra(IS_SWITCH_LANGUAGE, false);
        mCurrentLanguage = PreferenceUtil.getString("language", "en");
        mChangeLanguage = mCurrentLanguage;
        mLanguageListView = (ListView)findViewById(R.id.listview_language);
        mLanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChangeLanguage = mLanguageStrings[position];
                mLanguageAdapter.notifyDataSetChanged();
                invalidateOptionsMenu();
            }
        });
        mLanguageAdapter = new LanguageAdapter(this, mLanguageStrings);
        mLanguageListView.setAdapter(mLanguageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mCurrentLanguage.equals(mChangeLanguage)) {
            getMenuInflater().inflate(R.menu.save, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            switchLanguage(mChangeLanguage);
            mIsSwitchLanguage = true;
            setTitle(R.string.language);
            mCurrentLanguage = mChangeLanguage;
            invalidateOptionsMenu();
//            finish();
//            Intent it = new Intent(SettingLanguageActivity.this, SettingLanguageActivity.class);
//            it.putExtra(IS_SWITCH_LANGUAGE, true);
//            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed");
        Intent intent = new Intent();
        intent.putExtra(IS_SWITCH_LANGUAGE, mIsSwitchLanguage);
        setResult(SettingFragment.SET_LANGUAGE_REQUEST, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        Log.d(TAG,"onStop");
        super.onStop();
    }

    private class LanguageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private String[] languages;

        public LanguageAdapter(Context context, String[] languageStrings) {
            super();
            inflater = LayoutInflater.from(context);
            this.languages = languageStrings;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return languages.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.setting_language_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.languageNameTv = (TextView) convertView.findViewById(R.id.language_item_tv);
                viewHolder.languageCheckImg = (ImageView) convertView.findViewById(R.id.language_item_img);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            if (languages[position].equals("en")) {
                config.locale = Locale.ENGLISH;
            } else if(languages[position].equals("ar")){
                // 阿拉伯语
                config.locale = new Locale("ar");
            } else if(languages[position].equals("de")){
                // 德语
                config.locale = Locale.GERMANY;
            } else if(languages[position].equals("es")){
                // 西班牙语
                config.locale =  new Locale("es");
            } else if(languages[position].equals("it")){
                // 意大利语
                config.locale = Locale.ITALIAN;
            } else if(languages[position].equals("fr")){
                // 法语
                config.locale = Locale.FRENCH;
            }
            viewHolder.languageNameTv.setText(config.locale.getDisplayName(config.locale));
            if (mChangeLanguage.equals(languages[position])) {
                viewHolder.languageCheckImg.setVisibility(View.VISIBLE);
            } else {
                viewHolder.languageCheckImg.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            public TextView languageNameTv;
            public ImageView languageCheckImg;
        }

    }

}
