package com.alcatel.smartlinkv3.ui.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class SettingAboutFeedbackImageSelector extends BaseActivity implements OnClickListener{
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_title_back = null;
	private TextView m_tv_title_back = null;
	private Vector<String> m_image_list;
	private GridView m_image_view_list;
	
	private int screenwidth;
	private int screenheight;
	
	private int visibleImageIndex = 0;
	private int visibleImageCount = 1;
	private boolean isFirstEnter = true;
	
	final String BaseTag = "test.yanrui.baseTag";
	
	private LruCache<String, Bitmap> mMemoryCache;
	private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
	private ExecutorService decodeImageThreadPool = null;
	private ExecutorService loadImageThreadPool = null;
	private BlockingQueue<Runnable> mDownloadWorkQueue;
	
//	private Handler m_handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_image_selector);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		controlTitlebar();
		initUi();
	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_about);
		m_ib_title_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_title_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_title_back.setOnClickListener(this);
		m_tv_title_back.setOnClickListener(this);
	}
	
	private void initUi(){
		WindowManager wm = this.getWindowManager();
		screenwidth = wm.getDefaultDisplay().getWidth();
		screenheight = wm.getDefaultDisplay().getHeight();
		m_image_list = new Vector<String>();
		queryGallery();
		
		mDownloadWorkQueue = new LinkedBlockingQueue<Runnable>();
		decodeImageThreadPool = Executors.newFixedThreadPool(2);
		loadImageThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 3, TimeUnit.SECONDS, mDownloadWorkQueue);
		int maxMemory = (int) Runtime.getRuntime().maxMemory();    
        int mCacheSize = maxMemory / 8;  
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){  
            //必须重写此方法，来测量Bitmap的大小  
            @Override  
            protected int sizeOf(String key, Bitmap value) {  
                return value.getRowBytes() * value.getHeight();  
            }  
        };  
          
		m_image_view_list = (GridView) findViewById(R.id.image_list_grid);
		m_image_view_list.setAdapter(new GridAdapter(m_image_list, this));
		m_image_view_list.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){  
		            for(int i = visibleImageIndex; i < visibleImageIndex + visibleImageCount; ++i){
		            	showImage(i, true);
		            }
		        }
				else{
					for(int i = visibleImageIndex; i < visibleImageIndex + visibleImageCount; ++i){
		            	showImage(i, false);
		            }
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				visibleImageIndex = firstVisibleItem;
				visibleImageCount = visibleItemCount;
				
				if(isFirstEnter && visibleItemCount>0){
					isFirstEnter = false;
					for(int i = 0; i < visibleItemCount; ++i){
						showImage(i, true);
		            }
				}
			}
			
		});
	}
	
	public void queryGallery() {
		ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,null, "");
        while(cursor.moveToNext()){
        	String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        	m_image_list.add(path);
        }
    }
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {    
        if (getBitmapFromMemCache(key) == null && bitmap != null) {    
            mMemoryCache.put(key, bitmap);
        }    
    } 
	
	public Bitmap getBitmapFromMemCache(String key) {    
        return mMemoryCache.get(key);    
    }  
	
	public Bitmap showCacheBitmap(String url){  
        if(getBitmapFromMemCache(url) != null){  
            return getBitmapFromMemCache(url);  
        }else {  
        	Bitmap bitmap = BitmapFactory.decodeFile(url); 
        	
        	Matrix matrix = new Matrix();
    		int height = bitmap.getHeight();
    		int width = bitmap.getWidth();
    		float scale = (float)height/(float)width;
    		float scalewidth = ((float)screenwidth)/((float)width)/3.0f;
    		float scaleheight = ((float)screenheight)/((float)height)/3.0f/scale;
    		matrix.postScale(scalewidth, scaleheight);
    		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),  
                    matrix, true);  
            //将Bitmap 加入内存缓存  
            addBitmapToMemoryCache(url, bitmap);  
            return bitmap;  
        }  
    } 

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		decodeImageThreadPool.shutdown();
		decodeImageThreadPool = null;
		loadImageThreadPool.shutdown();
		loadImageThreadPool = null;
	}
	
	public class GridAdapter extends BaseAdapter{
		
		private LayoutInflater inflater; 
	    private List<String> pictures; 
	 
	    public GridAdapter(List<String> m_list, Context context) 
	    { 
	        super(); 
	        inflater = LayoutInflater.from(context);
	        pictures = m_list;
	    } 

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			 if (null != pictures) 
		        { 
		            return pictures.size(); 
		        } else
		        { 
		            return 0; 
		        } 
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pictures.get(position); 
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder; 
			
//			if (convertView == null)  { 
//	            convertView = inflater.inflate(R.layout.feed_back_image_selector_item, null); 
//	            viewHolder = new ViewHolder(); 
//	            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_selector_image_icon); 
//	            convertView.setTag(viewHolder); 
//	        } 
//			else { 
//	            viewHolder = (ViewHolder) convertView.getTag();
////	            viewHolder.image.setBackgroundColor(Color.BLUE);
//	        } 
//			viewHolder.image.setTag(BaseTag + Integer.toString(position));
//			Log.v("POSITION_POSITION", Integer.toString(position));
//			Log.v("POSITION_ITEM_INDEX", Integer.toString(visibleImageIndex));
//			Log.v("POSITION_ITEM_COUNT", Integer.toString(visibleImageCount));
			
			convertView = inflater.inflate(R.layout.feed_back_image_selector_item, null); 
			ImageView img = (ImageView) convertView.findViewById(R.id.image_selector_image_icon); 
			img.setTag(BaseTag + Integer.toString(position));
			img.setBackgroundColor(Color.BLUE);
			return convertView;
		}
	}
	
	public class ViewHolder 
	{ 
	    public ImageView image; 
	} 

	public void showImage(final int positionId, final boolean shown){
		final Handler handler = new Handler(){
			@Override  
            public void handleMessage(Message msg) {  
                super.handleMessage(msg);  
                try{
	                String tag = BaseTag + Integer.toString(msg.what);
	                if(msg.obj == null){
//	                	ImageView v = (ImageView) m_image_view_list.findViewWithTag(tag);
//	            		v.setBackgroundColor(Color.YELLOW);
	                }
	                else{
	            		Bitmap bm = (Bitmap) msg.obj;
	            		ImageView v = (ImageView) m_image_view_list.findViewWithTag(tag);
	            		v.setImageBitmap(bm);
	                }
                }
                catch(Exception e){
                	System.gc();
                	Log.v("GC1", "GARBAGE");
                }
            }  
		};
		
		loadImageThreadPool.execute(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(shown){
					try{
						String key = m_image_list.get(positionId);
						Bitmap bm = showCacheBitmap(key);
						Message msg = handler.obtainMessage(positionId, bm);
						handler.sendMessage(msg);
					}
					catch(Exception e){
						System.gc();
						Log.v("GC2", "GARBAGE");
					}
				}
				else{
					Message msg = handler.obtainMessage(positionId, null);
					handler.sendMessage(msg);
				}
			}
		});
	}
}
