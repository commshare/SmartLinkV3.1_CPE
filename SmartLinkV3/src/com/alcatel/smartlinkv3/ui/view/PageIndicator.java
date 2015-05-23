package com.alcatel.smartlinkv3.ui.view;

import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.view.View;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
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
