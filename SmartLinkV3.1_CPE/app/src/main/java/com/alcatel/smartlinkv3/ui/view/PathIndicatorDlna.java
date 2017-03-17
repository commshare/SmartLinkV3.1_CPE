package com.alcatel.smartlinkv3.ui.view;

import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.util.ArrayList;

public class PathIndicatorDlna extends HorizontalScrollView
 {
  private static final String TAG = "PathIndicatorDlna";
  private Context mContext;
  private Animation mAnimation;
  private Animation mFadeAnimation;
  private OnClickListener mFolderButtonClickListener;
  private RelativeLayout mFolderButtonContainer;
  private OnKeyListener mFolderButtonKeyListener;
  private ArrayList<ViewHolder> mFolderButtonViewHolderList;
  private ArrayList<String> mFolderNameList;
  private ArrayList<String> mPreFolderNameList;

  private OnPathChangeListener mPathChangeListener;

  public PathIndicatorDlna(Context context) {
    this(context, null);
  }

  public PathIndicatorDlna(Context context, AttributeSet attrs) {
    super(context, attrs);
    mFolderButtonKeyListener = new OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER)
          return false;
        try {
          TextView folderButton = (TextView) findViewById(R.id.path_indicator_button);
          mFolderButtonClickListener.onClick(folderButton);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        return false;
      }
    };
    mFolderButtonClickListener = new OnClickListener() {

      @Override
      public void onClick(View view) {
        int index = -1;

        if (mFolderButtonViewHolderList == null)
          return;
        int i = 0;
        for (ViewHolder vh : mFolderButtonViewHolderList) {
          if( vh.mPathIndicatorButton == view) {
            index = i;
            break;
          }
          i++;
        }
        
        if (index == -1 || mPathChangeListener == null){
          return;
        }
        
        mPathChangeListener.onPathSelected(0, index);
      }

    };
    mContext = context;
    createComponent();
  }

  public PathIndicatorDlna(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    //TODO::
  }

  //TODO::
  private void addPath(String folderName, int index, int totalSize, final boolean isAdd) {
    Resources res = mContext.getResources();
    LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final FrameLayout view = (FrameLayout) inflater.inflate(
        R.layout.path_indicator_button, null, false);
    TextView folderButton = (TextView) view.findViewById(R.id.path_indicator_button);
    View divider = view.findViewById(R.id.path_indicator_divider);
    ViewHolder vh = new ViewHolder(view, folderButton, divider);
    view.setId(index + 1);
    //TODO:: 
    ViewGroup.LayoutParams layoutParams = folderButton.getLayoutParams();
    if (folderName.length() >= 20) {
        layoutParams.width =res.getDimensionPixelSize(R.dimen.path_indicator_button_width);
    } else {
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
    }
    folderButton.setLayoutParams(layoutParams);
    folderButton.setText(folderName);
    
    folderButton.setOnClickListener(mFolderButtonClickListener);
    view.setOnKeyListener(mFolderButtonKeyListener);

    folderButton.setEnabled(true);

    Log.v(TAG, "folderName:" + folderName + ", index : " + index + 
        ", totalSize : " + totalSize + ", isAdd: " + isAdd);
    if (index != totalSize ||isAdd)
    	 mPreFolderNameList.add(getAbsoluteFolderName(folderName));

    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.MATCH_PARENT);
    if (mFolderButtonViewHolderList != null) {
      if (mFolderButtonViewHolderList.size() > 0) {
        lp.leftMargin = (int) res.getDimension(R.dimen.path_indicator_button_margin_left);
        ViewHolder lvh = mFolderButtonViewHolderList.get(mFolderButtonViewHolderList.size() - 1);
        lp.addRule(RelativeLayout.RIGHT_OF, lvh.mContainer.getId());
      } else {
        folderButton.setPadding(
            (int) res.getDimension(R.dimen.path_indicator_first_button_padding_left),
            folderButton.getPaddingTop(),
            folderButton.getPaddingRight(),
            folderButton.getPaddingBottom());
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
      }
      mFolderButtonViewHolderList.add(vh);
    }
    if (totalSize == index) {
      if (isAdd) {
        mFadeAnimation = AnimationUtils.loadAnimation(mContext,
            R.anim.add_alpha_path_indicator);
        mAnimation = AnimationUtils.loadAnimation(mContext,
            R.anim.add_scale_path_indicator);
      } else {
        mFadeAnimation = AnimationUtils.loadAnimation(mContext,
            R.anim.remove_alpha_path_indicator);
        mAnimation = AnimationUtils.loadAnimation(mContext,
            R.anim.remove_scale_path_indicator);
        mFolderButtonViewHolderList.remove(vh);
      }
      // folderButtonContainer.startAnimation();
      folderButton.startAnimation(mFadeAnimation);
      // pathBackground.startAnimation(mAnimation);
      mAnimation.setAnimationListener(new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
          if (isAdd)
            view.setVisibility(View.VISIBLE);
          else
            view.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

      });
    }

    mFolderButtonContainer.addView(view, lp);
    //addView(view, lp);
    post(new Runnable() {

      @Override
      public void run() {
        fullScroll(View.FOCUS_RIGHT);
      }

    });
  }
  
  private void addPath(ArrayList<String> foldersArray,  boolean isAdd)  {
    for(int index = 0; index < foldersArray.size();index++) {
      addPath(foldersArray.get(index), index, foldersArray.size(), isAdd);
    } 
  }
  private void createComponent() {
	mFolderNameList = new ArrayList<String>();
	mPreFolderNameList =  new ArrayList<String>();
    mFolderButtonViewHolderList = new ArrayList<ViewHolder>();
    mFolderButtonContainer = new RelativeLayout(mContext);
    addView(mFolderButtonContainer, 
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    
  }
  private String getAbsoluteFolderName(String path){
    return path;    
  }

  
  public void clear(){
	  mFolderNameList.clear();
    if (mFolderButtonViewHolderList != null) {
      mFolderButtonViewHolderList.clear();
    }
    mFolderButtonContainer.removeAllViews();
  }

  
  public void setOnPathChangeListener(OnPathChangeListener listener) {
    mPathChangeListener = listener;    
  }
  
  /*
   * folderPath="/Music"; 
   * 
   */
  public void setPath(String folderPathName, int index){
	  
    if(index < mPreFolderNameList.size() &&index >= 0)
    {
    	clear();
    	while(mFolderNameList.size()<=index)
    	{
    		mFolderNameList.add(mPreFolderNameList.get(mFolderNameList.size()));
    	}
    	mPreFolderNameList.clear();
	    addPath(mFolderNameList, true);
    }else
    {
        if (folderPathName == null )
            return;
        
	    clear();
	    mPreFolderNameList.add(folderPathName);
	    
	    while (mFolderNameList.size()<mPreFolderNameList.size())
	    {
	    	mFolderNameList.add(mPreFolderNameList.get(mFolderNameList.size()));
	    }
	    mPreFolderNameList.clear();
	    addPath(mFolderNameList, true);
    }

    if (mFolderButtonViewHolderList == null || mFolderButtonViewHolderList.size() == 0) 
      return;
    
    ViewHolder v = mFolderButtonViewHolderList.get(mFolderButtonViewHolderList.size() - 1);
    if (v != null && v.mPathIndicatorButton != null){
    v.mPathIndicatorButton.setClickable(false);
    v.mPathIndicatorButton.setTextColor(Color.BLACK);
    }
    
    v = mFolderButtonViewHolderList.get(0);
    if (v != null && v.mPathIndicatorDivider != null) {
    v.mPathIndicatorDivider.setVisibility(View.INVISIBLE);
    }
  }
  
  public interface OnPathChangeListener {
    public abstract void onPathSelected(int targetFolderID, int targetFolderNameindex);
  }

  public class PathElement {
    String mFullPath;
    int mID;
    String mName;

    public PathElement(int id, String name, String fullPath) {
      mID = id;
      mName = name;
      mFullPath = fullPath;
    }
  }

  class ViewHolder {
    public FrameLayout mContainer;
    public View mPathIndicatorDivider;
    public TextView mPathIndicatorButton;

    public ViewHolder(FrameLayout container, TextView pathIndicatorButton,
        View pathIndicatorDivider) {
      mContainer = container;
      mPathIndicatorButton = pathIndicatorButton;
      mPathIndicatorDivider = pathIndicatorDivider;
    }
  }
}