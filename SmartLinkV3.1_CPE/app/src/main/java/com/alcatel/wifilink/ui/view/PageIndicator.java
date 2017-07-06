package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ImageView;

import java.util.ArrayList;

public class PageIndicator extends LinearLayout
 {
     private ArrayList<ImageView> mBubbleList;
    private int mCurrentPage;
    private int mTotalPages;
 
 
  public PageIndicator(Context context) {   
      this(context, null);   
  }   
 
  public PageIndicator(Context context, AttributeSet attrs) {   
      super(context, attrs);
      mBubbleList = new ArrayList<ImageView>();
  }   
    
  public PageIndicator(Context context, AttributeSet attrs, int defStyle) {  
      super(context, attrs, defStyle);  
  }

  public void setCurrentPage(int currentPage) {
      mCurrentPage = currentPage;
      
      if (mTotalPages >= 0) {
      } else if (mCurrentPage != mTotalPages) {
      }

  }

  public void setTotalPages(int totalPages) {
  }


   
}
