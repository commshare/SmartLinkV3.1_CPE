package com.alcatel.smartlinkv3.mediaplayer.activity;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.mediaplayer.adapter.ContentAdapter;
import com.alcatel.smartlinkv3.mediaplayer.music.MusicPlayerActivity;
import com.alcatel.smartlinkv3.mediaplayer.picture.PicturePlayerActivity;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.BrowseDMSProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.proxy.MediaManager;
import com.alcatel.smartlinkv3.mediaplayer.proxy.BrowseDMSProxy.BrowseRequestCallback;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItemFactory;
import com.alcatel.smartlinkv3.mediaplayer.upnp.UpnpUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;
import com.alcatel.smartlinkv3.mediaplayer.video.VideoPlayerActivity;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;

public class Go2ContentActivity extends BaseActivity implements OnItemClickListener, IDeviceChangeListener, 
												BrowseRequestCallback, OnClickListener{

	private static final CommonLog log = LogFactory.createLog();
	
	private TextView mTVSelDeV;
	private ListView mContentListView;
	private Button mBtnBack;
	
	
	private ContentAdapter mContentAdapter;
	private AllShareProxy mAllShareProxy;
	private ContentManager mContentManager;
	
	private List<MediaItem> mCurlistItems;	
	private DMSDeviceBrocastFactory mBrocastFactory;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        
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
		
		Intent msdIntent= new Intent(ViewMicroSD.DLNA_DEVICES_OK);
		sendBroadcast(msdIntent);
		
		mContentManager.clear();
		mBrocastFactory.unRegisterListener();
		
		super.onDestroy();
	}

	private void initView()
    {
    	
    	mTVSelDeV = (TextView) findViewById(R.id.tv_selDev);
    	mContentListView = (ListView) findViewById(R.id.content_list);
    	mContentListView.setOnItemClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	
    	mProgressDialog = new ProgressDialog(this);   	
    	mProgressDialog.setMessage("Loading...");
    }

    private void initData()
    {
    	mAllShareProxy = AllShareProxy.getInstance(this);
    	mContentManager = ContentManager.getInstance();
    	
    	@SuppressWarnings("unchecked")
		ArrayList<MediaItem> mCurlistItems =  (ArrayList<MediaItem>) getIntent().getSerializableExtra(ViewMicroSD.LIST_KEY);
    	mContentAdapter = new ContentAdapter(this, mCurlistItems);
    	mContentListView.setAdapter(mContentAdapter);
    	
    	mContentManager.pushListItem(mCurlistItems);	
    	
    	mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	
    	updateSelDev();
    	
    	mBrocastFactory.registerListener(this);
    }
	
	private void setContentlist(List<MediaItem> list)
	{	
		mCurlistItems = list;
		if (list == null){
			mContentAdapter.clear();
		}else{
			mContentAdapter.refreshData(list);
		}
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
	

	private void back(){
		mContentManager.popListItem();
		List<MediaItem> list = mContentManager.peekListItem();
		if (list == null){
			super.onBackPressed();
		}else{
			setContentlist(list);
		}	
		
	}

	@Override
	public void onBackPressed() {
		back();	
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		MediaItem item = (MediaItem) parent.getItemAtPosition(position);
		log.e("item = \n" + item.getShowString());		
		
		if (UpnpUtil.isAudioItem(item)) {
			goMusicPlayerActivity(position, item);
		}else if (UpnpUtil.isVideoItem(item)){
			goVideoPlayerActivity(position, item);
		}else if (UpnpUtil.isPictureItem(item)){
			goPicturePlayerActivity(position, item);
		}else{
			BrowseDMSProxy.syncGetItems(Go2ContentActivity.this, item.getStringid(), Go2ContentActivity.this);
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
				setContentlist(list);
				
			}
		});
	}
	
	
	private void updateSelDev()
	{
		setSelDevUI(mAllShareProxy.getDMSSelectedDevice());
	}
	
	
	private void setSelDevUI(Device device)
	{
		if (device == null)
		{
			mTVSelDeV.setText("no select device");
		}else{
			mTVSelDeV.setText(device.getFriendlyName());
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_back:
			back();
			break;
		}
	}


}
