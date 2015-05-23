package com.alcatel.smartlinkv3.ui.view;

import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import java.io.File;
import java.util.ArrayList;

public class PathIndicator extends HorizontalScrollView
 {
  private static final String MODULE = "PathIndicator";
  private Context mContext;
  private String mCurrentPathIndicatorPath;
  private Animation mAnimation;
  private Animation mFadeAnimation;
  private OnClickListener mFolderButtonClickListener;
  private RelativeLayout mFolderButtonContainer;
  private OnKeyListener mFolderButtonKeyListener;
  private ArrayList<ViewHolder> mFolderButtonViewHolderList;
  private ArrayList<String> mFolderList;
  private boolean mForceRefreshPathIndicatorBar;

  private OnPathChangeListener mPathChangeListener;
  private String mPrePathIndicatorPath;

  public PathIndicator(Context context) {
    this(context, null);
  }

  public PathIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    mCurrentPathIndicatorPath = null;
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
          i++;
          if( vh.mPathIndicatorButton == view) {
            index = i;
            break;
          }
        }
        
        if (index == -1){
          return;
        }

        if (mPathChangeListener == null)
          return;
        StringBuilder sb = new StringBuilder();
        for (String f : mFolderList) {
          sb.append(f);
          sb.append(File.separator);
        }
        mPathChangeListener.onPathSelected(0, sb.toString());
      }

    };
    mContext = context;
    createComponent();
  }

  public PathIndicator(Context context, AttributeSet attrs, int defStyle) {
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
    // FrameLayout folderButtonContainer =
    // (FrameLayout)view.findViewById(R.id.path_indicator_container);
    TextView folderButton = (TextView) view.findViewById(R.id.path_indicator_button);
    ViewHolder vh = new ViewHolder(view, folderButton, null);
    view.setId(index + 1);
    folderButton.setText(folderName);
    folderButton.setOnClickListener(mFolderButtonClickListener);
    view.setOnKeyListener(mFolderButtonKeyListener);

    folderButton.setEnabled(true);

    if (index != totalSize && isAdd)
      mFolderList.add(getAbsoluteFolderName(folderName));

    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.MATCH_PARENT);
    if (mFolderButtonViewHolderList != null) {
      if (mFolderButtonViewHolderList.size() > 0) {
        lp.leftMargin = (int) res
            .getDimension(R.dimen.path_indicator_button_margin_left);
        ViewHolder lvh = mFolderButtonViewHolderList.get(mFolderButtonViewHolderList
            .size() - 1);
        lp.addRule(RelativeLayout.RIGHT_OF, lvh.mContainer.getId());
      } else {
        folderButton.setPadding(
            (int) res.getDimension(R.dimen.path_indicator_first_button_padding_left),
            folderButton.getPaddingTop(), folderButton.getPaddingRight(),
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
    
    //TOOD::??
    //mFolderButtonContainer.addView(view, lp);
    addView(view, lp);
    post(new Runnable() {

      @Override
      public void run() {
        fullScroll(View.FOCUS_RIGHT);
      }

    });
  }
  
  private void addPath(String[] foldersArray,  boolean isAdd)  {
    for(int index = 0; index < foldersArray.length;index++) {
      addPath(foldersArray[index], index, foldersArray.length - 1, isAdd);
    }    
  }
  private void createComponent() {
    mFolderList = new ArrayList<String>();
    mFolderButtonViewHolderList = new ArrayList<ViewHolder>();
    mFolderButtonContainer = new RelativeLayout(mContext);
    addView(mFolderButtonContainer, 
        new LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    
  }
  private String getAbsoluteFolderName(String path){
    //String folderPath;
    //mContext.getString(resId);
    return path;    
  }
  
  private String getCurPathIndicatorPath() {
    int length = mFolderList.size();
    StringBuilder path = new StringBuilder();
    for(int i=0; i < length; i++){
      path.append(mFolderList.get(i));
    }
    path.append("/");
    mCurrentPathIndicatorPath = path.toString();
    return mCurrentPathIndicatorPath;
  }
  
  private String getDisPlayedPath(String path){
    return path;
  }
  
  public void clear(){
    mFolderList.clear();
    if (mFolderButtonViewHolderList != null) {
      mFolderButtonViewHolderList.clear();
    }
    mFolderButtonContainer.removeAllViews();
  }
  
  public void goUp() {
    if (mFolderButtonViewHolderList == null ||
        mFolderButtonViewHolderList.size() == 0)
      return;
    int lastElementIndex = mFolderButtonViewHolderList.size() - 1;
    FrameLayout folderButton = mFolderButtonViewHolderList.get(lastElementIndex).mContainer;
    mFolderButtonContainer.removeView(folderButton);
    
    if (mFolderList.size() - 1 == lastElementIndex) {
      mFolderList.remove(lastElementIndex);
    }
    mFolderButtonViewHolderList.remove(lastElementIndex);
  }
  
  public void setForceRefreshPathIndicatorBar(boolean force) {
    mForceRefreshPathIndicatorBar = force;
  }
  
  public void setOnPathChangeListener(OnPathChangeListener listener) {
    mPathChangeListener = listener;    
  }
  
  public void setPath(String folderPath){
    if (folderPath == null )
      return;
    
    String currentFolderName = folderPath.substring(folderPath.length(),
        folderPath.lastIndexOf("/")+1);
    String preFolderName = "";
    
    if (folderPath.length() > 0 && !getCurPathIndicatorPath().equals(folderPath)
        && !mForceRefreshPathIndicatorBar){     
      return;
    }
    if (folderPath.length() == 0) {
      return;      
    } else if (folderPath.length() == 1){
      if ('/' != folderPath.charAt(0))
        return;
    }
    
    Log.d(MODULE, new StringBuilder().
                      append(" setPath() - folderPath : ").
                      append(folderPath).
                      toString());
    String[] folders = getDisPlayedPath(folderPath).substring(1).split("/");
    String[] preFolders = new String[]{""};
    
    if (!mForceRefreshPathIndicatorBar)
      return;
    if (mPrePathIndicatorPath != null){
      preFolders = getDisPlayedPath(mPrePathIndicatorPath).substring(1).split("/");
      preFolderName = mPrePathIndicatorPath.substring(mPrePathIndicatorPath.lastIndexOf("/") + 1, 
          mPrePathIndicatorPath.length());
    }
    
    if(preFolders.length == folders.length && mPrePathIndicatorPath != null){
     if(currentFolderName.equals(preFolderName) && !mForceRefreshPathIndicatorBar)
       return;
     clear();
     addPath(folders, true);
    } else {
      clear();
      if (preFolders.length < folders.length) {
        addPath(folders, true);
      } else if (preFolders.length > folders.length) {
        File currentFolder = new File(folderPath);
        File preFolder = new File(mPrePathIndicatorPath);
        if(preFolder.getParent().equals(currentFolder.getAbsolutePath())){
          addPath(preFolders, false);
        } else {
          addPath(folders, true);
        }
      } else if (preFolders.length > folders.length) {
        if(currentFolderName.equals(preFolderName)
            && !"".equals(preFolderName)
            && !mForceRefreshPathIndicatorBar) {
          return;
        }
      } else {
        addPath(folders, true);
      }
    }

    if (mForceRefreshPathIndicatorBar)
      mForceRefreshPathIndicatorBar = false;
    
    folderPath = mPrePathIndicatorPath;
    if (mFolderButtonViewHolderList == null || mFolderButtonViewHolderList.size() == 0) 
      return;
    
    ViewHolder v = mFolderButtonViewHolderList.get(mFolderButtonViewHolderList.size() - 1);
    if (v == null || v.mPathIndicatorButton == null)
      return;
    v.mPathIndicatorButton.setClickable(false);
    v.mPathIndicatorButton.setShadowLayer(2.0F, .0F, 2.0F, Color.BLACK);
  }
  
  public interface OnPathChangeListener {
    public abstract void onPathSelected(int targetFolderID, String targetFolderPath);
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
    public ImageView mPathIndicatorArrow;
    public TextView mPathIndicatorButton;

    public ViewHolder(FrameLayout container, TextView pathIndicatorButton,
        ImageView pathIndicatorArrow) {
      mContainer = container;
      mPathIndicatorButton = pathIndicatorButton;
      mPathIndicatorArrow = pathIndicatorArrow;
    }
  }
}