package com.alcatel.smartlinkv3.mediaplayer.picture;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.R.string;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.mediaplayer.proxy.MediaManager;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItemFactory;
import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.FileHelper;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;

public class PicturePlayerActivity extends BaseActivity implements DownLoadHelper.IDownLoadCallback,
									PictureUtil.IScalCallback{
	private static final CommonLog log = LogFactory.createLog();
	
	public static final String PLAY_INDEX = "player_index";
	
	private UIManager mUIManager;
	private DelCacheFileManager mDelCacheFileManager;
	private PictureControlCenter mControlCenter;
	
	
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	
	private MediaItem mMediaInfo = new MediaItem();	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picture_player_layout);
		m_bNeedBack = false;
		
		initView();
		initData();
		
		refreshIntent(getIntent());
	}
	
	private void initView(){
		mUIManager = new UIManager();
		mDelCacheFileManager = new DelCacheFileManager();
	}

	
	private void initData(){
	
		mControlCenter = new PictureControlCenter(this);
		mControlCenter.init();
		mControlCenter.setDownLoadCallback(this);
		
		/*pchong   start*/
	//	mThumbnailControlCenter = new ThumbnailPictureControlCenter(this);
	//	mThumbnailControlCenter.init();
	//	mThumbnailControlCenter.setThumbnailDownLoadCallback(this);
		
		mScreenWidth =  CommonUtil.getScreenWidth(this);
		mScreenHeight = CommonUtil.getScreenHeight(this);	
	}
	
	private void unInitData(){
		mDelCacheFileManager.start(FileManager.getSaveRootDir());
		
		/*pchong   start*/
		mControlCenter.unInit();
	//	mThumbnailControlCenter.unInit();
	}

	@Override
	protected void onDestroy() {
		unInitData();
		super.onDestroy();
	}

	
	private void refreshIntent(Intent intent){
		log.e("refreshIntent");
		int curIndex = 0;
		if (intent != null){
			curIndex = intent.getIntExtra(PLAY_INDEX, 0);		
			mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
			mUIManager.setTitle(mMediaInfo.title);
		}
		
		/*pchong   start*/
	//	mThumbnailControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getPictureList());
	//	mThumbnailControlCenter.play(curIndex);
		
		
		mControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getPictureList());
		mControlCenter.play(curIndex);
		mUIManager.showProgress(true);
		
		
	}	
	
	class UIManager implements OnClickListener{
		
		public TextView btitle;
		private ImageButton bnBack;
		private TextView tvback;
		
		public ImageView mImageView;
//		public ImageButton mBtnPre;
//		public ImageButton mBtnNext;
//		public ImageButton mBtnPlay;
//		public ImageButton mBtnPause;
		public View mLoadView;
		
		public Bitmap recycleBitmap;
		public boolean mIsScalBitmap = false;
		
		
		public UIManager(){
			initView();
		}
		
		
		private void initView() {
			btitle = (TextView) findViewById(R.id.title);
			bnBack = (ImageButton) findViewById(R.id.btn_back);
			bnBack.setOnClickListener(this);
			tvback = (TextView) findViewById(R.id.Back);
			tvback.setOnClickListener(this);
			
			mImageView = (ImageView) findViewById(R.id.imageview);
			mLoadView = findViewById(R.id.show_load_progress);
		
			
//			mBtnPre = (ImageButton) findViewById(R.id.btn_playpre);
//			mBtnNext = (ImageButton) findViewById(R.id.btn_playnext);
//			mBtnPlay = (ImageButton) findViewById(R.id.btn_play);
//			mBtnPause = (ImageButton) findViewById(R.id.btn_pause);
			//mBtnPre.setOnClickListener(this);
			//mBtnNext.setOnClickListener(this);
//			mBtnPlay.setOnClickListener(this);
//			mBtnPause.setOnClickListener(this);
		}
		
		public void setTitle(String text){
			btitle.setText(text);
		}
		
		public void setBitmap(Bitmap bitmap){
			if (recycleBitmap != null && !recycleBitmap.isRecycled()) {
				mImageView.setImageBitmap(null);
				recycleBitmap.recycle();
				recycleBitmap = null;
			}
						
			if (mIsScalBitmap) {
				mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
						
			recycleBitmap = bitmap;
			mImageView.setImageBitmap(recycleBitmap);
			
	//		log.e("	mImageView.setImageBitmap over...");
		}
		
		public void showProgress(boolean bShow)
		{
			if (bShow){
				mLoadView.setVisibility(View.VISIBLE);
			} else{
				mLoadView.setVisibility(View.GONE);
			}		
		}
		
		public void showLoadFailTip(){
			showToask(R.string.load_image_fail);
		}
		
		public void showParseFailTip(){
			showToask(R.string.parse_image_fail);
		}
		
		private void showToask(int tip) {
			Toast.makeText(PicturePlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
			case R.id.Back:
				finish();
				break;
//			case R.id.btn_playpre:
//				mControlCenter.prev();
//				break;
//			case R.id.btn_playnext:
//				mControlCenter.next();
//				break;
//			case R.id.btn_play:
//				mControlCenter.startAutoPlay(true);
//				togglePlayPause();
//				break;
//			case R.id.btn_pause:
//				mControlCenter.startAutoPlay(false);
//				togglePlayPause();
//				break;
			default:
				break;
			}
			
		}
		
		public void togglePlayPause(){
//			if (mBtnPlay.isShown()){
//				mBtnPlay.setVisibility(View.INVISIBLE);
//				mBtnPause.setVisibility(View.VISIBLE);
//			}else{
//				mBtnPlay.setVisibility(View.VISIBLE);
//				mBtnPause.setVisibility(View.INVISIBLE);
//			}
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
			log.e("DelCacheFileManager run...");
			try {
				FileHelper.deleteDirectory(mFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long interval = System.currentTimeMillis() - time;
			log.e("DelCacheFileManager del over, cost time = " + interval);
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

	@Override
	public void downLoadComplete(boolean isSuccess, String savePath) {

		onTransDelLoadResult(isSuccess, savePath);
	}
	
	private void onTransDelLoadResult(final boolean isSuccess,final String savePath){
	
		final Bitmap bitmap = PictureUtil.decodeOptionsFile(savePath, mScreenWidth, mScreenHeight, this);
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mUIManager.showProgress(false);
				
				if (!isSuccess){
					mUIManager.showLoadFailTip();
					return ;
				}
				
			
				if (bitmap == null){
					mUIManager.showParseFailTip();
					return ;
				}
				
				mUIManager.setBitmap(bitmap);
			}
		});

	}

	@Override
	public void isScalBitmap(boolean flag) {
		mUIManager.mIsScalBitmap = flag;
	}

	@Override
	public void startDownLoad() {
		mUIManager.showProgress(true);
	}

}