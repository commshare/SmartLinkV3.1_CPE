package com.alcatel.smartlinkv3.ui.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.fileexplorer.FtpFileExplorerTabActivity;
import com.alcatel.smartlinkv3.mediaplayer.activity.Go2ContentActivity;
import com.alcatel.smartlinkv3.mediaplayer.music.MusicPlayerActivity;
import com.alcatel.smartlinkv3.mediaplayer.picture.PicturePlayerActivity;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.BrowseDMSProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.MediaManager;
import com.alcatel.smartlinkv3.mediaplayer.proxy.BrowseDMSProxy.BrowseRequestCallback;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItemFactory;
import com.alcatel.smartlinkv3.mediaplayer.upnp.UpnpUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;
import com.alcatel.smartlinkv3.mediaplayer.video.VideoPlayerActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewMicroSD extends BaseViewImpl implements OnItemClickListener,BrowseRequestCallback{
	
	private final int ITEM_FILE = 0;
	private final int ITEM_VIDEO = 3;
	private final int ITEM_MUSIC = 1;
	private final int ITEM_PICTURES = 2;
	
	private ListView m_MicrosdListView = null;
	private UpitemAdapter adapter;
	private List<MicrosdItem>list;
	
	private AllShareProxy mAllShareProxy;
	private List<MediaItem> mCurItems;	
	private List<MediaItem> mNextItems;	
	private DMSDeviceBrocastFactory mBrocastFactory;
	private Handler mHandler;
	private Boolean filetag = false;
	private String titlePosition=null;

	
	private ViewMicroSDBroadcastReceiver m_viewMicroSDMsgReceiver;
	public static String DLNA_DEVICES_SUCCESS = "com.alcatel.smartlinkv3.dlna.device_success";
	public  final static String LIST_KEY = "com.alcatel.smartlinkv3.dlna.ArrayList";  
	
	private class ViewMicroSDBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DLNA_DEVICES_SUCCESS)) {
				filetag = true;
				mHandler = new Handler();
		    	mHandler.postDelayed(new RequestDirectoryRunnable(), 100);
	    	}
		
		}
	}
	
	@Override
	protected void init(){
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_microsd,
				null);
		
		m_MicrosdListView = (ListView)m_view.findViewById(R.id.microsdList);
		list = getData(m_context);
		adapter = new UpitemAdapter(m_context, list);
		
		m_MicrosdListView.setAdapter(adapter);
		m_MicrosdListView.setOnItemClickListener(this);
		
		mAllShareProxy = AllShareProxy.getInstance(this.m_context);
		//mBrocastFactory = new DMSDeviceBrocastFactory(this.m_context);
		mCurItems = new ArrayList<MediaItem>();
		mNextItems = new ArrayList<MediaItem>();
    	//mBrocastFactory.registerListener(this);	
	}
	
	public ViewMicroSD(Context context) {
		super(context);
		init();
		m_viewMicroSDMsgReceiver = new ViewMicroSDBroadcastReceiver();
		// TODO Auto-generated constructor stub
	}

	private void registerReceiver() {
		m_context.registerReceiver(m_viewMicroSDMsgReceiver, new IntentFilter(DLNA_DEVICES_SUCCESS));
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		registerReceiver();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		try {
//			mAllShareProxy.exitSearch();
			m_context.unregisterReceiver(m_viewMicroSDMsgReceiver);
		} catch (Exception e) {

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}
	
	private List<MicrosdItem> getData(Context context){
		List<MicrosdItem> list = new ArrayList<MicrosdItem>();
		MicrosdItem item;
		
		if ((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") == true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") == true))
		{
			item = new MicrosdItem(context.getString(R.string.microsd_file), context.getResources().getDrawable(R.drawable.microsd_item_folder), false);
			list.add(item);
		
			item = new MicrosdItem(context.getString(R.string.microsd_music), context.getResources().getDrawable(R.drawable.microsd_item_music), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_pictures), context.getResources().getDrawable(R.drawable.microsd_item_pictures), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_videos), context.getResources().getDrawable(R.drawable.microsd_item_videos), false);
			list.add(item);
		}else if((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") != true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") == true))
		{
			item = new MicrosdItem(context.getString(R.string.microsd_music), context.getResources().getDrawable(R.drawable.microsd_item_music), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_pictures), context.getResources().getDrawable(R.drawable.microsd_item_pictures), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_videos), context.getResources().getDrawable(R.drawable.microsd_item_videos), false);
			list.add(item);
		}else if((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") == true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") != true))
		{
			item = new MicrosdItem(context.getString(R.string.microsd_file), context.getResources().getDrawable(R.drawable.microsd_item_folder), false);
			list.add(item);
		}else if(BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900"))
		{
			item = new MicrosdItem(context.getString(R.string.microsd_file), context.getResources().getDrawable(R.drawable.microsd_item_folder), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_music), context.getResources().getDrawable(R.drawable.microsd_item_music), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_pictures), context.getResources().getDrawable(R.drawable.microsd_item_pictures), false);
			list.add(item);
			
			item = new MicrosdItem(context.getString(R.string.microsd_videos), context.getResources().getDrawable(R.drawable.microsd_item_videos), false);
			list.add(item);
		}
		return list;
	}
	
	public class UpitemAdapter extends BaseAdapter{

		private Context context;
		private List<MicrosdItem> listMicrosdItmes;
		
		public UpitemAdapter(Context context, List<MicrosdItem>list){
			this.context =context;
			this.listMicrosdItmes = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listMicrosdItmes.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listMicrosdItmes.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public final class ViewHolder{
			public ImageView Itemicon;
			public TextView ItemName;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(this.context).inflate(R.layout.microsd_item,null);	
				holder.Itemicon = (ImageView)convertView.findViewById(R.id.microsdItemIcon);
				holder.ItemName = (TextView)convertView.findViewById(R.id.microsdItemText);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			MicrosdItem item = listMicrosdItmes.get(position);
			
			holder.Itemicon.setBackgroundDrawable(item.getDrawableId());
			holder.ItemName.setText(item.getItemName());
			
			/*是否显示*/
//			Boolean blShowFlag = item.getIsShowFlag();
//			if(blShowFlag){
//				itemView.setVisibility(View.VISIBLE);				
//			}else{
//				itemView.setVisibility(View.GONE);
//			}
			return convertView;
		}
	}
	
	public class MicrosdItem{
		//Item name
		private String m_strItem;
		private Drawable m_drawableId ;
		private Boolean m_IsShowFlag;
		
		public MicrosdItem(String strItem, Drawable mdrawableid,Boolean IsShowFlag){
			super();
			this.m_strItem = strItem;
			this.m_drawableId = mdrawableid;
			this.m_IsShowFlag = IsShowFlag;
		}
		
		public Drawable getDrawableId(){
			return m_drawableId;
		}
		
		public void setDrawableId(Drawable mdrawableid){
			m_drawableId = mdrawableid;
		}
		
		public String getItemName(){
			return m_strItem;
		}
		
		public Boolean getIsShowFlag(){
			return m_IsShowFlag;
		}
		
		public void setIsShowFlag(Boolean IsShowFlag){
			m_IsShowFlag = IsShowFlag;
		}
	}
	
	private void goMusicPlayerActivity(int index, MediaItem item){
		
		MediaManager.getInstance().setMusicList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this.m_context, MusicPlayerActivity.class);
		intent.putExtra(MusicPlayerActivity.PLAY_INDEX, index);
		MediaItemFactory.putItemToIntent(item, intent);
		m_context.startActivity(intent);
	}
	
	private void goVideoPlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setVideoList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this.m_context, VideoPlayerActivity.class);
		intent.putExtra(VideoPlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		m_context.startActivity(intent);
	}
	
	
	private void goPicturePlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setPictureList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this.m_context, PicturePlayerActivity.class);
		intent.putExtra(PicturePlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		m_context.startActivity(intent);
	}
	
	private void requestDirectory()
    {
    	Device selDevice = mAllShareProxy.getDMSSelectedDevice();
    	if (selDevice == null){
    		CommonUtil.showToask(this.m_context, "can't find any devices...");
    		return ;
    	}
    	BrowseDMSProxy.syncGetDirectory(this.m_context, this);
    }
	
	class RequestDirectoryRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			requestDirectory();
		}
    }
	/*
	@Override
	public void onDeviceChange(boolean isSelDeviceChange) {
		// TODO Auto-generated method stub
		if (isSelDeviceChange){
			CommonUtil.showToask(this.m_context, "current device has been drop...");
			//finish();
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
	*/
	
	@Override
	public void onGetItems(final List<MediaItem> list) {
		// TODO Auto-generated method stub
	//	showProgress(false);
		
		if(filetag)
		{
			mCurItems = list;
		}else{
			filetag = true;
			mNextItems =list;
			goToDlnaViewPage();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") == true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") == true))
		{	
			switch(position){
			case 0:
				goToFilePage();
				break;
			case 1:
				onGetItemList(ITEM_MUSIC);
				break;
			case 2:
				onGetItemList(ITEM_PICTURES);
				break;
			case 3:
				onGetItemList(ITEM_VIDEO);
				break;
			}
		}else if((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") != true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") == true))
		{
			switch(position){
			case 0:
				onGetItemList(ITEM_MUSIC);
				break;
			case 1:
				onGetItemList(ITEM_PICTURES);
				break;
			case 2:
				onGetItemList(ITEM_VIDEO);
				break;
			}
		}else if((FeatureVersionManager.getInstance().isSupportApi("Sharing","GetFtpStatus") == true) 
				&& (FeatureVersionManager.getInstance().isSupportApi("Sharing",
				"GetDLNASettings") != true))
		{
			switch(position){
			case 0:
				onGetItemList(ITEM_MUSIC);
				break;
			}
		}else if(BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900"))
		{
			switch(position){
			case 0:
				goToFilePage();
				break;
			case 1:
				onGetItemList(ITEM_MUSIC);
				break;
			case 2:
				onGetItemList(ITEM_PICTURES);
				break;
			case 3:
				onGetItemList(ITEM_VIDEO);
				break;
			}
		}else
		{
			
		}
	}
	
	public void onGetItemList(int position) {
		if(BusinessManager.getInstance().getDlnaSettings().getDlnaStatus()  <= 0)
		{
			String strInfo = m_context.getString(R.string.dlna_not_open);
			Toast.makeText(m_context, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Device selDevice = mAllShareProxy.getDMSSelectedDevice();
    	if (selDevice == null){
    		CommonUtil.showToask(this.m_context, "can't find any devices,please check network!");
    		return ;
    	}else{
    		if(mCurItems.size() > position)
    		{
    			MediaItem item = (MediaItem) mCurItems.get(position);
    			titlePosition = item.getTitle();
    			filetag = false;
    			
    			if (UpnpUtil.isAudioItem(item)) {
    				goMusicPlayerActivity(position, item);
    			}else if (UpnpUtil.isVideoItem(item)){
    				goVideoPlayerActivity(position, item);
    			}else if (UpnpUtil.isPictureItem(item)){
    				goPicturePlayerActivity(position, item);
    			}else{
    				BrowseDMSProxy.syncGetItems(this.m_context, item.getStringid(), this);
    			  	//showProgress(true);
    			}
    		}
    	}
	}
		
	private void goToDlnaViewPage(){
		Intent intent = new Intent();
		intent.setClass(this.m_context, Go2ContentActivity.class);
		intent.putExtra(LIST_KEY, (Serializable)mNextItems);
		intent.putExtra("title", titlePosition);
		m_context.startActivity(intent);
	}

	private void goToFilePage(){	
		
		if(BusinessManager.getInstance().getFtpSettings().getFtpStatus() <= 0)
		{
			String strInfo = m_context.getString(R.string.ftp_not_open);
			Toast.makeText(m_context, strInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent();
		intent.setClass(this.m_context, FtpFileExplorerTabActivity.class);
		m_context.startActivity(intent);
	}
}
