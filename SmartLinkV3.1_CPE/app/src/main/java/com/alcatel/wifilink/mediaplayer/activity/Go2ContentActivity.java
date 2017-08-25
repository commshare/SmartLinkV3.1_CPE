package com.alcatel.wifilink.mediaplayer.activity;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.mediaplayer.adapter.ContentAdapter;
import com.alcatel.wifilink.mediaplayer.music.MusicPlayerActivity;
import com.alcatel.wifilink.mediaplayer.picture.FileManager;
import com.alcatel.wifilink.mediaplayer.picture.PicturePlayerActivity;
import com.alcatel.wifilink.mediaplayer.proxy.AllShareProxy;
import com.alcatel.wifilink.mediaplayer.proxy.BrowseDMSProxy;
import com.alcatel.wifilink.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.wifilink.mediaplayer.proxy.MediaManager;
import com.alcatel.wifilink.mediaplayer.proxy.BrowseDMSProxy.BrowseRequestCallback;
import com.alcatel.wifilink.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.wifilink.mediaplayer.upnp.MediaItem;
import com.alcatel.wifilink.mediaplayer.upnp.MediaItemFactory;
import com.alcatel.wifilink.mediaplayer.upnp.UpnpUtil;
import com.alcatel.wifilink.mediaplayer.util.CommonUtil;
import com.alcatel.wifilink.mediaplayer.util.FileHelper;
import com.alcatel.wifilink.mediaplayer.video.VideoPlayerActivity;
import com.alcatel.wifilink.ui.activity.BaseActivity;
import com.alcatel.wifilink.ui.view.PathIndicatorDlna;
import com.alcatel.wifilink.ui.view.PathIndicatorDlna.OnPathChangeListener;
import com.alcatel.wifilink.ui.view.ViewMicroSD;

public class Go2ContentActivity extends BaseActivity implements OnItemClickListener, IDeviceChangeListener, 
												BrowseRequestCallback, OnClickListener{

	private static final CommonLog log = LogFactory.createLog();
	
	private TextView mTVSelDeV;
	private ListView mContentListView;
	private ImageButton mBtnBack;
	private TextView tvback;
	
	
	private ContentAdapter mContentAdapter;
	private AllShareProxy mAllShareProxy;
	private ContentManager mContentManager;
	
	private List<MediaItem> mCurlistItems;	
	private DMSDeviceBrocastFactory mBrocastFactory;
	private String mRootTitle;
	private String mCurrTitle;
	private DelCacheFileManager mDelCacheFileManager;
	
	private PathIndicatorDlna mPathIndicatorDlna;
	private String mCurrentDlnaPath;
	
	private int addpathtag = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        getWindow().setBackgroundDrawable(null);
        m_bNeedBack = false;
        initView();   
        initData();
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		Intent msdIntent= new Intent(ViewMicroSD.DLNA_DEVICES_SUCCESS);
		sendBroadcast(msdIntent);
		
		mContentManager.clear();
		mContentManager.titleclear();
		mBrocastFactory.unRegisterListener();
		mDelCacheFileManager.start(FileManager.getSaveRootDir());
		
		super.onDestroy();
	}

	private void initView()
    {
    	
    	mTVSelDeV = (TextView) findViewById(R.id.title);
    	mBtnBack = (ImageButton) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	tvback = (TextView) this.findViewById(R.id.Back);
		tvback.setOnClickListener(this);
		
		mPathIndicatorDlna = (PathIndicatorDlna) this
				.findViewById(R.id.path_indicator_dlna);
		mPathIndicatorDlna.setOnPathChangeListener(new OnPathChangeListener() {
			@Override
			public void onPathSelected(int targetFolderID,
					int targetFolderPathIndex) {
					
				mCurrentDlnaPath = mRootTitle;
				refreshContentList(targetFolderPathIndex);
				refreshFileList(targetFolderPathIndex);
			}

		});
		
    	mContentListView = (ListView) findViewById(R.id.content_list);
    	mContentListView.setOnItemClickListener(this);
    	
    	mProgressDialog = new ProgressDialog(this);   	
    	mProgressDialog.setMessage("Loading...");
    }
	
	public void refreshFileList(int index) {
		mPathIndicatorDlna.setPath(mCurrentDlnaPath, index);
	}

    private void initData()
    {
    	mAllShareProxy = AllShareProxy.getInstance(this);
    	mContentManager = ContentManager.getInstance();
    	
    	@SuppressWarnings("unchecked")
		ArrayList<MediaItem> mCurlistItems =  (ArrayList<MediaItem>) getIntent().getSerializableExtra(ViewMicroSD.LIST_KEY);
    	mContentAdapter = new ContentAdapter(this, mCurlistItems);
    	mContentListView.setAdapter(mContentAdapter);
    	mRootTitle = this.getIntent().getStringExtra("title");
    	mCurrTitle = mRootTitle; 
    	
    	mContentManager.pushListItem(mCurlistItems);	
    	mContentManager.pushTitle(mCurrTitle);
    	
    	mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	
    	updateSelDevUI(mCurrTitle);
    	
    	mBrocastFactory.registerListener(this);
    	mDelCacheFileManager = new DelCacheFileManager();
    	
		refreshFileList(addpathtag);
		
    }
	
	private void setContentlist(List<MediaItem> list)
	{	
		mCurlistItems = list;
		if (list == null){
			mContentAdapter.clear();
		}else{
			mContentAdapter.refreshData(list);		
			updateSelDevUI(mCurrTitle);
		}
		
		refreshFileList(addpathtag);
	}
	
	
	private ProgressDialog mProgressDialog;
	private void showProgress(boolean bShow)
	{
		mProgressDialog.dismiss();
		if (bShow){
			mProgressDialog.show();
		}
			
	}


	private void goMusicPlayerActivity(int index, MediaItem item){
		
		MediaManager.getInstance().setMusicList(mCurlistItems);
		
		Intent intent = new Intent();
		intent.setClass(this, MusicPlayerActivity.class);
		intent.putExtra(MusicPlayerActivity.PLAY_INDEX, index);
		MediaItemFactory.putItemToIntent(item, intent);
		Go2ContentActivity.this.startActivity(intent);
	}
	
	private void goVideoPlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setVideoList(mCurlistItems);
		
		Intent intent = new Intent();
		intent.setClass(this, VideoPlayerActivity.class);
		intent.putExtra(VideoPlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		Go2ContentActivity.this.startActivity(intent);
	}
	
	
	private void goPicturePlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setPictureList(mCurlistItems);
		
		Intent intent = new Intent();
		intent.setClass(this, PicturePlayerActivity.class);
		intent.putExtra(PicturePlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		Go2ContentActivity.this.startActivity(intent);
	}
	
	private void refreshContentList(int index)
	{
		while(mContentManager.Stacksize() > index+1)
		{
			mContentManager.popListItem();
			mContentManager.popTitle();
		}
		
		List<MediaItem> list = mContentManager.peekListItem();
		mCurrTitle = mContentManager.peekTitle();
		if (list == null){
			super.onBackPressed();
		}else{
			mCurlistItems = list;
			if (list == null){
				mContentAdapter.clear();
			}else{
				mContentAdapter.refreshData(list);		
				updateSelDevUI(mCurrTitle);
			}
		}
	}
	
	private void back(){
		super.onBackPressed();
	}

//	@Override
//	public void onBackPressed() {
//		back();	
//	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		MediaItem item = (MediaItem) parent.getItemAtPosition(position);
		
		if (UpnpUtil.isAudioItem(item)) {
			goMusicPlayerActivity(position, item);
		}else if (UpnpUtil.isVideoItem(item)){
			goVideoPlayerActivity(position, item);
		}else if (UpnpUtil.isPictureItem(item)){
			goPicturePlayerActivity(position, item);
		}else{
			BrowseDMSProxy.syncGetItems(Go2ContentActivity.this, item.getStringid(), Go2ContentActivity.this);
			mCurrTitle = item.getTitle();
		  	showProgress(true);
		}
		
	}

	@Override
	public void onDeviceChange(boolean isSelDeviceChange) {
		// TODO Auto-generated method stub
		if (isSelDeviceChange){
			CommonUtil.showToask(this, "current device has been drop...");
			finish();
		}
	}

	@Override
	public void onGetItems(final List<MediaItem> list) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showProgress(false);
				if (list == null){
					CommonUtil.showToask(Go2ContentActivity.this, "can't get folder...");
					return ;
				}		
				mContentManager.pushListItem(list);			
				mContentManager.pushTitle(mCurrTitle);
				setContentlist(list);
				
			}
		});
	}
	
	
	private void updateSelDevUI(String title)
	{
		if (title == null)
		{
			mTVSelDeV.setText("no select device");
		}else{
			mTVSelDeV.setText(title);
		}
		
		mCurrentDlnaPath = title;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_back:
		case R.id.Back:	
			back();
			break;
		}
	}
	
	class DelCacheFileManager implements Runnable
	{
		private Thread mThread;
		private String mFilePath;
		
		public DelCacheFileManager()
		{
			
		}
		
		@Override
		public void run() {
			
			long time = System.currentTimeMillis();
			try {
				FileHelper.deleteDirectory(mFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long interval = System.currentTimeMillis() - time;
		}
		
		public boolean start(String directory)
		{		
			if (mThread != null)
			{
				if (mThread.isAlive())
				{
					return false;
				}			
			}
			mFilePath = directory;	
			mThread = new Thread(this);			
			mThread.start();	
			
			return true;
		}
		
	}
}
