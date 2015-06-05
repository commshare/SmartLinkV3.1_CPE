package com.alcatel.smartlinkv3.mediaplayer.adapter;

import java.util.List;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.upnp.UpnpUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;


public class ContentAdapter extends BaseAdapter{

	private static final CommonLog log = LogFactory.createLog();
	
	private  List<MediaItem> contentItem;
	private LayoutInflater mInflater;
	private Context mContext;
	private String IThumbnailWH = "?width=80,height=80";  
	private String requestUrl;
	private ThumbnailLoader thumbnailLoader;
	
	
	private Drawable foldIcon;
	private Drawable musicIcon;
	private Drawable picIcon;
	private Drawable videoIcon;
	
	
	public ContentAdapter(Context context, List<MediaItem>  contentItem) {
		mInflater = LayoutInflater.from(context);
		this.contentItem = contentItem;
		mContext = context;
		
		Resources res = context.getResources();
		foldIcon = res.getDrawable(R.drawable.microsd_item_folder);
		musicIcon = res.getDrawable(R.drawable.microsd_item_music);
		picIcon = res.getDrawable(R.drawable.microsd_item_pictures);
		videoIcon = res.getDrawable(R.drawable.microsd_item_videos);
		
		thumbnailLoader = new ThumbnailLoader(mContext);  
	}
	
	
	
	public void refreshData(List<MediaItem>  contentItem)
	{
		this.contentItem = contentItem;
		notifyDataSetChanged();
	}

	public void clear()
	{
		if (contentItem != null){
			contentItem.clear();
			thumbnailLoader.threadshutdown();
			notifyDataSetChanged();
		}
	}
	/**
	 * The number of items in the list is determined by the number of
	 * speeches in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return contentItem.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficent to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		return contentItem.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.content_list_item, null);
		}
		
		MediaItem dataItem = (MediaItem) getItem(position);
		TextView tvContent = (TextView)convertView.findViewById(R.id.tv_content);
		tvContent.setText(dataItem.getTitle());
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
		
		if (UpnpUtil.isAudioItem(dataItem)){
			icon.setImageDrawable(musicIcon);
			thumbnailLoader.DisplayImage(dataItem.getAlbumUri(), icon);
		}else if (UpnpUtil.isVideoItem(dataItem)){
			icon.setImageDrawable(videoIcon);
		}else if (UpnpUtil.isPictureItem(dataItem)){
			icon.setImageDrawable(picIcon);
			setUrl(dataItem);
			thumbnailLoader.DisplayImage(requestUrl, icon);	
		}else{
			icon.setImageDrawable(foldIcon);
		}
			

		return convertView;
	}
	
	public void setUrl(MediaItem mi)
	{
			requestUrl = mi.getRes();
			requestUrl = requestUrl.replace("MediaItems", "Resized");
			requestUrl = requestUrl+IThumbnailWH;
	}
}
