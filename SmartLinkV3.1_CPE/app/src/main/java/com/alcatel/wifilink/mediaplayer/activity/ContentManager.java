package com.alcatel.wifilink.mediaplayer.activity;

import java.util.List;
import java.util.Stack;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.alcatel.wifilink.mediaplayer.upnp.MediaItem;


public class ContentManager {

	private static final CommonLog log = LogFactory.createLog();
	
	private static ContentManager mInstance = null;
	
	private Stack<List<MediaItem>> mStack;
	private Stack<String> mStackTitle;
	
	public synchronized static ContentManager getInstance(){
		if (mInstance == null){
			mInstance =  new ContentManager();
		}
		
		return mInstance;
	}
	
	private ContentManager()
	{
		mStack = new Stack<List<MediaItem>>();
		mStackTitle = new Stack<String>();
	}
	
	public void pushListItem(List<MediaItem> dataList)
	{
		if (dataList != null){
	//		log.e("mStack.add data.size = " + dataList.size());
			mStack.add(dataList);
		}
	}
	public void pushTitle(String title)
	{
		if (title != null){
	//		log.e("mStack.add data.size = " + dataList.size());
			mStackTitle.add(title);
		}
	}
	
	
	public List<MediaItem> peekListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		return mStack.peek();
	}
	public String peekTitle()
	{
		if (mStackTitle.empty()){
			return null;
		}
		
		return mStackTitle.peek();
	}
	
	
	public List<MediaItem> popListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		return mStack.pop();
	}
	public String popTitle()
	{
		if (mStackTitle.empty()){
			return null;
		}
		
		return mStackTitle.pop();
	}
	
	
	public void clear()
	{
		mStack.clear();
	}
	
	public void titleclear()
	{
		mStackTitle.clear();
	}
	
	public int Stacksize()
	{
		return mStack.size();
	}
	
	public int mStackTitlesize()
	{
		return mStackTitle.size();
	}
}
