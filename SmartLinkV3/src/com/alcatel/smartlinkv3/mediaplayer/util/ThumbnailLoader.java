package com.alcatel.smartlinkv3.mediaplayer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;


public class ThumbnailLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ThumbnailLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	//final int stub_id = R.drawable.microsd_item_pictures;

	public void DisplayImage(String url, ImageView imageView) {
		if("" == url||null == url)
		{
			return;
		}
		
		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
		else {
			queuePhoto(url, imageView);
			//imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		Bitmap b = decodeFile(f);
		if (b != null)
		{
			return b;
		}

		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decode image size
	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 88;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			Log.v("pchong", "decodeFile width_tmp= "+o.outWidth+"height_tmp= "+o.outHeight);
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE
						|| height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			Log.v("pchong", "decodeFile scale= "+scale);
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// UI
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
			{
				return;
			}
			if (bitmap != null)
			{
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
			else
			{
				//photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void threadshutdown(){
		if (executorService != null)
		{
			executorService.shutdown();
			executorService.shutdownNow();
			executorService = null;
		}
	}
	
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
	
	public class FileCache {

		private File cacheDir;

		public FileCache(Context context) {
			if (CommonUtil.hasSDCard())
			{
				cacheDir = new File(
						android.os.Environment.getExternalStorageDirectory(),
						"sharing/thumbnails/picture/");
			}
			else{
				//cacheDir = context.getCacheDir();
				cacheDir = new File(
						android.os.Environment.getDataDirectory(),
						"smartlinkv3/sharing/thumbnails/picture/");
			}
			if (!cacheDir.exists())
				cacheDir.mkdirs();
		}

		public File getFile(String url) {
			// 将url的hashCode作为缓存的文件名
			String filename = String.valueOf(url.hashCode());
			// Another possible solution
			// String filename = URLEncoder.encode(url);
			File f = new File(cacheDir, filename);
			return f;

		}

		public void clear() {
			File[] files = cacheDir.listFiles();
			if (files == null)
				return;
			for (File f : files)
				f.delete();
		}

	}
	
	public class MemoryCache {
		
		private Map<String, SoftReference<Bitmap>> cache = Collections
				.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

		public Bitmap get(String id) {
			if (!cache.containsKey(id))
				return null;
			SoftReference<Bitmap> ref = cache.get(id);
			return ref.get();
		}

		public void put(String id, Bitmap bitmap) {
			cache.put(id, new SoftReference<Bitmap>(bitmap));
		}

		public void clear() {
			cache.clear();
		}

	}
}


