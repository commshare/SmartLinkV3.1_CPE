package com.alcatel.wifilink.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.utils.PreferenceUtil;

import java.util.Locale;

public class SettingLanguageActivity extends BaseActivityWithBack implements OnClickListener {
    private static final String TAG = "SettingLanguageActivity";
    private String[] mLanguageStrings = {Constants.Language.ENGLISH, Constants.Language.ARABIC, Constants.Language.ESPANYOL,
            Constants.Language.GERMENIC, Constants.Language.ITALIAN, Constants.Language.FRENCH};
    private ListView mLanguageListView;
    private LanguageAdapter mLanguageAdapter;
    private String mCurrentLanguage;
    private String mChangeLanguage;
//    private ImageView mEnglishImg;
//    private ImageView mEspanyolImg;
//    private ImageView mArabicImg;
//    private ImageView mGermanicImg;
//    private ImageView mItalianImg;
//    private ImageView mFrenchImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_language);
        setTitle("Language");
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
//        findViewById(R.id.language_english).setOnClickListener(this);
//        findViewById(R.id.language_espanyol).setOnClickListener(this);
//        findViewById(R.id.language_arabic).setOnClickListener(this);
//        findViewById(R.id.language_germanic).setOnClickListener(this);
//        findViewById(R.id.language_italian).setOnClickListener(this);
//        findViewById(R.id.language_french).setOnClickListener(this);
//        mEnglishImg = (ImageView) findViewById(R.id.language_english_img);
//        mEspanyolImg = (ImageView) findViewById(R.id.language_espanyol_img);
//        mArabicImg = (ImageView) findViewById(R.id.language_arabic_img);
//        mGermanicImg = (ImageView) findViewById(R.id.language_germanic_img);
//        mItalianImg = (ImageView) findViewById(R.id.language_italian_img);
//        mFrenchImg = (ImageView) findViewById(R.id.language_french_img);

//        String language = PreferenceUtil.getString("language", "en");
//        if (language.equals("en")) {
//            mEnglishImg.setVisibility(View.VISIBLE);
//        } else if(language.equals("ar")){
//            // 阿拉伯语
//            mArabicImg.setVisibility(View.VISIBLE);
//        } else if(language.equals("de")){
//            // 德语
//            mGermanicImg.setVisibility(View.VISIBLE);
//        } else if(language.equals("es")){
//            // 西班牙语
//            mEspanyolImg.setVisibility(View.VISIBLE);
//        } else if(language.equals("it")){
//            // 意大利语
//            mItalianImg.setVisibility(View.VISIBLE);
//        } else if(language.equals("fr")){
//            // 法语
//            mFrenchImg.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        int nID = v.getId();
//        switch (nID) {
//            case R.id.language_english:
//                switchLanguage("en");
//                break;
//            case R.id.language_arabic:
//                switchLanguage("ar");
//                break;
//            case R.id.language_espanyol:
//                switchLanguage("es");
//                break;
//            case R.id.language_germanic:
//                switchLanguage("de");
//                break;
//            case R.id.language_italian:
//                switchLanguage("it");
//                break;
//            case R.id.language_french:
//                switchLanguage("fr");
//                break;
//            default:
//                break;
//        }
//        //更新语言后，destroy当前页面，重新绘制
//        finish();
//        Intent it = new Intent(SettingLanguageActivity.this, SettingLanguageActivity.class);
//        startActivity(it);
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
            finish();
            Intent it = new Intent(SettingLanguageActivity.this, SettingLanguageActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
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
